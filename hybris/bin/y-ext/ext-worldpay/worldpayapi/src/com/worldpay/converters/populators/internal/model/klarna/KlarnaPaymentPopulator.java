package com.worldpay.converters.populators.internal.model.klarna;

import com.worldpay.internal.model.KLARNASSL;
import com.worldpay.internal.model.MerchantUrls;
import com.worldpay.data.klarna.KlarnaPayment;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link KLARNASSL} with the information of a {@link KlarnaPayment}
 */
public class KlarnaPaymentPopulator implements Populator<KlarnaPayment, KLARNASSL> {

    /**
     * Populates the data from the {@link KlarnaPayment} to a {@link KLARNASSL}
     *
     * @param source a {@link KlarnaPayment} from Worldpay
     * @param target a {@link KLARNASSL} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final KlarnaPayment source, final KLARNASSL target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target must not be null!");

        target.setExtraMerchantData(source.getExtraMerchantData());
        target.setPurchaseCountry(source.getPurchaseCountry());
        target.setShopperLocale(source.getShopperLocale());

        Optional.ofNullable(source.getMerchantUrls()).ifPresent(klarnaMerchantUrls -> {
            final MerchantUrls internalMerchantUrls = new MerchantUrls();
            internalMerchantUrls.setCheckoutURL(klarnaMerchantUrls.getCheckoutURL());
            internalMerchantUrls.setConfirmationURL(klarnaMerchantUrls.getConfirmationURL());
            target.setMerchantUrls(internalMerchantUrls);
        });
    }
}
