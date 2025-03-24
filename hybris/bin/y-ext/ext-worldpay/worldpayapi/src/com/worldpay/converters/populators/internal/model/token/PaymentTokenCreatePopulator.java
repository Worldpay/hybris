package com.worldpay.converters.populators.internal.model.token;

import com.worldpay.converters.internal.model.payment.PaymentConverterStrategy;
import com.worldpay.internal.model.AuthenticatedShopperID;
import com.worldpay.internal.model.CreateToken;
import com.worldpay.internal.model.PaymentTokenCreate;
import com.worldpay.internal.model.TransactionRiskData;
import com.worldpay.data.payment.StoredCredentials;
import com.worldpay.data.token.CardTokenRequest;
import com.worldpay.data.token.TokenRequest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link PaymentTokenCreate} with the information of a {@link CardTokenRequest}.
 */
public class PaymentTokenCreatePopulator implements Populator<CardTokenRequest, PaymentTokenCreate> {

    protected final Converter<TokenRequest, CreateToken> internalTokenRequestConverter;
    protected final Converter<StoredCredentials, com.worldpay.internal.model.StoredCredentials> internalStoredCredentialsConverter;
    protected final PaymentConverterStrategy internalPaymentConverterStrategy;

    public PaymentTokenCreatePopulator(final Converter<TokenRequest, CreateToken> internalTokenRequestConverter,
                                       final Converter<StoredCredentials, com.worldpay.internal.model.StoredCredentials> internalStoredCredentialsConverter,
                                       final PaymentConverterStrategy internalPaymentConverterStrategy) {
        this.internalTokenRequestConverter = internalTokenRequestConverter;
        this.internalStoredCredentialsConverter = internalStoredCredentialsConverter;
        this.internalPaymentConverterStrategy = internalPaymentConverterStrategy;
    }

    /**
     * Populates the data from the {@link CardTokenRequest} to a {@link PaymentTokenCreate}
     *
     * @param source a {@link CardTokenRequest} from Worldpay
     * @param target a {@link TransactionRiskData} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final CardTokenRequest source, final PaymentTokenCreate target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        Optional.ofNullable(source.getAuthenticatedShopperId()).ifPresent(shopperId -> {
            final AuthenticatedShopperID internalAuthenticatedShopperID = new AuthenticatedShopperID();
            internalAuthenticatedShopperID.setvalue(shopperId);
            target.setAuthenticatedShopperID(internalAuthenticatedShopperID);
        });

        Optional.ofNullable(source.getTokenRequest())
            .map(internalTokenRequestConverter::convert)
            .ifPresent(target::setCreateToken);

        Optional.ofNullable(source.getStoredCredentials())
            .map(internalStoredCredentialsConverter::convert)
            .ifPresent(target::setStoredCredentials);

        Optional.ofNullable(source.getPayment())
            .map(internalPaymentConverterStrategy::convertPayment)
            .ifPresent(intPayment -> target.getPaymentInstrumentOrCSEDATAOrPosPaymentInstrument().add(intPayment));
    }
}
