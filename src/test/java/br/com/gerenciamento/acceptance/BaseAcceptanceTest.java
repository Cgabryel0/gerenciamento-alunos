package br.com.gerenciamento.acceptance;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

/**
 * Infraestrutura compartilhada pelos testes de aceitação: abre uma janela do
 * Chrome antes de cada cenário e a encerra ao final, evitando repetir a
 * configuração do WebDriver em cada classe.
 */
public abstract class BaseAcceptanceTest {

    @LocalServerPort
    protected int porta;

    protected WebDriver navegador;
    protected WebDriverWait espera;

    @Before
    public void abrirNavegador() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions configuracoes = new ChromeOptions();
        configuracoes.addArguments("--remote-allow-origins=*");
        configuracoes.addArguments("--start-maximized");

        this.navegador = new ChromeDriver(configuracoes);
        this.espera = new WebDriverWait(this.navegador, Duration.ofSeconds(15));
    }

    @After
    public void fecharNavegador() {
        if (this.navegador != null) {
            this.navegador.quit();
        }
    }

    protected String enderecoDe(String caminho) {
        return "http://localhost:" + this.porta + caminho;
    }
}
