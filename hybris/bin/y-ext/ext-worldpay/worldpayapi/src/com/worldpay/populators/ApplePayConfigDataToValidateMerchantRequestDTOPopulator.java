package com.worldpay.populators;

import com.worldpay.config.merchant.ApplePayConfigData;
import com.worldpay.payment.applepay.ValidateMerchantRequestDTO;
import com.worldpay.service.WorldpayUrlService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.net.MalformedURLException;
import java.net.URL;

public class ApplePayConfigDataToValidateMerchantRequestDTOPopulator implements Populator<ApplePayConfigData, ValidateMerchantRequestDTO> {
    private static final String WEB = "web";
    private static final Logger LOG = LoggerFactory.getLogger(ApplePayConfigDataToValidateMerchantRequestDTOPopulator.class);

    private WorldpayUrlService worldpayUrlService;

    @Override
    public void populate(ApplePayConfigData applePayConfigData, ValidateMerchantRequestDTO validateMerchantRequestDTO) throws ConversionException {
        validateMerchantRequestDTO.setMerchantIdentifier(applePayConfigData.getMerchantId());
        validateMerchantRequestDTO.setDisplayName(applePayConfigData.getMerchantName());
        validateMerchantRequestDTO.setInitiative(WEB);
        final String currentWebSiteURL = worldpayUrlService.getWebsiteUrlForCurrentSite();
        try {
            final URL url = new URL(currentWebSiteURL);
            validateMerchantRequestDTO.setInitiativeContext(url.getHost());
        } catch (MalformedURLException e) {
            LOG.error("Malformed URL {}", currentWebSiteURL, e);
        }

    }

    @Required
    public void setWorldpayUrlService(WorldpayUrlService worldpayUrlService) {
        this.worldpayUrlService = worldpayUrlService;
    }

}
