package com.worldpay.controllers.pages.checkout;

import com.worldpay.controllers.pages.checkout.steps.WorldpayChoosePaymentMethodCheckoutStepController;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.forms.CSEPaymentForm;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.GuestForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.GuestValidator;
import de.hybris.platform.acceleratorstorefrontcommons.security.GUIDCookieStrategy;
import de.hybris.platform.acceleratorstorefrontcommons.util.AddressDataUtil;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.order.InvalidCartException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Web controller to handle a CSE checkout step
 */
@RestController
@RequestMapping(value = "/checkout/worldpay/payment/api")
public class WorldpayPaymentAPIController extends WorldpayChoosePaymentMethodCheckoutStepController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorldpayPaymentAPIController.class);

    @Resource
    protected Validator cseFormValidator;
    @Resource
    protected WorldpayDirectOrderFacade worldpayDirectOrderFacade;
    @Resource
    protected WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade;
    @Resource(name = "addressDataUtil")
    protected AddressDataUtil addressDataUtil;
    @Resource
    protected WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacade;
    @Resource(name = "guestValidator")
    protected GuestValidator guestValidator;
    @Resource(name = "guidCookieStrategy")
    protected GUIDCookieStrategy guidCookieStrategy;
    @Resource
    protected WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacade;

    /**
     * Validates and saves form details on submit of the payment details form for Client Side Encryption flow and redirects to payment form
     *
     * @param addressForm
     * @param bindingResult
     * @return The view to redirect to.
     */
    @PostMapping(value = "/payment-address")
    public AddressData addPaymentAddress(final AddressForm addressForm, final BindingResult bindingResult) {
        getAddressValidator().validate(addressForm, bindingResult);

        if (bindingResult.hasGlobalErrors()) {
            return null;
        }
        final AddressData addressData = addressDataUtil.convertToAddressData(addressForm);
        addressData.setEmail(getCheckoutCustomerStrategy().getCurrentUserForCheckout().getContactEmail());
        addressData.setBillingAddress(true);
        addressData.setShippingAddress(false);
        worldpayPaymentCheckoutFacade.setBillingDetails(addressData);
        return addressData;
    }

    @PostMapping(value = "/guest")
    public ResponseEntity<String> addGuestEmailAddress(final GuestForm form, final BindingResult bindingResult, final HttpServletRequest request, final HttpServletResponse response) {
        guestValidator.validate(form, bindingResult);
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            getCustomerFacade().createGuestUserForAnonymousCheckout(form.getEmail(),
                    getMessageSource().getMessage("text.guest.customer", null, getI18nService().getCurrentLocale()));
            guidCookieStrategy.setCookie(request, response);
            getSessionService().setAttribute(WebConstants.ANONYMOUS_CHECKOUT, Boolean.TRUE);
        } catch (final DuplicateUidException e) {
            LOGGER.warn("guest registration failed: ", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * Validates and saves form details on submit of the CSE payment form and authorises valid payment details.
     *
     * @param request
     * @param csePaymentForm
     * @param bindingResult
     * @return
     */
    @PostMapping(value = "/place-order")
    public DirectResponseData addCseData(final HttpServletRequest request, final CSEPaymentForm csePaymentForm, final BindingResult bindingResult) {
        cseFormValidator.validate(csePaymentForm, bindingResult);
        if (bindingResult.hasGlobalErrors()) {
            return createDirectResponseDataWithError();
        }
        final CSEAdditionalAuthInfo cseAdditionalAuthInfo = createCSEAdditionalAuthInfo(csePaymentForm);
        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = createWorldpayAdditionalInfo(request, csePaymentForm.getCvc());
        try {
            worldpayDirectOrderFacade.tokenize(cseAdditionalAuthInfo, worldpayAdditionalInfoData);
            return worldpayDirectOrderFacade.authorise(worldpayAdditionalInfoData);
        } catch (final InvalidCartException | WorldpayException e) {
            LOGGER.error("There was an error authorising the transaction", e);
            return createDirectResponseDataWithError();
        }
    }

    @GetMapping(value = "/cse-public-key", produces = "text/plain")
    public String getCsePublicKey() {
        return worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData().getCsePublicKey();
    }

    @GetMapping(value = "/delivery-modes")
    @SuppressWarnings("squid:S1452")
    public List<? extends DeliveryModeData> getDeliveryModes() {
        return getCheckoutFacade().getSupportedDeliveryModes();
    }

    @PostMapping(value = "/delivery-modes/select/{deliveryMethod}")
    public ResponseEntity<String> getDeliveryModes(@PathVariable final String deliveryMethod) {
        if (getCheckoutFacade().setDeliveryMode(deliveryMethod)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/cart")
    public CartData getCurrentCart() {
        return getCheckoutFacade().getCheckoutCart();
    }

    @PostMapping(value = "/delivery-address")
    public AddressData addDeliveryAddress(final AddressForm addressForm, final BindingResult bindingResult) {
        getAddressValidator().validate(addressForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return null;
        }

        final AddressData newAddress = addressDataUtil.convertToAddressData(addressForm);
        newAddress.setShippingAddress(true);
        newAddress.setBillingAddress(false);
        processAddressVisibilityAndDefault(addressForm, newAddress);

        final AddressData previousSelectedAddress = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
        // Set the new address as the selected checkout delivery address
        getCheckoutFacade().setDeliveryAddress(newAddress);
        if (previousSelectedAddress != null && !previousSelectedAddress.isVisibleInAddressBook()) { // temporary address should be removed
            getUserFacade().removeAddress(previousSelectedAddress);
        }

        return newAddress;
    }

    private DirectResponseData createDirectResponseDataWithError() {
        final DirectResponseData responseData = new DirectResponseData();
        responseData.setOrderData(null);
        responseData.setReturnCode("500");
        responseData.setTransactionStatus(TransactionStatus.ERROR);
        return responseData;
    }

    protected void processAddressVisibilityAndDefault(final AddressForm addressForm, final AddressData newAddress) {
        if (addressForm.getSaveInAddressBook() != null) {
            newAddress.setVisibleInAddressBook(addressForm.getSaveInAddressBook());
            if (addressForm.getSaveInAddressBook() && CollectionUtils.isEmpty(getUserFacade().getAddressBook())) {
                newAddress.setDefaultAddress(true);
            }
        } else if (getCheckoutCustomerStrategy().isAnonymousCheckout()) {
            newAddress.setDefaultAddress(true);
            newAddress.setVisibleInAddressBook(true);
        }
    }

    protected WorldpayAdditionalInfoData createWorldpayAdditionalInfo(final HttpServletRequest request, final String cvc) {
        final WorldpayAdditionalInfoData worldpayAdditionalInfo = worldpayAdditionalInfoFacade.createWorldpayAdditionalInfoData(request);
        worldpayAdditionalInfo.setSecurityCode(cvc);
        return worldpayAdditionalInfo;
    }
}
