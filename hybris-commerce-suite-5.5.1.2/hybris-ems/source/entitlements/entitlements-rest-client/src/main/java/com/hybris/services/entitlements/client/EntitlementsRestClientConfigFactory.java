/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package com.hybris.services.entitlements.client;

import com.hybris.commons.client.ClientConfigFactoryBean;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;

/**
 * Apache HTTP config factory for entitlements rest client.
 */
public class EntitlementsRestClientConfigFactory extends ClientConfigFactoryBean
{
    @Override
    public ApacheHttpClientConfig getObject()
    {
        final DefaultApacheHttpClientConfig config = (DefaultApacheHttpClientConfig) super.getObject();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        return config;
    }
}
