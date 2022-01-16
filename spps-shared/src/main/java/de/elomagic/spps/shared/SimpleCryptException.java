/*
 * Simple Password Protection Solution Shared Classes
 *
 * Copyright © 2021-present Carsten Rambow (spps.dev@elomagic.de)
 *
 * This file is part of Simple Password Protection Solution Shared Classes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.elomagic.spps.shared;

/**
 * Signals that a SimpleCrypt exception of some sort has occurred.
 *
 * This class is the general class of exceptions produced by failed SimpleCrypt operations.
 */
public class SimpleCryptException extends RuntimeException {

    /**
     * Constructs an SimpleCryptException with the specified detail message.

     * @param message The detail message (which is saved for later retrieval by the getMessage() method)
     */
    public SimpleCryptException(String message) {
        super(message);
    }

    /**
     * Constructs an SimpleCryptException with the specified detail message and cause.
     *
     * Note that the detail message associated with cause is not automatically incorporated into this exception's detail message.
     *
     * @param message The detail message (which is saved for later retrieval by the getMessage() method)
     * @param cause The cause (which is saved for later retrieval by the getCause() method). (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public SimpleCryptException(String message, Throwable cause) {
        super(message, cause);
    }

}
