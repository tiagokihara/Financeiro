package br.com.tksoft.financeiro.di;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Listener para inicializar o Guice quando a aplica��o � iniciada e 
 * finalizar o entity manager factory quando a aplica��o � terminada.
 */
public class GuiceContextListener implements ServletContextListener {
	@Override
	public void contextDestroyed(ServletContextEvent context) {
		EMF.fecharEntityManagerFactory();
	}

	@Override
	public void contextInitialized(ServletContextEvent context) {
		Injector injector = Guice.createInjector(new DAOGuiceModule());
		context.getServletContext().setAttribute(DI.GUICE_INJECTOR, injector);
	}
}
