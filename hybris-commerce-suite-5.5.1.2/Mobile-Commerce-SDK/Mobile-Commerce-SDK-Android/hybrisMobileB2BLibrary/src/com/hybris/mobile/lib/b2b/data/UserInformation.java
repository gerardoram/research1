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
package com.hybris.mobile.lib.b2b.data;

import com.hybris.mobile.lib.b2b.helper.SecurityHelper;

import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;


public class UserInformation
{

	private String userId;
	private String cartId;
	private String access_token;
	private String refresh_token;
	private String secureAccessToken;
	private String secureRefreshToken;
	private long expires_in;
	private long issuedOn;
	private Calendar calendarTokenExpiration;
	private boolean tokenInvalid = false;


	public UserInformation(String userId, String secureRefreshToken)
	{
		this.userId = userId;
		this.refresh_token = secureRefreshToken;
	}

	/**
	 * Return true if the token is expired
	 *
	 * @return true if token is expired else false
	 */
	public boolean isTokenExpired()
	{
		if (calendarTokenExpiration == null)
		{
			calendarTokenExpiration = Calendar.getInstance();
			calendarTokenExpiration.setTimeInMillis(issuedOn + expires_in);
		}

		return calendarTokenExpiration.before(Calendar.getInstance());
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getCartId()
	{
		return cartId;
	}

	public void setCartId(String cartId)
	{
		this.cartId = cartId;
	}

	public String getSecureAccessToken()
	{

        if (StringUtils.isBlank(this.secureAccessToken) && StringUtils.isNotBlank(this.access_token)) {
			this.secureAccessToken = SecurityHelper.encrypt(this.access_token);
			this.access_token = null;
		}

		return SecurityHelper.decrypt(this.secureAccessToken);
	}


	public String getSecureRefreshToken()
	{
        if (StringUtils.isBlank(this.secureRefreshToken) && StringUtils.isNotBlank(this.refresh_token)) {
			this.secureRefreshToken = SecurityHelper.encrypt(this.refresh_token);
			this.refresh_token = null;
		}

		return SecurityHelper.decrypt(this.secureRefreshToken);
	}

	public void setIssuedOn(long issuedOn)
	{
		this.issuedOn = issuedOn;
	}

	public void setExpires_in(long expires_in)
	{
		this.expires_in = expires_in;
	}

	public boolean isTokenInvalid()
	{
		return tokenInvalid || StringUtils.isBlank(getSecureAccessToken()) || StringUtils.isBlank(getSecureRefreshToken());
	}

	public void setTokenInvalid(boolean tokenInvalid)
	{
		this.tokenInvalid = tokenInvalid;
	}

    public void reset() {
        userId = null;
        cartId = null;
        access_token = null;
        refresh_token = null;
        secureAccessToken = null;
        secureRefreshToken = null;
        expires_in = 0;
        issuedOn = 0;
        calendarTokenExpiration = null;
        tokenInvalid = true;
    }
}