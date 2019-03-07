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
 * Generated class for type {@link de.hybris.platform.jalo.order.payment.PaymentInfo GooglePayPaymentInfo}.
 */
@SuppressWarnings({"deprecation","unused","cast","PMD"})
public abstract class GeneratedGooglePayPaymentInfo extends PaymentInfo
{
	/** Qualifier of the <code>GooglePayPaymentInfo.protocolVersion</code> attribute **/
	public static final String PROTOCOLVERSION = "protocolVersion";
	/** Qualifier of the <code>GooglePayPaymentInfo.signature</code> attribute **/
	public static final String SIGNATURE = "signature";
	/** Qualifier of the <code>GooglePayPaymentInfo.signedMessage</code> attribute **/
	public static final String SIGNEDMESSAGE = "signedMessage";
	protected static final Map<String, AttributeMode> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>(PaymentInfo.DEFAULT_INITIAL_ATTRIBUTES);
		tmp.put(PROTOCOLVERSION, AttributeMode.INITIAL);
		tmp.put(SIGNATURE, AttributeMode.INITIAL);
		tmp.put(SIGNEDMESSAGE, AttributeMode.INITIAL);
		DEFAULT_INITIAL_ATTRIBUTES = Collections.unmodifiableMap(tmp);
	}
	@Override
	protected Map<String, AttributeMode> getDefaultAttributeModes()
	{
		return DEFAULT_INITIAL_ATTRIBUTES;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>GooglePayPaymentInfo.protocolVersion</code> attribute.
	 * @return the protocolVersion
	 */
	public String getProtocolVersion(final SessionContext ctx)
	{
		return (String)getProperty( ctx, PROTOCOLVERSION);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>GooglePayPaymentInfo.protocolVersion</code> attribute.
	 * @return the protocolVersion
	 */
	public String getProtocolVersion()
	{
		return getProtocolVersion( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>GooglePayPaymentInfo.protocolVersion</code> attribute. 
	 * @param value the protocolVersion
	 */
	public void setProtocolVersion(final SessionContext ctx, final String value)
	{
		setProperty(ctx, PROTOCOLVERSION,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>GooglePayPaymentInfo.protocolVersion</code> attribute. 
	 * @param value the protocolVersion
	 */
	public void setProtocolVersion(final String value)
	{
		setProtocolVersion( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>GooglePayPaymentInfo.signature</code> attribute.
	 * @return the signature
	 */
	public String getSignature(final SessionContext ctx)
	{
		return (String)getProperty( ctx, SIGNATURE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>GooglePayPaymentInfo.signature</code> attribute.
	 * @return the signature
	 */
	public String getSignature()
	{
		return getSignature( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>GooglePayPaymentInfo.signature</code> attribute. 
	 * @param value the signature
	 */
	public void setSignature(final SessionContext ctx, final String value)
	{
		setProperty(ctx, SIGNATURE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>GooglePayPaymentInfo.signature</code> attribute. 
	 * @param value the signature
	 */
	public void setSignature(final String value)
	{
		setSignature( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>GooglePayPaymentInfo.signedMessage</code> attribute.
	 * @return the signedMessage
	 */
	public String getSignedMessage(final SessionContext ctx)
	{
		return (String)getProperty( ctx, SIGNEDMESSAGE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>GooglePayPaymentInfo.signedMessage</code> attribute.
	 * @return the signedMessage
	 */
	public String getSignedMessage()
	{
		return getSignedMessage( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>GooglePayPaymentInfo.signedMessage</code> attribute. 
	 * @param value the signedMessage
	 */
	public void setSignedMessage(final SessionContext ctx, final String value)
	{
		setProperty(ctx, SIGNEDMESSAGE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>GooglePayPaymentInfo.signedMessage</code> attribute. 
	 * @param value the signedMessage
	 */
	public void setSignedMessage(final String value)
	{
		setSignedMessage( getSession().getSessionContext(), value );
	}
	
}
