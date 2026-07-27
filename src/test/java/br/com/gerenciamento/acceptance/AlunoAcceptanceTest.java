package br.com.gerenciamento.acceptance;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AlunoAcceptanceTest extends BaseAcceptanceTest {

    @Test
    public void deveCadastrarAlunoEExibirNaListagem() {
        String codigo = UUID.randomUUID().toString().substring(0, 6);
        String nomeDoAluno = "Estudante QA " + codigo;

        navegador.get(enderecoDe("/inserirAlunos"));

        espera.until(ExpectedConditions.visibilityOfElementLocated(By.id("nome")))
                .sendKeys(nomeDoAluno);

        // Os selects usam o próprio nome do enum como value (th:value="${...}")
        new Select(navegador.findElement(By.id("curso"))).selectByValue("INFORMATICA");
        navegador.findElement(By.id("matricula")).sendKeys("MTR-" + codigo);
        new Select(navegador.findElement(By.id("turno"))).selectByValue("MATUTINO");
        new Select(navegador.findElement(By.id("status"))).selectByValue("ATIVO");

        navegador.findElement(
                By.xpath("//form[@action='/InsertAlunos']//button[@type='submit']")).click();

        espera.until(ExpectedConditions.urlContains("/alunos-adicionados"));

        assertTrue("O aluno recém-cadastrado deveria aparecer na tabela de listagem",
                navegador.getPageSource().contains(nomeDoAluno));
    }
}
