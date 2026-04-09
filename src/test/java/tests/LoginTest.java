package tests;

import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import pages.LoginPage;
import pages.MenuPage;
import java.time.Duration;

public class LoginTest {

    private WebDriver driver;
    private LoginPage loginPage;
    private MenuPage  menuPage;

    @Before
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        driver.get("https://app02.makernocode.dev/open.do?sys=TL6");

        loginPage = new LoginPage(driver);
        menuPage  = new MenuPage(driver);
    }

    @Test
    public void teste01_LoginComSucesso() {
        loginPage.realizarLogin("qualidade", "1");
        Assert.assertTrue("Login falhou — menu principal não foi encontrado.",
                menuPage.validarLoginComSucesso());
    }

    @After
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
