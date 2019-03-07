/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at 07-Mar-2019 15:29:51                        ---
 * ----------------------------------------------------------------
 */
package com.worldpay.worldpaynotifications.jalo;

import com.worldpay.worldpaynotifications.constants.WorldpaynotificationsConstants;
import de.hybris.platform.jalo.GenericItem;
import de.hybris.platform.jalo.Item.AttributeMode;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Generated class for type {@link de.hybris.platform.jalo.GenericItem WorldpayOrderModification}.
 */
@SuppressWarnings({"deprecation","unused","cast","PMD"})
public abstract class GeneratedWorldpayOrderModification extends GenericItem
{
	/** Qualifier of the <code>WorldpayOrderModification.code</code> attribute **/
	public static final String CODE = "code";
	/** Qualifier of the <code>WorldpayOrderModification.worldpayOrderCode</code> attribute **/
	public static final String WORLDPAYORDERCODE = "worldpayOrderCode";
	/** Qualifier of the <code>WorldpayOrderModification.type</code> attribute **/
	public static final String TYPE = "type";
	/** Qualifier of the <code>WorldpayOrderModification.processed</code> attribute **/
	public static final String PROCESSED = "processed";
	/** Qualifier of the <code>WorldpayOrderModification.notified</code> attribute **/
	public static final String NOTIFIED = "notified";
	/** Qualifier of the <code>WorldpayOrderModification.defective</code> attribute **/
	public static final String DEFECTIVE = "defective";
	/** Qualifier of the <code>WorldpayOrderModification.defectiveReason</code> attribute **/
	public static final String DEFECTIVEREASON = "defectiveReason";
	/** Qualifier of the <code>WorldpayOrderModification.defectiveCounter</code> attribute **/
	public static final String DEFECTIVECOUNTER = "defectiveCounter";
	/** Qualifier of the <code>WorldpayOrderModification.orderNotificationMessage</code> attribute **/
	public static final String ORDERNOTIFICATIONMESSAGE = "orderNotificationMessage";
	protected static final Map<String, AttributeMode> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>();
		tmp.put(CODE, AttributeMode.INITIAL);
		tmp.put(WORLDPAYORDERCODE, AttributeMode.INITIAL);
		tmp.put(TYPE, AttributeMode.INITIAL);
		tmp.put(PROCESSED, AttributeMode.INITIAL);
		tmp.put(NOTIFIED, AttributeMode.INITIAL);
		tmp.put(DEFECTIVE, AttributeMode.INITIAL);
		tmp.put(DEFECTIVEREASON, AttributeMode.INITIAL);
		tmp.put(DEFECTIVECOUNTER, AttributeMode.INITIAL);
		tmp.put(ORDERNOTIFICATIONMESSAGE, AttributeMode.INITIAL);
		DEFAULT_INITIAL_ATTRIBUTES = Collections.unmodifiableMap(tmp);
	}
	@Override
	protected Map<String, AttributeMode> getDefaultAttributeModes()
	{
		return DEFAULT_INITIAL_ATTRIBUTES;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.code</code> attribute.
	 * @return the code
	 */
	public String getCode(final SessionContext ctx)
	{
		return (String)getProperty( ctx, CODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.code</code> attribute.
	 * @return the code
	 */
	public String getCode()
	{
		return getCode( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.code</code> attribute. 
	 * @param value the code
	 */
	public void setCode(final SessionContext ctx, final String value)
	{
		setProperty(ctx, CODE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.code</code> attribute. 
	 * @param value the code
	 */
	public void setCode(final String value)
	{
		setCode( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.defective</code> attribute.
	 * @return the defective - Has the record created a defect
	 */
	public Boolean isDefective(final SessionContext ctx)
	{
		return (Boolean)getProperty( ctx, DEFECTIVE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.defective</code> attribute.
	 * @return the defective - Has the record created a defect
	 */
	public Boolean isDefective()
	{
		return isDefective( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.defective</code> attribute. 
	 * @return the defective - Has the record created a defect
	 */
	public boolean isDefectiveAsPrimitive(final SessionContext ctx)
	{
		Boolean value = isDefective( ctx );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.defective</code> attribute. 
	 * @return the defective - Has the record created a defect
	 */
	public boolean isDefectiveAsPrimitive()
	{
		return isDefectiveAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.defective</code> attribute. 
	 * @param value the defective - Has the record created a defect
	 */
	public void setDefective(final SessionContext ctx, final Boolean value)
	{
		setProperty(ctx, DEFECTIVE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.defective</code> attribute. 
	 * @param value the defective - Has the record created a defect
	 */
	public void setDefective(final Boolean value)
	{
		setDefective( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.defective</code> attribute. 
	 * @param value the defective - Has the record created a defect
	 */
	public void setDefective(final SessionContext ctx, final boolean value)
	{
		setDefective( ctx,Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.defective</code> attribute. 
	 * @param value the defective - Has the record created a defect
	 */
	public void setDefective(final boolean value)
	{
		setDefective( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.defectiveCounter</code> attribute.
	 * @return the defectiveCounter - Total number of defective modifications with same DefectiveReason, PaymentTransactionType and WorldpayOrderCode
	 */
	public Integer getDefectiveCounter(final SessionContext ctx)
	{
		return (Integer)getProperty( ctx, DEFECTIVECOUNTER);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.defectiveCounter</code> attribute.
	 * @return the defectiveCounter - Total number of defective modifications with same DefectiveReason, PaymentTransactionType and WorldpayOrderCode
	 */
	public Integer getDefectiveCounter()
	{
		return getDefectiveCounter( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.defectiveCounter</code> attribute. 
	 * @return the defectiveCounter - Total number of defective modifications with same DefectiveReason, PaymentTransactionType and WorldpayOrderCode
	 */
	public int getDefectiveCounterAsPrimitive(final SessionContext ctx)
	{
		Integer value = getDefectiveCounter( ctx );
		return value != null ? value.intValue() : 0;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.defectiveCounter</code> attribute. 
	 * @return the defectiveCounter - Total number of defective modifications with same DefectiveReason, PaymentTransactionType and WorldpayOrderCode
	 */
	public int getDefectiveCounterAsPrimitive()
	{
		return getDefectiveCounterAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.defectiveCounter</code> attribute. 
	 * @param value the defectiveCounter - Total number of defective modifications with same DefectiveReason, PaymentTransactionType and WorldpayOrderCode
	 */
	public void setDefectiveCounter(final SessionContext ctx, final Integer value)
	{
		setProperty(ctx, DEFECTIVECOUNTER,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.defectiveCounter</code> attribute. 
	 * @param value the defectiveCounter - Total number of defective modifications with same DefectiveReason, PaymentTransactionType and WorldpayOrderCode
	 */
	public void setDefectiveCounter(final Integer value)
	{
		setDefectiveCounter( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.defectiveCounter</code> attribute. 
	 * @param value the defectiveCounter - Total number of defective modifications with same DefectiveReason, PaymentTransactionType and WorldpayOrderCode
	 */
	public void setDefectiveCounter(final SessionContext ctx, final int value)
	{
		setDefectiveCounter( ctx,Integer.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.defectiveCounter</code> attribute. 
	 * @param value the defectiveCounter - Total number of defective modifications with same DefectiveReason, PaymentTransactionType and WorldpayOrderCode
	 */
	public void setDefectiveCounter(final int value)
	{
		setDefectiveCounter( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.defectiveReason</code> attribute.
	 * @return the defectiveReason - Why the record was defective
	 */
	public EnumerationValue getDefectiveReason(final SessionContext ctx)
	{
		return (EnumerationValue)getProperty( ctx, DEFECTIVEREASON);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.defectiveReason</code> attribute.
	 * @return the defectiveReason - Why the record was defective
	 */
	public EnumerationValue getDefectiveReason()
	{
		return getDefectiveReason( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.defectiveReason</code> attribute. 
	 * @param value the defectiveReason - Why the record was defective
	 */
	public void setDefectiveReason(final SessionContext ctx, final EnumerationValue value)
	{
		setProperty(ctx, DEFECTIVEREASON,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.defectiveReason</code> attribute. 
	 * @param value the defectiveReason - Why the record was defective
	 */
	public void setDefectiveReason(final EnumerationValue value)
	{
		setDefectiveReason( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.notified</code> attribute.
	 * @return the notified - Has the record been notified
	 */
	public Boolean isNotified(final SessionContext ctx)
	{
		return (Boolean)getProperty( ctx, NOTIFIED);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.notified</code> attribute.
	 * @return the notified - Has the record been notified
	 */
	public Boolean isNotified()
	{
		return isNotified( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.notified</code> attribute. 
	 * @return the notified - Has the record been notified
	 */
	public boolean isNotifiedAsPrimitive(final SessionContext ctx)
	{
		Boolean value = isNotified( ctx );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.notified</code> attribute. 
	 * @return the notified - Has the record been notified
	 */
	public boolean isNotifiedAsPrimitive()
	{
		return isNotifiedAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.notified</code> attribute. 
	 * @param value the notified - Has the record been notified
	 */
	public void setNotified(final SessionContext ctx, final Boolean value)
	{
		setProperty(ctx, NOTIFIED,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.notified</code> attribute. 
	 * @param value the notified - Has the record been notified
	 */
	public void setNotified(final Boolean value)
	{
		setNotified( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.notified</code> attribute. 
	 * @param value the notified - Has the record been notified
	 */
	public void setNotified(final SessionContext ctx, final boolean value)
	{
		setNotified( ctx,Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.notified</code> attribute. 
	 * @param value the notified - Has the record been notified
	 */
	public void setNotified(final boolean value)
	{
		setNotified( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.orderNotificationMessage</code> attribute.
	 * @return the orderNotificationMessage - OrderNotificationMessage
	 */
	public String getOrderNotificationMessage(final SessionContext ctx)
	{
		return (String)getProperty( ctx, ORDERNOTIFICATIONMESSAGE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.orderNotificationMessage</code> attribute.
	 * @return the orderNotificationMessage - OrderNotificationMessage
	 */
	public String getOrderNotificationMessage()
	{
		return getOrderNotificationMessage( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.orderNotificationMessage</code> attribute. 
	 * @param value the orderNotificationMessage - OrderNotificationMessage
	 */
	public void setOrderNotificationMessage(final SessionContext ctx, final String value)
	{
		setProperty(ctx, ORDERNOTIFICATIONMESSAGE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.orderNotificationMessage</code> attribute. 
	 * @param value the orderNotificationMessage - OrderNotificationMessage
	 */
	public void setOrderNotificationMessage(final String value)
	{
		setOrderNotificationMessage( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.processed</code> attribute.
	 * @return the processed - Has the record been processed
	 */
	public Boolean isProcessed(final SessionContext ctx)
	{
		return (Boolean)getProperty( ctx, PROCESSED);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.processed</code> attribute.
	 * @return the processed - Has the record been processed
	 */
	public Boolean isProcessed()
	{
		return isProcessed( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.processed</code> attribute. 
	 * @return the processed - Has the record been processed
	 */
	public boolean isProcessedAsPrimitive(final SessionContext ctx)
	{
		Boolean value = isProcessed( ctx );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.processed</code> attribute. 
	 * @return the processed - Has the record been processed
	 */
	public boolean isProcessedAsPrimitive()
	{
		return isProcessedAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.processed</code> attribute. 
	 * @param value the processed - Has the record been processed
	 */
	public void setProcessed(final SessionContext ctx, final Boolean value)
	{
		setProperty(ctx, PROCESSED,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.processed</code> attribute. 
	 * @param value the processed - Has the record been processed
	 */
	public void setProcessed(final Boolean value)
	{
		setProcessed( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.processed</code> attribute. 
	 * @param value the processed - Has the record been processed
	 */
	public void setProcessed(final SessionContext ctx, final boolean value)
	{
		setProcessed( ctx,Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.processed</code> attribute. 
	 * @param value the processed - Has the record been processed
	 */
	public void setProcessed(final boolean value)
	{
		setProcessed( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.type</code> attribute.
	 * @return the type - PaymentTransactionType
	 */
	public EnumerationValue getType(final SessionContext ctx)
	{
		return (EnumerationValue)getProperty( ctx, TYPE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.type</code> attribute.
	 * @return the type - PaymentTransactionType
	 */
	public EnumerationValue getType()
	{
		return getType( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.type</code> attribute. 
	 * @param value the type - PaymentTransactionType
	 */
	public void setType(final SessionContext ctx, final EnumerationValue value)
	{
		setProperty(ctx, TYPE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.type</code> attribute. 
	 * @param value the type - PaymentTransactionType
	 */
	public void setType(final EnumerationValue value)
	{
		setType( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.worldpayOrderCode</code> attribute.
	 * @return the worldpayOrderCode
	 */
	public String getWorldpayOrderCode(final SessionContext ctx)
	{
		return (String)getProperty( ctx, WORLDPAYORDERCODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayOrderModification.worldpayOrderCode</code> attribute.
	 * @return the worldpayOrderCode
	 */
	public String getWorldpayOrderCode()
	{
		return getWorldpayOrderCode( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.worldpayOrderCode</code> attribute. 
	 * @param value the worldpayOrderCode
	 */
	public void setWorldpayOrderCode(final SessionContext ctx, final String value)
	{
		setProperty(ctx, WORLDPAYORDERCODE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayOrderModification.worldpayOrderCode</code> attribute. 
	 * @param value the worldpayOrderCode
	 */
	public void setWorldpayOrderCode(final String value)
	{
		setWorldpayOrderCode( getSession().getSessionContext(), value );
	}
	
}
