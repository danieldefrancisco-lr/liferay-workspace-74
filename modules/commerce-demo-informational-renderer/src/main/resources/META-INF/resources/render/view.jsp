<%--
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
--%>

<%@ include file="./init.jsp" %>

<%
CPContentHelper cpContentHelper = (CPContentHelper)request.getAttribute(CPContentWebKeys.CP_CONTENT_HELPER);
CPCatalogEntry cpCatalogEntry = cpContentHelper.getCPCatalogEntry(request);
CPSku cpSku = cpContentHelper.getDefaultCPSku(cpCatalogEntry);
long cpDefinitionId = cpCatalogEntry.getCPDefinitionId();
String hideCssClass = "hide";
long skuId = 0;
long stockQuantity = 0;
if (cpSku != null) {
	hideCssClass = StringPool.BLANK;
	skuId = cpSku.getCPInstanceId();
	if (!Validator.isBlank(cpContentHelper.getStockQuantity(request))) {
		stockQuantity = Integer.valueOf(cpContentHelper.getStockQuantity(request));
	}
}
%>

<div id="<portlet:namespace /><%= cpDefinitionId %>ProductContent">
<clay:container-fluid>
	<clay:row justify="between">
		<clay:col size="6">
		
			<commerce-ui:gallery
				CPDefinitionId="<%= cpDefinitionId %>"
				namespace="<%= liferayPortletResponse.getNamespace() %>"
			/>
			
			
		</clay:col>
		<clay:col size="1"/>
		
		<clay:col size="5">
			<div class="justify-content-center">
			<header>
				<div class="availability d-flex mb-4">
					
				</div>


				<c:if test="<%= (cpSku != null) && cpSku.isDiscontinued() && (stockQuantity <= 0) %>">
					<p class="product-description"><%= LanguageUtil.get(request, "this-product-is-discontinued.-you-can-see-the-replacement-product-by-clicking-on-the-button-below") %></p>

					<aui:button cssClass="btn btn-primary btn-sm my-2" href="<%= cpContentHelper.getReplacementCommerceProductFriendlyURL(cpSku, themeDisplay) %>" value="replacement-product" />
				</c:if>

				<p class="my-2 <%= hideCssClass %>" data-text-cp-instance-sku>
					<span class="font-weight-semi-bold">
						<%= LanguageUtil.get(request, "sku") %>
					</span>
					<span>
						<%= (cpSku == null) ? StringPool.BLANK : HtmlUtil.escape(cpSku.getSku()) %>
					</span>
				</p>

				<h2 class="product-header-title"><%= HtmlUtil.escape(cpCatalogEntry.getName()) %></h2>
				
				<p class="my-2 <%= hideCssClass %>" data-text-cp-instance-gtin>
					<span class="font-weight-semi-bold">
						<%= cpCatalogEntry.getShortDescription() %>
					</span>
					
				</p>

				<c:if test="<%= (cpSku != null) && !cpSku.getManufacturerPartNumber().isEmpty() %>">
				<p class="my-2 <%= hideCssClass %>" data-text-cp-instance-manufacturer-part-number>
					<span class="font-weight-semi-bold">
						<%= LanguageUtil.get(request, "mpn") %>:
					</span>
					<span>
						<%= HtmlUtil.escape(cpSku.getManufacturerPartNumber()) %>
					</span>
				</p>
				</c:if>
				
				<c:if test="<%= (cpSku != null) && !cpSku.getGtin().isEmpty() %>">
				<p class="my-2 <%= hideCssClass %>" data-text-cp-instance-gtin>
					<span class="font-weight-semi-bold">
						<%= LanguageUtil.get(request, "gtin") %>:
					</span>
					<span>
						<%= HtmlUtil.escape(cpSku.getGtin()) %>
					</span>
				</p>
				</c:if>
				
			</header>
			</div>
		</clay:col>
	</clay:row>

	<clay:row justify="between">
	
		<clay:col size="6">
		<div class="justify-content-center">
			<p class="mt-3 product-description"><%= cpCatalogEntry.getDescription() %></p>

			
		</div>
		</clay:col>
		<clay:col size="1"/>
		<clay:col size="5">

	


<%
List<CPMedia> cpAttachmentFileEntries = cpContentHelper.getCPMedias(cpDefinitionId, themeDisplay);
List<CPDefinitionSpecificationOptionValue> cpDefinitionSpecificationOptionValues = cpContentHelper.getCPDefinitionSpecificationOptionValues(cpDefinitionId);
List<CPOptionCategory> cpOptionCategories = cpContentHelper.getCPOptionCategories(company.getCompanyId());
%>



<c:if test="<%= cpContentHelper.hasCPDefinitionSpecificationOptionValues(cpDefinitionId) %>">
	<commerce-ui:panel
		elementClasses="flex-column mb-3"
		title='<%= LanguageUtil.get(resourceBundle, "specifications") %>'
	>
		<dl class="specification-list">

			<%
			for (CPDefinitionSpecificationOptionValue cpDefinitionSpecificationOptionValue : cpDefinitionSpecificationOptionValues) {
				CPSpecificationOption cpSpecificationOption = cpDefinitionSpecificationOptionValue.getCPSpecificationOption();
			%>

				<dt class="specification-term">
					<%= HtmlUtil.escape(cpSpecificationOption.getTitle(languageId)) %>
				</dt>
				<dd class="specification-desc">
					<%= HtmlUtil.escape(cpDefinitionSpecificationOptionValue.getValue(languageId)) %>
				</dd>

			<%
			}
			for (CPOptionCategory cpOptionCategory : cpOptionCategories) {
				List<CPDefinitionSpecificationOptionValue> categorizedCPDefinitionSpecificationOptionValues = cpContentHelper.getCategorizedCPDefinitionSpecificationOptionValues(cpDefinitionId, cpOptionCategory.getCPOptionCategoryId());
			%>

				<c:if test="<%= !categorizedCPDefinitionSpecificationOptionValues.isEmpty() %>">

					<%
					for (CPDefinitionSpecificationOptionValue cpDefinitionSpecificationOptionValue : categorizedCPDefinitionSpecificationOptionValues) {
						CPSpecificationOption cpSpecificationOption = cpDefinitionSpecificationOptionValue.getCPSpecificationOption();
					%>

						<dt class="specification-term">
							<%= HtmlUtil.escape(cpSpecificationOption.getTitle(languageId)) %>
						</dt>
						<dd class="specification-desc">
							<%= HtmlUtil.escape(cpDefinitionSpecificationOptionValue.getValue(languageId)) %>
						</dd>

					<%
					}
					%>

				</c:if>

			<%
			}
			%>

		</dl>
	</commerce-ui:panel>
</c:if>

<c:if test="<%= !cpAttachmentFileEntries.isEmpty() %>">
	<commerce-ui:panel
		elementClasses="mb-3"
		title='<%= LanguageUtil.get(resourceBundle, "attachments") %>'
	>
		<dl class="specification-list">

			<%
			int attachmentsCount = 0;
			for (CPMedia curCPAttachmentFileEntry : cpAttachmentFileEntries) {
			%>

				<dt class="specification-term">
					<%= HtmlUtil.escape(curCPAttachmentFileEntry.getTitle()) %>
				</dt>
				<dd class="specification-desc">
					<aui:icon cssClass="icon-monospaced" image="download" markupView="lexicon" target="_blank" url="<%= curCPAttachmentFileEntry.getDownloadURL() %>" />
				</dd>

				<%
				attachmentsCount = attachmentsCount + 1;
				%>

				<c:if test="<%= attachmentsCount >= 2 %>">
					<dt class="specification-empty specification-term"></dt>
					<dd class="specification-desc specification-empty"></dd>

					<%
					attachmentsCount = 0;
					%>

				</c:if>

			<%
			}
			%>

		</dl>
	</commerce-ui:panel>
</c:if>

</clay:col>
</clay:row>
<clay:row justify="between">
<clay:col >
<h4 class="commerce-subscription-info mt-3 w-100">
				<c:if test="<%= cpSku != null %>">
					<commerce-ui:product-subscription-info
						CPInstanceId="<%= cpSku.getCPInstanceId() %>"
					/>
				</c:if>

				<span data-text-cp-instance-subscription-info></span>
				<span data-text-cp-instance-delivery-subscription-info></span>
			</h4>


			<div class="align-items-center d-flex mt-3 product-detail-actions">
				<commerce-ui:add-to-wish-list
					CPCatalogEntry="<%= cpCatalogEntry %>"
					large="<%= true %>"
				/>
				<commerce-ui:compare-checkbox
					CPCatalogEntry="<%= cpCatalogEntry %>"
					label='<%= LanguageUtil.get(resourceBundle, "compare") %>'
				/>
			</div>

			<div class="mt-3">
				
			</div>
	</clay:col>
</clay:row>
</clay:container-fluid>
</div>