package pages;

import io.qameta.allure.Attachment;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // --- UTILS PARA ALLURE ---
    public static class AllureHelper {
        @Attachment(value = "{nome}", type = "image/png")
        public static byte[] screenshot(WebDriver driver, String nome) {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        }
    }

    // --- NAVEGAÇÃO E CLIQUES ---
    public void acessarMenu(String menuPrincipal, String subMenu) {
        driver.switchTo().defaultContent();
        By menuBy = By.xpath("//span[contains(normalize-space(), '" + menuPrincipal + "')]");

        try {
            WebElement elMenu = wait.until(ExpectedConditions.elementToBeClickable(menuBy));
            clicarGarantido(elMenu);
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(menuBy));
        }

        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        By subMenuBy = By.xpath("//span[contains(normalize-space(), '" + subMenu + "')]");
        WebElement elSub = wait.until(ExpectedConditions.elementToBeClickable(subMenuBy));
        clicarGarantido(elSub);

        try { Thread.sleep(3500); } catch (InterruptedException e) {}
        driver.switchTo().defaultContent();
    }

    protected void clicarGarantido(WebElement elemento) {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("form-overlay")));
            elemento.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", elemento);
        }
    }

    protected void preencher(By locator, String texto) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", element);
        element.clear();
        element.sendKeys(texto);
    }

    protected void enviarBackupCtrlS() {
        try {
            Thread.sleep(1000);
            driver.findElement(By.tagName("body")).sendKeys(Keys.chord(Keys.CONTROL, "s"));
            System.out.println("LOG: Comando CTRL+S enviado.");
        } catch (Exception e) {}
    }

    public String obterMensagemDeAlerta() {
        for (int i = 0; i < 3; i++) {
            driver.switchTo().defaultContent();
            String msg = buscarErroRecursivamente(driver);
            if (msg != null && !msg.contains("Nenhum erro")) return msg;
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
        }
        return "Nenhuma mensagem capturada.";
    }

    protected void selecionarComboMaker(By locator, String valor) {
        localizarFocoNoElemento(locator, "ComboBox " + valor);
        WebElement comboContainer = driver.findElement(locator);

        try {
            WebElement btnAbrir = comboContainer.findElement(By.xpath("./..//button"));
            clicarGarantido(btnAbrir);
            System.out.println("LOG: Abrindo lista para: " + valor);
            Thread.sleep(1500);

            By itemBy = By.xpath("//li[contains(@class, 'list-group-item') and contains(., '" + valor + "')]");
            WebElement item = driver.findElement(itemBy);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", item);
            System.out.println("LOG: Item '" + valor + "' selecionado.");

        } catch (Exception e) {
            System.out.println("LOG: Fallback: Tentando digitar direto no input.");
            try {
                WebElement input = comboContainer.findElement(By.tagName("input"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", input);
                input.sendKeys(valor);
                Thread.sleep(800);
                input.sendKeys(Keys.TAB);
            } catch (Exception ex) {
                System.out.println("ERRO: Não foi possível interagir com o combo " + valor);
            }
        }
    }

    public void salvar() {
        System.out.println("LOG: Iniciando procedimento de salvamento...");
        driver.switchTo().defaultContent();

        By btnSalvarBy = By.cssSelector("a.webrun-form-nav-save");
        try {
            localizarFocoNoElemento(btnSalvarBy, "Botão Salvar");
            WebElement btn = driver.findElement(btnSalvarBy);
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView(true); " +
                    "var ev = new MouseEvent('click', {bubbles:true, cancelable:true}); " +
                    "arguments[0].dispatchEvent(ev);", btn);
            System.out.println("LOG: Clique no botão Salvar enviado.");
        } catch (Exception e) {
            System.out.println("LOG: Botão visual não encontrado, recorrendo ao atalho de teclado...");
        }

        try {
            ((JavascriptExecutor) driver).executeScript("document.body.focus();");
            new org.openqa.selenium.interactions.Actions(driver)
                    .keyDown(Keys.CONTROL).sendKeys("s").keyUp(Keys.CONTROL).perform();
            System.out.println("LOG: Atalho CTRL + S enviado.");
        } catch (Exception e) {
            System.out.println("LOG: Falha ao enviar comando de teclado.");
        }
    }

    public boolean validarSalvamentoPorBotaoEditar() {
        try {
            driver.switchTo().defaultContent();
            String erro = buscarErroRecursivamente(driver);
            if (erro != null && !erro.contains("Nenhum erro")) {
                System.out.println("LOG: Salvamento falhou. Erro: " + erro);
                return false;
            }

            By btnEditarBy = By.cssSelector("a.webrun-form-nav-edit");
            localizarFocoNoElemento(btnEditarBy, "Botão Editar");
            WebElement btnEditar = driver.findElement(btnEditarBy);
            boolean estaAtivo = !btnEditar.getAttribute("class").contains("disabled");
            System.out.println("LOG: Botão Editar: " + (estaAtivo ? "Ativo (Salvo)" : "Inativo"));
            return estaAtivo;
        } catch (Exception e) {
            return false;
        }
    }

    // --- BUSCA RECURSIVA DE ERROS (Boneca Russa) ---
    protected String buscarErroRecursivamente(WebDriver driver) {
        String[] seletores = {".swal2-html-container", ".modal-body", ".alert-danger"};
        for (String seletor : seletores) {
            try {
                List<WebElement> alerts = driver.findElements(By.cssSelector(seletor));
                if (!alerts.isEmpty() && alerts.get(0).isDisplayed()) {
                    String texto = alerts.get(0).getText();
                    try { driver.findElement(By.cssSelector(".swal2-confirm, .btn-close")).click(); } catch (Exception e) {}
                    return texto;
                }
            } catch (Exception e) {}
        }

        List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
        for (WebElement iframe : iframes) {
            try {
                driver.switchTo().frame(iframe);
                String erro = buscarErroRecursivamente(driver);
                if (erro != null) return erro;
                driver.switchTo().parentFrame();
            } catch (Exception e) {
                try { driver.switchTo().parentFrame(); } catch (Exception ex) {}
            }
        }
        return null;
    }

    // --- LOCALIZAÇÃO DE FOCO (Boneca Russa) ---
    protected void localizarFocoNoElemento(By locator, String nome) {
        long endTime = System.currentTimeMillis() + 20000;
        while (System.currentTimeMillis() < endTime) {
            driver.switchTo().defaultContent();
            try {
                wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.name("mainsystem")));
            } catch (Exception e) {
                try { wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("mainsystem"))); } catch (Exception e2) {}
            }
            if (!driver.findElements(locator).isEmpty()) return;
            if (percorrerFrames(locator)) return;
            try { Thread.sleep(1000); } catch (Exception e) {}
        }
        throw new RuntimeException("TIMEOUT: " + nome + " não encontrado.");
    }

    protected boolean percorrerFrames(By locator) {
        if (!driver.findElements(locator).isEmpty()) return true;
        List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
        for (int i = 0; i < iframes.size(); i++) {
            try {
                driver.switchTo().frame(i);
                if (percorrerFrames(locator)) return true;
                driver.switchTo().parentFrame();
            } catch (Exception e) {
                try { driver.switchTo().parentFrame(); } catch (Exception ex) {}
            }
        }
        return false;
    }
}
