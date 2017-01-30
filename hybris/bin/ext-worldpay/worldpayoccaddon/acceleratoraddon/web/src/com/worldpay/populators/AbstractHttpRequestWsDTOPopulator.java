package com.worldpay.populators;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;


/**
 * Abstract populator from {@link HttpServletRequest} to WsDTO object defined by subtype
 */
public abstract class AbstractHttpRequestWsDTOPopulator
{

	protected String updateStringValueFromRequest(final HttpServletRequest request, final String paramName,
			final String defaultValue) {
		final String requestParameterValue = getRequestParameterValue(request, paramName);
		if ("".equals(requestParameterValue)) {
			return null;
		}
		return StringUtils.defaultIfBlank(requestParameterValue, defaultValue);
	}

	protected boolean updateBooleanValueFromRequest(final HttpServletRequest request, final String paramName,
			final boolean defaultValue) {
		final String booleanString = updateStringValueFromRequest(request, paramName, null);
		if (booleanString == null) {
			return defaultValue;
		}
		return Boolean.parseBoolean(booleanString);
	}

	protected String getRequestParameterValue(final HttpServletRequest request, final String paramName) {
		return request.getParameter(paramName);
	}
}
