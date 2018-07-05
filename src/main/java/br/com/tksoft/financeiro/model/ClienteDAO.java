package br.com.tksoft.financeiro.model;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.google.inject.Inject;

import br.com.tksoft.financeiro.di.DAO;
import br.com.tksoft.financeiro.entity.Cliente;
import br.com.tksoft.financeiro.entity.Dependente;
import br.com.tksoft.financeiro.entity.EstadoCivil;
import br.com.tksoft.financeiro.entity.Parentesco;

@DAO
public class ClienteDAO {

	@Inject
	private EntityManager em;

	public List<Cliente> getClientes(Boolean ativos) {

		StringBuilder stmt = new StringBuilder();

		stmt.append("select cliente from Cliente cliente");

		if (ativos != null) {

			if (ativos) {

				stmt.append(" where cliente.dataFim is null");
			} else {

				stmt.append(" where not cliente.dataFim is null");
			}
		}

		stmt.append(" order by cliente.nome");

		TypedQuery<Cliente> query = em.createQuery(stmt.toString(), Cliente.class);

		return query.getResultList();
	}

	public List<Parentesco> getParentescos() {

		return em.createQuery("select parentesco from Parentesco parentesco order by parentesco.descricao", Parentesco.class).getResultList();
	}

	public List<Dependente> getDependentesByCliente(Long idCliente, Boolean ativos) {

		StringBuilder stmt = new StringBuilder();

		stmt.append("select dependente from Dependente dependente where dependente.cliente.id = :idCliente");

		if (ativos != null) {

			if (ativos) {

				stmt.append(" and dependente.dataFim is null");
			} else {

				stmt.append(" and not dependente.dataFim is null");
			}
		}

		stmt.append(" order by dependente.nome");

		TypedQuery<Dependente> query = em.createQuery(stmt.toString(), Dependente.class);

		query.setParameter("idCliente", idCliente);

		return query.getResultList();

		// TypedQuery<Dependente> query = em.createQuery("select dependente from
		// Dependente dependente where dependente.cliente.id = :idCliente order
		// by dependente.nome",
		// Dependente.class);

		// query.setParameter("idCliente", idCliente);

		// return query.getResultList();

	}

	public void salvar(Cliente cliente, Integer usuario) {

		if (cliente.getId() == null) {

			cliente.setUsuarioInclusao(usuario);
			cliente.setDataInclusao(new Date());
		} else {

			cliente.setUsuarioAlteracao(usuario);
			cliente.setDataAlteracao(new Date());
		}

		if (cliente.getEstadoCivil() != null && cliente.getEstadoCivil().getId() != null)
			cliente.setEstadoCivil(getEstadoCivilById(cliente.getEstadoCivil().getId()));
		else
			cliente.setEstadoCivil(null);

		if (cliente.getEmpresaResponsavel() != null && cliente.getEmpresaResponsavel().getId() != null)
			cliente.setEmpresaResponsavel(getClienteById(cliente.getEmpresaResponsavel().getId()));
		else
			cliente.setEmpresaResponsavel(null);

		em.merge(cliente);
	}

	public List<EstadoCivil> getEstadosCivis() {

		return em.createQuery("select estadoCivil from EstadoCivil estadoCivil order by estadoCivil.descricao", EstadoCivil.class).getResultList();
	}

	public EstadoCivil getEstadoCivilById(Integer idEstadoCivil) {

		return em.find(EstadoCivil.class, idEstadoCivil);
	}

	public List<Cliente> getClientesByFiltro(Cliente cliente) {

		StringBuilder stmt = new StringBuilder();
		Boolean condicao = false;

		stmt.append("select cliente from Cliente cliente");

		if (cliente.getNome() != null) {

			stmt.append((condicao ? " and" : " where") + " lower(cliente.nome) like :nome");
			condicao = true;
		}

		if (cliente.getTipo() != null) {

			stmt.append((condicao ? " and" : " where") + " cliente.tipo = :tipo");
			condicao = true;
		}

		stmt.append(" order by cliente.nome");

		TypedQuery<Cliente> query = em.createQuery(stmt.toString(), Cliente.class);

		if (cliente.getNome() != null) {

			query.setParameter("nome", "%" + cliente.getNome().toLowerCase() + "%");
		}

		if (cliente.getTipo() != null) {

			query.setParameter("tipo", cliente.getTipo());
		}

		return query.getResultList();

	}

	public Cliente getClienteById(Long idCliente) {

		return em.find(Cliente.class, idCliente);
	}

	public Dependente getDependenteById(Long idDependente) {

		return em.find(Dependente.class, idDependente);

	}

	public void salvar(Dependente dependente, Cliente cliente, Integer usuario) {

		if (dependente.getId() == null) {

			dependente.setUsuarioInclusao(usuario);
			dependente.setDataInclusao(new Date());
		} else {

			dependente.setUsuarioAlteracao(usuario);
			dependente.setDataAlteracao(new Date());
		}

		dependente.setParentesco(getParentescoById(dependente.getParentesco().getId()));
		dependente.setCliente(getClienteById(cliente.getId()));

		em.merge(dependente);

	}

	public void excluir(Cliente cliente, Integer usuario) throws Exception {

		String stmt = "update Cliente set dataFim = :dataFim, usuarioAlteracao = :usuarioAlteracao where id = :id";

		Query query = em.createQuery(stmt);

		query.setParameter("dataFim", new Date());
		query.setParameter("usuarioAlteracao", usuario);
		query.setParameter("id", cliente.getId());

		Integer registrosAtualizados = query.executeUpdate();

		if (registrosAtualizados == 0)
			throw new Exception("Erro ao excluir cliente.");
	}

	public void excluirDependente(Dependente dependente, Integer usuario) {

		dependente.setDataFim(new Date());
		dependente.setUsuarioAlteracao(usuario);

		em.merge(dependente);
	}

	public Parentesco getParentescoById(Integer idParentesco) {

		return em.find(Parentesco.class, idParentesco);
	}

	public Long getSituacaoContrato(Long idCliente) {

		return (Long) em.createQuery("select count(*) from Contrato contrato where contrato.dataFim is null " + "and contrato.cliente.id = :idCliente")
				.setParameter("idCliente", idCliente).getSingleResult();

	}

	public List<Cliente> getEmpresas() {

		return em.createQuery("select cliente from Cliente cliente where cliente.dataFim is null and cliente.tipo like 'J' order by cliente.nome", Cliente.class)
				.getResultList();
	}
}
