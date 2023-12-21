package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.forms.ThreeDSecureForm;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CheckoutFacade;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.order.InvalidCartException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.worldpay.payment.TransactionStatus.AUTHORISED;
import static com.worldpay.payment.TransactionStatus.ERROR;
import static java.text.MessageFormat.format;

/**
 * Controller to handle 3D secure response
 */
@Controller
@RequestMapping(value = "/checkout/multi/worldpay/3dsecure/sop/response")
@SuppressWarnings("java:S110")
public class WorldpayB2BThreeDSecureEndpointController extends WorldpayChoosePaymentMethodCheckoutStepController {

    private static final Logger LOG = LogManager.getLogger(WorldpayB2BThreeDSecureEndpointController.class);

    @Resource
    protected WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacade;
    @Resource
    private WorldpayDirectOrderFacade worldpayDirectOrderFacade;
    @Resource
    private WorldpayCartService worldpayCartService;

    /**
     * Method to handle 3D secure response
     *
     * @param request
     * @param threeDSecureForm
     * @param redirectAttributes
     * @return
     */
    @PostMapping
    @RequireHardLogIn
    public String doHandleThreeDSecureResponse(final HttpServletRequest request, final ThreeDSecureForm threeDSecureForm, final RedirectAttributes redirectAttributes) {
        final String worldpayOrderCode = threeDSecureForm.getMD();
        TransactionStatus transactionStatus = ERROR;
        try {
            final DirectResponseData responseData = worldpayDirectOrderFacade.authorise3DSecure(threeDSecureForm.getPaRes(),
                worldpayAdditionalInfoFacade.createWorldpayAdditionalInfoData(request));
            transactionStatus = responseData.getTransactionStatus();
            if (AUTHORISED.equals(transactionStatus)) {
                final PlaceOrderData placeOrderData = new PlaceOrderData();

                placeOrderData.setTermsCheck(true);
                final OrderData orderData = getB2BCheckoutFacade().placeOrder(placeOrderData);
                return redirectToOrderConfirmationPage(orderData);
            } else {
                LOG.error("Failed to create payment authorisation for successful 3DSecure response. Received [{}] as transactionStatus", transactionStatus);
                worldpayCartService.setWorldpayDeclineCodeOnCart(worldpayOrderCode, responseData.getReturnCode());
            }
        } catch (final WorldpayException | InvalidCartException e) {
            LOG.error(format("There was an error processing the 3d secure payment for order with worldpayOrderCode [{0}]", worldpayOrderCode), e);
        }
        redirectAttributes.addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, transactionStatus.toString());
        return REDIRECT_URL_CHOOSE_PAYMENT_METHOD + "?" + PAYMENT_STATUS_PARAMETER_NAME + "=" + transactionStatus;
    }

    protected CheckoutFacade getB2BCheckoutFacade() {
        return (CheckoutFacade) this.getCheckoutFacade();
    }
}
