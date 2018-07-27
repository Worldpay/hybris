package com.worldpay.converters;

import com.worldpay.service.model.Amount;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.response.ServiceResponse;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.payment.commands.result.AbstractResult;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Abstract converter to be used to transform a {@link ServiceResponse} from Worldpay to an {@link AbstractResult} in hybris in a payment transactions.
 * @param <S> object that extends ServiceResponse
 * @param <T> object that extends AbstractResult
 */
public abstract class WorldpayAbstractServiceResponseConverter<S extends ServiceResponse, T extends AbstractResult> extends AbstractConverter<S, T> {

    private WorldpayOrderService worldpayOrderService;

    /**
     * Returns a BigDecimal with the proper value after applying to the value returned from Worldpay the fraction digits from the {@link Currency} used.
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
}
