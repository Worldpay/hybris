package com.worldpay.controllers;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.dto.order.PlaceOrderResponseWsDTO;
import com.worldpay.exception.WorldpayException;
import com.worldpay.exceptions.NoCheckoutCartException;
import com.worldpay.exceptions.ThreeDSecureException;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartModificationDataList;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.commercewebservicescommons.strategies.CartLoaderStrategy;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.validators.CompositeValidator;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.worldpay.payment.TransactionStatus.AUTHORISED;
import static com.worldpay.payment.TransactionStatus.ERROR;
import static java.text.MessageFormat.format;

/**
 * Web Service Controller for placing Worldpay orders. Methods require authentication
 * and are restricted to https channel.
 *
 * @pathparam code Order GUID (Globally Unique Identifier) or order CODE
 * @pathparam userId User identifier or one of the literals below :
 *            <ul>
 *            <li>'current' for currently authenticated user</li>
 *            <li>'anonymous' for anonymous user</li>
 *            </ul>
 */
@Controller
@RequestMapping(value = "/{baseSiteId}")
public class WorldpayOrdersController extends AbstractWorldpayController {

    private static final Logger LOG = Logger.getLogger(WorldpayOrdersController.class);

    // Named like this in order to use the bean definition from ycommercewebservices
    @Resource(name = "commerceWebServicesCartFacade2")
    private CartFacade cartFacade;
    @Resource(name = "cartLoaderStrategy")
    private CartLoaderStrategy cartLoaderStrategy;
    @Resource(name = "worldpayPlaceOrderCartValidator")
    private CompositeValidator placeOrderCartValidator;
    @Resource
    private WorldpayDirectOrderFacade worldpayDirectOrderFacade;
    @Resource
    private WorldpayCartService worldpayCartService;

    /**
     * Authorizes cart and places the order. Response contains the new order data.
     *
     * @param request
     * @param cartId
     * @param fields
     * @param securityCode
     * @formparam cartId Cart code for logged in user, cart GUID for guest checkout
     * @formparam securityCode CCV security code.
     * @queryparam fields Response configuration (list of fields, which should be returned in response)
     * @return Created order data
     * @throws InvalidCartException
     * @throws NoCheckoutCartException
     * @throws WebserviceValidationException
     *            When the cart is not filled properly (e. g. delivery mode is not set, payment method is not set)
     * @security Allowed only for customers, customer managers, clients or trusted clients. Trusted client is able to
     *           impersonate as any customer and place order on his behalf
     */
    @Secured(
            { "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
    @RequestMapping(value = "/users/{userId}/worldpayorders", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PlaceOrderResponseWsDTO placeOrder(final HttpServletRequest request,
                                              @RequestParam final String cartId,
                                              @RequestParam final String securityCode,
                                              @RequestParam(defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
            throws InvalidCartException, WebserviceValidationException, NoCheckoutCartException, WorldpayException {

        cartLoaderStrategy.loadCart(cartId);
        validateCartForPlaceOrder();

        //authorize and placeorder
        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = createWorldpayAdditionalInfo(request, securityCode);
        final DirectResponseData directResponseData = worldpayDirectOrderFacade.authorise(worldpayAdditionalInfoData);

        return handleDirectResponse(directResponseData, fields);
    }

    @Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
    @RequestMapping(value = "/users/{userId}/worldpayorders/3dresponse", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    protected OrderWsDTO doHandleThreeDSecureResponse(final HttpServletRequest request,
                                  @RequestParam final String cartId,
                                  @RequestParam final String paRes,
                                  @RequestParam final String merchantData,
                                  @RequestParam(defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields) throws ThreeDSecureException, NoCheckoutCartException, InvalidCartException {

        cartLoaderStrategy.loadCart(cartId);
        validateCartForPlaceOrder(merchantData);

        TransactionStatus transactionStatus = ERROR;
        try {
            final DirectResponseData responseData = worldpayDirectOrderFacade.authorise3DSecure(paRes,
                    createWorldpayAdditionalInfo(request, null));
            transactionStatus = responseData.getTransactionStatus();
            if (AUTHORISED.equals(transactionStatus)) {
                return dataMapper.map(responseData.getOrderData(), OrderWsDTO.class, fields);
            } else {
                LOG.error(format("Failed to create payment authorisation for successful 3DSecure response. Received {0} as transactionStatus", transactionStatus));
                worldpayCartService.setWorldpayDeclineCodeOnCart(merchantData, responseData.getReturnCode());
            }
        } catch (WorldpayException | InvalidCartException e) {
            LOG.error(format("There was an error processing the 3d secure payment for order with worldpayOrderCode [{0}]", merchantData), e);
        }
        throw new ThreeDSecureException(format("Failed to handle authorisation for 3DSecure. Received {0} as transactionStatus", transactionStatus));
    }

    protected void validateCartForPlaceOrder(final String worldPayOrderCode) throws NoCheckoutCartException, InvalidCartException, WebserviceValidationException {
        validateCartForPlaceOrder();

        final CartData cartData = checkoutFacade.getCheckoutCart();
        if (!worldPayOrderCode.equals(cartData.getWorldpayOrderCode())) {
            throw new InvalidCartException("Cannot place order. Incorrect worldpay order code");
        }
    }

    protected void validateCartForPlaceOrder() throws NoCheckoutCartException, InvalidCartException, WebserviceValidationException {
        if (!checkoutFacade.hasCheckoutCart()) {
            throw new NoCheckoutCartException("Cannot place order. There was no checkout cart created yet!");
        }

        final CartData cartData = checkoutFacade.getCheckoutCart();

        final Errors errors = new BeanPropertyBindingResult(cartData, "sessionCart");
        placeOrderCartValidator.validate(cartData, errors);
        if (errors.hasErrors()) {
            throw new WebserviceValidationException(errors);
        }

        try {
            final List<CartModificationData> modificationList = cartFacade.validateCartData();
            if (modificationList != null && !modificationList.isEmpty()) {
                final CartModificationDataList cartModificationDataList = new CartModificationDataList();
                cartModificationDataList.setCartModificationList(modificationList);
                throw new WebserviceValidationException(cartModificationDataList);
            }
        } catch (final CommerceCartModificationException e) {
            throw new InvalidCartException(e);
        }
    }
}
