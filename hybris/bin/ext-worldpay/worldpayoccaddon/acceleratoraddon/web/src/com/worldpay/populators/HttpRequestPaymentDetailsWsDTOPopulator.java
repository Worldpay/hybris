package com.worldpay.populators;

import com.worldpay.populator.options.PaymentDetailsWsDTOOption;
import de.hybris.platform.acceleratorservices.payment.cybersource.enums.CardTypeEnum;
import de.hybris.platform.commercefacades.converter.ConfigurablePopulator;
import de.hybris.platform.commercewebservicescommons.dto.order.CardTypeWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;


/**
 * Populates {@link PaymentDetailsWsDTO} instance from the {@link HttpServletRequest} parameters:<br>
 * <ul>
 * <li>id</li>
 * <li>accountHolderName</li>
 * <li>cardNumber</li>
 * <li>cardType</li>
 * <li>expiryMonth</li>
 * <li>expiryYear</li>
 * <li>issueNumber</li>
 * <li>startMonth</li>
 * <li>startYear</li>
 * <li>subscriptionId</li>
 * <li>saved</li>
 * <li>defaultPaymentInfo</li>
 * <li>cseToken</li>
 * </ul>
 * 
 * If populator's options contains {@link PaymentDetailsWsDTOOption#BILLING_ADDRESS}, it uses
 * {@link HttpRequestAddressWsDTOPopulator} with prefix 'billingAddress' to populate also the billing address data from
 * http request request parameters.
 */
@Component("httpRequestPaymentDetailsWsDTOPopulator")
public class HttpRequestPaymentDetailsWsDTOPopulator extends AbstractHttpRequestWsDTOPopulator implements
		ConfigurablePopulator<HttpServletRequest, PaymentDetailsWsDTO, PaymentDetailsWsDTOOption> {

	protected static final String ACCOUNT_HOLDER_NAME = "accountHolderName";
	protected static final String CARD_NUMBER = "cardNumber";
	protected static final String CARD_TYPE = "cardType";
	protected static final String CSE_TOKEN = "cseToken";
	protected static final String EXPIRY_MONTH = "expiryMonth";
	protected static final String EXPIRY_YEAR = "expiryYear";
	protected static final String ISSUE_NUMBER = "issueNumber";
	protected static final String START_MONTH = "startMonth";
	protected static final String START_YEAR = "startYear";
	protected static final String SUBSCRIPTION_ID = "subscriptionId";
	protected static final String ID = "id";
	protected static final String DEFAULT_PAYMENT_INFO = "defaultPaymentInfo";
	protected static final String SAVED = "saved";

	@Resource(name = "httpRequestAddressWsDTOPopulator")
	private HttpRequestAddressWsDTOPopulator httpRequestAddressWsDTOPopulator ;

	@Override
	public void populate(final HttpServletRequest request, final PaymentDetailsWsDTO target,
			final Collection<PaymentDetailsWsDTOOption> options) throws ConversionException {
		target.setAccountHolderName(updateStringValueFromRequest(request, ACCOUNT_HOLDER_NAME, target.getAccountHolderName()));
		target.setCardNumber(updateStringValueFromRequest(request, CARD_NUMBER, target.getCardNumber()));
		target.setCardType(updateCartTypeFromRequest(request, target.getCardType()));
		target.setIssueNumber(updateStringValueFromRequest(request, ISSUE_NUMBER, target.getIssueNumber()));
		target.setCseToken(updateStringValueFromRequest(request, CSE_TOKEN, target.getCseToken()));

		target.setExpiryMonth(updateStringValueFromRequest(request, EXPIRY_MONTH, target.getExpiryMonth()));
		target.setExpiryYear(updateStringValueFromRequest(request, EXPIRY_YEAR, target.getExpiryYear()));
		target.setStartMonth(updateStringValueFromRequest(request, START_MONTH, target.getStartMonth()));
		target.setStartYear(updateStringValueFromRequest(request, START_YEAR, target.getStartYear()));
		target.setSubscriptionId(updateStringValueFromRequest(request, SUBSCRIPTION_ID, target.getSubscriptionId()));
		target.setId(updateStringValueFromRequest(request, ID, target.getId()));
		target.setSaved(updateBooleanValueFromRequest(request, SAVED, target.getSaved()!=null?target.getSaved():false));
		target.setDefaultPayment(updateBooleanValueFromRequest(request, DEFAULT_PAYMENT_INFO, target.getDefaultPayment()!=null?target.getDefaultPayment():false));

		if (options.contains(PaymentDetailsWsDTOOption.BILLING_ADDRESS)) {
			final AddressWsDTO billingAddress = target.getBillingAddress() == null ? new AddressWsDTO() : target.getBillingAddress();
			final HttpRequestAddressWsDTOPopulator billingAddressPopulator = getAddressPopulator();
			billingAddressPopulator.setAddressPrefix("billingAddress");
			billingAddressPopulator.populate(request, billingAddress);
			target.setBillingAddress(billingAddress);
		}
	}

	protected CardTypeWsDTO updateCartTypeFromRequest(HttpServletRequest request, CardTypeWsDTO defaultValue) {
		final String cardType = getRequestParameterValue(request, CARD_TYPE);
		if (StringUtils.isEmpty(cardType)) {
			return defaultValue;
		} else {
			CardTypeEnum enumValue = CardTypeEnum.valueOf(cardType.toLowerCase());
			CardTypeWsDTO cardTypeWsDTO = new CardTypeWsDTO();
			cardTypeWsDTO.setCode(enumValue.name());
			cardTypeWsDTO.setName(enumValue.getStringValue());
			return  cardTypeWsDTO;
		}
	}

	protected HttpRequestAddressWsDTOPopulator getAddressPopulator() {
		return httpRequestAddressWsDTOPopulator;
	}
}
