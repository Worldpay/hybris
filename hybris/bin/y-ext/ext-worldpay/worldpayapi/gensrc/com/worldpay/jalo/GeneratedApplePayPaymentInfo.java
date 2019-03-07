/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at 07-Mar-2019 15:29:51                        ---
 * ----------------------------------------------------------------
 */
package com.worldpay.jalo;

import com.worldpay.constants.WorldpayapiConstants;
import de.hybris.platform.jalo.Item.AttributeMode;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.order.payment.PaymentInfo;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Generated class for type {@link de.hybris.platform.jalo.order.payment.PaymentInfo ApplePayPaymentInfo}.
 */
@SuppressWarnings({"deprecation","unused","cast","PMD"})
public abstract class GeneratedApplePayPaymentInfo extends PaymentInfo
{
	/** Qualifier of the <code>ApplePayPaymentInfo.transactionId</code> attribute **/
	public static final String TRANSACTIONID = "transactionId";
	/** Qualifier of the <code>ApplePayPaymentInfo.version</code> attribute **/
	public static final String VERSION = "version";
	protected static final Map<String, AttributeMode> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>(PaymentInfo.DEFAULT_INITIAL_ATTRIBUTES);
		tmp.put(TRANSACTIONID, AttributeMode.INITIAL);
		tmp.put(VERSION, AttributeMode.INITIAL);
		DEFAULT_INITIAL_ATTRIBUTES = Collections.unmodifiableMap(tmp);
	}
	@Override
	protected Map<String, AttributeMode> getDefaultAttributeModes()
	{
		return DEFAULT_INITIAL_ATTRIBUTES;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ApplePayPaymentInfo.transactionId</code> attribute.
	 * @return the transactionId
	 */
	public String getTransactionId(final SessionContext ctx)
	{
		return (String)getProperty( ctx, TRANSACTIONID);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ApplePayPaymentInfo.transactionId</code> attribute.
	 * @return the transactionId
	 */
	public String getTransactionId()
	{
		return getTransactionId( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ApplePayPaymentInfo.transactionId</code> attribute. 
	 * @param value the transactionId
	 */
	public void setTransactionId(final SessionContext ctx, final String value)
	{
		setProperty(ctx, TRANSACTIONID,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ApplePayPaymentInfo.transactionId</code> attribute. 
	 * @param value the transactionId
	 */
	public void setTransactionId(final String value)
	{
		setTransactionId( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ApplePayPaymentInfo.version</code> attribute.
	 * @return the version
	 */
	public String getVersion(final SessionContext ctx)
	{
		return (String)getProperty( ctx, VERSION);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ApplePayPaymentInfo.version</code> attribute.
	 * @return the version
	 */
	public String getVersion()
	{
		return getVersion( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ApplePayPaymentInfo.version</code> attribute. 
	 * @param value the version
	 */
	public void setVersion(final SessionContext ctx, final String value)
	{
		setProperty(ctx, VERSION,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ApplePayPaymentInfo.version</code> attribute. 
	 * @param value the version
	 */
	public void setVersion(final String value)
	{
		setVersion( getSession().getSessionContext(), value );
	}
	
}
