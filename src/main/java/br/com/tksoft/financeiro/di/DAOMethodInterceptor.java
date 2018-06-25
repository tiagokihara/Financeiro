package br.com.tksoft.financeiro.di;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * Classe para interceptar chamadas a m�todos de classes anotadas com @DAO.
 * 
 * <p>
 * A primeira chamada a um m�todo inicia o escopo DAO, criando um entity manager e iniciando uma transa��o. Enquanto esse escopo estiver ativo, outras chamadas a m�todos n�o ser�o modificadas. Quando o m�todo que iniciou o escopo retornar,
 * a transa��o � finalizada (com commit se n�o houver erro; com rollback se uma exce��o tiver sido lan�ada) e o entity manager fechado.
 * </p>
 */
public class DAOMethodInterceptor implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		// Ignora chamadas ao m�todo finalize() que � chamado
		// pela JVM quando o objeto est� sendo destru�do
		if (invocation.getMethod().getName().equals("finalize") && invocation.getMethod().getParameterTypes().length == 0) {
			return invocation.proceed();
		}

		Injector injector = DI.obterInjectorGuice();
		SimpleScope escopoDAO = DI.obterEscopo(injector);

		if (escopoDAO.isInProgress()) {
			// Se o escopo j� est� ativo, todos os objetos j� foram injetados
			return invocation.proceed();
		} else {
			// Inicia o escopo
			escopoDAO.enter();

			// Cria o entity manager e inicia a transa��o
			EntityManager em = EMF.criarEntityManager();
			EntityTransaction tx = em.getTransaction();
			tx.begin();

			try {
				// Define o entity manager que ser� usado neste escopo
				escopoDAO.seed(Key.get(EntityManager.class), em);

				// Injeta os objetos que forem necess�rios (inclusive o entity manager
				// criado acima)
				injector.injectMembers(invocation.getThis());

				// Chama o m�todo original
				Object retorno = invocation.proceed();

				// Finaliza a transa��o
				tx.commit();

				return retorno;
			} catch (Throwable t) {
				// Cancela a transa��o
				if (tx.isActive()) {
					tx.rollback();
				}

				throw t;
			} finally {
				// Fecha o entity manager e finaliza o escopo
				em.close();
				escopoDAO.exit();
			}
		}
	}
}
