package commerce.demo.informational.renderer;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.content.constants.CPContentWebKeys;
import com.liferay.commerce.product.content.render.list.entry.CPContentListEntryRenderer;
import com.liferay.commerce.product.content.util.CPContentHelper;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author dfrancisco
 */
@Component(
		immediate = true,
		property = {
				"commerce.product.content.list.entry.renderer.key=" + InformationalCPContentListEntryRenderer.KEY,
				"commerce.product.content.list.entry.renderer.portlet.name=" + CPPortletKeys.CP_COMPARE_CONTENT_WEB,
				"commerce.product.content.list.entry.renderer.portlet.name=" + CPPortletKeys.CP_PUBLISHER_WEB,
				"commerce.product.content.list.entry.renderer.portlet.name=" + CPPortletKeys.CP_SEARCH_RESULTS,
				"commerce.product.content.list.entry.renderer.type=grouped",
				"commerce.product.content.list.entry.renderer.type=simple",
				"commerce.product.content.list.entry.renderer.type=virtual"
		},	
		service = CPContentListEntryRenderer.class
	)
public class InformationalCPContentListEntryRenderer implements CPContentListEntryRenderer {

	public static final String KEY = "informational-catalog";

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getLabel(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return LanguageUtil.get(resourceBundle, KEY);
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		httpServletRequest.setAttribute(
				CPContentWebKeys.CP_CONTENT_HELPER, _cpContentHelper);

		_jspRenderer.renderJSP(_servletContext,  httpServletRequest, httpServletResponse,
			"/render/entry/view.jsp");
	}

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(
			target = "(osgi.web.symbolicname=commerce.demo.informational.renderer)"
		)
	private ServletContext _servletContext;
	
	@Reference
	private CPContentHelper _cpContentHelper;




}