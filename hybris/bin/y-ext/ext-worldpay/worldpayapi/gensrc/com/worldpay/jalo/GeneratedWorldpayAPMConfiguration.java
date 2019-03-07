/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at 07-Mar-2019 15:29:51                        ---
 * ----------------------------------------------------------------
 */
package com.worldpay.jalo;

import com.worldpay.constants.WorldpayapiConstants;
import com.worldpay.jalo.WorldpayCurrencyRange;
import de.hybris.platform.jalo.GenericItem;
import de.hybris.platform.jalo.Item.AttributeMode;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Country;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.c2l.Language;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Generated class for type {@link com.worldpay.jalo.WorldpayAPMConfiguration WorldpayAPMConfiguration}.
 */
@SuppressWarnings({"deprecation","unused","cast","PMD"})
public abstract class GeneratedWorldpayAPMConfiguration extends GenericItem
{
	/** Qualifier of the <code>WorldpayAPMConfiguration.code</code> attribute **/
	public static final String CODE = "code";
	/** Qualifier of the <code>WorldpayAPMConfiguration.name</code> attribute **/
	public static final String NAME = "name";
	/** Qualifier of the <code>WorldpayAPMConfiguration.description</code> attribute **/
	public static final String DESCRIPTION = "description";
	/** Qualifier of the <code>WorldpayAPMConfiguration.autoCancelPendingTimeoutInMinutes</code> attribute **/
	public static final String AUTOCANCELPENDINGTIMEOUTINMINUTES = "autoCancelPendingTimeoutInMinutes";
	/** Qualifier of the <code>WorldpayAPMConfiguration.bank</code> attribute **/
	public static final String BANK = "bank";
	/** Qualifier of the <code>WorldpayAPMConfiguration.countries</code> attribute **/
	public static final String COUNTRIES = "countries";
	/** Qualifier of the <code>WorldpayAPMConfiguration.currencies</code> attribute **/
	public static final String CURRENCIES = "currencies";
	/** Qualifier of the <code>WorldpayAPMConfiguration.currencyRanges</code> attribute **/
	public static final String CURRENCYRANGES = "currencyRanges";
	/** Qualifier of the <code>WorldpayAPMConfiguration.automaticRefunds</code> attribute **/
	public static final String AUTOMATICREFUNDS = "automaticRefunds";
	/** Qualifier of the <code>WorldpayAPMConfiguration.bankTransferRefunds</code> attribute **/
	public static final String BANKTRANSFERREFUNDS = "bankTransferRefunds";
	protected static final Map<String, AttributeMode> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>();
		tmp.put(CODE, AttributeMode.INITIAL);
		tmp.put(NAME, AttributeMode.INITIAL);
		tmp.put(DESCRIPTION, AttributeMode.INITIAL);
		tmp.put(AUTOCANCELPENDINGTIMEOUTINMINUTES, AttributeMode.INITIAL);
		tmp.put(BANK, AttributeMode.INITIAL);
		tmp.put(COUNTRIES, AttributeMode.INITIAL);
		tmp.put(CURRENCIES, AttributeMode.INITIAL);
		tmp.put(CURRENCYRANGES, AttributeMode.INITIAL);
		tmp.put(AUTOMATICREFUNDS, AttributeMode.INITIAL);
		tmp.put(BANKTRANSFERREFUNDS, AttributeMode.INITIAL);
		DEFAULT_INITIAL_ATTRIBUTES = Collections.unmodifiableMap(tmp);
	}
	@Override
	protected Map<String, AttributeMode> getDefaultAttributeModes()
	{
		return DEFAULT_INITIAL_ATTRIBUTES;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.autoCancelPendingTimeoutInMinutes</code> attribute.
	 * @return the autoCancelPendingTimeoutInMinutes - Timeout in minutes before the order is auto-cancelled
	 */
	public Integer getAutoCancelPendingTimeoutInMinutes(final SessionContext ctx)
	{
		return (Integer)getProperty( ctx, AUTOCANCELPENDINGTIMEOUTINMINUTES);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.autoCancelPendingTimeoutInMinutes</code> attribute.
	 * @return the autoCancelPendingTimeoutInMinutes - Timeout in minutes before the order is auto-cancelled
	 */
	public Integer getAutoCancelPendingTimeoutInMinutes()
	{
		return getAutoCancelPendingTimeoutInMinutes( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.autoCancelPendingTimeoutInMinutes</code> attribute. 
	 * @return the autoCancelPendingTimeoutInMinutes - Timeout in minutes before the order is auto-cancelled
	 */
	public int getAutoCancelPendingTimeoutInMinutesAsPrimitive(final SessionContext ctx)
	{
		Integer value = getAutoCancelPendingTimeoutInMinutes( ctx );
		return value != null ? value.intValue() : 0;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.autoCancelPendingTimeoutInMinutes</code> attribute. 
	 * @return the autoCancelPendingTimeoutInMinutes - Timeout in minutes before the order is auto-cancelled
	 */
	public int getAutoCancelPendingTimeoutInMinutesAsPrimitive()
	{
		return getAutoCancelPendingTimeoutInMinutesAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.autoCancelPendingTimeoutInMinutes</code> attribute. 
	 * @param value the autoCancelPendingTimeoutInMinutes - Timeout in minutes before the order is auto-cancelled
	 */
	public void setAutoCancelPendingTimeoutInMinutes(final SessionContext ctx, final Integer value)
	{
		setProperty(ctx, AUTOCANCELPENDINGTIMEOUTINMINUTES,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.autoCancelPendingTimeoutInMinutes</code> attribute. 
	 * @param value the autoCancelPendingTimeoutInMinutes - Timeout in minutes before the order is auto-cancelled
	 */
	public void setAutoCancelPendingTimeoutInMinutes(final Integer value)
	{
		setAutoCancelPendingTimeoutInMinutes( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.autoCancelPendingTimeoutInMinutes</code> attribute. 
	 * @param value the autoCancelPendingTimeoutInMinutes - Timeout in minutes before the order is auto-cancelled
	 */
	public void setAutoCancelPendingTimeoutInMinutes(final SessionContext ctx, final int value)
	{
		setAutoCancelPendingTimeoutInMinutes( ctx,Integer.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.autoCancelPendingTimeoutInMinutes</code> attribute. 
	 * @param value the autoCancelPendingTimeoutInMinutes - Timeout in minutes before the order is auto-cancelled
	 */
	public void setAutoCancelPendingTimeoutInMinutes(final int value)
	{
		setAutoCancelPendingTimeoutInMinutes( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.automaticRefunds</code> attribute.
	 * @return the automaticRefunds - The APM can be refunded automatically.
	 */
	public Boolean isAutomaticRefunds(final SessionContext ctx)
	{
		return (Boolean)getProperty( ctx, AUTOMATICREFUNDS);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.automaticRefunds</code> attribute.
	 * @return the automaticRefunds - The APM can be refunded automatically.
	 */
	public Boolean isAutomaticRefunds()
	{
		return isAutomaticRefunds( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.automaticRefunds</code> attribute. 
	 * @return the automaticRefunds - The APM can be refunded automatically.
	 */
	public boolean isAutomaticRefundsAsPrimitive(final SessionContext ctx)
	{
		Boolean value = isAutomaticRefunds( ctx );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.automaticRefunds</code> attribute. 
	 * @return the automaticRefunds - The APM can be refunded automatically.
	 */
	public boolean isAutomaticRefundsAsPrimitive()
	{
		return isAutomaticRefundsAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.automaticRefunds</code> attribute. 
	 * @param value the automaticRefunds - The APM can be refunded automatically.
	 */
	public void setAutomaticRefunds(final SessionContext ctx, final Boolean value)
	{
		setProperty(ctx, AUTOMATICREFUNDS,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.automaticRefunds</code> attribute. 
	 * @param value the automaticRefunds - The APM can be refunded automatically.
	 */
	public void setAutomaticRefunds(final Boolean value)
	{
		setAutomaticRefunds( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.automaticRefunds</code> attribute. 
	 * @param value the automaticRefunds - The APM can be refunded automatically.
	 */
	public void setAutomaticRefunds(final SessionContext ctx, final boolean value)
	{
		setAutomaticRefunds( ctx,Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.automaticRefunds</code> attribute. 
	 * @param value the automaticRefunds - The APM can be refunded automatically.
	 */
	public void setAutomaticRefunds(final boolean value)
	{
		setAutomaticRefunds( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.bank</code> attribute.
	 * @return the bank - The APM is used for bank transfers
	 */
	public Boolean isBank(final SessionContext ctx)
	{
		return (Boolean)getProperty( ctx, BANK);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.bank</code> attribute.
	 * @return the bank - The APM is used for bank transfers
	 */
	public Boolean isBank()
	{
		return isBank( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.bank</code> attribute. 
	 * @return the bank - The APM is used for bank transfers
	 */
	public boolean isBankAsPrimitive(final SessionContext ctx)
	{
		Boolean value = isBank( ctx );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.bank</code> attribute. 
	 * @return the bank - The APM is used for bank transfers
	 */
	public boolean isBankAsPrimitive()
	{
		return isBankAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.bank</code> attribute. 
	 * @param value the bank - The APM is used for bank transfers
	 */
	public void setBank(final SessionContext ctx, final Boolean value)
	{
		setProperty(ctx, BANK,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.bank</code> attribute. 
	 * @param value the bank - The APM is used for bank transfers
	 */
	public void setBank(final Boolean value)
	{
		setBank( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.bank</code> attribute. 
	 * @param value the bank - The APM is used for bank transfers
	 */
	public void setBank(final SessionContext ctx, final boolean value)
	{
		setBank( ctx,Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.bank</code> attribute. 
	 * @param value the bank - The APM is used for bank transfers
	 */
	public void setBank(final boolean value)
	{
		setBank( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.bankTransferRefunds</code> attribute.
	 * @return the bankTransferRefunds - The APM can be refunded via a bank transfer.
	 */
	public Boolean isBankTransferRefunds(final SessionContext ctx)
	{
		return (Boolean)getProperty( ctx, BANKTRANSFERREFUNDS);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.bankTransferRefunds</code> attribute.
	 * @return the bankTransferRefunds - The APM can be refunded via a bank transfer.
	 */
	public Boolean isBankTransferRefunds()
	{
		return isBankTransferRefunds( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.bankTransferRefunds</code> attribute. 
	 * @return the bankTransferRefunds - The APM can be refunded via a bank transfer.
	 */
	public boolean isBankTransferRefundsAsPrimitive(final SessionContext ctx)
	{
		Boolean value = isBankTransferRefunds( ctx );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.bankTransferRefunds</code> attribute. 
	 * @return the bankTransferRefunds - The APM can be refunded via a bank transfer.
	 */
	public boolean isBankTransferRefundsAsPrimitive()
	{
		return isBankTransferRefundsAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.bankTransferRefunds</code> attribute. 
	 * @param value the bankTransferRefunds - The APM can be refunded via a bank transfer.
	 */
	public void setBankTransferRefunds(final SessionContext ctx, final Boolean value)
	{
		setProperty(ctx, BANKTRANSFERREFUNDS,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.bankTransferRefunds</code> attribute. 
	 * @param value the bankTransferRefunds - The APM can be refunded via a bank transfer.
	 */
	public void setBankTransferRefunds(final Boolean value)
	{
		setBankTransferRefunds( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.bankTransferRefunds</code> attribute. 
	 * @param value the bankTransferRefunds - The APM can be refunded via a bank transfer.
	 */
	public void setBankTransferRefunds(final SessionContext ctx, final boolean value)
	{
		setBankTransferRefunds( ctx,Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.bankTransferRefunds</code> attribute. 
	 * @param value the bankTransferRefunds - The APM can be refunded via a bank transfer.
	 */
	public void setBankTransferRefunds(final boolean value)
	{
		setBankTransferRefunds( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.code</code> attribute.
	 * @return the code - Contains the code of the APM
	 */
	public String getCode(final SessionContext ctx)
	{
		return (String)getProperty( ctx, CODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.code</code> attribute.
	 * @return the code - Contains the code of the APM
	 */
	public String getCode()
	{
		return getCode( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.code</code> attribute. 
	 * @param value the code - Contains the code of the APM
	 */
	public void setCode(final SessionContext ctx, final String value)
	{
		setProperty(ctx, CODE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.code</code> attribute. 
	 * @param value the code - Contains the code of the APM
	 */
	public void setCode(final String value)
	{
		setCode( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.countries</code> attribute.
	 * @return the countries - Countries for which the APM is restricted to.
	 */
	public Set<Country> getCountries(final SessionContext ctx)
	{
		Set<Country> coll = (Set<Country>)getProperty( ctx, COUNTRIES);
		return coll != null ? coll : Collections.EMPTY_SET;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.countries</code> attribute.
	 * @return the countries - Countries for which the APM is restricted to.
	 */
	public Set<Country> getCountries()
	{
		return getCountries( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.countries</code> attribute. 
	 * @param value the countries - Countries for which the APM is restricted to.
	 */
	public void setCountries(final SessionContext ctx, final Set<Country> value)
	{
		setProperty(ctx, COUNTRIES,value == null || !value.isEmpty() ? value : null );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.countries</code> attribute. 
	 * @param value the countries - Countries for which the APM is restricted to.
	 */
	public void setCountries(final Set<Country> value)
	{
		setCountries( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.currencies</code> attribute.
	 * @return the currencies - Currencies for which the APM is restricted to.
	 */
	public Set<Currency> getCurrencies(final SessionContext ctx)
	{
		Set<Currency> coll = (Set<Currency>)getProperty( ctx, CURRENCIES);
		return coll != null ? coll : Collections.EMPTY_SET;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.currencies</code> attribute.
	 * @return the currencies - Currencies for which the APM is restricted to.
	 */
	public Set<Currency> getCurrencies()
	{
		return getCurrencies( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.currencies</code> attribute. 
	 * @param value the currencies - Currencies for which the APM is restricted to.
	 */
	public void setCurrencies(final SessionContext ctx, final Set<Currency> value)
	{
		setProperty(ctx, CURRENCIES,value == null || !value.isEmpty() ? value : null );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.currencies</code> attribute. 
	 * @param value the currencies - Currencies for which the APM is restricted to.
	 */
	public void setCurrencies(final Set<Currency> value)
	{
		setCurrencies( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.currencyRanges</code> attribute.
	 * @return the currencyRanges - Currency ranges for which the APM is restricted to.
	 */
	public Set<WorldpayCurrencyRange> getCurrencyRanges(final SessionContext ctx)
	{
		Set<WorldpayCurrencyRange> coll = (Set<WorldpayCurrencyRange>)getProperty( ctx, CURRENCYRANGES);
		return coll != null ? coll : Collections.EMPTY_SET;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.currencyRanges</code> attribute.
	 * @return the currencyRanges - Currency ranges for which the APM is restricted to.
	 */
	public Set<WorldpayCurrencyRange> getCurrencyRanges()
	{
		return getCurrencyRanges( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.currencyRanges</code> attribute. 
	 * @param value the currencyRanges - Currency ranges for which the APM is restricted to.
	 */
	public void setCurrencyRanges(final SessionContext ctx, final Set<WorldpayCurrencyRange> value)
	{
		setProperty(ctx, CURRENCYRANGES,value == null || !value.isEmpty() ? value : null );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.currencyRanges</code> attribute. 
	 * @param value the currencyRanges - Currency ranges for which the APM is restricted to.
	 */
	public void setCurrencyRanges(final Set<WorldpayCurrencyRange> value)
	{
		setCurrencyRanges( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.description</code> attribute.
	 * @return the description - Contains the description of the APM
	 */
	public String getDescription(final SessionContext ctx)
	{
		if( ctx == null || ctx.getLanguage() == null )
		{
			throw new JaloInvalidParameterException("GeneratedWorldpayAPMConfiguration.getDescription requires a session language", 0 );
		}
		return (String)getLocalizedProperty( ctx, DESCRIPTION);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.description</code> attribute.
	 * @return the description - Contains the description of the APM
	 */
	public String getDescription()
	{
		return getDescription( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.description</code> attribute. 
	 * @return the localized description - Contains the description of the APM
	 */
	public Map<Language,String> getAllDescription(final SessionContext ctx)
	{
		return (Map<Language,String>)getAllLocalizedProperties(ctx,DESCRIPTION,C2LManager.getInstance().getAllLanguages());
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.description</code> attribute. 
	 * @return the localized description - Contains the description of the APM
	 */
	public Map<Language,String> getAllDescription()
	{
		return getAllDescription( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.description</code> attribute. 
	 * @param value the description - Contains the description of the APM
	 */
	public void setDescription(final SessionContext ctx, final String value)
	{
		if ( ctx == null) 
		{
			throw new JaloInvalidParameterException( "ctx is null", 0 );
		}
		if( ctx.getLanguage() == null )
		{
			throw new JaloInvalidParameterException("GeneratedWorldpayAPMConfiguration.setDescription requires a session language", 0 );
		}
		setLocalizedProperty(ctx, DESCRIPTION,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.description</code> attribute. 
	 * @param value the description - Contains the description of the APM
	 */
	public void setDescription(final String value)
	{
		setDescription( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.description</code> attribute. 
	 * @param value the description - Contains the description of the APM
	 */
	public void setAllDescription(final SessionContext ctx, final Map<Language,String> value)
	{
		setAllLocalizedProperties(ctx,DESCRIPTION,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.description</code> attribute. 
	 * @param value the description - Contains the description of the APM
	 */
	public void setAllDescription(final Map<Language,String> value)
	{
		setAllDescription( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.name</code> attribute.
	 * @return the name - Contains the name of the APM
	 */
	public String getName(final SessionContext ctx)
	{
		if( ctx == null || ctx.getLanguage() == null )
		{
			throw new JaloInvalidParameterException("GeneratedWorldpayAPMConfiguration.getName requires a session language", 0 );
		}
		return (String)getLocalizedProperty( ctx, NAME);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.name</code> attribute.
	 * @return the name - Contains the name of the APM
	 */
	public String getName()
	{
		return getName( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.name</code> attribute. 
	 * @return the localized name - Contains the name of the APM
	 */
	public Map<Language,String> getAllName(final SessionContext ctx)
	{
		return (Map<Language,String>)getAllLocalizedProperties(ctx,NAME,C2LManager.getInstance().getAllLanguages());
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WorldpayAPMConfiguration.name</code> attribute. 
	 * @return the localized name - Contains the name of the APM
	 */
	public Map<Language,String> getAllName()
	{
		return getAllName( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.name</code> attribute. 
	 * @param value the name - Contains the name of the APM
	 */
	public void setName(final SessionContext ctx, final String value)
	{
		if ( ctx == null) 
		{
			throw new JaloInvalidParameterException( "ctx is null", 0 );
		}
		if( ctx.getLanguage() == null )
		{
			throw new JaloInvalidParameterException("GeneratedWorldpayAPMConfiguration.setName requires a session language", 0 );
		}
		setLocalizedProperty(ctx, NAME,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.name</code> attribute. 
	 * @param value the name - Contains the name of the APM
	 */
	public void setName(final String value)
	{
		setName( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.name</code> attribute. 
	 * @param value the name - Contains the name of the APM
	 */
	public void setAllName(final SessionContext ctx, final Map<Language,String> value)
	{
		setAllLocalizedProperties(ctx,NAME,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>WorldpayAPMConfiguration.name</code> attribute. 
	 * @param value the name - Contains the name of the APM
	 */
	public void setAllName(final Map<Language,String> value)
	{
		setAllName( getSession().getSessionContext(), value );
	}
	
}
