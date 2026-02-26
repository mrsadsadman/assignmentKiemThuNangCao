package com.dmx.test.base;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.time.Duration;

public class BaseTest {

    protected WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void setUp() {

        ChromeOptions options = new ChromeOptions();

        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        // stability
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        // avoid automation detection
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");

        boolean isCI = System.getenv("CI") != null;

        if (isCI) {

            System.out.println("Running in GitHub Actions CI");

            options.addArguments("--headless=new");

            // realistic user agent
            options.addArguments(
                "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/145.0.0.0 Safari/537.36"
            );
        }

        driver = new ChromeDriver(options);

        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(10));

        driver.manage().timeouts()
                .pageLoadTimeout(Duration.ofSeconds(60));

        try {

            String url =
                "https://www.dienmayxanh.com/lich-su-mua-hang/dang-nhap";

            System.out.println("Opening: " + url);

            driver.get(url);

            System.out.println("Page title: " + driver.getTitle());

            // detect Cloudflare block
            if (driver.getTitle().toLowerCase().contains("attention")
                    || driver.getPageSource().toLowerCase().contains("cloudflare")) {

                throw new RuntimeException("Cloudflare detected");

            }

        }
        catch (Exception e) {

            if (isCI) {

                System.out.println(
                    "CI cannot access website. Marking tests skipped."
                );

                throw new SkipException(
                    "Website blocked GitHub runner: " + e.getMessage()
                );

            } else {

                throw new RuntimeException(e);

            }
        }
    }


    @AfterClass(alwaysRun = true)
    public void tearDown() {

        if (driver != null) {

            driver.quit();

            System.out.println("Driver closed");

        }
    }
}
