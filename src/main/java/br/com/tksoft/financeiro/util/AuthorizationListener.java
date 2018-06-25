package br.com.tksoft.financeiro.util;

import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import br.com.tksoft.financeiro.controller.UsuarioLogadoBean;

public class AuthorizationListener implements PhaseListener {
	private static final long serialVersionUID = 1L;

	public void afterPhase(PhaseEvent event) {
		FacesContext facesContext = event.getFacesContext();
		String currentPage = facesContext.getViewRoot().getViewId();

		boolean isLoginPage = currentPage.endsWith("login.xhtml");
		boolean isSearchPage = currentPage.endsWith("carteirinha.xhtml");
		UsuarioLogadoBean usuario = SessionUtils.getUsuario();

		if ((!isLoginPage) && (!isSearchPage) && (usuario == null)) {
			NavigationHandler nh = facesContext.getApplication().getNavigationHandler();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Permissão de acesso negada.", null));
			nh.handleNavigation(facesContext, null, "loginPage");
		}
	}

	public void beforePhase(PhaseEvent event) {
	}

	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}
}
