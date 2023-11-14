package org.etamburini.junitapp.example.domains;

import org.etamburini.junitapp.example.exceptions.InsufficientMoneyException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    @Test
    @DisplayName("Testing name account")
    void testAccountName() {
        final Account account = new Account("Testing", new BigDecimal("1000.12345"));
        //account.setPerson("Testing");

        final String waitedValue = "Testing";
        final String currentValue = account.getPerson();
        assertNotNull(currentValue, () -> "The account cannot be null");
        assertEquals(waitedValue, currentValue, () -> "The account name is not as expected: it was expected " + waitedValue + " but it was received" + currentValue);
        assertTrue(currentValue.equals("Testing"), () ->"The account name must be equal to the real name");
    }

    @Test
    @DisplayName("Testing amount account")
    void testAmountAccount() {
        final Account account = new Account("Testing", new BigDecimal("1000.12345"));
        assertNotNull(account.getAmount());
        assertEquals(1000.12345, account.getAmount().doubleValue());
        assertFalse(account.getAmount().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(account.getAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Testing equal operator overwrite")
    void testRefAccount() {
        final Account account = new Account("Testing", new BigDecimal("1000.12345"));
        final Account account2 = new Account("Testing", new BigDecimal("1000.12345"));

        assertEquals(account, account2);
    }

    @Test
    void testDebit() {
        final Account account = new Account("Testing", new BigDecimal("1000.12345"));
        account.debit(new BigDecimal(100));

        assertNotNull(account.getAmount());
        assertEquals(900, account.getAmount().intValue());
        assertEquals("900.12345", account.getAmount().toPlainString());
    }


    @Test
    void testCredit() {
        final Account account = new Account("Testing", new BigDecimal("1000.12345"));
        account.credit(new BigDecimal(100));

        assertNotNull(account.getAmount());
        assertEquals(1100, account.getAmount().intValue());
        assertEquals("1100.12345", account.getAmount().toPlainString());
    }

    @Test
    void testInsufficientMoneyAccountException() {
        final Account account = new Account("Testing", new BigDecimal("1000.12345"));
        final Exception exception = assertThrows(InsufficientMoneyException.class, () -> {
            account.debit(new BigDecimal("1500"));
        });

        final String currentMessage = exception.getMessage();
        final String waitedMessage = "Insufficient Money";

        assertEquals(currentMessage, waitedMessage);
    }

    @Test
    void testTransferBalanceAccount() {
        final Account account1 = new Account("Testing 1", new BigDecimal("2500"));
        final Account account2 = new Account("Testing 2", new BigDecimal("1500.01"));

        final Bank bank = new Bank("Testing Bank");
        bank.transfer(account1, account2, new BigDecimal("500"));

        assertEquals("2000", account1.getAmount().toPlainString());
        assertEquals("2000.01", account2.getAmount().toPlainString());
    }

    @Test
    @DisplayName("Testing bank and account relations with assertAll")
    void testRelationAccountBank() {
        final Account account1 = new Account("Testing 1", new BigDecimal("2500"));
        final Account account2 = new Account("Testing 2", new BigDecimal("1500.01"));

        final Bank bank = new Bank("Testing Bank");
        bank.addAccount(account1);
        bank.addAccount(account2);
        bank.transfer(account1, account2, new BigDecimal("500"));

        assertAll(
                () -> {
                    assertEquals("2000", account1.getAmount().toPlainString(), () -> "The account 1 did not have the expected value");
                    assertEquals("2000.01", account2.getAmount().toPlainString(), () -> "The account 2 did not have the expected value");
                },
                () ->
                    assertEquals(2, bank.getAccounts().size()),
                () -> {
                    assertEquals("Testing Bank", account1.getBank().getName());
                    assertEquals("Testing Bank", account2.getBank().getName());
                },
                () -> {
                    assertTrue(bank.getAccounts().stream()
                            .anyMatch(currentAccount -> currentAccount.getPerson().equals("Testing 1"))
                    );
                    assertEquals("Testing 1", bank.getAccounts().stream()
                            .filter(currentAccount -> currentAccount.getPerson().equals("Testing 1"))
                            .findFirst()
                            .get()
                            .getPerson()
                    );
                },
                () -> {
                    assertTrue(bank.getAccounts().stream()
                            .anyMatch(currentAccount -> currentAccount.getPerson().equals("Testing 2"))
                    );
                    assertEquals("Testing 2", bank.getAccounts().stream()
                            .filter(currentAccount -> currentAccount.getPerson().equals("Testing 2"))
                            .findFirst()
                            .get()
                            .getPerson()
                    );
                }
        );
    }

    @Test
    @Disabled
    void testFailed() {
        fail();
    }
}