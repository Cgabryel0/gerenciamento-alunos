package br.com.gerenciamento.controller;

import br.com.gerenciamento.model.Usuario;
import br.com.gerenciamento.repository.UsuarioRepository;
import br.com.gerenciamento.util.Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private String sufixo() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    public void loginComCredenciaisInvalidasDeveApresentarTelaDeCadastro() throws Exception {
        mockMvc.perform(post("/login")
                        .param("user", "ghost_" + sufixo())
                        .param("senha", "000"))
                .andExpect(status().isOk())
                .andExpect(view().name("login/cadastro"));
    }

    @Test
    public void loginComCredenciaisValidasDeveAbrirAHome() throws Exception {
        String login = "acesso_" + sufixo();
        Usuario usuario = new Usuario();
        usuario.setEmail(login + "@mail.com");
        usuario.setUser(login);
        usuario.setSenha(Util.md5("123"));
        usuarioRepository.save(usuario);

        mockMvc.perform(post("/login")
                        .param("user", login)
                        .param("senha", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name("home/index"));
    }

    @Test
    public void cadastroDeUsuarioDeveRedirecionarParaRaiz() throws Exception {
        String marca = sufixo();

        mockMvc.perform(post("/salvarUsuario")
                        .param("user", "novo_" + marca)
                        .param("email", "novo" + marca + "@mail.com")
                        .param("senha", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void logoutDeveRetornarParaTelaDeLogin() throws Exception {
        mockMvc.perform(post("/logout"))
                .andExpect(status().isOk())
                .andExpect(view().name("login/login"));
    }
}
