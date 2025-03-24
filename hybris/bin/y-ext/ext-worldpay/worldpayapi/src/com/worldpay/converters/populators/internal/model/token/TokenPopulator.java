package com.worldpay.converters.populators.internal.model.token;

import com.worldpay.internal.model.PaymentInstrument;
import com.worldpay.internal.model.PaymentTokenID;
import com.worldpay.internal.model.TOKENSSL;
import com.worldpay.data.token.CardDetails;
import com.worldpay.data.token.Token;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;
import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.TOKENSSL} with the information of a {@link Token}.
 */
public class TokenPopulator implements Populator<Token, TOKENSSL> {

    private static final String MERCHANT = "merchant";
    private static final String SHOPPER = "shopper";

    protected final Converter<CardDetails, com.worldpay.internal.model.CardDetails> internalCardDetailsConverter;

    public TokenPopulator(final Converter<CardDetails, com.worldpay.internal.model.CardDetails> internalCardDetailsConverter) {
        this.internalCardDetailsConverter = internalCardDetailsConverter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final Token source, final TOKENSSL target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        target.setTokenScope(source.isMerchantToken() ? MERCHANT : SHOPPER);

        final List<Object> tokenElements = target.getPaymentTokenIDOrPaymentInstrumentOrCvcOrSession();
        Optional.ofNullable(source.getPaymentTokenID()).ifPresent(tokenId -> {
            final PaymentTokenID paymentTokenIDElement = new PaymentTokenID();
            paymentTokenIDElement.setvalue(tokenId);
            tokenElements.add(paymentTokenIDElement);
        });

        Optional.ofNullable(source.getPaymentInstrument())
            .map(internalCardDetailsConverter::convert)
            .ifPresent(cardDetails -> {
                final PaymentInstrument intPaymentInstrument = new PaymentInstrument();
                intPaymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetailsOrSAMSUNGPAYSSLOrPAYWITHGOOGLESSLOrAPPLEPAYSSLOrEMVCOTOKENSSLOrObdetailsOrAccountHolder().add(cardDetails);
                tokenElements.add(intPaymentInstrument);
            });
    }
}
