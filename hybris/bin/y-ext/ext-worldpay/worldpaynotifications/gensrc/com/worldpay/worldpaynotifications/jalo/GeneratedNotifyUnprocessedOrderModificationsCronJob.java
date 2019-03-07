/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at 07-Mar-2019 15:29:51                        ---
 * ----------------------------------------------------------------
 */
package com.worldpay.worldpaynotifications.jalo;

import com.worldpay.worldpaynotifications.constants.WorldpaynotificationsConstants;
import de.hybris.platform.cronjob.jalo.CronJob;
import de.hybris.platform.jalo.Item.AttributeMode;
import de.hybris.platform.jalo.SessionContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Generated class for type {@link de.hybris.platform.cronjob.jalo.CronJob NotifyUnprocessedOrderModificationsCronJob}.
 */
@SuppressWarnings({"deprecation","unused","cast","PMD"})
public abstract class GeneratedNotifyUnprocessedOrderModificationsCronJob extends CronJob
{
	/** Qualifier of the <code>NotifyUnprocessedOrderModificationsCronJob.unprocessedTimeInDays</code> attribute **/
	public static final String UNPROCESSEDTIMEINDAYS = "unprocessedTimeInDays";
	protected static final Map<String, AttributeMode> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>(CronJob.DEFAULT_INITIAL_ATTRIBUTES);
		tmp.put(UNPROCESSEDTIMEINDAYS, AttributeMode.INITIAL);
		DEFAULT_INITIAL_ATTRIBUTES = Collections.unmodifiableMap(tmp);
	}
	@Override
	protected Map<String, AttributeMode> getDefaultAttributeModes()
	{
		return DEFAULT_INITIAL_ATTRIBUTES;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>NotifyUnprocessedOrderModificationsCronJob.unprocessedTimeInDays</code> attribute.
	 * @return the unprocessedTimeInDays - Max unprocessed time before ticket is created
	 */
	public Integer getUnprocessedTimeInDays(final SessionContext ctx)
	{
		return (Integer)getProperty( ctx, UNPROCESSEDTIMEINDAYS);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>NotifyUnprocessedOrderModificationsCronJob.unprocessedTimeInDays</code> attribute.
	 * @return the unprocessedTimeInDays - Max unprocessed time before ticket is created
	 */
	public Integer getUnprocessedTimeInDays()
	{
		return getUnprocessedTimeInDays( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>NotifyUnprocessedOrderModificationsCronJob.unprocessedTimeInDays</code> attribute. 
	 * @return the unprocessedTimeInDays - Max unprocessed time before ticket is created
	 */
	public int getUnprocessedTimeInDaysAsPrimitive(final SessionContext ctx)
	{
		Integer value = getUnprocessedTimeInDays( ctx );
		return value != null ? value.intValue() : 0;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>NotifyUnprocessedOrderModificationsCronJob.unprocessedTimeInDays</code> attribute. 
	 * @return the unprocessedTimeInDays - Max unprocessed time before ticket is created
	 */
	public int getUnprocessedTimeInDaysAsPrimitive()
	{
		return getUnprocessedTimeInDaysAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>NotifyUnprocessedOrderModificationsCronJob.unprocessedTimeInDays</code> attribute. 
	 * @param value the unprocessedTimeInDays - Max unprocessed time before ticket is created
	 */
	public void setUnprocessedTimeInDays(final SessionContext ctx, final Integer value)
	{
		setProperty(ctx, UNPROCESSEDTIMEINDAYS,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>NotifyUnprocessedOrderModificationsCronJob.unprocessedTimeInDays</code> attribute. 
	 * @param value the unprocessedTimeInDays - Max unprocessed time before ticket is created
	 */
	public void setUnprocessedTimeInDays(final Integer value)
	{
		setUnprocessedTimeInDays( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>NotifyUnprocessedOrderModificationsCronJob.unprocessedTimeInDays</code> attribute. 
	 * @param value the unprocessedTimeInDays - Max unprocessed time before ticket is created
	 */
	public void setUnprocessedTimeInDays(final SessionContext ctx, final int value)
	{
		setUnprocessedTimeInDays( ctx,Integer.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>NotifyUnprocessedOrderModificationsCronJob.unprocessedTimeInDays</code> attribute. 
	 * @param value the unprocessedTimeInDays - Max unprocessed time before ticket is created
	 */
	public void setUnprocessedTimeInDays(final int value)
	{
		setUnprocessedTimeInDays( getSession().getSessionContext(), value );
	}
	
}
