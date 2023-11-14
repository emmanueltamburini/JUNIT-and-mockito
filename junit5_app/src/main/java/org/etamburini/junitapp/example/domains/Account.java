package org.etamburini.junitapp.example.domains;

import org.etamburini.junitapp.example.exceptions.InsufficientMoneyException;

import java.math.BigDecimal;

public class Account {
    private String person;
    private BigDecimal amount;
    private Bank bank;

    public Account(final String person,final BigDecimal amount) {
        this.amount = amount;
        this.person = person;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(final  String person) {
        this.person = person;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final  BigDecimal amount) {
        this.amount = amount;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public void debit(final BigDecimal amount) {
        final BigDecimal newAmount = this.amount.subtract(amount);
        if (newAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientMoneyException("Insufficient Money");
        }

        this.amount = newAmount;
    }

    public void credit(final BigDecimal amount) {
        this.amount = this.amount.add(amount);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Account account)) {
            return false;
        }

        if (this.person == null || this.amount == null) {
            return false;
        }

        return this.person.equals(account.getPerson()) && this.amount.equals(this.getAmount());
    }
}
