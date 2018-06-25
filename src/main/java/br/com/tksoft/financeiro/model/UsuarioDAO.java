package br.com.tksoft.financeiro.model;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.google.inject.Inject;

import br.com.tksoft.financeiro.di.DAO;
import br.com.tksoft.financeiro.entity.Usuario;
import br.com.tksoft.financeiro.util.Criptografia;
import br.com.tksoft.financeiro.util.SessionUtils;

@DAO
public class UsuarioDAO {

	@Inject
	private EntityManager em;

	public List<Usuario> getUsuarios() {

		return em.createQuery("select usuario from Usuario usuario order by usuario.nome", Usuario.class).getResultList();
	}

	public Usuario salvar(Usuario usuario) {

		return em.merge(usuario);
	}

	public void logar(Usuario usuario) throws Exception {

		Usuario usuarioSalvo = getUsuarioPorEmail(usuario.getEmail());

		try {

			if (Criptografia.compararSenha(usuarioSalvo.getSenha(), usuario.getSenha())) {

				SessionUtils.carregarUsuario(usuarioSalvo);
			} else {

				throw new Exception("Senha incorreta.");
			}
		} catch (UnsupportedEncodingException e) {

			throw new Exception("Senha incorreta.");
		} catch (NoSuchAlgorithmException e) {

			throw new Exception("Senha incorreta.");
		}
	}

	public Usuario getUsuarioPorEmail(String email) throws Exception {

		String stmt = "select usuario from Usuario usuario where lower(usuario.email) like :email";

		Query query = em.createQuery(stmt);

		query.setParameter("email", email.toLowerCase());

		try {

			return (Usuario) query.getSingleResult();
		} catch (NoResultException e) {

			throw new Exception("E-mail não encontrado.");
		}
	}
}
