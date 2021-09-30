package com.worldpay.actions.replenishment;

import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.direct.WorldpayB2BDirectOrderFacade;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import com.worldpay.data.MerchantInfo;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bacceleratorservices.model.process.ReplenishmentProcessModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
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
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
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
    private WorldpayB2BDirectOrderFacade worldpayB2BDirectOrderFacadeMock;
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoServiceMock;

    @Mock
    private ProcessParameterHelper processParameterHelperMock;
    @Mock
    private ReplenishmentProcessModel processModelMock;
    @Mock
    private BusinessProcessParameterModel clonedCartParameterMock;
    @Mock
    private CartModel clonedCartMock;
    @Mock
    private CreditCardPaymentInfoModel paymentInfoMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private DirectResponseData directResponseDataMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private BaseSiteModel currentSiteMock;

    @Before
    public void setUp() throws WorldpayException, InvalidCartException {
        Whitebox.setInternalState(testObj, "impersonationService", new TestImpersonationService());
        Whitebox.setInternalState(testObj, "processParameterHelper", processParameterHelperMock);
        Whitebox.setInternalState(testObj, "modelService", modelServiceMock);
        when(processParameterHelperMock.getProcessParameterByName(processModelMock, "cart")).thenReturn(clonedCartParameterMock);
        when(clonedCartMock.getSite()).thenReturn(currentSiteMock);
        when(clonedCartParameterMock.getValue()).thenReturn(clonedCartMock);
        when(clonedCartMock.getPaymentInfo()).thenReturn(paymentInfoMock);
        when(worldpayMerchantInfoServiceMock.getReplenishmentMerchant(currentSiteMock)).thenReturn(merchantInfoMock);
        when(worldpayB2BDirectOrderFacadeMock.authoriseRecurringPayment(Matchers.eq(clonedCartMock), any(WorldpayAdditionalInfoData.class))).thenReturn(directResponseDataMock);
    }

    @Test
    public void executeAction_WhenAuthorised_ShouldReturnOK() {
        when(directResponseDataMock.getTransactionStatus()).thenReturn(TransactionStatus.AUTHORISED);

        final AbstractSimpleDecisionAction.Transition transition = testObj.executeAction(processModelMock);

        assertEquals(AbstractSimpleDecisionAction.Transition.OK, transition);
        verify(modelServiceMock).refresh(clonedCartMock);
    }

    @Test
    public void executeAction_WhenNotAuthorised_ShouldReturnNOK() {
        when(directResponseDataMock.getTransactionStatus()).thenReturn(TransactionStatus.REFUSED);

        final AbstractSimpleDecisionAction.Transition transition = testObj.executeAction(processModelMock);

        assertEquals(AbstractSimpleDecisionAction.Transition.NOK, transition);
    }

    protected class TestImpersonationService implements ImpersonationService {
        @Override
        public <R, T extends Throwable> R executeInContext(final ImpersonationContext context, final Executor<R, T> wrapper) throws T {
            return wrapper.execute();
        }
    }
}
