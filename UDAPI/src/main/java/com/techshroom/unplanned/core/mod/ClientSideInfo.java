package com.techshroom.unplanned.core.mod;

import java.net.InetSocketAddress;
import java.util.Optional;

public interface ClientSideInfo extends GameSideInfo {

    default Side getSide() {
        return Side.CLIENT;
    }

    /**
     * Returns the address of the server this client is connected to. May be
     * absent if this client is not connected to a server.
     * 
     * @return the address of the server this client is connected to
     */
            Optional<InetSocketAddress> getServerAddress();

}
