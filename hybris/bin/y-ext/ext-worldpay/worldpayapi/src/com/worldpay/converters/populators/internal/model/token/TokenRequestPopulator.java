package com.worldpay.converters.populators.internal.model.token;

import com.worldpay.internal.model.CreateToken;
import com.worldpay.internal.model.TokenReason;
import com.worldpay.data.token.TokenRequest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.CreateToken} with the information of a {@link TokenRequest}.
 */
public class TokenRequestPopulator implements Populator<TokenRequest, CreateToken> {

    private static final String MERCHANT = "merchant";
    private static final String SHOPPER = "shopper";

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final TokenRequest source, final CreateToken target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        Optional.ofNullable(source.getTokenReason()).ifPresent(token -> {
            final TokenReason intTokenReason = new TokenReason();
            intTokenReason.setvalue(token);
            target.setTokenReason(intTokenReason);
        });

        target.setTokenEventReference(source.getTokenEventReference());

        target.setTokenScope(source.isMerchantToken() ? MERCHANT : SHOPPER);
    }
}
