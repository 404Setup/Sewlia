package one.tranic.sewlia.maven;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import one.tranic.sewlia.config.mod.feature.maven.Designation;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The {@code Accelerate} class is responsible for selecting the fastest available Maven repository mirror.
 * It provides utility methods for determining if a repository is a central Maven repository, retrieving
 * repository configurations, and replacing repository URLs with the selected mirror URL.
 * <p>
 * On initialization, it tests multiple predefined mirrors and selects the fastest one, which is then
 * used as the default Maven repository for dependency resolution.
 */
public class Accelerate {
    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger("LibraryResolverAccelerate");
    protected static final String central = "https://repo.maven.apache.org/maven2";
    protected static final String central2 = "https://repo1.maven.org/maven2";
    protected static final URI centralUri = URI.create(central);
    protected static final URI central2Uri = URI.create(central2);
    protected static String maven = "";
    protected static ReentrantLock lock = new ReentrantLock();

    public static void ping() {
        if (!maven.isEmpty()) return;
        lock.lock();
        try {
            if (!one.tranic.sewlia.config.mod.feature.maven.Enable.value) {
                maven = central;
                return;
            }
            Map<String, String> mirrors = getMirrors();
            if (!one.tranic.sewlia.config.mod.feature.maven.Designation.value.isEmpty()) {
                String selectRepo = mirrors.get(Designation.value);
                if (selectRepo == null || selectRepo.isBlank()) {
                    maven = central;
                    logger.warn("The repository {} is not a valid repository", one.tranic.sewlia.config.mod.feature.maven.Designation.value);
                } else {
                    maven = selectRepo;
                    logger.info("The mirror {} ({}) has been selected", one.tranic.sewlia.config.mod.feature.maven.Designation.value, selectRepo);
                }
                return;
            }
            selectMirror(mirrors);
        } finally {
            lock.unlock();
        }
    }

    protected static Map<String, String> getMirrors() {
        Map<String, String> builder = new Object2ObjectOpenHashMap<>();
        builder.put("central", "https://repo.maven.apache.org/maven2");
        builder.put("central1", "https://repo1.maven.org/maven2");
        builder.put("redhat", "https://repository.jboss.org/nexus/content/groups/public");
        builder.put("google-asia", "https://maven-central-asia.storage-download.googleapis.com/maven2/");
        builder.put("google-eu", "https://maven-central-eu.storage-download.googleapis.com/maven2/");
        builder.put("google-us", "https://maven-central.storage-download.googleapis.com/maven2/");

        String custom = one.tranic.sewlia.config.mod.feature.maven.UserRepository.value;
        if (!custom.isEmpty()) {
            try {
                new URI(custom);
            } catch (Exception e) {
                return builder;
            }
            builder.put("user-custom-mirror", custom);
        }
        return builder;
    }

    /**
     * Checks if a given URL string belongs to the central Maven repository.
     *
     * <pre>{@code
     * boolean b = Accelerate.isCentral("https://repo.maven.apache.org/maven2");
     * }</pre>
     *
     * @param str the repository URL to check
     * @return {@code true} if the URL matches the central Maven repository, {@code false} otherwise
     */
    public static boolean isCentral(@NotNull String str) {
        try {
            String s = URI.create(str).getHost();
            return Objects.equals(s, centralUri.getHost()) || Objects.equals(s, central2Uri.getHost());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if a given {@link RemoteRepository} belongs to the central Maven repository.
     *
     * @param remoteRepository the repository to check
     * @return {@code true} if the repository matches the central Maven repository, {@code false} otherwise
     */
    public static boolean isCentral(@NotNull RemoteRepository remoteRepository) {
        return isCentral(remoteRepository.getUrl());
    }

    /**
     * Retrieves a {@link RemoteRepository} instance for the selected fastest Maven repository mirror.
     *
     * @return a {@link RemoteRepository} pointing to the fastest selected mirror
     */
    public static RemoteRepository get() {
        if (maven.isEmpty()) ping();
        return new RemoteRepository.Builder("central", "default", maven).build();
    }

    /**
     * Replaces a central repository URL with the selected fastest mirror URL, if applicable.
     *
     * @param remoteRepository the repository whose URL is to be replaced
     * @return a new {@link RemoteRepository} with the mirror URL if the repository is central,
     * or the original repository otherwise
     */
    public static RemoteRepository get(RemoteRepository remoteRepository) {
        if (isCentral(remoteRepository)) return get();
        return remoteRepository;
    }

    /**
     * Replaces the provided repository URL string with the selected fastest mirror URL
     * if the URL matches the central Maven repository.
     *
     * <pre>{@code
     * boolean b = Accelerate.replace("https://repo.maven.apache.org/maven2");
     * }</pre>
     *
     * @param str the repository URL to potentially replace, not be {@code null}.
     * @return the mirror URL if the repository is central, or the original URL otherwise, not be {@code null}.
     */
    public static @NotNull String replace(@NotNull String str) {
        return Objects.equals(str, central) ? maven : str;
    }

    /**
     * Selects the fastest available mirror from the predefined list by testing their response times.
     * The mirror with the shortest response time is selected as the default Maven repository.
     */
    protected static void selectMirror(Map<String, String> mirrors) {
        if (!maven.isEmpty()) return;
        try (ExecutorService executor = Executors.newCachedThreadPool(Thread.ofVirtual().factory())) {
            ObjectArrayList<Future<MirrorResult>> futures = new ObjectArrayList<>();
            for (Map.Entry<String, String> entry : mirrors.entrySet()) {
                futures.add(executor.submit(() -> testMirror(entry.getKey(), entry.getValue())));
            }

            long bestTime = Long.MAX_VALUE;
            String bestMirror = central;
            String bestMirrorName = "central";

            if (futures.isEmpty()) {
                maven = central;
                return;
            }

            for (Future<MirrorResult> future : futures) {
                try {
                    MirrorResult result = future.get(4, TimeUnit.SECONDS);
                    if (result.time < bestTime) {
                        bestTime = result.time;
                        bestMirror = result.url;
                        bestMirrorName = result.name;
                    }
                } catch (TimeoutException | InterruptedException | ExecutionException e) {
                    logger.warn("Error testing mirror: {}", e.getMessage());
                }
            }

            maven = bestMirror;
            logger.info("The fastest mirror is selected: {}: {} ({} ms)", bestMirrorName, bestMirror, bestTime);
        }
    }

    /**
     * Tests the response time of a given mirror by sending a GET request and measuring the round-trip time.
     *
     * @param name the name of the mirror being tested, not be {@code null}.
     * @param url  the URL of the mirror being tested, not be {@code null}.
     * @return a {@link MirrorResult} object containing the mirror URL and the response time, not be {@code null}.
     */
    protected static @NotNull MirrorResult testMirror(@NotNull String name, @NotNull String url) {
        long start = System.currentTimeMillis();
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == 200 || responseCode == 404 || responseCode == 302 || responseCode == 301) {
                long time = System.currentTimeMillis() - start;
                logger.info("Mirror {} responded in {} ms", name, time);
                return new MirrorResult(url, name, time);
            } else {
                logger.warn("Mirror {} failed with response code: {}", name, responseCode);
            }
        } catch (IOException e) {
            logger.warn("Mirror {} failed to connect: {}", name, e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return new MirrorResult(url, name, Long.MAX_VALUE);
    }

    /**
     * A simple class to store the result of a mirror test, including the URL of the mirror and its response time.
     */
    protected record MirrorResult(String url, String name, long time) {
    }
}