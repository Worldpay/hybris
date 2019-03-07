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
 * Generated class for type {@link de.hybris.platform.cronjob.jalo.CronJob CleanUpProcessedOrderModificationsCronJob}.
 */
@SuppressWarnings({"deprecation","unused","cast","PMD"})
public abstract class GeneratedCleanUpProcessedOrderModificationsCronJob extends CronJob
{
	/** Qualifier of the <code>CleanUpProcessedOrderModificationsCronJob.daysToWaitBeforeDeletion</code> attribute **/
	public static final String DAYSTOWAITBEFOREDELETION = "daysToWaitBeforeDeletion";
	protected static final Map<String, AttributeMode> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>(CronJob.DEFAULT_INITIAL_ATTRIBUTES);
		tmp.put(DAYSTOWAITBEFOREDELETION, AttributeMode.INITIAL);
		DEFAULT_INITIAL_ATTRIBUTES = Collections.unmodifiableMap(tmp);
	}
	@Override
	protected Map<String, AttributeMode> getDefaultAttributeModes()
	{
		return DEFAULT_INITIAL_ATTRIBUTES;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>CleanUpProcessedOrderModificationsCronJob.daysToWaitBeforeDeletion</code> attribute.
	 * @return the daysToWaitBeforeDeletion - Max unprocessed time before processed order modifications are deleted
	 */
	public Integer getDaysToWaitBeforeDeletion(final SessionContext ctx)
	{
		return (Integer)getProperty( ctx, DAYSTOWAITBEFOREDELETION);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>CleanUpProcessedOrderModificationsCronJob.daysToWaitBeforeDeletion</code> attribute.
	 * @return the daysToWaitBeforeDeletion - Max unprocessed time before processed order modifications are deleted
	 */
	public Integer getDaysToWaitBeforeDeletion()
	{
		return getDaysToWaitBeforeDeletion( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>CleanUpProcessedOrderModificationsCronJob.daysToWaitBeforeDeletion</code> attribute. 
	 * @return the daysToWaitBeforeDeletion - Max unprocessed time before processed order modifications are deleted
	 */
	public int getDaysToWaitBeforeDeletionAsPrimitive(final SessionContext ctx)
	{
		Integer value = getDaysToWaitBeforeDeletion( ctx );
		return value != null ? value.intValue() : 0;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>CleanUpProcessedOrderModificationsCronJob.daysToWaitBeforeDeletion</code> attribute. 
	 * @return the daysToWaitBeforeDeletion - Max unprocessed time before processed order modifications are deleted
	 */
	public int getDaysToWaitBeforeDeletionAsPrimitive()
	{
		return getDaysToWaitBeforeDeletionAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>CleanUpProcessedOrderModificationsCronJob.daysToWaitBeforeDeletion</code> attribute. 
	 * @param value the daysToWaitBeforeDeletion - Max unprocessed time before processed order modifications are deleted
	 */
	public void setDaysToWaitBeforeDeletion(final SessionContext ctx, final Integer value)
	{
		setProperty(ctx, DAYSTOWAITBEFOREDELETION,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>CleanUpProcessedOrderModificationsCronJob.daysToWaitBeforeDeletion</code> attribute. 
	 * @param value the daysToWaitBeforeDeletion - Max unprocessed time before processed order modifications are deleted
	 */
	public void setDaysToWaitBeforeDeletion(final Integer value)
	{
		setDaysToWaitBeforeDeletion( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>CleanUpProcessedOrderModificationsCronJob.daysToWaitBeforeDeletion</code> attribute. 
	 * @param value the daysToWaitBeforeDeletion - Max unprocessed time before processed order modifications are deleted
	 */
	public void setDaysToWaitBeforeDeletion(final SessionContext ctx, final int value)
	{
		setDaysToWaitBeforeDeletion( ctx,Integer.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>CleanUpProcessedOrderModificationsCronJob.daysToWaitBeforeDeletion</code> attribute. 
	 * @param value the daysToWaitBeforeDeletion - Max unprocessed time before processed order modifications are deleted
	 */
	public void setDaysToWaitBeforeDeletion(final int value)
	{
		setDaysToWaitBeforeDeletion( getSession().getSessionContext(), value );
	}
	
}
