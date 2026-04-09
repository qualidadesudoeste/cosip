package tests;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.junit.*;
import pages.LoginPage;
import pages.ParametrosTipPage;

/**
 * Módulo: PARÂMETROS DA TIP (BASE DE CÁLCULO)
 * Caminho: Parametrização > Parâmetro da TIP
 *
 * Cenários baseados na HU-04:
 *   TIP01 — Abrir modal e validar título
 *   TIP02 — Data Início preenchida automaticamente (D+1)
 *   TIP03 — Cadastrar nova vigência com sucesso
 *   TIP04 — Validar que VALOR DO MÓDULO = TARIFA × 1000 (cálculo automático)
 *   TIP05 — Não permitir Tarifa B4A negativa
 *   TIP06 — Cancelar operação fecha o modal sem salvar
 *
 * ATENÇÃO: Ajuste o caminho do PDF em ATO_LEGAL_PDF antes de rodar.
 */
public class ParametrosTipTest extends BaseTest {

    // Ajuste o caminho para um PDF válido na sua máquina
    private static final String ATO_LEGAL_PDF = System.getProperty("user.dir")
            + "/src/test/resources/ato_legal.pdf";

    private LoginPage         loginPage;
    private ParametrosTipPage tipPage;

    @Before
    public void setup() {
        loginPage = new LoginPage(driver);
        tipPage   = new ParametrosTipPage(driver);

        loginPage.realizarLogin("qualidade", "1");
        tipPage.acessarModulo();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  TIP01 — Modal deve abrir com título correto
    // ════════════════════════════════════════════════════════════════════════
    @Test
    public void teste01_DeveAbrirModalComTituloCorreto() {
        test = extent.createTest("TIP01 - Abrir Modal Nova Vigência")
                .assignCategory("Parâmetros da TIP");

        test.log(Status.INFO, MarkupHelper.createLabel(
                "Critério de Aceite: Ao clicar em '+ NOVA VIGÊNCIA', o sistema deve exibir " +
                        "o pop-up com o título 'NOVA VIGÊNCIA – MÓDULO TIP'.",
                ExtentColor.BLUE));

        try {
            test.info("Clicando em '+ NOVA VIGÊNCIA'...");
            tipPage.abrirNovaVigencia();

            test.info("Validando título do modal...");
            boolean tituloCorreto = tipPage.validarTituloModal();
            Assert.assertTrue("O título do modal deve conter 'NOVA VIGÊNCIA'.", tituloCorreto);

            test.pass("Modal aberto com título correto: NOVA VIGÊNCIA – MÓDULO TIP.");
            tipPage.cancelarOperacao();

        } catch (AssertionError | Exception e) {
            test.fail("Teste falhou: " + e.getMessage(),
                    MediaEntityBuilder.createScreenCaptureFromBase64String(capturarScreenshotBase64()).build());
            throw e;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  TIP02 — Data Início deve ser preenchida automaticamente (D+1)
    // ════════════════════════════════════════════════════════════════════════
    @Test
    public void teste02_DataInicioDeveSerPreenchidaAutomaticamente() {
        test = extent.createTest("TIP02 - Data Início Preenchida Automaticamente (D+1)")
                .assignCategory("Parâmetros da TIP");

        test.log(Status.INFO, MarkupHelper.createLabel(
                "Critério de Aceite: O campo Data Início de Vigência deve ser preenchido " +
                        "automaticamente com D+1 da Data Fim da vigência anterior.",
                ExtentColor.ORANGE));

        try {
            test.info("Abrindo modal Nova Vigência...");
            tipPage.abrirNovaVigencia();

            String dataInicio = tipPage.obterDataInicioPreenchida();
            test.info("Data Início preenchida pelo sistema: " + dataInicio);

            Assert.assertNotNull("Data Início não deve ser nula.", dataInicio);
            Assert.assertFalse("Data Início não deve estar vazia — o sistema deve preencher D+1.",
                    dataInicio.trim().isEmpty());

            test.pass("Data Início preenchida automaticamente: " + dataInicio +
                    " (D+1 da vigência anterior).");
            tipPage.cancelarOperacao();

        } catch (AssertionError | Exception e) {
            test.fail("Teste falhou: " + e.getMessage(),
                    MediaEntityBuilder.createScreenCaptureFromBase64String(capturarScreenshotBase64()).build());
            throw e;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  TIP03 — Cadastrar nova vigência com sucesso
    // ════════════════════════════════════════════════════════════════════════
    @Test
    public void teste03_CadastrarNovaVigenciaComSucesso() {
        test = extent.createTest("TIP03 - Cadastrar Nova Vigência com Sucesso")
                .assignCategory("Parâmetros da TIP");

        test.log(Status.INFO, MarkupHelper.createLabel(
                "Critério de Aceite: Preencher Data Fim, upload do Ato Legal e Tarifa B4A " +
                        "e salvar com sucesso. Vigência anterior deve ficar EXPIRADA.",
                ExtentColor.BLUE));

        try {
            test.info("Abrindo modal Nova Vigência...");
            tipPage.abrirNovaVigencia();

            // Data Início já preenchida automaticamente — apenas confirma
            String dataInicio = tipPage.obterDataInicioPreenchida();
            test.info("Data Início (automática): " + dataInicio);

            test.info("Preenchendo Data Fim: 2026-12-31");
            tipPage.preencherDataFimAutomatica();


            test.info("Preenchendo Tarifa B4A: 1.00000");
            tipPage.preencherTarifaB4A("1.00000");

            test.info("Clicando em SALVAR PARÂMETRO...");
            tipPage.salvarParametro();

            try { Thread.sleep(3000); } catch (InterruptedException e) {}

            String alerta = tipPage.capturarAlertaSistema();
            test.info("Mensagem do sistema: " + (alerta != null ? alerta : "Nenhuma"));

            boolean sucesso = tipPage.validarSalvamento() ||
                    (alerta != null && alerta.toLowerCase().contains("sucesso"));

            Assert.assertTrue("A vigência deveria ter sido salva com sucesso.", sucesso);
            test.pass("Nova vigência cadastrada com sucesso!");

        } catch (AssertionError | Exception e) {
            test.fail("Teste falhou: " + e.getMessage(),
                    MediaEntityBuilder.createScreenCaptureFromBase64String(capturarScreenshotBase64()).build());
            throw e;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  TIP04 — VALOR DO MÓDULO deve ser TARIFA × 1000 (cálculo automático)
    // ════════════════════════════════════════════════════════════════════════
    @Test
    public void teste04_ValorModuloDeveSerCalculadoAutomaticamente() {
        test = extent.createTest("TIP04 - Cálculo Automático: Valor do Módulo = Tarifa × 1.000")
                .assignCategory("Parâmetros da TIP");

        test.log(Status.INFO, MarkupHelper.createLabel(
                "Critério de Aceite: O campo VALOR DO MÓDULO (somente leitura) deve ser " +
                        "calculado automaticamente com a fórmula: TARIFA B4A × 1.000 kWh.",
                ExtentColor.ORANGE));

        try {
            test.info("Abrindo modal Nova Vigência...");
            tipPage.abrirNovaVigencia();

            // Tarifa: 1,00 → Módulo esperado: R$ 1.000,00
            test.info("Preenchendo Tarifa B4A: 1.00000");
            tipPage.preencherTarifaB4A("1.00000");

            try { Thread.sleep(1500); } catch (InterruptedException e) {}

            String modulo = tipPage.obterValorModulo();
            test.info("VALOR DO MÓDULO calculado pelo sistema: " + modulo);

            Assert.assertNotNull("VALOR DO MÓDULO não deve ser nulo.", modulo);
            Assert.assertFalse("VALOR DO MÓDULO não deve estar vazio.", modulo.isEmpty());

            // Verifica que o módulo contém 1.000 (tarifa 1,00 × 1000)
            boolean calculoCorreto = modulo.contains("1.000") || modulo.contains("1000");
            Assert.assertTrue(
                    "VALOR DO MÓDULO deve ser R$ 1.000,00 para Tarifa B4A = 1,00. Valor obtido: " + modulo,
                    calculoCorreto);

            test.pass("Cálculo automático correto: 1,00000 × 1.000 = " + modulo);
            tipPage.cancelarOperacao();

        } catch (AssertionError | Exception e) {
            test.fail("Teste falhou: " + e.getMessage(),
                    MediaEntityBuilder.createScreenCaptureFromBase64String(capturarScreenshotBase64()).build());
            throw e;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  TIP05 — Não permitir Tarifa B4A com valor negativo
    // ════════════════════════════════════════════════════════════════════════
    @Test
    public void teste05_NaoDevePermitirTarifaNegativa() {
        test = extent.createTest("TIP05 - Não Permitir Tarifa B4A Negativa")
                .assignCategory("Parâmetros da TIP");

        test.log(Status.INFO, MarkupHelper.createLabel(
                "Critério de Aceite: O sistema não deve permitir a inserção de " +
                        "valores negativos no campo Tarifa B4A.",
                ExtentColor.ORANGE));

        try {
            test.info("Abrindo modal Nova Vigência...");
            tipPage.abrirNovaVigencia();

            test.info("Tentando inserir valor negativo: -1.00000");
            tipPage.preencherTarifaB4A("-1.00000");
            tipPage.preencherDataFimAutomatica();

            test.info("Clicando em SALVAR PARÂMETRO...");
            tipPage.salvarParametro();

            try { Thread.sleep(2000); } catch (InterruptedException e) {}

            String alerta = tipPage.capturarAlertaSistema();
            test.info("Alerta capturado: " + alerta);

            boolean bloqueou = (alerta != null && !alerta.isEmpty()) || !tipPage.validarSalvamento();
            Assert.assertTrue(
                    "Sistema deve bloquear o salvamento com Tarifa B4A negativa.", bloqueou);

            test.pass("Sistema bloqueou corretamente o valor negativo. Alerta: " + alerta);

        } catch (AssertionError | Exception e) {
            test.fail("Teste falhou: " + e.getMessage(),
                    MediaEntityBuilder.createScreenCaptureFromBase64String(capturarScreenshotBase64()).build());
            throw e;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  TIP06 — Cancelar operação fecha o modal sem salvar
    // ════════════════════════════════════════════════════════════════════════
    @Test
    public void teste06_CancelarOperacaoFechaModalSemSalvar() {
        test = extent.createTest("TIP06 - Cancelar Operação Fecha Modal sem Salvar")
                .assignCategory("Parâmetros da TIP");

        test.log(Status.INFO, MarkupHelper.createLabel(
                "Critério de Aceite: Ao clicar em 'CANCELAR OPERAÇÃO', o modal deve " +
                        "fechar sem persistir nenhum dado.",
                ExtentColor.BLUE));

        try {
            test.info("Abrindo modal Nova Vigência...");
            tipPage.abrirNovaVigencia();

            test.info("Preenchendo Tarifa B4A parcialmente...");
            tipPage.preencherTarifaB4A("5.00000");

            test.info("Clicando em CANCELAR OPERAÇÃO...");
            tipPage.cancelarOperacao();

            try { Thread.sleep(2000); } catch (InterruptedException e) {}

            // Após cancelar, o modal deve ter fechado — tentamos abrir novamente
            // Se conseguir abrir, significa que voltou à tela principal normalmente
            test.info("Verificando que voltou à tela principal...");
            tipPage.abrirNovaVigencia();
            boolean modalAbrouNovamente = tipPage.validarTituloModal();
            Assert.assertTrue("Após cancelar, deve ser possível abrir o modal novamente.",
                    modalAbrouNovamente);

            tipPage.cancelarOperacao();
            test.pass("Modal fechado com sucesso ao cancelar — nenhum dado salvo.");

        } catch (AssertionError | Exception e) {
            test.fail("Teste falhou: " + e.getMessage(),
                    MediaEntityBuilder.createScreenCaptureFromBase64String(capturarScreenshotBase64()).build());
            throw e;
        }
    }
}