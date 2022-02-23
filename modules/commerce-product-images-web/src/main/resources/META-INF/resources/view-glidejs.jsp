<%@ include file="./init.jsp" %>

<%
CPContentHelper cpContentHelper = (CPContentHelper)request.getAttribute(CPContentWebKeys.CP_CONTENT_HELPER);
CPCatalogEntry cpCatalogEntry = cpContentHelper.getCPCatalogEntry(request);
CPSku cpSku = cpContentHelper.getDefaultCPSku(cpCatalogEntry);
long cpDefinitionId = cpCatalogEntry.getCPDefinitionId();
%>
<link href="https://cdn.jsdelivr.net/npm/glidejs@2.1.0/dist/css/glide.core.css" rel="stylesheet" type="text/css"  />
<link href="https://cdn.jsdelivr.net/npm/glidejs@2.1.0/dist/css/glide.theme.css" rel="stylesheet" type="text/css"  /> 
<link href="/o/commerce-product-images-web/css/glidejs-ext.css" rel="stylesheet" type="text/css"  />

<div class="container-fluid" id="<portlet:namespace /><%= cpCatalogEntry.getCPDefinitionId() %>ProductImages">
<c:choose>
<c:when test="<%= !cpContentHelper.getImages(cpDefinitionId, themeDisplay).isEmpty() %>">    
 <div class="glide">
 <div class="glide__arrows">
        <button class="glide__arrow prev" data-glide-dir="<">prev</button>
        <button class="glide__arrow next" data-glide-dir=">">next</button>
    </div>
  <div class="glide__track" data-glide-el="track">
    <ul class="glide__slides">
    <%
							for (CPMedia cpMedia : cpContentHelper.getImages(cpDefinitionId, themeDisplay)) {
							%>
      <li class="glide__slide">
      			<div class="box" data-url="<%= cpMedia.getUrl() %>">
                 <img class="center-block img-responsive" src="<%= cpMedia.getUrl() %>" />
                 </div>
      </li>
      <%
							}
							%>
    </ul>
  </div>
  <ul class="glide__bullets"></ul>
  <%-- <div class="glide__arrows" data-glide-el="controls">
    <button class="glide__arrow glide__arrow--left" data-glide-dir="<">prev</button>
    <button class="glide__arrow glide__arrow--right" data-glide-dir=">">next</button>
  </div> --%>
</div>
 </c:when>
 
 
    <c:otherwise>
        <div class="alert alert-info">
            <liferay-ui:message key="no-products-were-found" />
        </div>
    </c:otherwise>
</c:choose>
</div>
<script src="https://cdn.jsdelivr.net/npm/@glidejs/glide"></script>

<script>
glide.on('build.after', function() {
	var slideHeight = $('.glide__slide--active').outerHeight();
	var glideTrack = $('.glide__track').outerHeight();
	if (slideHeight != glideTrack) {
		var newHeight = slideHeight;
		$('.glide__track').css('height', newHeight);
	}
});

glide.on('run.after', function() {
	var slideHeight = $('.glide__slide--active').outerHeight();
	var glideTrack = $('.glide__track').outerHeight();
	if (slideHeight != glideTrack) {
		var newHeight = slideHeight;
		$('.glide__track').css('height', newHeight);
	}
})
	var glider = new Glide('.glide',{
		  type: 'slider',
		  autoplay: 4000,
		  focusAt: 'center',
		  perView: 2
		});
	
	glider.mount();
</script>