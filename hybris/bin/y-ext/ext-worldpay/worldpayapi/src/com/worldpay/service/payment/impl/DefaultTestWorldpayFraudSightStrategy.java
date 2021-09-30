package com.worldpay.service.payment.impl;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.model.WorldpayFraudSightModel;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.data.*;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;

import java.util.Optional;

/**
 * Extension of {@link DefaultWorldpayFraudSightStrategy} in order to allow the testing of Fraud Sight functionality
 */
public class DefaultTestWorldpayFraudSightStrategy extends DefaultWorldpayFraudSightStrategy {

    public DefaultTestWorldpayFraudSightStrategy(final WorldpayCartService worldpayCartService,
                                                 final Converter<AddressModel, Address> worldpayAddressConverter,
                                                 final BaseSiteService baseSiteService,
                                                 final Converter<FraudSightResponse, WorldpayFraudSightModel> worldpayFraudSightResponseConverter,
                                                 final ModelService modelService) {
        super(worldpayCartService, worldpayAddressConverter, baseSiteService, worldpayFraudSightResponseConverter, modelService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ShopperFields createShopperFields(final AbstractOrderModel abstractOrder, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {
        final ShopperFields shopperFields = callSuperCreateShopperFields(abstractOrder, worldpayAdditionalInfoData);

        // This has been done to perform testing with the address line 3. The billing address ootb does not have
        // any address line 3. So it will be populated from line 2
        Optional.ofNullable(abstractOrder.getPaymentAddress())
            .ifPresent(address -> shopperFields.getShopperAddress().setAddress3(address.getLine2()));

        return shopperFields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CustomStringFields createCustomStringFields() {
        final CustomStringFields customStringFields = new CustomStringFields();
        customStringFields.setCustomStringField1("abc");
        return customStringFields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CustomNumericFields createCustomNumericFields() {
        final CustomNumericFields customNumericFields = new CustomNumericFields();
        customNumericFields.setCustomNumericField1(123);
        return customNumericFields;
    }

    protected ShopperFields callSuperCreateShopperFields(final AbstractOrderModel abstractOrder, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {
        return super.createShopperFields(abstractOrder, worldpayAdditionalInfoData);
    }
}
