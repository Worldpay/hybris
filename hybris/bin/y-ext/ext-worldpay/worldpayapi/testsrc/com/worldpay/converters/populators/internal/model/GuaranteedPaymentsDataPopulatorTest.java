package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.*;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GuaranteedPaymentsDataPopulatorTest {

    private static final String VALUE = "100";
    private static final String DELIVERY = "DELIVERY";

    @InjectMocks
    private GuaranteedPaymentsDataPopulator testObj;

    @Mock
    private Converter<PurchaseDiscount, com.worldpay.internal.model.PurchaseDiscount> internalPurchaseDiscountConverterMock;
    @Mock
    private Converter<Membership, com.worldpay.internal.model.Membership> internalMembershipConverterMock;
    @Mock
    private Converter<Product, com.worldpay.internal.model.Product> internalProductConverterMock;
    @Mock
    private Converter<UserAccount, com.worldpay.internal.model.UserAccount> internalUserAccountConverterMock;

    @Mock
    private GuaranteedPaymentsData sourceMock;
    @Mock
    private UserAccount userAccountMock;
    @Mock
    private Membership membershipsMock;
    @Mock
    private PurchaseDiscount discountCodesMock;
    @Mock
    private Product productDetailsMock;
    @Mock
    private com.worldpay.internal.model.UserAccount intUserAccountMock;
    @Mock
    private com.worldpay.internal.model.PurchaseDiscount intPurchaseDiscount;
    @Mock
    private com.worldpay.internal.model.Membership intMembership;
    @Mock
    private com.worldpay.internal.model.Product intProduct;

    @Before
    public void setUp() {
        testObj = new GuaranteedPaymentsDataPopulator(internalPurchaseDiscountConverterMock, internalMembershipConverterMock, internalProductConverterMock, internalUserAccountConverterMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_whenSourceIsNull_shouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.GuaranteedPaymentsData());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_whenTargetIsNull_shouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_whenSourceIsNotNullAndAllFieldsAreFilled_shouldPopulate() {
        when(sourceMock.getMemberships()).thenReturn(Collections.singletonList(membershipsMock));
        when(sourceMock.getUserAccount()).thenReturn(userAccountMock);
        when(sourceMock.getDiscountCodes()).thenReturn(Collections.singletonList(discountCodesMock));
        when(sourceMock.getProductDetails()).thenReturn(Collections.singletonList(productDetailsMock));
        when(sourceMock.getSecondaryAmount()).thenReturn(VALUE);
        when(sourceMock.getFulfillmentMethodType()).thenReturn(DELIVERY);
        when(sourceMock.getSurchargeAmount()).thenReturn(VALUE);
        when(sourceMock.getTotalShippingCost()).thenReturn(VALUE);

        when(internalPurchaseDiscountConverterMock.convertAll(Collections.singletonList(discountCodesMock))).thenReturn(Collections.singletonList(intPurchaseDiscount));
        when(internalMembershipConverterMock.convertAll(Collections.singletonList(membershipsMock))).thenReturn(Collections.singletonList(intMembership));
        when(internalProductConverterMock.convertAll(Collections.singletonList(productDetailsMock))).thenReturn(Collections.singletonList(intProduct));
        when(internalUserAccountConverterMock.convert(userAccountMock)).thenReturn(intUserAccountMock);

        final com.worldpay.internal.model.GuaranteedPaymentsData target = new com.worldpay.internal.model.GuaranteedPaymentsData();
        testObj.populate(sourceMock, target);

        assertThat(target.getTotalShippingCost()).isEqualTo(VALUE);
        assertThat(target.getSurchargeAmount().getValue()).isEqualTo(VALUE);
        assertThat(target.getSecondaryAmount().getValue()).isEqualTo(VALUE);
        assertThat(target.getFulfillmentMethodType().getValue()).isEqualTo(DELIVERY);
        assertThat(target.getMemberships().getMembership()).isEqualTo(Collections.singletonList(intMembership));
        assertThat(target.getDiscountCodes().getPurchaseDiscount()).isEqualTo(Collections.singletonList(intPurchaseDiscount));
        assertThat(target.getProductDetails().getProduct()).isEqualTo(Collections.singletonList(intProduct));
        assertThat(target.getUserAccount()).isEqualTo(intUserAccountMock);
    }

    @Test
    public void populate_whenDiscountCodesIsNull_shouldNotPopulateDiscountCodes() {
        when(sourceMock.getDiscountCodes()).thenReturn(null);

        final com.worldpay.internal.model.GuaranteedPaymentsData target = new com.worldpay.internal.model.GuaranteedPaymentsData();
        testObj.populate(sourceMock, target);

        assertThat(target.getDiscountCodes()).isNull();
    }

    @Test
    public void populate_whenMembershipsIsNull_shouldNotPopulateMemberships() {
        when(sourceMock.getMemberships()).thenReturn(null);

        final com.worldpay.internal.model.GuaranteedPaymentsData target = new com.worldpay.internal.model.GuaranteedPaymentsData();
        testObj.populate(sourceMock, target);

        assertThat(target.getMemberships()).isNull();
    }

    @Test
    public void populate_whenProductDetailsIsNull_shouldNotPopulateProductDetails() {
        when(sourceMock.getProductDetails()).thenReturn(null);

        final com.worldpay.internal.model.GuaranteedPaymentsData target = new com.worldpay.internal.model.GuaranteedPaymentsData();
        testObj.populate(sourceMock, target);

        assertThat(target.getProductDetails()).isNull();
    }

    @Test
    public void populate_whenUserAccountIsNull_shouldNotPopulateUserAccount() {
        when(sourceMock.getUserAccount()).thenReturn(null);

        final com.worldpay.internal.model.GuaranteedPaymentsData target = new com.worldpay.internal.model.GuaranteedPaymentsData();
        testObj.populate(sourceMock, target);

        assertThat(target.getUserAccount()).isNull();
    }
}
