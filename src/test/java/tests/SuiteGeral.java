package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoginTest.class,
        ParametrosTipTest.class,
        GestaoLoaTest.class,
        EventosTest.class
        // Próximos módulos entram aqui:
        // UploadArquivosTest.class,
        // AliquotasRegrasTest.class,
})
public class SuiteGeral {
    /* mvn test -Dtest=SuiteGeral          → roda tudo
       mvn test -Dtest=GestaoLoaTest       → só este módulo
       mvn test -Dtest=ParametrosTipTest   → só TIP
    */
}