package com.worldpay.service.request.transform;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.Order;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Submit;
import com.worldpay.service.request.AuthoriseServiceRequest;
import com.worldpay.service.request.ServiceRequest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.springframework.beans.factory.annotation.Required;

/**
 * Specific class for transforming an {@link AuthoriseServiceRequest} into a {@link PaymentService} object
 * <p/>
 * <p>The external model objects each know how to transform themselves into an internal model object representation. This class adds the surrounding classes that are required
 * to generate xml in the form:
 * <pre>
 *  &lt;paymentService merchantCode="MYMERCHANT" version="1.4"&gt;
 *      &lt;submit&gt;
 *          &lt;order orderCode="T0211010"&gt;
 *              &lt;description&gt;20 English roses from MYMERCHANT Webshop&lt;/description&gt;
 *              &lt;amount value="2600" currencyCode="GBP" exponent="2"/&gt;
 *              &lt;orderContent&gt;
 *                  &lt;![CDATA[&lt;center&gt;&lt;table&gt;&lt;tr&gt;&lt;td class="one width190" align="left" valign="top"&gt;&lt;span style=" font-family: Arial, Helvetica, sans-serif; font-size: 12pt; color:#002469;"&gt;
 *                           Product:&lt;/span&gt;&nbsp;&nbsp;&lt;/td&gt;&lt;tr&gt;&lt;td class="one" align="left" valign="top"&gt;&lt;span style=" font-family: Arial, Helvetica, sans-serif; font-size: 12pt; color: #002469;"&gt;
 *                           &lt;strong&gt;Product title&lt;/strong&gt;&lt;/span&gt;&lt;/td&gt;&lt;/tr&gt;&lt;/table&gt;&lt;/center&gt;]]&gt;
 *              &lt;/orderContent&gt;
 *              &lt;paymentMethodMask&gt;
 *                  &lt;include code="ALL"/&gt;
 *                  &lt;exclude code="AMEX-SSL"/&gt;
 *              &lt;/paymentMethodMask&gt;
 *              &lt;shopper&gt;
 *                  &lt;shopperEmailAddress&gt;jshopper@myprovider.int&lt;/shopperEmailAddress&gt;
 *              &lt;/shopper&gt;
 *              &lt;shippingAddress&gt;
 *                  &lt;address&gt;
 *                      &lt;firstName&gt;John&lt;/firstName&gt;
 *                      &lt;lastName&gt;Shopper&lt;/lastName&gt;
 *                      &lt;address1&gt;Shopperstreet&lt;/address1&gt;
 *                      &lt;address2&gt;Shopperaddress2&lt;/address2&gt;
 *                      &lt;address3&gt;Shopperaddress3&gt;&lt;/address3&gt;
 *                      &lt;postalCode&gt;1234&lt;/postalCode&gt;
 *                      &lt;city&gt;Shoppercity&lt;/city&gt;
 *                      &lt;countryCode&gt;NL&lt;/countryCode&gt;
 *                      &lt;telephoneNumber&gt;0123456789&lt;/telephoneNumber&gt;
 *                  &lt;/address&gt;
 *              &lt;/shippingAddress&gt;
 *              &lt;statementNarrative&gt;STATEMENT NARRATIVE TEXT&lt;/statementNarrative&gt;
 *          &lt;/order&gt;
 *      &lt;/submit&gt;
 *  &lt;/paymentService&gt;
 * </pre>
 * </p>
 */
public class AuthoriseRequestTransformer implements ServiceRequestTransformer {
    private static final String WORLDPAY_CONFIG_VERSION = "worldpay.config.version";
    private ConfigurationService configurationService;

    /**
     * (non-Javadoc)
     *
     * @see com.worldpay.service.request.transform.ServiceRequestTransformer#transform(ServiceRequest)
     */
    @Override
    public PaymentService transform(ServiceRequest request) throws WorldpayModelTransformationException {
        if (request == null || request.getMerchantInfo() == null || request.getOrderCode() == null) {
            throw new WorldpayModelTransformationException("Request provided to do the authorise is invalid.");
        }
        final AuthoriseServiceRequest authRequest = (AuthoriseServiceRequest) request;

        final PaymentService paymentService = new PaymentService();
        paymentService.setMerchantCode(request.getMerchantInfo().getMerchantCode());
        paymentService.setVersion(configurationService.getConfiguration().getString(WORLDPAY_CONFIG_VERSION));

        if (authRequest.getOrder() == null) {
            throw new WorldpayModelTransformationException("No order object to transform on the authorise request");
        }
        final Submit submit = new Submit();
        final Order order = (Order) authRequest.getOrder().transformToInternalModel();
        submit.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreateOrChallenge().add(order);
        paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(submit);
        return paymentService;
    }

    @Required
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
