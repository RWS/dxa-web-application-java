/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
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

package org.dd4t.core.exceptions;

public class SerializationException extends FactoryException {

    private static final long serialVersionUID = 1785726125343L;

    public SerializationException() {
        super();
    }

    public SerializationException(final String message) {
        super(message);
    }

    public SerializationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SerializationException(final Throwable cause) {
        super(cause);
    }
}
