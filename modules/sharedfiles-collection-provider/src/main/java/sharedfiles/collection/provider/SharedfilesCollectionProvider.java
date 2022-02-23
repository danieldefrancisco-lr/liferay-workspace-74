package sharedfiles.collection.provider;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.pagination.InfoPage;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.service.SharingEntryLocalService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author jverweij
 */
@Component(service = InfoCollectionProvider.class)
public class SharedfilesCollectionProvider implements InfoCollectionProvider<AssetEntry> {

    @Override
    public InfoPage<AssetEntry> getCollectionInfoPage(CollectionQuery collectionQuery) {

        try {
            return InfoPage.of(getSharedFiles());
        } catch (Exception x) {
            System.out.println("Something wrong here..." + x.getMessage());
        }
        return null;
    }

    @Override
    public String getLabel(Locale locale) {
        return "Shared Documents with User";
    }

    private List<AssetEntry> getSharedFiles() throws PortalException {
        ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext();
        HttpServletRequest httpServletRequest = serviceContext.getRequest();
        ThemeDisplay themeDisplay = (ThemeDisplay) httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY);

        List<SharingEntry> sharedEntries = _sharingEntryLocalService.getToUserSharingEntries(themeDisplay.getUser().getUserId());
        System.out.println(sharedEntries.size());
        List<AssetEntry> assetEntries = new ArrayList<>();

        for (SharingEntry entry:sharedEntries) {
            AssetRenderer<?> assetRenderer = _getAssetEntryRenderer(entry);
            AssetEntry asset = AssetEntryLocalServiceUtil.getEntry(DLFileEntry.class.getName(), assetRenderer.getClassPK());
            assetEntries.add(asset);
        }

        return assetEntries;
    }

    private AssetRenderer<?> _getAssetEntryRenderer(SharingEntry sharingEntry)
            throws PortalException {

        AssetRendererFactory<?> assetRendererFactory =
                AssetRendererFactoryRegistryUtil.
                        getAssetRendererFactoryByClassNameId(
                                sharingEntry.getClassNameId());

        return assetRendererFactory.getAssetRenderer(sharingEntry.getClassPK());
    }

    @Reference
    private SharingEntryLocalService _sharingEntryLocalService;
}