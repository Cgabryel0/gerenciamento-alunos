package br.com.gerenciamento.controller;

import br.com.gerenciamento.enums.Curso;
import br.com.gerenciamento.enums.Status;
import br.com.gerenciamento.enums.Turno;
import br.com.gerenciamento.model.Aluno;
import br.com.gerenciamento.repository.AlunoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AlunoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlunoRepository alunoRepository;

    private void gravarAluno(String nome) {
        Aluno aluno = new Aluno();
        aluno.setNome(nome);
        aluno.setMatricula("MAT-" + UUID.randomUUID());
        aluno.setCurso(Curso.CONTABILIDADE);
        aluno.setStatus(Status.ATIVO);
        aluno.setTurno(Turno.NOTURNO);
        alunoRepository.save(aluno);
    }

    @Test
    public void pesquisaComNomeVazioDeveTrazerTodosOsAlunos() throws Exception {
        gravarAluno("Ana Paula Souza");
        gravarAluno("Bruno Carvalho");

        mockMvc.perform(post("/pesquisar-aluno").param("nome", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("ListaDeAlunos"))
                .andExpect(model().attribute("ListaDeAlunos", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    public void pesquisaSemInformarNomeDeveTrazerTodosOsAlunos() throws Exception {
        gravarAluno("Joana Ribeiro");

        mockMvc.perform(post("/pesquisar-aluno"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("ListaDeAlunos"));
    }

    @Test
    public void pesquisaPorNomeDeveFiltrarOsResultados() throws Exception {
        String token = UUID.randomUUID().toString().substring(0, 6);
        gravarAluno("Cliente " + token);
        gravarAluno("Outro Aluno");

        mockMvc.perform(post("/pesquisar-aluno").param("nome", token))
                .andExpect(status().isOk())
                .andExpect(model().attribute("ListaDeAlunos", hasSize(1)));
    }

    @Test
    public void pesquisaPorNomeInexistenteDeveTrazerListaVazia() throws Exception {
        gravarAluno("Pedro Henrique");

        mockMvc.perform(post("/pesquisar-aluno").param("nome", "zzz-" + UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("ListaDeAlunos", hasSize(0)));
    }
}
