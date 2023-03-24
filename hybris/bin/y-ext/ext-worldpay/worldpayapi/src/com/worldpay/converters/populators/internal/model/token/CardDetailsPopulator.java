package com.worldpay.converters.populators.internal.model.token;

import com.worldpay.data.token.CardDetails;
import com.worldpay.factories.CardBrandFactory;
import com.worldpay.internal.model.*;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.CardDetails} with the information of a {@link CardDetails}.
 */
public class CardDetailsPopulator implements Populator<CardDetails, com.worldpay.internal.model.CardDetails> {

    protected final Converter<com.worldpay.data.Address, Address> internalAddressConverter;
    protected final Converter<com.worldpay.data.Date, Date> internalDateConverter;
    protected final CardBrandFactory cardBrandFactory;

    public CardDetailsPopulator(final Converter<com.worldpay.data.Address, com.worldpay.internal.model.Address> internalAddressConverter,
                                final Converter<com.worldpay.data.Date, com.worldpay.internal.model.Date> internalDateConverter,
                                final CardBrandFactory cardBrandFactory) {
        this.internalAddressConverter = internalAddressConverter;
        this.internalDateConverter = internalDateConverter;
        this.cardBrandFactory = cardBrandFactory;
    }

    /**
     * Populates the data from the {@link CardDetails} to a {@link com.worldpay.internal.model.CardDetails}
     *
     * @param source a {@link CardDetails} from Worldpay
     * @param target a {@link com.worldpay.internal.model.TransactionRiskData} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final CardDetails source, final com.worldpay.internal.model.CardDetails target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        Optional.ofNullable(source.getCvcNumber()).ifPresent(cvcNumber -> {
            final Cvc internalCvc = new Cvc();
            internalCvc.setvalue(cvcNumber);
            target.setCvc(internalCvc);
        });

        Optional.ofNullable(source.getCardAddress()).ifPresent(cardAddress -> {
            final CardAddress internalCardAddress = new CardAddress();
            internalCardAddress.setAddress(internalAddressConverter.convert(cardAddress));
            target.setCardAddress(internalCardAddress);
        });

        Optional.ofNullable(source.getCardHolderName()).ifPresent(cardHolderName -> {
            final CardHolderName internalCardHolderName = new CardHolderName();
            internalCardHolderName.setvalue(cardHolderName);
            target.setCardHolderName(internalCardHolderName);
        });

        Optional.ofNullable(source.getExpiryDate()).ifPresent(expiryDate -> {
            final ExpiryDate internalExpiryDate = new ExpiryDate();
            internalExpiryDate.setDate(internalDateConverter.convert(expiryDate));
            target.setExpiryDate(internalExpiryDate);
        });

        Optional.ofNullable(source.getCardNumber()).ifPresent(cardNumber -> {
            final Derived internalDerived = new Derived();
            internalDerived.setObfuscatedPAN(cardNumber);
            internalDerived.setIssuerCountryCode(source.getIssuerCountryCode());
            internalDerived.setCardSubBrand(source.getCardSubBrand());
            internalDerived.setCardBrand(cardBrandFactory.createCardBrandWithValue(source.getCardBrand()));
            target.setDerived(internalDerived);
        });
    }
}
