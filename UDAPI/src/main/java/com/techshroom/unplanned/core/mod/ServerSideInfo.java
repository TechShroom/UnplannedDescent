package com.techshroom.unplanned.core.mod;

import java.net.InetSocketAddress;
import java.util.Collection;

public interface ServerSideInfo extends GameSideInfo {

    @Override
    default Side getSide() {
        return Side.SERVER;
    }

    /**
     * Returns the address(es) this server is bound to. May be empty if this
     * server has not bound to any addresses yet.
     * 
     * @return the bound addresses
     */
    Collection<InetSocketAddress> getBoundAddresses();

}
