package com.worldpay.worldpayresponsemock.controllers.pages;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.exception.WorldpayException;
import com.worldpay.worldpayresponsemock.controllers.WorldpayResponseMockControllerConstants;
import com.worldpay.worldpayresponsemock.form.ResponseForm;
import com.worldpay.worldpayresponsemock.merchant.WorldpayResponseMockMerchantInfoService;
import com.worldpay.worldpayresponsemock.mock.WorldpayMockConnector;
import com.worldpay.worldpayresponsemock.responses.WorldpayNotificationResponseBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Calendar.*;

/**
 * Controller for page to post mocked order modifications into hybris.
 * With this mock you can emulate order changes in Worldpay
 */
@Controller
public class WorldpayOrderModificationMockController {

    protected static final String FRAUD_SIGHT_MESSAGES = "fraudSightMessages";
    protected static final String FRAUD_SIGHT_REASON_CODES = "fraudSightReasonCodes";
    protected static final String GUARANTEED_PAYMENTS_MESSAGES = "guaranteedPaymentsMessages";
    protected static final String GUARANTEED_PAYMENTS_TRIGGERED_RULES = "guaranteedPaymentsTriggeredRules";
    protected static final int END_INDEX = 4;
    protected static final int START_INDEX = 6;
    protected static final String XML_RESPONSE = "xmlResponse";
    protected static final String DEFAULT_MERCHANT_CODE = "MERCHANT1ECOM";
    protected static final String DEFAULT_ORDER_CODE = "11111-22222222";
    protected static final String DEFAULT_CARD_MONTH = "01";
    protected static final String DEFAULT_CARD_YEAR = "2022";
    protected static final String DEFAULT_CURRENCY_CODE = "GBP";
    protected static final String DEFAULT_RISK_SCORE = "1.00";
    protected static final String DEFAULT_TRANSACTION_AMOUNT = "9985";
    protected static final String RESPONSE_CODES = "responseCodes";
    protected static final String TEST_CREDIT_CARDS = "testCreditCards";
    protected static final String PAYMENT_METHODS = "paymentMethods";
    protected static final String NUMERIC_PATTERN = "[0-9]";
    protected static final String FINAL_SCORE = "19";
    protected static final String DEFAULT_CARD_HOLDER_NAME = "aaa bbb";
    protected static final String CREDIT_CARD = "CC";
    protected static final String PAYMENT_METHOD_APMS = "paymentMethodAPMs";
    protected static final String MERCHANTS = "merchants";
    protected static final String POSSIBLE_EVENTS = "possibleEvents";
    protected static final String DEFAULT_AAV_ADDRESS = "B";
    protected static final String DEFAULT_AAV_CARDHOLDER_NAME = "B";
    protected static final String DEFAULT_AAV_EMAIL = "B";
    protected static final String DEFAULT_AAV_POSTCODE = "B";
    protected static final String DEFAULT_AAV_TELEPHONE = "B";
    protected static final String TOKEN_EVENTS = "tokenEvents";
    protected static final String DEFAULT_TOKEN_REASON = "tokenReason";
    protected static final String DEFAULT_LAST_NAME = "lastName";
    protected static final String DEFAULT_ADDRESS_1 = "default address 1";
    protected static final String DEFAULT_ADDRESS_2 = "default Address 2";
    protected static final String DEFAULT_ADDRESS_3 = "default address 3";
    protected static final String DEFAULT_POST_CODE = "postCode";
    protected static final String DEFAULT_CITY = "city";
    protected static final String DEFAULT_COUNTRY_CODE = "GB";
    protected static final String DEFAULT_SUB_BRAND = "subBrand";
    protected static final String DEFAULT_STORED_CREDENTIALS = "noStoredCredentials";

    @Resource
    private WorldpayNotificationResponseBuilder worldpayNotificationResponseBuilder;

    @Resource
    private Map<Integer, String> iso8583ResponseCodes;

    @Resource
    private Map<String, String> worldpayCreditCards;

    @Resource
    private Map<String, String> worldpayPaymentMethods;

    @Resource
    private APMConfigurationLookupService apmConfigurationLookupService;

    @Resource
    private WorldpayResponseMockMerchantInfoService worldpayResponseMockMerchantInfoService;

    @Resource
    private Set<String> possibleEvents;

    @Resource
    private Set<String> tokenEvents;

    @Resource
    private Set<String> fraudSightMessages;

    @Resource
    private Set<String> fraudSightReasonCodes;

    @Resource
    private WorldpayMockConnector worldpayMockConnector;

    @Resource
    private Set<String> guaranteedPaymentsMessages;

    @Resource
    private Set<String> guaranteedPaymentsTriggeredRules;

    /**
     * Get form for mocked order modifications
     *
     * @param model
     * @return
     */
    @GetMapping(value = "/responses")
    public ModelAndView getAllAnswers(ModelMap model) {
        populateModel(model);
        return new ModelAndView(WorldpayResponseMockControllerConstants.Pages.Views.RESPONSES, "responseForm", getResponseForm());
    }

    private ResponseForm getResponseForm() {
        final ResponseForm responseForm = new ResponseForm();
        responseForm.setWorldpayOrderCode(DEFAULT_ORDER_CODE);
        responseForm.setMerchantCode(DEFAULT_MERCHANT_CODE);
        responseForm.setCardHolderName(DEFAULT_CARD_HOLDER_NAME);
        responseForm.setCardMonth(DEFAULT_CARD_MONTH);
        responseForm.setCardYear(DEFAULT_CARD_YEAR);
        responseForm.setCurrencyCode(DEFAULT_CURRENCY_CODE);
        final Calendar calendar = getInstance();
        responseForm.setCurrentDay(String.valueOf(calendar.get(DAY_OF_MONTH)));
        responseForm.setCurrentMonth(String.valueOf(calendar.get(MONTH) + 1));
        responseForm.setCurrentYear(String.valueOf(calendar.get(YEAR)));
        responseForm.setRiskValue(DEFAULT_RISK_SCORE);
        responseForm.setTransactionAmount(DEFAULT_TRANSACTION_AMOUNT);
        responseForm.setFinalScore(FINAL_SCORE);
        responseForm.setSelectedPaymentMethod(CREDIT_CARD);
        responseForm.setAavAddress(DEFAULT_AAV_ADDRESS);
        responseForm.setAavCardholderName(DEFAULT_AAV_CARDHOLDER_NAME);
        responseForm.setAavEmail(DEFAULT_AAV_EMAIL);
        responseForm.setAavPostcode(DEFAULT_AAV_POSTCODE);
        responseForm.setAavTelephone(DEFAULT_AAV_TELEPHONE);
        responseForm.setTokenReason(DEFAULT_TOKEN_REASON);
        responseForm.setTokenCardHolderName(DEFAULT_CARD_HOLDER_NAME);
        responseForm.setLastName(DEFAULT_LAST_NAME);
        responseForm.setAddress1(DEFAULT_ADDRESS_1);
        responseForm.setAddress2(DEFAULT_ADDRESS_2);
        responseForm.setAddress3(DEFAULT_ADDRESS_3);
        responseForm.setPostalCode(DEFAULT_POST_CODE);
        responseForm.setCity(DEFAULT_CITY);
        responseForm.setCountryCode(DEFAULT_COUNTRY_CODE);
        responseForm.setCardSubBrand(DEFAULT_SUB_BRAND);
        responseForm.setIssuerCountry(DEFAULT_COUNTRY_CODE);
        responseForm.setSelectStoredCredentials(DEFAULT_STORED_CREDENTIALS);

        return responseForm;
    }

    /**
     * Post the mocked order modification into hybris
     *
     * @param responseForm
     * @param model
     * @param request
     * @return
     * @throws WorldpayException
     */
    @PostMapping(value = "/responses")
    public String sendResponse(final ResponseForm responseForm, ModelMap model, final HttpServletRequest request) throws WorldpayException {
        responseForm.setResponseDescription(iso8583ResponseCodes.get(responseForm.getResponseCode()));
        responseForm.setTestCreditCard(maskCreditCardNumber(responseForm.getTestCreditCard()));
        responseForm.setObfuscatedPAN(maskCreditCardNumber(responseForm.getObfuscatedPAN()));
        final String responseXML = worldpayNotificationResponseBuilder.buildResponse(responseForm);

        model.put(XML_RESPONSE, worldpayNotificationResponseBuilder.prettifyXml(responseXML));
        populateModel(model);
        worldpayMockConnector.sendResponse(request, responseXML);
        return WorldpayResponseMockControllerConstants.Pages.Views.RESPONSES;
    }

    /**
     * Get all configured merchants
     *
     * @return
     */
    @GetMapping(value = "/merchants")
    @ResponseBody
    public Set<String> getMerchants() {
        return worldpayResponseMockMerchantInfoService.getAllMerchantCodes();
    }

    protected String maskCreditCardNumber(final String creditCardNumber) {
        final int len = creditCardNumber.length();
        final Pattern pattern = Pattern.compile(NUMERIC_PATTERN, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(creditCardNumber.substring(START_INDEX, len - END_INDEX));

        final String mask = matcher.replaceAll("*");
        return creditCardNumber.substring(0, START_INDEX) + mask + creditCardNumber.substring(len - END_INDEX, len);
    }

    protected void populateModel(final ModelMap model) {
        final Set<String> allApmPaymentTypeCodes = apmConfigurationLookupService.getAllApmPaymentTypeCodes();
        // For testing non configured APMs. Could be adjusted for a Non-configured APM in your system.
        allApmPaymentTypeCodes.add("EKONTO-SSL");
        model.put(PAYMENT_METHOD_APMS, allApmPaymentTypeCodes);
        model.put(RESPONSE_CODES, iso8583ResponseCodes);
        model.put(TEST_CREDIT_CARDS, worldpayCreditCards);
        model.put(PAYMENT_METHODS, worldpayPaymentMethods);
        model.put(POSSIBLE_EVENTS, possibleEvents);
        model.put(TOKEN_EVENTS, tokenEvents);
        model.put(MERCHANTS, worldpayResponseMockMerchantInfoService.getAllMerchantCodes());
        model.put(FRAUD_SIGHT_MESSAGES, fraudSightMessages);
        model.put(FRAUD_SIGHT_REASON_CODES, fraudSightReasonCodes);
        model.put(GUARANTEED_PAYMENTS_MESSAGES, guaranteedPaymentsMessages);
        model.put(GUARANTEED_PAYMENTS_TRIGGERED_RULES, guaranteedPaymentsTriggeredRules);
    }
}
