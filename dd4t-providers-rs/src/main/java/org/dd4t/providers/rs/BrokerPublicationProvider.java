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

package org.dd4t.providers.rs;

import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.util.PublicationDescriptor;
import org.dd4t.providers.PublicationProvider;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public class BrokerPublicationProvider extends BaseBrokerProvider implements PublicationProvider {
	@Override public int discoverPublicationId (final String url) throws SerializationException {
		return 7;
	}

	@Override public String discoverPublicationUrl (final int publicationId) {
		return null;
	}

	@Override public String discoverPublicationPath (final int publicationId) {
		return null;
	}

	@Override public String discoverImagesUrl (final int publicationId) {
		return null;
	}

	@Override public String discoverImagesPath (final int publicationId) {
		return null;
	}

	@Override public String discoverPublicationTitle (final int publicationId) {
		return null;
	}

	@Override public String discoverPublicationKey (final int publicationId) {
		return null;
	}

	@Override public PublicationDescriptor getPublicationDescriptor (final int publicationId) {
		return null;
	}
}
