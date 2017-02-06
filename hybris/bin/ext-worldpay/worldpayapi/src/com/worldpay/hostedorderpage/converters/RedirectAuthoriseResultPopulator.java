package com.worldpay.hostedorderpage.converters;

import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.service.model.Amount;
import com.worldpay.service.payment.WorldpayOrderService;
import de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.response.AbstractResultPopulator;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Currency;
import java.util.Map;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


/**
 * Implementation of {@link AbstractResultPopulator} to convert a map of String objects to a
 * {@link RedirectAuthoriseResult}
 */
public class RedirectAuthoriseResultPopulator implements Populator<Map<String, String>, RedirectAuthoriseResult> {

    private static Logger LOG = Logger.getLogger(RedirectAuthoriseResultPopulator.class);

    public static final String PAYMENT_STATUS = "paymentStatus";
    public static final String STATUS = "status";
    public static final String SAVE_PAYMENT_INFO = "savePaymentInfo";
    public static final String PAYMENT_AMOUNT = "paymentAmount";
    public static final String PAYMENT_CURRENCY = "paymentCurrency";

    private WorldpayOrderService worldpayOrderService;

    /**
     * Populates the {@link RedirectAuthoriseResult} with the values received in the URL from Worldpay.
     * @param source the source object
     * @param target the target to fill
     * @throws ConversionException
     */
    @Override
    public void populate(final Map<String, String> source, final RedirectAuthoriseResult target) throws ConversionException {
        // Validate parameters
        validateParameterNotNull(source, "Parameter source (Map<String, String>) cannot be null");
        validateParameterNotNull(target, "Parameter target (RedirectAuthoriseResult) cannot be null");

        // Example of key: MERCHANT_OWNER^MERCHANTCODE^00002000-1431098192721

        final String orderKey = source.get("orderKey");
        target.setOrderKey(orderKey);
        setOrderCode(target, orderKey);
        target.setPending(false);
        //in case of Credit Card payment - extract paymentStatus, APM payment - extract status parameter
        target.setPaymentStatus(source.get(PAYMENT_STATUS) != null ? source.get(PAYMENT_STATUS) : source.get(STATUS));
        target.setSaveCard(Boolean.valueOf(source.get(SAVE_PAYMENT_INFO)));
        target.setPaymentAmount(getPaymentAmount(source));

    }

    protected void setOrderCode(final RedirectAuthoriseResult target, final String orderKey) {
        if (orderKey != null) {
            final String[] orderKeyParts = orderKey.split("\\^");
            if (orderKeyParts.length >= 3) {
                target.setOrderCode(orderKeyParts[2]);
            }
        }
    }

    private BigDecimal getPaymentAmount (final Map<String, String> source) {
        if (StringUtils.isNotEmpty(source.get(PAYMENT_AMOUNT)) && StringUtils.isNotEmpty(source.get(PAYMENT_CURRENCY))) {
            try {
                final Currency currency = Currency.getInstance(source.get(PAYMENT_CURRENCY));
                Amount amount = worldpayOrderService.createAmount(currency, Integer.valueOf(source.get(PAYMENT_AMOUNT)));
                return (new BigDecimal(amount.getValue()));
            } catch (IllegalArgumentException exception) {
                LOG.error(MessageFormat.format("Received invalid currecny isocode: {0}", source.get(PAYMENT_CURRENCY)), exception);
                return null;
            }
        }
        return null;
    }

    public void setWorldpayOrderService(WorldpayOrderService worldpayOrderService) {
        this.worldpayOrderService = worldpayOrderService;
    }
}
