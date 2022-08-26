package demo.collection.providers;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.util.AssetHelper;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.pagination.InfoPage;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(immediate = true, service = InfoCollectionProvider.class)
public class RecentWebContentInfoCollectionProvider
	extends BaseWebContentsInfoCollectionProvider
	implements InfoCollectionProvider<AssetEntry> {

	@Override
	public InfoPage<AssetEntry> getCollectionInfoPage(
		CollectionQuery collectionQuery) {

		AssetEntryQuery assetEntryQuery = getAssetEntryQuery(
			Field.MODIFIED_DATE, "DESC", collectionQuery.getPagination());

		try {
			SearchContext searchContext = _getSearchContext();

			Hits hits = _assetHelper.search(
				searchContext, assetEntryQuery, assetEntryQuery.getStart(),
				assetEntryQuery.getEnd());

			Long count = _assetHelper.searchCount(
				searchContext, assetEntryQuery);

			return InfoPage.of(
				_assetHelper.getAssetEntries(hits),
				collectionQuery.getPagination(), count.intValue());
		}
		catch (Exception exception) {
			_log.error("Unable to get asset entries", exception);
		}

		return InfoPage.of(
			Collections.emptyList(), collectionQuery.getPagination(), 0);
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "recent-web-content");
	}

	private SearchContext _getSearchContext() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		SearchContext searchContext = SearchContextFactory.getInstance(
			new long[0], new String[0], new HashMap<>(),
			serviceContext.getCompanyId(), null, themeDisplay.getLayout(), null,
			serviceContext.getScopeGroupId(), null, serviceContext.getUserId());

		searchContext.setSorts(
			SortFactoryUtil.create(Field.MODIFIED_DATE, Sort.LONG_TYPE, true),
			SortFactoryUtil.create(Field.CREATE_DATE, Sort.LONG_TYPE, true));

		return searchContext;
	}

	private static final Log _log = LogFactoryUtil.getLog(
			RecentWebContentInfoCollectionProvider.class);

	@Reference
	private AssetHelper _assetHelper;

	@Reference
	private Language _language;

}