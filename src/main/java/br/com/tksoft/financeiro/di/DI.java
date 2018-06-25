package br.com.tksoft.financeiro.di;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

/**
 * Classe utilit�ria para inje��o de depend�ncia.
 */
public class DI {

	/**
	 * Nome que identifica o escopo DAO no injector global do Guice.
	 */
	static final String ESCOPO_DAO = "escopoDAO";

	/**
	 * Nome que identifica o injector global do Guice nos atributos do servlet context.
	 */
	static final String GUICE_INJECTOR = "guiceInjector";

	/**
	 * Nome que identifica o servlet context no contexto do scheduler do Quartz.
	 */
	static final String SERVLET_CONTEXT = "servletContext";

	/**
	 * Realiza a inje��o nos campos marcados com @Inject.
	 * 
	 * <p>
	 * Este m�todo deve ser chamado no @PostContruct de managed beans ou no construtor de objetos que usam DAOs.
	 * </p>
	 * 
	 * @param o
	 *            O objeto cujos campos ter�o valores injetados
	 */
	public static void inject(Object o) {
		inject(o, obterServletContext());
	}

	/**
	 * Realiza a inje��o nos campos marcados com @Inject.
	 * 
	 * <p>
	 * Este m�todo deve ser chamado no @PostContruct de managed beans ou no construtor de objetos que usam DAOs.
	 * </p>
	 * 
	 * @param o
	 *            O objeto cujos campos ter�o valores injetados
	 * @param servletContext
	 *            O servlet context que cont�m o injector
	 */
	public static void inject(Object o, ServletContext servletContext) {
		Injector injector = obterInjectorGuice(servletContext);
		SimpleScope escopoDAO = DI.obterEscopo(injector);

		escopoDAO.enter();
		try {
			escopoDAO.seed(Key.get(EntityManager.class), new EntityManagerDummy());
			injector.injectMembers(o);
		} finally {
			escopoDAO.exit();
		}
	}

	/**
	 * Obt�m um objeto atrav�s da inje��o de depend�ncias do Guice.
	 * 
	 * <p>
	 * Este m�todo s� deve ser usado quando n�o for poss�vel usar {@link #inject(Object)} durante a inicializa��o da classe que precisa do objeto injetado (ex: em m�todos est�ticos de classes utilit�rias).
	 * </p>
	 * 
	 * <p>
	 * Se o escopo DAO n�o estiver ativo, um escopo tempor�rio � criado somente durante a execu��o deste m�todo.
	 * </p>
	 * 
	 * @param clazz
	 *            A classe ou interface solicitada
	 * @return O objeto obtido pelo Guice
	 */
	public static <T> T get(Class<T> clazz) {
		return get(clazz, obterServletContext());
	}

	/**
	 * Obt�m um objeto atrav�s da inje��o de depend�ncias do Guice.
	 * 
	 * <p>
	 * Este m�todo s� deve ser usado quando n�o for poss�vel usar {@link #inject(Object)} durante a inicializa��o da classe que precisa do objeto injetado (ex: em m�todos est�ticos de classes utilit�rias).
	 * </p>
	 * 
	 * <p>
	 * Se o escopo DAO n�o estiver ativo, um escopo tempor�rio � criado somente durante a execu��o deste m�todo.
	 * </p>
	 * 
	 * @param clazz
	 *            A classe ou interface solicitada
	 * @param servletContext
	 *            O servlet context que cont�m o injector
	 * @return O objeto obtido pelo Guice
	 */
	public static <T> T get(Class<T> clazz, ServletContext servletContext) {
		Injector injector = obterInjectorGuice(servletContext);
		SimpleScope escopoDAO = DI.obterEscopo(injector);

		boolean escopoAtivo = escopoDAO.isInProgress();
		if (!escopoAtivo) {
			escopoDAO.enter();
		}

		try {
			if (!escopoAtivo) {
				escopoDAO.seed(Key.get(EntityManager.class), new EntityManagerDummy());
			}

			return injector.getInstance(clazz);
		} finally {
			if (!escopoAtivo) {
				escopoDAO.exit();
			}
		}
	}

	/**
	 * Obt�m o escopo DAO.
	 * 
	 * @param injector
	 *            O injetor global do Guice
	 * @return O escopo DAO
	 */
	static SimpleScope obterEscopo(Injector injector) {
		return injector.getInstance(Key.get(SimpleScope.class, Names.named(ESCOPO_DAO)));
	}

	/**
	 * Obt�m o injector Guice a partir do servlet context.
	 *
	 * @return O injector Guice
	 * @throws IllegalStateException
	 *             Caso n�o seja poss�vel obter o injector
	 */
	static Injector obterInjectorGuice() {
		return obterInjectorGuice(obterServletContext());
	}

	/**
	 * Obt�m o injector Guice a partir do servlet context.
	 *
	 * @param servletContext
	 *            O servlet context que cont�m o injector
	 * @return O injector Guice
	 * @throws IllegalStateException
	 *             Caso n�o seja poss�vel obter o injector
	 */
	static Injector obterInjectorGuice(ServletContext servletContext) {
		Injector injector = (Injector) servletContext.getAttribute(GUICE_INJECTOR);
		if (injector == null) {
			throw new IllegalStateException("N�o foi poss�vel encontrar o injector Guice no servlet context");
		}
		return injector;
	}

	/**
	 * Obt�m o servlet context a partir do contexto JSF ou do scheduler Quartz.
	 * 
	 * @return O servlet context
	 * @throws IllegalStateException
	 *             Caso n�o seja poss�vel obter o servlet context
	 */
	public static ServletContext obterServletContext() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();
			if (servletContext != null) {
				return servletContext;
			}
		}

		throw new IllegalStateException("N�o foi poss�vel obter o servlet context");
	}
}
