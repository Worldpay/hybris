package com.worldpay.service.payment.impl;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.data.*;
import com.worldpay.model.WorldpayFraudSightModel;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.payment.WorldpayAdditionalDataRequestStrategy;
import com.worldpay.service.payment.WorldpayFraudSightStrategy;
import com.worldpay.service.request.AuthoriseRequestParameters;
import com.worldpay.util.WorldpayInternalModelTransformerUtil;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import org.apache.commons.lang3.NotImplementedException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Default implementation of {@link WorldpayFraudSightStrategy}.
 * Extend this strategy if you want customize the Fraud Sight Data payload
 */
public class DefaultWorldpayFraudSightStrategy extends AbstractWorldpayFraudSightStrategy implements WorldpayAdditionalDataRequestStrategy {

    protected final WorldpayCartService worldpayCartService;
    protected final Converter<AddressModel, Address> worldpayAddressConverter;
    protected final BaseSiteService baseSiteService;
    protected final Converter<FraudSightResponse, WorldpayFraudSightModel> worldpayFraudSightResponseConverter;
    protected final ModelService modelService;

    public DefaultWorldpayFraudSightStrategy(final WorldpayCartService worldpayCartService,
                                             final Converter<AddressModel, Address> worldpayAddressConverter,
                                             final BaseSiteService baseSiteService,
                                             final Converter<FraudSightResponse, WorldpayFraudSightModel> worldpayFraudSightResponseConverter,
                                             final ModelService modelService) {
        this.worldpayCartService = worldpayCartService;
        this.worldpayAddressConverter = worldpayAddressConverter;
        this.baseSiteService = baseSiteService;
        this.worldpayFraudSightResponseConverter = worldpayFraudSightResponseConverter;
        this.modelService = modelService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateRequestWithAdditionalData(final AbstractOrderModel cart,
                                                  final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                  final AuthoriseRequestParameters.AuthoriseRequestParametersCreator authoriseRequestParametersCreator) {
        if (isFraudSightEnabled()) {

            final FraudSightData fraudSightData = createFraudSightData(cart, worldpayAdditionalInfoData);
            authoriseRequestParametersCreator.withFraudSightData(fraudSightData);

            if (isNotBlank(worldpayAdditionalInfoData.getDeviceSession())) {
                authoriseRequestParametersCreator.withDeviceSession(worldpayAdditionalInfoData.getDeviceSession());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFraudSightEnabled() {
        return baseSiteService.getCurrentBaseSite().getEnableFS();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFraudSightEnabled(final BaseSiteModel baseSite) {
        return baseSite.getEnableFS();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FraudSightData createFraudSightData(final AbstractOrderModel abstractOrder, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {
        validateParameterNotNull(abstractOrder, "The cart is null");

        final ShopperFields shopperFields = createShopperFields(abstractOrder, worldpayAdditionalInfoData);
        final CustomNumericFields customNumericFields = createCustomNumericFields();
        final CustomStringFields customStringFields = createCustomStringFields();

        final FraudSightData fraudSightData = new FraudSightData();
        fraudSightData.setShopperFields(shopperFields);
        fraudSightData.setCustomNumericFields(customNumericFields);
        fraudSightData.setCustomStringFields(customStringFields);

        return fraudSightData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFraudSight(final PaymentTransactionModel paymentTransactionModel, final PaymentReply paymentReply) {
        Optional.ofNullable(paymentReply.getFraudSight())
            .map(worldpayFraudSightResponseConverter::convert)
            .map(fraudSight -> {
                paymentTransactionModel.setFraudSight(fraudSight);
                return paymentTransactionModel;
            })
            .ifPresent(modelService::save);
    }

    /**
     * Creates and populates the ShopperFields
     *
     * @param abstractOrder              the cart
     * @param worldpayAdditionalInfoData the additional data
     * @return the {@link ShopperFields}
     */
    protected ShopperFields createShopperFields(final AbstractOrderModel abstractOrder, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {
        final ShopperFields shopperFields = new ShopperFields();

        validateParameterNotNull(abstractOrder.getUser(), "The customer is null");

        shopperFields.setShopperId(worldpayCartService.getAuthenticatedShopperId(abstractOrder));
        shopperFields.setShopperName(abstractOrder.getUser().getName());

        Optional.ofNullable(abstractOrder.getPaymentAddress())
            .map(worldpayAddressConverter::convert)
            .ifPresent(shopperFields::setShopperAddress);

        Optional.ofNullable(worldpayAdditionalInfoData.getDateOfBirth()).ifPresent(dob -> {
            final LocalDateTime dateOfBirth = Instant.ofEpochMilli(dob.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
            shopperFields.setBirthDate(WorldpayInternalModelTransformerUtil.newDateFromLocalDateTime(dateOfBirth));
        });
        return shopperFields;
    }

    /**
     * Creates and populates the CustomStringFields. Override this method with Real values based on business requirements
     *
     * @return the {@link CustomStringFields}
     */
    protected CustomStringFields createCustomStringFields() {
        throw new NotImplementedException("Implement this method based on business requirements");
    }

    /**
     * Creates and populates the CustomNumericFields. Override this method with Real values based on business requirements
     *
     * @return the {@link CustomNumericFields}
     */
    protected CustomNumericFields createCustomNumericFields() {
        throw new NotImplementedException("Implement this method based on business requirements");
    }
}
