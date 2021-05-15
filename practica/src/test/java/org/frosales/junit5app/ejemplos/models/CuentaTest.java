package org.frosales.junit5app.ejemplos.models;

import jdk.jfr.Enabled;
import org.frosales.junit5app.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)

class CuentaTest {
    Cuenta cuenta;
    private TestInfo testInfo;
    private TestReporter testReporter;

    @BeforeEach
    void initMetodTest(TestInfo testInfo,TestReporter testReporter){
        this.cuenta = new Cuenta("fede", new BigDecimal("10879.465"));
        this.testInfo = testInfo;
        this.testReporter = testReporter;
        System.out.println("iniciando el metodo.");
        testReporter.publishEntry("ejecutando: " + testInfo.getDisplayName() +
                " " + testInfo.getTestMethod().orElse(null).getName() +
                " con las etiquetas: " + testInfo.getTags());
    }

    @AfterEach
    void tearDown(){
        System.out.println("finalizando el metodo de prueba");
    }

    @BeforeAll
     void beforeAll() {
        System.out.println("Inicializando la clase Test");
    }

    @AfterAll
     void afterAll() {
        System.out.println("finalizando el test");
    }
    @Tag("Cuenta")
    @Nested
    @DisplayName("Probando atributo de la cuenta")
    class CuentaTestNombreSaldo{
        @Test
        @DisplayName("Nombre")
        void testNombreCuenta() {
            testReporter.publishEntry(testInfo.getTags().toString());
            if(testInfo.getTags().contains("cuenta")){
                testReporter.publishEntry("hacer algo con la etiqueta cuenta");
            }
            cuenta = new Cuenta("fede", new BigDecimal("10879.465"));
            String esperado = "fede";
            String real = cuenta.getPersona();
            assertNotNull(real,() -> "La cuenta no puede ser nula");
            assertEquals(esperado, real,() ->"El nombre de la cuenta no es el nombre que se esperaba: "
                    + esperado + ", sin embargo fue: " + real);
            assertTrue(real.equals("fede"),() ->"El nombre cuenta esperada debe ser igual a la real");
        }

        @Test
        @DisplayName("Saldo, que no sea Null, menor que cero,valor esperado")
        void testSaldoCuenta() {
            assertNotNull(cuenta.getSaldo());
            assertEquals(10879.465, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0); // comparamos que nunca sea cero ni negatico
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0); // comparamos que sea siempre positivo
        }

        @Test
        @DisplayName("Testeando referencias que sean iguales con el metodo equals")
        void testReferenciaCuenta() {
            cuenta = new Cuenta("John Doe", new BigDecimal("1000.9998"));
            Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("1000.9998"));

            // assertNotEquals(cuenta,cuenta2); // controlamos que las cuentas/objetos no sean iguales
            assertEquals(cuenta, cuenta2); // falla porque son objetos con instancias diferentes
        }

    }
    @Nested
    class CuentaOperacionestest{
        @Tag("cuenta")
        @Test
        @DisplayName("Probando DebitoCuenta")
        void testDebitoCuenta() {
            cuenta = new Cuenta("John Doe", new BigDecimal("1000.9998"));
            cuenta.debito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue()); // resta la parte entera
            assertEquals("900.9998", cuenta.getSaldo().toPlainString());
        }
        @Tag("cuenta")
        @Test
        @DisplayName("Probando CreditoCuenta")
        void testCreditoCuenta() {
            cuenta = new Cuenta("John Doe", new BigDecimal("1000.9998"));
            cuenta.credito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue()); // resta la parte entera
            assertEquals("1100.9998", cuenta.getSaldo().toPlainString());
        }
        @Tag("cuenta")
        @Tag("banco")
        @Test
        @DisplayName("Probando TransferenciaDineroCuenta")
        void testTransferirDineroCuenta() {
            Cuenta cuenta1 = new Cuenta("fede", new BigDecimal("2500"));
            Cuenta cuenta2 = new Cuenta("andres", new BigDecimal("1500.8989"));
            Banco banco = new Banco();
            banco.setNombre("Banco del Estado");
            banco.tranferir(cuenta2, cuenta1, new BigDecimal(500));
            assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
            assertEquals("3000", cuenta1.getSaldo().toPlainString());


        }

    }

    @Tag("cuenta")
    @Tag("Error")
    @Test
    @DisplayName("Probando la exceptionDineroInsuficiente")
    void testDineroInsuficienteExceptionCuenta() {
        cuenta = new Cuenta("fede", new BigDecimal("1000.12345"));
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal("1500"));
        });
        String real = exception.getMessage();
        String esperado = "Dinero Insuficiente";
        assertEquals(esperado, real);
    }

    @Tag("cuenta")
    @Tag("banco")
    @Test
    @DisplayName("Probando relaciones entre las cuentas y el banco con assertAll")
    void testRelacionBancoCuentas() {
        Cuenta cuenta1 = new Cuenta("fede", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("andres", new BigDecimal("1500.8989"));
        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("Banco del Estado");
        banco.tranferir(cuenta2, cuenta1, new BigDecimal(500));
        assertAll(
                () ->assertEquals("1000.8989", cuenta2.getSaldo().toPlainString(),()-> "El valor de la cuenta2 del saldo no es el esperado."),
                () -> assertEquals("3000", cuenta1.getSaldo().toPlainString(),()-> "El valor de la cuenta1 del saldo no es el esperado."),
                () -> assertEquals(2, banco.getCuentas().size(),()-> "El banco no tiene las cuentas esperadas."),
                () -> assertEquals("Banco del Estado", cuenta1.getBanco().getNombre(),()-> "El nombre del banco no es el esperado."),
                () -> {
                    assertEquals("fede", banco.getCuentas().stream().
                            filter(c -> c.getPersona().equals("fede")).
                            findFirst().get().getPersona());
                }, () -> {
                    assertTrue(banco.getCuentas().stream().
                            anyMatch(c -> c.getPersona().equals("andres")));
                });

    }

    @Nested
    class SistemaOperativoTest{
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows(){

        }

        @Test
        @EnabledOnOs({OS.LINUX,OS.MAC})
        void testSoloLinuxMac(){

        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows(){

        }
    }

    @Nested
class JavaVersionTest{
    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void soloJDK(){

    }

    @Test
    @EnabledOnJre(JRE.JAVA_15)
    void soloJDK15(){

    }

    @Test
    @DisabledOnJre(JRE.JAVA_15)
    void soloNoJDK15(){

    }
}

    @Nested
class SistemPropertyTest{
    @Test
    void imprimirSystemProperties(){
        Properties properties= System.getProperties();
        properties.forEach((k,v) -> System.out.println(k + ":" + v));
    }

    @Test
    @EnabledIfSystemProperty(named="java.version",matches="15.0.2")
    void testJavaVersion(){

    }

    @Test
    @DisabledIfSystemProperty(named="os.arch",matches=".*32.*")
    void testSolo64(){

    }

    @Test
    @EnabledIfSystemProperty(named="os.arch",matches=".*32.*")
    void testNoSolo64(){

    }

    @Test
    @EnabledIfSystemProperty(named="user.name",matches="Federico")
    void testUserName(){

    }

    @Test
    @EnabledIfSystemProperty(named="ENV",matches="dev")
    void testDev(){

    }

}

    @Nested
class VariableAmbienteTest {
    @Test
    void imprimirVariablesAmbiente(){
        Map<String,String> getenv= System.getenv();
        getenv.forEach((k,v)-> System.out.println(k + " = " + v));
    }

    @Test
    @EnabledIfEnvironmentVariable(named="JAVA_HOME",matches = ".*jdk-15.0.1.*")
    void testJavaHome(){

    }

    @Test
    @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS",matches="8")
    void testProcesadores(){

    }

    @Test
    @EnabledIfEnvironmentVariable(named="ENVIRONMENT",matches="dev")
    void testenv(){

    }

    @Test
    @DisabledIfEnvironmentVariable(named="ENVIRONMENT",matches="prod")
    void testEnvProdDisabled(){

    }

}


    @Test
    @DisplayName("test Saldo Cuenta Dev")
    void testSaldoCuentaDev() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(esDev, () -> {
            assertNotNull(cuenta.getSaldo());
            assertEquals(10879.465, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0); // comparamos que nunca sea cero ni negatico
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0); // comparamos que sea siempre positivo

        });
          }

          // repetimos test
    @DisplayName("Probando DebitoCuenta Repetir!")
    @RepeatedTest(value=5,name="{displayName} - Repeticion numero {currentRepetition} de {totalRepetitions}")
    void testDebitoCuentaRepetir(RepetitionInfo info) {
if(info.getCurrentRepetition() == 3 ){
    System.out.println("estamos en la repeticion " + info.getCurrentRepetition());
}

        cuenta = new Cuenta("John Doe", new BigDecimal("1000.9998"));
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue()); // resta la parte entera
        assertEquals("900.9998", cuenta.getSaldo().toPlainString());
    }

    @Tag("param")
    @Nested
    class PruebasParametrizadadasTest {
        @ParameterizedTest(name = "numero {index} ejecutando con valor {0}")
        @ValueSource(strings = {"100","200","300","500","1000"})
        void testDebitoCuentaValueSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0); // resta la parte entera
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0}")
        @CsvSource( {"1,100","2,200","3,300","4,500","5,1000"})
        void testDebitoCuentaCsvSource(String index, String monto) {
            System.out.println(index + " -> " + monto);
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0); // resta la parte entera
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0}")
        @CsvSource( {"200,100,fede,ramiro","400,200,juan,juan","301,300,lucas,luca","900,500,luis,luis","1001,1000,gabriel,gabriel"})
        void testDebitoCuentaCsvSource2(String saldo, String monto,String esperado,String actual) {
            System.out.println(saldo + " -> " + monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);
            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado,actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0); // resta la parte entera
        }
        @ParameterizedTest(name = "numero {index} ejecutando con valor {0}")
        @CsvFileSource( resources = { "/data.csv"})
        void testDebitoCuentaCsvFileSource( String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0); // resta la parte entera
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0}")
        @CsvFileSource( resources = { "/data2.csv"})
        void testDebitoCuentaCsvFileSource2( String saldo, String monto,String esperado,String actual) {
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);
            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado,actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0); // resta la parte entera
        }
    }

    @Tag("param")
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0}")
    @MethodSource("montoList")
    void testDebitoCuentaMethodSource( String monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0); // resta la parte entera
    }

    static List<String> montoList(){
        return Arrays.asList("100","200","300","500","1000");
    }

    @Nested
    @Tag("timeout")
    class testTimeout {
        @Test
        @Timeout(1)
        void pruebaTimeout() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(100);
        }
        @Test
        @Timeout(value=1000,unit = TimeUnit.MILLISECONDS)
        void pruebaTimeout2() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(900);
        }
        @Test
        void testTimeoutAssertions() {
            assertTimeout(Duration.ofSeconds(5), () ->{
                TimeUnit.MILLISECONDS.sleep(5000);
            });
        }
    }

}