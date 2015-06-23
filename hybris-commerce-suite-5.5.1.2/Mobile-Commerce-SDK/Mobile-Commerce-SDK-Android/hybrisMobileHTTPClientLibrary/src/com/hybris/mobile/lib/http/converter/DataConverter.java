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
package com.hybris.mobile.lib.http.converter;

import java.util.List;

import com.hybris.mobile.lib.http.converter.exception.DataConverterException;


/**
 * Interface for the data converter
 */
public interface DataConverter
{

	/**
	 * Get the java object associated to the data string
	 *
	 * @param className Object to be associated
	 * @param data String that contains data to be converted
	 * @return Converted Java Object
	 * @throws DataConverterException
	 */
	public <T> T convertFrom(Class<T> className, String data) throws DataConverterException;

	/**
	 * Get the java object associated to the data string within the property name
	 *
	 * @param className Object to be associated
	 * @param data String that contains data to be converted
	 * @param property Attribute from the data string
	 * @return Converted Java Object
	 */
	public <T> T convertFrom(Class<T> className, String data, String property) throws DataConverterException;

	/**
	 * Get the java object List associated to the data string
	 *
	 * @param className Object to be associated
	 * @param data String that contains data to be converted
	 * @return Converted Java Object
	 */
	public <T> List<T> convertFromList(Class<T> className, String data) throws DataConverterException;

	/**
	 * Get the java object List associated to the data string within the property name
	 *
	 * @param className Object to be associated
	 * @param data String that contains data to be converted
	 * @param property Attribute from the data string
	 * @return Converted Java Object
	 */
	public <T> List<T> convertFromList(Class<T> className, String data, String property) throws DataConverterException;

	/**
	 * Convert the data object to a data string
	 *
	 * @param data String that contains data to be converted
	 * @return Converted Java Object
	 */
	public String convertTo(Object data) throws DataConverterException;


	public List<String> getValuesFromProperty(String data, String property, String element, boolean recursive);

	public List<String> getValuesFromProperty(String data, String property);

	public List<String> getValues(String data, String element);

	public List<String> getValues(String data);

	/**
	 * Create the data error string containing an error message
	 *
	 * @param errorMessage String of character to inform user
	 * @param errorType Kind of message
	 * @return Data error string containing an error message
	 */
	public String createErrorMessage(String errorMessage, String errorType);

	/**
	 * Helper for data conversion
	 *
	 * @param <T>
	 */
	static class Helper<T, Z>
	{

		private final Class<T> className;
		private final Class<Z> errorClassName;
		private final String propertyName;

		private Helper(Class<T> className, Class<Z> errorClassName, String propertyName)
		{
			this.className = className;
			this.errorClassName = errorClassName;
			this.propertyName = propertyName;
		}

		public static <T, Z> Helper<T, Z> build(Class<T> className, Class<Z> errorClassName)
		{
			return new Helper<>(className, errorClassName, null);
		}

		public static <T, Z> Helper<T, Z> build(Class<T> className, Class<Z> errorClassName, String propertyName)
		{
			return new Helper<>(className, errorClassName, propertyName);
		}

		public Class<T> getClassName()
		{
			return className;
		}

		public Class<Z> getErrorClassName()
		{
			return errorClassName;
		}

		public String getPropertyName()
		{
			return propertyName;
		}
	}
}
