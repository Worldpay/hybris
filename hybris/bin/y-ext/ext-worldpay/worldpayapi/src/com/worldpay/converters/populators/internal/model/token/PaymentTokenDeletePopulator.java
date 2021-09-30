package com.worldpay.converters.populators.internal.model.token;

import com.worldpay.internal.model.*;
import com.worldpay.data.token.DeleteTokenRequest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link PaymentTokenDelete} with the information of a {@link DeleteTokenRequest}.
 */
public class PaymentTokenDeletePopulator implements Populator<DeleteTokenRequest, PaymentTokenDelete> {

    private static final String MERCHANT = "merchant";

    /**
     * Populates the data from the {@link DeleteTokenRequest} to a {@link PaymentTokenDelete}
     *
     * @param source a {@link DeleteTokenRequest} from Worldpay
     * @param target a {@link TransactionRiskData} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final DeleteTokenRequest source, final PaymentTokenDelete target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        if (source.getTokenRequest().isMerchantToken()) {
            target.setTokenScope(MERCHANT);
        }

        Optional.ofNullable(source.getAuthenticatedShopperId()).ifPresent(shopperId -> {
            final AuthenticatedShopperID internalAuthenticatedShopperID = new AuthenticatedShopperID();
            internalAuthenticatedShopperID.setvalue(shopperId);
            target.setAuthenticatedShopperID(internalAuthenticatedShopperID);
        });

        Optional.ofNullable(source.getPaymentTokenId()).ifPresent(paymentTokenId -> {
            final PaymentTokenID internalPaymentTokenIDWrapper = new PaymentTokenID();
            internalPaymentTokenIDWrapper.setvalue(paymentTokenId);
            target.setPaymentTokenID(internalPaymentTokenIDWrapper);
        });

        Optional.ofNullable(source.getTokenRequest()).ifPresent(tokenRequest -> {
            target.setTokenEventReference(tokenRequest.getTokenEventReference());
            final TokenReason internalTokenReason = new TokenReason();
            internalTokenReason.setvalue(tokenRequest.getTokenReason());
            target.setTokenReason(internalTokenReason);
        });
    }
}
