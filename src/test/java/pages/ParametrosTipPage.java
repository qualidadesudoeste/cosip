package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * PARÂMETROS DA TIP (BASE DE CÁLCULO)
 * Caminho: Parametrização > Parâmetro da TIP
 *
 * IDs reais extraídos do DevTools do sistema:
 *   - iframe name="mainform"
 *   - input#dataInicio    (Data Início de Vigência)
 *   - input#dataFim       (Data Fim de Vigência)
 *   - input#inputTarifa   (Tarifa B4A)
 *   - button.btn-salvar   (Salvar Parâmetro)
 *   - button.btn-cancelar (Cancelar Operação)
 */
public class ParametrosTipPage extends BasePage {

    // ── NAVEGAÇÃO ────────────────────────────────────────────────────────────
    // Menu lateral: "! Parâmetro da TIP"
    private final By menuParametrizacaoBy = By.xpath("//span[contains(normalize-space(),'Parametrização')]");
    private final By subMenuTipBy         = By.xpath("//span[contains(normalize-space(),'Parâmetro da TIP')]");

    // ── TELA PRINCIPAL ───────────────────────────────────────────────────────
    // Botão azul "+ NOVA VIGÊNCIA" — onclick="ebfFlowExecute('TIP - Abrir nova vigencia')"
    private final By btnNovaVigenciaBy    = By.cssSelector("button.btn-primary");

    // Filtros da grid
    private final By inputFiltroAnoBy     = By.xpath("//input[@placeholder='Ex: 2025']");
    private final By selectFiltroStatusBy = By.xpath("//select[contains(@class,'form-select') or contains(@class,'form-control')]");
    private final By btnFiltrarBy         = By.xpath("//button[normalize-space()='FILTRAR']");

    // ── MODAL: NOVA VIGÊNCIA – MÓDULO TIP ───────────────────────────────────
    // IDs extraídos diretamente do DevTools (imagens 3, 4, 5 e 6)
    private final By inputDataInicioBy    = By.id("dataInicio");
    private final By inputDataFimBy       = By.id("dataFim");
    private final By inputTarifaBy        = By.id("inputTarifa");   // class="tarifa-input-field"

    // Upload do Ato Legal — área de drop + input file oculto
    private final By areaUploadBy         = By.xpath("//div[contains(@class,'bloco-campo') and .//text()[contains(.,'UPLOAD DO ATO LEGAL')]]//input[@type='file'] | //input[@type='file']");

    // Botões do modal
    // onclick="ebfFlowExecute('TIP - CONFIRMAR SALVAR')"
    private final By btnSalvarParametroBy = By.cssSelector("button.btn-salvar");
    // onclick="ebfFlowExecute('Geral - Fechar Formulário Corrente')"
    private final By btnCancelarBy        = By.xpath("//button[normalize-space()='Cancelar Operação']");

    // Título do modal para validação
    private final By tituloModalBy        = By.xpath("//*[contains(normalize-space(),'NOVA VIGÊNCIA')]");

    // Valor do Módulo (somente leitura) — calculado automaticamente
    // A estrutura real é: label "VALOR DO MÓDULO (R$)" + campo display com "R$ X.XXX,XX"
    // Usamos múltiplas estratégias via JS no método obterValorModulo()
    private final By valorModuloBy        = By.xpath(
            "//label[contains(.,'VALOR DO MÓDULO')]/..//input | " +
                    "//label[contains(.,'VALOR DO MÓDULO')]/..//span[contains(.,'R$')] | " +
                    "//*[contains(@class,'valor-modulo')] | " +
                    "//*[@id='valorModulo'] | " +
                    "//*[@id='moduloValor']");

    public ParametrosTipPage(WebDriver driver) {
        super(driver);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  NAVEGAÇÃO
    // ═══════════════════════════════════════════════════════════════════════

    public void acessarModulo() {
        driver.switchTo().defaultContent();

        localizarFocoNoElemento(menuParametrizacaoBy, "Menu Parametrização");
        clicarGarantido(driver.findElement(menuParametrizacaoBy));
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        localizarFocoNoElemento(subMenuTipBy, "Submenu Parâmetro da TIP");
        clicarGarantido(driver.findElement(subMenuTipBy));

        try { Thread.sleep(3500); } catch (InterruptedException e) {}
        driver.switchTo().defaultContent();
        System.out.println("LOG: Módulo Parâmetros da TIP acessado.");
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  TELA PRINCIPAL
    // ═══════════════════════════════════════════════════════════════════════

    public void filtrarPorAno(String ano) {
        localizarFocoNoElemento(inputFiltroAnoBy, "Filtro Ano");
        preencher(inputFiltroAnoBy, ano);
        localizarFocoNoElemento(btnFiltrarBy, "Botão Filtrar");
        clicarGarantido(driver.findElement(btnFiltrarBy));
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        System.out.println("LOG: Filtro por ano aplicado: " + ano);
    }

    public void filtrarPorStatus(String status) {
        localizarFocoNoElemento(selectFiltroStatusBy, "Select Status");
        new Select(driver.findElement(selectFiltroStatusBy)).selectByVisibleText(status);
        localizarFocoNoElemento(btnFiltrarBy, "Botão Filtrar");
        clicarGarantido(driver.findElement(btnFiltrarBy));
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        System.out.println("LOG: Filtro por status aplicado: " + status);
    }

    public void abrirNovaVigencia() {
        localizarFocoNoElemento(btnNovaVigenciaBy, "Botão NOVA VIGÊNCIA");
        clicarGarantido(driver.findElement(btnNovaVigenciaBy));
        try { Thread.sleep(2500); } catch (InterruptedException e) {}
        // O modal abre dentro de um novo iframe — reseta o contexto
        driver.switchTo().defaultContent();
        System.out.println("LOG: Modal Nova Vigência aberto.");
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  MODAL: NOVA VIGÊNCIA – MÓDULO TIP
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Valida que o título do modal é "NOVA VIGÊNCIA – MÓDULO TIP".
     */
    public boolean validarTituloModal() {
        localizarFocoNoElemento(tituloModalBy, "Título do Modal");
        String titulo = driver.findElement(tituloModalBy).getText();
        System.out.println("LOG: Título do modal: " + titulo);
        return titulo.toUpperCase().contains("NOVA VIGÊNCIA");
    }

    /**
     * Retorna o valor do campo Data Início preenchido automaticamente (D+1).
     * O sistema preenche com D+1 da Data Fim da vigência anterior.
     */
    public String obterDataInicioPreenchida() {
        localizarFocoNoElemento(inputDataInicioBy, "Data Início de Vigência");
        String valor = driver.findElement(inputDataInicioBy).getAttribute("value");
        System.out.println("LOG: Data Início preenchida automaticamente: " + valor);
        return valor;
    }

    /**
     * Preenche a Data Início de Vigência.
     * Formato esperado pelo input[type=date]: yyyy-MM-dd
     * Mas o Maker pode aceitar via JS com o formato da HU.
     */
    public void preencherDataInicio(String dataIso) {
        localizarFocoNoElemento(inputDataInicioBy, "Data Início de Vigência");
        WebElement campo = driver.findElement(inputDataInicioBy);
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", campo, dataIso);
        ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('change'));", campo);
        System.out.println("LOG: Data Início preenchida: " + dataIso);
    }

    /**
     * Preenche a Data Fim de Vigência.
     * Formato ISO: yyyy-MM-dd (ex: "2026-12-31")
     */
    public void preencherDataFim(String dataIso) {
        localizarFocoNoElemento(inputDataFimBy, "Data Fim de Vigência");
        WebElement campo = driver.findElement(inputDataFimBy);
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", campo, dataIso);
        ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('change'));", campo);
        System.out.println("LOG: Data Fim preenchida: " + dataIso);
    }

    /**
     * Faz upload do Ato Legal (aceita exclusivamente PDF).
     * O input[type=file] é oculto — usamos JS para expô-lo antes do sendKeys.
     * @param caminhoAbsolutoPdf Caminho completo do arquivo PDF na máquina.
     */
    public void uploadAtoLegal(String caminhoAbsolutoPdf) {
        localizarFocoNoElemento(areaUploadBy, "Input File Ato Legal");
        WebElement inputFile = driver.findElement(areaUploadBy);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].style.display='block';" +
                        "arguments[0].style.visibility='visible';" +
                        "arguments[0].style.opacity='1';", inputFile);
        inputFile.sendKeys(caminhoAbsolutoPdf);
        try { Thread.sleep(1500); } catch (InterruptedException e) {}
        System.out.println("LOG: Upload do Ato Legal concluído: " + caminhoAbsolutoPdf);
    }

    /**
     * Preenche a Tarifa B4A (R$).
     * id="inputTarifa" — campo monetário, não aceita valores negativos.
     * O sistema calcula automaticamente: VALOR DO MÓDULO = TARIFA × 1000
     */
    public void preencherTarifaB4A(String valor) {
        localizarFocoNoElemento(inputTarifaBy, "Tarifa B4A");
        WebElement campo = driver.findElement(inputTarifaBy);
        // Limpa via JS (campo pode ter máscara)
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", campo);
        campo.click();
        campo.sendKeys(valor);
        // Tab para disparar o cálculo automático do Módulo
        campo.sendKeys(Keys.TAB);
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        System.out.println("LOG: Tarifa B4A preenchida: " + valor);
    }

    /**
     * Lê o VALOR DO MÓDULO (R$) calculado automaticamente (somente leitura).
     * Fórmula: TARIFA B4A × 1.000 kWh
     */
    /**
     * Lê o VALOR DO MÓDULO usando JavaScript para varrer todos os elementos
     * visíveis no frame e encontrar aquele que contém "R$" e está próximo
     * da label "VALOR DO MÓDULO".
     *
     * A estratégia JS é mais robusta que XPath para campos calculados do Maker
     * que usam innerHTML dinâmico.
     */
    public String obterValorModulo() {
        // Estratégia 1: JS — varre o frame ativo buscando "R$" próximo ao label
        try {
            String valorJS = (String) ((JavascriptExecutor) driver).executeScript(
                    "var labels = document.querySelectorAll('label, span, div');" +
                            "for(var i=0; i<labels.length; i++){" +
                            "  var t = labels[i].innerText || labels[i].textContent || '';" +
                            "  if(t.includes('VALOR DO MÓDULO') || t.includes('Valor do Módulo')){" +
                            "    var parent = labels[i].parentElement;" +
                            "    if(parent){" +
                            "      var inputs = parent.querySelectorAll('input, span, div');" +
                            "      for(var j=0; j<inputs.length; j++){" +
                            "        var v = (inputs[j].value || inputs[j].innerText || '').trim();" +
                            "        if(v.includes('R$') || (v.length>3 && !isNaN(v.replace(/[.,]/g,'')))) return v;" +
                            "      }" +
                            "    }" +
                            "  }" +
                            "}" +
                            // Fallback: qualquer elemento com "R$" que não seja label de campo de data
                            "var all = document.querySelectorAll('*');" +
                            "for(var k=0; k<all.length; k++){" +
                            "  var txt = (all[k].value || all[k].innerText || '').trim();" +
                            "  if(txt.startsWith('R$') && txt.length > 3 && !txt.includes('VIGÊNCIA') && !txt.includes('DATA')) return txt;" +
                            "}" +
                            "return '';");
            if (valorJS != null && !valorJS.isEmpty()) {
                System.out.println("LOG: Valor do Módulo (JS): " + valorJS);
                return valorJS;
            }
        } catch (Exception e) {
            System.out.println("LOG: Fallback JS falhou: " + e.getMessage());
        }

        // Estratégia 2: XPath direto no frame ativo
        try {
            localizarFocoNoElemento(valorModuloBy, "Valor do Módulo");
            WebElement el = driver.findElement(valorModuloBy);
            String valor = el.getAttribute("value");
            if (valor == null || valor.isEmpty()) valor = el.getText();
            valor = valor.trim();
            System.out.println("LOG: Valor do Módulo (XPath): " + valor);
            return valor;
        } catch (Exception e) {
            System.out.println("LOG: Não foi possível ler o Valor do Módulo: " + e.getMessage());
            return "";
        }
    }

    /**
     * Clica em "SALVAR PARÂMETRO" e captura o SweetAlert de sucesso
     * ANTES de sair do iframe (o alerta aparece dentro do frame da Nova Vigência).
     *
     * Após capturar e fechar o SweetAlert, switcha para defaultContent.
     * O resultado fica armazenado em ultimoSweetAlertScreenshot e ultimoSweetAlertMsg
     * para ser consumido por validarSalvamento().
     */
    private String ultimoSweetAlertMsg = "";

    public void salvarParametro() {
        localizarFocoNoElemento(btnSalvarParametroBy, "Botão Salvar Parâmetro");
        WebElement btn = driver.findElement(btnSalvarParametroBy);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(true);" +
                        "var ev = new MouseEvent('click',{bubbles:true,cancelable:true});" +
                        "arguments[0].dispatchEvent(ev);", btn);
        System.out.println("LOG: Salvar Parâmetro clicado. Aguardando SweetAlert...");

        // ── Aguarda e captura o SweetAlert AINDA DENTRO DO IFRAME ────────────
        // O alerta aparece no body do frame da Nova Vigência, não no defaultContent
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver,
                    java.time.Duration.ofSeconds(8))
                    .until(org.openqa.selenium.support.ui.ExpectedConditions
                            .presenceOfElementLocated(swal2SucessoBy));

            // Screenshot COM modal aberto (dentro do iframe)
            ultimoSweetAlertScreenshot = tirarScreenshotBase64();

            // Lê a mensagem
            try {
                ultimoSweetAlertMsg = driver.findElement(swal2MsgBy).getText().trim();
            } catch (Exception e) { ultimoSweetAlertMsg = "sucesso"; }

            System.out.println("LOG: SweetAlert capturado no iframe: " + ultimoSweetAlertMsg);

            // Fecha clicando em OK
            try {
                WebElement ok = driver.findElement(swal2OkBy);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", ok);
                try { Thread.sleep(800); } catch (InterruptedException ie) {}
                System.out.println("LOG: SweetAlert fechado via OK.");
            } catch (Exception e) {
                try { driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE); }
                catch (Exception ignored) {}
            }

        } catch (Exception e) {
            System.out.println("LOG: SweetAlert não encontrado no iframe: " + e.getMessage());
            ultimoSweetAlertMsg = "";
            try { Thread.sleep(2000); } catch (InterruptedException ie) {}
        }

        driver.switchTo().defaultContent();
        System.out.println("LOG: Salvar Parâmetro concluído.");
    }

    /**
     * Clica em "CANCELAR OPERAÇÃO"
     * onclick="ebfFlowExecute('Geral - Fechar Formulário Corrente')"
     */
    public void cancelarOperacao() {
        localizarFocoNoElemento(btnCancelarBy, "Botão Cancelar Operação");
        clicarGarantido(driver.findElement(btnCancelarBy));
        driver.switchTo().defaultContent();
        System.out.println("LOG: Operação cancelada.");
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  VALIDAÇÕES
    // ═══════════════════════════════════════════════════════════════════════

    // SweetAlert de sucesso do Maker
    // Texto: "Registro inserido e atualizado com sucesso!"
    // Ícone: div.swal2-icon.swal2-success (check verde)
    private final By swal2SucessoBy  = By.cssSelector("div.swal2-popup.swal2-show");
    private final By swal2MsgBy      = By.cssSelector(
            "#swal2-html-container, .swal2-html-container, " +
                    "h2.swal2-title, div.swal2-popup p");
    private final By swal2OkBy       = By.cssSelector(
            "div.swal2-popup .swal2-confirm, " +
                    "div.swal2-popup button.btn-primary, " +
                    "div.swal2-popup button");

    // Screenshot tirada COM o SweetAlert visível (antes de fechar)
    private String ultimoSweetAlertScreenshot = "";

    public String getUltimoSweetAlertScreenshot() {
        return ultimoSweetAlertScreenshot;
    }

    private String tirarScreenshotBase64() {
        try {
            return ((org.openqa.selenium.TakesScreenshot) driver)
                    .getScreenshotAs(org.openqa.selenium.OutputType.BASE64);
        } catch (Exception e) { return ""; }
    }

    /**
     * Verifica e captura o SweetAlert de sucesso.
     * Tira screenshot ANTES de fechar (para o ExtentReport mostrar o modal visível).
     * Fecha o modal clicando em OK.
     * @return texto da mensagem capturada, ou "" se nenhum alerta estiver visível.
     */
    public String capturarEFecharSweetAlertSucesso() {
        driver.switchTo().defaultContent();

        // Tenta na raiz e nos frames
        boolean encontrado = false;
        try {
            if (!driver.findElements(swal2SucessoBy).isEmpty() &&
                    driver.findElement(swal2SucessoBy).isDisplayed()) encontrado = true;
        } catch (Exception ignored) {}
        if (!encontrado) {
            try {
                localizarFocoNoElemento(swal2SucessoBy, "SweetAlert Sucesso");
                encontrado = true;
            } catch (Exception e) { return ""; }
        }

        // Lê a mensagem
        String mensagem = "";
        try { mensagem = driver.findElement(swal2MsgBy).getText().trim(); }
        catch (Exception ignored) {}

        System.out.println("LOG: SweetAlert de sucesso → " + mensagem);

        // ── Screenshot COM o modal aberto ────────────────────────────────────
        ultimoSweetAlertScreenshot = tirarScreenshotBase64();

        // ── Fecha clicando em OK ─────────────────────────────────────────────
        try {
            WebElement btnOk = driver.findElement(swal2OkBy);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnOk);
            try { Thread.sleep(800); } catch (InterruptedException ie) {}
            System.out.println("LOG: SweetAlert fechado via OK.");
        } catch (Exception e) {
            try { driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE); }
            catch (Exception ignored) {}
        }

        driver.switchTo().defaultContent();
        return mensagem;
    }

    /**
     * Valida o salvamento verificando o SweetAlert "Registro inserido e atualizado com sucesso!".
     * Fallback: verifica o botão Editar (comportamento anterior).
     */
    public boolean validarSalvamento() {
        // Usa o SweetAlert já capturado por salvarParametro() (antes do defaultContent)
        if (ultimoSweetAlertMsg != null && !ultimoSweetAlertMsg.isEmpty()) {
            boolean sucesso = ultimoSweetAlertMsg.toLowerCase().contains("sucesso") ||
                    ultimoSweetAlertMsg.toLowerCase().contains("inserido") ||
                    ultimoSweetAlertMsg.toLowerCase().contains("atualizado");
            System.out.println("LOG: Salvamento via SweetAlert capturado: "
                    + sucesso + " | msg: " + ultimoSweetAlertMsg);
            return sucesso;
        }
        // Fallback: botão Editar (caso salvarParametro não tenha capturado o alerta)
        System.out.println("LOG: SweetAlert não capturado, usando fallback botão Editar.");
        return validarSalvamentoPorBotaoEditar();
    }

    public String capturarAlertaSistema() {
        driver.switchTo().defaultContent();
        return buscarErroRecursivamente(driver);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  DATA FIM — 2 MESES APÓS O DATA INÍCIO (automático)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Lê a Data Início preenchida automaticamente pelo sistema,
     * calcula D+2 meses e preenche a Data Fim de Vigência.
     *
     * O Data Início vem no formato "yyyy-MM-dd" (input[type=date]).
     * Retorna a data fim calculada no formato ISO para confirmar no log.
     */
    public String preencherDataFimAutomatica() {
        try {
            // Lê o valor do input[type=date] — sempre yyyy-MM-dd
            localizarFocoNoElemento(inputDataInicioBy, "Data Início (leitura para cálculo)");
            String dataInicioISO = driver.findElement(inputDataInicioBy).getAttribute("value");
            System.out.println("LOG: Data Início lida: " + dataInicioISO);

            if (dataInicioISO == null || dataInicioISO.isEmpty()) {
                System.out.println("LOG: Data Início vazia, usando data padrão.");
                dataInicioISO = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            }

            // Calcula 2 meses depois — último dia do mês resultante
            LocalDate inicio  = LocalDate.parse(dataInicioISO);
            LocalDate fimData = inicio.plusMonths(2).withDayOfMonth(
                    inicio.plusMonths(2).lengthOfMonth());
            String dataFimISO = fimData.format(DateTimeFormatter.ISO_LOCAL_DATE);

            System.out.println("LOG: Data Fim calculada (+2 meses): " + dataFimISO);

            // Preenche o campo
            localizarFocoNoElemento(inputDataFimBy, "Data Fim de Vigência");
            WebElement campo = driver.findElement(inputDataFimBy);
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value = arguments[1];", campo, dataFimISO);
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));", campo);
            System.out.println("LOG: Data Fim preenchida automaticamente: " + dataFimISO);

            return dataFimISO;

        } catch (Exception e) {
            System.out.println("LOG: Erro ao calcular Data Fim: " + e.getMessage());
            return "";
        }
    }
}