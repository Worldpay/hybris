package com.worldpay.converters.populators.internal.model.payment;

import com.worldpay.data.Address;
import com.worldpay.data.payment.AchDirectDebitPayment;
import com.worldpay.internal.model.ACHDIRECTDEBITSSL;
import com.worldpay.internal.model.BillingAddress;
import com.worldpay.internal.model.EcheckSale;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link ACHDIRECTDEBITSSL} with the information of a {@link AchDirectDebitPayment}.
 */
public class ACHDirectDebitPaymentPopulator implements Populator<AchDirectDebitPayment, ACHDIRECTDEBITSSL> {

    protected final Converter<Address, com.worldpay.internal.model.Address> internalAddressConverter;

    public ACHDirectDebitPaymentPopulator(final Converter<Address, com.worldpay.internal.model.Address> internalAddressConverter) {
        this.internalAddressConverter = internalAddressConverter;
    }

        /**
         * {@inheritDoc}
         */
    @Override
    public void populate(final AchDirectDebitPayment source, final ACHDIRECTDEBITSSL target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        final EcheckSale echeckSale = new EcheckSale();

        Optional.ofNullable(source.getAccountType())
            .ifPresent(accountType -> echeckSale.setBankAccountType(accountType.getCode()));
        Optional.ofNullable(source.getAccountNumber())
                .ifPresent(echeckSale::setAccountNumber);
        Optional.ofNullable(source.getRoutingNumber())
                .ifPresent(echeckSale::setRoutingNumber);
        Optional.ofNullable(source.getCheckNumber())
                .ifPresent(echeckSale::setCheckNumber);
        Optional.ofNullable(source.getCompanyName())
                .ifPresent(echeckSale::setCompanyName);
        Optional.ofNullable(source.getCustomIdentifier())
                .ifPresent(echeckSale::setCustomIdentifier);

        Optional.ofNullable(source.getAddress())
                .map(internalAddressConverter::convert)
                .ifPresent(address -> {
                    final BillingAddress billingAddress = new BillingAddress();
                    billingAddress.setAddress(address);
                    echeckSale.setBillingAddress(billingAddress);
                });

        target.getEcheckSaleOrEcheckVerification().add(echeckSale);

    }
}
