package com.worldpay.klarna.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayKlarnaUtilsTest {

    private static final String KLARNA_1 = "klarna1";
    private static final String KLARNA_2 = " klarna2";
    private static final String KLARNA_3 = "klarna3";
    private static final String NON_KLARNA = "nonKlarna";

    private DefaultWorldpayKlarnaUtils testObj;

    private final List<String> klarnaPaymentMethods = Arrays.asList(KLARNA_1, KLARNA_2, KLARNA_3);

    @Before
    public void setUp() throws Exception {
        testObj = new DefaultWorldpayKlarnaUtils(klarnaPaymentMethods);
    }

    @Test
    public void isKlarnaPaymentType_shouldReturnTrueWhenCodeIsInKlarnaPaymentMethodsList() {
        final boolean result = testObj.isKlarnaPaymentType(KLARNA_2);

        assertThat(result).isTrue();
    }

    @Test
    public void isKlarnaPaymentType_shouldReturnFalseWhenCodeIsNotInKlarnaPaymentMethodsList() {
        final boolean result = testObj.isKlarnaPaymentType(NON_KLARNA);

        assertThat(result).isFalse();
    }
}
