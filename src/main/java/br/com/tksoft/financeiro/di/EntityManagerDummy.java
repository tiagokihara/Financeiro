package br.com.tksoft.financeiro.di;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;

/**
 * Implementa��o da interface EntityManager para ser injetada nos DAOs durante
 * a sua cria��o.
 * 
 * <p>Quando o DAO � criado pelo m�todo {@link DI#inject()}, um EntityManager precisa
 * ser criado porque o Guice n�o aceita a inje��o do valor null. Contudo, durante
 * essa primeira inje��o o escopo @DAO ainda n�o est� ativo de verdade (porque
 * nenhum m�todo do DAO est� sendo chamado). Assim, na primeira inje��o uma
 * inst�ncia desta classe � criada e injetada no DAO.</p>
 * 
 * <p>Assim que o primeiro m�todo do DAO � chamado, o seu campo EntityManager e
 * os campos EntityManager dos DAOs referenciados s�o injetados com o EntityManager
 * real criado e gerenciado pelo {@link DAOMethodInterceptor}.</p>
 * 
 * <p>Todos os m�todos desta classe lan�am {@link UnsupportedOperationException} quando 
 * chamados.</p>
 */
public class EntityManagerDummy implements EntityManager {
	private static final String MENSAGEM_ERRO = "Este m�todo pertence a uma classe sem a marca��o @DAO ou a um objeto que n�o foi instanciado usando @Inject";

	@Override
	public void clear() {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public boolean contains(Object entity) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public Query createNamedQuery(String name) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public <T> TypedQuery<T> createNamedQuery(String arg0, Class<T> arg1) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public Query createNativeQuery(String sqlString) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Query createNativeQuery(String sqlString, Class resultClass) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public Query createNativeQuery(String sqlString, String resultSetMapping) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public Query createQuery(String qlString) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> arg0) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public <T> TypedQuery<T> createQuery(String arg0, Class<T> arg1) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public void detach(Object arg0) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public <T> T find(Class<T> arg0, Object arg1, Map<String, Object> arg2) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public <T> T find(Class<T> arg0, Object arg1, LockModeType arg2) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public <T> T find(Class<T> arg0, Object arg1, LockModeType arg2, Map<String, Object> arg3) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public void flush() {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public CriteriaBuilder getCriteriaBuilder() {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public Object getDelegate() {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public EntityManagerFactory getEntityManagerFactory() {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public FlushModeType getFlushMode() {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public LockModeType getLockMode(Object arg0) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public Metamodel getMetamodel() {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public Map<String, Object> getProperties() {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public EntityTransaction getTransaction() {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public boolean isOpen() {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public void joinTransaction() {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public void lock(Object entity, LockModeType lockMode) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public void lock(Object arg0, LockModeType arg1, Map<String, Object> arg2) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public <T> T merge(T entity) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public void persist(Object entity) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public void refresh(Object entity) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public void refresh(Object arg0, Map<String, Object> arg1) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public void refresh(Object arg0, LockModeType arg1) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public void refresh(Object arg0, LockModeType arg1, Map<String, Object> arg2) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public void remove(Object entity) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public void setFlushMode(FlushModeType flushMode) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public void setProperty(String arg0, Object arg1) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public <T> T unwrap(Class<T> arg0) {
		throw new UnsupportedOperationException(MENSAGEM_ERRO);
	}

	@Override
	public Query createQuery(CriteriaUpdate updateQuery) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query createQuery(CriteriaDelete deleteQuery) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isJoinedToTransaction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityGraph<?> createEntityGraph(String graphName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityGraph<?> getEntityGraph(String graphName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}
}
