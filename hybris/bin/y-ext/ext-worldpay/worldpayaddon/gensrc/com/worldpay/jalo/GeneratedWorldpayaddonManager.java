/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at 07-Mar-2019 15:29:51                        ---
 * ----------------------------------------------------------------
 */
package com.worldpay.jalo;

import com.worldpay.constants.WorldpayaddonConstants;
import com.worldpay.jalo.WorldpayAPMComponent;
import com.worldpay.jalo.WorldpayApplePayComponent;
import com.worldpay.jalo.WorldpayCCComponent;
import com.worldpay.jalo.WorldpayGooglePayComponent;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.Item.AttributeMode;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.extension.Extension;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.JaloGenericCreationException;
import java.util.HashMap;
import java.util.Map;

/**
 * Generated class for type <code>WorldpayaddonManager</code>.
 */
@SuppressWarnings({"deprecation","unused","cast","PMD"})
public abstract class GeneratedWorldpayaddonManager extends Extension
{
	protected static final Map<String, Map<String, AttributeMode>> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, Map<String, AttributeMode>> ttmp = new HashMap();
		DEFAULT_INITIAL_ATTRIBUTES = ttmp;
	}
	@Override
	public Map<String, AttributeMode> getDefaultAttributeModes(final Class<? extends Item> itemClass)
	{
		Map<String, AttributeMode> ret = new HashMap<>();
		final Map<String, AttributeMode> attr = DEFAULT_INITIAL_ATTRIBUTES.get(itemClass.getName());
		if (attr != null)
		{
			ret.putAll(attr);
		}
		return ret;
	}
	
	public WorldpayAPMComponent createWorldpayAPMComponent(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( WorldpayaddonConstants.TC.WORLDPAYAPMCOMPONENT );
			return (WorldpayAPMComponent)type.newInstance( ctx, attributeValues );
		}
		catch( JaloGenericCreationException e)
		{
			final Throwable cause = e.getCause();
			throw (cause instanceof RuntimeException ?
			(RuntimeException)cause
			:
			new JaloSystemException( cause, cause.getMessage(), e.getErrorCode() ) );
		}
		catch( JaloBusinessException e )
		{
			throw new JaloSystemException( e ,"error creating WorldpayAPMComponent : "+e.getMessage(), 0 );
		}
	}
	
	public WorldpayAPMComponent createWorldpayAPMComponent(final Map attributeValues)
	{
		return createWorldpayAPMComponent( getSession().getSessionContext(), attributeValues );
	}
	
	public WorldpayApplePayComponent createWorldpayApplePayComponent(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( WorldpayaddonConstants.TC.WORLDPAYAPPLEPAYCOMPONENT );
			return (WorldpayApplePayComponent)type.newInstance( ctx, attributeValues );
		}
		catch( JaloGenericCreationException e)
		{
			final Throwable cause = e.getCause();
			throw (cause instanceof RuntimeException ?
			(RuntimeException)cause
			:
			new JaloSystemException( cause, cause.getMessage(), e.getErrorCode() ) );
		}
		catch( JaloBusinessException e )
		{
			throw new JaloSystemException( e ,"error creating WorldpayApplePayComponent : "+e.getMessage(), 0 );
		}
	}
	
	public WorldpayApplePayComponent createWorldpayApplePayComponent(final Map attributeValues)
	{
		return createWorldpayApplePayComponent( getSession().getSessionContext(), attributeValues );
	}
	
	public WorldpayCCComponent createWorldpayCCComponent(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( WorldpayaddonConstants.TC.WORLDPAYCCCOMPONENT );
			return (WorldpayCCComponent)type.newInstance( ctx, attributeValues );
		}
		catch( JaloGenericCreationException e)
		{
			final Throwable cause = e.getCause();
			throw (cause instanceof RuntimeException ?
			(RuntimeException)cause
			:
			new JaloSystemException( cause, cause.getMessage(), e.getErrorCode() ) );
		}
		catch( JaloBusinessException e )
		{
			throw new JaloSystemException( e ,"error creating WorldpayCCComponent : "+e.getMessage(), 0 );
		}
	}
	
	public WorldpayCCComponent createWorldpayCCComponent(final Map attributeValues)
	{
		return createWorldpayCCComponent( getSession().getSessionContext(), attributeValues );
	}
	
	public WorldpayGooglePayComponent createWorldpayGooglePayComponent(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( WorldpayaddonConstants.TC.WORLDPAYGOOGLEPAYCOMPONENT );
			return (WorldpayGooglePayComponent)type.newInstance( ctx, attributeValues );
		}
		catch( JaloGenericCreationException e)
		{
			final Throwable cause = e.getCause();
			throw (cause instanceof RuntimeException ?
			(RuntimeException)cause
			:
			new JaloSystemException( cause, cause.getMessage(), e.getErrorCode() ) );
		}
		catch( JaloBusinessException e )
		{
			throw new JaloSystemException( e ,"error creating WorldpayGooglePayComponent : "+e.getMessage(), 0 );
		}
	}
	
	public WorldpayGooglePayComponent createWorldpayGooglePayComponent(final Map attributeValues)
	{
		return createWorldpayGooglePayComponent( getSession().getSessionContext(), attributeValues );
	}
	
	@Override
	public String getName()
	{
		return WorldpayaddonConstants.EXTENSIONNAME;
	}
	
}
