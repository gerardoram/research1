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

import java.util.List;

public class OpeningHours {

    private List<Days> weekDayOpeningList;

    public List<Days> getWeekDayOpeningList() {
        return weekDayOpeningList;
    }

    public void setWeekDayOpeningList(List<Days> weekDayOpeningList) {
        this.weekDayOpeningList = weekDayOpeningList;
    }
}
