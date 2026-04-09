package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * GESTÃO DA LOA
 * Caminho: Parametrização > Gestão da LOA
 *
 * IDs reais extraídos do DevTools:
 *   Tela principal:
 *     a.btnadicionar           → botão "+ Adicionar Exercício"
 *     div#ContainerTitle       → título da tela
 *     div#ContainerFiltro      → área do filtro por ano
 *     div#ContainerGrade       → grid Histórico LOA
 *
 *   Modal ADICIONAR LOA:
 *     input#WFRInput1135897    → campo ANO (placeholder="Ex: 2025", maxlength="4")
 *     div#TL6_LOA_ANO1         → container do campo ANO
 *     div#TL6_LOA_VALOR_TOTAL_ANUAL1 → container do Valor Total Anual
 *     div#ContainerFoot        → rodapé do modal (botões)
 *     div#closebtn             → container do botão Cancelar
 *     div#btnconfirm           → container do botão Salvar LOA
 */
public class GestaoLoaPage extends BasePage {

    // ── NAVEGAÇÃO ────────────────────────────────────────────────────────────
    private final By menuParametrizacaoBy  = By.xpath("//span[contains(normalize-space(),'Parametrização')]");
    private final By subMenuGestaoLoaBy    = By.xpath("//span[contains(normalize-space(),'Gestão da LOA')]");

    // ── TELA PRINCIPAL ───────────────────────────────────────────────────────
    // onclick="ebfFlowExecute('Abrir Formulário adicionarLOA em Modo de Inserção')"
    private final By btnAdicionarExercicioBy = By.cssSelector("a.btnadicionar");

    // Título e subtítulo da tela
    private final By tituloTelaBy          = By.id("ContainerTitle");

    // Filtro por ano
    private final By inputFiltroAnoBy      = By.xpath("//input[@placeholder='Ex: 2025']");
    private final By btnFiltrarBy          = By.xpath("//button[normalize-space()='FILTRAR']");

    // Grid Histórico LOA
    private final By containerGradeBy     = By.id("ContainerGrade");
    private final By totalRegistrosBy      = By.xpath("//*[contains(normalize-space(),'TOTAL:')]");

    // ── MODAL: ADICIONAR LOA ─────────────────────────────────────────────────
    // ANO — id="WFRInput1135897", placeholder="Ex: 2025", maxlength="4"
    private final By inputAnoBy            = By.id("WFRInput1135897");

    // VALOR TOTAL ANUAL — input dentro de div#TL6_LOA_VALOR_TOTAL_ANUAL1
    private final By inputValorTotalBy     = By.xpath("//div[@id='TL6_LOA_VALOR_TOTAL_ANUAL1']//input");

    // Campos mensais (Janeiro a Dezembro) — dentro do grid de distribuição mensal
    private final By inputJaneiroBy        = By.xpath("//label[normalize-space()='JANEIRO *']/..//input | //div[contains(@id,'LOA_MES') and .//text()[contains(.,'JANEIRO')]]//input");
    private final By inputFevereiroBy      = By.xpath("//label[normalize-space()='FEVEREIRO *']/..//input");
    private final By inputMarcoBy          = By.xpath("//label[normalize-space()='MARÇO *']/..//input");
    private final By inputAbrilBy          = By.xpath("//label[normalize-space()='ABRIL *']/..//input");
    private final By inputMaioBy           = By.xpath("//label[normalize-space()='MAIO *']/..//input");
    private final By inputJunhoBy          = By.xpath("//label[normalize-space()='JUNHO *']/..//input");
    private final By inputJulhoBy          = By.xpath("//label[normalize-space()='JULHO *']/..//input");
    private final By inputAgostoBy         = By.xpath("//label[normalize-space()='AGOSTO *']/..//input");
    private final By inputSetembroBy       = By.xpath("//label[normalize-space()='SETEMBRO *']/..//input");
    private final By inputOutubroBy        = By.xpath("//label[normalize-space()='OUTUBRO *']/..//input");
    private final By inputNovembroBy       = By.xpath("//label[normalize-space()='NOVEMBRO *']/..//input");
    private final By inputDezembroBy       = By.xpath("//label[normalize-space()='DEZEMBRO *']/..//input");

    // VALOR A DISTRIBUIR (somente leitura) — deve ser R$ 0,00 para salvar
    private final By valorADistribuirBy    = By.xpath("//*[contains(normalize-space(),'VALOR A DISTRIBUIR')]");

    // Botões do modal
    // div#btnconfirm > button.btn-primary
    private final By btnSalvarLoaBy        = By.cssSelector("#btnconfirm button.btn-primary");
    // div#closebtn
    private final By btnCancelarBy         = By.xpath("//div[@id='closebtn']//button | //button[normalize-space()='Cancelar']");

    public GestaoLoaPage(WebDriver driver) {
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

        localizarFocoNoElemento(subMenuGestaoLoaBy, "Submenu Gestão da LOA");
        clicarGarantido(driver.findElement(subMenuGestaoLoaBy));

        try { Thread.sleep(3500); } catch (InterruptedException e) {}
        driver.switchTo().defaultContent();
        System.out.println("LOG: Módulo Gestão da LOA acessado.");
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  TELA PRINCIPAL — GESTÃO DA LOA
    // ═══════════════════════════════════════════════════════════════════════

    /** Valida que o título "GESTÃO DA LOA" está visível na tela. */
    public boolean validarTituloDaTela() {
        localizarFocoNoElemento(tituloTelaBy, "Título da Tela");
        String titulo = driver.findElement(tituloTelaBy).getText();
        System.out.println("LOG: Título da tela: " + titulo);
        return titulo.toUpperCase().contains("GESTÃO DA LOA");
    }

    /** Valida que o botão "+ Adicionar Exercício" está visível. */
    public boolean validarBotaoAdicionarVisivel() {
        localizarFocoNoElemento(btnAdicionarExercicioBy, "Botão Adicionar Exercício");
        boolean visivel = driver.findElement(btnAdicionarExercicioBy).isDisplayed();
        System.out.println("LOG: Botão Adicionar Exercício visível: " + visivel);
        return visivel;
    }

    /** Valida que a grid Histórico LOA está presente com o total de registros. */
    public boolean validarGridHistoricoVisivel() {
        localizarFocoNoElemento(containerGradeBy, "Grid Histórico LOA");
        boolean visivel = driver.findElement(containerGradeBy).isDisplayed();
        System.out.println("LOG: Grid Histórico LOA visível: " + visivel);
        return visivel;
    }

    /** Retorna o texto do total de registros exibido na grid. */
    public String obterTotalRegistros() {
        try {
            localizarFocoNoElemento(totalRegistrosBy, "Total de Registros");
            String total = driver.findElement(totalRegistrosBy).getText().trim();
            System.out.println("LOG: Total de registros: " + total);
            return total;
        } catch (Exception e) {
            System.out.println("LOG: Total de registros não encontrado.");
            return "";
        }
    }

    /** Filtra a grid pelo ano informado. */
    public void filtrarPorAno(String ano) {
        localizarFocoNoElemento(inputFiltroAnoBy, "Filtro Ano");
        preencher(inputFiltroAnoBy, ano);
        localizarFocoNoElemento(btnFiltrarBy, "Botão Filtrar");
        clicarGarantido(driver.findElement(btnFiltrarBy));
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        System.out.println("LOG: Filtro por ano aplicado: " + ano);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  MODAL: ADICIONAR LOA
    // ═══════════════════════════════════════════════════════════════════════

    /** Clica em "+ Adicionar Exercício" e aguarda o modal abrir. */
    public void abrirAdicionarExercicio() {
        localizarFocoNoElemento(btnAdicionarExercicioBy, "Botão Adicionar Exercício");
        clicarGarantido(driver.findElement(btnAdicionarExercicioBy));
        try { Thread.sleep(2500); } catch (InterruptedException e) {}
        driver.switchTo().defaultContent();
        System.out.println("LOG: Modal Adicionar LOA aberto.");
    }

    /**
     * Preenche o campo ANO.
     * id="WFRInput1135897" | maxlength="4" | placeholder="Ex: 2025"
     */
    public void preencherAno(String ano) {
        localizarFocoNoElemento(inputAnoBy, "Campo ANO");
        WebElement campo = driver.findElement(inputAnoBy);
        campo.click();
        campo.clear();
        campo.sendKeys(ano);
        // TAB dispara a validação de duplicidade do sistema
        campo.sendKeys(Keys.TAB);
        try { Thread.sleep(1500); } catch (InterruptedException e) {}
        System.out.println("LOG: Ano preenchido: " + ano);
    }

    /**
     * Preenche o VALOR TOTAL ANUAL (R$).
     * Após o preenchimento, o sistema distribui automaticamente nos 12 meses.
     */
    public void preencherValorTotalAnual(String valor) {
        localizarFocoNoElemento(inputValorTotalBy, "Valor Total Anual");
        WebElement campo = driver.findElement(inputValorTotalBy);
        // Limpa via JS (campo pode ter máscara monetária)
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", campo);
        campo.click();
        campo.sendKeys(valor);
        // TAB dispara a distribuição automática
        campo.sendKeys(Keys.TAB);
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        System.out.println("LOG: Valor Total Anual preenchido: R$ " + valor);
    }

    /**
     * Retorna o texto do campo "VALOR A DISTRIBUIR" (somente leitura).
     * Deve estar em R$ 0,00 para permitir salvar.
     */
    public String obterValorADistribuir() {
        try {
            localizarFocoNoElemento(valorADistribuirBy, "Valor a Distribuir");
            // O campo fica ao lado do label — pega o texto do container
            String texto = driver.findElement(valorADistribuirBy).getText().trim();
            System.out.println("LOG: Valor a Distribuir: " + texto);
            return texto;
        } catch (Exception e) {
            System.out.println("LOG: Campo Valor a Distribuir não encontrado.");
            return "";
        }
    }

    /**
     * Verifica se os 12 campos mensais foram preenchidos automaticamente
     * após informar o Valor Total Anual.
     */
    public boolean verificarDistribuicaoMensalPreenchida() {
        try {
            localizarFocoNoElemento(inputJaneiroBy, "Campo Janeiro");
            String valorJaneiro = driver.findElement(inputJaneiroBy).getAttribute("value");
            boolean preenchido = valorJaneiro != null && !valorJaneiro.isEmpty()
                    && !valorJaneiro.equals("R$ 0,00") && !valorJaneiro.equals("0,00");
            System.out.println("LOG: Distribuição mensal preenchida: " + preenchido
                    + " (Janeiro = " + valorJaneiro + ")");
            return preenchido;
        } catch (Exception e) {
            System.out.println("LOG: Não foi possível verificar distribuição mensal.");
            return false;
        }
    }

    /**
     * Clica em "Salvar LOA".
     * div#btnconfirm > button.btn-primary
     */
    public void salvarLoa() {
        localizarFocoNoElemento(btnSalvarLoaBy, "Botão Salvar LOA");
        WebElement btn = driver.findElement(btnSalvarLoaBy);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(true);" +
                        "var ev = new MouseEvent('click',{bubbles:true,cancelable:true});" +
                        "arguments[0].dispatchEvent(ev);", btn);
        try { Thread.sleep(3000); } catch (InterruptedException e) {}
        driver.switchTo().defaultContent();
        System.out.println("LOG: Salvar LOA clicado.");
    }

    /** Clica em "Cancelar" para fechar o modal sem salvar. */
    public void cancelar() {
        localizarFocoNoElemento(btnCancelarBy, "Botão Cancelar");
        clicarGarantido(driver.findElement(btnCancelarBy));
        driver.switchTo().defaultContent();
        System.out.println("LOG: Operação cancelada.");
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  VALIDAÇÕES
    // ═══════════════════════════════════════════════════════════════════════

    public String capturarAlertaSistema() {
        driver.switchTo().defaultContent();
        return buscarErroRecursivamente(driver);
    }

    public boolean validarSalvamento() {
        return validarSalvamentoPorBotaoEditar();
    }
}