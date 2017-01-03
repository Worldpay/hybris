package com.worldpay.controllers.pages;

import com.worldpay.core.services.WorldpayHybrisOrderService;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayB2BDirectOrderFacade;
import com.worldpay.forms.AcceptQuoteForm;
import com.worldpay.forms.ThreeDSecureForm;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import com.worldpay.service.WorldpayAddonEndpointService;
import com.worldpay.service.WorldpayUrlService;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.util.XSSFilterUtil;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.b2bacceleratoraddon.controllers.pages.MyQuotesController;
import de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static com.worldpay.payment.TransactionStatus.*;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages.addErrorMessage;
import static java.text.MessageFormat.format;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@Controller
@RequestMapping(value = "/my-account/worldpay")
public class WorldpayQuotesController extends MyQuotesController
{
	private static final Logger LOG = LoggerFactory.getLogger(WorldpayQuotesController.class);

	protected static final String REDIRECT_MY_ACCOUNT = REDIRECT_PREFIX + "/my-account";
	protected static final String REDIRECT_TO_QUOTES_DETAILS = REDIRECT_PREFIX + "/my-account/my-quote/%s";
	protected static final String MY_QUOTES_CMS_PAGE = "my-quotes";
	protected static final String ACCEPTQUOTE = "ACCEPTQUOTE";
	protected static final String TERM_URL_PARAM_NAME = "termURL";
	protected static final String PA_REQUEST_PARAM_NAME = "paRequest";
	protected static final String ISSUER_URL_PARAM_NAME = "issuerURL";
	protected static final String MERCHANT_DATA_PARAM_NAME = "merchantData";
	protected static final String CHECKOUT_MULTI_WORLD_PAY_DECLINED_MESSAGE_DEFAULT = "checkout.multi.worldpay.declined.message.default";
	protected static final String CHECKOUT_MULTI_WORLD_PAY_DECLINED_MESSAGE = "checkout.multi.worldpay.declined.message.";

	@Resource(name = "b2bOrderFacade")
	private B2BOrderFacade orderFacade;
	@Resource(name = "worldpayDirectOrderFacade")
	private WorldpayB2BDirectOrderFacade worldpayB2BDirectOrderFacade;
	@Resource
	private WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacade;
	@Resource
	private UiExperienceService uiExperienceService;
	@Resource
	private WorldpayUrlService worldpayUrlService;
	@Resource
	private MessageSource themeSource;
	@Resource
	private WorldpayHybrisOrderService worldpayHybrisOrderService;
	@Resource
	private B2BOrderService b2BOrderService;
	@Resource
	private WorldpayAddonEndpointService worldpayAddonEndpointService;

	@RequestMapping(value = "/quote/acceptQuoteOrder")
	@RequireHardLogIn
	public String acceptQuoteOrder(@ModelAttribute("acceptQuoteOrderDecisionForm") final AcceptQuoteForm acceptQuoteForm,
			final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {

		storeCmsPageInModel(model, getContentPageForLabelOrId(MY_QUOTES_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MY_QUOTES_CMS_PAGE));
		try {
			final String orderCode = acceptQuoteForm.getOrderCode();
			final String securityCode = XSSFilterUtil.filter(acceptQuoteForm.getSecurityCode());

			final OrderData orderDetails = orderFacade.getOrderDetailsForCode(orderCode);
			final Date quoteExpirationDate = orderDetails.getQuoteExpirationDate();
			if (quoteExpirationDate != null && quoteExpirationDate.before(new Date())) {
				GlobalMessages.addErrorMessage(model, "text.quote.expired");
				return quotesDetails(orderCode, model);
			}

			if (orderDetails.getPaymentInfo() != null) {
				final WorldpayAdditionalInfoData worldpayAdditionalInfoData = getWorldpayAdditionalInfo(request, securityCode);
				try {
					final DirectResponseData directResponseData = worldpayB2BDirectOrderFacade.authoriseRecurringPayment(orderCode, worldpayAdditionalInfoData);
					if (AUTHORISED != directResponseData.getTransactionStatus()) {
						return handleDirectResponse(orderCode, model, directResponseData);
					}
				} catch (WorldpayException | InvalidCartException e) {
					LOG.error("There was an error authorising the transaction", e);
					addErrorMessage(model, "checkout.placeOrder.failed");
					return quotesDetails(orderCode, model);
				}
			}

			orderFacade.createAndSetNewOrderFromApprovedQuote(orderCode, null);
			return REDIRECT_PREFIX + "/checkout/orderConfirmation/" + orderCode;
		}
		catch (final UnknownIdentifierException e) {
			LOG.warn("Attempted to load a order that does not exist or is not visible", e);
			return REDIRECT_MY_ACCOUNT;
		}
	}

	@RequestMapping (value = "/3dsecure/sop/response", method = POST)
	@RequireHardLogIn
	public String doHandleThreeDSecureResponse(final RedirectAttributes redirectAttributes, final Model model, final HttpServletRequest request, final ThreeDSecureForm threeDSecureForm) throws CMSItemNotFoundException {
		final String worldpayOrderCode = threeDSecureForm.getMD();
		final String orderCode = worldpayHybrisOrderService.findOrderCodeByWorldpayOrderCode(worldpayOrderCode);

		try {
			final DirectResponseData responseData = worldpayB2BDirectOrderFacade.authorise3DSecureOnOrder(orderCode, threeDSecureForm.getPaRes(),
					worldpayAdditionalInfoFacade.createWorldpayAdditionalInfoData(request));
			TransactionStatus transactionStatus = responseData.getTransactionStatus();
			if (AUTHORISED.equals(transactionStatus)) {
				orderFacade.createAndSetNewOrderFromApprovedQuote(orderCode, null);
				return REDIRECT_PREFIX + "/checkout/orderConfirmation/" + orderCode;
			} else {
				LOG.error(format("Failed to create payment authorisation for successful 3DSecure response. Received {0} as transactionStatus", transactionStatus));
				worldpayHybrisOrderService.setWorldpayDeclineCodeOnOrder(worldpayOrderCode, responseData.getReturnCode());
				GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER, getLocalisedDeclineMessage(responseData.getReturnCode()));
			}
		} catch (WorldpayException | InvalidCartException e) {
			LOG.error(format("There was an error processing the 3d secure payment for order with worldpayOrderCode [{0}]", worldpayOrderCode), e);
		}
		return getErrorView(orderCode);
	}

	protected WorldpayAdditionalInfoData getWorldpayAdditionalInfo(final HttpServletRequest request,
																 final String securityCode) {
		final WorldpayAdditionalInfoData info = worldpayAdditionalInfoFacade.createWorldpayAdditionalInfoData(request);
		info.setUiExperienceLevel(uiExperienceService.getUiExperienceLevel());
		info.setSecurityCode(securityCode);
		return info;
	}

	protected String handleDirectResponse(final String orderCode, final Model model, final DirectResponseData directResponseData) throws CMSItemNotFoundException, WorldpayConfigurationException {
		if (AUTHENTICATION_REQUIRED == directResponseData.getTransactionStatus()) {
			model.addAttribute(ISSUER_URL_PARAM_NAME, directResponseData.getIssuerURL());
			model.addAttribute(PA_REQUEST_PARAM_NAME, directResponseData.getPaRequest());
			model.addAttribute(TERM_URL_PARAM_NAME, worldpayUrlService.getFullThreeDSecureQuoteTermURL());
			model.addAttribute(MERCHANT_DATA_PARAM_NAME, b2BOrderService.getOrderForCode(orderCode).getWorldpayOrderCode());
			return worldpayAddonEndpointService.getAutoSubmit3DSecure();
		} else if (CANCELLED == directResponseData.getTransactionStatus()) {
			GlobalMessages.addErrorMessage(model, CHECKOUT_MULTI_WORLD_PAY_DECLINED_MESSAGE_DEFAULT);
			return quotesDetails(orderCode, model);
		} else {
			GlobalMessages.addErrorMessage(model, getLocalisedDeclineMessage(directResponseData.getReturnCode()));
			return quotesDetails(orderCode, model);
		}
	}

	protected String getLocalisedDeclineMessage(final String returnCode) {
		return themeSource.getMessage(CHECKOUT_MULTI_WORLD_PAY_DECLINED_MESSAGE + returnCode, null, getI18nService().getCurrentLocale());
	}

	protected String getErrorView(final String orderCode) throws CMSItemNotFoundException {
		return String.format(REDIRECT_TO_QUOTES_DETAILS, orderCode);
	}

}
