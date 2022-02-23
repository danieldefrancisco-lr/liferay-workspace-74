package commerce.demo.myproducts.datasource;

import com.liferay.commerce.account.model.CommerceAccount;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.data.source.CPDataSource;
import com.liferay.commerce.product.data.source.CPDataSourceResult;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


/**
 * @author dfrancisco
 */
@Component(
		immediate = true,
		property = "commerce.product.data.source.name=" + CommerceDemoMyproductsDatasource.NAME,
		service = CPDataSource.class
	)
public class CommerceDemoMyproductsDatasource implements CPDataSource {
	
	public static final String NAME = "purchasedProductsDataSource";

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(
			getResourceBundle(locale),
			"purchased-products-by-account");
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
		
		CommerceContext commerceContext = (CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);
		
		long commerceaccountId = getCommerceAccountId(httpServletRequest,commerceContext);
		long companyId = _portal.getCompanyId(httpServletRequest);
		// The groupId of the orders is the ChannelId, and not the GroupId
		long groupId = commerceContext.getCommerceChannelGroupId();
		_log.info("Commerce Account Id: "+commerceaccountId);
		_log.info("Company Id: "+companyId);
		_log.info("Group Id: "+groupId);
		
		List<CPCatalogEntry> cpCatalogEntries = getPurchasedProducts(companyId, groupId,
				commerceaccountId,themeDisplay.getLocale(), start, end);
		int countCpCatalogEntries = countPurchasedProductsQuery(companyId, groupId,
				commerceaccountId).intValue();
		return new CPDataSourceResult(cpCatalogEntries,countCpCatalogEntries);
	}

	protected List<CPCatalogEntry> getPurchasedProducts(long companyId, long groupId, 
			long commerceAccountId, Locale locale, int start, int end) 
		throws PortalException {
		
		DynamicQuery productsPurchasedQuery = createPurchasedProductsQuery(companyId, groupId, commerceAccountId);
		
		List<CPCatalogEntry> CPCatalogEntryList = new ArrayList<CPCatalogEntry>();
		List<Long> results = _commerceOrderItemLocalService.dynamicQuery(productsPurchasedQuery,start,end);
		List <Long> cproductIds = new ArrayList<Long>();
		if (results != null) {
			for (Long row : results) {
				cproductIds.add(row);
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
	
	private Integer countPurchasedProductsQuery(long companyId, long groupId, 
			long commerceAccountId) throws PortalException {
		
		// Subquery to get all approved orders for the selected account
		DynamicQuery accountOrders = createAccountOrdersQuery(companyId, groupId, commerceAccountId);
		
		// Query to count all distinct products included in the selected account orders of the subquery
		DynamicQuery countpurchasedProducts = _commerceOrderItemLocalService.dynamicQuery();
		countpurchasedProducts
					.add(PropertyFactoryUtil.forName("commerceOrderId").in(accountOrders))
		            .setProjection(ProjectionFactoryUtil.projectionList()
		            .add(ProjectionFactoryUtil.countDistinct("CProductId")));
		List<Object> countResult =  _commerceOrderItemLocalService.dynamicQuery(countpurchasedProducts);
		Long countLong = (Long) countResult.get(0);
		Integer count = new Integer(countLong.intValue());
		return count;
	}
	
	
	private DynamicQuery createPurchasedProductsQuery(long companyId, long groupId, 
			long commerceAccountId) throws PortalException {
		
		// Subquery to get all approved orders for the selected account
		DynamicQuery accountOrders = createAccountOrdersQuery(companyId, groupId, commerceAccountId);
		
		// Query to get all distinct products included in the selected account orders of the subquery
		DynamicQuery purchasedProducts = _commerceOrderItemLocalService.dynamicQuery();
		purchasedProducts
					.add(PropertyFactoryUtil.forName("commerceOrderId").in(accountOrders))
		            .setProjection(ProjectionFactoryUtil.projectionList()
		            .add(ProjectionFactoryUtil.distinct(ProjectionFactoryUtil.property("CProductId"))));

		return purchasedProducts;
	}
	
	
	private DynamicQuery createAccountOrdersQuery (long companyId, long groupId, 
			long commerceAccountId) throws PortalException {
		
		// Query to get all approved orders for the selected account
		Collection<Integer> orderStatuses = new ArrayList<Integer>();
		orderStatuses.add(new Integer(10));
		orderStatuses.add(new Integer(15));
		orderStatuses.add(new Integer(0));
		DynamicQuery accountOrders = _commerceOrderLocalService.dynamicQuery();
		accountOrders.add(RestrictionsFactoryUtil.eq("commerceAccountId", commerceAccountId))
					 .add(RestrictionsFactoryUtil.eq("groupId", groupId))
					 .add(RestrictionsFactoryUtil.eq("companyId", companyId))
					 .add(RestrictionsFactoryUtil.in("orderStatus",orderStatuses))
					 .setProjection(ProjectionFactoryUtil.property("commerceOrderId"));
		
		return accountOrders;
	}
	
	
	
	private Collection<Long> getCommerceAccountIdOrders(long companyId, long groupId, 
			long commerceAccountId) throws PortalException {
		
		long[] commerceAccountIds = new long[1];
		commerceAccountIds[0] = commerceAccountId;
		int[] orderStatusApproved = new int[1];
		orderStatusApproved[0] = 10;
		_log.debug("Commerce Orders for accountId: "+commerceAccountId + " in channelId/groupId: "+groupId+ " in companyId: "+companyId);
		List<CommerceOrder> commerceOrders = _commerceOrderLocalService.getCommerceOrders(
				companyId, groupId, commerceAccountIds,
				"", orderStatusApproved, false,
				0, Integer.MAX_VALUE);
		
		_log.debug("Number of orders from get: "+commerceOrders.size());
		Collection <Long> commerceOrderIds = new ArrayList<Long>();
		if (commerceOrders != null) {
			for (CommerceOrder commerceOrder : commerceOrders) {
				_log.debug(commerceOrder.getCommerceOrderId());
				commerceOrderIds.add(commerceOrder.getCommerceOrderId());
			}
		}
		return commerceOrderIds;
	}
	
	private long getCommerceAccountId (HttpServletRequest httpServletRequest, CommerceContext commerceContext) 
			throws PortalException {
		
		CommerceAccount commerceAccount = commerceContext.getCommerceAccount();
		long commerceAccountId = 0;

		if (commerceAccount != null) {
			commerceAccountId = commerceAccount.getCommerceAccountId();
		}
		
		return commerceAccountId;
	}
	
	protected ResourceBundle getResourceBundle(Locale locale) {
		return ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());
	}

	private static final Log _log = LogFactoryUtil.getLog(
			CommerceDemoMyproductsDatasource.class);
	

	@Reference
	private Portal _portal;
	
	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;
	
	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;
	
	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;
	
	
}