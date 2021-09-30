package com.worldpay.service.payment;

import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.request.AuthoriseRequestParameters.AuthoriseRequestParametersCreator;
import de.hybris.platform.core.model.order.AbstractOrderModel;

/**
 * Strategy to handle the request additional data
 */
public interface WorldpayAdditionalDataRequestStrategy {

    /**
     * Populates the request with additional data based on the strategy
     *
     * @param cart                              the cart
     * @param worldpayAdditionalInfoData        additionalInfoData
     * @param authoriseRequestParametersCreator request parameters
     */
    void populateRequestWithAdditionalData(AbstractOrderModel cart,
                                           WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                           AuthoriseRequestParametersCreator authoriseRequestParametersCreator);
}
