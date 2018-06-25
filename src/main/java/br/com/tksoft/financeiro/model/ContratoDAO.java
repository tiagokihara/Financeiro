package br.com.tksoft.financeiro.model;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.google.inject.Inject;

import br.com.tksoft.financeiro.di.DAO;
import br.com.tksoft.financeiro.entity.Boleto;
import br.com.tksoft.financeiro.entity.Cliente;
import br.com.tksoft.financeiro.entity.Conta;
import br.com.tksoft.financeiro.entity.Contrato;
import br.com.tksoft.financeiro.entity.ContratoParcela;
import br.com.tksoft.financeiro.entity.Plano;
import br.com.tksoft.financeiro.enums.Status;

@DAO
public class ContratoDAO {

	@Inject
	private EntityManager em;

	private final Integer BOLETO = 1;

	public void salvar(Contrato contrato, Cliente cliente, Integer usuario) {

		Date dataOperacao = new Date();

		contrato.setDataInclusao(dataOperacao);
		contrato.setUsuarioInclusao(usuario);
		contrato.setPlano(em.find(Plano.class, contrato.getPlano().getId()));

		if (contrato.getTipoPagamento().equals(BOLETO)) {

			contrato.setConta(em.find(Conta.class, contrato.getConta().getId()));
		} else {
			contrato.setConta(null);
		}

		BigDecimal valor = contrato.getValorParcela();

		contrato.setCliente(cliente);

		contrato = em.merge(contrato);

		ContratoParcela parcela;
		Calendar vencimento = Calendar.getInstance();
		vencimento.setTime(contrato.getPrimeiraParcela());

		if (valor.compareTo(BigDecimal.ZERO) > 0) {

			for (int i = 0; i < contrato.getQuantidadeParcelas(); i++) {

				parcela = new ContratoParcela();

				parcela.setContrato(contrato);
				parcela.setDataInclusao(dataOperacao);
				parcela.setUsuarioInclusao(usuario);
				parcela.setVencimento(vencimento.getTime());
				parcela.setValor(valor);
				parcela.setNumeroParcela(i + 1);

				parcela = em.merge(parcela);

				if (contrato.getTipoPagamento().equals(BOLETO)) {

					Boleto boleto = new Boleto();

					boleto.setParcela(parcela);
					boleto.setStatus(Status.PENDENTE.getCodigo());

					em.merge(boleto);
				}

				vencimento.add(Calendar.MONTH, 1);
			}
		}
	}

	public List<Contrato> getContratos(int first, int pageSize, Long idCliente) {

		StringBuilder stmt = new StringBuilder();

		stmt.append("select contrato from Contrato contrato where contrato.dataFim is null");

		if (idCliente != null) {

			stmt.append(" and contrato.cliente.id = :idCliente");
		}

		stmt.append(" order by contrato.dataAssinatura desc");

		TypedQuery<Contrato> query = em.createQuery(stmt.toString(), Contrato.class);

		if (idCliente != null) {

			query.setParameter("idCliente", idCliente);
		}

		query.setFirstResult(first);
		query.setMaxResults(pageSize);

		return query.getResultList();
	}

	public int getContratosRowCount(Long idCliente) {

		try {

			StringBuilder stmt = new StringBuilder();

			stmt.append("select count(contrato) from Contrato contrato where contrato.dataFim is null");

			if (idCliente != null) {

				stmt.append(" and contrato.cliente.id = :idCliente");
			}

			Query query = em.createQuery(stmt.toString());

			if (idCliente != null) {

				query.setParameter("idCliente", idCliente);
			}

			return ((Long) query.getSingleResult()).intValue();
		} catch (Exception e) {

			e.printStackTrace();
			return 0;
		}
	}

	public Contrato getContrato(Long idContrato) {

		return em.find(Contrato.class, idContrato);
	}

	public List<ContratoParcela> getParcelasVencidasByContrato(Long idContrato) {

		TypedQuery<ContratoParcela> query = em.createQuery(
				"select parcela from ContratoParcela parcela where parcela.contrato.id =:idContrato and parcela.vencimento < :dataAtual", ContratoParcela.class);
		query.setParameter("idContrato", idContrato);
		query.setParameter("dataAtual", new Date());
		return query.getResultList();

	}

	public List<ContratoParcela> getParcelasByContrato(Long idContrato) {

		TypedQuery<ContratoParcela> query = em.createQuery(
				"select contratoparcela from ContratoParcela contratoparcela where contratoparcela.contrato.id = :idContrato order by contratoparcela.id",
				ContratoParcela.class);

		query.setParameter("idContrato", idContrato);

		List<ContratoParcela> parcelas = query.getResultList();

		if (parcelas != null && parcelas.size() > 0) {

			if (parcelas.get(0).getContrato().getTipoPagamento().equals(BOLETO)) {

				for (ContratoParcela parc : parcelas) {

					parc.setBoleto(getBoletoByIdParcela(parc.getId()));
				}
			}
		}

		return parcelas;
	}

	public Boleto getBoletoByIdParcela(Long idParcela) {

		return (Boleto) em.createQuery("select boleto from Boleto boleto where boleto.parcela.id = :idParcela").setParameter("idParcela", idParcela).getSingleResult();
	}

	public List<Boleto> getBoletosByContrato(Long idContrato) {

		TypedQuery<Boleto> query = em.createQuery("select boleto from Boleto boleto where boleto.parcela.contrato.id = :idContrato order by boleto.parcela.vencimento",
				Boleto.class);

		query.setParameter("idContrato", idContrato);

		return query.getResultList();
	}

	public Date getUltimaParcelaContrato(Long idContrato) {

		Query query = em.createQuery("select max(contratoparcela.vencimento) from ContratoParcela contratoparcela where contratoparcela.contrato.id = :idContrato");

		query.setParameter("idContrato", idContrato);

		return (Date) query.getSingleResult();
	}

	public Boleto getBoletoById(Long idBoleto) {

		return em.find(Boleto.class, idBoleto);
	}

	public ContratoParcela getParcelaById(Long idParcela) {

		return em.find(ContratoParcela.class, idParcela);
	}

	public void baixarParcela(ContratoParcela parcela, Integer usuario) {

		parcela.setUsuarioAlteracao(usuario);
		parcela.setDataAlteracao(new Date());

		em.merge(parcela);

	}

	public void estornarParcela(ContratoParcela parcela, Integer usuario) {

		parcela.setUsuarioAlteracao(usuario);
		parcela.setDataAlteracao(new Date());
		parcela.setPagamento(null);
		parcela.setValorPagamento(null);

		em.merge(parcela);

	}

	public List<Boleto> getBoletosByStatusConta(Integer status, Long idConta) {

		TypedQuery<Boleto> query = em.createQuery(
				"select boleto from Boleto boleto where boleto.status = :status and boleto.parcela.contrato.conta.id = :idConta and boleto.parcela.contrato.dataFim is null order by boleto.id",
				Boleto.class);
		// "select boleto from Boleto boleto where boleto.status = :status and
		// boleto.parcela.contrato.conta.id = :idConta order by boleto.id",
		// Boleto.class);

		query.setParameter("status", status);
		query.setParameter("idConta", idConta);

		return query.getResultList();
	}

	public void excluir(Contrato contrato, Integer usuario) {

		contrato.setDataFim(new Date());
		contrato.setUsuarioAlteracao(usuario);

		em.merge(contrato);
	}

	public Long getSituacaoRemessa(Long idContrato) {

		return (Long) em.createQuery("select count(*) from RemessaBoleto rb" + " inner join rb.boleto b" + " inner join b.parcela cp" + " inner join cp.contrato c"
				+ " where c.id = :idContrato").setParameter("idContrato", idContrato).getSingleResult();
	}

	public Contrato getUltimoContratoByCliente(Long idCliente, String cpf) {

		try {

			StringBuilder stmt = new StringBuilder();

			stmt.append("select contrato from Contrato contrato");
			stmt.append(" where contrato.id = (select max(contr.id) from Contrato contr where 1 = 1");

			if (idCliente != null)
				stmt.append(" and contr.cliente.id = :idCliente");
			
			if (cpf != null)
				stmt.append(" and contr.cliente.cpf = :cpf");

			stmt.append(") and contrato.dataFim is null");

			TypedQuery<Contrato> query = em.createQuery(stmt.toString(), Contrato.class);

			if (idCliente != null)
				query.setParameter("idCliente", idCliente);
			
			if (cpf != null)
				query.setParameter("cpf", cpf);

			return query.getSingleResult();
		} catch (Exception e) {

			return null;
		}
	}
}
