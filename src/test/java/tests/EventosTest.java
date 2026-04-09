package tests;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.junit.*;
import pages.EventosPage;
import pages.LoginPage;

/**
 * Módulo: REGISTRO DE EVENTO/PROBLEMA
 * Caminho: Menu lateral > Registro de Evento/Problema
 *
 * Fluxo da tela:
 *   1. Menu abre listagem (grid: #, Ações, Data e Hora, Competência, Problema Apresentado)
 *   2. Botão "Incluir Registro" (button#addRecordButton) abre o formulário
 *   3. Formulário: Data e Hora, Competência, Contrato, Arquivo,
 *                  Problema Apresentado (máx 200 chars), Linha Arquivo Texto
 *
 * Cenários:
 *   EVT01 — Tela de listagem carrega com botão "Incluir Registro" visível
 *   EVT02 — Formulário abre após clicar em "Incluir Registro"
 *   EVT03 — Cadastrar registro completo com sucesso
 *   EVT04 — Validar campos obrigatórios (salvar sem preencher)
 *   EVT05 — Limite de 200 caracteres no campo Problema Apresentado
 *   EVT06 — Cancelar descarta o preenchimento sem salvar
 */
public class EventosTest extends BaseTest {

    private LoginPage   loginPage;
    private EventosPage eventosPage;

    @Before
    public void setup() {
        loginPage   = new LoginPage(driver);
        eventosPage = new EventosPage(driver);

        loginPage.realizarLogin("qualidade", "1");
        eventosPage.acessarModulo();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  EVT01 — Tela de listagem deve carregar com todos os elementos
    // ════════════════════════════════════════════════════════════════════════
    @Test
    public void teste01_DeveCarregarTelaDeListagem() {
        test = extent.createTest("EVT01 - Carregar Tela de Listagem de Evento/Problema")
                .assignCategory("Registro de Evento/Problema");

        test.log(Status.INFO, MarkupHelper.createLabel(
                "Critério de Aceite: Ao acessar 'Registro de Evento/Problema', " +
                        "a tela deve exibir a grid (colunas: Data e Hora, Competência, " +
                        "Problema Apresentado) e o botão 'Incluir Registro'.",
                ExtentColor.BLUE));

        try {
            test.info("Verificando que a listagem carregou...");
            boolean listagemOk = eventosPage.validarTelaListagemCarregada();
            Assert.assertTrue("Container da listagem deve estar visível.", listagemOk);
            test.info("✔ Listagem carregada.");

            test.info("Verificando botão 'Incluir Registro'...");
            boolean btnOk = eventosPage.validarBotaoIncluirVisivel();
            Assert.assertTrue("Botão 'Incluir Registro' deve estar visível.", btnOk);
            test.info("✔ Botão 'Incluir Registro' visível.");

            test.pass("Tela de listagem carregada com todos os elementos esperados.");

        } catch (AssertionError | Exception e) {
            test.fail("Teste falhou: " + e.getMessage(),
                    MediaEntityBuilder.createScreenCaptureFromBase64String(capturarScreenshotBase64()).build());
            throw e;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  EVT02 — Formulário abre após clicar em "Incluir Registro"
    // ════════════════════════════════════════════════════════════════════════
    @Test
    public void teste02_DeveAbrirFormularioAoClicarIncluir() {
        test = extent.createTest("EVT02 - Abrir Formulário ao Clicar em Incluir Registro")
                .assignCategory("Registro de Evento/Problema");

        test.log(Status.INFO, MarkupHelper.createLabel(
                "Critério de Aceite: Ao clicar em 'Incluir Registro' (button#addRecordButton), " +
                        "o sistema deve abrir o formulário com os campos: Data e Hora, Competência, " +
                        "Contrato, Arquivo, Problema Apresentado e Linha Arquivo Texto.",
                ExtentColor.BLUE));

        try {
            test.info("Clicando em 'Incluir Registro'...");
            eventosPage.incluirRegistro();

            test.info("Verificando que o formulário está aberto...");
            boolean aberto = eventosPage.validarFormularioAberto();
            Assert.assertTrue("Formulário deve abrir com o campo 'Data e Hora' visível.", aberto);

            test.pass("Formulário de inclusão aberto com sucesso após clicar em 'Incluir Registro'.");
            eventosPage.cancelar();

        } catch (AssertionError | Exception e) {
            test.fail("Teste falhou: " + e.getMessage(),
                    MediaEntityBuilder.createScreenCaptureFromBase64String(capturarScreenshotBase64()).build());
            throw e;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  EVT03 — Cadastrar registro completo com sucesso
    // ════════════════════════════════════════════════════════════════════════
    @Test
    public void teste03_CadastrarRegistroComSucesso() {
        test = extent.createTest("EVT03 - Cadastrar Registro de Evento/Problema com Sucesso")
                .assignCategory("Registro de Evento/Problema");

        test.log(Status.INFO, MarkupHelper.createLabel(
                "Critério de Aceite: Preencher todos os campos obrigatórios e salvar. " +
                        "O sistema deve confirmar o salvamento.",
                ExtentColor.BLUE));

        try {
            test.info("Clicando em 'Incluir Registro'...");
            eventosPage.incluirRegistro();

            test.info("Preenchendo Data e Hora: 06/04/2026 14:00:00");
            eventosPage.preencherDataHora("06/04/2026 14:00:00");

            test.info("Preenchendo Competência: 202604");
            eventosPage.preencherCompetencia("202604");

            test.info("Preenchendo Problema Apresentado...");
            eventosPage.preencherProblema(
                    "Divergência identificada na leitura do arquivo RLC. " +
                            "Valor cobrado não corresponde à alíquota vigente no período.");

            test.info("Preenchendo Linha Arquivo Texto...");
            eventosPage.preencherLinhaArquivo(
                    "0101202601|COSIP-RLC-FAT-202601|12345|0,07|150,00|10,50");

            test.info("Salvando registro...");
            eventosPage.salvar();
            try { Thread.sleep(3000); } catch (InterruptedException e) {}

            String alerta = eventosPage.capturarAlertaSistema();
            test.info("Mensagem do sistema: " + (alerta != null ? alerta : "Nenhuma"));

            boolean sucesso = eventosPage.validarSalvamento() ||
                    (alerta != null && alerta.toLowerCase().contains("sucesso"));
            Assert.assertTrue("Registro deveria ter sido salvo com sucesso.", sucesso);

            test.pass("Registro de Evento/Problema cadastrado com sucesso!");

        } catch (AssertionError | Exception e) {
            test.fail("Teste falhou: " + e.getMessage(),
                    MediaEntityBuilder.createScreenCaptureFromBase64String(capturarScreenshotBase64()).build());
            throw e;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  EVT04 — Validar campos obrigatórios
    // ════════════════════════════════════════════════════════════════════════
    @Test
    public void teste04_ValidarCamposObrigatorios() {
        test = extent.createTest("EVT04 - Validar Campos Obrigatórios")
                .assignCategory("Registro de Evento/Problema");

        test.log(Status.INFO, MarkupHelper.createLabel(
                "Critério de Aceite: Ao tentar salvar sem preencher os campos obrigatórios " +
                        "(marcados com *), o sistema deve exibir mensagem de alerta bloqueando o salvamento.",
                ExtentColor.ORANGE));

        try {
            test.info("Abrindo formulário sem preencher nenhum campo...");
            eventosPage.incluirRegistro();

            test.info("Tentando salvar formulário vazio...");
            eventosPage.salvar();
            try { Thread.sleep(2000); } catch (InterruptedException e) {}

            String alerta = eventosPage.capturarAlertaSistema();
            test.info("Alerta capturado: " + alerta);

            boolean bloqueou = (alerta != null && !alerta.isEmpty()) ||
                    !eventosPage.validarSalvamento();
            Assert.assertTrue(
                    "Sistema deve bloquear o salvamento com campos obrigatórios vazios.",
                    bloqueou);

            test.pass("Validação de obrigatoriedade funcionando. Mensagem: " + alerta);

        } catch (AssertionError | Exception e) {
            test.fail("Teste falhou: " + e.getMessage(),
                    MediaEntityBuilder.createScreenCaptureFromBase64String(capturarScreenshotBase64()).build());
            throw e;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  EVT05 — Limite de 200 caracteres no Problema Apresentado
    // ════════════════════════════════════════════════════════════════════════
    @Test
    public void teste05_ValidarLimite200CaracteresProblema() {
        test = extent.createTest("EVT05 - Validar Limite de 200 Caracteres no Problema Apresentado")
                .assignCategory("Registro de Evento/Problema");

        test.log(Status.INFO, MarkupHelper.createLabel(
                "Critério de Aceite: O campo 'Problema Apresentado' deve aceitar no máximo " +
                        "200 caracteres conforme indicado no placeholder da tela.",
                ExtentColor.ORANGE));

        try {
            test.info("Abrindo formulário de inclusão...");
            eventosPage.incluirRegistro();

            // Tenta digitar 210 caracteres
            String texto210 = "X".repeat(210);
            test.info("Digitando 210 caracteres no campo Problema Apresentado...");
            eventosPage.preencherProblema(texto210);

            int tamanhoFinal = eventosPage.obterTamanhoProblema();
            test.info("Caracteres aceitos pelo campo: " + tamanhoFinal + " (máximo permitido: 200)");

            Assert.assertTrue(
                    "O campo deve aceitar no máximo 200 caracteres. Aceitou: " + tamanhoFinal,
                    tamanhoFinal <= 200);

            test.pass("Limite de 200 caracteres respeitado. Campo aceitou: " + tamanhoFinal + " chars.");
            eventosPage.cancelar();

        } catch (AssertionError | Exception e) {
            test.fail("Teste falhou: " + e.getMessage(),
                    MediaEntityBuilder.createScreenCaptureFromBase64String(capturarScreenshotBase64()).build());
            throw e;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  EVT06 — Cancelar descarta dados sem salvar
    // ════════════════════════════════════════════════════════════════════════
    @Test
    public void teste06_CancelarDescartaDadosSemSalvar() {
        test = extent.createTest("EVT06 - Cancelar Descarta Dados sem Salvar")
                .assignCategory("Registro de Evento/Problema");

        test.log(Status.INFO, MarkupHelper.createLabel(
                "Critério de Aceite: Ao cancelar o formulário, o sistema deve descartar " +
                        "os dados inseridos. O Maker mantém a aba aberta mas limpa o formulário " +
                        "e reexibe o botão 'Incluir Registro'.",
                ExtentColor.BLUE));

        try {
            test.info("Abrindo formulário e preenchendo dados parcialmente...");
            eventosPage.incluirRegistro();
            eventosPage.preencherDataHora("06/04/2026 10:00:00");
            eventosPage.preencherCompetencia("202604");
            eventosPage.preencherProblema("Dado de teste — não deve ser salvo.");

            test.info("Cancelando operação (X da toolbar)...");
            eventosPage.cancelar();
            try { Thread.sleep(2000); } catch (InterruptedException e) {}

            // O Maker não fecha a aba — retorna ao estado de listagem com addRecordButton visível
            test.info("Verificando que o botão 'Incluir Registro' voltou a ficar visível...");
            boolean voltouEstadoInicial = eventosPage.validarTelaListagemCarregada();
            Assert.assertTrue(
                    "Após cancelar, o botão 'Incluir Registro' deve estar visível (estado de listagem).",
                    voltouEstadoInicial);

            test.pass("Cancelamento funcionou — dados descartados e 'Incluir Registro' visível novamente.");

        } catch (AssertionError | Exception e) {
            test.fail("Teste falhou: " + e.getMessage(),
                    MediaEntityBuilder.createScreenCaptureFromBase64String(capturarScreenshotBase64()).build());
            throw e;
        }
    }
}