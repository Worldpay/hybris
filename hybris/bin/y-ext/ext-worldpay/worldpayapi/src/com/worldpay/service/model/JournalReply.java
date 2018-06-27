package com.worldpay.service.model;

import com.worldpay.enums.order.AuthorisedStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * POJO representation of the journal returned from a notification message
 */
public class JournalReply implements Serializable {

    private AuthorisedStatus journalType;
    private Date bookingDate;
    private List<AccountTransaction> accountTransactions;

    public AuthorisedStatus getJournalType() {
        return journalType;
    }

    public void setJournalType(AuthorisedStatus journalType) {
        this.journalType = journalType;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public List<AccountTransaction> getAccountTransactions() {
        return accountTransactions;
    }

    public void setAccountTransactions(List<AccountTransaction> accountTransactions) {
        this.accountTransactions = accountTransactions;
    }

    /**
     * Convenience method to add an {@link AccountTransaction} to the list of account transactions
     *
     * @param accountTransaction the accountTransaction to add to the list
     */
    public void addAccountTransaction(AccountTransaction accountTransaction) {
        if (accountTransactions == null) {
            accountTransactions = new ArrayList<>();
        }
        accountTransactions.add(accountTransaction);
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "JournalReply [journalType=" + journalType.name() + ", bookingDate=" + bookingDate + ", accountTransactions=" + accountTransactions + "]";
    }
}
