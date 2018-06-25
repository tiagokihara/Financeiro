package br.com.tksoft.financeiro.di;

import javax.persistence.EntityManager;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

/**
 * M�dulo para configura��o do Guice para a inje��o de objetos DAO e entity managers.
 * 
 * <p>As classes DAO s�o marcadas com um escopo Guice (annotation @DAO) para os seguintes
 * fins:</p>
 * 
 * <ul>
 *   <li>Interceptar as chamadas aos m�todos para cria��o e gerenciamento do EntityManager (e transa��es)</li>
 *   <li>Reaproveitar DAO e EntityManager j� criados e ativos enquanto durar o escopo</li>
 * </ul>
 * 
 * <p>O escopo @DAO dura enquanto o primeiro m�todo do DAO estiver executando. M�todos chamados por esse 
 * primeiro m�todo n�o criam novos escopos; em vez disso, esses m�todos continuam dentro do escopo inicial. Ap�s 
 * o retorno dessa primeira chamada, uma nova chamada ao DAO criar� um novo escopo.</p>
 */
public class DAOGuiceModule extends AbstractModule {
	/**
	 * Representa o escopo DAO.
	 */
	private SimpleScope escopoDAO;

	/**
	 * Configura a intercepta��o e o escopo de classes anotadas com @DAO.
	 */
	@Override
	protected void configure() {
		bindInterceptor(Matchers.annotatedWith(DAO.class), Matchers.any(), new DAOMethodInterceptor());
		
		escopoDAO = new SimpleScope();
		bind(SimpleScope.class).annotatedWith(Names.named(DI.ESCOPO_DAO)).toInstance(escopoDAO);
		bindScope(DAO.class, escopoDAO);
	}
	
	/**
	 * Obt�m o entity manager registrado no escopo DAO.
	 */
	@Provides
	EntityManager criarEntityManager() {
		return (EntityManager) escopoDAO.getScopedObject(Key.get(EntityManager.class));
	}
}
