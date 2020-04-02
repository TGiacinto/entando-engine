/*
 * Copyright 2018-Present Entando S.r.l. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package org.entando.entando.aps.system.services.cache;

/**
 * @author E.Santoboni
 */
public interface ICacheInfoManager {

    String CACHE_INFO_MANAGER_CACHE_NAME = "Entando_CacheInfoManager";

    String GROUP_CACHE_NAME_PREFIX = "CacheInfoManager_groups_";

    String EXPIRATIONS_CACHE_NAME_PREFIX = "CacheInfoManager_expitation_";

    String DEFAULT_CACHE_NAME = "Entando_Cache";

    @Deprecated
    String CACHE_NAME = DEFAULT_CACHE_NAME;

    void flushEntry(String targetCache, String key);

    void flushGroup(String targetCache, String group);

    void putInGroup(String targetCache, String key, String[] groups);

    boolean isExpired(String targetCache, String key);

}
