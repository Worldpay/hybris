package com.worldpay.service.payment.impl;

import com.worldpay.enums.lineItem.LineItemType;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.data.LineItem;
import com.worldpay.data.OrderLines;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.CoreAlgorithms;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayKlarnaServiceTest {

    private static final String TERMS_URL = "termsURL";
    private static final String SHIPPING_LINE_ITEM_REFERENCE = "SHIPPING_LINE_ITEM_REFERENCE";
    private static final String ORDER_DISCOUNT = "ORDER_DISCOUNT";
    private static final String DISCOUNT_LINE_ITEM_REFERENCE = "DISCOUNT_LINE_ITEM_REFERENCE";
    private static final String PIECES = "Piece";
    private static final String EXPECTED_599 = "599";
    private static final String EXPECTED_2000 = "2000";
    private static final String EXPECTED_1000 = "1000";
    private static final String EXPECTED_100 = "100";
    private static final String SHIPPING = "shipping";
    private static final String PRODUCT_NAME_A = "BT Airhole Helgasons Facemask tiedie LXL";
    private static final String PRODUCT_NAME_B = "Shades Quiksilver Dinero black white red grey";
    private static final String DELIVERY_MODE_NAME = "deliveryModeName";
    private static final String PRODUCT_NAME = "productName";
    private static final String UK_VAT_FULL = "uk-vat-full";
    private static final String KLARNA_1 = "klarna1";
    private static final String KLARNA_2 = " klarna2";
    private static final String KLARNA_3 = "klarna3";
    private static final String NON_KLARNA = "nonKlarna";

    private final List<String> klarnaPaymentMethods = List.of(KLARNA_1, KLARNA_2, KLARNA_3);

    @InjectMocks
    private DefaultWorldpayKlarnaService testObj;

    @Mock
    private CommonI18NService commonI18NServiceMock;
    @Mock
    private WorldpayUrlService worldpayUrlServiceMock;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CartModel cartModelMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CartEntryModel cartEntryModelMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CartEntryModel cartEntryModel2Mock;

    @Before
    public void setUp() throws WorldpayConfigurationException {
        ReflectionTestUtils.setField(testObj, "klarnaPayments", klarnaPaymentMethods);
        when(worldpayUrlServiceMock.getFullTermsUrl()).thenReturn(TERMS_URL);
    }

    @Test
    public void shouldPopulateOrderLines() throws WorldpayConfigurationException {
        when(cartModelMock.getTotalTax()).thenReturn(14.16);
        when(cartModelMock.getEntries()).thenReturn(singletonList(cartEntryModelMock));
        when(cartModelMock.getCurrency().getDigits()).thenReturn(2);
        when(cartEntryModelMock.getOrder().getCurrency().getDigits()).thenReturn(2);
        when(cartEntryModelMock.getEntryNumber()).thenReturn(1);
        when(cartEntryModelMock.getProduct().getName()).thenReturn(PRODUCT_NAME);
        when(cartEntryModelMock.getQuantity()).thenReturn(1L);
        when(cartEntryModelMock.getProduct().getUnit().getName()).thenReturn(PIECES);
        when(cartEntryModelMock.getBasePrice()).thenReturn(84.96);
        when(cartEntryModelMock.getTotalPrice()).thenReturn(84.96);
        when(cartEntryModelMock.getTaxValues()).thenReturn(singletonList(new TaxValue(UK_VAT_FULL, 20, false, 14.16D, "GBP")));
        when(cartEntryModelMock.getDiscountValues()).thenReturn(singletonList(new DiscountValue("dv", 0D, false, "GBP")));

        final OrderLines result = testObj.createOrderLines(cartModelMock);

        assertThat(result.getTermsURL()).isEqualToIgnoringCase(TERMS_URL);
        assertThat(result.getLineItems()).hasSize(1);
        assertThat(result.getOrderTaxAmount()).isEqualToIgnoringCase("1416");
        final LineItem lineItem = result.getLineItems().get(0);
        assertThat(lineItem.getLineItemReference().getValue()).isEqualToIgnoringCase("1");
        assertThat(lineItem.getName()).isEqualToIgnoringCase(PRODUCT_NAME);
        assertThat(lineItem.getQuantity()).isEqualToIgnoringCase("1");
        assertThat(lineItem.getTaxRate()).isEqualToIgnoringCase(EXPECTED_2000);
        assertThat(lineItem.getTotalTaxAmount()).isEqualToIgnoringCase("1416");
        assertThat(lineItem.getTotalDiscountAmount()).isEqualToIgnoringCase("0");
        assertThat(lineItem.getLineItemType()).isEqualTo(LineItemType.PHYSICAL);
    }

    @Test
    public void shouldPopulateOrderLinesAndEscapeThemForXML() throws WorldpayConfigurationException {
        when(cartModelMock.getTotalTax()).thenReturn(14.16);
        when(cartModelMock.getEntries()).thenReturn(singletonList(cartEntryModelMock));
        when(cartModelMock.getCurrency().getDigits()).thenReturn(2);
        when(cartEntryModelMock.getOrder().getCurrency().getDigits()).thenReturn(2);
        when(cartEntryModelMock.getEntryNumber()).thenReturn(1);
        when(cartEntryModelMock.getProduct().getName()).thenReturn("productáéÑÑÑName");
        when(cartEntryModelMock.getQuantity()).thenReturn(1L);
        when(cartEntryModelMock.getProduct().getUnit().getName()).thenReturn("Stück");
        when(cartEntryModelMock.getBasePrice()).thenReturn(84.96);
        when(cartEntryModelMock.getTotalPrice()).thenReturn(84.96);
        when(cartEntryModelMock.getTaxValues()).thenReturn(singletonList(new TaxValue(UK_VAT_FULL, 20, false, 14.16D, "GBP")));
        when(cartEntryModelMock.getDiscountValues()).thenReturn(singletonList(new DiscountValue("dv", 0D, false, "GBP")));

        final OrderLines result = testObj.createOrderLines(cartModelMock);

        assertThat(result.getTermsURL()).isEqualToIgnoringCase(TERMS_URL);
        assertThat(result.getLineItems()).hasSize(1);
        assertThat(result.getOrderTaxAmount()).isEqualToIgnoringCase("1416");
        final LineItem lineItem = result.getLineItems().get(0);
        assertThat(lineItem.getLineItemReference().getValue()).isEqualToIgnoringCase("1");
        assertThat(lineItem.getName()).isEqualToIgnoringCase(StringEscapeUtils.escapeXml10("productáéÑÑÑName"));
        assertThat(lineItem.getQuantityUnit()).isEqualToIgnoringCase(StringEscapeUtils.escapeXml10("Stück"));
        assertThat(lineItem.getQuantity()).isEqualToIgnoringCase("1");
        assertThat(lineItem.getTaxRate()).isEqualToIgnoringCase(EXPECTED_2000);
        assertThat(lineItem.getTotalTaxAmount()).isEqualToIgnoringCase("1416");
        assertThat(lineItem.getTotalDiscountAmount()).isEqualToIgnoringCase("0");
        assertThat(lineItem.getLineItemType()).isEqualTo(LineItemType.PHYSICAL);
    }

    @Test
    public void shouldPopulateOrderLinesFixedDiscountWithoutDelivery() throws WorldpayConfigurationException {
        when(cartModelMock.getTotalTax()).thenReturn(9.66);
        when(cartModelMock.getEntries()).thenReturn(singletonList(cartEntryModelMock));
        when(cartModelMock.getCurrency().getDigits()).thenReturn(2);
        when(cartModelMock.getDeliveryMode().getName()).thenReturn(DELIVERY_MODE_NAME);
        when(cartModelMock.getTotalTaxValues()).thenReturn(singletonList(new TaxValue(UK_VAT_FULL, 20, false, 9.66D, "GBP")));
        when(cartModelMock.getGlobalDiscountValues()).thenReturn(singletonList(new DiscountValue("dv", 10D, true, 10D, "GBP")));

        when(cartEntryModelMock.getOrder().getCurrency().getDigits()).thenReturn(2);
        when(cartEntryModelMock.getEntryNumber()).thenReturn(1);
        when(cartEntryModelMock.getProduct().getName()).thenReturn(PRODUCT_NAME);
        when(cartEntryModelMock.getQuantity()).thenReturn(1L);
        when(cartEntryModelMock.getProduct().getUnit().getName()).thenReturn(PIECES);
        when(cartEntryModelMock.getBasePrice()).thenReturn(67.96);
        when(cartEntryModelMock.getTotalPrice()).thenReturn(67.96);
        when(cartEntryModelMock.getTaxValues()).thenReturn(singletonList(new TaxValue(UK_VAT_FULL, 20, false, 11.33D, "GBP")));
        when(commonI18NServiceMock.roundCurrency(anyDouble(), anyInt())).thenAnswer(invocationOnMock -> {
            final Double amount = (Double) invocationOnMock.getArguments()[0];
            final Integer digits = (Integer) invocationOnMock.getArguments()[1];
            return BigDecimal.valueOf(amount).movePointRight(digits).setScale(digits, RoundingMode.HALF_UP).doubleValue();
        });
        final OrderLines result = testObj.createOrderLines(cartModelMock);

        assertThat(result.getTermsURL()).isEqualToIgnoringCase(TERMS_URL);
        assertThat(result.getLineItems()).hasSize(2);
        assertThat(result.getOrderTaxAmount()).isEqualToIgnoringCase("966");

        final LineItem lineItem = result.getLineItems().get(0);
        assertThat(lineItem.getLineItemType()).isEqualTo(LineItemType.PHYSICAL);
        assertThat(lineItem.getLineItemReference().getValue()).isEqualToIgnoringCase("1");
        assertThat(lineItem.getName()).isEqualToIgnoringCase(PRODUCT_NAME);
        assertThat(lineItem.getQuantity()).isEqualToIgnoringCase("1");
        assertThat(lineItem.getUnitPrice()).isEqualToIgnoringCase("6796");
        assertThat(lineItem.getTaxRate()).isEqualToIgnoringCase(EXPECTED_2000);
        assertThat(lineItem.getTotalAmount()).isEqualToIgnoringCase("6796");
        assertThat(lineItem.getTotalTaxAmount()).isEqualToIgnoringCase("1133");
        assertThat(lineItem.getTotalDiscountAmount()).isEqualToIgnoringCase("0");

        final LineItem lineItemDiscount = result.getLineItems().get(1);
        assertThat(lineItemDiscount.getLineItemType()).isEqualTo(LineItemType.DISCOUNT);
        assertThat(lineItemDiscount.getLineItemReference().getValue()).isEqualToIgnoringCase(DISCOUNT_LINE_ITEM_REFERENCE);
        assertThat(lineItemDiscount.getName()).isEqualToIgnoringCase(ORDER_DISCOUNT);
        assertThat(lineItemDiscount.getQuantity()).isEqualToIgnoringCase("1");
        assertThat(lineItemDiscount.getUnitPrice()).isEqualToIgnoringCase("0");
        assertThat(lineItemDiscount.getTaxRate()).isEqualToIgnoringCase(EXPECTED_2000);
        assertThat(lineItemDiscount.getTotalAmount()).isEqualToIgnoringCase("-1000");
        assertThat(lineItemDiscount.getTotalTaxAmount()).isEqualToIgnoringCase("-167");
        assertThat(lineItemDiscount.getTotalDiscountAmount()).isEqualToIgnoringCase(EXPECTED_1000);
    }

    @Test
    public void shouldPopulateOrderLinesFixedAndPercentageDiscountDelivery() throws WorldpayConfigurationException {
        when(cartModelMock.getDeliveryCost()).thenReturn(5.99D);
        when(cartModelMock.getTotalTax()).thenReturn(27.85D);
        when(cartModelMock.getEntries()).thenReturn(Arrays.asList(cartEntryModelMock, cartEntryModel2Mock));
        when(cartModelMock.getCurrency().getDigits()).thenReturn(2);
        when(cartModelMock.getDeliveryMode().getName()).thenReturn(DELIVERY_MODE_NAME);
        when(cartModelMock.getTotalTaxValues()).thenReturn(singletonList(new TaxValue(UK_VAT_FULL, 20, false, 9.66D, "GBP")));
        when(cartModelMock.getGlobalDiscountValues()).thenReturn(Arrays.asList(
            new DiscountValue("dv", 10D, true, 10D, "GBP"),
            new DiscountValue("dv2", 10D, false, "GBP"))
        );

        when(cartEntryModelMock.getOrder()).thenReturn(cartModelMock);
        when(cartEntryModelMock.getEntryNumber()).thenReturn(0);
        when(cartEntryModelMock.getProduct().getName()).thenReturn(PRODUCT_NAME_B);
        when(cartEntryModelMock.getQuantity()).thenReturn(2L);
        when(cartEntryModelMock.getProduct().getUnit().getName()).thenReturn(PIECES);
        when(cartEntryModelMock.getBasePrice()).thenReturn(84.96D);
        when(cartEntryModelMock.getTotalPrice()).thenReturn(169.92D);
        when(cartEntryModelMock.getTaxValues()).thenReturn(singletonList(new TaxValue(UK_VAT_FULL, 20, false, 28.32D, "GBP")));

        when(cartEntryModel2Mock.getOrder()).thenReturn(cartModelMock);
        when(cartEntryModel2Mock.getEntryNumber()).thenReturn(1);
        when(cartEntryModel2Mock.getProduct().getName()).thenReturn(PRODUCT_NAME_A);
        when(cartEntryModel2Mock.getQuantity()).thenReturn(1L);
        when(cartEntryModel2Mock.getProduct().getUnit().getName()).thenReturn(PIECES);
        when(cartEntryModel2Mock.getBasePrice()).thenReturn(20.21D);
        when(cartEntryModel2Mock.getTotalPrice()).thenReturn(20.21D);
        when(cartEntryModel2Mock.getTaxValues()).thenReturn(singletonList(new TaxValue(UK_VAT_FULL, 20, false, 3.37D, "GBP")));

        when(worldpayUrlServiceMock.getFullTermsUrl()).thenReturn(TERMS_URL);
        when(commonI18NServiceMock.roundCurrency(anyDouble(), anyInt())).thenAnswer(invocationOnMock -> {
            final Double amount = (Double) invocationOnMock.getArguments()[0];
            final Integer digits = (Integer) invocationOnMock.getArguments()[1];
            return CoreAlgorithms.round(amount, digits);
        });

        final OrderLines result = testObj.createOrderLines(cartModelMock);

        assertThat(result.getTermsURL()).isEqualToIgnoringCase(TERMS_URL);
        assertThat(result.getLineItems()).hasSize(4);
        assertThat(result.getOrderTaxAmount()).isEqualToIgnoringCase("2785");

        final LineItem lineItem = result.getLineItems().get(0);
        assertThat(lineItem.getLineItemType()).isEqualTo(LineItemType.PHYSICAL);
        assertThat(lineItem.getLineItemReference().getValue()).isEqualToIgnoringCase("0");
        assertThat(lineItem.getName()).isEqualToIgnoringCase(PRODUCT_NAME_B);
        assertThat(lineItem.getQuantity()).isEqualToIgnoringCase("2");
        assertThat(lineItem.getUnitPrice()).isEqualToIgnoringCase("8496");
        assertThat(lineItem.getTaxRate()).isEqualToIgnoringCase(EXPECTED_2000);
        assertThat(lineItem.getTotalAmount()).isEqualToIgnoringCase("15293");
        assertThat(lineItem.getTotalTaxAmount()).isEqualToIgnoringCase("2549");
        assertThat(lineItem.getTotalDiscountAmount()).isEqualToIgnoringCase("1699");

        final LineItem lineItem2 = result.getLineItems().get(1);
        assertThat(lineItem2.getLineItemType()).isEqualTo(LineItemType.PHYSICAL);
        assertThat(lineItem2.getLineItemReference().getValue()).isEqualToIgnoringCase("1");
        assertThat(lineItem2.getName()).isEqualToIgnoringCase(PRODUCT_NAME_A);
        assertThat(lineItem2.getQuantity()).isEqualToIgnoringCase("1");
        assertThat(lineItem2.getUnitPrice()).isEqualToIgnoringCase("2021");
        assertThat(lineItem2.getTaxRate()).isEqualToIgnoringCase(EXPECTED_2000);
        assertThat(lineItem2.getTotalAmount()).isEqualToIgnoringCase("1819");
        assertThat(lineItem2.getTotalTaxAmount()).isEqualToIgnoringCase("303");
        assertThat(lineItem2.getTotalDiscountAmount()).isEqualToIgnoringCase("202");

        final LineItem lineItemShipping = result.getLineItems().get(2);
        assertThat(lineItemShipping.getLineItemType()).isEqualTo(LineItemType.SHIPPING_FEE);
        assertThat(lineItemShipping.getLineItemReference().getValue()).isEqualToIgnoringCase(SHIPPING_LINE_ITEM_REFERENCE);
        assertThat(lineItemShipping.getQuantity()).isEqualToIgnoringCase("1");
        assertThat(lineItemShipping.getQuantityUnit()).isEqualToIgnoringCase(SHIPPING);
        assertThat(lineItemShipping.getUnitPrice()).isEqualToIgnoringCase(EXPECTED_599);
        assertThat(lineItemShipping.getTaxRate()).isEqualToIgnoringCase(EXPECTED_2000);
        assertThat(lineItemShipping.getTotalAmount()).isEqualToIgnoringCase(EXPECTED_599);
        assertThat(lineItemShipping.getTotalTaxAmount()).isEqualToIgnoringCase(EXPECTED_100);
        assertThat(lineItemShipping.getTotalDiscountAmount()).isEqualToIgnoringCase("0");

        final LineItem lineItemDiscount = result.getLineItems().get(3);
        assertThat(lineItemDiscount.getLineItemType()).isEqualTo(LineItemType.DISCOUNT);
        assertThat(lineItemDiscount.getLineItemReference().getValue()).isEqualToIgnoringCase(DISCOUNT_LINE_ITEM_REFERENCE);
        assertThat(lineItemDiscount.getName()).isEqualToIgnoringCase(ORDER_DISCOUNT);
        assertThat(lineItemDiscount.getQuantity()).isEqualToIgnoringCase("1");
        assertThat(lineItemDiscount.getUnitPrice()).isEqualToIgnoringCase("0");
        assertThat(lineItemDiscount.getTaxRate()).isEqualToIgnoringCase(EXPECTED_2000);
        assertThat(lineItemDiscount.getTotalAmount()).isEqualToIgnoringCase("-1000");
        assertThat(lineItemDiscount.getTotalTaxAmount()).isEqualToIgnoringCase("-167");
        assertThat(lineItemDiscount.getTotalDiscountAmount()).isEqualToIgnoringCase(EXPECTED_1000);
    }

    @Test
    public void shouldPopulateOrderLinesWithDeliveryCost() throws WorldpayConfigurationException {
        when(cartModelMock.getDeliveryCost()).thenReturn(5.99D);
        when(cartModelMock.getTotalTax()).thenReturn(12.32);
        when(cartModelMock.getEntries()).thenReturn(singletonList(cartEntryModelMock));
        when(cartModelMock.getCurrency().getDigits()).thenReturn(2);
        when(cartModelMock.getDeliveryMode().getName()).thenReturn(DELIVERY_MODE_NAME);
        when(cartModelMock.getTotalTaxValues()).thenReturn(singletonList(new TaxValue(UK_VAT_FULL, 20, false, 12.32D, "GBP")));

        when(cartEntryModelMock.getOrder().getCurrency().getDigits()).thenReturn(2);
        when(cartEntryModelMock.getEntryNumber()).thenReturn(0);
        when(cartEntryModelMock.getProduct().getName()).thenReturn("Helmet Women TSG Lotus Graphic Designs wms Butterfly LXL");
        when(cartEntryModelMock.getQuantity()).thenReturn(1L);
        when(cartEntryModelMock.getProduct().getUnit().getName()).thenReturn(PIECES);
        when(cartEntryModelMock.getBasePrice()).thenReturn(67.96);
        when(cartEntryModelMock.getTotalPrice()).thenReturn(67.96);
        when(cartEntryModelMock.getTaxValues()).thenReturn(singletonList(new TaxValue(UK_VAT_FULL, 20, false, 11.33D, "GBP")));
        when(commonI18NServiceMock.roundCurrency(anyDouble(), anyInt())).thenAnswer(invocationOnMock -> {
            final Double amount = (Double) invocationOnMock.getArguments()[0];
            final Integer digits = (Integer) invocationOnMock.getArguments()[1];
            return BigDecimal.valueOf(amount).movePointRight(digits).setScale(digits, RoundingMode.HALF_UP).doubleValue();
        });
        final OrderLines result = testObj.createOrderLines(cartModelMock);

        assertThat(result.getTermsURL()).isEqualToIgnoringCase(TERMS_URL);
        assertThat(result.getLineItems()).hasSize(2);
        assertThat(result.getOrderTaxAmount()).isEqualToIgnoringCase("1232");

        final LineItem lineItem = result.getLineItems().get(0);
        assertThat(lineItem.getLineItemType()).isEqualTo(LineItemType.PHYSICAL);
        assertThat(lineItem.getLineItemReference().getValue()).isEqualToIgnoringCase("0");
        assertThat(lineItem.getName()).isEqualToIgnoringCase("Helmet Women TSG Lotus Graphic Designs wms Butterfly LXL");
        assertThat(lineItem.getQuantity()).isEqualToIgnoringCase("1");
        assertThat(lineItem.getUnitPrice()).isEqualToIgnoringCase("6796");
        assertThat(lineItem.getTaxRate()).isEqualToIgnoringCase(EXPECTED_2000);
        assertThat(lineItem.getTotalAmount()).isEqualToIgnoringCase("6796");
        assertThat(lineItem.getTotalTaxAmount()).isEqualToIgnoringCase("1133");
        assertThat(lineItem.getTotalDiscountAmount()).isEqualToIgnoringCase("0");

        final LineItem lineItemShipping = result.getLineItems().get(1);
        assertThat(lineItemShipping.getLineItemType()).isEqualTo(LineItemType.SHIPPING_FEE);
        assertThat(lineItemShipping.getLineItemReference().getValue()).isEqualToIgnoringCase(SHIPPING_LINE_ITEM_REFERENCE);
        assertThat(lineItemShipping.getQuantity()).isEqualToIgnoringCase("1");
        assertThat(lineItemShipping.getQuantityUnit()).isEqualToIgnoringCase(SHIPPING);
        assertThat(lineItemShipping.getUnitPrice()).isEqualToIgnoringCase(EXPECTED_599);
        assertThat(lineItemShipping.getTaxRate()).isEqualToIgnoringCase(EXPECTED_2000);
        assertThat(lineItemShipping.getTotalAmount()).isEqualToIgnoringCase(EXPECTED_599);
        assertThat(lineItemShipping.getTotalTaxAmount()).isEqualToIgnoringCase("99");
        assertThat(lineItemShipping.getTotalDiscountAmount()).isEqualToIgnoringCase("0");
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
