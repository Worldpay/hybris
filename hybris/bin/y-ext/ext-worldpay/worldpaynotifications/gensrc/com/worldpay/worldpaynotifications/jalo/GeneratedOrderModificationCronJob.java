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
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Generated class for type {@link de.hybris.platform.cronjob.jalo.CronJob OrderModificationCronJob}.
 */
@SuppressWarnings({"deprecation","unused","cast","PMD"})
public abstract class GeneratedOrderModificationCronJob extends CronJob
{
	/** Qualifier of the <code>OrderModificationCronJob.typeOfPaymentTransactionToProcessSet</code> attribute **/
	public static final String TYPEOFPAYMENTTRANSACTIONTOPROCESSSET = "typeOfPaymentTransactionToProcessSet";
	protected static final Map<String, AttributeMode> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>(CronJob.DEFAULT_INITIAL_ATTRIBUTES);
		tmp.put(TYPEOFPAYMENTTRANSACTIONTOPROCESSSET, AttributeMode.INITIAL);
		DEFAULT_INITIAL_ATTRIBUTES = Collections.unmodifiableMap(tmp);
	}
	@Override
	protected Map<String, AttributeMode> getDefaultAttributeModes()
	{
		return DEFAULT_INITIAL_ATTRIBUTES;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>OrderModificationCronJob.typeOfPaymentTransactionToProcessSet</code> attribute.
	 * @return the typeOfPaymentTransactionToProcessSet - List of payment transaction types that will be processed
	 */
	public Set<EnumerationValue> getTypeOfPaymentTransactionToProcessSet(final SessionContext ctx)
	{
		Set<EnumerationValue> coll = (Set<EnumerationValue>)getProperty( ctx, TYPEOFPAYMENTTRANSACTIONTOPROCESSSET);
		return coll != null ? coll : Collections.EMPTY_SET;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>OrderModificationCronJob.typeOfPaymentTransactionToProcessSet</code> attribute.
	 * @return the typeOfPaymentTransactionToProcessSet - List of payment transaction types that will be processed
	 */
	public Set<EnumerationValue> getTypeOfPaymentTransactionToProcessSet()
	{
		return getTypeOfPaymentTransactionToProcessSet( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>OrderModificationCronJob.typeOfPaymentTransactionToProcessSet</code> attribute. 
	 * @param value the typeOfPaymentTransactionToProcessSet - List of payment transaction types that will be processed
	 */
	public void setTypeOfPaymentTransactionToProcessSet(final SessionContext ctx, final Set<EnumerationValue> value)
	{
		setProperty(ctx, TYPEOFPAYMENTTRANSACTIONTOPROCESSSET,value == null || !value.isEmpty() ? value : null );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>OrderModificationCronJob.typeOfPaymentTransactionToProcessSet</code> attribute. 
	 * @param value the typeOfPaymentTransactionToProcessSet - List of payment transaction types that will be processed
	 */
	public void setTypeOfPaymentTransactionToProcessSet(final Set<EnumerationValue> value)
	{
		setTypeOfPaymentTransactionToProcessSet( getSession().getSessionContext(), value );
	}
	
}
