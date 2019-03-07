/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at 07-Mar-2019 15:29:51                        ---
 * ----------------------------------------------------------------
 */
package com.worldpay.jalo;

import com.worldpay.constants.WorldpayapiConstants;
import de.hybris.platform.jalo.GenericItem;
import de.hybris.platform.jalo.Item.AttributeMode;
import de.hybris.platform.jalo.SessionContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Generated class for type {@link com.worldpay.jalo.WorldpayAavResponse WorldpayAavResponse}.
 */
@SuppressWarnings({"deprecation","unused","cast","PMD"})
public abstract class GeneratedWorldpayAavResponse extends GenericItem
{
	/** Qualifier of the <code>WorldpayAavResponse.code</code> attribute **/
	public static final String CODE = "code";
	/** Qualifier of the <code>WorldpayAavResponse.aavAddressResultCode</code> attribute **/
	public static final String AAVADDRESSRESULTCODE = "aavAddressResultCode";
	/** Qualifier of the <code>WorldpayAavResponse.aavCardholderNameResultCode</code> attribute **/
	public static final String AAVCARDHOLDERNAMERESULTCODE = "aavCardholderNameResultCode";
	/** Qualifier of the <code>WorldpayAavResponse.aavEmailResultCode</code> attribute **/
	public static final String AAVEMAILRESULTCODE = "aavEmailResultCode";
	/** Qualifier of the <code>WorldpayAavResponse.aavPostcodeResultCode</code> attribute **/
	public static final String AAVPOSTCODERESULTCODE = "aavPostcodeResultCode";
	/** Qualifier of the <code>WorldpayAavResponse.aavTelephoneResultCode</code> attribute **/
	public static final String AAVTELEPHONERESULTCODE = "aavTelephoneResultCode";
	protected static final Map<String, AttributeMode> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>();
		tmp.put(CODE, AttributeMode.INITIAL);
		tmp.put(AAVADDRESSRESULTCODE, AttributeMode.INITIAL);
		tmp.put(AAVCARDHOLDERNAMERESULTCODE, AttributeMode.INITIAL);
		tmp.put(AAVEMAILRESULTCODE, AttributeMode.INITIAL);
		tmp.put(AAVPOSTCODERESULTCODE, AttributeMode.INITIAL);
		tmp.put(AAVTELEPHONERESULTCODE, AttributeMode.INITIAL);
		DEFAULT_INITIAL_ATTRIBUTES = Collections.unmodifiableMap(tmp);
	}
	@Override
	protected Map<String, AttributeMode> getDefaultAttributeModes()
	{
		return DEFAULT_INITIAL_ATTRIBUTES;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAavResponse.aavAddressResultCode</code> attribute.
	 * @return the aavAddressResultCode
	 */
	public String getAavAddressResultCode(final SessionContext ctx)
	{
		return (String)getProperty( ctx, AAVADDRESSRESULTCODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAavResponse.aavAddressResultCode</code> attribute.
	 * @return the aavAddressResultCode
	 */
	public String getAavAddressResultCode()
	{
		return getAavAddressResultCode( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAavResponse.aavAddressResultCode</code> attribute. 
	 * @param value the aavAddressResultCode
	 */
	public void setAavAddressResultCode(final SessionContext ctx, final String value)
	{
		setProperty(ctx, AAVADDRESSRESULTCODE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAavResponse.aavAddressResultCode</code> attribute. 
	 * @param value the aavAddressResultCode
	 */
	public void setAavAddressResultCode(final String value)
	{
		setAavAddressResultCode( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAavResponse.aavCardholderNameResultCode</code> attribute.
	 * @return the aavCardholderNameResultCode
	 */
	public String getAavCardholderNameResultCode(final SessionContext ctx)
	{
		return (String)getProperty( ctx, AAVCARDHOLDERNAMERESULTCODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAavResponse.aavCardholderNameResultCode</code> attribute.
	 * @return the aavCardholderNameResultCode
	 */
	public String getAavCardholderNameResultCode()
	{
		return getAavCardholderNameResultCode( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAavResponse.aavCardholderNameResultCode</code> attribute. 
	 * @param value the aavCardholderNameResultCode
	 */
	public void setAavCardholderNameResultCode(final SessionContext ctx, final String value)
	{
		setProperty(ctx, AAVCARDHOLDERNAMERESULTCODE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAavResponse.aavCardholderNameResultCode</code> attribute. 
	 * @param value the aavCardholderNameResultCode
	 */
	public void setAavCardholderNameResultCode(final String value)
	{
		setAavCardholderNameResultCode( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAavResponse.aavEmailResultCode</code> attribute.
	 * @return the aavEmailResultCode
	 */
	public String getAavEmailResultCode(final SessionContext ctx)
	{
		return (String)getProperty( ctx, AAVEMAILRESULTCODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAavResponse.aavEmailResultCode</code> attribute.
	 * @return the aavEmailResultCode
	 */
	public String getAavEmailResultCode()
	{
		return getAavEmailResultCode( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAavResponse.aavEmailResultCode</code> attribute. 
	 * @param value the aavEmailResultCode
	 */
	public void setAavEmailResultCode(final SessionContext ctx, final String value)
	{
		setProperty(ctx, AAVEMAILRESULTCODE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAavResponse.aavEmailResultCode</code> attribute. 
	 * @param value the aavEmailResultCode
	 */
	public void setAavEmailResultCode(final String value)
	{
		setAavEmailResultCode( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAavResponse.aavPostcodeResultCode</code> attribute.
	 * @return the aavPostcodeResultCode
	 */
	public String getAavPostcodeResultCode(final SessionContext ctx)
	{
		return (String)getProperty( ctx, AAVPOSTCODERESULTCODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAavResponse.aavPostcodeResultCode</code> attribute.
	 * @return the aavPostcodeResultCode
	 */
	public String getAavPostcodeResultCode()
	{
		return getAavPostcodeResultCode( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAavResponse.aavPostcodeResultCode</code> attribute. 
	 * @param value the aavPostcodeResultCode
	 */
	public void setAavPostcodeResultCode(final SessionContext ctx, final String value)
	{
		setProperty(ctx, AAVPOSTCODERESULTCODE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAavResponse.aavPostcodeResultCode</code> attribute. 
	 * @param value the aavPostcodeResultCode
	 */
	public void setAavPostcodeResultCode(final String value)
	{
		setAavPostcodeResultCode( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAavResponse.aavTelephoneResultCode</code> attribute.
	 * @return the aavTelephoneResultCode
	 */
	public String getAavTelephoneResultCode(final SessionContext ctx)
	{
		return (String)getProperty( ctx, AAVTELEPHONERESULTCODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAavResponse.aavTelephoneResultCode</code> attribute.
	 * @return the aavTelephoneResultCode
	 */
	public String getAavTelephoneResultCode()
	{
		return getAavTelephoneResultCode( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAavResponse.aavTelephoneResultCode</code> attribute. 
	 * @param value the aavTelephoneResultCode
	 */
	public void setAavTelephoneResultCode(final SessionContext ctx, final String value)
	{
		setProperty(ctx, AAVTELEPHONERESULTCODE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAavResponse.aavTelephoneResultCode</code> attribute. 
	 * @param value the aavTelephoneResultCode
	 */
	public void setAavTelephoneResultCode(final String value)
	{
		setAavTelephoneResultCode( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAavResponse.code</code> attribute.
	 * @return the code
	 */
	public String getCode(final SessionContext ctx)
	{
		return (String)getProperty( ctx, CODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAavResponse.code</code> attribute.
	 * @return the code
	 */
	public String getCode()
	{
		return getCode( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAavResponse.code</code> attribute. 
	 * @param value the code
	 */
	public void setCode(final SessionContext ctx, final String value)
	{
		setProperty(ctx, CODE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAavResponse.code</code> attribute. 
	 * @param value the code
	 */
	public void setCode(final String value)
	{
		setCode( getSession().getSessionContext(), value );
	}
	
}
