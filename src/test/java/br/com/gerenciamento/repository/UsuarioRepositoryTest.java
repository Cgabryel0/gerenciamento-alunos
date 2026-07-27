package br.com.gerenciamento.repository;

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
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario novoUsuario(String login, String email, String senha) {
        Usuario usuario = new Usuario();
        usuario.setUser(login);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        return usuario;
    }

    private String sufixo() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    public void deveArmazenarUsuarioComIdGerado() {
        String marca = sufixo();
        Usuario salvo = usuarioRepository.save(
                novoUsuario("save_" + marca, "save" + marca + "@mail.com", "abc"));
        Assert.assertNotNull(salvo.getId());
    }

    @Test
    public void deveRecuperarUsuarioPeloEmail() {
        String email = "email" + sufixo() + "@mail.com";
        usuarioRepository.save(novoUsuario("byemail_" + sufixo(), email, "abc"));

        Assert.assertNotNull(usuarioRepository.findByEmail(email));
    }

    @Test
    public void deveEncontrarLoginComUsuarioESenhaCorretos() throws Exception {
        String login = "valido_" + sufixo();
        String senhaHash = Util.md5("minhaSenha");
        usuarioRepository.save(novoUsuario(login, login + "@mail.com", senhaHash));

        Usuario retorno = usuarioRepository.buscarLogin(login, senhaHash);
        Assert.assertNotNull(retorno);
    }

    @Test
    public void naoDeveEncontrarLoginComDadosInexistentes() {
        Assert.assertNull(usuarioRepository.buscarLogin("nao_existe", "invalido"));
    }
}
