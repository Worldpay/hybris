package com.worldpay.checkout.steps.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.ValidationResults;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@UnitTest
@ExtendWith(MockitoExtension.class)
class WorldpayCheckoutStepValidatorTest {

    @InjectMocks
    private WorldpayCheckoutStepValidator testObj;

    @Mock
    private RedirectAttributes redirectAttributesMock;

    @Mock
    private CheckoutFlowFacade checkoutFlowFacadeMock;


    @Test
    void validateOnEnter_shouldReturnRedirectToCart_whenPciOptionIsInvalid() {
        when(checkoutFlowFacadeMock.getSubscriptionPciOption()).thenReturn(CheckoutPciOptionEnum.SOP);

        try (MockedStatic<GlobalMessages> globalMessages = mockStatic(GlobalMessages.class)) {
            final ValidationResults result = testObj.validateOnEnter(redirectAttributesMock);

            assertEquals(ValidationResults.REDIRECT_TO_CART, result);

            globalMessages.verifyNoInteractions();
        }
    }

    @Test
    void validateOnEnter_shouldReturnRedirectToCart_whenPciOptionIsNull() {
        when(checkoutFlowFacadeMock.getSubscriptionPciOption()).thenReturn(null);

        try (MockedStatic<GlobalMessages> globalMessages = mockStatic(GlobalMessages.class)) {
            final ValidationResults result = testObj.validateOnEnter(redirectAttributesMock);

            assertEquals(ValidationResults.REDIRECT_TO_CART, result);

            globalMessages.verifyNoInteractions();
        }
    }

    @Test
    void validateOnEnter_shouldReturnRedirectToCart_whenInvalidCart() {
        when(checkoutFlowFacadeMock.getSubscriptionPciOption()).thenReturn(CheckoutPciOptionEnum.HOP);
        when(checkoutFlowFacadeMock.hasValidCart()).thenReturn(false);

        try (MockedStatic<GlobalMessages> globalMessages = mockStatic(GlobalMessages.class)) {
            final ValidationResults result = testObj.validateOnEnter(redirectAttributesMock);

            assertEquals(ValidationResults.REDIRECT_TO_CART, result);

            globalMessages.verifyNoInteractions();
        }
    }

    @Test
    void validateOnEnter_shouldReturnRedirectToDeliveryAddress_whenNoDeliveryAddress_andAddsGlobalMessage() {
        when(checkoutFlowFacadeMock.getSubscriptionPciOption()).thenReturn(CheckoutPciOptionEnum.HOP);
        when(checkoutFlowFacadeMock.hasValidCart()).thenReturn(true);
        when(checkoutFlowFacadeMock.hasNoDeliveryAddress()).thenReturn(true);

        try (MockedStatic<GlobalMessages> globalMessages = mockStatic(GlobalMessages.class)) {
            final ValidationResults result = testObj.validateOnEnter(redirectAttributesMock);

            assertEquals(ValidationResults.REDIRECT_TO_DELIVERY_ADDRESS, result);

            globalMessages.verify(() -> GlobalMessages.addFlashMessage(redirectAttributesMock, GlobalMessages.INFO_MESSAGES_HOLDER, "checkout.multi.deliveryAddress.notprovided"));
        }
    }

    @Test
    void validateOnEnter_shouldReturnRedirectToDeliveryMethod_whenNoDeliveryMode_andAddsGlobalMessage() {
        when(checkoutFlowFacadeMock.getSubscriptionPciOption()).thenReturn(CheckoutPciOptionEnum.HOP);
        when(checkoutFlowFacadeMock.hasValidCart()).thenReturn(true);
        when(checkoutFlowFacadeMock.hasNoDeliveryAddress()).thenReturn(false);
        when(checkoutFlowFacadeMock.hasNoDeliveryMode()).thenReturn(true);

        try (MockedStatic<GlobalMessages> globalMessages = mockStatic(GlobalMessages.class)) {
            final ValidationResults result = testObj.validateOnEnter(redirectAttributesMock);

            assertEquals(ValidationResults.REDIRECT_TO_DELIVERY_METHOD, result);
            globalMessages.verify(() -> GlobalMessages.addFlashMessage(redirectAttributesMock, GlobalMessages.INFO_MESSAGES_HOLDER, "checkout.multi.deliveryMethod.notprovided"));
        }
    }

    // Happy path
    @Test
    void validateOnEnter_shouldReturnSuccess_whenAllValidationsPass() {
        when(checkoutFlowFacadeMock.getSubscriptionPciOption()).thenReturn(CheckoutPciOptionEnum.HOP);
        when(checkoutFlowFacadeMock.hasValidCart()).thenReturn(true);
        when(checkoutFlowFacadeMock.hasNoDeliveryAddress()).thenReturn(false);
        when(checkoutFlowFacadeMock.hasNoDeliveryMode()).thenReturn(false);


        try (MockedStatic<GlobalMessages> globalMessages = mockStatic(GlobalMessages.class)) {
            final ValidationResults result = testObj.validateOnEnter(redirectAttributesMock);

            assertEquals(ValidationResults.SUCCESS, result);

            globalMessages.verifyNoInteractions();
        }
    }

}
