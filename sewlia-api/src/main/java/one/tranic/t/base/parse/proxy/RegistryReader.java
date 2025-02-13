package one.tranic.t.base.parse.proxy;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import org.jetbrains.annotations.Nullable;

/**
 * A utility class for reading Windows registry values to retrieve proxy settings.
 * <p>
 * The class provides functionality for accessing proxy-related registry keys and
 * returning corresponding settings wrapped in a ProxySettings record.
 */
public class RegistryReader {
    /**
     * Reads proxy settings from the Windows registry and returns them as a ProxySettings object.
     * <p>
     * If an error occurs during registry access, it attempts to read proxy settings from
     * environment variables as a fallback.
     *
     * @return a ProxySettings object containing the proxy server, proxy override rules,
     * and a flag indicating whether a proxy is enabled. If registry access fails,
     * it uses values from environment variables.
     */
    public static ProxySettings readWindowsProxySettings() {
        try {
            String proxyServer = Advapi32Util.registryGetStringValue(
                    WinReg.HKEY_CURRENT_USER,
                    "Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings",
                    "ProxyServer"
            );
            String proxyOverride = Advapi32Util.registryGetStringValue(
                    WinReg.HKEY_CURRENT_USER,
                    "Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings",
                    "ProxyOverride"
            );
            int proxyEnabled = Advapi32Util.registryGetIntValue(
                    WinReg.HKEY_CURRENT_USER,
                    "Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings",
                    "ProxyEnable"
            );
            return new ProxySettings(proxyServer, proxyOverride, proxyEnabled == 1);
        } catch (Exception e) {
            String httpProxy = System.getenv("http_proxy");
            String httpsProxy = System.getenv("https_proxy");
            String noProxy = System.getenv("no_proxy");

            return new ProxySettings(httpProxy != null ? httpProxy : httpsProxy, noProxy, noProxy != null || httpProxy != null || httpsProxy != null);
        }
    }

    /**
     * A record class representing proxy settings.
     *
     * @param server   the address of the proxy server; may be null if no proxy server is configured.
     * @param override rules for bypassing the proxy (e.g., specific hosts or addresses to ignore); may be null.
     * @param enabled  a flag indicating whether proxy usage is enabled (true) or disabled (false).
     */
    public record ProxySettings(@Nullable String server, @Nullable String override, boolean enabled) {
    }
}
