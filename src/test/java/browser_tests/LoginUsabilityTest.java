
package browser_tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TakesScreenshot;

public class LoginUsabilityTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String URL =
        "https://practicetestautomation.com/practice-test-login/";

    @BeforeEach
    void setUp() {
        driver = new SafariDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void login(String username, String password) {
        driver.get(URL);

        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("username")
        )).sendKeys(username);

        driver.findElement(By.id("password"))
              .sendKeys(password);

        driver.findElement(By.id("submit")).click();
    }

    private void saveScreenshot(String filename) throws IOException {
        Path folder = Path.of("evidence");
        Files.createDirectories(folder);

        Files.copy(
            ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath(),
            folder.resolve(filename),
            StandardCopyOption.REPLACE_EXISTING
        );
    }

    // UT-01: Successful login
    @Test
    void validLoginTest() throws IOException {
        login("student", "Password123");

        wait.until(ExpectedConditions.urlContains(
            "logged-in-successfully"
        ));

        WebElement logout = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.linkText("Log out")
            )
        );

        assertTrue(logout.isDisplayed());

        saveScreenshot("successful_login.png");
        System.out.println("PASS: Successful login");
    }

    // UT-02: Incorrect username
    @Test
    void invalidUsernameTest() throws IOException {
        login("incorrectUser", "Password123");

        WebElement error = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.id("error")
            )
        );

        assertEquals("Your username is invalid!", error.getText());

        saveScreenshot("invalid_username.png");
        System.out.println("PASS: Invalid username feedback");
    }

    // UT-03: Incorrect password
    @Test
    void invalidPasswordTest() throws IOException {
        login("student", "incorrectPassword");

        WebElement error = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.id("error")
            )
        );

        assertEquals("Your password is invalid!", error.getText());

        saveScreenshot("invalid_password.png");
        System.out.println("PASS: Invalid password feedback");
    }
}
