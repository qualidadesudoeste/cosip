package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By usernameBy = By.xpath("//input[@placeholder='Login' or @placeholder='Usuário' or contains(@name,'user')]");
    private final By passwordBy = By.xpath("//input[@type='password' or @placeholder='Senha' or contains(@name,'password')]");
    private final By iframeBy   = By.tagName("iframe");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    private boolean tentarFocarNoIframe() {
        try {
            driver.switchTo().defaultContent();
            wait.until(ExpectedConditions.presenceOfElementLocated(iframeBy));
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iframeBy));
            return true;
        } catch (Exception e) { return false; }
    }

    private void preencherComJS(By locator, String texto) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value = '';", element);
        js.executeScript("arguments[0].value = arguments[1];", element, texto);
        js.executeScript("arguments[0].dispatchEvent(new Event('input'));", element);
        js.executeScript("arguments[0].dispatchEvent(new Event('change'));", element);
    }

    public void preencherUsuario(String usuario) {
        tentarFocarNoIframe();
        preencherComJS(usernameBy, usuario);
        try { driver.findElement(usernameBy).sendKeys(Keys.TAB); } catch (Exception e) {}
    }

    public void preencherSenha(String senha) {
        boolean sucesso = false;
        int tentativas  = 0;
        while (!sucesso && tentativas < 4) {
            try {
                tentarFocarNoIframe();
                new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(ExpectedConditions.presenceOfElementLocated(passwordBy));
                preencherComJS(passwordBy, senha);
                driver.findElement(passwordBy).sendKeys(Keys.ENTER);
                sucesso = true;
            } catch (Exception e) {
                tentativas++;
                System.out.println("LOG: Iframe recarregando... Tentativa " + tentativas + "/4");
                try { Thread.sleep(2000); } catch (InterruptedException i) {}
            }
        }
        if (!sucesso) throw new RuntimeException("ERRO: Não foi possível preencher a senha após 4 tentativas.");
    }

    public void realizarLogin(String usuario, String senha) {
        try { Thread.sleep(3000); } catch (InterruptedException e) {}
        preencherUsuario(usuario);
        try { Thread.sleep(3000); } catch (InterruptedException e) {}
        preencherSenha(senha);
        System.out.println("LOG: Login enviado. Voltando ao contexto padrão.");
        driver.switchTo().defaultContent();
    }
}
