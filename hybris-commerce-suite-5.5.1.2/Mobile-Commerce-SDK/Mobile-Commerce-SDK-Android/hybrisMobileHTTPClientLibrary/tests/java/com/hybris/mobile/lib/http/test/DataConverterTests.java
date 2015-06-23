/*******************************************************************************
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 ******************************************************************************/
package com.hybris.mobile.lib.http.test;

import android.test.AndroidTestCase;

import com.google.gson.reflect.TypeToken;
import com.hybris.mobile.lib.http.converter.DataConverter;
import com.hybris.mobile.lib.http.converter.JsonDataConverter;
import com.hybris.mobile.lib.http.converter.exception.DataConverterException;

import java.lang.reflect.Type;
import java.util.List;
import java.util.NoSuchElementException;


public class DataConverterTests extends AndroidTestCase {

    private final String dummyPojoJson = "{\"firstName\":\"testFirstName\", \"lastName\":\"testLastName\", \"phoneNumber\":\"testPhoneNumber\", \"dummyPojos\":[{\"firstName\":\"testFirstName2\",\"phoneNumber\":\"testPhoneNumber\"},{\"firstName\":\"testFirstName3\"}] }";
    private final String dummyPojoWithPropertyJson = "{\"dummyPojos\":" + dummyPojoJson + "}";
    private final String dummyPojoJsonList = "[" + dummyPojoJson + "," + dummyPojoJson + "," + dummyPojoJson + "," + dummyPojoJson + "]";
    private final String dummyPojoJsonListWithPropertyJson = "{\"dummyPojos\":[" + dummyPojoJson + "," + dummyPojoJson + ","
            + dummyPojoJson + "," + dummyPojoJson + "]}";

    private DataConverter dataConverter;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dataConverter = new JsonDataConverter() {

            @Override
            public <T> Type getAssociatedTypeFromClass(Class<T> className) {
                Type listType = null;

                if (className == DummyPojo.class) {
                    listType = new TypeToken<List<DummyPojo>>() {
                    }.getType();
                }

                return listType;
            }

            @Override
            public String createErrorMessage(String errorMessage, String errorType) {
                return null;
            }
        };
    }

    public void testConvertFrom() throws DataConverterException {
        DummyPojo dummyPojo = dataConverter.convertFrom(DummyPojo.class, dummyPojoJson);
        assertTrue(dummyPojo != null);
    }

    public void testConvertFromProperty() throws DataConverterException {
        DummyPojo dummyPojo = dataConverter.convertFrom(DummyPojo.class, dummyPojoWithPropertyJson, "dummyPojos");
        assertTrue(dummyPojo != null);
    }

    public void testConvertFromPropertyKO() throws DataConverterException {
        try {
            dataConverter.convertFrom(DummyPojo.class, dummyPojoWithPropertyJson, "unknownProperty");
        } catch (DataConverterException e) {
            assertTrue(true);
        }
    }

    public void testConvertFromList() throws DataConverterException {
        List<DummyPojo> dummyPojos = dataConverter.convertFromList(DummyPojo.class, dummyPojoJsonList);
        assertTrue(dummyPojos != null && !dummyPojos.isEmpty());
    }

    public void testConvertFromListFromProperty() throws DataConverterException {
        List<DummyPojo> dummyPojos = dataConverter
                .convertFromList(DummyPojo.class, dummyPojoJsonListWithPropertyJson, "dummyPojos");
        assertTrue(dummyPojos != null && !dummyPojos.isEmpty());
    }

    public void testConvertFromListFromPropertyKO() throws DataConverterException {
        try {
            dataConverter.convertFromList(DummyPojo.class, dummyPojoJsonListWithPropertyJson, "unknownProperty");
        } catch (DataConverterException e) {
            assertTrue(true);
        }
    }

    public void testGetValues() throws DataConverterException {
        List<String> data = dataConverter.getValues(dummyPojoJsonList);

        for (String currentData : data) {
            assertTrue(dataConverter.convertFrom(DummyPojo.class, currentData) != null);
        }
    }

    public void testGetValuesFromElement() {
        List<String> data = dataConverter.getValues(dummyPojoJsonList, "lastName");

        for (String currentData : data) {
            assertTrue(currentData.equals("testLastName"));
        }
    }

    public void testGetValuesFromElementKO() {
        try {
            dataConverter.getValues(dummyPojoJsonList, "unknownElement");
        } catch (NoSuchElementException e) {
            assertTrue(true);
        }
    }

    public void testGetValuesFromProperty() throws DataConverterException {
        List<String> data = dataConverter.getValuesFromProperty(dummyPojoJsonListWithPropertyJson, "dummyPojos");

        for (String currentData : data) {
            assertTrue(dataConverter.convertFrom(DummyPojo.class, currentData) != null);
        }
    }

    public void testGetValuesFromPropertyKO() {
        try {
            dataConverter.getValuesFromProperty(dummyPojoJsonListWithPropertyJson, "unknownProperty");
        } catch (NoSuchElementException e) {
            assertTrue(true);
        }
    }

    public void testGetValuesFromPropertyFromElement() {
        List<String> data = dataConverter.getValuesFromProperty(dummyPojoJsonListWithPropertyJson, "dummyPojos", "lastName", false);

        for (String currentData : data) {
            assertTrue(currentData.equals("testLastName"));
        }
    }

    public void testGetValuesFromPropertyFromElementKO() {
        try {
            dataConverter.getValuesFromProperty(dummyPojoJsonListWithPropertyJson, "dummyPojos", "unknownElement", false);
        } catch (NoSuchElementException e) {
            assertTrue(true);
        }
    }

    public void testGetValuesFromPropertyFromElementRecursive() {
        List<String> data = dataConverter.getValuesFromProperty(dummyPojoWithPropertyJson, "dummyPojos", "firstName", true);

        assertTrue(data.get(0).equals("testFirstName2"));
        assertTrue(data.get(1).equals("testFirstName3"));
        assertTrue(data.get(2).equals("testFirstName"));
    }

    class DummyPojo {

        private List<DummyPojo> dummyPojos;
        private String firstName;
        private String lastName;
        private String phoneNumber;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public List<DummyPojo> getDummyPojos() {
            return dummyPojos;
        }

        public void setDummyPojos(List<DummyPojo> dummyPojos) {
            this.dummyPojos = dummyPojos;
        }
    }

}
