package related.documents.info.collection.provider;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.commerce.account.model.CommerceAccount;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.pagination.InfoPage;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import commerce.custom.service.api.CommerceCustomService;

/**
 * @author Dani de Francisco
 */
@Component(immediate = true, service = InfoCollectionProvider.class)
public class RelatedDocumentsInfoCollectionProvider implements InfoCollectionProvider<AssetEntry> {
	
	
	public InfoPage<AssetEntry> getCollectionInfoPage(
			CollectionQuery collectionQuery) {
		
		List<AssetEntry> results = new ArrayList<AssetEntry>();
		
		try {

			List<CPCatalogEntry> cpCatalogEntries = getPurchasedProductsByAccount();
			List<CPAttachmentFileEntry> attachments = new ArrayList<CPAttachmentFileEntry>();
			
		//Get all the attachment files of the products purchased by the selected commerce account
			for (CPCatalogEntry cpCatalogEntry : cpCatalogEntries) {
				attachments.addAll(_cpAttachmentFileEntryLocalServivce.getCPAttachmentFileEntries(_portal.getClassNameId(com.liferay.commerce.product.model.CPDefinition.class),
						cpCatalogEntry.getCPDefinitionId(), 1, 0, 0, Integer.MAX_VALUE));
			}
       
		//Get the AssetEntry associated with each file
			for (CPAttachmentFileEntry cpAttachmentFileEntry : attachments) {
				//IF to avoid duplicates
				if (!results.contains(_assetEntryLocalService.getEntry(dlFileEntryClassName, cpAttachmentFileEntry.getFileEntryId())))
				results.add(
						_assetEntryLocalService.getEntry(dlFileEntryClassName, cpAttachmentFileEntry.getFileEntryId()));
			}
		} catch (PortalException e) {
			_log.error(e.getStackTrace());
		}

		// return _assetEntryLocalService.getAssetEntries(0, 5);
		// return results;
		return InfoPage.of(
				results,
				collectionQuery.getPagination(),
				results.size());
	}

	

	@Override
	public String getLabel(Locale locale) {
		return "Related Documents to purchased products - NEW";
	}

	private long getCommerceAccountId(HttpServletRequest httpServletRequest, CommerceContext commerceContext)
			throws PortalException {

		CommerceAccount commerceAccount = commerceContext.getCommerceAccount();
		long commerceAccountId = 0;

		if (commerceAccount != null) {
			commerceAccountId = commerceAccount.getCommerceAccountId();
		}

		return commerceAccountId;
	}
	
	
	private List<CPCatalogEntry> getPurchasedProductsByAccount() throws PortalException{

		ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext();
		HttpServletRequest httpServletRequest = serviceContext.getRequest();
		ThemeDisplay themeDisplay = (ThemeDisplay) httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		CommerceContext commerceContext = (CommerceContext) httpServletRequest
				.getAttribute(CommerceWebKeys.COMMERCE_CONTEXT);

			long commerceaccountId = getCommerceAccountId(httpServletRequest, commerceContext);
			long companyId = _portal.getCompanyId(httpServletRequest);
			
			// The groupId of the orders is the ChannelId, and not the GroupId
			long groupId = commerceContext.getCommerceChannelGroupId();

			List<CPCatalogEntry> cpCatalogEntries = _commerceCustomService.getPurchasedProducts(companyId, groupId,
					commerceaccountId, themeDisplay.getLocale(), 0, Integer.MAX_VALUE);
			
			return cpCatalogEntries;
	}
	

	private static final Log _log = LogFactoryUtil.getLog(RelatedDocumentsInfoCollectionProvider.class);

	String dlFileEntryClassName = "com.liferay.document.library.kernel.model.DLFileEntry";
	


	@Reference
	private Portal _portal;

	@Reference
	AssetEntryLocalService _assetEntryLocalService;

	@Reference
	CPAttachmentFileEntryLocalService _cpAttachmentFileEntryLocalServivce;

	@Reference
	DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	CommerceCustomService _commerceCustomService;
	

}