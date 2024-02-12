package com.worldpay.factories.impl;

import com.worldpay.internal.model.CardBrand;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayCardBrandFactoryTest {
    public static final String CARD_BRAND = "cardBrand";
    private DefaultWorldpayCardBrandFactory testObj;

    @Before
    public void setup() {
        testObj = new DefaultWorldpayCardBrandFactory();
    }
    @Test
    public void createCardBrandWithValue_WhenCodeIsAWellFormedString_ShouldReturnACardBrandWithThatCode() {
        final CardBrand testCardBrand = testObj.createCardBrandWithValue(CARD_BRAND);
        assertThat(testCardBrand).isNotNull();
        assertThat(testCardBrand.getvalue()).isNotEmpty();
        assertThat(testCardBrand.getvalue()).isEqualTo(CARD_BRAND);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createCardBrandWithValue_WhenCodeIsNull_ShouldThrowAnException() {
        testObj.createCardBrandWithValue(null);
    }
}
