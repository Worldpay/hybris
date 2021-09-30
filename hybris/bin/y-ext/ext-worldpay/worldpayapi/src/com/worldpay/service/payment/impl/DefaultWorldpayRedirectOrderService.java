package com.worldpay.service.payment.impl;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.hop.WorldpayHOPPService;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.WorldpayRedirectOrderService;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.math.BigDecimal;
import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


/**
 * Default implementation of the {@link WorldpayRedirectOrderService}
 */
public class DefaultWorldpayRedirectOrderService extends AbstractWorldpayOrderService implements WorldpayRedirectOrderService {

    protected final WorldpayHOPPService worldpayHOPService;

    public DefaultWorldpayRedirectOrderService(final WorldpayPaymentInfoService worldpayPaymentInfoService,
                                               final WorldpayPaymentTransactionService worldpayPaymentTransactionService,
                                               final WorldpayOrderService worldpayOrderService,
                                               final WorldpayServiceGateway worldpayServiceGateway,
                                               final WorldpayHOPPService worldpayHOPService) {
        super(worldpayPaymentInfoService, worldpayPaymentTransactionService, worldpayOrderService, worldpayServiceGateway);
        this.worldpayHOPService = worldpayHOPService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentData redirectAuthorise(final MerchantInfo merchantInfo, final CartModel cartModel, final AdditionalAuthInfo additionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException {
        return worldpayHOPService.buildHOPPageData(cartModel, additionalAuthInfo, merchantInfo, worldpayAdditionalInfoData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completePendingRedirectAuthorise(final RedirectAuthoriseResult result, final String merchantCode, final CartModel cartModel) {
        validateParameterNotNull(result, "RedirectAuthoriseResult cannot be null");

        //Only if payment is CC the payment info already exists in the cart
        final PaymentInfoModel paymentInfoModel = Optional.ofNullable(cartModel.getPaymentInfo())
                .orElse(worldpayPaymentInfoService.createPaymentInfo(cartModel));
        cloneAndSetBillingAddressFromCart(cartModel, paymentInfoModel);
        final CommerceCheckoutParameter commerceCheckoutParameter = worldpayOrderService.createCheckoutParameterAndSetPaymentInfo(paymentInfoModel, result.getPaymentAmount(), cartModel);
        final PaymentTransactionModel paymentTransaction = worldpayPaymentTransactionService.createPaymentTransaction(result.getPending(), merchantCode, commerceCheckoutParameter);
        worldpayPaymentTransactionService.createPendingAuthorisePaymentTransactionEntry(paymentTransaction, merchantCode, cartModel, result.getPaymentAmount());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeConfirmedRedirectAuthorise(final BigDecimal paymentAmount, final String merchantCode, final CartModel cartModel) {
        validateParameterNotNull(paymentAmount, "Payment amount cannot be null");

        final PaymentInfoModel paymentInfoModel = worldpayPaymentInfoService.createPaymentInfo(cartModel);
        cloneAndSetBillingAddressFromCart(cartModel, paymentInfoModel);
        final CommerceCheckoutParameter commerceCheckoutParameter = worldpayOrderService.createCheckoutParameterAndSetPaymentInfo(paymentInfoModel, paymentAmount, cartModel);
        final PaymentTransactionModel paymentTransaction = worldpayPaymentTransactionService.createPaymentTransaction(false, merchantCode, commerceCheckoutParameter);
        worldpayPaymentTransactionService.createNonPendingAuthorisePaymentTransactionEntry(paymentTransaction, merchantCode, cartModel, paymentAmount);
    }
}
