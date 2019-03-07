/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at 07-Mar-2019 15:29:51                        ---
 * ----------------------------------------------------------------
 */
package com.worldpay.jalo;

import com.worldpay.constants.WorldpayapiConstants;
import com.worldpay.jalo.WorldpayAPMConfiguration;
import de.hybris.platform.jalo.GenericItem;
import de.hybris.platform.jalo.Item.AttributeMode;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Language;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Generated class for type {@link com.worldpay.jalo.WorldpayBankConfiguration WorldpayBankConfiguration}.
 */
@SuppressWarnings({"deprecation","unused","cast","PMD"})
public abstract class GeneratedWorldpayBankConfiguration extends GenericItem
{
	/** Qualifier of the <code>WorldpayBankConfiguration.code</code> attribute **/
	public static final String CODE = "code";
	/** Qualifier of the <code>WorldpayBankConfiguration.apm</code> attribute **/
	public static final String APM = "apm";
	/** Qualifier of the <code>WorldpayBankConfiguration.name</code> attribute **/
	public static final String NAME = "name";
	/** Qualifier of the <code>WorldpayBankConfiguration.description</code> attribute **/
	public static final String DESCRIPTION = "description";
	/** Qualifier of the <code>WorldpayBankConfiguration.active</code> attribute **/
	public static final String ACTIVE = "active";
	protected static final Map<String, AttributeMode> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>();
		tmp.put(CODE, AttributeMode.INITIAL);
		tmp.put(APM, AttributeMode.INITIAL);
		tmp.put(NAME, AttributeMode.INITIAL);
		tmp.put(DESCRIPTION, AttributeMode.INITIAL);
		tmp.put(ACTIVE, AttributeMode.INITIAL);
		DEFAULT_INITIAL_ATTRIBUTES = Collections.unmodifiableMap(tmp);
	}
	@Override
	protected Map<String, AttributeMode> getDefaultAttributeModes()
	{
		return DEFAULT_INITIAL_ATTRIBUTES;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayBankConfiguration.active</code> attribute.
	 * @return the active - Defines if the bank is active or not
	 */
	public Boolean isActive(final SessionContext ctx)
	{
		return (Boolean)getProperty( ctx, ACTIVE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayBankConfiguration.active</code> attribute.
	 * @return the active - Defines if the bank is active or not
	 */
	public Boolean isActive()
	{
		return isActive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayBankConfiguration.active</code> attribute. 
	 * @return the active - Defines if the bank is active or not
	 */
	public boolean isActiveAsPrimitive(final SessionContext ctx)
	{
		Boolean value = isActive( ctx );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayBankConfiguration.active</code> attribute. 
	 * @return the active - Defines if the bank is active or not
	 */
	public boolean isActiveAsPrimitive()
	{
		return isActiveAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayBankConfiguration.active</code> attribute. 
	 * @param value the active - Defines if the bank is active or not
	 */
	public void setActive(final SessionContext ctx, final Boolean value)
	{
		setProperty(ctx, ACTIVE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayBankConfiguration.active</code> attribute. 
	 * @param value the active - Defines if the bank is active or not
	 */
	public void setActive(final Boolean value)
	{
		setActive( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayBankConfiguration.active</code> attribute. 
	 * @param value the active - Defines if the bank is active or not
	 */
	public void setActive(final SessionContext ctx, final boolean value)
	{
		setActive( ctx,Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayBankConfiguration.active</code> attribute. 
	 * @param value the active - Defines if the bank is active or not
	 */
	public void setActive(final boolean value)
	{
		setActive( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayBankConfiguration.apm</code> attribute.
	 * @return the apm - Bank APM
	 */
	public WorldpayAPMConfiguration getApm(final SessionContext ctx)
	{
		return (WorldpayAPMConfiguration)getProperty( ctx, APM);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayBankConfiguration.apm</code> attribute.
	 * @return the apm - Bank APM
	 */
	public WorldpayAPMConfiguration getApm()
	{
		return getApm( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayBankConfiguration.apm</code> attribute. 
	 * @param value the apm - Bank APM
	 */
	public void setApm(final SessionContext ctx, final WorldpayAPMConfiguration value)
	{
		setProperty(ctx, APM,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayBankConfiguration.apm</code> attribute. 
	 * @param value the apm - Bank APM
	 */
	public void setApm(final WorldpayAPMConfiguration value)
	{
		setApm( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayBankConfiguration.code</code> attribute.
	 * @return the code - Bank code
	 */
	public String getCode(final SessionContext ctx)
	{
		return (String)getProperty( ctx, CODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayBankConfiguration.code</code> attribute.
	 * @return the code - Bank code
	 */
	public String getCode()
	{
		return getCode( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayBankConfiguration.code</code> attribute. 
	 * @param value the code - Bank code
	 */
	public void setCode(final SessionContext ctx, final String value)
	{
		setProperty(ctx, CODE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayBankConfiguration.code</code> attribute. 
	 * @param value the code - Bank code
	 */
	public void setCode(final String value)
	{
		setCode( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayBankConfiguration.description</code> attribute.
	 * @return the description - Bank description
	 */
	public String getDescription(final SessionContext ctx)
	{
		if( ctx == null || ctx.getLanguage() == null )
		{
			throw new JaloInvalidParameterException("GeneratedWorldpayBankConfiguration.getDescription requires a session language", 0 );
		}
		return (String)getLocalizedProperty( ctx, DESCRIPTION);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayBankConfiguration.description</code> attribute.
	 * @return the description - Bank description
	 */
	public String getDescription()
	{
		return getDescription( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayBankConfiguration.description</code> attribute. 
	 * @return the localized description - Bank description
	 */
	public Map<Language,String> getAllDescription(final SessionContext ctx)
	{
		return (Map<Language,String>)getAllLocalizedProperties(ctx,DESCRIPTION,C2LManager.getInstance().getAllLanguages());
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayBankConfiguration.description</code> attribute. 
	 * @return the localized description - Bank description
	 */
	public Map<Language,String> getAllDescription()
	{
		return getAllDescription( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayBankConfiguration.description</code> attribute. 
	 * @param value the description - Bank description
	 */
	public void setDescription(final SessionContext ctx, final String value)
	{
		if ( ctx == null) 
		{
			throw new JaloInvalidParameterException( "ctx is null", 0 );
		}
		if( ctx.getLanguage() == null )
		{
			throw new JaloInvalidParameterException("GeneratedWorldpayBankConfiguration.setDescription requires a session language", 0 );
		}
		setLocalizedProperty(ctx, DESCRIPTION,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayBankConfiguration.description</code> attribute. 
	 * @param value the description - Bank description
	 */
	public void setDescription(final String value)
	{
		setDescription( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayBankConfiguration.description</code> attribute. 
	 * @param value the description - Bank description
	 */
	public void setAllDescription(final SessionContext ctx, final Map<Language,String> value)
	{
		setAllLocalizedProperties(ctx,DESCRIPTION,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayBankConfiguration.description</code> attribute. 
	 * @param value the description - Bank description
	 */
	public void setAllDescription(final Map<Language,String> value)
	{
		setAllDescription( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayBankConfiguration.name</code> attribute.
	 * @return the name - Bank name
	 */
	public String getName(final SessionContext ctx)
	{
		if( ctx == null || ctx.getLanguage() == null )
		{
			throw new JaloInvalidParameterException("GeneratedWorldpayBankConfiguration.getName requires a session language", 0 );
		}
		return (String)getLocalizedProperty( ctx, NAME);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayBankConfiguration.name</code> attribute.
	 * @return the name - Bank name
	 */
	public String getName()
	{
		return getName( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayBankConfiguration.name</code> attribute. 
	 * @return the localized name - Bank name
	 */
	public Map<Language,String> getAllName(final SessionContext ctx)
	{
		return (Map<Language,String>)getAllLocalizedProperties(ctx,NAME,C2LManager.getInstance().getAllLanguages());
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayBankConfiguration.name</code> attribute. 
	 * @return the localized name - Bank name
	 */
	public Map<Language,String> getAllName()
	{
		return getAllName( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayBankConfiguration.name</code> attribute. 
	 * @param value the name - Bank name
	 */
	public void setName(final SessionContext ctx, final String value)
	{
		if ( ctx == null) 
		{
			throw new JaloInvalidParameterException( "ctx is null", 0 );
		}
		if( ctx.getLanguage() == null )
		{
			throw new JaloInvalidParameterException("GeneratedWorldpayBankConfiguration.setName requires a session language", 0 );
		}
		setLocalizedProperty(ctx, NAME,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayBankConfiguration.name</code> attribute. 
	 * @param value the name - Bank name
	 */
	public void setName(final String value)
	{
		setName( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayBankConfiguration.name</code> attribute. 
	 * @param value the name - Bank name
	 */
	public void setAllName(final SessionContext ctx, final Map<Language,String> value)
	{
		setAllLocalizedProperties(ctx,NAME,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayBankConfiguration.name</code> attribute. 
	 * @param value the name - Bank name
	 */
	public void setAllName(final Map<Language,String> value)
	{
		setAllName( getSession().getSessionContext(), value );
	}
	
}
