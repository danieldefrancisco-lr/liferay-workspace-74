
package commerce.demo.realestate.renderer.custom.renderer;

import com.liferay.commerce.account.model.CommerceAccount;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.content.render.CPContentRenderer;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.wish.list.model.CommerceWishList;
import com.liferay.commerce.wish.list.service.CommerceWishListItemService;
import com.liferay.commerce.wish.list.service.CommerceWishListService;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


/**
 * It is important to provide a distinct key for the product content renderer so
 * that Liferay Commerce can distinguish the new renderer from others in the
 * product content renderer registry. Reusing a key that is already in use will
 * override the existing associated renderer. The
 * commerce.product.content.renderer.order property determines the ordering of
 * renderers in the UI, from lowest to highest value. For example, the
 * SimpleCPContentRenderer has this property set to the minimum integer value,
 * so other renderers for Simple type products will appear after it in the list.
 * The commerce.product.content.renderer.type property determines what type of
 * product this renderer can be used for. In our example, we use a Simple type,
 * so the renderer will appear under the Simple category in the UI.
 */

/**
 * @author Daniel de Francisco
 */
@Component(immediate = true, property = {
	"commerce.product.content.renderer.key=" + SimpleRealEstateCPContentRenderer.KEY,
    "commerce.product.content.renderer.order=" + 1,
	"commerce.product.content.renderer.type=simple",
}, service = CPContentRenderer.class)
public class SimpleRealEstateCPContentRenderer implements CPContentRenderer {

	public static final String KEY = "simple-real-estate";

	/**
	 * This method provides a unique identifier for the product content renderer
	 * in the product content renderer registry. The key can be used to fetch
	 * the renderer from the registry. Reusing a key that is already in use will
	 * override the existing associated renderer.
	 */
	@Override
	public String getKey() {

		return KEY;
	}

	/**
	 * This returns a text label that describes the product content renderer.
	 * See the implementation in CustomCPContentRenderer.java for a reference in
	 * retrieving the label with a language key.
	 */
	@Override
	public String getLabel(Locale locale) {

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return LanguageUtil.get(resourceBundle, "simple-real-estate");
	}

	@Override
	public void render(
		CPCatalogEntry cpCatalogEntry, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse)
		throws Exception {
		
		ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);
		
		CommerceContext commerceContext =
				(CommerceContext)httpServletRequest.getAttribute(
					CommerceWebKeys.COMMERCE_CONTEXT);
		
		CommerceAccount commerceAccount = commerceContext.getCommerceAccount();
		long commerceAccountId = 0;

		if (commerceAccount != null) {
			commerceAccountId = commerceAccount.getCommerceAccountId();
		}
		httpServletRequest.setAttribute("commerceAccountId", commerceAccountId);
		httpServletRequest.setAttribute("cpCatalogEntry", cpCatalogEntry);
		
		/* Get geolocation from custom field (expando) and put it in the request as attributes */
		/* @TO-DO: make custom field name configurable */
		CPDefinition cpDefinition = cpDefinitionLocalService.fetchCPDefinition(cpCatalogEntry.getCPDefinitionId());
		JSONObject geolocation = (JSONObject) cpDefinition.getExpandoBridge().getAttribute("Property Location");
		
		httpServletRequest.setAttribute("latitude", geolocation.getString("latitude"));
		httpServletRequest.setAttribute("longitude", geolocation.getString("longitude"));
		
		/* Get wishlist information to control if the button must be enabled  */
		CommerceWishList commerceWishList =
				_commerceWishListService.getDefaultCommerceWishList(
					themeDisplay.getScopeGroupId(), themeDisplay.getUserId());

			if (commerceWishList != null) {
				boolean addedToWishlist = false;

				if (cpDefinition != null) {
					if (_commerceWishListItemService.
							getCommerceWishListItemByContainsCProductCount(
								commerceWishList.getCommerceWishListId(),
								cpDefinition.getCProductId()) > 0) {

						addedToWishlist = true;
					}
				}
				httpServletRequest.setAttribute("addedToWishlist", addedToWishlist);

				httpServletRequest.setAttribute("addToWishlistButtonVisible", true);
				httpServletRequest.setAttribute(
					"wishlistAPI",
					_portal.getPortalURL(httpServletRequest) +
						"/o/commerce-ui/wish-list-item");
			}
			else {
				httpServletRequest.setAttribute("addToWishlistButtonVisible", false);
			}

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/render/view-simple.jsp");

	}

	private static final Log _log =
		LogFactoryUtil.getLog(SimpleRealEstateCPContentRenderer.class);

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(target = "(osgi.web.symbolicname=commerce.demo.realestate.renderer)")
	private ServletContext _servletContext;
	
	@Reference
	private CPDefinitionLocalService cpDefinitionLocalService;

	@Reference
	private CommerceWishListItemService _commerceWishListItemService;

	@Reference
	private CommerceWishListService _commerceWishListService;
	
	@Reference
	private Portal _portal;
}
