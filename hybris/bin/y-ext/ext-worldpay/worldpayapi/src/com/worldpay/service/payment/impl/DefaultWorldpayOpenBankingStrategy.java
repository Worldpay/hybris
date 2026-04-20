package com.worldpay.service.payment.impl;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.internal.model.OPENBANKINGSSL;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.payment.WorldpayAdditionalDataRequestStrategy;
import com.worldpay.service.request.AuthoriseRequestParameters.AuthoriseRequestParametersCreator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.stream.Stream;

public class DefaultWorldpayOpenBankingStrategy implements WorldpayAdditionalDataRequestStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWorldpayOpenBankingStrategy.class);

    protected final WorldpayMerchantInfoService worldpayMerchantInfoService;

    public DefaultWorldpayOpenBankingStrategy(final WorldpayMerchantInfoService worldpayMerchantInfoService) {
        this.worldpayMerchantInfoService = worldpayMerchantInfoService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateRequestWithAdditionalData(final AbstractOrderModel cart,
                                                  final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                  final AuthoriseRequestParametersCreator authoriseRequestParametersCreator) {

        Stream.ofNullable(authoriseRequestParametersCreator.build().getIncludedPTs())
                .flatMap(Collection::stream)
                .filter(paymentType -> OPENBANKINGSSL.class.equals(paymentType.getModelClass()))
                .findAny()
                .ifPresent(paymentType -> addOpenBankingMerchant(authoriseRequestParametersCreator));

    }

    protected void addOpenBankingMerchant(final AuthoriseRequestParametersCreator authoriseRequestParametersCreator) {
        try {
            authoriseRequestParametersCreator
                    .withMerchantInfo(worldpayMerchantInfoService.getCurrentSiteOpenBankingMerchant());
        } catch (WorldpayConfigurationException e) {
            LOG.error("Error retrieving Open Banking merchant configuration", e);
        }

    }
}
