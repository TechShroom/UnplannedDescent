package com.techshroom.unplanned.core.mod;

public interface GameSideInfo {

    enum Side {
        CLIENT, SERVER;

        public boolean isClient() {
            return this == CLIENT;
        }

        public boolean isServer() {
            return this == SERVER;
        }

    }

    /**
     * Returns the side this game info is for.
     * 
     * @return the side this game info is for
     */
    Side getSide();

    /**
     * Adds a connection listener to the game. Please take note of which side
     * the connection listener is on, as the server and client will both fire on
     * a connection.
     * 
     * @param listener
     *            - The listener to attach
     */
    void addConnectionListener(ConnectionListener listener);

}
