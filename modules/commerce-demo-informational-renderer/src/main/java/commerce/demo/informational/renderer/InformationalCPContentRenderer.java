package commerce.demo.informational.renderer;

import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.content.render.CPContentRenderer;
import com.liferay.commerce.product.type.grouped.util.GroupedCPTypeHelper;
import com.liferay.commerce.product.type.virtual.util.VirtualCPTypeHelper;
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
 * @author Daniel de Francisco
 */

@Component(
	immediate = true,
	property = {
		"commerce.product.content.renderer.key=" + InformationalCPContentRenderer.KEY,
		"commerce.product.content.renderer.type=grouped",
		"commerce.product.content.renderer.type=simple",
		"commerce.product.content.renderer.type=virtual"
	},
	service = CPContentRenderer.class
)
public class InformationalCPContentRenderer implements CPContentRenderer {

	public static final String KEY = "informational";

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
			CPCatalogEntry cpCatalogEntry,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		httpServletRequest.setAttribute(
			"groupedCPTypeHelper", _groupedCPTypeHelper);
		httpServletRequest.setAttribute(
			"virtualCPTypeHelper", _virtualCPTypeHelper);

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/render/view.jsp");
	}

	@Reference
	private GroupedCPTypeHelper _groupedCPTypeHelper;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=commerce.demo.informational.renderer)"
	)
	private ServletContext _servletContext;

	@Reference
	private VirtualCPTypeHelper _virtualCPTypeHelper;

}