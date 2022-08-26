package demo.collection.providers;

import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.info.pagination.Pagination;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 * @author Dani
 */
public abstract class BaseWebContentsInfoCollectionProvider {
	
	protected AssetEntryQuery getAssetEntryQuery(
			String orderByCol, String orderByType, Pagination pagination) {

			AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();
			
			long[] availableClassNameIds = new long[1];
				
			long journalArticleClassNameId = PortalUtil.getClassNameId(JournalArticle.class);
			
			availableClassNameIds[0] = journalArticleClassNameId;

			assetEntryQuery.setClassNameIds(availableClassNameIds);

			assetEntryQuery.setEnablePermissions(true);
			assetEntryQuery.setGroupIds(
				new long[] {serviceContext.getScopeGroupId()});

			if (pagination != null) {
				assetEntryQuery.setStart(pagination.getStart());
				assetEntryQuery.setEnd(pagination.getEnd());
			}

			assetEntryQuery.setOrderByCol1(orderByCol);
			assetEntryQuery.setOrderByType1(orderByType);
			assetEntryQuery.setOrderByCol2(Field.CREATE_DATE);
			assetEntryQuery.setOrderByType2("DESC");

			return assetEntryQuery;
		}

		@Reference
		protected Portal portal;
		
		@Reference
		ClassNameLocalService classNameLocalService;

}
