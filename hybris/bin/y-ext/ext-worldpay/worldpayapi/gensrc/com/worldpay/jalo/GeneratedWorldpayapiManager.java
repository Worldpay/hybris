/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at 07-Mar-2019 15:29:51                        ---
 * ----------------------------------------------------------------
 */
package com.worldpay.jalo;

import com.worldpay.constants.WorldpayapiConstants;
import com.worldpay.jalo.ApplePayPaymentInfo;
import com.worldpay.jalo.GooglePayPaymentInfo;
import com.worldpay.jalo.WorldpayAPMConfiguration;
import com.worldpay.jalo.WorldpayAavResponse;
import com.worldpay.jalo.WorldpayBankConfiguration;
import com.worldpay.jalo.WorldpayCurrencyRange;
import com.worldpay.jalo.WorldpayPaymentPage;
import com.worldpay.jalo.WorldpayRiskScore;
import de.hybris.platform.jalo.GenericItem;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.Item.AttributeMode;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.extension.Extension;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.order.Cart;
import de.hybris.platform.jalo.order.payment.PaymentInfo;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.JaloGenericCreationException;
import de.hybris.platform.payment.jalo.PaymentTransaction;
import de.hybris.platform.payment.jalo.PaymentTransactionEntry;
import de.hybris.platform.returns.jalo.ReturnRequest;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Generated class for type <code>WorldpayapiManager</code>.
 */
@SuppressWarnings({"deprecation","unused","cast","PMD"})
public abstract class GeneratedWorldpayapiManager extends Extension
{
	protected static final Map<String, Map<String, AttributeMode>> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, Map<String, AttributeMode>> ttmp = new HashMap();
		Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>();
		tmp.put("worldpayOrderCode", AttributeMode.INITIAL);
		tmp.put("worldpayDeclineCode", AttributeMode.INITIAL);
		ttmp.put("de.hybris.platform.jalo.order.AbstractOrder", Collections.unmodifiableMap(tmp));
		tmp = new HashMap<String, AttributeMode>();
		tmp.put("shopperBankCode", AttributeMode.INITIAL);
		ttmp.put("de.hybris.platform.jalo.order.Cart", Collections.unmodifiableMap(tmp));
		tmp = new HashMap<String, AttributeMode>();
		tmp.put("worldpayOrderCode", AttributeMode.INITIAL);
		tmp.put("paymentType", AttributeMode.INITIAL);
		tmp.put("authenticatedShopperID", AttributeMode.INITIAL);
		tmp.put("eventReference", AttributeMode.INITIAL);
		tmp.put("expiryDate", AttributeMode.INITIAL);
		tmp.put("merchantId", AttributeMode.INITIAL);
		ttmp.put("de.hybris.platform.jalo.order.payment.PaymentInfo", Collections.unmodifiableMap(tmp));
		tmp = new HashMap<String, AttributeMode>();
		tmp.put("apmOpen", AttributeMode.INITIAL);
		tmp.put("riskScore", AttributeMode.INITIAL);
		tmp.put("worldpayBank", AttributeMode.INITIAL);
		ttmp.put("de.hybris.platform.payment.jalo.PaymentTransaction", Collections.unmodifiableMap(tmp));
		tmp = new HashMap<String, AttributeMode>();
		tmp.put("pending", AttributeMode.INITIAL);
		tmp.put("aavResponse", AttributeMode.INITIAL);
		ttmp.put("de.hybris.platform.payment.jalo.PaymentTransactionEntry", Collections.unmodifiableMap(tmp));
		tmp = new HashMap<String, AttributeMode>();
		tmp.put("paymentTransactionEntry", AttributeMode.INITIAL);
		ttmp.put("de.hybris.platform.returns.jalo.ReturnRequest", Collections.unmodifiableMap(tmp));
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
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentTransactionEntry.aavResponse</code> attribute.
	 * @return the aavResponse
	 */
	public WorldpayAavResponse getAavResponse(final SessionContext ctx, final PaymentTransactionEntry item)
	{
		return (WorldpayAavResponse)item.getProperty( ctx, WorldpayapiConstants.Attributes.PaymentTransactionEntry.AAVRESPONSE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentTransactionEntry.aavResponse</code> attribute.
	 * @return the aavResponse
	 */
	public WorldpayAavResponse getAavResponse(final PaymentTransactionEntry item)
	{
		return getAavResponse( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentTransactionEntry.aavResponse</code> attribute. 
	 * @param value the aavResponse
	 */
	public void setAavResponse(final SessionContext ctx, final PaymentTransactionEntry item, final WorldpayAavResponse value)
	{
		item.setProperty(ctx, WorldpayapiConstants.Attributes.PaymentTransactionEntry.AAVRESPONSE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentTransactionEntry.aavResponse</code> attribute. 
	 * @param value the aavResponse
	 */
	public void setAavResponse(final PaymentTransactionEntry item, final WorldpayAavResponse value)
	{
		setAavResponse( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentTransaction.apmOpen</code> attribute.
	 * @return the apmOpen - Determines if the transaction is Open
	 */
	public Boolean isApmOpen(final SessionContext ctx, final PaymentTransaction item)
	{
		return (Boolean)item.getProperty( ctx, WorldpayapiConstants.Attributes.PaymentTransaction.APMOPEN);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentTransaction.apmOpen</code> attribute.
	 * @return the apmOpen - Determines if the transaction is Open
	 */
	public Boolean isApmOpen(final PaymentTransaction item)
	{
		return isApmOpen( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentTransaction.apmOpen</code> attribute. 
	 * @return the apmOpen - Determines if the transaction is Open
	 */
	public boolean isApmOpenAsPrimitive(final SessionContext ctx, final PaymentTransaction item)
	{
		Boolean value = isApmOpen( ctx,item );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentTransaction.apmOpen</code> attribute. 
	 * @return the apmOpen - Determines if the transaction is Open
	 */
	public boolean isApmOpenAsPrimitive(final PaymentTransaction item)
	{
		return isApmOpenAsPrimitive( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentTransaction.apmOpen</code> attribute. 
	 * @param value the apmOpen - Determines if the transaction is Open
	 */
	public void setApmOpen(final SessionContext ctx, final PaymentTransaction item, final Boolean value)
	{
		item.setProperty(ctx, WorldpayapiConstants.Attributes.PaymentTransaction.APMOPEN,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentTransaction.apmOpen</code> attribute. 
	 * @param value the apmOpen - Determines if the transaction is Open
	 */
	public void setApmOpen(final PaymentTransaction item, final Boolean value)
	{
		setApmOpen( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentTransaction.apmOpen</code> attribute. 
	 * @param value the apmOpen - Determines if the transaction is Open
	 */
	public void setApmOpen(final SessionContext ctx, final PaymentTransaction item, final boolean value)
	{
		setApmOpen( ctx, item, Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentTransaction.apmOpen</code> attribute. 
	 * @param value the apmOpen - Determines if the transaction is Open
	 */
	public void setApmOpen(final PaymentTransaction item, final boolean value)
	{
		setApmOpen( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentInfo.authenticatedShopperID</code> attribute.
	 * @return the authenticatedShopperID
	 */
	public String getAuthenticatedShopperID(final SessionContext ctx, final PaymentInfo item)
	{
		return (String)item.getProperty( ctx, WorldpayapiConstants.Attributes.PaymentInfo.AUTHENTICATEDSHOPPERID);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentInfo.authenticatedShopperID</code> attribute.
	 * @return the authenticatedShopperID
	 */
	public String getAuthenticatedShopperID(final PaymentInfo item)
	{
		return getAuthenticatedShopperID( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentInfo.authenticatedShopperID</code> attribute. 
	 * @param value the authenticatedShopperID
	 */
	public void setAuthenticatedShopperID(final SessionContext ctx, final PaymentInfo item, final String value)
	{
		item.setProperty(ctx, WorldpayapiConstants.Attributes.PaymentInfo.AUTHENTICATEDSHOPPERID,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentInfo.authenticatedShopperID</code> attribute. 
	 * @param value the authenticatedShopperID
	 */
	public void setAuthenticatedShopperID(final PaymentInfo item, final String value)
	{
		setAuthenticatedShopperID( getSession().getSessionContext(), item, value );
	}
	
	public ApplePayPaymentInfo createApplePayPaymentInfo(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( WorldpayapiConstants.TC.APPLEPAYPAYMENTINFO );
			return (ApplePayPaymentInfo)type.newInstance( ctx, attributeValues );
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
			throw new JaloSystemException( e ,"error creating ApplePayPaymentInfo : "+e.getMessage(), 0 );
		}
	}
	
	public ApplePayPaymentInfo createApplePayPaymentInfo(final Map attributeValues)
	{
		return createApplePayPaymentInfo( getSession().getSessionContext(), attributeValues );
	}
	
	public GooglePayPaymentInfo createGooglePayPaymentInfo(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( WorldpayapiConstants.TC.GOOGLEPAYPAYMENTINFO );
			return (GooglePayPaymentInfo)type.newInstance( ctx, attributeValues );
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
			throw new JaloSystemException( e ,"error creating GooglePayPaymentInfo : "+e.getMessage(), 0 );
		}
	}
	
	public GooglePayPaymentInfo createGooglePayPaymentInfo(final Map attributeValues)
	{
		return createGooglePayPaymentInfo( getSession().getSessionContext(), attributeValues );
	}
	
	public WorldpayAavResponse createWorldpayAavResponse(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( WorldpayapiConstants.TC.WORLDPAYAAVRESPONSE );
			return (WorldpayAavResponse)type.newInstance( ctx, attributeValues );
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
			throw new JaloSystemException( e ,"error creating WorldpayAavResponse : "+e.getMessage(), 0 );
		}
	}
	
	public WorldpayAavResponse createWorldpayAavResponse(final Map attributeValues)
	{
		return createWorldpayAavResponse( getSession().getSessionContext(), attributeValues );
	}
	
	public WorldpayAPMConfiguration createWorldpayAPMConfiguration(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( WorldpayapiConstants.TC.WORLDPAYAPMCONFIGURATION );
			return (WorldpayAPMConfiguration)type.newInstance( ctx, attributeValues );
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
			throw new JaloSystemException( e ,"error creating WorldpayAPMConfiguration : "+e.getMessage(), 0 );
		}
	}
	
	public WorldpayAPMConfiguration createWorldpayAPMConfiguration(final Map attributeValues)
	{
		return createWorldpayAPMConfiguration( getSession().getSessionContext(), attributeValues );
	}
	
	public PaymentInfo createWorldpayAPMPaymentInfo(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( WorldpayapiConstants.TC.WORLDPAYAPMPAYMENTINFO );
			return (PaymentInfo)type.newInstance( ctx, attributeValues );
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
			throw new JaloSystemException( e ,"error creating WorldpayAPMPaymentInfo : "+e.getMessage(), 0 );
		}
	}
	
	public PaymentInfo createWorldpayAPMPaymentInfo(final Map attributeValues)
	{
		return createWorldpayAPMPaymentInfo( getSession().getSessionContext(), attributeValues );
	}
	
	public WorldpayBankConfiguration createWorldpayBankConfiguration(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( WorldpayapiConstants.TC.WORLDPAYBANKCONFIGURATION );
			return (WorldpayBankConfiguration)type.newInstance( ctx, attributeValues );
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
			throw new JaloSystemException( e ,"error creating WorldpayBankConfiguration : "+e.getMessage(), 0 );
		}
	}
	
	public WorldpayBankConfiguration createWorldpayBankConfiguration(final Map attributeValues)
	{
		return createWorldpayBankConfiguration( getSession().getSessionContext(), attributeValues );
	}
	
	public WorldpayCurrencyRange createWorldpayCurrencyRange(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( WorldpayapiConstants.TC.WORLDPAYCURRENCYRANGE );
			return (WorldpayCurrencyRange)type.newInstance( ctx, attributeValues );
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
			throw new JaloSystemException( e ,"error creating WorldpayCurrencyRange : "+e.getMessage(), 0 );
		}
	}
	
	public WorldpayCurrencyRange createWorldpayCurrencyRange(final Map attributeValues)
	{
		return createWorldpayCurrencyRange( getSession().getSessionContext(), attributeValues );
	}
	
	public WorldpayPaymentPage createWorldpayPaymentPage(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( WorldpayapiConstants.TC.WORLDPAYPAYMENTPAGE );
			return (WorldpayPaymentPage)type.newInstance( ctx, attributeValues );
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
			throw new JaloSystemException( e ,"error creating WorldpayPaymentPage : "+e.getMessage(), 0 );
		}
	}
	
	public WorldpayPaymentPage createWorldpayPaymentPage(final Map attributeValues)
	{
		return createWorldpayPaymentPage( getSession().getSessionContext(), attributeValues );
	}
	
	public WorldpayRiskScore createWorldpayRiskScore(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( WorldpayapiConstants.TC.WORLDPAYRISKSCORE );
			return (WorldpayRiskScore)type.newInstance( ctx, attributeValues );
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
			throw new JaloSystemException( e ,"error creating WorldpayRiskScore : "+e.getMessage(), 0 );
		}
	}
	
	public WorldpayRiskScore createWorldpayRiskScore(final Map attributeValues)
	{
		return createWorldpayRiskScore( getSession().getSessionContext(), attributeValues );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentInfo.eventReference</code> attribute.
	 * @return the eventReference
	 */
	public String getEventReference(final SessionContext ctx, final PaymentInfo item)
	{
		return (String)item.getProperty( ctx, WorldpayapiConstants.Attributes.PaymentInfo.EVENTREFERENCE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentInfo.eventReference</code> attribute.
	 * @return the eventReference
	 */
	public String getEventReference(final PaymentInfo item)
	{
		return getEventReference( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentInfo.eventReference</code> attribute. 
	 * @param value the eventReference
	 */
	public void setEventReference(final SessionContext ctx, final PaymentInfo item, final String value)
	{
		item.setProperty(ctx, WorldpayapiConstants.Attributes.PaymentInfo.EVENTREFERENCE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentInfo.eventReference</code> attribute. 
	 * @param value the eventReference
	 */
	public void setEventReference(final PaymentInfo item, final String value)
	{
		setEventReference( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentInfo.expiryDate</code> attribute.
	 * @return the expiryDate
	 */
	public Date getExpiryDate(final SessionContext ctx, final PaymentInfo item)
	{
		return (Date)item.getProperty( ctx, WorldpayapiConstants.Attributes.PaymentInfo.EXPIRYDATE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentInfo.expiryDate</code> attribute.
	 * @return the expiryDate
	 */
	public Date getExpiryDate(final PaymentInfo item)
	{
		return getExpiryDate( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentInfo.expiryDate</code> attribute. 
	 * @param value the expiryDate
	 */
	public void setExpiryDate(final SessionContext ctx, final PaymentInfo item, final Date value)
	{
		item.setProperty(ctx, WorldpayapiConstants.Attributes.PaymentInfo.EXPIRYDATE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentInfo.expiryDate</code> attribute. 
	 * @param value the expiryDate
	 */
	public void setExpiryDate(final PaymentInfo item, final Date value)
	{
		setExpiryDate( getSession().getSessionContext(), item, value );
	}
	
	@Override
	public String getName()
	{
		return WorldpayapiConstants.EXTENSIONNAME;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentInfo.merchantId</code> attribute.
	 * @return the merchantId
	 */
	public String getMerchantId(final SessionContext ctx, final PaymentInfo item)
	{
		return (String)item.getProperty( ctx, WorldpayapiConstants.Attributes.PaymentInfo.MERCHANTID);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentInfo.merchantId</code> attribute.
	 * @return the merchantId
	 */
	public String getMerchantId(final PaymentInfo item)
	{
		return getMerchantId( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentInfo.merchantId</code> attribute. 
	 * @param value the merchantId
	 */
	public void setMerchantId(final SessionContext ctx, final PaymentInfo item, final String value)
	{
		item.setProperty(ctx, WorldpayapiConstants.Attributes.PaymentInfo.MERCHANTID,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentInfo.merchantId</code> attribute. 
	 * @param value the merchantId
	 */
	public void setMerchantId(final PaymentInfo item, final String value)
	{
		setMerchantId( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ReturnRequest.paymentTransactionEntry</code> attribute.
	 * @return the paymentTransactionEntry
	 */
	public PaymentTransactionEntry getPaymentTransactionEntry(final SessionContext ctx, final ReturnRequest item)
	{
		return (PaymentTransactionEntry)item.getProperty( ctx, WorldpayapiConstants.Attributes.ReturnRequest.PAYMENTTRANSACTIONENTRY);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ReturnRequest.paymentTransactionEntry</code> attribute.
	 * @return the paymentTransactionEntry
	 */
	public PaymentTransactionEntry getPaymentTransactionEntry(final ReturnRequest item)
	{
		return getPaymentTransactionEntry( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ReturnRequest.paymentTransactionEntry</code> attribute. 
	 * @param value the paymentTransactionEntry
	 */
	public void setPaymentTransactionEntry(final SessionContext ctx, final ReturnRequest item, final PaymentTransactionEntry value)
	{
		item.setProperty(ctx, WorldpayapiConstants.Attributes.ReturnRequest.PAYMENTTRANSACTIONENTRY,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>ReturnRequest.paymentTransactionEntry</code> attribute. 
	 * @param value the paymentTransactionEntry
	 */
	public void setPaymentTransactionEntry(final ReturnRequest item, final PaymentTransactionEntry value)
	{
		setPaymentTransactionEntry( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentInfo.paymentType</code> attribute.
	 * @return the paymentType
	 */
	public String getPaymentType(final SessionContext ctx, final PaymentInfo item)
	{
		return (String)item.getProperty( ctx, WorldpayapiConstants.Attributes.PaymentInfo.PAYMENTTYPE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentInfo.paymentType</code> attribute.
	 * @return the paymentType
	 */
	public String getPaymentType(final PaymentInfo item)
	{
		return getPaymentType( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentInfo.paymentType</code> attribute. 
	 * @param value the paymentType
	 */
	public void setPaymentType(final SessionContext ctx, final PaymentInfo item, final String value)
	{
		item.setProperty(ctx, WorldpayapiConstants.Attributes.PaymentInfo.PAYMENTTYPE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentInfo.paymentType</code> attribute. 
	 * @param value the paymentType
	 */
	public void setPaymentType(final PaymentInfo item, final String value)
	{
		setPaymentType( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentTransactionEntry.pending</code> attribute.
	 * @return the pending - Determines if the transaction has been confirmed by Worldpay
	 */
	public Boolean isPending(final SessionContext ctx, final PaymentTransactionEntry item)
	{
		return (Boolean)item.getProperty( ctx, WorldpayapiConstants.Attributes.PaymentTransactionEntry.PENDING);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentTransactionEntry.pending</code> attribute.
	 * @return the pending - Determines if the transaction has been confirmed by Worldpay
	 */
	public Boolean isPending(final PaymentTransactionEntry item)
	{
		return isPending( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentTransactionEntry.pending</code> attribute. 
	 * @return the pending - Determines if the transaction has been confirmed by Worldpay
	 */
	public boolean isPendingAsPrimitive(final SessionContext ctx, final PaymentTransactionEntry item)
	{
		Boolean value = isPending( ctx,item );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentTransactionEntry.pending</code> attribute. 
	 * @return the pending - Determines if the transaction has been confirmed by Worldpay
	 */
	public boolean isPendingAsPrimitive(final PaymentTransactionEntry item)
	{
		return isPendingAsPrimitive( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentTransactionEntry.pending</code> attribute. 
	 * @param value the pending - Determines if the transaction has been confirmed by Worldpay
	 */
	public void setPending(final SessionContext ctx, final PaymentTransactionEntry item, final Boolean value)
	{
		item.setProperty(ctx, WorldpayapiConstants.Attributes.PaymentTransactionEntry.PENDING,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentTransactionEntry.pending</code> attribute. 
	 * @param value the pending - Determines if the transaction has been confirmed by Worldpay
	 */
	public void setPending(final PaymentTransactionEntry item, final Boolean value)
	{
		setPending( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentTransactionEntry.pending</code> attribute. 
	 * @param value the pending - Determines if the transaction has been confirmed by Worldpay
	 */
	public void setPending(final SessionContext ctx, final PaymentTransactionEntry item, final boolean value)
	{
		setPending( ctx, item, Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentTransactionEntry.pending</code> attribute. 
	 * @param value the pending - Determines if the transaction has been confirmed by Worldpay
	 */
	public void setPending(final PaymentTransactionEntry item, final boolean value)
	{
		setPending( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentTransaction.riskScore</code> attribute.
	 * @return the riskScore
	 */
	public WorldpayRiskScore getRiskScore(final SessionContext ctx, final PaymentTransaction item)
	{
		return (WorldpayRiskScore)item.getProperty( ctx, WorldpayapiConstants.Attributes.PaymentTransaction.RISKSCORE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentTransaction.riskScore</code> attribute.
	 * @return the riskScore
	 */
	public WorldpayRiskScore getRiskScore(final PaymentTransaction item)
	{
		return getRiskScore( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentTransaction.riskScore</code> attribute. 
	 * @param value the riskScore
	 */
	public void setRiskScore(final SessionContext ctx, final PaymentTransaction item, final WorldpayRiskScore value)
	{
		item.setProperty(ctx, WorldpayapiConstants.Attributes.PaymentTransaction.RISKSCORE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentTransaction.riskScore</code> attribute. 
	 * @param value the riskScore
	 */
	public void setRiskScore(final PaymentTransaction item, final WorldpayRiskScore value)
	{
		setRiskScore( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Cart.shopperBankCode</code> attribute.
	 * @return the shopperBankCode - Bank code
	 */
	public String getShopperBankCode(final SessionContext ctx, final Cart item)
	{
		return (String)item.getProperty( ctx, WorldpayapiConstants.Attributes.Cart.SHOPPERBANKCODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Cart.shopperBankCode</code> attribute.
	 * @return the shopperBankCode - Bank code
	 */
	public String getShopperBankCode(final Cart item)
	{
		return getShopperBankCode( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Cart.shopperBankCode</code> attribute. 
	 * @param value the shopperBankCode - Bank code
	 */
	public void setShopperBankCode(final SessionContext ctx, final Cart item, final String value)
	{
		item.setProperty(ctx, WorldpayapiConstants.Attributes.Cart.SHOPPERBANKCODE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Cart.shopperBankCode</code> attribute. 
	 * @param value the shopperBankCode - Bank code
	 */
	public void setShopperBankCode(final Cart item, final String value)
	{
		setShopperBankCode( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentTransaction.worldpayBank</code> attribute.
	 * @return the worldpayBank
	 */
	public WorldpayBankConfiguration getWorldpayBank(final SessionContext ctx, final PaymentTransaction item)
	{
		return (WorldpayBankConfiguration)item.getProperty( ctx, WorldpayapiConstants.Attributes.PaymentTransaction.WORLDPAYBANK);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentTransaction.worldpayBank</code> attribute.
	 * @return the worldpayBank
	 */
	public WorldpayBankConfiguration getWorldpayBank(final PaymentTransaction item)
	{
		return getWorldpayBank( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentTransaction.worldpayBank</code> attribute. 
	 * @param value the worldpayBank
	 */
	public void setWorldpayBank(final SessionContext ctx, final PaymentTransaction item, final WorldpayBankConfiguration value)
	{
		item.setProperty(ctx, WorldpayapiConstants.Attributes.PaymentTransaction.WORLDPAYBANK,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentTransaction.worldpayBank</code> attribute. 
	 * @param value the worldpayBank
	 */
	public void setWorldpayBank(final PaymentTransaction item, final WorldpayBankConfiguration value)
	{
		setWorldpayBank( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.worldpayDeclineCode</code> attribute.
	 * @return the worldpayDeclineCode - Decline code of the transaction if it was refused
	 */
	public String getWorldpayDeclineCode(final SessionContext ctx, final AbstractOrder item)
	{
		return (String)item.getProperty( ctx, WorldpayapiConstants.Attributes.AbstractOrder.WORLDPAYDECLINECODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.worldpayDeclineCode</code> attribute.
	 * @return the worldpayDeclineCode - Decline code of the transaction if it was refused
	 */
	public String getWorldpayDeclineCode(final AbstractOrder item)
	{
		return getWorldpayDeclineCode( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.worldpayDeclineCode</code> attribute. 
	 * @param value the worldpayDeclineCode - Decline code of the transaction if it was refused
	 */
	public void setWorldpayDeclineCode(final SessionContext ctx, final AbstractOrder item, final String value)
	{
		item.setProperty(ctx, WorldpayapiConstants.Attributes.AbstractOrder.WORLDPAYDECLINECODE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.worldpayDeclineCode</code> attribute. 
	 * @param value the worldpayDeclineCode - Decline code of the transaction if it was refused
	 */
	public void setWorldpayDeclineCode(final AbstractOrder item, final String value)
	{
		setWorldpayDeclineCode( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.worldpayOrderCode</code> attribute.
	 * @return the worldpayOrderCode - This is the code that Worldpay will use in their systems.
	 */
	public String getWorldpayOrderCode(final SessionContext ctx, final AbstractOrder item)
	{
		return (String)item.getProperty( ctx, WorldpayapiConstants.Attributes.AbstractOrder.WORLDPAYORDERCODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrder.worldpayOrderCode</code> attribute.
	 * @return the worldpayOrderCode - This is the code that Worldpay will use in their systems.
	 */
	public String getWorldpayOrderCode(final AbstractOrder item)
	{
		return getWorldpayOrderCode( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.worldpayOrderCode</code> attribute. 
	 * @param value the worldpayOrderCode - This is the code that Worldpay will use in their systems.
	 */
	public void setWorldpayOrderCode(final SessionContext ctx, final AbstractOrder item, final String value)
	{
		item.setProperty(ctx, WorldpayapiConstants.Attributes.AbstractOrder.WORLDPAYORDERCODE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>AbstractOrder.worldpayOrderCode</code> attribute. 
	 * @param value the worldpayOrderCode - This is the code that Worldpay will use in their systems.
	 */
	public void setWorldpayOrderCode(final AbstractOrder item, final String value)
	{
		setWorldpayOrderCode( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentInfo.worldpayOrderCode</code> attribute.
	 * @return the worldpayOrderCode - Contains the Worldpay order code
	 */
	public String getWorldpayOrderCode(final SessionContext ctx, final PaymentInfo item)
	{
		return (String)item.getProperty( ctx, WorldpayapiConstants.Attributes.PaymentInfo.WORLDPAYORDERCODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PaymentInfo.worldpayOrderCode</code> attribute.
	 * @return the worldpayOrderCode - Contains the Worldpay order code
	 */
	public String getWorldpayOrderCode(final PaymentInfo item)
	{
		return getWorldpayOrderCode( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentInfo.worldpayOrderCode</code> attribute. 
	 * @param value the worldpayOrderCode - Contains the Worldpay order code
	 */
	public void setWorldpayOrderCode(final SessionContext ctx, final PaymentInfo item, final String value)
	{
		item.setProperty(ctx, WorldpayapiConstants.Attributes.PaymentInfo.WORLDPAYORDERCODE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>PaymentInfo.worldpayOrderCode</code> attribute. 
	 * @param value the worldpayOrderCode - Contains the Worldpay order code
	 */
	public void setWorldpayOrderCode(final PaymentInfo item, final String value)
	{
		setWorldpayOrderCode( getSession().getSessionContext(), item, value );
	}
	
}
