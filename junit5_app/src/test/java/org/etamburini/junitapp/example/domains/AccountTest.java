package org.etamburini.junitapp.example.domains;

import org.etamburini.junitapp.example.exceptions.InsufficientMoneyException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountTest {

    private Account account;
    @BeforeEach
    void initMethodTest() {
        System.out.println("Init Method");
        this.account = new Account("Testing", new BigDecimal("1000.12345"));
    }

    @AfterEach
    void tearDown() {
        System.out.println("Ending Method");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Init tests");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Ending tests");
    }

    @Test
    @DisplayName("Testing name account")
    void testAccountName() {
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
        assertNotNull(account.getAmount());
        assertEquals(1000.12345, account.getAmount().doubleValue());
        assertFalse(account.getAmount().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(account.getAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Testing equal operator overwrite")
    void testRefAccount() {
        final Account account2 = new Account("Testing", new BigDecimal("1000.12345"));

        assertEquals(account, account2);
    }

    @Test
    void testDebit() {
        account.debit(new BigDecimal(100));

        assertNotNull(account.getAmount());
        assertEquals(900, account.getAmount().intValue());
        assertEquals("900.12345", account.getAmount().toPlainString());
    }


    @Test
    void testCredit() {
        account.credit(new BigDecimal(100));

        assertNotNull(account.getAmount());
        assertEquals(1100, account.getAmount().intValue());
        assertEquals("1100.12345", account.getAmount().toPlainString());
    }

    @Test
    void testInsufficientMoneyAccountException() {
        final Exception exception = assertThrows(InsufficientMoneyException.class, () -> account.debit(new BigDecimal("1500")));

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
                            .orElse(null)
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
                            .orElse(null)
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

    @Test
    @EnabledOnOs(OS.LINUX)
    void testOnlyLinux() {}

    @Test
    @EnabledOnOs({OS.WINDOWS, OS.MAC})
    void testOnlyWindowsAndMac() {}

    @Test
    @DisabledOnOs(OS.LINUX)
    void testDisableOnLinux() {
    }

    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void testOnlyJdk8() {
    }

    @Test
    @EnabledOnJre(JRE.JAVA_21)
    void testOnlyJdk21() {
    }

    @Test
    @DisabledOnJre(JRE.JAVA_21)
    void testDisableJdk21() {
    }

    @Test
    void testPrintSystemProperties() {
        final Properties properties = System.getProperties();
        properties.forEach((key, value) -> {
            System.out.println(key + " : " + value);
        });
    }

    @Test
    @EnabledIfSystemProperty(named = "java.version", matches = "21.0.1")
    void testJdkVersion() {
    }

    @Test
    @EnabledIfSystemProperty(named = "java.version", matches = ".*")
    void testJdkRegularExpressionVersion() {
    }

    @Test
    @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
    void testOnly64() {
    }

    @Test
    @EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
    void testNo64() {
    }

    @Test
    @EnabledIfSystemProperty(named = "user.name", matches = "emmanuel")
    void testUsername() {
    }

    @Test
    @EnabledIfSystemProperty(named = "ENV", matches = "dev")
    void testDev() {
    }

    @Test
    void testPrintEnvironmentVar() {
        final Map<String, String> getenv = System.getenv();
        getenv.forEach((key, value) -> System.out.println(key + " : " + value));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "GNOME_SHELL_SESSION_MODE", matches = "ubuntu")
    void testGnomeShellSessionMode() {
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "dev")
    void testEnv() {
    }

    @Test
    @DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "prod")
    void testProd() {
    }

    @Test
    @DisplayName("Testing amount account with Assumptions")
    void testAmountAccountDev() {
        final boolean isDev = "dev".equals(System.getProperty("ENV"));

        assumeTrue(isDev);

        assertNotNull(account.getAmount());
        assertEquals(1000.12345, account.getAmount().doubleValue());
        assertFalse(account.getAmount().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(account.getAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Testing amount account with Assumptions 2")
    void testAmountAccountDev2() {
        final boolean isDev = "de".equals(System.getProperty("ENV"));

        assumingThat(isDev, () -> {
            assertNotNull(account.getAmount());
            assertEquals(1000.12345, account.getAmount().doubleValue());
            assertFalse(account.getAmount().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(account.getAmount().compareTo(BigDecimal.ZERO) > 0);
        });
    }
}