package com.worldpay.worldpayresponsemock.builders;

import com.worldpay.internal.model.AccountTx;
import com.worldpay.internal.model.Amount;
import com.worldpay.internal.model.BookingDate;
import com.worldpay.internal.model.Date;
import com.worldpay.internal.model.Journal;

import java.time.LocalDate;

/**
 * Builder for the internal Journal model generated from the Worldpay DTD
 */
public final class JournalBuilder {

    private static final String IN_PROCESS_AUTHORISED = "inProcessAuthorised";
    private static final String BATCH_ID = "19";
    private static final LocalDate DATE = LocalDate.now();

    private String journalType;
    private Date journalBookingDate;
    private Amount amount;

    private JournalBuilder() {
    }

    /**
     * Factory method to create a builder
     * @return an Journal builder object
     */
    public static JournalBuilder aJournalBuilder() {
        return new JournalBuilder();
    }

    /**
     * Build with these given values
     * @param day
     * @param month
     * @param year
     * @return this builder
     */
    public JournalBuilder withBookingDate(String day, String month, String year) {
        journalBookingDate = buildDate(day, month, year);
        return this;
    }

    /**
     * Build with this given value
     * @param journalType
     * @return this builder
     */
    public JournalBuilder withJournalType(String journalType) {
        this.journalType = journalType;
        return this;
    }

    /**
     * Build with this given value
     * @param amount
     * @return this builder
     */
    public JournalBuilder withAmount(Amount amount) {
        this.amount = amount;
        return this;
    }

    /**
     * Build the Journal object based on the builders internal state
     * @return the internal Journal model
     */
    public Journal build() {
        final Journal journal = new Journal();

        final BookingDate bookingDate = new BookingDate();

        if (journalBookingDate == null) {
            journalBookingDate = buildDate(String.valueOf(DATE.getDayOfMonth()), String.valueOf(DATE.getMonthValue()), String.valueOf(DATE.getYear()));
        }

        bookingDate.setDate(journalBookingDate);
        journal.setJournalType(journalType);
        journal.setBookingDate(bookingDate);

        final AccountTx accountTx = new AccountTx();
        accountTx.setAccountType(IN_PROCESS_AUTHORISED);
        accountTx.setAmount(amount);
        accountTx.setBatchId(BATCH_ID);
        journal.getAccountTx().add(accountTx);

        return journal;
    }

    private Date buildDate(String day, String month, String year) {
        Date date = new Date();
        date.setDayOfMonth(day);
        date.setMonth(month);
        date.setYear(year);
        return date;
    }
}
