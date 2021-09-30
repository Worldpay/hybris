package com.worldpay.converters.populators.internal.model.token;

import com.worldpay.internal.model.*;
import com.worldpay.data.token.CardDetails;
import com.worldpay.data.token.TokenRequest;
import com.worldpay.data.token.UpdateTokenRequest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.PaymentTokenUpdate} with the information of a {@link UpdateTokenRequest}.
 */
public class PaymentTokenUpdatePopulator implements Populator<UpdateTokenRequest, PaymentTokenUpdate> {

    private static final String MERCHANT = "merchant";

    protected final Converter<CardDetails, com.worldpay.internal.model.CardDetails> internalCardDetailsConverter;

    public PaymentTokenUpdatePopulator(final Converter<CardDetails, com.worldpay.internal.model.CardDetails> internalCardDetailsConverter) {
        this.internalCardDetailsConverter = internalCardDetailsConverter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final UpdateTokenRequest source,
                         final PaymentTokenUpdate target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        if (source.isMerchantToken()) {
            target.setTokenScope(MERCHANT);
            target.setAuthenticatedShopperID(null);
        } else {
            Optional.ofNullable(source.getAuthenticatedShopperID())
                .ifPresent(shopperId -> {
                    final AuthenticatedShopperID intAuthenticatedShopperID = new AuthenticatedShopperID();
                    intAuthenticatedShopperID.setvalue(shopperId);
                    target.setAuthenticatedShopperID(intAuthenticatedShopperID);
                });
        }

        final PaymentTokenID intPaymentTokenIDWrapper = new PaymentTokenID();
        intPaymentTokenIDWrapper.setvalue(source.getPaymentTokenId());
        target.setPaymentTokenID(intPaymentTokenIDWrapper);

        Optional.ofNullable(source.getTokenRequest())
            .map(TokenRequest::getTokenEventReference)
            .ifPresent(target::setTokenEventReference);

        Optional.ofNullable(source.getCardDetails())
            .map(internalCardDetailsConverter::convert)
            .ifPresent(intCardDetails -> {
                final PaymentInstrument intPaymentInstrument = new PaymentInstrument();
                intPaymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSL()
                    .add(intCardDetails);
                target.setPaymentInstrument(intPaymentInstrument);
            });

        Optional.ofNullable(source.getTokenRequest())
            .map(TokenRequest::getTokenReason)
            .ifPresent(tokenReason -> {
                final TokenReason intTokenReason = new TokenReason();
                intTokenReason.setvalue(tokenReason);
                target.setTokenReason(intTokenReason);
            });
    }
}
