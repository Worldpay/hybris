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
import de.hybris.platform.jalo.c2l.Currency;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Generated class for type {@link com.worldpay.jalo.WorldpayCurrencyRange WorldpayCurrencyRange}.
 */
@SuppressWarnings({"deprecation","unused","cast","PMD"})
public abstract class GeneratedWorldpayCurrencyRange extends GenericItem
{
	/** Qualifier of the <code>WorldpayCurrencyRange.currency</code> attribute **/
	public static final String CURRENCY = "currency";
	/** Qualifier of the <code>WorldpayCurrencyRange.min</code> attribute **/
	public static final String MIN = "min";
	/** Qualifier of the <code>WorldpayCurrencyRange.max</code> attribute **/
	public static final String MAX = "max";
	protected static final Map<String, AttributeMode> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>();
		tmp.put(CURRENCY, AttributeMode.INITIAL);
		tmp.put(MIN, AttributeMode.INITIAL);
		tmp.put(MAX, AttributeMode.INITIAL);
		DEFAULT_INITIAL_ATTRIBUTES = Collections.unmodifiableMap(tmp);
	}
	@Override
	protected Map<String, AttributeMode> getDefaultAttributeModes()
	{
		return DEFAULT_INITIAL_ATTRIBUTES;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayCurrencyRange.currency</code> attribute.
	 * @return the currency - Contains the currency code
	 */
	public Currency getCurrency(final SessionContext ctx)
	{
		return (Currency)getProperty( ctx, CURRENCY);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayCurrencyRange.currency</code> attribute.
	 * @return the currency - Contains the currency code
	 */
	public Currency getCurrency()
	{
		return getCurrency( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayCurrencyRange.currency</code> attribute. 
	 * @param value the currency - Contains the currency code
	 */
	public void setCurrency(final SessionContext ctx, final Currency value)
	{
		setProperty(ctx, CURRENCY,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayCurrencyRange.currency</code> attribute. 
	 * @param value the currency - Contains the currency code
	 */
	public void setCurrency(final Currency value)
	{
		setCurrency( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayCurrencyRange.max</code> attribute.
	 * @return the max - Contains the maximum allowed amount
	 */
	public Double getMax(final SessionContext ctx)
	{
		return (Double)getProperty( ctx, MAX);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayCurrencyRange.max</code> attribute.
	 * @return the max - Contains the maximum allowed amount
	 */
	public Double getMax()
	{
		return getMax( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayCurrencyRange.max</code> attribute. 
	 * @return the max - Contains the maximum allowed amount
	 */
	public double getMaxAsPrimitive(final SessionContext ctx)
	{
		Double value = getMax( ctx );
		return value != null ? value.doubleValue() : 0.0d;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayCurrencyRange.max</code> attribute. 
	 * @return the max - Contains the maximum allowed amount
	 */
	public double getMaxAsPrimitive()
	{
		return getMaxAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayCurrencyRange.max</code> attribute. 
	 * @param value the max - Contains the maximum allowed amount
	 */
	public void setMax(final SessionContext ctx, final Double value)
	{
		setProperty(ctx, MAX,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayCurrencyRange.max</code> attribute. 
	 * @param value the max - Contains the maximum allowed amount
	 */
	public void setMax(final Double value)
	{
		setMax( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayCurrencyRange.max</code> attribute. 
	 * @param value the max - Contains the maximum allowed amount
	 */
	public void setMax(final SessionContext ctx, final double value)
	{
		setMax( ctx,Double.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayCurrencyRange.max</code> attribute. 
	 * @param value the max - Contains the maximum allowed amount
	 */
	public void setMax(final double value)
	{
		setMax( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayCurrencyRange.min</code> attribute.
	 * @return the min - Contains the minimum allowed amount
	 */
	public Double getMin(final SessionContext ctx)
	{
		return (Double)getProperty( ctx, MIN);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayCurrencyRange.min</code> attribute.
	 * @return the min - Contains the minimum allowed amount
	 */
	public Double getMin()
	{
		return getMin( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayCurrencyRange.min</code> attribute. 
	 * @return the min - Contains the minimum allowed amount
	 */
	public double getMinAsPrimitive(final SessionContext ctx)
	{
		Double value = getMin( ctx );
		return value != null ? value.doubleValue() : 0.0d;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayCurrencyRange.min</code> attribute. 
	 * @return the min - Contains the minimum allowed amount
	 */
	public double getMinAsPrimitive()
	{
		return getMinAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayCurrencyRange.min</code> attribute. 
	 * @param value the min - Contains the minimum allowed amount
	 */
	public void setMin(final SessionContext ctx, final Double value)
	{
		setProperty(ctx, MIN,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayCurrencyRange.min</code> attribute. 
	 * @param value the min - Contains the minimum allowed amount
	 */
	public void setMin(final Double value)
	{
		setMin( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayCurrencyRange.min</code> attribute. 
	 * @param value the min - Contains the minimum allowed amount
	 */
	public void setMin(final SessionContext ctx, final double value)
	{
		setMin( ctx,Double.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayCurrencyRange.min</code> attribute. 
	 * @param value the min - Contains the minimum allowed amount
	 */
	public void setMin(final double value)
	{
		setMin( getSession().getSessionContext(), value );
	}
	
}
