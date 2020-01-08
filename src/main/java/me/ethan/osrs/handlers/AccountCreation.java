package me.ethan.osrs.handlers;

import me.ethan.osrs.api.captcha.ReCaptchaV2;
import me.ethan.osrs.data.Constants;
import me.ethan.osrs.utils.Condition;
import me.ethan.osrs.utils.Random;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.File;

public class AccountCreation {
    private final JavascriptExecutor jsExecutor;
    private WebDriver webDriver;
    private String exePath = System.getProperty("user.home") + "\\Desktop\\drivers" + File.separator;
    private String email;

    public AccountCreation() {
        jsExecutor = (JavascriptExecutor) getWebDriver();
        this.email = generateName();
    }


    public String getResponse() {

        final ReCaptchaV2 reCaptcha = new ReCaptchaV2(Constants.CAPTCHA_KEY, Constants.GOOGLE_KEY, Constants.CREATE_LINK);
        final String captchaResponse = reCaptcha.solve();
        System.err.println(captchaResponse);
        getWebDriver().get(Constants.CREATE_LINK);
        Condition.sleep(2000);
        setEmail();
        Condition.sleep(250);
        setPassword();
        Condition.sleep(250);
        final WebElement day = findElement(By.name("day"));
        final WebElement month = findElement(By.name("month"));
        final WebElement year = findElement(By.name("year"));

        if (day == null || month == null || year == null) {
            return "ERROR";
        }

        setDay();
        Condition.sleep(250);
        setMonth();
        Condition.sleep(250);
        setYear();

        final WebElement submit = findElement(By.id("create-submit"));
        if (submit == null) {
            return "ERROR";
        }

        final JavascriptExecutor jsExecutor = (JavascriptExecutor) getWebDriver();
        jsExecutor.executeScript("document.getElementById('g-recaptcha-response').value=\"" + captchaResponse + "\";");
        jsExecutor.executeScript("onSubmit()");
        Condition.wait(new Condition.Check() {
            public boolean poll() {
                return createSuccess();
            }
        }, 100, 60);
        if (createSuccess()) {
            return "SUCCESS";
        }
        return "ERROR";
    }

    private void setDay() {
        jsExecutor.executeScript("document.getElementsByName('day')[0].value=\"" + "02" + "\";");
    }

    private void setMonth() {
        jsExecutor.executeScript("document.getElementsByName('month')[0].value=\"" + "02" + "\";");
    }

    private void setYear() {
        jsExecutor.executeScript("document.getElementsByName('year')[0].value=\"" + "1990" + "\";");
    }

    private void setEmail() {
        final WebElement email = findElement(By.id("create-email"));
        if (email == null)
            return;
        jsExecutor.executeScript("document.getElementById('create-email').value=\"" + this.email + "\";");
    }

    private void setPassword() {
        final WebElement pass = findElement(By.id("create-password"));
        if (pass == null)
            return;
        jsExecutor.executeScript("document.getElementById('create-password').value=\"" + Constants.BOT_PASSWORD + "\";");
    }

    public WebElement findElement(By by) {
        try {
            return getWebDriver().findElement(by);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean createSuccess() {
        if (getWebDriver() == null)
            return false;
        return getWebDriver().getPageSource().contains("Account Created");
    }

    private String generateName() {
        return Random.nextInt(0, 99) + Random.getRandomName(Random.nextInt(6, 15)) + Random.nextInt(0, 99) + "@mail.com";
    }

    public WebDriver getWebDriver() {
        if (webDriver != null)
            return webDriver;
        final FirefoxBinary firefoxBinary = new FirefoxBinary();
        firefoxBinary.addCommandLineOptions("");//--headless
        final File file = new File(exePath + "geckodriver.exe");
        final FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setBinary(firefoxBinary);
        System.setProperty("webdriver.gecko.driver", file.getAbsolutePath());
        this.webDriver = new FirefoxDriver(firefoxOptions);
        return this.webDriver;
    }

    public void setWebDriver(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public String getEmail() {
        return email;
    }
}
