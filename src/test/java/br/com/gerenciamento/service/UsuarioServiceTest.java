package br.com.gerenciamento.service;

import br.com.gerenciamento.exception.EmailExistsException;
import br.com.gerenciamento.model.Usuario;
import br.com.gerenciamento.util.Util;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UsuarioServiceTest {

    @Autowired
    private ServiceUsuario serviceUsuario;

    private Usuario criarUsuario(String login, String senha) {
        Usuario usuario = new Usuario();
        usuario.setUser(login);
        usuario.setEmail(login + "@mail.com");
        usuario.setSenha(senha);
        return usuario;
    }

    private String sufixo() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    public void deveCadastrarUsuarioComSucesso() throws Exception {
        Usuario usuario = criarUsuario("cadastro_" + sufixo(), "senha123");

        serviceUsuario.salvarUsuario(usuario);

        Assert.assertNotNull(usuario.getId());
    }

    @Test
    public void naoDevePermitirCadastroDeEmailDuplicado() throws Exception {
        String email = "duplicado" + sufixo() + "@mail.com";

        Usuario primeiro = new Usuario();
        primeiro.setUser("user_" + sufixo());
        primeiro.setEmail(email);
        primeiro.setSenha("111");
        serviceUsuario.salvarUsuario(primeiro);

        Usuario segundo = new Usuario();
        segundo.setUser("user_" + sufixo());
        segundo.setEmail(email);
        segundo.setSenha("222");

        Assert.assertThrows(EmailExistsException.class,
                () -> serviceUsuario.salvarUsuario(segundo));
    }

    @Test
    public void deveAutenticarUsuarioPreviamenteCadastrado() throws Exception {
        String login = "login_" + sufixo();
        serviceUsuario.salvarUsuario(criarUsuario(login, "123"));

        Usuario autenticado = serviceUsuario.loginUser(login, Util.md5("123"));

        Assert.assertNotNull(autenticado);
    }

    @Test
    public void naoDeveAutenticarUsuarioInexistente() {
        Assert.assertNull(serviceUsuario.loginUser("fantasma_" + sufixo(), "123"));
    }
}
