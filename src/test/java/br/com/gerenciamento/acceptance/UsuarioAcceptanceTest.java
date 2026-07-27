package br.com.gerenciamento.acceptance;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsuarioAcceptanceTest extends BaseAcceptanceTest {

    @Test
    public void deveRegistrarUsuarioEEntrarNoSistema() {
        String codigo = UUID.randomUUID().toString().substring(0, 6);
        String login = "aluno_" + codigo;
        String email = login + "@escola.com";
        String senha = "Segredo" + codigo;

        // Etapa 1 - efetuar o cadastro de um novo usuário
        navegador.get(enderecoDe("/cadastro"));

        WebElement campoEmail = espera.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        campoEmail.sendKeys(email);
        navegador.findElement(By.id("user")).sendKeys(login);
        navegador.findElement(By.id("senha")).sendKeys(senha);
        navegador.findElement(By.xpath("//button[contains(., 'Cadastrar')]")).click();

        // O cadastro redireciona para a tela de login; garantimos a troca de página
        espera.until(ExpectedConditions.stalenessOf(campoEmail));

        // Etapa 2 - autenticar com as credenciais recém-criadas
        espera.until(ExpectedConditions.visibilityOfElementLocated(By.id("user")))
                .sendKeys(login);
        navegador.findElement(By.id("senha")).sendKeys(senha);
        navegador.findElement(By.xpath("//button[contains(., 'Login')]")).click();

        WebElement tituloHome = espera.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1.texto-centro")));

        assertTrue(tituloHome.getText().contains("Sistema de Gerenciamento de Alunos"));
    }
}
