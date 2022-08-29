package commerce.demo.message.listener.channel.marketplace.api;

import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.inventory.service.CommerceInventoryBookedQuantityLocalService;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.service.UserService;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dani
 */
@Component(enabled = true, immediate = true, property = "destination.name="
		+ DestinationNames.COMMERCE_ORDER_STATUS, service = MessageListener.class)
public class MarketplaceChannelMessageListener extends BaseMessageListener {

	@Override
	protected void doReceive(Message message) throws Exception {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(String.valueOf(message.getPayload()));

		long commerceOrderId = jsonObject.getLong("commerceOrderId");

		CommerceOrder commerceOrder = _commerceOrderLocalService.fetchCommerceOrder(commerceOrderId);

		if (commerceOrder == null) {
			return;
		}

		int orderStatus = jsonObject.getInt("orderStatus");

		if (CommerceOrderConstants.ORDER_STATUS_PROCESSING == orderStatus) {

			List<CommerceChannel> commerceChannels = _commerceChannelLocalService
					.getCommerceChannels(commerceOrder.getCompanyId());
			long targetCommerceChannelGroupId = 0;

			for (CommerceChannel commerceChannel : commerceChannels) {
				for (CommerceOrderItem commerceOrderItem : commerceOrder.getCommerceOrderItems()) {
					CommerceCatalog commerceCatalog = commerceOrderItem.getCPDefinition().getCommerceCatalog();
					
					if (commerceCatalog.getName().equals(commerceChannel.getName())) {
						targetCommerceChannelGroupId = commerceChannel.getGroupId();
						_log.info("Compatible Channel Found: "+commerceChannel.getName());
						System.out.println("Commerce Channel Id:" + targetCommerceChannelGroupId);
						break;
					}
				}
			}
			if (targetCommerceChannelGroupId > 0) {
				_log.info("Moving Order to channel with groupId: "+targetCommerceChannelGroupId);
				commerceOrder.setGroupId(targetCommerceChannelGroupId);
				_commerceOrderLocalService.updateCommerceOrder(commerceOrder);
			}

		}
	}

	@Reference
	private CommerceInventoryBookedQuantityLocalService _commerceInventoryBookedQuantityLocalService;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private UserService _userService;
	
	private static final Log _log = LogFactoryUtil.getLog(
			MarketplaceChannelMessageListener.class);

}