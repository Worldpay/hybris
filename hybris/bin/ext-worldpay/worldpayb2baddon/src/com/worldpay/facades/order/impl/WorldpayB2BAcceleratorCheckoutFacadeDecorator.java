package com.worldpay.facades.order.impl;

import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BCommentData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BReplenishmentRecurrenceEnum;
import de.hybris.platform.b2bacceleratorfacades.order.data.TriggerData;
import de.hybris.platform.b2bacceleratorfacades.order.impl.DefaultB2BAcceleratorCheckoutFacade;
import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

import static de.hybris.platform.util.localization.Localization.getLocalizedString;

/**
 * A decorator for the DefaultB2BAcceleratorCheckoutFacade in order to modify the placeOrder(PlaceOrderData) method.
 *
 */
public class WorldpayB2BAcceleratorCheckoutFacadeDecorator extends DefaultB2BAcceleratorCheckoutFacade {

    protected static final String CART_CHECKOUT_TRANSACTION_NOT_AUTHORIZED = "cart.transation.notAuthorized";
    protected static final String CART_CHECKOUT_TERM_UNCHECKED = "cart.term.unchecked";
    protected static final String CART_CHECKOUT_REPLENISHMENT_NO_STARTDATE = "cart.replenishment.no.startdate";
    protected static final String CART_CHECKOUT_REPLENISHMENT_NO_FREQUENCY = "cart.replenishment.no.frequency";
    protected static final String CART_CHECKOUT_NO_QUOTE_DESCRIPTION = "cart.no.quote.description";

    private L10NService l10NService;
    private DefaultB2BAcceleratorCheckoutFacade b2BAcceleratorCheckoutFacade;

    @Override
    public <T extends AbstractOrderData> T placeOrder(PlaceOrderData placeOrderData) throws InvalidCartException {
        // term must be checked
        if (!placeOrderData.getTermsCheck().equals(Boolean.TRUE)) {
            throw new EntityValidationException(getLocalizedString(CART_CHECKOUT_TERM_UNCHECKED));
        }

        // for CARD type and Pay-now, transaction must be authorized before placing order
        final boolean isCardtPaymentType = CheckoutPaymentType.CARD.getCode().equals(getCart().getPaymentType().getCode());
        if (isCardtPaymentType && isPayNowOrder(placeOrderData)) {
            final List<PaymentTransactionModel> transactions = getCart().getPaymentTransactions();
            boolean authorized = false;
            for (final PaymentTransactionModel transaction : transactions) {
                for (final PaymentTransactionEntryModel entry : transaction.getEntries()) {
                    if (entry.getType().equals(PaymentTransactionType.AUTHORIZATION)
                            && TransactionStatus.ACCEPTED.name().equals(entry.getTransactionStatus())) {
                        authorized = true;
                        break;
                    }
                }
            }
            if (!authorized) {
                throw new EntityValidationException(l10NService.getLocalizedString(CART_CHECKOUT_TRANSACTION_NOT_AUTHORIZED));
            }
        }

        if (isValidCheckoutCart(placeOrderData)) {

            if (placeOrderData.getNegotiateQuote() != null && placeOrderData.getNegotiateQuote().equals(Boolean.TRUE)) {
                handleQuote(placeOrderData);
            }

            if (placeOrderData.getReplenishmentOrder() != null && placeOrderData.getReplenishmentOrder().equals(Boolean.TRUE)) {
                return handleReplenishment(placeOrderData);
            }

            return (T) b2BAcceleratorCheckoutFacade.placeOrder();
        }

        return null;
    }

    protected void handleQuote(final PlaceOrderData placeOrderData) {
        if (StringUtils.isBlank(placeOrderData.getQuoteRequestDescription())) {
            throw new EntityValidationException(l10NService.getLocalizedString(CART_CHECKOUT_NO_QUOTE_DESCRIPTION));
        } else {
            final B2BCommentData b2BComment = new B2BCommentData();
            b2BComment.setComment(placeOrderData.getQuoteRequestDescription());

            final CartData cartData = new CartData();
            cartData.setB2BComment(b2BComment);

            b2BAcceleratorCheckoutFacade.updateCheckoutCart(cartData);
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
        boolean isQuote = data.getNegotiateQuote() != null && data.getNegotiateQuote().booleanValue();
        boolean isReplenishment = data.getReplenishmentOrder() != null && data.getReplenishmentOrder().booleanValue();

        return !isQuote && !isReplenishment;
    }

    public L10NService getL10NService() {
        return l10NService;
    }

    @Required
    public void setL10NService(final L10NService l10NService) {
        this.l10NService = l10NService;
    }

    public DefaultB2BAcceleratorCheckoutFacade getB2BAcceleratorCheckoutFacade() {
        return b2BAcceleratorCheckoutFacade;
    }

    @Required
    public void setB2BAcceleratorCheckoutFacade(DefaultB2BAcceleratorCheckoutFacade b2BAcceleratorCheckoutFacade) {
        this.b2BAcceleratorCheckoutFacade = b2BAcceleratorCheckoutFacade;
    }
}
