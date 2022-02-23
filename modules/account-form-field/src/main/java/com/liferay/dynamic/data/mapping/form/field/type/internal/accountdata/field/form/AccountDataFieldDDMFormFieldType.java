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

import com.liferay.dynamic.data.mapping.form.field.type.BaseDDMFormFieldType;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeSettings;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolver;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author daniel.defrancisco
 */
@Component(
	immediate = true,
	property = {
		"ddm.form.field.type.description=accountdata-field-description",
		"ddm.form.field.type.display.order:Integer=14",
		"ddm.form.field.type.group=customized", "ddm.form.field.type.icon=user",
		"ddm.form.field.type.label=accountdata-field-label",
		"ddm.form.field.type.name=accountdataField"
	},
	service = DDMFormFieldType.class
)
public class AccountDataFieldDDMFormFieldType extends BaseDDMFormFieldType {

	@Override
	public Class<? extends DDMFormFieldTypeSettings>
		getDDMFormFieldTypeSettings() {

		return AccountDataFieldDDMFormFieldTypeSettings.class;
	}

	@Override
	public String getModuleName() {
		return _npmResolver.resolveModuleName(
			"dynamic-data-accountdata-field-form/accountdata-field.es");
	}

	@Override
	public String getName() {
		return "accountdataField";
	}

	@Override
	public boolean isCustomDDMFormFieldType() {
		return true;
	}

	@Reference
	private NPMResolver _npmResolver;

}