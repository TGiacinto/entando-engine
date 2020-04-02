/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
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

package org.entando.entando.aps.system.services.api.server;

import com.agiletec.aps.system.exception.ApsSystemException;
import java.util.Properties;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.api.model.ApiMethod;

/**
 * @author E.Santoboni
 */
public interface IResponseBuilder {

    String SUCCESS = "SUCCESS";
    String FAILURE = "FAILURE";

    ApiMethod extractApiMethod(ApiMethod.HttpMethod httpMethod, String namespace, String resourceName) throws ApiException;

    @Deprecated
    Object invoke(String resourceName, Properties parameters) throws ApiException, ApsSystemException;

    @Deprecated
    Object createResponse(String resourceName, Properties parameters) throws ApsSystemException;

    Object createResponse(ApiMethod method, Properties parameters) throws ApsSystemException;

    Object createResponse(ApiMethod method, Object bodyObject, Properties parameters) throws ApsSystemException;

}