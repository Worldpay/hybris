package com.worldpay.service;

import com.worldpay.exception.WorldpayException;
import com.worldpay.service.request.*;
import com.worldpay.service.request.transform.ServiceRequestTransformer;
import com.worldpay.service.response.*;
import com.worldpay.service.response.transform.ServiceResponseTransformer;

/**
 * This class forms the main gateway for users into the Worldpay back-end systems.
 * <p/>
 * <p>There are convenience methods for making the following calls through to Worldpay:
 * <ul>
 * <li>Direct Authorise - To be used to authorise funds if your site is PCI compliant. Expects the payment details to be provided along with all the other authorisation
 * details like amount, shopper and shipping details. Use the convenience methods in {@link DirectAuthoriseServiceRequest} to create the request. The same call should be used
 * to pass in the 3D details if 3D authentication is needed.</li>
 * <li>Redirect Authorise - To be used to initialise the authorisation of funds if your site is not PCI compliant. Relevant payment methods can be passed in as a
 * payment method mask either as an include list, an exclude list or both. If mac validation is enabled then this is also carried out to ensure the mac code in the returned
 * url has not been tampered with. Use the convenience method in {@link RedirectAuthoriseServiceRequest} to create the request.</li>
 * <li>Capture - To be used to capture the funds. Provide the order code and the amount. Use the convenience method in {@link CaptureServiceRequest} to create the request.</li>
 * <li>Cancel - To be used to cancel the authorisation of funds. Cannot be invoked once the capture has taken place. Simply provide the order code. Use the convenience method in
 * {@link CancelServiceRequest} to create the request.</li>
 * <li>Refund - To be used to refund the captured funds. Any amount up to the original authorised amount can be refunded. Provide the order code and the amount to be refunded.
 * Use the convenience method in {@link RefundServiceRequest} to create the request.</li>
 * <li>Add Back Office Code - To be used to add the back office code. Provide the order code and the back office code. Use the convenience method in {@link AddBackOfficeCodeServiceRequest}
 * to create the request.</li>
 * <li>Authorisation Code - To be used to add the authorisation code. Provide the order code and the authorisation code. Use the convenience method in {@link AuthorisationCodeServiceRequest}
 * to create the request. </li>
 * <li>Order Inquiry - To be used to make inquiry against an order and find out the latest status in Worldpay. Provide the order code. Use the convenience method in
 * {@link OrderInquiryServiceResponse} to create the request.</li>
 * </ul>
 * Each call follows roughly the same steps of transforming the {@link ServiceRequest} object into an internal model representation of the payment service.
 * Then validating that this will produce well formed xml against the schema. Sending this xml to Worldpay. Receiving the response xml and building this into
 * an internal model representation of the reply. Finally the internal model is transformed back into a {@link ServiceResponse} to be returned to any implementers</p>
 * <p/>
 * <p>The same framework can be extended by injecting a {@link ServiceRequestTransformer} into the {@code requestTransformerStrategyMap}, and a {@link ServiceResponseTransformer}
 * into the {@code responseTransformerStrategyMap}. This allows further methods to be added and still use all the underlying framework for transforming, validating, sending
 * and receiving the xml</p>
 */
public interface WorldpayServiceGateway {

    /**
     * Make an authorisation request when using the direct payment model. The same method is also used for validating the 3D PaResponse if this is needed
     *
     * @param request Use the convenience methods in {@link DirectAuthoriseServiceRequest} to create the request depending on the action that is required. Ensures
     *                the correct details are set before making the call
     * @return {@link DirectAuthoriseServiceResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    DirectAuthoriseServiceResponse directAuthorise(DirectAuthoriseServiceRequest request) throws WorldpayException;

    /**
     * Make an authorisation request when using the redirect payment model
     *
     * @param request Use the convenience method in {@link RedirectAuthoriseServiceRequest} to create the request. Ensures the correct details are set before making
     *                the call
     * @return {@link RedirectAuthoriseServiceResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    RedirectAuthoriseServiceResponse redirectAuthorise(RedirectAuthoriseServiceRequest request) throws WorldpayException;

    /**
     * Create a token in Worldpay
     *
     * @param request Use the convenience method in {@link CreateTokenServiceRequest} to create the tokenRequest.
     * @return {@link CreateTokenResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    CreateTokenResponse createToken(CreateTokenServiceRequest request) throws WorldpayException;

    /**
     * Updates a token in Worldpay
     *
     * @param request Use the convenience method in {@link UpdateTokenServiceRequest} to create the tokenRequest.
     * @return {@link UpdateTokenResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    UpdateTokenResponse updateToken(UpdateTokenServiceRequest request) throws WorldpayException;

    /**
     * Delete a token in Worldpay
     *
     * @param request Use the convenience method in {@link DeleteTokenServiceRequest} to create the tokenRequest.
     * @return {@link DeleteTokenResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    DeleteTokenResponse deleteToken(DeleteTokenServiceRequest request) throws WorldpayException;

    /**
     * Make a capture request with Worldpay
     *
     * @param request Use the convenience method in {@link CaptureServiceRequest} to create the request. Ensures the correct details are set before making the call
     * @return {@link CaptureServiceResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    CaptureServiceResponse capture(CaptureServiceRequest request) throws WorldpayException;

    /**
     * Make a cancel request with Worldpay. Can only be invoked if the funds have not yet been captured
     *
     * @param request Use the convenience method in {@link CancelServiceRequest} to create the request. Ensures the correct details are set before making the call
     * @return {@link CancelServiceResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    CancelServiceResponse cancel(CancelServiceRequest request) throws WorldpayException;

    /**
     * Make a void request with Worldpay
     *
     * @param request Use the convenience method in {@link VoidSaleServiceRequest} to create the request. Ensures the correct details are set before making the call
     * @return {@link CancelServiceResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    VoidSaleServiceResponse voidSale(VoidSaleServiceRequest request) throws WorldpayException;

    /**
     * Make a refund request with Worldpay. Can only be invoked if the funds have been captured
     *
     * @param request Use the convenience method in {@link RefundServiceRequest} to create the request. Ensures the correct details are set before making the call
     * @return {@link RefundServiceResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    RefundServiceResponse refund(RefundServiceRequest request) throws WorldpayException;

    /**
     * Make an add back office code request with Worldpay
     *
     * @param request Use the convenience method in {@link AddBackOfficeCodeServiceRequest} to create the request. Ensures the correct details are set before making the call
     * @return {@link AddBackOfficeCodeServiceResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    AddBackOfficeCodeServiceResponse addBackOfficeCode(AddBackOfficeCodeServiceRequest request) throws WorldpayException;

    /**
     * Make an authorisation code request with Worldpay
     *
     * @param request Use the convenience method in {@link AuthorisationCodeServiceRequest} to create the request. Ensures the correct details are set before making the call
     * @return {@link AuthorisationCodeServiceResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    AuthorisationCodeServiceResponse authorisationCode(AuthorisationCodeServiceRequest request) throws WorldpayException;

    /**
     * Make an order inquiry request with Worldpay
     *
     * @param request Use the convenience method in {@link AbstractServiceRequest} to create the request. Ensures the correct details are set before making the call
     * @return {@link OrderInquiryServiceResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    OrderInquiryServiceResponse orderInquiry(AbstractServiceRequest request) throws WorldpayException;

    /**
     * Makes a second 3D secure request to Worldpay
     *
     * @param request Use the convenience method in {@link SecondThreeDSecurePaymentRequest} to create the request. Ensures the correct details are set before making the call
     * @return {@link DirectAuthoriseServiceResponse} object with the reply details.
     * @throws WorldpayException if there have been issues making the request
     */
    DirectAuthoriseServiceResponse sendSecondThreeDSecurePayment(SecondThreeDSecurePaymentRequest request) throws WorldpayException;
}
