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

package org.dd4t.providers.util;

import com.tridion.broker.StorageException;
import com.tridion.storage.StorageManagerFactory;
import com.tridion.storage.StorageTypeMapping;
import com.tridion.storage.dao.BaseDAO;

/**
 * dd4t-2
 *
 * @author R. Kempees, M. Cadariu
 */
public class DaoUtils {
    public static BaseDAO getStorageDAO (int publicationId, StorageTypeMapping storageTypeMapping) throws StorageException {
        return StorageManagerFactory.getDAO(publicationId, storageTypeMapping);
    }

    private DaoUtils () {

    }
}