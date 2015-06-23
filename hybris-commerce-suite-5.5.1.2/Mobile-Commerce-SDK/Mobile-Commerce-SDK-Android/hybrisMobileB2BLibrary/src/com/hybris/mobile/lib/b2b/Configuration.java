/*******************************************************************************
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 ******************************************************************************/

package com.hybris.mobile.lib.b2b;

/**
 * Settings to define builds with different package name, backend url, Authority and path
 */
public class Configuration {

    private String backendUrl;
    private String catalogPathUrl;
    private String catalogParameterUrl;
    private String catalogAuthority;

    public String getBackendUrl() {
        return backendUrl;
    }

    public void setBackendUrl(String backendUrl) {
        this.backendUrl = backendUrl;
    }

    public String getCatalogPathUrl() {
        return catalogPathUrl;
    }

    public void setCatalogPathUrl(String catalogPathUrl) {
        this.catalogPathUrl = catalogPathUrl;
    }

    public String getCatalogParameterUrl() {
        return catalogParameterUrl;
    }

    public void setCatalogParameterUrl(String catalogParameterUrl) {
        this.catalogParameterUrl = catalogParameterUrl;
    }

    public String getCatalogAuthority() {
        return catalogAuthority;
    }

    public void setCatalogAuthority(String catalogAuthority) {
        this.catalogAuthority = catalogAuthority;
    }
}
