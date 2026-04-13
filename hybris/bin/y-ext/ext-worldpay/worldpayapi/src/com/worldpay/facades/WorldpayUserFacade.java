package com.worldpay.facades;

import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.UserFacade;

import java.util.List;

public interface WorldpayUserFacade extends UserFacade {

    /**
     * Returns the current user's Credit Card and Alternative Payment Methods Payment Infos filtered by delivery address.
     * For simplification reasons, alternative Payment Methods are also converted to Credit Card Payment Info Data
     *
     * @param saved <code>true</code> to retrieve only saved credit card payment infos
     * @return list of Credit Card Payment Info Data
     */
    List<CCPaymentInfoData> getAvailableCCPaymentInfos(final boolean saved);
}
