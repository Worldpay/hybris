package com.worldpay.service.response.transform.impl;

import com.worldpay.internal.model.*;
import com.worldpay.internal.model.Error;
import com.worldpay.service.model.*;
import com.worldpay.service.model.Address;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.Date;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.model.token.DeleteTokenReply;
import com.worldpay.service.model.token.TokenReply;
import com.worldpay.service.model.token.UpdateTokenReply;
import com.worldpay.service.response.ServiceResponse;
import com.worldpay.service.response.transform.ServiceResponseTransformerHelper;

import java.util.List;

/**
 * Helper class with commonly used methods by the {@link ServiceResponseTransformerHelper} objects
 */
public class DefaultServiceResponseTransformerHelper implements ServiceResponseTransformerHelper {

    /**
     * Check the reply for any errors and if so add the details to the supplied response
     *
     * @param response response to add {@link ErrorDetail} to
     * @param reply    reply to interrogate for errors
     * @return true if errors exist, else false
     */
    @Override
    public boolean checkForError(ServiceResponse response, Reply reply) {
        final Object replyType = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken().get(0);
        if (replyType instanceof com.worldpay.internal.model.Error) {
            final com.worldpay.internal.model.Error intError = (com.worldpay.internal.model.Error) replyType;
            final ErrorDetail errorDtl = buildErrorDetail(intError);
            response.setError(errorDtl);
            return true;
        } else if (replyType instanceof OrderStatus) {
            OrderStatus ordStatus = (OrderStatus) replyType;
            Object statusType = ordStatus.getReferenceOrBankAccountOrErrorOrPaymentOrCardBalanceOrPaymentAdditionalDetailsOrBillingAddressDetailsOrOrderModificationOrJournalOrRequestInfoOrFxApprovalRequiredOrZappRTPOrContent().get(0);
            if (statusType instanceof com.worldpay.internal.model.Error) {
                final com.worldpay.internal.model.Error intError = (com.worldpay.internal.model.Error) statusType;
                final ErrorDetail errorDtl = buildErrorDetail(intError);
                response.setError(errorDtl);
                return true;
            }
        }
        return false;
    }

    private ErrorDetail buildErrorDetail(com.worldpay.internal.model.Error intError) {
        ErrorDetail errorDtl = null;
        if (intError != null) {
            errorDtl = new ErrorDetail(intError.getCode(), intError.getvalue());
        }
        return errorDtl;
    }

    /**
     * Build a {@link PaymentReply representation} given an internal representation of the payment
     *
     * @param intPayment intPayment to transform
     * @return PaymentReply representation
     */
    @Override
    public PaymentReply buildPaymentReply(final Payment intPayment) {
        PaymentReply paymentReply = new PaymentReply();
        paymentReply.setMethodCode(intPayment.getPaymentMethod());
        final PaymentMethodDetail paymentMethodDetail = intPayment.getPaymentMethodDetail();
        if (paymentMethodDetail != null) {
            paymentReply.setCardDetails(transformCard(paymentMethodDetail.getCard(), intPayment.getCardHolderName()));
        }

        com.worldpay.internal.model.Amount intAmount = intPayment.getAmount();
        final Amount amount = transformAmount(intAmount);
        paymentReply.setAmount(amount);
        paymentReply.setAuthStatus(AuthorisedStatus.getAuthorisedStatus(intPayment.getLastEvent()));
        CVCResultCode intCvcResultCode = intPayment.getCVCResultCode();
        if (intCvcResultCode != null) {
            String cvcResultDescription = intCvcResultCode.getDescription().get(0);
            paymentReply.setCvcResultDescription(cvcResultDescription);
        }
        final List<Balance> balanceList = intPayment.getBalance();
        if (balanceList != null && !balanceList.isEmpty()) {
            Balance balance = balanceList.get(0);
            paymentReply.setBalanceAccountType(balance.getAccountType());
            com.worldpay.internal.model.Amount intBalAmount = balance.getAmount();
            Amount balAmount = transformAmount(intBalAmount);
            paymentReply.setBalanceAmount(balAmount);
        }

        if (intPayment.getISO8583ReturnCode() != null) {
            paymentReply.setReturnCode(intPayment.getISO8583ReturnCode().getCode());
        }
        paymentReply.setRiskScore(intPayment.getRiskScore());

        setAAVCodes(intPayment, paymentReply);

        paymentReply.setRefundReference(intPayment.getRefundReference());

        final AuthorisationId authorisationId = intPayment.getAuthorisationId();
        if (authorisationId != null) {
            paymentReply.setAuthorisationId(authorisationId.getId());
            final String authorisedBy = intPayment.getAuthorisationId().getBy();
            if (authorisedBy != null) {
                paymentReply.setAuthorisedBy(authorisedBy);
            }
        }

        return paymentReply;
    }

    @Override
    public UpdateTokenReply buildUpdateTokenReply(final UpdateTokenReceived intUpdateTokenReceived) {
        final UpdateTokenReply updateTokenReply = new UpdateTokenReply();
        updateTokenReply.setPaymentTokenId(intUpdateTokenReceived.getPaymentTokenID());
        return updateTokenReply;
    }

    @Override
    public DeleteTokenReply buildDeleteTokenReply(final DeleteTokenReceived intDeleteTokenReceived) {
        final DeleteTokenReply deleteTokenReply = new DeleteTokenReply();
        deleteTokenReply.setPaymentTokenId(intDeleteTokenReceived.getPaymentTokenID());
        return deleteTokenReply;
    }

    @Override
    public TokenReply buildTokenReply(final Token intToken) {
        final TokenReply tokenReply = new TokenReply();
        tokenReply.setAuthenticatedShopperID(intToken.getAuthenticatedShopperID());
        tokenReply.setTokenEventReference(intToken.getTokenEventReference());

        final List<Object> tokenInformationFields = intToken.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrError();
        for (final Object tokenInformationField : tokenInformationFields) {
            if (tokenInformationField instanceof TokenReason) {
                tokenReply.setTokenReason(((TokenReason) tokenInformationField).getvalue());
            } else if (tokenInformationField instanceof TokenDetails) {
                final com.worldpay.service.model.token.TokenDetails tokenDetails = new com.worldpay.service.model.token.TokenDetails();
                final TokenDetails intTokenDetails = (TokenDetails) tokenInformationField;
                final TokenReason intTokenReason = intTokenDetails.getTokenReason();
                if (intTokenReason != null) {
                    tokenDetails.setTokenReason(intTokenDetails.getTokenReason().getvalue());
                }
                tokenDetails.setTokenEventReference(intTokenDetails.getTokenEventReference());
                tokenDetails.setTokenEvent(intTokenDetails.getTokenEvent());
                tokenDetails.setPaymentTokenExpiry(transformDate(intTokenDetails.getPaymentTokenExpiry().getDate()));
                tokenDetails.setPaymentTokenID(intTokenDetails.getPaymentTokenID());
                tokenDetails.setReportingTokenID(intTokenDetails.getReportingTokenID());
                if (intTokenDetails.getReportingTokenExpiry() != null) {
                    tokenDetails.setReportingTokenExpiry(transformDate(intTokenDetails.getReportingTokenExpiry().getDate()));
                }
                tokenReply.setTokenDetails(tokenDetails);
            } else if (tokenInformationField instanceof PaymentInstrument) {
                final Object paymentInstrument = ((PaymentInstrument) tokenInformationField).getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetails().get(0);
                if (paymentInstrument instanceof CardDetails) {
                    final com.worldpay.service.model.payment.Card card = transformCard(((CardDetails) paymentInstrument));
                    tokenReply.setPaymentInstrument(card);
                } else if (paymentInstrument instanceof Paypal) {
                    tokenReply.setPaypalDetails(((Paypal) paymentInstrument).getvalue());
                }
            } else if (tokenInformationField instanceof Error) {
                tokenReply.setError(buildErrorDetail((Error) tokenInformationField));
            }
        }
        return tokenReply;
    }

    @Override
    public WebformRefundReply buildWebformRefundReply(final ShopperWebformRefundDetails intShopperWebformRefundDetails) {
        final WebformRefundReply webformRefundReply = new WebformRefundReply();
        webformRefundReply.setWebformURL(intShopperWebformRefundDetails.getWebformURL());
        webformRefundReply.setWebformId(intShopperWebformRefundDetails.getWebformId());
        webformRefundReply.setWebformStatus(intShopperWebformRefundDetails.getWebformStatus());
        webformRefundReply.setPaymentId(intShopperWebformRefundDetails.getPaymentId());
        webformRefundReply.setReason(intShopperWebformRefundDetails.getReason());
        webformRefundReply.setAmount(transformAmount(intShopperWebformRefundDetails.getAmount()));
        webformRefundReply.setRefundId(intShopperWebformRefundDetails.getRefundId());
        return webformRefundReply;
    }

    private Amount transformAmount(final com.worldpay.internal.model.Amount intAmount) {
        return new Amount(intAmount.getValue(), intAmount.getCurrencyCode(), intAmount.getExponent(), DebitCreditIndicator.getDebitCreditIndicator(intAmount.getDebitCreditIndicator()));
    }

    private void setAAVCodes(final Payment intPayment, final PaymentReply paymentReply) {
        final AAVCardholderNameResultCode aavCardholderNameResultCode = intPayment.getAAVCardholderNameResultCode();
        if (aavCardholderNameResultCode != null && aavCardholderNameResultCode.getDescription() != null && aavCardholderNameResultCode.getDescription().size() > 0) {
            paymentReply.setAavCardholderNameResultCode(aavCardholderNameResultCode.getDescription().get(0));
        }
        final AAVAddressResultCode aavAddressResultCode = intPayment.getAAVAddressResultCode();
        if (aavAddressResultCode != null && aavAddressResultCode.getDescription() != null && aavAddressResultCode.getDescription().size() > 0) {
            paymentReply.setAavAddressResultCode(aavAddressResultCode.getDescription().get(0));
        }
        final AAVEmailResultCode aavEmailResultCode = intPayment.getAAVEmailResultCode();
        if (aavEmailResultCode != null && aavEmailResultCode.getDescription() != null && aavEmailResultCode.getDescription().size() > 0) {
            paymentReply.setAavEmailResultCode(aavEmailResultCode.getDescription().get(0));
        }
        final AAVPostcodeResultCode aavPostcodeResultCode = intPayment.getAAVPostcodeResultCode();
        if (aavPostcodeResultCode != null && aavPostcodeResultCode.getDescription() != null && aavPostcodeResultCode.getDescription().size() > 0) {
            paymentReply.setAavPostcodeResultCode(aavPostcodeResultCode.getDescription().get(0));
        }
        final AAVTelephoneResultCode aavTelephoneResultCode = intPayment.getAAVTelephoneResultCode();
        if (aavTelephoneResultCode != null && aavTelephoneResultCode.getDescription() != null && aavTelephoneResultCode.getDescription().size() > 0) {
            paymentReply.setAavTelephoneResultCode(aavTelephoneResultCode.getDescription().get(0));
        }
    }

    private com.worldpay.service.model.payment.Card transformCard(final CardDetails intCardDetails) {
        final Derived derived = intCardDetails.getDerived();
        if (derived != null) {
            final String cvc = intCardDetails.getCvc() == null ? null : intCardDetails.getCvc().getvalue();
            Date expiryDate = null;
            if (intCardDetails.getExpiryDate() != null) {
                expiryDate = transformDate(intCardDetails.getExpiryDate().getDate());
            }
            final Address address = transformAddress(intCardDetails.getCardAddress());
            return new com.worldpay.service.model.payment.Card(PaymentType.getPaymentType(derived.getCardBrand()), derived.getObfuscatedPAN(), cvc, expiryDate, transformCardHolderName(intCardDetails.getCardHolderName()), address, null, null, null);
        }
        return null;
    }

    private com.worldpay.service.model.payment.Card transformCard(final Card intCard, final CardHolderName cardHolderName) {
        Date transformedDate = null;
        String cardHolderNameValue = transformCardHolderName(cardHolderName);
        if (intCard.getExpiryDate() != null) {
            transformedDate = transformDate(intCard.getExpiryDate().getDate());
        }
        return new com.worldpay.service.model.payment.Card(PaymentType.getPaymentType(intCard.getType()),
                intCard.getNumber(), null, transformedDate, cardHolderNameValue, null, null, null, null);
    }

    private String transformCardHolderName(final CardHolderName cardHolderName) {
        if (cardHolderName != null && cardHolderName.getvalue() != null && cardHolderName.getvalue().length() > 0) {
            return cardHolderName.getvalue();
        }
        return null;
    }

    private Address transformAddress(final com.worldpay.internal.model.CardAddress intCardAddress) {
        if (intCardAddress != null && intCardAddress.getAddress() != null) {
            com.worldpay.internal.model.Address intAddress = intCardAddress.getAddress();
            final List<Object> streetOrHouseNameOrHouseNumberOrHouseNumberExtensionOrAddress1OrAddress2OrAddress3 = intAddress.getStreetOrHouseNameOrHouseNumberOrHouseNumberExtensionOrAddress1OrAddress2OrAddress3();
            Address address = new Address();
            for (final Object intAddressDetails : streetOrHouseNameOrHouseNumberOrHouseNumberExtensionOrAddress1OrAddress2OrAddress3) {
                if (intAddressDetails instanceof Street) {
                    address.setStreet(((Street) intAddressDetails).getvalue());
                }
                if (intAddressDetails instanceof HouseName) {
                    address.setHouseName(((HouseName) intAddressDetails).getvalue());
                }
                if (intAddressDetails instanceof HouseNumber) {
                    address.setHouseNumber(((HouseNumber) intAddressDetails).getvalue());
                }
                if (intAddressDetails instanceof HouseNumberExtension) {
                    address.setHouseNumberExtension(((HouseNumberExtension) intAddressDetails).getvalue());
                }
                if (intAddressDetails instanceof Address1) {
                    address.setAddress1(((Address1) intAddressDetails).getvalue());
                }
                if (intAddressDetails instanceof Address2) {
                    address.setAddress2(((Address2) intAddressDetails).getvalue());
                }
                if (intAddressDetails instanceof Address3) {
                    address.setAddress3(((Address3) intAddressDetails).getvalue());
                }
            }
            address.setFirstName(intAddress.getFirstName());
            address.setLastName(intAddress.getLastName());
            address.setCity(intAddress.getCity());
            address.setCountryCode(intAddress.getCountryCode());
            address.setPostalCode(intAddress.getPostalCode());
            address.setState(intAddress.getState());
            address.setTelephoneNumber(intAddress.getTelephoneNumber());
            return address;
        }
        return null;
    }


    private Date transformDate(final com.worldpay.internal.model.Date intDate) {
        return new Date(intDate.getDayOfMonth(), intDate.getMonth(), intDate.getYear(), intDate.getHour(), intDate.getMinute(), intDate.getSecond());
    }

    @Override
    public JournalReply buildJournalReply(Journal intJournal) {
        JournalReply journalReply = new JournalReply();
        journalReply.setJournalType(AuthorisedStatus.getAuthorisedStatus(intJournal.getJournalType()));
        populateBookingDate(intJournal, journalReply);
        populateAccountTransactions(intJournal, journalReply);
        return journalReply;
    }

    private void populateBookingDate(Journal intJournal, JournalReply journalReply) {
        BookingDate bookingDate = intJournal.getBookingDate();
        if (bookingDate != null) {
            com.worldpay.internal.model.Date intDate = bookingDate.getDate();
            if (intDate != null) {

                com.worldpay.service.model.Date date = transformDate(intDate);

                journalReply.setBookingDate(date);
            }
        }
    }

    private void populateAccountTransactions(Journal intJournal, JournalReply journalReply) {
        List<AccountTx> accountTxs = intJournal.getAccountTx();
        for (AccountTx accountTx : accountTxs) {
            AccountTransaction accountTransaction = new AccountTransaction();

            accountTransaction.setAccountType(accountTx.getAccountType());
            accountTransaction.setBatchId(accountTx.getBatchId());

            com.worldpay.internal.model.Amount amount = accountTx.getAmount();

            if (amount != null) {
                accountTransaction.setAmount(
                        transformAmount(amount));
            }

            journalReply.addAccountTransaction(accountTransaction);
        }
    }
}
