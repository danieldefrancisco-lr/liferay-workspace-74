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

<%@ include file="./init.jsp"%>

<%
	CPContentHelper cpContentHelper = (CPContentHelper) request
			.getAttribute(CPContentWebKeys.CP_CONTENT_HELPER);
	CPCatalogEntry cpCatalogEntry = cpContentHelper.getCPCatalogEntry(request);
	CPSku cpSku = cpContentHelper.getDefaultCPSku(cpCatalogEntry);
	long cpDefinitionId = cpCatalogEntry.getCPDefinitionId();
	String longitude = (String) request.getAttribute("longitude");
	String latitude = (String) request.getAttribute("latitude");
	Long groupId = themeDisplay.getScopeGroupId();
	Long productId = cpCatalogEntry.getCProductId();
	Long cpInstanceId = new Long(0);
	if (cpSku != null)
		cpInstanceId = cpSku.getCPInstanceId();
%>
<%
	List<CPDefinitionSpecificationOptionValue> cpDefinitionSpecificationOptionValues = cpContentHelper
			.getCPDefinitionSpecificationOptionValues(cpDefinitionId);
	List<CPOptionCategory> cpOptionCategories = cpContentHelper.getCPOptionCategories(company.getCompanyId());
	List<CPMedia> cpAttachmentFileEntries = cpContentHelper.getCPMedias(cpDefinitionId, themeDisplay);
%>

<link rel="stylesheet"
	href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css"
	integrity="sha512-xodZBNTC5n17Xt2atTPuE1HxjVMSvLVW9ocqUKLsCC5CXdbqCmblAshOMAS6/keqq/sMZMZ19scR4PsZChSR7A=="
	crossorigin="" />
<!-- Make sure you put this AFTER Leaflet's CSS -->
<script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"
	integrity="sha512-XQoYMqMTK8LvdxXYG3nZ448hOEQiglfqkJs1NOQV44cWnUrBc8PkAOcXy20w0vlaXaVUearIOBhiXZ5V3ynxwA=="
	crossorigin=""></script>

<link href="/o/commerce-demo-realestate-renderer/css/main.css"
	rel="stylesheet" type="text/css" />

<div class="container-fluid product-detail" id="<portlet:namespace /><%=cpDefinitionId%>ProductContent" style="padding-left: 25px">
	<div class="product-detail-header">
		<div class="row">
			<div class="col-lg-7 col-md-5">
				<div class="row product-name">
						<h1><%=HtmlUtil.escape(cpCatalogEntry.getName())%></h1>
				</div>
				<div class="row product-short-description">
					<p><%=cpCatalogEntry.getShortDescription()%></p>
				</div>
				<div class="row">
					<div class="col-lg-10 col-md-9 col-xs-10 full-image">
						<commerce-ui:gallery
								CPDefinitionId="<%= cpDefinitionId %>"
								namespace="<%= liferayPortletResponse.getNamespace() %>"
						/>
					</div>
				</div>
			</div>
			<div class="col-lg-4 col-md-4">
				<div class="row">
					<h2 class="commerce-price mt-0">
						<commerce-ui:price
						CPCatalogEntry="<%= cpCatalogEntry %>"
						namespace="<%= liferayPortletResponse.getNamespace() %>"
					/>
					</h2>
				</div>
				<div class="row">
				<c:if test="<%= cpContentHelper.hasCPDefinitionSpecificationOptionValues(cpDefinitionId) %>">
<div id="product-specifications " class="props_features">
	<h2><%=LanguageUtil.get(resourceBundle, "specifications")%></h2>
	<div class="specifications">
		<%
			for (CPOptionCategory cpOptionCategory : cpOptionCategories) {
				List<CPDefinitionSpecificationOptionValue> categorizedCPDefinitionSpecificationOptionValues = cpContentHelper
						.getCategorizedCPDefinitionSpecificationOptionValues(cpDefinitionId,
								cpOptionCategory.getCPOptionCategoryId());
		%>

		<c:if
			test="<%=!categorizedCPDefinitionSpecificationOptionValues.isEmpty()%>">
			<div class="table-responsive">
				<table class="table table-bordered ">
					<colgroup>
						<col style="width: 50%">
						<col style="width: 50%">
					</colgroup>
					<tbody>
						<tr>
							<th><%=cpOptionCategory.getTitle(languageId)%></th>
							<th></th>
						</tr>

						<%
							for (CPDefinitionSpecificationOptionValue cpDefinitionSpecificationOptionValue : categorizedCPDefinitionSpecificationOptionValues) {
										CPSpecificationOption cpSpecificationOption = cpDefinitionSpecificationOptionValue
												.getCPSpecificationOption();
						%>

						<tr>
							<td><%=HtmlUtil.escape(cpSpecificationOption.getTitle(languageId))%></td>
							<td><%=HtmlUtil.escape(cpDefinitionSpecificationOptionValue.getValue(languageId))%></td>
						</tr>

						<%
							}
						%>
					</tbody>
				</table>
			</div>
		</c:if>

		<%
			}
		%>
	</div>
</div>
</c:if>
				</div>
				
			</div>

		</div>




	</div>

<div id="product-details">
	<div class="row">
		<div class="features-list-fav"
			style="background-color: #fff; padding: 25px 25px;">
			<ul class="features-list">
				<li class="features-list-metros">148m<sup>2</sup></li>
				<li class="features-list-habitaciones">3 ro.</li>
				<li class="features-list-banos">2 WC</li>
				<li class="features-list-garaje">2 Garages</li>
			</ul>
		</div>
	</div>
</div>







<div id="product-description " class="props_features">
	<h2><%=LanguageUtil.get(resourceBundle, "description")%></h2>
	<div class="description">
		<p><%=cpCatalogEntry.getDescription()%></p>
	</div>
</div>


<div id="product-attachments " class="props_features">
	<h2><%=LanguageUtil.get(resourceBundle, "downloads")%></h2>
	<div class="attachments">
		<c:if test="<%=!cpAttachmentFileEntries.isEmpty()%>">
			<div class="tab-pane" id="<portlet:namespace />attachments">
				<div class="table-responsive">
					<table class="table table-bordered table-striped">

						<%
							for (CPMedia curCPMedia : cpAttachmentFileEntries) {
						%>

						<tr>
							<td><span><%=curCPMedia.getTitle()%></span> <span> <aui:icon
										cssClass="icon-monospaced" image="download"
										markupView="lexicon" target="_blank"
										url="<%=curCPMedia.getDownloadURL()%>" />
							</span></td>
						</tr>

						<%
							}
						%>

					</table>
				</div>
			</div>
		</c:if>
	</div>
</div>

<div id="product-location " class="props_features">
	<h2><%=LanguageUtil.get(resourceBundle, "location")%></h2>
	<div id="mapid"
		style="width: 600px; height: 400px; position: relative; outline: none;"></div>
</div>

<aui:script>
var mymap = L.map('mapid').setView([<%=latitude%>, <%=longitude%>], 13);
	L.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token={accessToken}', {
   attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, Imagery Â© <a href="https://www.mapbox.com/">Mapbox</a>',
    maxZoom: 18,
    id: 'mapbox/streets-v11',
    tileSize: 512,
    zoomOffset: -1,
    accessToken: 'pk.eyJ1IjoiZGFuaWVsZGVmcmFuY2lzY28iLCJhIjoiY2traTRwcmU2MDM3MzJ3czd0YXZraGRyOSJ9.-P-IRN57WdhI0dQAReqSkg'
}).addTo(mymap);

var circle = L.circle([<%=latitude%>, <%=longitude%>], {
    color: 'blue',
    fillColor: '#99ccff',
    fillOpacity: 0.5,
    radius: 800
}).addTo(mymap);
</aui:script>






