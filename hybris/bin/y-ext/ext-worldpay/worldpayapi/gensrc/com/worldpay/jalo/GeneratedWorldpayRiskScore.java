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
 * Generated class for type {@link com.worldpay.jalo.WorldpayRiskScore WorldpayRiskScore}.
 */
@SuppressWarnings({"deprecation","unused","cast","PMD"})
public abstract class GeneratedWorldpayRiskScore extends GenericItem
{
	/** Qualifier of the <code>WorldpayRiskScore.code</code> attribute **/
	public static final String CODE = "code";
	/** Qualifier of the <code>WorldpayRiskScore.value</code> attribute **/
	public static final String VALUE = "value";
	/** Qualifier of the <code>WorldpayRiskScore.provider</code> attribute **/
	public static final String PROVIDER = "provider";
	/** Qualifier of the <code>WorldpayRiskScore.id</code> attribute **/
	public static final String ID = "id";
	/** Qualifier of the <code>WorldpayRiskScore.finalScore</code> attribute **/
	public static final String FINALSCORE = "finalScore";
	/** Qualifier of the <code>WorldpayRiskScore.rgid</code> attribute **/
	public static final String RGID = "rgid";
	/** Qualifier of the <code>WorldpayRiskScore.tScore</code> attribute **/
	public static final String TSCORE = "tScore";
	/** Qualifier of the <code>WorldpayRiskScore.tRisk</code> attribute **/
	public static final String TRISK = "tRisk";
	/** Qualifier of the <code>WorldpayRiskScore.message</code> attribute **/
	public static final String MESSAGE = "message";
	/** Qualifier of the <code>WorldpayRiskScore.extendedResponse</code> attribute **/
	public static final String EXTENDEDRESPONSE = "extendedResponse";
	protected static final Map<String, AttributeMode> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>();
		tmp.put(CODE, AttributeMode.INITIAL);
		tmp.put(VALUE, AttributeMode.INITIAL);
		tmp.put(PROVIDER, AttributeMode.INITIAL);
		tmp.put(ID, AttributeMode.INITIAL);
		tmp.put(FINALSCORE, AttributeMode.INITIAL);
		tmp.put(RGID, AttributeMode.INITIAL);
		tmp.put(TSCORE, AttributeMode.INITIAL);
		tmp.put(TRISK, AttributeMode.INITIAL);
		tmp.put(MESSAGE, AttributeMode.INITIAL);
		tmp.put(EXTENDEDRESPONSE, AttributeMode.INITIAL);
		DEFAULT_INITIAL_ATTRIBUTES = Collections.unmodifiableMap(tmp);
	}
	@Override
	protected Map<String, AttributeMode> getDefaultAttributeModes()
	{
		return DEFAULT_INITIAL_ATTRIBUTES;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.code</code> attribute.
	 * @return the code
	 */
	public String getCode(final SessionContext ctx)
	{
		return (String)getProperty( ctx, CODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.code</code> attribute.
	 * @return the code
	 */
	public String getCode()
	{
		return getCode( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.code</code> attribute. 
	 * @param value the code
	 */
	public void setCode(final SessionContext ctx, final String value)
	{
		setProperty(ctx, CODE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.code</code> attribute. 
	 * @param value the code
	 */
	public void setCode(final String value)
	{
		setCode( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.extendedResponse</code> attribute.
	 * @return the extendedResponse
	 */
	public String getExtendedResponse(final SessionContext ctx)
	{
		return (String)getProperty( ctx, EXTENDEDRESPONSE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.extendedResponse</code> attribute.
	 * @return the extendedResponse
	 */
	public String getExtendedResponse()
	{
		return getExtendedResponse( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.extendedResponse</code> attribute. 
	 * @param value the extendedResponse
	 */
	public void setExtendedResponse(final SessionContext ctx, final String value)
	{
		setProperty(ctx, EXTENDEDRESPONSE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.extendedResponse</code> attribute. 
	 * @param value the extendedResponse
	 */
	public void setExtendedResponse(final String value)
	{
		setExtendedResponse( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.finalScore</code> attribute.
	 * @return the finalScore
	 */
	public Double getFinalScore(final SessionContext ctx)
	{
		return (Double)getProperty( ctx, FINALSCORE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.finalScore</code> attribute.
	 * @return the finalScore
	 */
	public Double getFinalScore()
	{
		return getFinalScore( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.finalScore</code> attribute. 
	 * @return the finalScore
	 */
	public double getFinalScoreAsPrimitive(final SessionContext ctx)
	{
		Double value = getFinalScore( ctx );
		return value != null ? value.doubleValue() : 0.0d;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.finalScore</code> attribute. 
	 * @return the finalScore
	 */
	public double getFinalScoreAsPrimitive()
	{
		return getFinalScoreAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.finalScore</code> attribute. 
	 * @param value the finalScore
	 */
	public void setFinalScore(final SessionContext ctx, final Double value)
	{
		setProperty(ctx, FINALSCORE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.finalScore</code> attribute. 
	 * @param value the finalScore
	 */
	public void setFinalScore(final Double value)
	{
		setFinalScore( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.finalScore</code> attribute. 
	 * @param value the finalScore
	 */
	public void setFinalScore(final SessionContext ctx, final double value)
	{
		setFinalScore( ctx,Double.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.finalScore</code> attribute. 
	 * @param value the finalScore
	 */
	public void setFinalScore(final double value)
	{
		setFinalScore( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.id</code> attribute.
	 * @return the id
	 */
	public String getId(final SessionContext ctx)
	{
		return (String)getProperty( ctx, ID);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.id</code> attribute.
	 * @return the id
	 */
	public String getId()
	{
		return getId( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.id</code> attribute. 
	 * @param value the id
	 */
	public void setId(final SessionContext ctx, final String value)
	{
		setProperty(ctx, ID,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.id</code> attribute. 
	 * @param value the id
	 */
	public void setId(final String value)
	{
		setId( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.message</code> attribute.
	 * @return the message
	 */
	public String getMessage(final SessionContext ctx)
	{
		return (String)getProperty( ctx, MESSAGE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.message</code> attribute.
	 * @return the message
	 */
	public String getMessage()
	{
		return getMessage( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.message</code> attribute. 
	 * @param value the message
	 */
	public void setMessage(final SessionContext ctx, final String value)
	{
		setProperty(ctx, MESSAGE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.message</code> attribute. 
	 * @param value the message
	 */
	public void setMessage(final String value)
	{
		setMessage( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.provider</code> attribute.
	 * @return the provider
	 */
	public String getProvider(final SessionContext ctx)
	{
		return (String)getProperty( ctx, PROVIDER);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.provider</code> attribute.
	 * @return the provider
	 */
	public String getProvider()
	{
		return getProvider( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.provider</code> attribute. 
	 * @param value the provider
	 */
	public void setProvider(final SessionContext ctx, final String value)
	{
		setProperty(ctx, PROVIDER,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.provider</code> attribute. 
	 * @param value the provider
	 */
	public void setProvider(final String value)
	{
		setProvider( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.rgid</code> attribute.
	 * @return the rgid
	 */
	public Long getRgid(final SessionContext ctx)
	{
		return (Long)getProperty( ctx, RGID);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.rgid</code> attribute.
	 * @return the rgid
	 */
	public Long getRgid()
	{
		return getRgid( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.rgid</code> attribute. 
	 * @return the rgid
	 */
	public long getRgidAsPrimitive(final SessionContext ctx)
	{
		Long value = getRgid( ctx );
		return value != null ? value.longValue() : 0;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.rgid</code> attribute. 
	 * @return the rgid
	 */
	public long getRgidAsPrimitive()
	{
		return getRgidAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.rgid</code> attribute. 
	 * @param value the rgid
	 */
	public void setRgid(final SessionContext ctx, final Long value)
	{
		setProperty(ctx, RGID,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.rgid</code> attribute. 
	 * @param value the rgid
	 */
	public void setRgid(final Long value)
	{
		setRgid( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.rgid</code> attribute. 
	 * @param value the rgid
	 */
	public void setRgid(final SessionContext ctx, final long value)
	{
		setRgid( ctx,Long.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.rgid</code> attribute. 
	 * @param value the rgid
	 */
	public void setRgid(final long value)
	{
		setRgid( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.tRisk</code> attribute.
	 * @return the tRisk
	 */
	public Double getTRisk(final SessionContext ctx)
	{
		return (Double)getProperty( ctx, TRISK);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.tRisk</code> attribute.
	 * @return the tRisk
	 */
	public Double getTRisk()
	{
		return getTRisk( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.tRisk</code> attribute. 
	 * @return the tRisk
	 */
	public double getTRiskAsPrimitive(final SessionContext ctx)
	{
		Double value = getTRisk( ctx );
		return value != null ? value.doubleValue() : 0.0d;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.tRisk</code> attribute. 
	 * @return the tRisk
	 */
	public double getTRiskAsPrimitive()
	{
		return getTRiskAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.tRisk</code> attribute. 
	 * @param value the tRisk
	 */
	public void setTRisk(final SessionContext ctx, final Double value)
	{
		setProperty(ctx, TRISK,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.tRisk</code> attribute. 
	 * @param value the tRisk
	 */
	public void setTRisk(final Double value)
	{
		setTRisk( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.tRisk</code> attribute. 
	 * @param value the tRisk
	 */
	public void setTRisk(final SessionContext ctx, final double value)
	{
		setTRisk( ctx,Double.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.tRisk</code> attribute. 
	 * @param value the tRisk
	 */
	public void setTRisk(final double value)
	{
		setTRisk( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.tScore</code> attribute.
	 * @return the tScore
	 */
	public Double getTScore(final SessionContext ctx)
	{
		return (Double)getProperty( ctx, TSCORE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.tScore</code> attribute.
	 * @return the tScore
	 */
	public Double getTScore()
	{
		return getTScore( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.tScore</code> attribute. 
	 * @return the tScore
	 */
	public double getTScoreAsPrimitive(final SessionContext ctx)
	{
		Double value = getTScore( ctx );
		return value != null ? value.doubleValue() : 0.0d;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.tScore</code> attribute. 
	 * @return the tScore
	 */
	public double getTScoreAsPrimitive()
	{
		return getTScoreAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.tScore</code> attribute. 
	 * @param value the tScore
	 */
	public void setTScore(final SessionContext ctx, final Double value)
	{
		setProperty(ctx, TSCORE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.tScore</code> attribute. 
	 * @param value the tScore
	 */
	public void setTScore(final Double value)
	{
		setTScore( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.tScore</code> attribute. 
	 * @param value the tScore
	 */
	public void setTScore(final SessionContext ctx, final double value)
	{
		setTScore( ctx,Double.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.tScore</code> attribute. 
	 * @param value the tScore
	 */
	public void setTScore(final double value)
	{
		setTScore( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.value</code> attribute.
	 * @return the value
	 */
	public String getValue(final SessionContext ctx)
	{
		return (String)getProperty( ctx, VALUE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayRiskScore.value</code> attribute.
	 * @return the value
	 */
	public String getValue()
	{
		return getValue( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.value</code> attribute. 
	 * @param value the value
	 */
	public void setValue(final SessionContext ctx, final String value)
	{
		setProperty(ctx, VALUE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayRiskScore.value</code> attribute. 
	 * @param value the value
	 */
	public void setValue(final String value)
	{
		setValue( getSession().getSessionContext(), value );
	}
	
}
