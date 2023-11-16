package org.etamburini.junitapp.example.domains;

import org.etamburini.junitapp.example.exceptions.InsufficientMoneyException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS) NOTE: If this is activated, the attributes of the class will be the same for every test
class AccountTest {
    private Account account;
    private TestInfo testInfo;
    private TestReporter testReporter;

    @BeforeEach
    void initMethodTest(final TestInfo testInfo, final TestReporter testReporter) {
        System.out.println("Init Method");
        this.account = new Account("Testing", new BigDecimal("1000.12345"));
        testReporter.publishEntry("Running: " + testInfo.getDisplayName() + " - " + testInfo.getTestMethod().orElse(null).getName()
                + " with tags " + testInfo.getTags());
        this.testInfo = testInfo;
        this.testReporter = testReporter;
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

    @Nested
    @DisplayName("Account functionality test")
    @Tag("Account")
    @Tag("Test1")
    class AccountFunctionalityTests {
        @Test
        @DisplayName("Testing name account")
        void testAccountName(final TestInfo testInfo, final TestReporter testReporter) {
            if (testInfo.getTags().contains("Account")) {
                testReporter.publishEntry("Account tag is included");
            }

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
    }

    @Nested
    @Tag("Account")
    class OperationAccountsTests {
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
        void testTransferBalanceAccount() {
            final Account account1 = new Account("Testing 1", new BigDecimal("2500"));
            final Account account2 = new Account("Testing 2", new BigDecimal("1500.01"));

            final Bank bank = new Bank("Testing Bank");
            bank.transfer(account1, account2, new BigDecimal("500"));

            assertEquals("2000", account1.getAmount().toPlainString());
            assertEquals("2000.01", account2.getAmount().toPlainString());
        }
    }

    @Nested
    @Tag("Account")
    class ExceptionTests {
        @Test
        void testInsufficientMoneyAccountException() {
            final Exception exception = assertThrows(InsufficientMoneyException.class, () -> account.debit(new BigDecimal("1500")));

            final String currentMessage = exception.getMessage();
            final String waitedMessage = "Insufficient Money";

            assertEquals(currentMessage, waitedMessage);
        }
    }

    @Nested
    @Tag("Account")
    class RelationTests {
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
    }

    @Nested
    class OtherTests {
        @Test
        @Tag("Test1")
        @Disabled
        void testFailed() {
            fail();
        }
    }

    @Nested
    class OperatingSystemTests {
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
    }

    @Nested
    class JavaVersionTests {
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
    }

    @Nested
    class SystemPropertiesTests {
        @Test
        void testPrintSystemProperties() {
            final Properties properties = System.getProperties();
            properties.forEach((key, value) -> {
                System.out.println(key + " : " + value);
            });
        }

        @Test
        @Tag("Test1")
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
    }

    @Nested
    class EnvironmentVariablesTests {
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
    }

    @Nested
    class AssumptionsTests {
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

    @Nested
    @Tag("LoopsTests")
    class LoopsTests {
        static List<String> amountList () {
            return Arrays.asList("100", "200", "300", "500", "1000");
        }

        @DisplayName("Test Debit Repetition")
        @RepeatedTest(value= 5, name = "{displayName}: Repetition test: {currentRepetition} of {totalRepetitions}")
        void testDebitRepeated(final RepetitionInfo info) {
            if (info.getCurrentRepetition() == 3) {
                System.out.println("This is the repetition: " + info.getCurrentRepetition() + " of " + info.getTotalRepetitions());
            }
            account.debit(new BigDecimal(100));

            assertNotNull(account.getAmount());
            assertEquals(900, account.getAmount().intValue());
            assertEquals("900.12345", account.getAmount().toPlainString());
        }

        @DisplayName("Test Debit Parameterized")
        @ParameterizedTest(name = "{displayName}: Parameterized test {index} with value: {0} - {argumentsWithNames}")
        @ValueSource(strings = {"100", "200", "300", "500", "1000"})
        void testDebitParameterized(final String amount) {
            account.debit(new BigDecimal(amount));

            assertNotNull(account.getAmount());
            assertTrue(account.getAmount().compareTo(BigDecimal.ZERO) > 0);
        }

        @DisplayName("Test Debit Parameterized with doubles")
        @ParameterizedTest(name = "{displayName}: Parameterized test {index} with value: {0} -{argumentsWithNames}")
        @ValueSource(doubles = {100, 200, 300, 500, 1000})
        void testDebitParameterizedWithDouble(final double amount) {
            account.debit(new BigDecimal(amount));

            assertNotNull(account.getAmount());
            assertTrue(account.getAmount().compareTo(BigDecimal.ZERO) > 0);
        }

        @DisplayName("Test Debit Parameterized with csv source")
        @ParameterizedTest(name = "{displayName}: Parameterized test {index} with value: {0} - {argumentsWithNames}")
        @CsvSource({"1,100", "2,200", "3,300", "4,500", "5,1000"})
        void testDebitParameterizedCsvSource(final String index, final String amount) {
            account.debit(new BigDecimal(amount));
            System.out.println(index + " -> " + amount);

            assertNotNull(account.getAmount());
            assertTrue(account.getAmount().compareTo(BigDecimal.ZERO) > 0);
        }

        @DisplayName("Test Debit Parameterized with csv file")
        @ParameterizedTest(name = "{displayName}: Parameterized test {index} with value: {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        void testDebitParameterizedCsvFile(final String amount) {
            account.debit(new BigDecimal(amount));

            assertNotNull(account.getAmount());
            assertTrue(account.getAmount().compareTo(BigDecimal.ZERO) > 0);
        }

        @DisplayName("Test Debit Parameterized with method source")
        @ParameterizedTest(name = "{displayName}: Parameterized test {index} with value: {0} - {argumentsWithNames}")
        @MethodSource("amountList")
        void testDebitParameterizedMethodSource(final String amount) {
            account.debit(new BigDecimal(amount));

            assertNotNull(account.getAmount());
            assertTrue(account.getAmount().compareTo(BigDecimal.ZERO) > 0);
        }

        @DisplayName("Test Debit Parameterized with csv source more than one parameters")
        @ParameterizedTest(name = "{displayName}: Parameterized test {index} with value: {0} - {argumentsWithNames}")
        @CsvSource({"200,100,test1,test1", "250,200,test2,test2", "300,300,test3,test3", "510,500,test4,test4", "1000.01,1000.01,test5,test5"})
        void testDebitParameterizedCsvSourceMoreThanOne(final String amount, final String amountToDebit, final String waitedName, final String currentName) {
            account.setAmount(new BigDecimal(amount));
            account.debit(new BigDecimal(amountToDebit));
            account.setPerson(currentName);
            System.out.println(amount + " -> " + amountToDebit);

            assertNotNull(account.getAmount());
            assertNotNull(account.getPerson());

            assertEquals(account.getPerson(), waitedName);
            assertTrue(account.getAmount().compareTo(BigDecimal.ZERO) >= 0);
        }

        @DisplayName("Test Debit Parameterized with csv file more than one parameters")
        @ParameterizedTest(name = "{displayName}: Parameterized test {index} with value: {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data2.csv")
        void testDebitParameterizedCsvFileMoreThanOne(final String amount, final String amountToDebit, final String waitedName, final String currentName) {
            account.setAmount(new BigDecimal(amount));
            account.debit(new BigDecimal(amountToDebit));
            account.setPerson(currentName);
            System.out.println(amount + " -> " + amountToDebit);

            assertNotNull(account.getAmount());
            assertNotNull(account.getPerson());

            assertEquals(account.getPerson(), waitedName);
            assertTrue(account.getAmount().compareTo(BigDecimal.ZERO) >= 0);
        }
    }

    @Nested
    @Tag("Timeout")
    class TimeoutTests {
        @Test
        @Timeout(2)
        @DisplayName("Testing with timeout")
        void testTimeout() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);
        }

        @Test
        @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
        @DisplayName("Testing with timeout two parameters")
        void testTimeoutTwoParameters() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(450);
        }

        @Test
        void testTimeoutAssertions() {
            assertTimeout(Duration.ofSeconds(2), () -> {
                TimeUnit.SECONDS.sleep(1);
            });
        }
    }

}