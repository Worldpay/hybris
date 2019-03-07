/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at 07-Mar-2019 15:29:51                        ---
 * ----------------------------------------------------------------
 */
package com.worldpay.jalo;

import com.worldpay.constants.WorldpayaddonConstants;
import com.worldpay.jalo.WorldpayAPMConfiguration;
import de.hybris.platform.cms2.jalo.contents.components.SimpleCMSComponent;
import de.hybris.platform.jalo.Item.AttributeMode;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.media.Media;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Generated class for type {@link com.worldpay.jalo.WorldpayAPMComponent WorldpayAPMComponent}.
 */
@SuppressWarnings({"deprecation","unused","cast","PMD"})
public abstract class GeneratedWorldpayAPMComponent extends SimpleCMSComponent
{
	/** Qualifier of the <code>WorldpayAPMComponent.apmConfiguration</code> attribute **/
	public static final String APMCONFIGURATION = "apmConfiguration";
	/** Qualifier of the <code>WorldpayAPMComponent.media</code> attribute **/
	public static final String MEDIA = "media";
	protected static final Map<String, AttributeMode> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>(SimpleCMSComponent.DEFAULT_INITIAL_ATTRIBUTES);
		tmp.put(APMCONFIGURATION, AttributeMode.INITIAL);
		tmp.put(MEDIA, AttributeMode.INITIAL);
		DEFAULT_INITIAL_ATTRIBUTES = Collections.unmodifiableMap(tmp);
	}
	@Override
	protected Map<String, AttributeMode> getDefaultAttributeModes()
	{
		return DEFAULT_INITIAL_ATTRIBUTES;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMComponent.apmConfiguration</code> attribute.
	 * @return the apmConfiguration
	 */
	public WorldpayAPMConfiguration getApmConfiguration(final SessionContext ctx)
	{
		return (WorldpayAPMConfiguration)getProperty( ctx, APMCONFIGURATION);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMComponent.apmConfiguration</code> attribute.
	 * @return the apmConfiguration
	 */
	public WorldpayAPMConfiguration getApmConfiguration()
	{
		return getApmConfiguration( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMComponent.apmConfiguration</code> attribute. 
	 * @param value the apmConfiguration
	 */
	public void setApmConfiguration(final SessionContext ctx, final WorldpayAPMConfiguration value)
	{
		setProperty(ctx, APMCONFIGURATION,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMComponent.apmConfiguration</code> attribute. 
	 * @param value the apmConfiguration
	 */
	public void setApmConfiguration(final WorldpayAPMConfiguration value)
	{
		setApmConfiguration( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMComponent.media</code> attribute.
	 * @return the media
	 */
	public Media getMedia(final SessionContext ctx)
	{
		if( ctx == null || ctx.getLanguage() == null )
		{
			throw new JaloInvalidParameterException("GeneratedWorldpayAPMComponent.getMedia requires a session language", 0 );
		}
		return (Media)getLocalizedProperty( ctx, MEDIA);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMComponent.media</code> attribute.
	 * @return the media
	 */
	public Media getMedia()
	{
		return getMedia( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMComponent.media</code> attribute. 
	 * @return the localized media
	 */
	public Map<Language,Media> getAllMedia(final SessionContext ctx)
	{
		return (Map<Language,Media>)getAllLocalizedProperties(ctx,MEDIA,C2LManager.getInstance().getAllLanguages());
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMComponent.media</code> attribute. 
	 * @return the localized media
	 */
	public Map<Language,Media> getAllMedia()
	{
		return getAllMedia( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMComponent.media</code> attribute. 
	 * @param value the media
	 */
	public void setMedia(final SessionContext ctx, final Media value)
	{
		if ( ctx == null) 
		{
			throw new JaloInvalidParameterException( "ctx is null", 0 );
		}
		if( ctx.getLanguage() == null )
		{
			throw new JaloInvalidParameterException("GeneratedWorldpayAPMComponent.setMedia requires a session language", 0 );
		}
		setLocalizedProperty(ctx, MEDIA,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMComponent.media</code> attribute. 
	 * @param value the media
	 */
	public void setMedia(final Media value)
	{
		setMedia( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMComponent.media</code> attribute. 
	 * @param value the media
	 */
	public void setAllMedia(final SessionContext ctx, final Map<Language,Media> value)
	{
		setAllLocalizedProperties(ctx,MEDIA,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMComponent.media</code> attribute. 
	 * @param value the media
	 */
	public void setAllMedia(final Map<Language,Media> value)
	{
		setAllMedia( getSession().getSessionContext(), value );
	}
	
}
