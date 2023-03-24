package com.worldpay.factories;

import com.worldpay.internal.model.CardBrand;

public interface CardBrandFactory {
    /**
     * It returns a new CardBrand object with the code added as input
     * @param   inputCode   {@link String   the input code.}
     */
    CardBrand createCardBrandWithValue(String inputCode);
}
