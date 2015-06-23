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
package com.hybris.services.entitlements.rest.exception.mappers;

import org.codehaus.jackson.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Mapper to convert JsonProcessingException to HTTP response 412.
 */
@Provider
public class JsonProcessingExceptionMapper implements ExceptionMapper<JsonProcessingException>
{
    private static final Logger LOG = LoggerFactory.getLogger(ValidationExceptionMapper.class);

    @Override
    public Response toResponse(final JsonProcessingException exception)
    {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        LOG.warn(sw.toString());
        // I thought a lot about the most suitable response code for that and decided to use the same
        //   as for ValidationException, because it is kind of validation too,
        //      and I don't want to create too much complexity here                   --myakovlev
        return Response.status(Response.Status.PRECONDITION_FAILED).entity(exception.getMessage())
                .type(MediaType.TEXT_PLAIN_TYPE).build();
    }
}
