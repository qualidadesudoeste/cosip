package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class MenuPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Menu lateral do COSIP (visível na screenshot: app02.makernocode.dev/open.do?sys=TL6)
    private final By avatarUsuarioBy = By.id("UserInfo");
    private final By campoBuscaBy    = By.xpath("//input[@placeholder='Buscar no menu']");

    public MenuPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public boolean validarLoginComSucesso() {
        try { Thread.sleep(3000); } catch (InterruptedException e) {}
        try {
            driver.switchTo().defaultContent();
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.name("mainsystem")));
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.tagName("iframe")));
            if (elementoExiste(avatarUsuarioBy) || elementoExiste(campoBuscaBy)) return true;
        } catch (Exception e) { return false; }
        return false;
    }

    private boolean elementoExiste(By locator) {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) { return false; }
    }
}
