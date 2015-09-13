package org.ftccommunity.ftcxtensible.networkedopmode;

import java.util.HashMap;

/**
 * An interface used for future use
 *
 * @param <T> Type of device to use for an upstream call
 */
public interface UpstreamHardwareUpdate<T> {
    /**
     * Handles a read event on a device of the type specificed
     *
     * @param in  the device to process
     * @param out a hashmap to write any output to, for futher use
     */
    void processRead(T in, HashMap<String, Object> out);
}
