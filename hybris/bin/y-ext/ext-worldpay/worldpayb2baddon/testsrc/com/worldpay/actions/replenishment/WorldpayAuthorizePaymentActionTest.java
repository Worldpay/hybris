package com.worldpay.actions.replenishment;

import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.direct.WorldpayB2BDirectOrderFacade;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import com.worldpay.service.model.MerchantInfo;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bacceleratorservices.model.process.ReplenishmentProcessModel;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.processengine.helpers.ProcessParameterHelper;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayAuthorizePaymentActionTest {

    @InjectMocks
    private WorldpayAuthorizePaymentAction testObj;

    @Mock
    private WorldpayB2BDirectOrderFacade worldpayB2BDirectOrderFacade;
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoService;

    @Mock
    private ProcessParameterHelper processParameterHelper;
    @Mock
    private ReplenishmentProcessModel processModel;
    @Mock
    private BusinessProcessParameterModel clonedCartParameter;
    @Mock
    private CartModel clonedCart;
    @Mock
    private CreditCardPaymentInfoModel paymentInfo;
    @Mock
    private MerchantInfo merchantInfo;
    @Mock
    private DirectResponseData directResponseData;
    @Mock
    private ModelService modelServiceMock;
    
    @Before
    public void setUp() throws WorldpayException, InvalidCartException {
        testObj.setImpersonationService(new TestImpersonationService());
        when(processParameterHelper.getProcessParameterByName(processModel, "cart")).thenReturn(clonedCartParameter);
        when(clonedCartParameter.getValue()).thenReturn(clonedCart);
        when(clonedCart.getPaymentInfo()).thenReturn(paymentInfo);
        when(worldpayMerchantInfoService.getReplenishmentMerchant()).thenReturn(merchantInfo);
        when(worldpayB2BDirectOrderFacade.authoriseRecurringPayment(Matchers.eq(clonedCart), any(WorldpayAdditionalInfoData.class), Matchers.eq(merchantInfo))).thenReturn(directResponseData);
    }

    @Test
    public void executeActionShouldReturnOKWhenAuthorized() {
        when(directResponseData.getTransactionStatus()).thenReturn(TransactionStatus.AUTHORISED);

        AbstractSimpleDecisionAction.Transition transition = testObj.executeAction(processModel);

        assertEquals(AbstractSimpleDecisionAction.Transition.OK, transition);
        verify(modelServiceMock).refresh(clonedCart);
    }

    @Test
    public void executeActionShouldReturnNOKWhenNotAuthorized() {
        when(directResponseData.getTransactionStatus()).thenReturn(TransactionStatus.REFUSED);

        AbstractSimpleDecisionAction.Transition transition = testObj.executeAction(processModel);

        assertEquals(AbstractSimpleDecisionAction.Transition.NOK, transition);
    }

    protected class TestImpersonationService implements ImpersonationService {
        @Override
        public <R, T extends Throwable> R executeInContext(final ImpersonationContext context, final Executor<R, T> wrapper) throws T {
            return wrapper.execute();
        }
    }
}
