package br.com.tksoft.financeiro.di;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

/**
 * Escopo controlado manualmente e com registro de valores.
 * 
 * <p>Quando um escopo est� ativo, objetos associados ao escopo s�o criados somente
 * uma vez; quando forem requisitados novamente, os objetos j� criados � que ser�o
 * retornados. Nesta classe tamb�m � poss�vel pr�-definir, ap�s a ativa��o do escopo,
 * os objetos associados ao escopo que devem ser retornados.</p>
 * 
 * <p>Mesmo que exista somente uma inst�ncia desta classe para a aplica��o inteira,
 * cada thread de execu��o tem o pr�prio escopo independente.</p>
 */
public class SimpleScope implements Scope {
	/**
	 * Os valores do escopo da thread atual.
	 */
	private final ThreadLocal<Map<Key<?>, Object>> values = new ThreadLocal<Map<Key<?>, Object>>();

	/**
	 * Verifica se o escopo est� em andamento.
	 * 
	 * @return true se o m�todo enter() j� foi chamado para este escopo nesta thread
	 */
	public boolean isInProgress() {
		return values.get() != null;
	}
	
	/**
	 * Inicia este escopo nesta thread.
	 * 
	 * @throws IllegalStateException Caso este escopo j� esteja iniciado nesta thread
	 */
	public void enter() {
		if (isInProgress()) {
			throw new IllegalStateException("A scoping block is already in progress");
		}
		values.set(new HashMap<Key<?>, Object>());
	}

	/**
	 * Finaliza este escopo nesta thread.
	 * 
	 * @throws IllegalStateException Caso este escopo n�o esteja iniciado nesta thread
	 */
	public void exit() {
		if (values.get() == null) {
			throw new IllegalStateException("No scoping block in progress");
		}
		values.remove();
	}

	/**
	 * Define um objeto para uma chave de inje��o Guice neste escopo.
	 * 
	 * @param key A chave Guice a qual ser� associado o objeto
	 * @param value O objeto a ser associado
	 */
	public <T> void seed(Key<T> key, T value) {
		Map<Key<?>, Object> scopedObjects = getScopedObjectMap(key);
		if (scopedObjects.containsKey(key)) {
			throw new IllegalStateException("A value for the key " + key + " was already seeded in this scope");
		}
		scopedObjects.put(key, value);
	}

	/**
	 * Obt�m o objeto definido neste escopo a partir de uma chave de inje��o Guice.
	 * 
	 * @param key A chave Guice que foi associado o objeto
	 * @return O objeto associado ou null caso o objeto n�o tenha sido definido
	 */
	public <T> Object getScopedObject(Key<T> key) {
		return getScopedObjectMap(key).get(key);
	}
	
	/**
	 * Obt�m o mapa de objetos deste escopo.
	 * 
	 * @param key A chave Guice que foi associado o objeto
	 * @return O mapa de valores deste escopo
	 */
	private <T> Map<Key<?>, Object> getScopedObjectMap(Key<T> key) {
		Map<Key<?>, Object> scopedObjects = values.get();
		return scopedObjects;
	}

	/**
	 * Decora um provider Guice para retornar objetos j� definidos neste escopo.
	 */
	@Override
	public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
		return new Provider<T>() {
			@Override
			public T get() {
				Map<Key<?>, Object> scopedObjects = getScopedObjectMap(key);

				@SuppressWarnings("unchecked")
				T current = (T) scopedObjects.get(key);
				if (current == null && !scopedObjects.containsKey(key)) {
					current = unscoped.get();
					scopedObjects.put(key, current);
				}
				return current;
			}
		};
	}
}
