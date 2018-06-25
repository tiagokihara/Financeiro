package br.com.tksoft.financeiro.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.material.application.ToastService;

import com.google.inject.Inject;

import br.com.tksoft.financeiro.di.DI;
import br.com.tksoft.financeiro.entity.Usuario;
import br.com.tksoft.financeiro.model.UsuarioDAO;
import br.com.tksoft.financeiro.util.SessionUtils;

@ManagedBean
@ViewScoped
public class LoginBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private UsuarioDAO usuarioDAO;

	private Usuario usuario;

	@PostConstruct
	public void postConstruct() {

		DI.inject(this);
	}

	public String entrar() {

		try {
			
			if (usuario.getEmail() == null || usuario.getSenha() == null) {
				
				ToastService.getInstance().newToast("Por favor, informe e-mail e senha!", 5000);
				return null;
			}

			usuarioDAO.logar(usuario);
			return "/paginas/index.xhtml?faces-redirect=true";
		} catch (Exception e) {

			ToastService.getInstance().newToast(e.getMessage(), 5000);
		}

		return null;
	}

	public String sair() {

		SessionUtils.removerUsuario();

		return "/login.jsf?faces-redirect=true";
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}
