package com.worldpay.actions.replenishment;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bacceleratorservices.model.process.ReplenishmentProcessModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;
import de.hybris.platform.processengine.helpers.impl.DefaultProcessParameterHelper;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayCloneCartActionTest {

    public static final String GENERATED_CODE = "00001000";
    public static final String GENERATED_GUID = "xxxxx-xxxxx-xxxxx-xxxxx";

    @InjectMocks
    private WorldpayCloneCartAction testObj;

    @Mock
    private CartService cartService;
    @Mock
    private TypeService typeService;
    @Mock
    @SuppressWarnings("PMD.UnusedPrivateField")
    private UserService userService;
    @Mock
    private KeyGenerator keyGenerator;
    @Mock
    private KeyGenerator guidKeyGenerator;
    @Mock
    private ModelService modelService;

    @Mock
    private ReplenishmentProcessModel processModel;
    @Mock
    private CartToOrderCronJobModel cartToOrderCronJobModel;
    @Mock
    private CartModel cronJobCart;
    @Mock
    private ComposedTypeModel composedTypeForCart;
    @Mock
    private ComposedTypeModel composedTypeForCartEntry;
    @Mock
    private AddressModel paymentAddress;
    @Mock
    private AddressModel deliveryAddress;
    @Mock
    private PaymentInfoModel paymentInfoModel;

    private BusinessProcessParameterModel businessProcessParameterModel;
    private CartModel clonedCart;

    @Before
    public void setUp() {
        clonedCart = new CartModel();
        businessProcessParameterModel = new BusinessProcessParameterModel();
        DefaultProcessParameterHelper processParameterHelper = new DefaultProcessParameterHelper();
        processParameterHelper.setModelService(modelService);

        testObj.setProcessParameterHelper(processParameterHelper);

        Mockito.when(processModel.getCartToOrderCronJob()).thenReturn(cartToOrderCronJobModel);
        Mockito.when(cartToOrderCronJobModel.getCart()).thenReturn(cronJobCart);
        Mockito.when(modelService.create(BusinessProcessParameterModel.class)).thenReturn(businessProcessParameterModel);
        Mockito.when(keyGenerator.generate()).thenReturn(GENERATED_CODE);
        Mockito.when(guidKeyGenerator.generate()).thenReturn(GENERATED_GUID);
        Mockito.when(typeService.getComposedTypeForClass(CartModel.class)).thenReturn(composedTypeForCart);
        Mockito.when(typeService.getComposedTypeForClass(CartEntryModel.class)).thenReturn(composedTypeForCartEntry);
        Mockito.when(cartService.clone(composedTypeForCart, composedTypeForCartEntry, cronJobCart, GENERATED_CODE)).thenReturn(clonedCart);
        Mockito.when(cartToOrderCronJobModel.getPaymentAddress()).thenReturn(paymentAddress);
        Mockito.when(cartToOrderCronJobModel.getDeliveryAddress()).thenReturn(deliveryAddress);
        Mockito.when(cartToOrderCronJobModel.getPaymentInfo()).thenReturn(paymentInfoModel);
    }

    @Test
    public void executeActionShouldAddCloneToProcess() {
        testObj.executeAction(processModel);

        Assert.assertEquals(processModel, businessProcessParameterModel.getProcess());
        Assert.assertEquals("cart", businessProcessParameterModel.getName());
        Assert.assertEquals(clonedCart, businessProcessParameterModel.getValue());
    }

    @Test
    public void executeActionShouldCreateCloneOfCart() {
        testObj.executeAction(processModel);

        Mockito.verify(cartService).clone(composedTypeForCart, composedTypeForCartEntry, cronJobCart, GENERATED_CODE);
        Assert.assertEquals(paymentAddress, clonedCart.getPaymentAddress());
        Assert.assertEquals(deliveryAddress, clonedCart.getDeliveryAddress());
        Assert.assertEquals(paymentInfoModel, clonedCart.getPaymentInfo());
        Assert.assertEquals(GENERATED_GUID, clonedCart.getGuid());
    }
}
