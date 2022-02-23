package commerce.custom.service;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import commerce.custom.service.api.CommerceCustomService;

/**
 * @author Dani
 */
@Component(
	immediate = true,
	property = {
		// TODO enter required service properties
	},
	service = CommerceCustomService.class
)
public class CommerceCustomServiceImpl implements CommerceCustomService {

	// TODO enter required service methods
	
	public List<CPCatalogEntry> getPurchasedProducts(long companyId, long groupId, 
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
	
	public Integer countPurchasedProductsQuery(long companyId, long groupId, 
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
	
	
	
	public Collection<Long> getCommerceAccountIdOrders(long companyId, long groupId, 
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
	
	
	
	protected ResourceBundle getResourceBundle(Locale locale) {
		return ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());
	}

	private static final Log _log = LogFactoryUtil.getLog(
			CommerceCustomServiceImpl.class);
	

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