package com.worldpay.converters.populators;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import com.worldpay.config.merchant.ApplePayConfigData;
import com.worldpay.payment.applepay.ValidateMerchantRequestDTO;
import com.worldpay.service.WorldpayUrlService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ApplePayConfigDataToValidateMerchantRequestDTOPopulator implements Populator<ApplePayConfigData, ValidateMerchantRequestDTO> {
    private static final String WEB = "web";
    private static final Logger LOG = LogManager.getLogger(ApplePayConfigDataToValidateMerchantRequestDTOPopulator.class);

    protected final WorldpayUrlService worldpayUrlService;

    public ApplePayConfigDataToValidateMerchantRequestDTOPopulator(final WorldpayUrlService worldpayUrlService) {
        this.worldpayUrlService = worldpayUrlService;
    }

    @Override
    public void populate(final ApplePayConfigData source, final ValidateMerchantRequestDTO target) throws ConversionException {
        target.setMerchantIdentifier(source.getMerchantId());
        target.setDisplayName(source.getMerchantName());
        target.setInitiative(WEB);
        final String currentWebSiteURL = worldpayUrlService.getWebsiteUrlForCurrentSite();
        try {
            final URL url = URI.create(currentWebSiteURL).toURL();
            target.setInitiativeContext(url.getHost());
        } catch (MalformedURLException | IllegalArgumentException e) {
            LOG.error("Malformed URL {}", currentWebSiteURL, e);
        }
    }

}
