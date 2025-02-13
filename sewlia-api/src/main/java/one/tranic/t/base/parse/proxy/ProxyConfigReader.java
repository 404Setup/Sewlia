package one.tranic.t.base.parse.proxy;

import one.tranic.sewlia.config.mod.feature.EnableAutoProxyReader;
import one.tranic.t.util.Collections;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;

/**
 * A utility class for reading and configuring proxy settings based on the system's environment
 * or operating system-specific configurations. The class provides methods to retrieve proxy
 * settings dynamically or use a static configuration, with support for both HTTP and HTTPS protocols.
 */
public class ProxyConfigReader {

    /**
     * Returns a Proxy object based on the provided proxy parameter.
     * If the provided proxy is equal to Proxy.NO_PROXY, it falls back
     * to a default proxy configuration by invoking the parameterless getProxy method.
     *
     * @param proxy the Proxy instance to be evaluated and returned if not equal to Proxy.NO_PROXY
     * @return the provided proxy if it's not Proxy.NO_PROXY, or a default configured Proxy object otherwise
     */
    public static Proxy getProxy(Proxy proxy) {
        if (proxy.equals(Proxy.NO_PROXY)) return getProxy();
        return proxy;
    }

    /**
     * Retrieves the active proxy configuration and returns a corresponding {@link Proxy} object.
     * <p>
     * The proxy configuration is determined based on the operating system and environment variables.
     * If no active proxy configuration is found or protocols are empty, {@link Proxy#NO_PROXY} is returned.
     *
     * @return A {@link Proxy} object representing the active proxy configuration, or {@link Proxy#NO_PROXY}
     * if no active configuration is available.
     */
    public static Proxy getProxy() {
        if (!EnableAutoProxyReader.value) return Proxy.NO_PROXY;
        ProxyConfig config = getProxyConfig();
        if (!config.isStaticActive || config.protocols.isEmpty())
            return Proxy.NO_PROXY;

        String protocol = config.protocols.containsKey("https") ? "https" : "http";
        String proxyAddress = config.protocols.get(protocol);
        String[] proxyParts = proxyAddress.split(":");
        String host = proxyParts[0];
        int port = proxyParts.length > 1 ? Integer.parseInt(proxyParts[1]) : 8080;

        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
    }

    /**
     * Retrieves the proxy configuration for the current operating system.
     * <p>
     * The method determines whether the system is running on Windows or another OS
     * and retrieves the appropriate proxy settings accordingly.
     *
     * @return a {@link ProxyConfig} object containing the proxy settings for the current system.
     * Returns configuration specific to Windows if running on Windows OS,
     * otherwise returns configuration for Linux or Linux-like environments.
     */
    public static ProxyConfig getProxyConfig() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return getWindowsProxyConfig();
        } else {
            return getLinuxProxyConfig();
        }
    }

    /**
     * Reads and returns the Windows proxy configuration as a ProxyConfig object.
     * <p>
     * This method accesses the Windows registry to determine the proxy settings
     * and populates a ProxyConfig object with the retrieved information.
     *
     * @return A ProxyConfig object containing the Windows proxy settings, or null
     * if an exception occurs during retrieval or if the settings cannot be accessed.
     */
    private static @Nullable ProxyConfig getWindowsProxyConfig() {
        ProxyConfig config = new ProxyConfig();
        try {
            RegistryReader.ProxySettings proxyMap = RegistryReader.readWindowsProxySettings();
            config.isStaticActive = proxyMap.enabled();
            config.protocols.put("http", proxyMap.server());
            config.noProxy = proxyMap.override();
        } catch (Exception e) {
            return null;
        }
        return config;
    }

    /**
     * Retrieves the proxy configuration for Linux-based systems by reading
     * environment variables such as "http_proxy", "https_proxy", and "no_proxy".
     * Populates a {@code ProxyConfig} object with the gathered data.
     *
     * @return a {@code ProxyConfig} containing the proxy setup obtained from
     * environment variables, or {@code null} if an error occurs during processing.
     */
    private static ProxyConfig getLinuxProxyConfig() {
        ProxyConfig config = new ProxyConfig();
        try {
            String httpProxy = System.getenv("http_proxy");
            String httpsProxy = System.getenv("https_proxy");
            String noProxy = System.getenv("no_proxy");

            config.isStaticActive = (httpProxy != null || httpsProxy != null);
            if (httpProxy != null) config.protocols.put("http", httpProxy);
            if (httpsProxy != null) config.protocols.put("https", httpsProxy);
            config.noProxy = noProxy;
        } catch (Exception e) {
            return null;
        }
        return config;
    }

    public static class ProxyConfig {
        public boolean isStaticActive = false;
        public Map<String, String> protocols = Collections.newHashMap();
        public String noProxy = null;
        public boolean isAutomaticActive = false;
        public String preConfiguredURL = null;

        @Override
        public String toString() {
            return "ProxyConfig{" +
                    "isStaticActive=" + isStaticActive +
                    ", protocols=" + protocols +
                    ", noProxy='" + noProxy + '\'' +
                    ", isAutomaticActive=" + isAutomaticActive +
                    ", preConfiguredURL='" + preConfiguredURL + '\'' +
                    '}';
        }
    }
}
