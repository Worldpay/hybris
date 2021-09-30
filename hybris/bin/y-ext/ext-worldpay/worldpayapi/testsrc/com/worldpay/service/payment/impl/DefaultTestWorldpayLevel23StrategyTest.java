package com.worldpay.service.payment.impl;

import com.worldpay.data.Amount;
import com.worldpay.data.Item;
import com.worldpay.data.Purchase;
import com.worldpay.service.payment.WorldpayOrderService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTestWorldpayLevel23StrategyTest {

    private static final String VALID_PRODUCT_NAME = "valid product name";

    @InjectMocks
    private DefaultTestWorldpayLevel23Strategy testObj;

    @Mock
    private WorldpayOrderService worldpayOrderServiceMock;

    @Mock
    private AbstractOrderModel orderMock;
    @Mock
    private ProductModel productMock;
    @Mock
    private CurrencyModel currencyMock;
    @Mock
    private Amount amountMock;

    private Purchase purchase;

    @Before
    public void setUp() {
        purchase = new Purchase();
        when(orderMock.getCurrency()).thenReturn(currencyMock);
        when(worldpayOrderServiceMock.createAmount(currencyMock, 0)).thenReturn(amountMock);
        when(productMock.getName()).thenReturn(VALID_PRODUCT_NAME);
    }

    @Test
    public void setCustomerReference_ShouldSetRandomStringLength17() {
        testObj.setCustomerReference(orderMock, purchase);

        assertThat(purchase.getCustomerReference()).isNotEmpty();
        assertThat(purchase.getCustomerReference().length()).isEqualTo(17);
    }

    @Test
    public void setProductDescription_WhenProductNameLessThen26Char_ShouldSetProductName() {
        final Item item = new Item();

        testObj.setProductDescription(productMock, item);

        assertThat(item.getDescription()).isNotEmpty();
        assertThat(item.getDescription()).isEqualTo(VALID_PRODUCT_NAME);
    }

    @Test
    public void setProductDescription_WhenProductNameMoreThen26Char_ShouldTrimProductName() {
        when(productMock.getName()).thenReturn("Product name that exceeds the maximum limit of 26 characters");
        final Item item = new Item();

        testObj.setProductDescription(productMock, item);

        assertThat(item.getDescription().length()).isEqualTo(26);
        assertThat(item.getDescription()).isEqualTo("Product name that exceeds ");
    }

    @Test
    public void setDutyAmount_ShouldCreateAmountWithZeroValue() {
        testObj.setDutyAmount(orderMock, purchase);

        assertThat(purchase.getDutyAmount()).isEqualTo(amountMock);
    }
}
