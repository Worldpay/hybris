package com.worldpay.attributehandlers;

import com.worldpay.constants.Worldpayb2cdemoConstants;
import com.worldpay.model.WorldpayBinRangeModel;
import com.worldpay.service.WorldpayBinRangeService;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

/**
 * Used to handle WorldpayBinRange dynamic attribute on PaymentInfoModel
 */
public class CreditCardPaymentInfoWorldpayBinRangeHandler implements DynamicAttributeHandler<WorldpayBinRangeModel, CreditCardPaymentInfoModel> {

    protected final WorldpayBinRangeService worldpayBinRangeService;
    protected final ConfigurationService configurationService;

    public CreditCardPaymentInfoWorldpayBinRangeHandler(final WorldpayBinRangeService worldpayBinRangeService, final ConfigurationService configurationService) {
        this.worldpayBinRangeService = worldpayBinRangeService;
        this.configurationService = configurationService;
    }

    /**
     * Get bin range based on creditcard number - until masking begins - from payment info.
     * @param paymentInfo
     * @return WorldpayBinRangeModel
     */
    @Override
    public WorldpayBinRangeModel get(final CreditCardPaymentInfoModel paymentInfo) {
        final String maskedCCNumber = paymentInfo.getNumber();
        final int indexMaskStart = maskedCCNumber.indexOf('*');

        if (indexMaskStart < 0) {
            return worldpayBinRangeService.getBinRange(maskedCCNumber.substring(0,
                    configurationService.getConfiguration().getInt(Worldpayb2cdemoConstants.BIN_RANGE_CARD_SIZE_PROPERTY)));
        }

        return worldpayBinRangeService.getBinRange(maskedCCNumber.substring(0, indexMaskStart));
    }

    /**
     * Method not implemented. Throws UnsupportedOperationException.
     * @param model
     * @param worldpayBinRangeModel
     */
    @Override
    public void set(final CreditCardPaymentInfoModel model, final WorldpayBinRangeModel worldpayBinRangeModel) {
        throw new UnsupportedOperationException();
    }
}
