package com.worldpay.service.payment;

import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.request.AuthoriseRequestParameters.AuthoriseRequestParametersCreator;
import de.hybris.platform.core.model.order.AbstractOrderModel;

/**
 * Service to handle additional data for the payment request strategies
 */
public interface WorldpayAdditionalRequestDataService {

    /**
     * Populated the request additional data for direct integrations
     *
     * @param cart                              the cart model
     * @param worldpayAdditionalInfoData
     * @param authoriseRequestParametersCreator
     */
    void populateDirectRequestAdditionalData(AbstractOrderModel cart, WorldpayAdditionalInfoData worldpayAdditionalInfoData, AuthoriseRequestParametersCreator authoriseRequestParametersCreator);

    /**
     * Populated the request additional data for redirect integrations
     *
     * @param cart                              the cart model
     * @param worldpayAdditionalInfoData
     * @param authoriseRequestParametersCreator
     */
    void populateRedirectRequestAdditionalData(AbstractOrderModel cart, WorldpayAdditionalInfoData worldpayAdditionalInfoData, AuthoriseRequestParametersCreator authoriseRequestParametersCreator);
}
