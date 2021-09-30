package com.worldpay.converters.populators.internal.model.payment;

import com.worldpay.internal.model.CSEDATA;
import com.worldpay.internal.model.CardAddress;
import com.worldpay.internal.model.EncryptedData;
import com.worldpay.data.Address;
import com.worldpay.data.payment.Cse;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link CSEDATA} with the information of a {@link Cse}
 */
public class CsePaymentPopulator implements Populator<Cse, CSEDATA> {

    protected final Converter<Address, com.worldpay.internal.model.Address> internalAddressConverter;

    public CsePaymentPopulator(final Converter<Address, com.worldpay.internal.model.Address> internalAddressConverter) {
        this.internalAddressConverter = internalAddressConverter;
    }

    /**
     * Populates the data from the {@link Cse} to a {@link CSEDATA}
     *
     * @param source a {@link Cse} from Worldpay
     * @param target a {@link CSEDATA} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final Cse source, final CSEDATA target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        Optional.ofNullable(source.getAddress()).ifPresent(address -> {
            final CardAddress cardAddress = new CardAddress();
            cardAddress.setAddress(internalAddressConverter.convert(address));
            target.setCardAddress(cardAddress);
        });

        Optional.ofNullable(source.getEncryptedData()).ifPresent(encryptedData -> {
            final EncryptedData internalEncryptedData = new EncryptedData();
            internalEncryptedData.setvalue(encryptedData);
            target.setEncryptedData(internalEncryptedData);
        });
    }
}
