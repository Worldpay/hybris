package com.worldpay.service.hop;

import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.model.MerchantInfo;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.core.model.order.CartModel;

/**
 * Service to generate the necessary data to create the redirect HOP Payment data
 */
public interface WorldpayHOPPService {
    /**
     * Build HOP page data to be sent via POST to worldpay
     *
     * @param cartModel                  The {@link CartModel}
     * @param additionalAuthInfo         The {@link AdditionalAuthInfo}
     * @param merchantInfo               The {@link MerchantInfo}
     * @param worldpayAdditionalInfoData The {@link WorldpayAdditionalInfoData}
     * @return {@link PaymentData} object
     */
    PaymentData buildHOPPageData(CartModel cartModel, AdditionalAuthInfo additionalAuthInfo, MerchantInfo merchantInfo, WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException;
}
