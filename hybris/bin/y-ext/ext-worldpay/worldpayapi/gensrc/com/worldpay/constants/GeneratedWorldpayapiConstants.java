/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at 07-Mar-2019 15:29:51                        ---
 * ----------------------------------------------------------------
 */
package com.worldpay.constants;

/**
 * @deprecated since ages - use constants in Model classes instead
 */
@Deprecated
@SuppressWarnings({"unused","cast","PMD"})
public class GeneratedWorldpayapiConstants
{
	public static final String EXTENSIONNAME = "worldpayapi";
	public static class TC
	{
		public static final String APPLEPAYPAYMENTINFO = "ApplePayPaymentInfo".intern();
		public static final String GOOGLEPAYPAYMENTINFO = "GooglePayPaymentInfo".intern();
		public static final String WORLDPAYAAVRESPONSE = "WorldpayAavResponse".intern();
		public static final String WORLDPAYAPMCONFIGURATION = "WorldpayAPMConfiguration".intern();
		public static final String WORLDPAYAPMPAYMENTINFO = "WorldpayAPMPaymentInfo".intern();
		public static final String WORLDPAYBANKCONFIGURATION = "WorldpayBankConfiguration".intern();
		public static final String WORLDPAYCURRENCYRANGE = "WorldpayCurrencyRange".intern();
		public static final String WORLDPAYPAYMENTPAGE = "WorldpayPaymentPage".intern();
		public static final String WORLDPAYRISKSCORE = "WorldpayRiskScore".intern();
	}
	public static class Attributes
	{
		public static class AbstractOrder
		{
			public static final String WORLDPAYDECLINECODE = "worldpayDeclineCode".intern();
			public static final String WORLDPAYORDERCODE = "worldpayOrderCode".intern();
		}
		public static class Cart
		{
			public static final String SHOPPERBANKCODE = "shopperBankCode".intern();
		}
		public static class PaymentInfo
		{
			public static final String AUTHENTICATEDSHOPPERID = "authenticatedShopperID".intern();
			public static final String EVENTREFERENCE = "eventReference".intern();
			public static final String EXPIRYDATE = "expiryDate".intern();
			public static final String ISAPM = "isApm".intern();
			public static final String MERCHANTID = "merchantId".intern();
			public static final String PAYMENTTYPE = "paymentType".intern();
			public static final String WORLDPAYORDERCODE = "worldpayOrderCode".intern();
		}
		public static class PaymentTransaction
		{
			public static final String APMOPEN = "apmOpen".intern();
			public static final String RISKSCORE = "riskScore".intern();
			public static final String WORLDPAYBANK = "worldpayBank".intern();
		}
		public static class PaymentTransactionEntry
		{
			public static final String AAVRESPONSE = "aavResponse".intern();
			public static final String PENDING = "pending".intern();
		}
		public static class ReturnRequest
		{
			public static final String PAYMENTTRANSACTIONENTRY = "paymentTransactionEntry".intern();
		}
		public static class WorldpayAPMPaymentInfo
		{
			public static final String APMCONFIGURATION = "apmConfiguration".intern();
			public static final String TIMEOUTDATE = "timeoutDate".intern();
		}
	}
	public static class Enumerations
	{
		public static class CreditCardType
		{
			public static final String JCB = "jcb".intern();
			public static final String UATP = "uatp".intern();
			public static final String GE_CAPITAL = "ge_capital".intern();
			public static final String DISCOVER = "discover".intern();
			public static final String DANKORT = "dankort".intern();
			public static final String CARTEBLEUE = "cartebleue".intern();
			public static final String CB = "cb".intern();
			public static final String AURORE = "aurore".intern();
			public static final String AIRPLUS = "airplus".intern();
			public static final String CARD = "card".intern();
			public static final String TOKEN = "token".intern();
		}
		public static class OrderStatus
		{
			public static final String PAYMENT_PENDING = "PAYMENT_PENDING".intern();
		}
		public static class PaymentTransactionType
		{
			public static final String SETTLED = "SETTLED".intern();
		}
	}
	
	protected GeneratedWorldpayapiConstants()
	{
		// private constructor
	}
	
	
}
