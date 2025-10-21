package net.ddns.mask.data.util

interface IpAddressProvider {
    /**
     * Attempts to find and return a local IPv6 Global Unicast Address (GUA).
     * Returns the IP address string if found, or null otherwise.
     */
    fun getLocalIPv6GUA(): String?
}