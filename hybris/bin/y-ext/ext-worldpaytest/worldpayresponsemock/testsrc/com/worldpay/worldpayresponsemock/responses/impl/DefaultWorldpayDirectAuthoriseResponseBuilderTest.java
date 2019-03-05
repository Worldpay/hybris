package com.worldpay.worldpayresponsemock.responses.impl;

import com.worldpay.internal.model.*;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static com.worldpay.worldpayresponsemock.builders.AmountBuilder.anAmountBuilder;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayDirectAuthoriseResponseBuilderTest {

    private static final String AUTHENTICATED_SHOPPER_ID_VALUE = "authenticatedShopperIdValue";
    private static final String MERCHANT_CODE = "merchantCode";
    private static final Amount TRANSACTION_AMOUNT = anAmountBuilder().build();
    private static final PaymentService AUTHORISE_REQUEST = buildAuthRequest(false);
    private static final PaymentService AUTHORISE_REQUEST_WITH_TOKEN = buildAuthRequest(true);
    private static final String AUTHORISED = "AUTHORISED";
    private static final String NEW_TOKEN_EVENT = "NEW";
    private static final String OBFUSCATED_PAN = "4111********1111";
    private static final String VISA_SSL = "VISA-SSL";
    private static final String WORLDPAY_ORDER_CODE = "orderCode";
    private static final String TOKEN_REASON_VALUE = "tokenReasonValue";

    @InjectMocks
    private DefaultWorldpayDirectAuthoriseResponseBuilder testObj;

    @Test
    public void shouldBuildDirectResponseFromPaymentServiceRequest() {
        final PaymentService result = testObj.buildDirectResponse(AUTHORISE_REQUEST);

        final Reply reply = (Reply) result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final OrderStatus orderStatus = (OrderStatus) reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken().get(0);
        final Payment payment = (Payment) orderStatus.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrOrderModificationOrJournalOrRequestInfoOrFxApprovalRequiredOrZappRTPOrContent().get(0);

        assertEquals(WORLDPAY_ORDER_CODE, orderStatus.getOrderCode());
        assertEquals(TRANSACTION_AMOUNT.getValue(), payment.getAmount().getValue());
        assertNull(orderStatus.getToken());
        assertEquals(AUTHORISED, payment.getLastEvent());
    }

    @Test
    public void shouldBuildDirectResponseWithTokenFromPaymentServiceRequest() {

        final PaymentService result = testObj.buildDirectResponse(AUTHORISE_REQUEST_WITH_TOKEN);

        final Reply reply = (Reply) result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final OrderStatus orderStatus = (OrderStatus) reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrCheckCardResponseOrPaymentOptionOrToken().get(0);
        final Payment payment = (Payment) orderStatus.getReferenceOrBankAccountOrApmEnrichedDataOrErrorOrPaymentOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrOrderModificationOrJournalOrRequestInfoOrFxApprovalRequiredOrZappRTPOrContent().get(0);
        final Token token = orderStatus.getToken();

        final List<Object> tokenElements = token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrError();
        for (Object tokenElement : tokenElements) {
            if (tokenElement instanceof TokenDetails) {
                TokenDetails tokenDetails = (TokenDetails) tokenElement;
                assertNotNull(tokenDetails.getPaymentTokenID());
                assertEquals(TOKEN_REASON_VALUE, tokenDetails.getTokenReason().getvalue());
                assertEquals(NEW_TOKEN_EVENT, tokenDetails.getTokenEvent());
            }

            if (tokenElement instanceof PaymentInstrument) {
                PaymentInstrument paymentInstrument = (PaymentInstrument) tokenElement;
                final CardDetails cardDetails = ((CardDetails) paymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetails().get(0));
                assertNotNull(cardDetails);
                assertNotNull(cardDetails.getCardAddress());
                assertEquals(OBFUSCATED_PAN, cardDetails.getDerived().getObfuscatedPAN());
                assertEquals(VISA_SSL, cardDetails.getDerived().getCardBrand());
            }
        }

        assertEquals(AUTHENTICATED_SHOPPER_ID_VALUE, token.getAuthenticatedShopperID());
        assertThat(tokenElements, hasItems(instanceOf(TokenDetails.class), instanceOf(PaymentInstrument.class)));
        assertEquals(WORLDPAY_ORDER_CODE, orderStatus.getOrderCode());
        assertEquals(TRANSACTION_AMOUNT.getValue(), payment.getAmount().getValue());
        assertEquals(AUTHORISED, payment.getLastEvent());
        assertNotNull(token);
    }

    private static PaymentService buildAuthRequest(final boolean withToken) {
        final Order order = new Order();
        order.setOrderCode(WORLDPAY_ORDER_CODE);
        final List<Object> orderElements = order.getDescriptionOrAmountOrRiskOrOrderContentOrPaymentMethodMaskOrPaymentDetailsOrPayAsOrderOrShopperOrShippingAddressOrBillingAddressOrBranchSpecificExtensionOrRedirectPageAttributeOrPaymentMethodAttributeOrEchoDataOrStatementNarrativeOrHcgAdditionalDataOrThirdPartyDataOrResultURLOrShopperAdditionalDataOrApprovedAmountOrMandateOrAuthorisationAmountStatusOrDynamic3DSOrCreateTokenOrCreateTokenApprovalOrOrderLinesOrSubMerchantDataOrDynamicMCCOrDynamicInteractionTypeOrPrimeRoutingRequestOrRiskDataOrAdditional3DSDataOrInfo3DSecureOrSession();
        orderElements.add(TRANSACTION_AMOUNT);
        if (withToken) {
            final CreateToken createToken = new CreateToken();
            final TokenReason tokenReason = new TokenReason();
            tokenReason.setvalue(TOKEN_REASON_VALUE);
            createToken.setTokenReason(tokenReason);
            orderElements.add(createToken);
        }

        final Shopper shopper = new Shopper();
        shopper.setAuthenticatedShopperID(AUTHENTICATED_SHOPPER_ID_VALUE);

        final Submit submit = new Submit();
        submit.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate().add(order);
        submit.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate().add(shopper);

        final PaymentService paymentService = new PaymentService();
        paymentService.setMerchantCode(MERCHANT_CODE);
        paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(submit);
        return paymentService;
    }
}
