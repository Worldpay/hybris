/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at 07-Mar-2019 15:29:51                        ---
 * ----------------------------------------------------------------
 */
package com.worldpay.worldpaynotifications.jalo;

import com.worldpay.worldpaynotifications.constants.WorldpaynotificationsConstants;
import com.worldpay.worldpaynotifications.jalo.CleanUpProcessedOrderModificationsCronJob;
import com.worldpay.worldpaynotifications.jalo.NotifyUnprocessedOrderModificationsCronJob;
import com.worldpay.worldpaynotifications.jalo.OrderModificationCronJob;
import com.worldpay.worldpaynotifications.jalo.WorldpayOrderModification;
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
 * Generated class for type <code>WorldpaynotificationsManager</code>.
 */
@SuppressWarnings({"deprecation","unused","cast","PMD"})
public abstract class GeneratedWorldpaynotificationsManager extends Extension
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
	
	public CleanUpProcessedOrderModificationsCronJob createCleanUpProcessedOrderModificationsCronJob(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( WorldpaynotificationsConstants.TC.CLEANUPPROCESSEDORDERMODIFICATIONSCRONJOB );
			return (CleanUpProcessedOrderModificationsCronJob)type.newInstance( ctx, attributeValues );
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
			throw new JaloSystemException( e ,"error creating CleanUpProcessedOrderModificationsCronJob : "+e.getMessage(), 0 );
		}
	}
	
	public CleanUpProcessedOrderModificationsCronJob createCleanUpProcessedOrderModificationsCronJob(final Map attributeValues)
	{
		return createCleanUpProcessedOrderModificationsCronJob( getSession().getSessionContext(), attributeValues );
	}
	
	public NotifyUnprocessedOrderModificationsCronJob createNotifyUnprocessedOrderModificationsCronJob(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( WorldpaynotificationsConstants.TC.NOTIFYUNPROCESSEDORDERMODIFICATIONSCRONJOB );
			return (NotifyUnprocessedOrderModificationsCronJob)type.newInstance( ctx, attributeValues );
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
			throw new JaloSystemException( e ,"error creating NotifyUnprocessedOrderModificationsCronJob : "+e.getMessage(), 0 );
		}
	}
	
	public NotifyUnprocessedOrderModificationsCronJob createNotifyUnprocessedOrderModificationsCronJob(final Map attributeValues)
	{
		return createNotifyUnprocessedOrderModificationsCronJob( getSession().getSessionContext(), attributeValues );
	}
	
	public OrderModificationCronJob createOrderModificationCronJob(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( WorldpaynotificationsConstants.TC.ORDERMODIFICATIONCRONJOB );
			return (OrderModificationCronJob)type.newInstance( ctx, attributeValues );
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
			throw new JaloSystemException( e ,"error creating OrderModificationCronJob : "+e.getMessage(), 0 );
		}
	}
	
	public OrderModificationCronJob createOrderModificationCronJob(final Map attributeValues)
	{
		return createOrderModificationCronJob( getSession().getSessionContext(), attributeValues );
	}
	
	public WorldpayOrderModification createWorldpayOrderModification(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( WorldpaynotificationsConstants.TC.WORLDPAYORDERMODIFICATION );
			return (WorldpayOrderModification)type.newInstance( ctx, attributeValues );
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
			throw new JaloSystemException( e ,"error creating WorldpayOrderModification : "+e.getMessage(), 0 );
		}
	}
	
	public WorldpayOrderModification createWorldpayOrderModification(final Map attributeValues)
	{
		return createWorldpayOrderModification( getSession().getSessionContext(), attributeValues );
	}
	
	@Override
	public String getName()
	{
		return WorldpaynotificationsConstants.EXTENSIONNAME;
	}
	
}
