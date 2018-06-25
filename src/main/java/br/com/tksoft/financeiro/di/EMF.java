package br.com.tksoft.financeiro.di;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EMF {
	private static EntityManagerFactory emf;
	
	/**
	 * Cria um novo entity manager.
	 * 
	 * <p>Este m�todo cria o entity manager factory se ele ainda n�o tiver sido criado.</p>
	 * 
	 * @return O entity manager criado
	 */
	public static EntityManager criarEntityManager() {
		if (emf == null) {
			emf = Persistence.createEntityManagerFactory("FinanceiroPu");
		}
		return emf.createEntityManager();
	}
	
	/**
	 * Fecha o entity manager factory se necess�rio.
	 * 
	 * <p>Este m�todo s� deve ser chamado quando a aplica��o estiver encerrando.</p>
	 */
	public static void fecharEntityManagerFactory() {
		if (emf != null) {
			emf.close();
			emf = null;
		}
	}
}
