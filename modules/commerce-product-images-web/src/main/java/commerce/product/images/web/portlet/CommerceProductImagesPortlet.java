package commerce.product.images.web.portlet;

import com.liferay.commerce.product.content.constants.CPContentWebKeys;
import com.liferay.commerce.product.content.util.CPContentHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import commerce.product.images.web.constants.CommerceProductImagesPortletKeys;

/**
 * @author dfrancisco
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=commerce",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=Commerce Product Images",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view-glider.jsp",
		"javax.portlet.name=" + CommerceProductImagesPortletKeys.COMMERCEPRODUCTIMAGES,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class CommerceProductImagesPortlet extends MVCPortlet {
	
	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {


		renderRequest.setAttribute(
			CPContentWebKeys.CP_CONTENT_HELPER, _cpContentHelper);

		super.render(renderRequest, renderResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
			CommerceProductImagesPortlet.class);

	@Reference
	private CPContentHelper _cpContentHelper;

	@Reference
	private Portal _portal;
}