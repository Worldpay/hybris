package com.worldpay.service.request;

import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.*;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.model.token.TokenRequest;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * This class represents the details that must be passed to a call to {@link WorldpayServiceGateway#redirectAuthorise(RedirectAuthoriseServiceRequest) redirectAuthorise()} in the
 * WorldpayServiceGateway
 * <p/>
 * <p>It sets the relevant details into the Order object that are required to be sent</p>
 */
public class RedirectAuthoriseServiceRequest extends AuthoriseServiceRequest {

    protected RedirectAuthoriseServiceRequest(MerchantInfo merchantInfo, String orderCode) {
        super(merchantInfo, orderCode);
    }

    /**
     * Static convenience method for creating an instance of the RedirectAuthoriseServiceRequest
     *
     * @param merch                  merchantInfo to be used in the Worldpay call
     * @param orderInfo              orderInfo to be used in the Worldpay call
     * @param installationId         installationId to be used in the Worldpay call
     * @param orderContent           orderContent to be used in the Worldpay call
     * @param includedPaymentMethods includedPaymentMethods to be used in the Worldpay call
     * @param excludedPaymentMethods excludedPaymentMethods to be used in the Worldpay call
     * @param shopper                shopper object representing the customer
     * @param shippingAddress        shippingAddress to be used in the Worldpay call
     * @param billingAddress         billingAddress to be used in the Worldpay call
     * @param statementNarrative     statementNarrative to be used in the Worldpay call
     * @return new instance of the RedirectAuthoriseServiceRequest initialised with input parameters
     */
    public static RedirectAuthoriseServiceRequest createRedirectAuthoriseRequest(final MerchantInfo merch, final BasicOrderInfo orderInfo, String installationId,
                                                                                 final String orderContent, final List<PaymentType> includedPaymentMethods, final List<PaymentType> excludedPaymentMethods, final Shopper shopper,
                                                                                 final Address shippingAddress, final Address billingAddress, final String statementNarrative) {
        checkParameters("RedirectAuthoriseRequest", merch, orderInfo);
        final RedirectAuthoriseServiceRequest authRequest = new RedirectAuthoriseServiceRequest(merch, orderInfo.getOrderCode());
        final Order reqOrder = createOrder(orderInfo, installationId, orderContent, includedPaymentMethods, excludedPaymentMethods, shopper, shippingAddress, billingAddress, statementNarrative);
        authRequest.setOrder(reqOrder);
        return authRequest;
    }

    /**
     * Static convenience method for creating an instance of the RedirectAuthoriseServiceRequest
     *
     * @param merch                  merchantInfo to be used in the Worldpay call
     * @param orderInfo              orderInfo to be used in the Worldpay call
     * @param installationId         installationId to be used in the Worldpay call
     * @param orderContent           orderContent to be used in the Worldpay call
     * @param includedPaymentMethods includedPaymentMethods to be used in the Worldpay call
     * @param excludedPaymentMethods excludedPaymentMethods to be used in the Worldpay call
     * @param shopper                shopper object representing the customer
     * @param shippingAddress        shippingAddress to be used in the Worldpay call
     * @param billingAddress         billingAddress to be used in the Worldpay call
     * @param statementNarrative     statementNarrative to be used in the Worldpay call
     * @param tokenRequest           requests Worldpay to create a token with the details provided during HOP
     * @return new instance of the RedirectAuthoriseServiceRequest initialised with input parameters
     */
    public static RedirectAuthoriseServiceRequest createTokenAndRedirectAuthoriseRequest(final MerchantInfo merch, final BasicOrderInfo orderInfo, String installationId,
                                                                                         String orderContent, final List<PaymentType> includedPaymentMethods, final List<PaymentType> excludedPaymentMethods, final Shopper shopper,
                                                                                         final Address shippingAddress, final Address billingAddress, String statementNarrative, final TokenRequest tokenRequest) {
        checkParameters("TokenAndRedirectAuthoriseRequest", merch, orderInfo, tokenRequest);
        final RedirectAuthoriseServiceRequest authRequest = new RedirectAuthoriseServiceRequest(merch, orderInfo.getOrderCode());
        final Order reqOrder = createOrder(orderInfo, installationId, orderContent, includedPaymentMethods, excludedPaymentMethods, shopper, shippingAddress, billingAddress, statementNarrative);
        reqOrder.setTokenRequest(tokenRequest);
        authRequest.setOrder(reqOrder);
        return authRequest;
    }

    private static Order createOrder(final BasicOrderInfo orderInfo, final String installationId, final String orderContent, final List<PaymentType> includedPaymentMethods, final List<PaymentType> excludedPaymentMethods, final Shopper shopper, final Address shippingAddress, final Address billingAddress, final String statementNarrative) {
        final Order reqOrder = new Order(orderInfo.getOrderCode(), orderInfo.getDescription(), orderInfo.getAmount());
        reqOrder.setInstallationId(installationId);
        reqOrder.setOrderContent(orderContent);
        final PaymentMethodMask pmm = new PaymentMethodMask();
        if (CollectionUtils.isNotEmpty(includedPaymentMethods)) {
            includedPaymentMethods.forEach(pmm::addInclude);
        }
        if (CollectionUtils.isNotEmpty(excludedPaymentMethods)) {
            excludedPaymentMethods.forEach(pmm::addExclude);
        }
        if (CollectionUtils.isNotEmpty(includedPaymentMethods) || CollectionUtils.isNotEmpty(excludedPaymentMethods)) {
            reqOrder.setPaymentMethodMask(pmm);
        }
        reqOrder.setShopper(shopper);
        reqOrder.setShippingAddress(shippingAddress);
        reqOrder.setBillingAddress(billingAddress);
        reqOrder.setStatementNarrative(statementNarrative);
        return reqOrder;
    }
}
