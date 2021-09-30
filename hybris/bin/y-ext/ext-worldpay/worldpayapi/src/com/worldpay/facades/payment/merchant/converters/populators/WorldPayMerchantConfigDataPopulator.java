package com.worldpay.facades.payment.merchant.converters.populators;

import com.worldpay.config.merchant.ApplePayConfigData;
import com.worldpay.config.merchant.GooglePayConfigData;
import com.worldpay.config.merchant.ThreeDSFlexJsonWebTokenCredentials;
import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.enums.*;
import com.worldpay.model.WorldpayApplePayConfigurationModel;
import com.worldpay.model.WorldpayGooglePayConfigurationModel;
import com.worldpay.model.WorldpayMerchantConfigurationModel;
import com.worldpay.model.WorldpayThreeDS2JsonWebTokenConfigurationModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Populates the ApplePaySettingsData from the CheckoutComApplePayConfigurationModel
 */
public class WorldPayMerchantConfigDataPopulator implements Populator<WorldpayMerchantConfigurationModel, WorldpayMerchantConfigData> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final WorldpayMerchantConfigurationModel source, final WorldpayMerchantConfigData target) throws ConversionException {
        Assert.notNull(source, "Source WorldpayMerchantConfiguration cannot be null.");
        Assert.notNull(target, "Target WorldpayMerchantConfigData cannot be null.");

        target.setCode(source.getCode());
        target.setPassword(source.getPassword());
        target.setMacValidation(source.getMacValidation());
        target.setMacSecret(source.getMacSecret());
        target.setCsePublicKey(source.getCsePublicKey());
        target.setInstallationId(source.getInstallationId());
        target.setStatementNarrative(source.getStatementNarrative());
        target.setOrderContent(source.getOrderContent());

        Optional.ofNullable(source.getApplePayConfiguration())
            .map(this::createApplePayConfigData)
            .ifPresent(target::setApplePaySettings);

        Optional.ofNullable(source.getGooglePayConfiguration())
            .map(this::createGooglePayConfigData)
            .ifPresent(target::setGooglePaySettings);

        Optional.ofNullable(source.getThreeDSFlexJsonWebTokenSettings())
            .map(this::createThreeDSFlexJsonWebTokenSettings)
            .ifPresent(target::setThreeDSFlexJsonWebTokenSettings);

        Optional.ofNullable(source.getThreeDSFlexChallengePreference())
            .map(ChallengePreference::getCode)
            .ifPresent(target::setThreeDSFlexChallengePreference);
    }

    private ApplePayConfigData createApplePayConfigData(final WorldpayApplePayConfigurationModel applePayConfiguration) {
        final ApplePayConfigData applePaySettings = new ApplePayConfigData();
        applePaySettings.setMerchantId(applePayConfiguration.getMerchantId());
        applePaySettings.setMerchantName(applePayConfiguration.getMerchantName());
        applePaySettings.setCountryCode(applePayConfiguration.getCountryCode());

        if (CollectionUtils.isNotEmpty(applePayConfiguration.getMerchantCapabilities())) {
            applePaySettings.setMerchantCapabilities(applePayConfiguration.getMerchantCapabilities()
                .stream()
                .map(ApplePayMerchantCapabilities::getCode)
                .collect(Collectors.toList()));
        }

        if (CollectionUtils.isNotEmpty(applePayConfiguration.getSupportedNetworks())) {
            applePaySettings.setSupportedNetworks(applePayConfiguration.getSupportedNetworks()
                .stream()
                .map(ApplePaySupportedNetworks::getCode)
                .collect(Collectors.toList()));

        }
        return applePaySettings;
    }

    private GooglePayConfigData createGooglePayConfigData(final WorldpayGooglePayConfigurationModel googlePayConfiguration) {
        final GooglePayConfigData googlePayConfigData = new GooglePayConfigData();
        googlePayConfigData.setMerchantId(googlePayConfiguration.getMerchantId());
        googlePayConfigData.setMerchantName(googlePayConfiguration.getMerchantName());
        googlePayConfigData.setEnvironment(googlePayConfiguration.getEnvironment().getCode());
        googlePayConfigData.setGatewayMerchantId(googlePayConfiguration.getGatewayMerchantId());
        googlePayConfigData.setCardType(googlePayConfiguration.getCardType());

        if (CollectionUtils.isNotEmpty(googlePayConfiguration.getAllowedCardNetworks())) {
            googlePayConfigData.setAllowedCardNetworks(googlePayConfiguration.getAllowedCardNetworks()
                .stream()
                .map(GooglePayCardNetworks::getCode)
                .collect(Collectors.toList()));
        }

        if (CollectionUtils.isNotEmpty(googlePayConfiguration.getAllowedAuthMethods())) {
            googlePayConfigData.setAllowedAuthMethods(googlePayConfiguration.getAllowedAuthMethods()
                .stream()
                .map(GooglePayCardAuthMethods::getCode)
                .collect(Collectors.toList()));

        }
        return googlePayConfigData;
    }

    private ThreeDSFlexJsonWebTokenCredentials createThreeDSFlexJsonWebTokenSettings(final WorldpayThreeDS2JsonWebTokenConfigurationModel threeDS2JsonWebTokenConfiguration) {
        final ThreeDSFlexJsonWebTokenCredentials threeDSFlexJsonWebTokenCredentials = new ThreeDSFlexJsonWebTokenCredentials();
        threeDSFlexJsonWebTokenCredentials.setIss(threeDS2JsonWebTokenConfiguration.getIss());
        threeDSFlexJsonWebTokenCredentials.setJwtMacKey(threeDS2JsonWebTokenConfiguration.getJwtMacKey());
        threeDSFlexJsonWebTokenCredentials.setAlg(threeDS2JsonWebTokenConfiguration.getAlg());
        threeDSFlexJsonWebTokenCredentials.setOrgUnitId(threeDS2JsonWebTokenConfiguration.getOrgUnitId());
        threeDSFlexJsonWebTokenCredentials.setEventOriginDomain(threeDS2JsonWebTokenConfiguration.getEventOriginDomain());
        threeDSFlexJsonWebTokenCredentials.setChallengeUrl(threeDS2JsonWebTokenConfiguration.getChallengeUrl());
        threeDSFlexJsonWebTokenCredentials.setDdcUrl(threeDS2JsonWebTokenConfiguration.getDdcUrl());
        return threeDSFlexJsonWebTokenCredentials;
    }
}
