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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import static com.worldpay.payment.TransactionStatus.AUTHORISED;

/**
 * Customized AuthorizePaymentAction to authorize using Worldpay
 */
public class WorldpayAuthorizePaymentAction extends AbstractSimpleDecisionAction<ReplenishmentProcessModel> {

    private static final Logger LOG = LogManager.getLogger(WorldpayAuthorizePaymentAction.class);

    private ImpersonationService impersonationService;
    private WorldpayDirectOrderFacade worldpayDirectOrderFacade;
    private WorldpayMerchantInfoService worldpayMerchantInfoService;

    @Override
    public Transition executeAction(final ReplenishmentProcessModel process) {
        final BusinessProcessParameterModel clonedCartParameter = processParameterHelper.getProcessParameterByName(process, "cart");
        final CartModel clonedCart = (CartModel) clonedCartParameter.getValue();
        getModelService().refresh(clonedCart);

        final ImpersonationContext context = new ImpersonationContext();
        context.setOrder(clonedCart);
        return impersonationService.executeInContext(context, (ImpersonationService.Executor<Transition, ImpersonationService.Nothing>) () -> {
                    if (clonedCart.getPaymentInfo() instanceof CreditCardPaymentInfoModel) {
                        return authoriseRecurringPaymentWithCreditCard(clonedCart);
                    }
                    return Transition.OK;
                });
    }

    private Transition authoriseRecurringPaymentWithCreditCard(final CartModel clonedCart) {
        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = new WorldpayAdditionalInfoData();
        worldpayAdditionalInfoData.setReplenishmentOrder(true);
        try {
            final MerchantInfo merchantInfo = worldpayMerchantInfoService.getReplenishmentMerchant();
            final DirectResponseData directResponseData = worldpayDirectOrderFacade.authoriseRecurringPayment(clonedCart, worldpayAdditionalInfoData, merchantInfo);
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

    @Required
    public void setImpersonationService(final ImpersonationService impersonationService) {
        this.impersonationService = impersonationService;
    }
    @Required
    public void setWorldpayDirectOrderFacade(final WorldpayDirectOrderFacade worldpayDirectOrderFacade) {
        this.worldpayDirectOrderFacade = worldpayDirectOrderFacade;
    }
    @Required
    public void setWorldpayMerchantInfoService(final WorldpayMerchantInfoService worldpayMerchantInfoService) {
        this.worldpayMerchantInfoService = worldpayMerchantInfoService;
    }
}
