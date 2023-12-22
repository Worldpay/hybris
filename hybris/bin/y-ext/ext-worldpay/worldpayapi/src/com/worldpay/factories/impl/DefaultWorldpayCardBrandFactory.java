package com.worldpay.factories.impl;

import com.worldpay.factories.CardBrandFactory;
import com.worldpay.internal.model.CardBrand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

public class DefaultWorldpayCardBrandFactory implements CardBrandFactory {

    @Override
    public CardBrand createCardBrandWithValue(final String inputCode) {
        if(StringUtils.isBlank(inputCode)) {
            throw new IllegalArgumentException("The input code shouldn't be null");
        }

        final CardBrand newCardBrand = new CardBrand();
        newCardBrand.setvalue(inputCode);
        return newCardBrand;
    }
}
