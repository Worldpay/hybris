package com.worldpay.facades.order.impl;

import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BReplenishmentRecurrenceEnum;
import de.hybris.platform.b2bacceleratorfacades.order.data.TriggerData;
import de.hybris.platform.b2bacceleratorfacades.order.impl.DefaultB2BAcceleratorCheckoutFacade;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collection;
import java.util.List;

import static de.hybris.platform.util.localization.Localization.getLocalizedString;

/**
 * A decorator for the DefaultB2BAcceleratorCheckoutFacade in order to modify the placeOrder(PlaceOrderData) method.
 */
public class WorldpayB2BAcceleratorCheckoutFacadeDecorator extends DefaultB2BAcceleratorCheckoutFacade {

    protected static final String CART_CHECKOUT_TRANSACTION_NOT_AUTHORIZED = "cart.transation.notAuthorized";
    protected static final String CART_CHECKOUT_TERM_UNCHECKED = "cart.term.unchecked";
    protected static final String CART_CHECKOUT_REPLENISHMENT_NO_STARTDATE = "cart.replenishment.no.startdate";
    protected static final String CART_CHECKOUT_REPLENISHMENT_NO_FREQUENCY = "cart.replenishment.no.frequency";

    private L10NService l10NService;
    private DefaultB2BAcceleratorCheckoutFacade b2BAcceleratorCheckoutFacade;

    @Override
    public <T extends AbstractOrderData> T placeOrder(PlaceOrderData placeOrderData) throws InvalidCartException {
        // term must be checked
        if (!placeOrderData.getTermsCheck().equals(Boolean.TRUE)) {
            throw new EntityValidationException(getLocalizedString(CART_CHECKOUT_TERM_UNCHECKED));
        }
        isAuthorisedNow(placeOrderData);

        if (isValidCheckoutCart(placeOrderData)) {

            if (placeOrderData.getReplenishmentOrder() != null && placeOrderData.getReplenishmentOrder().equals(Boolean.TRUE)) {
                return handleReplenishment(placeOrderData);
            }

            return (T) b2BAcceleratorCheckoutFacade.placeOrder();
        }

        return null;
    }

    protected void isAuthorisedNow(final PlaceOrderData placeOrderData) throws EntityValidationException {
        // for CARD type and Pay-now, transaction must be authorized before placing order
        final boolean isCardPaymentType = CheckoutPaymentType.CARD.getCode().equals(getCart().getPaymentType().getCode());
        if (isCardPaymentType && isPayNowOrder(placeOrderData)) {
            final List<PaymentTransactionModel> transactions = getCart().getPaymentTransactions();
            if (transactions.stream()
                    .map(PaymentTransactionModel::getEntries)
                    .flatMap(Collection::stream)
                    .noneMatch(entry -> PaymentTransactionType.AUTHORIZATION.equals(entry.getType()) && TransactionStatus.ACCEPTED.name().equals(entry.getTransactionStatus()))) {
                throw new EntityValidationException(l10NService.getLocalizedString(CART_CHECKOUT_TRANSACTION_NOT_AUTHORIZED));
            }
        }
    }

    protected <T extends AbstractOrderData> T handleReplenishment(final PlaceOrderData placeOrderData) {
        if (placeOrderData.getReplenishmentStartDate() == null) {
            throw new EntityValidationException(l10NService.getLocalizedString(CART_CHECKOUT_REPLENISHMENT_NO_STARTDATE));
        }

        if (placeOrderData.getReplenishmentRecurrence().equals(B2BReplenishmentRecurrenceEnum.WEEKLY)
                && CollectionUtils.isEmpty(placeOrderData.getNDaysOfWeek())) {
            throw new EntityValidationException(l10NService.getLocalizedString(CART_CHECKOUT_REPLENISHMENT_NO_FREQUENCY));
        }

        final TriggerData triggerData = new TriggerData();
        populateTriggerDataFromPlaceOrderData(placeOrderData, triggerData);

        return (T) b2BAcceleratorCheckoutFacade.scheduleOrder(triggerData);
    }

    protected boolean isPayNowOrder(final PlaceOrderData data) {
        return data.getReplenishmentOrder() == null || !data.getReplenishmentOrder();
    }


    @Required
    public void setL10NService(final L10NService l10NService) {
        this.l10NService = l10NService;
    }

    @Required
    public void setB2BAcceleratorCheckoutFacade(DefaultB2BAcceleratorCheckoutFacade b2BAcceleratorCheckoutFacade) {
        this.b2BAcceleratorCheckoutFacade = b2BAcceleratorCheckoutFacade;
    }
}
