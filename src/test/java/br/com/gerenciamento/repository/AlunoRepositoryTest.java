package br.com.gerenciamento.repository;

import br.com.gerenciamento.enums.Curso;
import br.com.gerenciamento.enums.Status;
import br.com.gerenciamento.enums.Turno;
import br.com.gerenciamento.model.Aluno;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AlunoRepositoryTest {

    @Autowired
    private AlunoRepository alunoRepository;

    private Aluno novoAluno(String nome, String matricula, Status status) {
        Aluno aluno = new Aluno();
        aluno.setNome(nome);
        aluno.setMatricula(matricula);
        aluno.setStatus(status);
        aluno.setCurso(Curso.INFORMATICA);
        aluno.setTurno(Turno.MATUTINO);
        return aluno;
    }

    private String identificador() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    public void devePersistirAlunoGerandoIdentificador() {
        Aluno salvo = alunoRepository.save(novoAluno("Fernanda Lima", "MAT-" + identificador(), Status.ATIVO));
        Assert.assertNotNull(salvo.getId());
    }

    @Test
    public void deveListarSomenteAlunosInativos() {
        String marca = identificador();
        String matriculaInativo = "INA-" + marca;
        String matriculaAtivo = "ATV-" + marca;

        alunoRepository.save(novoAluno("Roberto Alves " + marca, matriculaInativo, Status.INATIVO));
        alunoRepository.save(novoAluno("Carla Souza " + marca, matriculaAtivo, Status.ATIVO));

        List<Aluno> inativos = alunoRepository.findByStatusInativo();

        Assert.assertTrue("O aluno inativo deveria ser retornado pela consulta",
                inativos.stream().anyMatch(aluno -> matriculaInativo.equals(aluno.getMatricula())));
        Assert.assertFalse("O aluno ativo nao deveria ser retornado pela consulta",
                inativos.stream().anyMatch(aluno -> matriculaAtivo.equals(aluno.getMatricula())));
        Assert.assertTrue("A consulta deveria devolver apenas alunos com status INATIVO",
                inativos.stream().allMatch(aluno -> Status.INATIVO.equals(aluno.getStatus())));
    }

    @Test
    public void deveListarSomenteAlunosAtivos() {
        String nomeUnico = "Ativo " + identificador();
        alunoRepository.save(novoAluno(nomeUnico, "ATV-" + identificador(), Status.ATIVO));

        boolean encontrouAtivo = alunoRepository.findByStatusAtivo().stream()
                .anyMatch(aluno -> nomeUnico.equals(aluno.getNome()));

        Assert.assertTrue(encontrouAtivo);
    }

    @Test
    public void deveBuscarPorParteDoNomeIgnorandoMaiusculas() {
        String token = identificador().substring(0, 5);
        alunoRepository.save(novoAluno("Mariana " + token, "NOM-" + token, Status.ATIVO));

        boolean encontrado = alunoRepository
                .findByNomeContainingIgnoreCase(token.toUpperCase())
                .stream()
                .anyMatch(aluno -> aluno.getNome().contains(token));

        Assert.assertTrue(encontrado);
    }
}
