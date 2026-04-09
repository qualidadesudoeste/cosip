package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * REGISTRO DE EVENTO/PROBLEMA
 * Caminho: Menu lateral > Registro de Evento/Problema
 *
 * IDs reais extraídos do DevTools:
 *   Tela de listagem:
 *     button#addRecordButton       → "Incluir Registro"
 *     div#searchInputContainer     → campo Buscar
 *     div#page-container           → container principal da listagem
 *
 *   Formulário de inclusão (abre após clicar em Incluir Registro):
 *     Campos via placeholder/label (mesmos da screenshot anterior)
 *     Toolbar padrão Maker: a.webrun-form-nav-save / a.webrun-form-nav-edit
 */
public class EventosPage extends BasePage {

    // ── NAVEGAÇÃO ────────────────────────────────────────────────────────────
    private final By menuEventosBy = By.xpath(
            "//span[contains(normalize-space(),'Registro de Evento')] | " +
                    "//a[contains(normalize-space(),'Registro de Evento')]");

    // ── TELA DE LISTAGEM ─────────────────────────────────────────────────────
    // ID real extraído do DevTools: button#addRecordButton
    // class="btn btn-light border addRecordButton py-2 px-3 align-self-end mb-4 mt-2"
    // title="Incluir Registro"
    private final By btnIncluirRegistroBy = By.id("addRecordButton");

    // Campo de busca da listagem
    private final By inputBuscarBy        = By.id("searchInputContainer");

    // Container principal da listagem (confirma que a tela carregou)
    private final By pageContainerBy      = By.id("page-container");

    // ── TOOLBAR DO FORMULÁRIO (padrão Maker — igual ao SGOS) ─────────────────
    private final By btnSalvarBy  = By.cssSelector("a.webrun-form-nav-save");
    private final By btnEditarBy  = By.cssSelector("a.webrun-form-nav-edit");
    private final By btnCancelarBy= By.cssSelector("a.webrun-form-nav-cancel, " +
            "a.webrun-form-nav-discard");

    // ── CAMPOS DO FORMULÁRIO DE INCLUSÃO ─────────────────────────────────────
    // Data e Hora — placeholder="DD/MM/YYYY HH:MM:SS"
    private final By inputDataHoraBy = By.xpath(
            "//input[@placeholder='DD/MM/YYYY HH:MM:SS'] | " +
                    "//label[contains(.,'Data e Hora')]/..//input");

    // Competência — placeholder="Ex: 202501"
    private final By inputCompetenciaBy = By.xpath(
            "//input[@placeholder='Ex: 202501'] | " +
                    "//label[contains(.,'Competência')]/..//input");

    // Contrato e Arquivo — combos Maker
    private final By inputContratoBy = By.xpath(
            "//label[contains(.,'Contrato')]/..//input | " +
                    "//label[contains(.,'Contrato')]/..//select");

    private final By inputArquivoBy = By.xpath(
            "//label[contains(.,'Arquivo')]/..//input | " +
                    "//label[contains(.,'Arquivo')]/..//select");

    // Problema Apresentado — textarea, máx 200 chars
    // id real: WFRInput1135868 — campo INPUT (não textarea), maxlength="200"
    private final By textareaProblemaBy = By.id("WFRInput1135868");

    // Linha Arquivo Texto — textarea grande
    // id real: WFRInput1135869 — textarea, maxlength="4000"
    private final By textareaLinhaArquivoBy = By.id("WFRInput1135869");

    public EventosPage(WebDriver driver) {
        super(driver);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  NAVEGAÇÃO
    // ═══════════════════════════════════════════════════════════════════════

    public void acessarModulo() {
        driver.switchTo().defaultContent();
        localizarFocoNoElemento(menuEventosBy, "Menu Registro de Evento/Problema");
        clicarGarantido(driver.findElement(menuEventosBy));
        try { Thread.sleep(3500); } catch (InterruptedException e) {}
        driver.switchTo().defaultContent();
        System.out.println("LOG: Módulo Registro de Evento/Problema acessado.");
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  TELA DE LISTAGEM
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Confirma que o formulário voltou ao estado inicial após cancelar.
     * O Maker não fecha a aba — ele limpa o form e reexibe o botão "Incluir Registro".
     * Por isso checamos button#addRecordButton (indicador real do estado de listagem).
     */
    public boolean validarTelaListagemCarregada() {
        try {
            localizarFocoNoElemento(btnIncluirRegistroBy, "Botão Incluir Registro");
            boolean ok = driver.findElement(btnIncluirRegistroBy).isDisplayed();
            System.out.println("LOG: Botão Incluir Registro visível (estado de listagem): " + ok);
            return ok;
        } catch (Exception e) {
            System.out.println("LOG: Botão Incluir Registro não encontrado: " + e.getMessage());
            return false;
        }
    }

    /** Confirma que o botão 'Incluir Registro' está visível e clicável. */
    public boolean validarBotaoIncluirVisivel() {
        localizarFocoNoElemento(btnIncluirRegistroBy, "Botão Incluir Registro");
        boolean visivel = driver.findElement(btnIncluirRegistroBy).isDisplayed();
        System.out.println("LOG: Botão 'Incluir Registro' visível: " + visivel);
        return visivel;
    }

    /**
     * Clica em "Incluir Registro" (button#addRecordButton) e aguarda o
     * formulário de inclusão abrir dentro do iframe do Maker.
     */
    public void incluirRegistro() {
        // 1. Garante que estamos no frame certo (Boneca Russa até achar o botão)
        localizarFocoNoElemento(btnIncluirRegistroBy, "Botão Incluir Registro");

        // 2. Clica via JS para evitar interceptação de overlays
        WebElement btn = driver.findElement(btnIncluirRegistroBy);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(true);" +
                        "arguments[0].click();", btn);

        System.out.println("LOG: Clique em 'Incluir Registro' enviado.");

        // 3. Aguarda o formulário de inclusão carregar (novo iframe do Maker)
        try { Thread.sleep(3000); } catch (InterruptedException e) {}
        driver.switchTo().defaultContent();
        System.out.println("LOG: Formulário de inclusão aberto.");
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  FORMULÁRIO DE INCLUSÃO
    // ═══════════════════════════════════════════════════════════════════════

    public void preencherDataHora(String dataHora) {
        localizarFocoNoElemento(inputDataHoraBy, "Data e Hora");
        WebElement campo = driver.findElement(inputDataHoraBy);
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", campo);
        campo.click();
        campo.sendKeys(dataHora);
        campo.sendKeys(Keys.TAB);
        System.out.println("LOG: Data e Hora preenchida: " + dataHora);
    }

    public void preencherCompetencia(String competencia) {
        localizarFocoNoElemento(inputCompetenciaBy, "Competência");
        preencher(inputCompetenciaBy, competencia);
        System.out.println("LOG: Competência preenchida: " + competencia);
    }

    public void selecionarContrato(String contrato) {
        if (contrato == null || contrato.isEmpty()) return;
        selecionarComboMaker(inputContratoBy, contrato);
        System.out.println("LOG: Contrato selecionado: " + contrato);
    }

    public void selecionarArquivo(String arquivo) {
        if (arquivo == null || arquivo.isEmpty()) return;
        selecionarComboMaker(inputArquivoBy, arquivo);
        System.out.println("LOG: Arquivo selecionado: " + arquivo);
    }

    public void preencherProblema(String problema) {
        // Campo é input[type=text] id="WFRInput1135868" maxlength="200" — não textarea
        localizarFocoNoElemento(textareaProblemaBy, "Problema Apresentado");
        WebElement campo = driver.findElement(textareaProblemaBy);
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", campo);
        campo.click();
        campo.sendKeys(problema);
        campo.sendKeys(Keys.TAB);
        System.out.println("LOG: Problema preenchido (" + problema.length() + " chars).");
    }

    public void preencherLinhaArquivo(String linha) {
        localizarFocoNoElemento(textareaLinhaArquivoBy, "Linha Arquivo Texto");
        WebElement campo = driver.findElement(textareaLinhaArquivoBy);
        campo.click();
        campo.clear();
        campo.sendKeys(linha);
        System.out.println("LOG: Linha Arquivo Texto preenchida.");
    }

    /** Verifica que o formulário de inclusão está aberto (campo Data e Hora visível). */
    public boolean validarFormularioAberto() {
        try {
            localizarFocoNoElemento(inputDataHoraBy, "Campo Data e Hora");
            return driver.findElement(inputDataHoraBy).isDisplayed();
        } catch (Exception e) {
            System.out.println("LOG: Formulário não encontrado: " + e.getMessage());
            return false;
        }
    }

    /** Retorna o comprimento atual do campo Problema Apresentado. */
    public int obterTamanhoProblema() {
        try {
            localizarFocoNoElemento(textareaProblemaBy, "Problema Apresentado");
            String valor = driver.findElement(textareaProblemaBy).getAttribute("value");
            return valor == null ? 0 : valor.length();
        } catch (Exception e) { return 0; }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  SALVAR / CANCELAR
    // ═══════════════════════════════════════════════════════════════════════

    public void salvar() {
        System.out.println("LOG: Iniciando salvamento...");
        driver.switchTo().defaultContent();

        try {
            localizarFocoNoElemento(btnSalvarBy, "Botão Salvar");
            WebElement btn = driver.findElement(btnSalvarBy);
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView(true);" +
                            "var ev = new MouseEvent('click',{bubbles:true,cancelable:true});" +
                            "arguments[0].dispatchEvent(ev);", btn);
            System.out.println("LOG: Clique no botão Salvar enviado.");
        } catch (Exception e) {
            System.out.println("LOG: Botão Salvar não encontrado, usando CTRL+S...");
        }

        try {
            ((JavascriptExecutor) driver).executeScript("document.body.focus();");
            new org.openqa.selenium.interactions.Actions(driver)
                    .keyDown(Keys.CONTROL).sendKeys("s").keyUp(Keys.CONTROL).perform();
        } catch (Exception e) {
            System.out.println("LOG: Falha ao enviar CTRL+S.");
        }
    }

    public void cancelar() {
        driver.switchTo().defaultContent();
        // Tenta o botão X da toolbar (webrun-form-nav-cancel / discard)
        try {
            localizarFocoNoElemento(btnCancelarBy, "Botão Cancelar toolbar");
            clicarGarantido(driver.findElement(btnCancelarBy));
            System.out.println("LOG: Cancelar via toolbar.");
        } catch (Exception e1) {
            // Fallback 1: ícone fa-times dentro de qualquer botão da toolbar
            try {
                By xToolbarBy = By.cssSelector(
                        "a[title='Cancelar'], a[title='Descartar'], " +
                                "button[title='Cancelar'], button[title='Descartar']");
                localizarFocoNoElemento(xToolbarBy, "Botão Cancelar (title)");
                clicarGarantido(driver.findElement(xToolbarBy));
                System.out.println("LOG: Cancelar via title.");
            } catch (Exception e2) {
                // Fallback 2: ESC
                driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
                System.out.println("LOG: Cancelar via ESC.");
            }
        }
        try { Thread.sleep(1500); } catch (InterruptedException e) {}
        driver.switchTo().defaultContent();
        System.out.println("LOG: Operação cancelada.");
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  VALIDAÇÕES
    // ═══════════════════════════════════════════════════════════════════════

    public boolean validarSalvamento() {
        return validarSalvamentoPorBotaoEditar();
    }

    public String capturarAlertaSistema() {
        driver.switchTo().defaultContent();
        return buscarErroRecursivamente(driver);
    }
}