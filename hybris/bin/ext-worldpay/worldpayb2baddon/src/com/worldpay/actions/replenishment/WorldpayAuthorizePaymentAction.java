package com.worldpay.actions.replenishment;

import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.model.MerchantInfo;
import de.hybris.platform.b2bacceleratorservices.model.process.ReplenishmentProcessModel;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

import static com.worldpay.payment.TransactionStatus.AUTHORISED;

/**
 *   Customized AuthorizePaymentAction to authorize using Worldpay
 */
public class WorldpayAuthorizePaymentAction extends AbstractSimpleDecisionAction<ReplenishmentProcessModel> {

    private static final Logger LOG = LoggerFactory.getLogger(WorldpayAuthorizePaymentAction.class);

    @Resource
    private ImpersonationService impersonationService;
    @Resource
    private WorldpayDirectOrderFacade worldpayDirectOrderFacade;
    @Resource
    private WorldpayMerchantInfoService worldpayMerchantInfoService;

    @Override
    public Transition executeAction(final ReplenishmentProcessModel process) {
        final BusinessProcessParameterModel clonedCartParameter = processParameterHelper.getProcessParameterByName(process, "cart");
        final CartModel clonedCart = (CartModel) clonedCartParameter.getValue();
        getModelService().refresh(clonedCart);

        final ImpersonationContext context = new ImpersonationContext();
        context.setOrder(clonedCart);
        return impersonationService.executeInContext(context,
            new ImpersonationService.Executor<Transition, ImpersonationService.Nothing>() {

                @Override
                public Transition execute() {
                    if (clonedCart.getPaymentInfo() instanceof CreditCardPaymentInfoModel) {
                        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = new WorldpayAdditionalInfoData();
                        DirectResponseData directResponseData = null;
                        try {
                            MerchantInfo merchantInfo = worldpayMerchantInfoService.getReplenishmentMerchant();
                            directResponseData = worldpayDirectOrderFacade.authoriseRecurringPayment(clonedCart, worldpayAdditionalInfoData, merchantInfo);
                            if (directResponseData != null && AUTHORISED == directResponseData.getTransactionStatus()) {
                                return Transition.OK;
                            }
                        } catch (WorldpayException | InvalidCartException e) {
                            LOG.error("There was an error authorising the transaction", e);
                        }
                        clonedCart.setStatus(OrderStatus.B2B_PROCESSING_ERROR);
                        modelService.save(clonedCart);
                        return Transition.NOK;
                    }
                    return Transition.OK;
                }
            });
    }
}
