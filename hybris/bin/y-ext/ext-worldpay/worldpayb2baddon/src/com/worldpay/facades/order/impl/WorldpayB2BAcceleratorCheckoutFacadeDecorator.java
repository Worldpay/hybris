package com.worldpay.facades.order.impl;

import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BReplenishmentRecurrenceEnum;
import de.hybris.platform.b2bacceleratorfacades.order.data.TriggerData;
import de.hybris.platform.b2bacceleratorfacades.order.impl.DefaultB2BAcceleratorCheckoutFacade;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import org.apache.commons.collections4.CollectionUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

/**
 * A decorator for the DefaultB2BAcceleratorCheckoutFacade in order to modify the placeOrder(PlaceOrderData) method.
 */
public class WorldpayB2BAcceleratorCheckoutFacadeDecorator extends DefaultB2BAcceleratorCheckoutFacade {

    private static final Logger LOG = LogManager.getLogger(WorldpayB2BAcceleratorCheckoutFacadeDecorator.class);


    protected static final String CART_CHECKOUT_TRANSACTION_NOT_AUTHORIZED = "cart.transation.notAuthorized";
    protected static final String CART_CHECKOUT_TERM_UNCHECKED = "cart.term.unchecked";
    protected static final String CART_CHECKOUT_REPLENISHMENT_NO_STARTDATE = "cart.replenishment.no.startdate";
    protected static final String CART_CHECKOUT_REPLENISHMENT_NO_FREQUENCY = "cart.replenishment.no.frequency";
    protected static final String CART_CHECKOUT_PAYMENTTYPE_INVALID = "cart.paymenttype.invalid";
    protected static final String CART_CHECKOUT_DELIVERYADDRESS_INVALID = "cart.deliveryAddress.invalid";
    protected static final String CART_CHECKOUT_DELIVERYMODE_INVALID = "cart.deliveryMode.invalid";
    protected static final String CART_CHECKOUT_PAYMENTINFO_EMPTY = "cart.paymentInfo.empty";
    protected static final String CART_CHECKOUT_NOT_CALCULATED = "cart.not.calculated";
    protected static final String CART_CHECKOUT_QUOTE_REQUIREMENTS_NOT_SATISFIED = "cart.quote.requirements.not.satisfied";

    private L10NService l10NService;
    private DefaultB2BAcceleratorCheckoutFacade b2BAcceleratorCheckoutFacade;
    private CommerceCheckoutService commerceCheckoutService;

    @Override
    public <T extends AbstractOrderData> T placeOrder(final PlaceOrderData placeOrderData) throws InvalidCartException {
        // term must be checked
        if (!placeOrderData.getTermsCheck().equals(Boolean.TRUE)) {
            throw new EntityValidationException(l10NService.getLocalizedString(CART_CHECKOUT_TERM_UNCHECKED));
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNoPaymentInfo() {
        final CartData cartData = callSuperGetCheckoutCart();
        return cartData == null || (cartData.getPaymentInfo() == null && cartData.getWorldpayAPMPaymentInfo() == null);
    }

    @Override
    public boolean setPaymentDetails(final String paymentInfoId) {
        validateParameterNotNullStandardMessage("paymentInfoId", paymentInfoId);

        if (callSuperCheckIfCurrentUserIsTheCartUser() && StringUtils.isNotBlank(paymentInfoId)) {
            final CustomerModel currentUserForCheckout = callSuperGetCurrentUserForCheckout();
            final PaymentInfoModel matchingPaymentInfoModel = callSuperGetCurrentUserForCheckout(currentUserForCheckout).getPaymentInfos().stream()
                    .filter(paymentInfoModel -> paymentInfoId.equalsIgnoreCase(paymentInfoModel.getPk().toString()))
                    .findFirst()
                    .orElse(null);
            final CartModel cartModel = callSuperGetCart();
            if (matchingPaymentInfoModel != null) {
                cartModel.setPaymentAddress(matchingPaymentInfoModel.getBillingAddress());
                final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(cartModel, true);
                parameter.setPaymentInfo(matchingPaymentInfoModel);
                return commerceCheckoutService.setPaymentInfo(parameter);
            }
            LOG.warn(
                    "Did not find CreditCardPaymentInfoModel for user: {}, cart: {} &  paymentInfoId: {}. PaymentInfo Will not get set.",
                    () -> currentUserForCheckout, cartModel::getCode, () -> paymentInfoId);
        }
        return false;
    }

    protected CustomerModel callSuperGetCurrentUserForCheckout(final CustomerModel currentUserForCheckout) {
        return currentUserForCheckout;
    }

    protected CustomerModel callSuperGetCurrentUserForCheckout() {
        return getCurrentUserForCheckout();
    }

    protected boolean callSuperCheckIfCurrentUserIsTheCartUser()
    {
        return checkIfCurrentUserIsTheCartUser();
    }

    protected void isAuthorisedNow(final PlaceOrderData placeOrderData) throws EntityValidationException {
        // for CARD type and Pay-now, transaction must be authorized before placing order
        final boolean isCardPaymentType = CheckoutPaymentType.CARD.getCode().equals(callSuperGetCart().getPaymentType().getCode());
        if (isCardPaymentType && isPayNowOrder(placeOrderData)) {
            final List<PaymentTransactionModel> transactions = callSuperGetCart().getPaymentTransactions();
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

    protected CartData callSuperGetCheckoutCart() {
        return super.getCheckoutCart();
    }

    protected CartModel callSuperGetCart()
    {
        return super.getCart();
    }

    @Override
    protected boolean isValidCheckoutCart(final PlaceOrderData placeOrderData) {
        final CartData cartData = callSuperGetCheckoutCart();
        final boolean valid = true;

        if (!cartData.isCalculated()) {
            throw new EntityValidationException(l10NService.getLocalizedString(CART_CHECKOUT_NOT_CALCULATED));
        }

        if (cartData.getDeliveryAddress() == null) {
            throw new EntityValidationException(l10NService.getLocalizedString(CART_CHECKOUT_DELIVERYADDRESS_INVALID));
        }

        if (cartData.getDeliveryMode() == null) {
            throw new EntityValidationException(l10NService.getLocalizedString(CART_CHECKOUT_DELIVERYMODE_INVALID));
        }

        if (Boolean.TRUE.equals(placeOrderData.getNegotiateQuote()) && !cartData.getQuoteAllowed()) {
            throw new EntityValidationException(l10NService.getLocalizedString(CART_CHECKOUT_QUOTE_REQUIREMENTS_NOT_SATISFIED));
        }

        return valid;
    }

    public L10NService getL10NService() {
        return l10NService;
    }

    public void setL10NService(L10NService l10NService) {
        this.l10NService = l10NService;
    }

    public DefaultB2BAcceleratorCheckoutFacade getB2BAcceleratorCheckoutFacade() {
        return b2BAcceleratorCheckoutFacade;
    }

    public void setB2BAcceleratorCheckoutFacade(DefaultB2BAcceleratorCheckoutFacade b2BAcceleratorCheckoutFacade) {
        this.b2BAcceleratorCheckoutFacade = b2BAcceleratorCheckoutFacade;
    }

    @Override
    public CommerceCheckoutService getCommerceCheckoutService() {
        return commerceCheckoutService;
    }

    @Override
    public void setCommerceCheckoutService(CommerceCheckoutService commerceCheckoutService) {
        this.commerceCheckoutService = commerceCheckoutService;
    }

}
