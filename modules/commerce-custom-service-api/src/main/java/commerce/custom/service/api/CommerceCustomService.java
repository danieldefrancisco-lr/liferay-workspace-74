package commerce.custom.service.api;

import com.liferay.portal.kernel.exception.PortalException;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import com.liferay.commerce.product.catalog.CPCatalogEntry;


/**
 * @author Dani
 */
public interface CommerceCustomService {
	
	public List<CPCatalogEntry> getPurchasedProducts(long companyId, long groupId, 
			long commerceAccountId, Locale locale, int start, int end) throws PortalException;
	
	public Integer countPurchasedProductsQuery(long companyId, long groupId, 
			long commerceAccountId) throws PortalException;
	
	public Collection<Long> getCommerceAccountIdOrders(long companyId, long groupId, 
			long commerceAccountId) throws PortalException;
	
}

