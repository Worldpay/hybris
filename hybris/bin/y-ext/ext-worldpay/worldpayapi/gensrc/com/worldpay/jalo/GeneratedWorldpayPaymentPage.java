/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at 07-Mar-2019 15:29:51                        ---
 * ----------------------------------------------------------------
 */
package com.worldpay.jalo;

import com.worldpay.constants.WorldpayapiConstants;
import de.hybris.platform.cms2.jalo.pages.ContentPage;
import de.hybris.platform.jalo.Item.AttributeMode;
import de.hybris.platform.jalo.SessionContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Generated class for type {@link com.worldpay.jalo.WorldpayPaymentPage WorldpayPaymentPage}.
 */
@SuppressWarnings({"deprecation","unused","cast","PMD"})
public abstract class GeneratedWorldpayPaymentPage extends ContentPage
{
	/** Qualifier of the <code>WorldpayPaymentPage.debug</code> attribute **/
	public static final String DEBUG = "debug";
	protected static final Map<String, AttributeMode> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>(ContentPage.DEFAULT_INITIAL_ATTRIBUTES);
		tmp.put(DEBUG, AttributeMode.INITIAL);
		DEFAULT_INITIAL_ATTRIBUTES = Collections.unmodifiableMap(tmp);
	}
	@Override
	protected Map<String, AttributeMode> getDefaultAttributeModes()
	{
		return DEFAULT_INITIAL_ATTRIBUTES;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayPaymentPage.debug</code> attribute.
	 * @return the debug
	 */
	public Boolean isDebug(final SessionContext ctx)
	{
		return (Boolean)getProperty( ctx, DEBUG);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayPaymentPage.debug</code> attribute.
	 * @return the debug
	 */
	public Boolean isDebug()
	{
		return isDebug( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayPaymentPage.debug</code> attribute. 
	 * @return the debug
	 */
	public boolean isDebugAsPrimitive(final SessionContext ctx)
	{
		Boolean value = isDebug( ctx );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayPaymentPage.debug</code> attribute. 
	 * @return the debug
	 */
	public boolean isDebugAsPrimitive()
	{
		return isDebugAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayPaymentPage.debug</code> attribute. 
	 * @param value the debug
	 */
	public void setDebug(final SessionContext ctx, final Boolean value)
	{
		setProperty(ctx, DEBUG,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayPaymentPage.debug</code> attribute. 
	 * @param value the debug
	 */
	public void setDebug(final Boolean value)
	{
		setDebug( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayPaymentPage.debug</code> attribute. 
	 * @param value the debug
	 */
	public void setDebug(final SessionContext ctx, final boolean value)
	{
		setDebug( ctx,Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayPaymentPage.debug</code> attribute. 
	 * @param value the debug
	 */
	public void setDebug(final boolean value)
	{
		setDebug( getSession().getSessionContext(), value );
	}
	
}
