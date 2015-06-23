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

package com.hybris.mobile.lib.b2b.data.store;

public class Days {

    private Hours openingTime;
    private Hours closingTime;
    private String weekDay;
    private boolean closed;

    public Hours getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(Hours openingTime) {
        this.openingTime = openingTime;
    }

    public Hours getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(Hours closingTime) {
        this.closingTime = closingTime;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
