package tests;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.markuputils.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.junit.*;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import java.time.Duration;

public class BaseTest {

    protected static ExtentReports extent;
    public static ExtentTest test;
    protected WebDriver driver;

    // --- CAPTURA DE SCREENSHOT PARA O EXTENT ---
    protected String capturarScreenshotBase64() {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            System.out.println("LOG: Erro ao capturar screenshot: " + e.getMessage());
            return "";
        }
    }

    // --- CONFIGURAÇÃO DO RELATÓRIO (roda uma vez por suite) ---
    @BeforeClass
    public static void setupReport() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("target/RelatorioCOSIP.html");
            spark.config().setReportName("COSIP — Relatório de Automação");
            spark.config().setDocumentTitle("COSIP Testes Automatizados");
            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Sistema", "COSIP-SEFAZ");
            extent.setSystemInfo("URL",     "https://app02.makernocode.dev/open.do?sys=TL6");
            extent.setSystemInfo("Browser", "Chrome");
        }
    }

    // --- SETUP DO DRIVER (roda antes de cada teste) ---
    @Before
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");
        // options.addArguments("--headless=new"); // Ative para CI/CD

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        driver.get("https://app02.makernocode.dev/open.do?sys=TL6");
    }

    // --- TESTWATCHER: captura falha com screenshot automaticamente ---
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void failed(Throwable t, Description description) {
            if (test != null && driver != null) {
                try {
                    String base64 = capturarScreenshotBase64();
                    test.log(Status.FAIL, MarkupHelper.createLabel(
                            "TESTE FALHOU: " + description.getMethodName(), ExtentColor.RED));
                    test.fail("Causa da falha: " + t.getMessage());
                    if (base64 != null && !base64.isEmpty()) {
                        test.fail("Print do erro:",
                                MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
                    }
                } catch (Exception ex) {
                    System.out.println("LOG: Erro ao registrar falha no Extent: " + ex.getMessage());
                }
            }
        }

        @Override
        protected void finished(Description description) {
            if (driver != null) {
                driver.quit();
                driver = null;
            }
        }
    };

    @After
    public void tearDown() {
        // Gerenciado pelo TestWatcher
    }

    // --- SALVA O RELATÓRIO AO FINAL DA SUITE ---
    @AfterClass
    public static void salvarRelatorio() {
        if (extent != null) extent.flush();
    }
}
