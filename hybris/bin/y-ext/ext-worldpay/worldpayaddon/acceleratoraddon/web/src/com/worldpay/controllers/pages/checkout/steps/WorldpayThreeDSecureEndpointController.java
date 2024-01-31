package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.forms.ThreeDSecureForm;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.order.InvalidCartException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.worldpay.payment.TransactionStatus.AUTHORISED;
import static com.worldpay.payment.TransactionStatus.ERROR;

/**
 * Web controller ot handle a secure endpoint in a checkout step
 */
@Controller
@RequestMapping(value = "/checkout/multi/worldpay/3dsecure/sop/response")
@SuppressWarnings("java:S110")
public class WorldpayThreeDSecureEndpointController extends WorldpayChoosePaymentMethodCheckoutStepController {

    private static final Logger LOG = LoggerFactory.getLogger(WorldpayThreeDSecureEndpointController.class);

    @Resource
    protected WorldpayDirectOrderFacade worldpayDirectOrderFacade;
    @Resource
    protected WorldpayCartService worldpayCartService;
    @Resource
    protected WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacade;

    /**
     * @param request            the {@HttpServletRequest} to be used
     * @param threeDSecureForm   the ThreeDSecureForm to be used
     * @param redirectAttributes the {@RedirectAttributes} to be used
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
                return redirectToOrderConfirmationPage(responseData.getOrderData());
            } else {
                LOG.error("Failed to create payment authorisation for successful 3DSecure response. Received {} as transactionStatus", transactionStatus);
                worldpayCartService.setWorldpayDeclineCodeOnCart(worldpayOrderCode, responseData.getReturnCode());
            }
        } catch (WorldpayException | InvalidCartException e) {
            LOG.error("There was an error processing the 3d secure payment for order with worldpayOrderCode [{}]", worldpayOrderCode, e);
        }
        redirectAttributes.addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, transactionStatus.toString());
        return REDIRECT_URL_CHOOSE_PAYMENT_METHOD + "?" + PAYMENT_STATUS_PARAMETER_NAME + "=" + transactionStatus;
    }
}
