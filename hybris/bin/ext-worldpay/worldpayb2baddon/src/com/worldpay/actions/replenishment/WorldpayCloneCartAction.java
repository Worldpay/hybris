package com.worldpay.actions.replenishment;


import de.hybris.platform.b2bacceleratorservices.model.process.ReplenishmentProcessModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Resource;
import java.util.Collections;

/**
 *   Customized CloneCartAction in order to be able to set a normal orderCodeGenerator code
 *   on the cloned cart. Using a Guid as the code causes the generated requestId against
 *   Worldpay in the authorisation later on in the process, to be invalid.
 */
public class WorldpayCloneCartAction extends AbstractProceduralAction<ReplenishmentProcessModel> {

    @Resource
    private CartService cartService;
    @Resource
    private TypeService typeService;
    @Resource
    private UserService userService;

    private KeyGenerator keyGenerator;
    private KeyGenerator guidKeyGenerator;

    @Override
    public void executeAction(final ReplenishmentProcessModel process) {
        final CartToOrderCronJobModel cartToOrderCronJob = process.getCartToOrderCronJob();
        final CartModel cronJobCart = cartToOrderCronJob.getCart();
        userService.setCurrentUser(cronJobCart.getUser());
        final CartModel clone = cartService.clone(typeService.getComposedTypeForClass(CartModel.class),
                typeService.getComposedTypeForClass(CartEntryModel.class), cronJobCart,
                keyGenerator.generate().toString());
        clone.setPaymentAddress(cartToOrderCronJob.getPaymentAddress());
        clone.setDeliveryAddress(cartToOrderCronJob.getDeliveryAddress());
        clone.setPaymentInfo(cartToOrderCronJob.getPaymentInfo());
        clone.setStatus(OrderStatus.CREATED);
        clone.setAllPromotionResults(Collections.EMPTY_SET);
        clone.setPaymentTransactions(Collections.EMPTY_LIST);
        clone.setPermissionResults(Collections.EMPTY_LIST);
        clone.setGuid(guidKeyGenerator.generate().toString());
        this.modelService.save(clone);
        processParameterHelper.setProcessParameter(process, "cart", clone);
    }

    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    @Required
    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    public KeyGenerator getGuidKeyGenerator() {
        return guidKeyGenerator;
    }

    @Required
    public void setGuidKeyGenerator(KeyGenerator guidKeyGenerator) {
        this.guidKeyGenerator = guidKeyGenerator;
    }
}
