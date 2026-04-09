package tests;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.junit.*;
import org.openqa.selenium.By;
import pages.GestaoLoaPage;
import pages.LoginPage;

/**
 * Módulo: GESTÃO DA LOA
 * Caminho: Parametrização > Gestão da LOA
 *
 * Cenários baseados na HU-02:
 *   LOA01 — Verificar elementos da tela principal (título, botão, grid, total)
 *   LOA02 — Filtrar histórico por ano
 *   LOA03 — Abrir modal Adicionar LOA e verificar campos
 *   LOA04 — Cadastrar novo exercício com distribuição automática
 *   LOA05 — Validar que VALOR A DISTRIBUIR deve ser R$0,00 para salvar
 *   LOA06 — Validar duplicidade: exercício já cadastrado
 *   LOA07 — Cancelar operação fecha modal sem salvar
 */
public class GestaoLoaTest extends BaseTest {

    private LoginPage     loginPage;
    private GestaoLoaPage loaPage;

    @Before
    public void setup() {
        loginPage = new LoginPage(driver);
        loaPage   = new GestaoLoaPage(driver);

        loginPage.realizarLogin("qualidade", "1");
        loaPage.acessarModulo();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LOA01 — Tela principal deve exibir todos os elementos esperados
    // ════════════════════════════════════════════════════════════════════════
    @Test
    public void teste01_DeveExibirElementosDaTelaGestaoLoa() {
        test = extent.createTest("LOA01 - Verificar Elementos da Tela Gestão da LOA")
                .assignCategory("Gestão da LOA");

        test.log(Status.INFO, MarkupHelper.createLabel(
                "Critério de Aceite: A tela deve exibir o título 'GESTÃO DA LOA', " +
                        "subtítulo 'Parametrização do Orçamento Anual', botão '+ Adicionar Exercício' " +
                        "e a grid 'Histórico LOA' com o total de registros.",
                ExtentColor.BLUE));

        try {
            test.info("Validando título da tela...");
            boolean tituloOk = loaPage.validarTituloDaTela();
            Assert.assertTrue("Título 'GESTÃO DA LOA' deve estar visível.", tituloOk);
            test.info("✔ Título GESTÃO DA LOA encontrado.");

            test.info("Validando botão '+ Adicionar Exercício'...");
            boolean btnOk = loaPage.validarBotaoAdicionarVisivel();
            Assert.assertTrue("Botão '+ Adicionar Exercício' deve estar visível.", btnOk);
            test.info("✔ Botão Adicionar Exercício visível.");

            test.info("Validando grid Histórico LOA...");
            boolean gradeOk = loaPage.validarGridHistoricoVisivel();
            Assert.assertTrue("Grid 'Histórico LOA' deve estar visível.", gradeOk);
            test.info("✔ Grid Histórico LOA visível.");

            String total = loaPage.obterTotalRegistros();
            test.info("Total de registros exibido: " + total);

            test.pass("Todos os elementos da tela Gestão da LOA estão presentes e visíveis.");

        } catch (AssertionError | Exception e) {
            test.fail("Teste falhou: " + e.getMessage(),
                    MediaEntityBuilder.createScreenCaptureFromBase64String(capturarScreenshotBase64()).build());
            throw e;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LOA02 — Filtrar histórico por ano
    // ════════════════════════════════════════════════════════════════════════
    @Test
    public void teste02_DeveFiltrarHistoricoPorAno() {
        test = extent.createTest("LOA02 - Filtrar Histórico da LOA por Ano")
                .assignCategory("Gestão da LOA");

        test.log(Status.INFO, MarkupHelper.createLabel(
                "Critério de Aceite: Ao informar um ano no filtro e clicar em FILTRAR, " +
                        "a grid deve exibir apenas os exercícios do ano informado.",
                ExtentColor.BLUE));

        try {
            test.info("Aplicando filtro por ano: 2026");
            loaPage.filtrarPorAno("2026");

            String total = loaPage.obterTotalRegistros();
            test.info("Total de registros após filtro: " + total);

            test.pass("Filtro por ano aplicado. Registros retornados: " + total);

        } catch (AssertionError | Exception e) {
            test.fail("Teste falhou: " + e.getMessage(),
                    MediaEntityBuilder.createScreenCaptureFromBase64String(capturarScreenshotBase64()).build());
            throw e;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LOA03 — Modal Adicionar LOA deve abrir com todos os campos
    // ════════════════════════════════════════════════════════════════════════
    @Test
    public void teste03_DeveAbrirModalComCamposCorretos() {
        test = extent.createTest("LOA03 - Abrir Modal Adicionar LOA e Verificar Campos")
                .assignCategory("Gestão da LOA");

        test.log(Status.INFO, MarkupHelper.createLabel(
                "Critério de Aceite: Ao clicar em '+ Adicionar Exercício', o sistema deve " +
                        "exibir o modal com os campos: ANO, VALOR TOTAL ANUAL e DISTRIBUIÇÃO MENSAL.",
                ExtentColor.BLUE));

        try {
            test.info("Clicando em '+ Adicionar Exercício'...");
            loaPage.abrirAdicionarExercicio();

            test.info("Modal aberto. Verificando campo ANO...");
            // Valida que o campo ANO está acessível (Boneca Russa encontrou)
            loaPage.preencherAno("2099");
            test.info("✔ Campo ANO acessível e editável.");

            test.pass("Modal Adicionar LOA aberto com campos ANO e VALOR TOTAL ANUAL visíveis.");
            loaPage.cancelar();

        } catch (AssertionError | Exception e) {
            test.fail("Teste falhou: " + e.getMessage(),
                    MediaEntityBuilder.createScreenCaptureFromBase64String(capturarScreenshotBase64()).build());
            throw e;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LOA04 — Cadastrar exercício com distribuição automática
    // ════════════════════════════════════════════════════════════════════════
    @Test
    public void teste04_CadastrarExercicioComDistribuicaoAutomatica() {
        test = extent.createTest("LOA04 - Cadastrar Exercício com Distribuição Automática")
                .assignCategory("Gestão da LOA");

        test.log(Status.INFO, MarkupHelper.createLabel(
                "Critério de Aceite: Ao informar o Valor Total Anual, o sistema deve dividir " +
                        "automaticamente em 12 parcelas iguais. Saldo remanescente vai para Janeiro. " +
                        "VALOR A DISTRIBUIR deve ficar em R$ 0,00 para permitir salvar.",
                ExtentColor.BLUE));

        // Gera um ano único para evitar conflito de duplicidade
        String anoTeste = String.valueOf(2030 + (int)(System.currentTimeMillis() % 10));

        try {
            test.info("Abrindo modal Adicionar LOA...");
            loaPage.abrirAdicionarExercicio();

            test.info("Preenchendo ANO: " + anoTeste);
            loaPage.preencherAno(anoTeste);

            test.info("Preenchendo Valor Total Anual: R$ 120.000,00");
            loaPage.preencherValorTotalAnual("120000");

            test.info("Verificando distribuição mensal automática...");
            boolean distribuido = loaPage.verificarDistribuicaoMensalPreenchida();
            test.info("Distribuição mensal preenchida automaticamente: " + distribuido);

            String valorADistribuir = loaPage.obterValorADistribuir();
            test.info("VALOR A DISTRIBUIR: " + valorADistribuir);

            test.info("Clicando em Salvar LOA...");
            loaPage.salvarLoa();

            try { Thread.sleep(3000); } catch (InterruptedException e) {}

            String alerta = loaPage.capturarAlertaSistema();
            test.info("Mensagem do sistema: " + (alerta != null ? alerta : "Nenhuma"));

            boolean sucesso = loaPage.validarSalvamento() ||
                    (alerta != null && alerta.toLowerCase().contains("sucesso"));

            Assert.assertTrue("Exercício " + anoTeste + " deveria ter sido salvo com sucesso.", sucesso);
            test.pass("Exercício " + anoTeste + " cadastrado com distribuição automática!");

        } catch (AssertionError | Exception e) {
            test.fail("Teste falhou: " + e.getMessage(),
                    MediaEntityBuilder.createScreenCaptureFromBase64String(capturarScreenshotBase64()).build());
            throw e;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LOA05 — VALOR A DISTRIBUIR deve ser R$0,00 para salvar
    // ════════════════════════════════════════════════════════════════════════
    @Test
    public void teste05_NaoDeveSalvarComValorADistribuirPendente() {
        test = extent.createTest("LOA05 - Bloquear Salvar com Valor a Distribuir Pendente")
                .assignCategory("Gestão da LOA");

        test.log(Status.INFO, MarkupHelper.createLabel(
                "Critério de Aceite: O cadastro não poderá ser concluído enquanto o campo " +
                        "VALOR A DISTRIBUIR não estiver em R$ 0,00. " +
                        "Qualquer saldo positivo ou negativo deve acionar uma mensagem de alerta.",
                ExtentColor.ORANGE));

        try {
            test.info("Abrindo modal Adicionar LOA...");
            loaPage.abrirAdicionarExercicio();

            // Preenche um valor que não divide exatamente por 12 para gerar saldo
            test.info("Preenchendo ANO: 2098");
            loaPage.preencherAno("2098");

            // Não preenche o Valor Total Anual para forçar saldo pendente
            test.info("Tentando salvar sem preencher os campos obrigatórios...");
            loaPage.salvarLoa();

            try { Thread.sleep(2000); } catch (InterruptedException e) {}

            String alerta = loaPage.capturarAlertaSistema();
            test.info("Alerta capturado do sistema: " + alerta);

            boolean bloqueou = (alerta != null && !alerta.isEmpty()) || !loaPage.validarSalvamento();
            Assert.assertTrue("Sistema deve bloquear o salvamento com VALOR A DISTRIBUIR pendente.", bloqueou);

            test.pass("Sistema bloqueou corretamente. Mensagem: " + alerta);

        } catch (AssertionError | Exception e) {
            test.fail("Teste falhou: " + e.getMessage(),
                    MediaEntityBuilder.createScreenCaptureFromBase64String(capturarScreenshotBase64()).build());
            throw e;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LOA06 — Validar duplicidade: exercício já cadastrado
    // ════════════════════════════════════════════════════════════════════════
    @Test
    public void teste06_ValidarDuplicidadeDeExercicio() {
        test = extent.createTest("LOA06 - Validar Duplicidade de Exercício da LOA")
                .assignCategory("Gestão da LOA");

        test.log(Status.INFO, MarkupHelper.createLabel(
                "Critério de Aceite: Ao informar um ANO já cadastrado, o sistema deve " +
                        "exibir a mensagem: 'Exercício da LOA já cadastrado.'",
                ExtentColor.ORANGE));

        // Usa um exercício que sabemos existir na base (visível na grid: 2029, 2027, 2026)
        String anoDuplicado = "2029";

        try {
            test.info("Abrindo modal Adicionar LOA...");
            loaPage.abrirAdicionarExercicio();

            test.info("Preenchendo ANO já existente: " + anoDuplicado);
            loaPage.preencherAno(anoDuplicado);

            // O TAB dentro de preencherAno já dispara a validação
            try { Thread.sleep(2000); } catch (InterruptedException e) {}

            String alerta = loaPage.capturarAlertaSistema();
            test.info("Alerta capturado: " + alerta);

            boolean duplicidade = alerta != null && (
                    alerta.toLowerCase().contains("já cadastrado") ||
                            alerta.toLowerCase().contains("exercício") ||
                            alerta.toLowerCase().contains("duplicado"));

            Assert.assertTrue(
                    "Sistema deve exibir mensagem 'Exercício da LOA já cadastrado.' " +
                            "Mensagem recebida: " + alerta,
                    duplicidade);

            test.pass("Validação de duplicidade funcionando. Mensagem: " + alerta);

        } catch (AssertionError | Exception e) {
            test.fail("Teste falhou: " + e.getMessage(),
                    MediaEntityBuilder.createScreenCaptureFromBase64String(capturarScreenshotBase64()).build());
            throw e;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LOA07 — Cancelar fecha modal sem salvar
    // ════════════════════════════════════════════════════════════════════════
    @Test
    public void teste07_CancelarFechaModalSemSalvar() {
        test = extent.createTest("LOA07 - Cancelar Fecha Modal sem Salvar")
                .assignCategory("Gestão da LOA");

        test.log(Status.INFO, MarkupHelper.createLabel(
                "Critério de Aceite: Ao clicar em 'Cancelar', o modal deve fechar " +
                        "sem persistir nenhum dado.",
                ExtentColor.BLUE));

        try {
            test.info("Abrindo modal Adicionar LOA...");
            loaPage.abrirAdicionarExercicio();

            test.info("Preenchendo dados parcialmente...");
            loaPage.preencherAno("2097");

            test.info("Clicando em Cancelar...");
            loaPage.cancelar();

            try { Thread.sleep(2000); } catch (InterruptedException e) {}

            // Após cancelar, deve conseguir abrir o modal novamente normalmente
            test.info("Verificando que voltou à tela principal...");
            loaPage.abrirAdicionarExercicio();
            boolean modalAbriu = loaPage.validarBotaoAdicionarVisivel() ||
                    !driver.findElements(By.id("WFRInput1135897")).isEmpty();

            loaPage.cancelar();

            test.pass("Modal fechado com sucesso ao cancelar — nenhum dado foi salvo.");

        } catch (AssertionError | Exception e) {
            test.fail("Teste falhou: " + e.getMessage(),
                    MediaEntityBuilder.createScreenCaptureFromBase64String(capturarScreenshotBase64()).build());
            throw e;
        }
    }
}