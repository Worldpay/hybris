package com.worldpay.facades;

import com.google.common.base.Preconditions;
import com.worldpay.data.*;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.model.IPInformationModel;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.IPLookupService;
import com.worldpay.service.WeatherLookupService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayDirectOrderFacadeDemo implements WorldpayDirectOrderFacade {

    protected final IPLookupService ipLookupService;
    protected final WeatherLookupService weatherLookupService;
    protected final WorldpayDirectOrderFacade directOrderFacade;
    protected final CartService cartService;

    public DefaultWorldpayDirectOrderFacadeDemo(final IPLookupService ipLookupService, final WeatherLookupService weatherLookupService, final WorldpayDirectOrderFacade directOrderFacade, final CartService cartService) {
        this.ipLookupService = ipLookupService;
        this.weatherLookupService = weatherLookupService;
        this.directOrderFacade = directOrderFacade;
        this.cartService = cartService;
    }

    @Override
    public DirectResponseData authorise(final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {

        Preconditions.checkState(cartService.hasSessionCart(), "Cannot authorize payment where there is no cart");

        final CartModel cart = cartService.getSessionCart();
        final IPInformationModel ipInfo = ipLookupService.getIPInformation(worldpayAdditionalInfoData.getCustomerIPAddress());
        cart.setIpInformation(ipInfo);

        final String lat = ipInfo.getLat();
        final String lon = ipInfo.getLon();
        if (lat != null && lon != null) {
            cart.setWeatherInformation(weatherLookupService.getWeatherInformation(lat, lon));
        }

        return directOrderFacade.authorise(worldpayAdditionalInfoData);
    }

    @Override
    public void tokenize(final CartModel cartModel, final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException {
        directOrderFacade.tokenize(cartModel, cseAdditionalAuthInfo, worldpayAdditionalInfoData);
    }

    @Override
    public String authoriseBankTransferRedirect(final BankTransferAdditionalAuthInfo bankTransferAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException {
        return directOrderFacade.authoriseBankTransferRedirect(bankTransferAdditionalAuthInfo, worldpayAdditionalInfoData);
    }

    @Override
    public DirectResponseData authorise3DSecure(final String paResponse, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {
        return directOrderFacade.authorise3DSecure(paResponse, worldpayAdditionalInfoData);
    }

    @Override
    public DirectResponseData authoriseRecurringPayment(final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {
        return directOrderFacade.authoriseRecurringPayment(worldpayAdditionalInfoData);
    }

    @Override
    public DirectResponseData authoriseRecurringPayment(final AbstractOrderModel abstractOrderModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {
        return directOrderFacade.authoriseRecurringPayment(abstractOrderModel, worldpayAdditionalInfoData);
    }

    @Override
    public String authoriseKlarnaRedirect(final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final AdditionalAuthInfo additionalAuthInfo) throws WorldpayException {
        return directOrderFacade.authoriseKlarnaRedirect(worldpayAdditionalInfoData, additionalAuthInfo);
    }

    @Override
    public DirectResponseData authoriseApplePayDirect(final ApplePayAdditionalAuthInfo applePayAdditionalAuthInfo) throws WorldpayException, InvalidCartException {
        return directOrderFacade.authoriseApplePayDirect(applePayAdditionalAuthInfo);
    }

    @Override
    public DirectResponseData authoriseGooglePayDirect(final GooglePayAdditionalAuthInfo googlePayAdditionalAuthInfo) throws WorldpayException, InvalidCartException {
        return directOrderFacade.authoriseGooglePayDirect(googlePayAdditionalAuthInfo);
    }

    @Override
    public ApplePayOrderUpdate updatePaymentMethod(final ApplePayPaymentMethodUpdateRequest paymentMethodUpdateRequest) {
        return directOrderFacade.updatePaymentMethod(paymentMethodUpdateRequest);
    }

    @Override
    public DirectResponseData executeSecondPaymentAuthorisation3DSecure() throws WorldpayException, InvalidCartException {
        return directOrderFacade.executeSecondPaymentAuthorisation3DSecure();
    }

    @Override
    public DirectResponseData executeSecondPaymentAuthorisation3DSecure(final String worldpayOrderCode) throws WorldpayException, InvalidCartException {
        return directOrderFacade.executeSecondPaymentAuthorisation3DSecure(worldpayOrderCode);
    }

    @Override
    public DirectResponseData authoriseAndTokenize(final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final CSEAdditionalAuthInfo cseAdditionalAuthInfo) throws WorldpayException, InvalidCartException {
        return directOrderFacade.authoriseAndTokenize(worldpayAdditionalInfoData, cseAdditionalAuthInfo);
    }

    @Override
    public void tokenize(final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException {
        directOrderFacade.tokenize(cseAdditionalAuthInfo, worldpayAdditionalInfoData);
    }

    @Override
    public DirectResponseData
    executeFirstPaymentAuthorisation3DSecure(final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {
        return directOrderFacade.executeFirstPaymentAuthorisation3DSecure(cseAdditionalAuthInfo, worldpayAdditionalInfoData);
    }
}
