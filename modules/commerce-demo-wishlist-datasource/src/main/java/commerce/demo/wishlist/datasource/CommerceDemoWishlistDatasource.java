package commerce.demo.wishlist.datasource;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.commerce.account.model.CommerceAccount;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.data.source.CPDataSource;
import com.liferay.commerce.product.data.source.CPDataSourceResult;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.wish.list.service.CommerceWishListItemLocalService;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel de Francisco
 */
@Component(
	immediate = true,
	property = "commerce.product.data.source.name=" + CommerceDemoWishlistDatasource.NAME,
	service = CPDataSource.class
)
public class CommerceDemoWishlistDatasource implements CPDataSource {

	public static final String NAME = "wishlistDataSource";

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(
			getResourceBundle(locale),
			"products-more-added-to-wishlists");
	}

	@Override
	public String getName() {
		return NAME;
	}
	/**
	 * This will be where we add the business logic to perform the search for
	 * related products. The HttpServletRequest contains a reference to a
	 * particular product which the results should be related to in some way.
	 * The method will return a CPDataSourceResult, which contains a list of the
	 * search results; see the implementation at CPDataSourceResult.java.
	 */
	@Override
	public CPDataSourceResult getResult(
			HttpServletRequest httpServletRequest, int start, int end)
		throws Exception {
		
		ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);
		
		long groupId = _portal.getScopeGroupId(httpServletRequest);
		List<CPCatalogEntry> cpCatalogEntries = getMostWishedProducts(groupId,_portal.getCompanyId(httpServletRequest),
				getCommerceAccountId(httpServletRequest),themeDisplay.getLocale(),start,end);
		int resultsSize = countMostWishedProducts(groupId,_portal.getCompanyId(httpServletRequest),
				getCommerceAccountId(httpServletRequest),themeDisplay.getLocale());
		if (end > CommerceDemoWishlistDatasourceConstants.MAX_NUMBER_RESULTS) {
			resultsSize = CommerceDemoWishlistDatasourceConstants.MAX_NUMBER_RESULTS;
		}
		return new CPDataSourceResult(cpCatalogEntries,resultsSize);
	}

	protected List<CPCatalogEntry> getMostWishedProducts(long groupId, long companyId,
			long commerceAccountId,Locale locale, int start, int end) 
		throws PortalException {
		
		DynamicQuery wishlistQuery = createWishListDynamicQuery(groupId, companyId);
		List<CPCatalogEntry> CPCatalogEntryList = new ArrayList<CPCatalogEntry>();
		List<Object[]> results = _commerceWishListItemLocalService.dynamicQuery(wishlistQuery,start,end);

		List <Long> cproductIds = new ArrayList<Long>();
		if (results != null) {
			for (Object[] row : results) {
				cproductIds.add(new Long((Long)row[0]));
			}
		}
		if (!cproductIds.isEmpty()) {
			long cpDefinitionId;
			for (Long cproductId : cproductIds) {
				cpDefinitionId = _cpDefinitionLocalService.fetchCPDefinitionByCProductId(cproductId.longValue()).getCPDefinitionId();
				CPCatalogEntryList.add(_cpDefinitionHelper.getCPCatalogEntry(commerceAccountId, groupId, cpDefinitionId, locale));
			}
		}	
		
		return CPCatalogEntryList;
	}
	
	protected int countMostWishedProducts(long groupId, long companyId,
			long commerceAccountId,Locale locale) 
		throws PortalException {
		
		DynamicQuery wishlistQuery = createWishListDynamicQuery(groupId, companyId);
		List<Object[]> results = _commerceWishListItemLocalService.dynamicQuery(wishlistQuery);
		return results.size();
	}
	
	private long getCommerceAccountId (HttpServletRequest httpServletRequest) 
			throws PortalException {
		CommerceContext commerceContext =
				(CommerceContext)httpServletRequest.getAttribute(
					CommerceWebKeys.COMMERCE_CONTEXT);
		
		CommerceAccount commerceAccount = commerceContext.getCommerceAccount();
		long commerceAccountId = 0;

		if (commerceAccount != null) {
			commerceAccountId = commerceAccount.getCommerceAccountId();
		}
		
		return commerceAccountId;
	}
	
	private DynamicQuery createWishListDynamicQuery(long groupId, long companyId) {
		DynamicQuery wishlistQuery = _commerceWishListItemLocalService.dynamicQuery();
		wishlistQuery.add(RestrictionsFactoryUtil.eq("groupId", groupId))
		            .add(RestrictionsFactoryUtil.eq("companyId", companyId))
		            .setProjection(ProjectionFactoryUtil.projectionList()
		            .add(ProjectionFactoryUtil.groupProperty("CProductId"))
		            .add(ProjectionFactoryUtil.alias(ProjectionFactoryUtil.rowCount(),"added")))
		            .addOrder(OrderFactoryUtil.desc("added"));
		return wishlistQuery;
	}

	
	protected ResourceBundle getResourceBundle(Locale locale) {
		return ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;
	
	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;
	
	@Reference
	private CommerceWishListItemLocalService _commerceWishListItemLocalService;
	
	@Reference
	private Portal _portal;

}