package com.worldpay.facades.payment.hosted.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.merchant.strategies.WorldpayOrderInfoStrategy;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.payment.WorldpayRedirectOrderService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayHostedOrderFacadeTest {

    private static final String MERCHANT_CODE = "merchantCode";
    private static final String WORLDPAY_MERCHANT_CODE = "worldpayMerchantCode";

    @InjectMocks
    private DefaultWorldpayHostedOrderFacade testObj;

    @Mock
    private WorldpayRedirectOrderService worldpayRedirectOrderServiceMock;
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoServiceMock;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private WorldpayOrderInfoStrategy worldpayOrderInfoStrategyMock;
    @Mock
    private SessionService sessionServiceMock;
    @Mock
    private WorldpayPaymentInfoService worldpayPaymentInfoServiceMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private AdditionalAuthInfo additionalAuthInfoMock;
    @Mock
    private WorldpayMerchantConfigData worldpayMerchantConfigDataMock;
    @Mock
    private RedirectAuthoriseResult redirectAuthoriseResultMock;
    @Mock
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacadeMock;
    @Mock
    private PaymentInfoModel paymentInfoModelMock;
    @Mock
    private WorldpayAdditionalInfoData worlpayAdditionalInfoDataMock;

    @Before
    public void setUp() {
        when(sessionServiceMock.getAttribute(WORLDPAY_MERCHANT_CODE)).thenReturn(MERCHANT_CODE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
    }

    @Test
    public void shouldDoRedirectAuthorise() throws WorldpayException {
        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant()).thenReturn(merchantInfoMock);
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData()).thenReturn(worldpayMerchantConfigDataMock);

        testObj.redirectAuthorise(additionalAuthInfoMock, worlpayAdditionalInfoDataMock);

        verify(worldpayOrderInfoStrategyMock).populateAdditionalAuthInfo(additionalAuthInfoMock, worldpayMerchantConfigDataMock);
        verify(worldpayRedirectOrderServiceMock).redirectAuthorise(merchantInfoMock, cartModelMock, additionalAuthInfoMock, worlpayAdditionalInfoDataMock);
    }

    @Test
    public void shouldCompleteRedirect() {
        testObj.completeRedirectAuthorise(redirectAuthoriseResultMock);

        verify(worldpayRedirectOrderServiceMock).completePendingRedirectAuthorise(redirectAuthoriseResultMock, MERCHANT_CODE, cartModelMock);
    }

    @Test
    public void createPaymentInfoModelOnCart_shouldCreateAPaymentInfoWithAttributeIsSavedSetToTrue_whenItIsCalled() {
        testObj.createPaymentInfoModelOnCart(true);

        verify(cartServiceMock).getSessionCart();
        verify(worldpayPaymentInfoServiceMock).createPaymentInfoModelOnCart(cartModelMock, true);
    }

    @Test
    public void createPaymentInfoModelOnCart_shouldCreateAPaymentInfoWithAttributeIsSavedSetToFalse_whenItIsCalled() {
        testObj.createPaymentInfoModelOnCart(false);

        verify(worldpayPaymentInfoServiceMock).createPaymentInfoModelOnCart(cartModelMock, false);
    }
}
