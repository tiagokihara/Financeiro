package br.com.tksoft.financeiro.util;

import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import br.com.tksoft.financeiro.controller.UsuarioLogadoBean;
import br.com.tksoft.financeiro.entity.Usuario;

public class SessionUtils {

	private static final String KEY_USUARIO = "usuarioLogadoBean";

	public static void carregarUsuario(Usuario usuario) {

		UsuarioLogadoBean usuarioLogadoBean = new UsuarioLogadoBean();

		usuarioLogadoBean.setId(usuario.getId());
		usuarioLogadoBean.setNome(usuario.getNome());
		usuarioLogadoBean.setEmail(usuario.getEmail());
		usuarioLogadoBean.setNivel(usuario.getNivel());

		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		sessionMap.put(KEY_USUARIO, usuarioLogadoBean);
	}

	public static void removerUsuario() {

		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		sessionMap.remove(KEY_USUARIO);
	}

	public static UsuarioLogadoBean getUsuario() {

		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		return (UsuarioLogadoBean) sessionMap.get(KEY_USUARIO);
	}

}
