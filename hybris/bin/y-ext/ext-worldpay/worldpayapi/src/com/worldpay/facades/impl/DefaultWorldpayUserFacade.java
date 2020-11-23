package com.worldpay.facades.impl;

import com.worldpay.customer.WorldpayCustomerAccountService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

public class DefaultWorldpayUserFacade implements UserFacade {

    protected final CheckoutCustomerStrategy checkoutCustomerStrategy;
    protected final UserFacade userFacade;
    protected final Converter<WorldpayAPMPaymentInfoModel, CCPaymentInfoData> apmPaymentInfoConverter;
    protected final Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter;
    protected final WorldpayCustomerAccountService customerAccountService;

    public DefaultWorldpayUserFacade(final CheckoutCustomerStrategy checkoutCustomerStrategy,
                                     final UserFacade userFacade,
                                     final Converter<WorldpayAPMPaymentInfoModel, CCPaymentInfoData> apmPaymentInfoConverter,
                                     final Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter,
                                     final WorldpayCustomerAccountService customerAccountService) {
        this.checkoutCustomerStrategy = checkoutCustomerStrategy;
        this.userFacade = userFacade;
        this.apmPaymentInfoConverter = apmPaymentInfoConverter;
        this.creditCardPaymentInfoConverter = creditCardPaymentInfoConverter;
        this.customerAccountService = customerAccountService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TitleData> getTitles() {
        return userFacade.getTitles();
    }

    /**
     * Test if the address book is empty.
     *
     * @return true if the customer has no addresses
     * @deprecated since 6.5, instead check if {@link #getAddressBook()} is empty directly
     */
    @SuppressWarnings("java:S1133")
    @Deprecated
    @Override
    public boolean isAddressBookEmpty() {
        return userFacade.isAddressBookEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AddressData> getAddressBook() {
        return userFacade.getAddressBook();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAddress(final AddressData addressData) {
        userFacade.addAddress(addressData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAddress(final AddressData addressData) {
        userFacade.removeAddress(addressData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void editAddress(final AddressData addressData) {
        userFacade.editAddress(addressData);
    }

    @Override
    public AddressData getDefaultAddress() {
        return userFacade.getDefaultAddress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDefaultAddress(final AddressData addressData) {
        userFacade.setDefaultAddress(addressData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AddressData getAddressForCode(final String code) {
        return userFacade.getAddressForCode(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDefaultAddress(final String addressId) {
        return userFacade.isDefaultAddress(addressId);
    }

    /**
     * Returns the current user's Credit Card and Alternative Payment Methods Payment Infos
     * For simplification reasons, alternative Payment Methods are also converted to Credit Card Payment Info Data
     *
     * @param saved <code>true</code> to retrieve only saved credit card payment infos
     * @return list of Credit Card Payment Info Data
     */
    @Override
    public List<CCPaymentInfoData> getCCPaymentInfos(final boolean saved) {
        final CustomerModel currentCustomer = checkoutCustomerStrategy.getCurrentUserForCheckout();
        List<PaymentInfoModel> savedPaymentInfos = currentCustomer.getPaymentInfos().stream()
            .filter(paymentInfo -> Boolean.FALSE.equals(paymentInfo.getDuplicate()))
            .filter(paymentInfo -> filterBySavedValue(paymentInfo, saved))
            .collect(Collectors.toList());

        final List<CCPaymentInfoData> ccPaymentInfos = new ArrayList<>();
        final PaymentInfoModel defaultPaymentInfoModel = currentCustomer.getDefaultPaymentInfo();
        for (final PaymentInfoModel paymentInfoModel : savedPaymentInfos) {
            final CCPaymentInfoData paymentInfoData = convertPaymentInfoModel(paymentInfoModel);
            if (paymentInfoData != null) {
                if (paymentInfoModel.equals(defaultPaymentInfoModel)) {
                    paymentInfoData.setDefaultPaymentInfo(true);
                    ccPaymentInfos.add(0, paymentInfoData);
                } else {
                    ccPaymentInfos.add(paymentInfoData);
                }
            }
        }
        return ccPaymentInfos;
    }

    /**
     * Returns the current user's credit card or alternative payment methods payment info given it's code
     *
     * @param code the code
     * @return the Credit Card Payment Info Data
     */
    @Override
    public CCPaymentInfoData getCCPaymentInfoForCode(final String code) {
        final Optional<PaymentInfoModel> paymentInfoModelForCode = Optional.ofNullable(getPaymentInfoModelForCode(code));
        if (paymentInfoModelForCode.isPresent()) {
            final PaymentInfoModel paymentInfoModel = paymentInfoModelForCode.get();
            final CCPaymentInfoData paymentInfoData = convertPaymentInfoModel(paymentInfoModel);
            if (paymentInfoData != null) {
                final CustomerModel currentCustomer = checkoutCustomerStrategy.getCurrentUserForCheckout();
                final PaymentInfoModel defaultPaymentInfoModel = currentCustomer.getDefaultPaymentInfo();
                if (paymentInfoModel.equals(defaultPaymentInfoModel)) {
                    paymentInfoData.setDefaultPaymentInfo(true);
                }
                return paymentInfoData;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateCCPaymentInfo(final CCPaymentInfoData paymentInfo) {
        userFacade.updateCCPaymentInfo(paymentInfo);
    }

    /**
     * Removes credit card or alternative payment methods payment info by id
     *
     * @param id the id
     */
    @Override
    public void removeCCPaymentInfo(final String id) {
        validateParameterNotNullStandardMessage("id", id);

        final CustomerModel currentCustomer = checkoutCustomerStrategy.getCurrentUserForCheckout();

        currentCustomer.getPaymentInfos().stream()
            .filter(paymentInfo -> Boolean.FALSE.equals(paymentInfo.getDuplicate()))
            .filter(paymentInfo -> id.equals(paymentInfo.getPk().toString()))
            .findFirst()
            .ifPresent(paymentInfo -> deletePaymentInfo(paymentInfo, currentCustomer));

        updateDefaultPaymentInfo(currentCustomer);
    }

    /**
     * Unlink the credit card or alternative payment methods payment info by id info from the customer by id
     *
     * @param id the id
     * @deprecated since 6.7. Use {@link UserFacade#removeCCPaymentInfo(String)} instead
     */
    @SuppressWarnings("java:S1133")
    @Deprecated
    @Override
    public void unlinkCCPaymentInfo(final String id) {
        removeCCPaymentInfo(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDefaultPaymentInfo(final CCPaymentInfoData paymentInfo) {
        validateParameterNotNullStandardMessage("paymentInfoData", paymentInfo);
        Optional.ofNullable(paymentInfo)
            .map(CCPaymentInfoData::getId)
            .map(this::getPaymentInfoModelForCode)
            .ifPresent(this::setDefaultPaymentInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void syncSessionLanguage() {
        userFacade.syncSessionLanguage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void syncSessionCurrency() {
        userFacade.syncSessionCurrency();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAnonymousUser() {
        return userFacade.isAnonymousUser();
    }

    /**
     * @param code
     * @return
     */
    protected PaymentInfoModel getPaymentInfoModelForCode(final String code) {
        final Collection<PaymentInfoModel> paymentInfos = Optional.ofNullable(checkoutCustomerStrategy.getCurrentUserForCheckout())
            .map(CustomerModel::getPaymentInfos)
            .orElse(Collections.emptyList());

        return paymentInfos.stream()
            .filter(paymentInfo -> code.equals(paymentInfo.getPk().toString()))
            .filter(paymentInfo -> Boolean.FALSE.equals(paymentInfo.getDuplicate()))
            .findAny()
            .orElse(null);
    }

    /**
     * Converts a {@link PaymentInfoModel} into a {@link CCPaymentInfoData} based on the subtype
     *
     * @param paymentInfoModel - the payment info model to be converted
     * @return the converted payment info
     */
    protected CCPaymentInfoData convertPaymentInfoModel(final PaymentInfoModel paymentInfoModel) {
        if (paymentInfoModel instanceof CreditCardPaymentInfoModel) {
            return creditCardPaymentInfoConverter.convert((CreditCardPaymentInfoModel) paymentInfoModel);
        } else if (paymentInfoModel instanceof WorldpayAPMPaymentInfoModel) {
            return apmPaymentInfoConverter.convert((WorldpayAPMPaymentInfoModel) paymentInfoModel);
        }
        return null;
    }

    /**
     * Sets the default Payment Info
     *
     * @param paymentInfoModel the paymentInfoModel to make default
     */
    protected void setDefaultPaymentInfo(final PaymentInfoModel paymentInfoModel) {
        final CustomerModel currentUserForCheckout = checkoutCustomerStrategy.getCurrentUserForCheckout();
        customerAccountService.setDefaultPaymentInfo(currentUserForCheckout, paymentInfoModel);
    }


    /**
     * Removes credit card or alternatieve payment method payment info
     *
     * @param paymentInfoModel - payment info to be removed
     * @param currentCustomer  - the customer to whom the payment info belongs
     */
    protected void deletePaymentInfo(final PaymentInfoModel paymentInfoModel, final CustomerModel currentCustomer) {
        if (paymentInfoModel instanceof CreditCardPaymentInfoModel) {
            customerAccountService.deleteCCPaymentInfo(currentCustomer, (CreditCardPaymentInfoModel) paymentInfoModel);
        } else if (paymentInfoModel instanceof WorldpayAPMPaymentInfoModel) {
            customerAccountService.deleteAPMPaymentInfo(currentCustomer, (WorldpayAPMPaymentInfoModel) paymentInfoModel);
        }
    }

    /**
     * Updates the default payment info
     *
     * @param currentCustomer - current customer
     */
    protected void updateDefaultPaymentInfo(final CustomerModel currentCustomer) {
        if (currentCustomer.getDefaultPaymentInfo() == null) {
            final List<PaymentInfoModel> ccPaymentInfoModelList = currentCustomer.getPaymentInfos().stream()
                .filter(paymentInfo -> Boolean.valueOf(true).equals(paymentInfo.isSaved()))
                .filter(paymentInfo -> Boolean.FALSE.equals(paymentInfo.getDuplicate()))
                .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(ccPaymentInfoModelList)) {
                customerAccountService.setDefaultPaymentInfo(currentCustomer, ccPaymentInfoModelList.get(ccPaymentInfoModelList.size() - 1));
            }
        }
    }

    /**
     * Test if a given {@link PaymentInfoModel} saved value attribute has the same value as the given
     *
     * @param paymentInfoModel - paymentInfoModel to be filtered
     * @param saved            - boolean value to compare with
     * @return true if paymentInfo saved attribute value is the same as the given boolean
     */
    protected Boolean filterBySavedValue(final PaymentInfoModel paymentInfoModel, final Boolean saved) {
        return !saved || paymentInfoModel.isSaved();
    }
}
