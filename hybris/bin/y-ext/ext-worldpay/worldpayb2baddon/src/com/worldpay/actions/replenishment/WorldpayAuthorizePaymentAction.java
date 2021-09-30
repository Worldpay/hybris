package com.worldpay.actions.replenishment;

import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
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

import static com.worldpay.payment.TransactionStatus.AUTHORISED;

/**
 * Customized AuthorizePaymentAction to authorize using Worldpay
 */
public class WorldpayAuthorizePaymentAction extends AbstractSimpleDecisionAction<ReplenishmentProcessModel> {

    private static final Logger LOG = LogManager.getLogger(WorldpayAuthorizePaymentAction.class);

    protected final ImpersonationService impersonationService;
    protected final WorldpayDirectOrderFacade worldpayDirectOrderFacade;

    public WorldpayAuthorizePaymentAction(final ImpersonationService impersonationService,
                                          final WorldpayDirectOrderFacade worldpayDirectOrderFacade) {
        this.impersonationService = impersonationService;
        this.worldpayDirectOrderFacade = worldpayDirectOrderFacade;
    }

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
            final DirectResponseData directResponseData = worldpayDirectOrderFacade.authoriseRecurringPayment(clonedCart, worldpayAdditionalInfoData);
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
}
