/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.dynamic.data.mapping.form.field.type.internal.accountdata.field.form;

import com.liferay.commerce.account.model.CommerceAccount;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author daniel.defrancisco
 */
@Component(
	immediate = true, property = "ddm.form.field.type.name=accountdataField",
	service = {
		DDMFormFieldTemplateContextContributor.class,
		AccountDataFieldDDMFormFieldTemplateContextContributor.class
	}
)
public class AccountDataFieldDDMFormFieldTemplateContextContributor
	implements DDMFormFieldTemplateContextContributor {

	@Override
	public Map<String, Object> getParameters(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		Map<String, Object> parameters = new HashMap<>();

		String predefinedValue = null;

		if (ddmFormFieldRenderingContext.isReturnFullContext()) {
			try {
				predefinedValue = getPredefinedValue(
					ddmFormField, ddmFormFieldRenderingContext);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		parameters.put("predefinedValue", predefinedValue);

		return parameters;
	}

	protected String getPredefinedValue(
			DDMFormField ddmFormField,
			DDMFormFieldRenderingContext ddmFormFieldRenderingContext)
		throws Exception {

		HttpServletRequest httpServletRequest =
			ddmFormFieldRenderingContext.getHttpServletRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);
		
		CommerceContext commerceContext = (CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);
		
		
		

		String predefinedValue = null;

		if (themeDisplay.isSignedIn()) {
			String accountDataField = (String)ddmFormField.getProperty(
				"accountDataField");

			if (_log.isDebugEnabled()) {
				_log.debug(accountDataField);
			}

			String methodName = accountDataField.replace("[", "");

			methodName = methodName.replace("]", "");
			methodName = methodName.replace("\"", "");

			CommerceAccount commerceAccount = commerceContext.getCommerceAccount();

			Method m = CommerceAccount.class.getMethod(methodName);

			predefinedValue = (String)m.invoke(commerceAccount).toString();
			
		}

		return predefinedValue;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AccountDataFieldDDMFormFieldTemplateContextContributor.class);

}