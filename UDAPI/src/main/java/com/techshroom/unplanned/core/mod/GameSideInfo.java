/*
 * This file is part of UnplannedDescent, licensed under the MIT License (MIT).
 *
 * Copyright (c) TechShroom Studios <https://techshoom.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
