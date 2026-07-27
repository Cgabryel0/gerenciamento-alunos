package br.com.gerenciamento.service;

import br.com.gerenciamento.enums.Curso;
import br.com.gerenciamento.enums.Status;
import br.com.gerenciamento.enums.Turno;
import br.com.gerenciamento.model.Aluno;
import jakarta.validation.ConstraintViolationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AlunoServiceTest {

    @Autowired
    private ServiceAluno serviceAluno;

    private Aluno montarAluno(String nome, String matricula) {
        Aluno aluno = new Aluno();
        aluno.setNome(nome);
        aluno.setMatricula(matricula);
        aluno.setCurso(Curso.DIREITO);
        aluno.setTurno(Turno.NOTURNO);
        aluno.setStatus(Status.ATIVO);
        return aluno;
    }

    private String sufixo() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    public void deveRecuperarAlunoPeloIdGerado() {
        Aluno gravado = montarAluno("Vinicius Andrade", "MAT-" + sufixo());
        serviceAluno.save(gravado);

        Aluno encontrado = serviceAluno.getById(gravado.getId());
        Assert.assertEquals("Vinicius Andrade", encontrado.getNome());
    }

    @Test
    public void naoDeveSalvarAlunoSemNome() {
        Aluno semNome = new Aluno();
        semNome.setMatricula("MAT-" + sufixo());
        semNome.setCurso(Curso.DIREITO);
        semNome.setTurno(Turno.NOTURNO);
        semNome.setStatus(Status.ATIVO);

        Assert.assertThrows(ConstraintViolationException.class, () -> serviceAluno.save(semNome));
    }

    @Test
    public void deveFiltrarAlunosPorParteDoNome() {
        String token = sufixo();
        serviceAluno.save(montarAluno("Carlos " + token, "MAT-" + sufixo()));
        serviceAluno.save(montarAluno("Carla " + token, "MAT-" + sufixo()));

        long quantidade = serviceAluno.findByNomeContainingIgnoreCase(token).stream()
                .filter(aluno -> aluno.getNome().contains(token))
                .count();

        Assert.assertEquals(2, quantidade);
    }

    @Test
    public void deveRemoverAlunoPeloId() {
        Aluno aluno = montarAluno("Joao Otavio", "MAT-" + sufixo());
        serviceAluno.save(aluno);
        Long id = aluno.getId();

        serviceAluno.deleteById(id);

        boolean aindaPresente = serviceAluno.findAll().stream()
                .anyMatch(a -> id.equals(a.getId()));
        Assert.assertFalse(aindaPresente);
    }
}
