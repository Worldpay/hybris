package com.worldpay.converters;

import com.worldpay.service.model.Amount;
import com.worldpay.service.model.ErrorDetail;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.response.ServiceResponse;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.payment.commands.result.AbstractResult;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

import static de.hybris.platform.payment.dto.TransactionStatusDetails.*;

/**
 * Abstract converter to be used to transform a {@link ServiceResponse} from Worldpay to an {@link AbstractResult} in hybris in a payment transactions.
 *
 * @param <S> object that extends ServiceResponse
 * @param <T> object that extends AbstractResult
 */
public abstract class WorldpayAbstractServiceResponseConverter<S extends ServiceResponse, T extends AbstractResult> extends AbstractConverter<S, T> {

    private WorldpayOrderService worldpayOrderService;

    /**
     * Returns a BigDecimal with the proper value after applying to the value returned from Worldpay the fraction digits from the {@link Currency} used.
     *
     * @param amount the {@link Amount} received from Worldpay
     * @return a BigDecimal
     */
    protected BigDecimal getTotalAmount(final Amount amount) {
        return worldpayOrderService.convertAmount(amount);
    }

    @Required
    public void setWorldpayOrderService(final WorldpayOrderService worldpayOrderService) {
        this.worldpayOrderService = worldpayOrderService;
    }

    /**
     * Maps the worldpay error to a {@link TransactionStatusDetails}
     *
     * @param error the error object
     * @return the corresponding status
     */
    protected TransactionStatusDetails getTransactionStatusDetails(final ErrorDetail error) {

        if (Objects.nonNull(error) && StringUtils.isNotBlank(error.getCode())) {
            switch (error.getCode()) {
                case "1":
                    return ERROR_INTERNAL;
                case "2":
                    return ERROR_PARSE;
                case "3":
                    return ERROR_ORDER_AMOUNT;
                case "4":
                    return ERROR_SECURITY;
                case "5":
                    return ERROR_INVALID_REQUEST;
                case "6":
                    return ERROR_INVALID_CONTENT;
                case "7":
                    return ERROR_PAYMENT_DETAILS;
                case "8":
                    return ERROR_NOT_AVAILABLE;
                case "9":
                    return ERROR_IDEMPOTENCY_SERVICE;
                case "10":
                    return ERROR_PRIME_ROUTING;
                case "11":
                    return ERROR_L2_L3_DATA;
                case "12":
                    return ERROR_LODGING_DATA;
                case "13":
                    return ERROR_SPLIT_AUTH;
                default:
                    return UNKNOWN_CODE;
            }
        } else return UNKNOWN_CODE;
    }
}
