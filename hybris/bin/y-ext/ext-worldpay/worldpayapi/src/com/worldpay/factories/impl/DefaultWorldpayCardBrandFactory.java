package com.worldpay.factories.impl;

import com.worldpay.factories.CardBrandFactory;
import com.worldpay.internal.model.CardBrand;
import org.springframework.lang.NonNull;

public class DefaultWorldpayCardBrandFactory implements CardBrandFactory {

    @Override
    public CardBrand createCardBrandWithValue(@NonNull String inputCode) {
        if(inputCode == null) {
            throw new IllegalArgumentException("The input code shouldn't be null");
        }
        CardBrand newCardBrand = new CardBrand();
        newCardBrand.setvalue(inputCode);
        return newCardBrand;
    }
}
