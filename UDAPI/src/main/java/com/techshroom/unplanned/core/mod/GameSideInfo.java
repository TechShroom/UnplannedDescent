package com.techshroom.unplanned.core.mod;

import java.util.Optional;

import com.google.common.reflect.TypeToken;

public interface GameSideInfo {

    class Side<SUBCLASS extends GameSideInfo> {

        public static final Side<ClientSideInfo> CLIENT = new Side<>();
        public static final Side<ServerSideInfo> SERVER = new Side<>();

        private final TypeToken<SUBCLASS> token =
                new TypeToken<SUBCLASS>(getClass()) {

                    private static final long serialVersionUID =
                            -4649053180888347429L;
                };

        private Side() {
        }

        public boolean isClient() {
            return this == CLIENT;
        }

        public boolean isServer() {
            return this == SERVER;
        }

        @SuppressWarnings("unchecked")
        private Class<SUBCLASS> getSubclass() {
            // seriously java....
            return (Class<SUBCLASS>) this.token.getRawType();
        }

    }

    /**
     * Returns the side this game info is for.
     * 
     * @return the side this game info is for
     */
    Side<?> getSide();

    default <T extends GameSideInfo> Optional<T> asSidedInfo(Side<T> side) {
        return getSide() == side ? Optional.of(side.getSubclass().cast(this))
                : Optional.empty();
    }

    default Optional<ClientSideInfo> asClientSideInfo() {
        return asSidedInfo(Side.CLIENT);
    }

    default Optional<ServerSideInfo> asServerSideInfo() {
        return asSidedInfo(Side.SERVER);
    }

}
