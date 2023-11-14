package org.etamburini.junitapp.example.domains;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Bank {
    private String name;

    private List<Account> accounts;

    public Bank(String name) {
        this.name = name;
        this.accounts = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public void addAccount(final Account account) {
        accounts.add(account);
        account.setBank(this);
    }

    public void transfer(final Account origin, final Account destination, final BigDecimal amount) {
        origin.debit(amount);
        destination.credit(amount);
    }
}
