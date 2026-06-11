package com.worldpay.worldpayresponsemock.controllers.pages;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.worldpay.worldpayresponsemock.controllers.WorldpayResponseMockControllerConstants.Pages.Views.RESPONSES;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.DEFAULT_CARD_HOLDER_NAME;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.DEFAULT_CARD_MONTH;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.DEFAULT_CARD_YEAR;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.DEFAULT_CURRENCY_CODE;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.DEFAULT_MERCHANT_CODE;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.DEFAULT_ORDER_CODE;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.DEFAULT_RISK_SCORE;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.DEFAULT_TRANSACTION_AMOUNT;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.FINAL_SCORE;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.MERCHANTS;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.PAYMENT_METHODS;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.PAYMENT_METHOD_APMS;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.POSSIBLE_EVENTS;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.RESPONSE_CODES;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.TEST_CREDIT_CARDS;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.XML_RESPONSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.exception.WorldpayException;
import com.worldpay.worldpayresponsemock.form.ResponseForm;
import com.worldpay.worldpayresponsemock.merchant.WorldpayResponseMockMerchantInfoService;
import com.worldpay.worldpayresponsemock.mock.WorldpayMockConnector;
import com.worldpay.worldpayresponsemock.responses.WorldpayNotificationResponseBuilder;
import de.hybris.bootstrap.annotations.UnitTest;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

@UnitTest
@ExtendWith(MockitoExtension.class)
public class WorldpayOrderModificationMockControllerTest {

    private static final Integer RESPONSE_CODE = 10;
    private static final String RESPONSE_DESCRIPTION = "responseDescription";
    private static final String MASKED_CREDIT_CARD_1 = "444433******1111";
    private static final String MASKED_CREDIT_CARD_2 = "630495********0000";
    private static final String SOME_RESPONSE = "some_response";
    private static final String CREDIT_CARD_NUMBER = "4444333322221111";
    private static final String CREDIT_CARD_NUMBER_2 = "630495060000000000";
    private static final String DEFAULT_AAV_RESULT = "B";

    private static final String PRETTY_XML = "prettyXML";
    private static final String MERCHANT_1 = "merchant1";
    private static final String MERCHANT_2 = "merchant2";
    private static final String APM_1 = "apm1";
    private static final String APM_2 = "apm2";
    private static final String RESPONSE_FORM = "responseForm";
    private static final String FRAUD_SIGHT_MESSAGES = "fraudSightMessages";
    private static final String WORLDPAY_CREDIT_CARDS = "worldpayCreditCards";
    private static final String ISO_8583_RESPONSE_CODES = "iso8583ResponseCodes";
    private static final String WORLDPAY_MOCK_CONNECTOR = "worldpayMockConnector";
    private static final String FRAUD_SIGHT_REASON_CODES = "fraudSightReasonCodes";
    private static final String WORLDPAY_PAYMENT_METHODS = "worldpayPaymentMethods";
    private static final String GUARANTEED_PAYMENTS_MESSAGES = "guaranteedPaymentsMessages";
    private static final String GUARANTEED_PAYMENTS_REASON_CODES = "guaranteedPaymentsTriggeredRules";
    private static final String APM_CONFIGURATION_LOOKUP_SERVICE = "apmConfigurationLookupService";
    private static final String WORLDPAY_NOTIFICATION_RESPONSE_BUILDER = "worldpayNotificationResponseBuilder";
    private static final String WORLDPAY_RESPONSE_MOCK_MERCHANT_INFO_SERVICE = "worldpayResponseMockMerchantInfoService";

    private WorldpayOrderModificationMockController testObj;

    @Mock
    private ResponseForm responseFormMock;
    @Mock
    private ModelMap modelMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock(name = ISO_8583_RESPONSE_CODES)
    private Map<Integer, String> iso8583ResponseCodesMock;
    @Mock
    private WorldpayNotificationResponseBuilder worldpayNotificationResponseBuilderMock;
    @Mock
    private APMConfigurationLookupService apmConfigurationLookupServiceMock;
    @Mock
    private WorldpayResponseMockMerchantInfoService worldpayMerchantMockServiceMock;
    @Mock(name = WORLDPAY_CREDIT_CARDS)
    private Map<String, String> worldpayCreditCardsMock;
    @Mock(name = WORLDPAY_PAYMENT_METHODS)
    private Map<String, String> worldpayPaymentMethodsMock;
    @Mock
    private Set<String> possibleEventsMock;
    @Mock
    private Set<String> fraudSightMessagesMock;
    @Mock
    private Set<String> fraudSightReasonCodesMock;
    @Mock
    private WorldpayMockConnector worldpayMockConnectorMock;
    @Mock
    private Set<String> guaranteedPaymentsMessagesMock;
    @Mock
    private Set<String> guaranteedPaymentsTriggeredRulesMock;

    @BeforeEach
    public void setUp() {
        testObj = new WorldpayOrderModificationMockController();
        ReflectionTestUtils.setField(testObj, GUARANTEED_PAYMENTS_MESSAGES, guaranteedPaymentsMessagesMock);
        ReflectionTestUtils.setField(testObj, GUARANTEED_PAYMENTS_REASON_CODES, guaranteedPaymentsTriggeredRulesMock);
        ReflectionTestUtils.setField(testObj, FRAUD_SIGHT_MESSAGES, fraudSightMessagesMock);
        ReflectionTestUtils.setField(testObj, FRAUD_SIGHT_REASON_CODES, fraudSightReasonCodesMock);
        ReflectionTestUtils.setField(testObj, POSSIBLE_EVENTS, possibleEventsMock);
        ReflectionTestUtils.setField(testObj, APM_CONFIGURATION_LOOKUP_SERVICE, apmConfigurationLookupServiceMock);
        ReflectionTestUtils.setField(testObj, WORLDPAY_RESPONSE_MOCK_MERCHANT_INFO_SERVICE, worldpayMerchantMockServiceMock);
        ReflectionTestUtils.setField(testObj, ISO_8583_RESPONSE_CODES, iso8583ResponseCodesMock);
        ReflectionTestUtils.setField(testObj, WORLDPAY_CREDIT_CARDS, worldpayCreditCardsMock);
        ReflectionTestUtils.setField(testObj, WORLDPAY_PAYMENT_METHODS, worldpayPaymentMethodsMock);
        ReflectionTestUtils.setField(testObj, WORLDPAY_NOTIFICATION_RESPONSE_BUILDER, worldpayNotificationResponseBuilderMock);
        ReflectionTestUtils.setField(testObj, WORLDPAY_MOCK_CONNECTOR, worldpayMockConnectorMock);
    }

    @Test
    public void testMaskCreditCardNumber() {
        final String result = testObj.maskCreditCardNumber(CREDIT_CARD_NUMBER);
        assertEquals(MASKED_CREDIT_CARD_1, result);
    }

    @Test
    public void testMaskCreditCardNumberOtherLength() {
        final String result = testObj.maskCreditCardNumber(CREDIT_CARD_NUMBER_2);
        assertEquals(MASKED_CREDIT_CARD_2, result);
    }

    @Test
    public void sendResponseShouldPostNotificationMessageToStorefront() throws Exception {
        when(apmConfigurationLookupServiceMock.getAllApmPaymentTypeCodes()).thenReturn(new HashSet<>(List.of(APM_1, APM_2)));

        when(responseFormMock.getResponseCode()).thenReturn(RESPONSE_CODE);
        when(iso8583ResponseCodesMock.get(RESPONSE_CODE)).thenReturn(RESPONSE_DESCRIPTION);
        when(responseFormMock.getTestCreditCard()).thenReturn(CREDIT_CARD_NUMBER);
        when(responseFormMock.getObfuscatedPAN()).thenReturn(CREDIT_CARD_NUMBER);
        when(worldpayNotificationResponseBuilderMock.buildResponse(responseFormMock)).thenReturn(SOME_RESPONSE);
        final Set<String> merchantSet = Set.of(MERCHANT_1, MERCHANT_2);
        when(worldpayMerchantMockServiceMock.getAllMerchantCodes()).thenReturn(merchantSet);

        when(worldpayNotificationResponseBuilderMock.prettifyXml(anyString())).thenReturn(PRETTY_XML);

        final String result = testObj.sendResponse(responseFormMock, modelMock, requestMock);

        assertEquals(RESPONSES, result);
        verify(worldpayMockConnectorMock).sendResponse(requestMock, SOME_RESPONSE);
        verify(apmConfigurationLookupServiceMock).getAllApmPaymentTypeCodes();
        verify(responseFormMock).setResponseDescription(RESPONSE_DESCRIPTION);
        verify(modelMock).put(eq(PAYMENT_METHOD_APMS), anySet());
        verify(modelMock).put(XML_RESPONSE, PRETTY_XML);
        verify(modelMock).put(RESPONSE_CODES, iso8583ResponseCodesMock);
        verify(modelMock).put(TEST_CREDIT_CARDS, worldpayCreditCardsMock);
        verify(modelMock).put(PAYMENT_METHODS, worldpayPaymentMethodsMock);
        verify(modelMock).put(POSSIBLE_EVENTS, possibleEventsMock);
        verify(modelMock).put(MERCHANTS, merchantSet);
    }

    @Test
    public void getAllAnswersShouldPopulateModelWithDefaultSiteIdAndNotPost() {
        when(apmConfigurationLookupServiceMock.getAllApmPaymentTypeCodes()).thenReturn(new HashSet<>(List.of(APM_1, APM_2)));
        final Set<String> merchantSet = Set.of(MERCHANT_1, MERCHANT_2);
        when(worldpayMerchantMockServiceMock.getAllMerchantCodes()).thenReturn(merchantSet);

        testObj.getAllAnswers(modelMock);

        verify(apmConfigurationLookupServiceMock).getAllApmPaymentTypeCodes();
        verify(modelMock).put(eq(PAYMENT_METHOD_APMS), anySet());
        verify(modelMock, never()).put(eq(XML_RESPONSE), anyString());
        verify(modelMock).put(RESPONSE_CODES, iso8583ResponseCodesMock);
        verify(modelMock).put(TEST_CREDIT_CARDS, worldpayCreditCardsMock);
        verify(modelMock).put(PAYMENT_METHODS, worldpayPaymentMethodsMock);
        verify(modelMock).put(POSSIBLE_EVENTS, possibleEventsMock);
        verify(worldpayMerchantMockServiceMock).getAllMerchantCodes();
        verify(modelMock).put(MERCHANTS, merchantSet);
        verify(modelMock).put(FRAUD_SIGHT_MESSAGES, fraudSightMessagesMock);
        verify(modelMock).put(FRAUD_SIGHT_REASON_CODES, fraudSightReasonCodesMock);
        verify(modelMock).put(GUARANTEED_PAYMENTS_MESSAGES, guaranteedPaymentsMessagesMock);
        verify(modelMock).put(GUARANTEED_PAYMENTS_REASON_CODES, guaranteedPaymentsTriggeredRulesMock);
    }

    @Test
    public void getAllAnswersShouldPopulateResponseForm() throws WorldpayException {
        when(apmConfigurationLookupServiceMock.getAllApmPaymentTypeCodes()).thenReturn(new HashSet<>(List.of(APM_1, APM_2)));
        when(worldpayMerchantMockServiceMock.getAllMerchantCodes()).thenReturn(Set.of(MERCHANT_1, MERCHANT_2));

        final ModelAndView result = testObj.getAllAnswers(modelMock);
        Assertions.assertNotNull(result);
        assertEquals(RESPONSES, result.getViewName());

        final ResponseForm responseForm = (ResponseForm) result.getModelMap().get(RESPONSE_FORM);
        assertEquals(DEFAULT_ORDER_CODE, responseForm.getWorldpayOrderCode());
        assertEquals(DEFAULT_MERCHANT_CODE, responseForm.getMerchantCode());
        assertEquals(DEFAULT_CARD_HOLDER_NAME, responseForm.getCardHolderName());
        assertEquals(DEFAULT_CARD_MONTH, responseForm.getCardMonth());
        assertEquals(DEFAULT_CARD_YEAR, responseForm.getCardYear());
        assertEquals(DEFAULT_CURRENCY_CODE, responseForm.getCurrencyCode());
        assertEquals(DEFAULT_RISK_SCORE, responseForm.getRiskValue());
        assertEquals(DEFAULT_TRANSACTION_AMOUNT, responseForm.getTransactionAmount());
        assertEquals(FINAL_SCORE, responseForm.getFinalScore());
        assertEquals(DEFAULT_AAV_RESULT, responseForm.getAavAddress());
        assertEquals(DEFAULT_AAV_RESULT, responseForm.getAavCardholderName());
        assertEquals(DEFAULT_AAV_RESULT, responseForm.getAavEmail());
        assertEquals(DEFAULT_AAV_RESULT, responseForm.getAavPostcode());
        assertEquals(DEFAULT_AAV_RESULT, responseForm.getAavTelephone());
        assertEquals(0.0d, responseForm.getFraudSightScore());
    }

    @Test
    public void getAllMerchantsForSiteShouldReturnAListOfMerchants() {
        when(worldpayMerchantMockServiceMock.getAllMerchantCodes()).thenReturn(Set.of(MERCHANT_1, MERCHANT_2));

        final Set<String> merchantsBySite = testObj.getMerchants();

        verify(worldpayMerchantMockServiceMock).getAllMerchantCodes();
        assertTrue(merchantsBySite.contains(MERCHANT_1));
        assertTrue(merchantsBySite.contains(MERCHANT_2));
    }
}
