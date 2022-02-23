<%@ include file="./init.jsp" %>

<%
CPContentHelper cpContentHelper = (CPContentHelper)request.getAttribute(CPContentWebKeys.CP_CONTENT_HELPER);
CPCatalogEntry cpCatalogEntry = cpContentHelper.getCPCatalogEntry(request);
CPSku cpSku = cpContentHelper.getDefaultCPSku(cpCatalogEntry);
long cpDefinitionId = cpCatalogEntry.getCPDefinitionId();
%>
<div class="container-fluid" id="<portlet:namespace /><%= cpCatalogEntry.getCPDefinitionId() %>ProductImages">
<c:choose>
    <c:when test="<%= !cpContentHelper.getImages(cpDefinitionId, themeDisplay).isEmpty() %>">
        <div class="glider-contain">
            <div class="glider">
            
            <%
							for (CPMedia cpMedia : cpContentHelper.getImages(cpDefinitionId, themeDisplay)) {
							%>

								<div class="card thumb" data-url="<%= cpMedia.getURL() %>">
									<img class="center-block img-responsive" src="<%= cpMedia.getURL() %>" />
								</div>
								

							<%
							}
							%>
            </div>
            <button aria-label="Previous" class="glider-prev">
                <liferay-ui:icon icon="angle-left" markupView="lexicon" />
            </button>
            <button aria-label="Next" class="glider-next">
                <liferay-ui:icon icon="angle-right" markupView="lexicon" />
            </button>
            <div role="tablist" class="dots"></div>
        </div>
    </c:when>

    <c:otherwise>
        <div class="alert alert-info">
            <liferay-ui:message key="no-products-were-found" />
        </div>
    </c:otherwise>
</c:choose>
</div>
<script src="https://cdn.jsdelivr.net/npm/glider-js@1/glider.min.js"></script>

<script>
var slider = new Glider(document.querySelector('.glider'), {
        slidesToShow:  2.5,
        slidesToScroll: 1,
        draggable: true,
        dots: '.dots',
        arrows: {
            prev: '.glider-prev',
            next: '.glider-next'
        }
    });

    slideAutoPaly(slider, '.glider');

    function slideAutoPaly(glider, selector, delay = 1500, repeat = true) {
        let autoplay = null;
        const slidesCount = glider.track.childElementCount;
        let nextIndex = 1;
        let pause = true;

        function slide() {
            autoplay = setInterval(() => {
                if (nextIndex >= slidesCount) {
                    if (!repeat) {
                        clearInterval(autoplay);
                    } else {
                        nextIndex = 0;
                    }
                }
                glider.scrollItem(nextIndex++);
            }, delay);
        }

        slide();

        var element = document.querySelector(selector);
        element.addEventListener('mouseover', (event) => {
            if (pause) {
                clearInterval(autoplay);
                pause = false;
            }
        }, 300);

        element.addEventListener('mouseout', (event) => {
            if (!pause) {
                slide();
                pause = true;
            }
        }, 300);
    }
</script>