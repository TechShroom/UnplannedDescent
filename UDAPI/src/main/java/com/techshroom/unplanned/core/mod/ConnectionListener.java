package com.techshroom.unplanned.core.mod;

import java.net.Socket;

@FunctionalInterface
public interface ConnectionListener {

    void onConnection(Socket connection);

}
