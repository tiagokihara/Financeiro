package br.com.tksoft.financeiro.model;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.google.inject.Inject;

import br.com.tksoft.financeiro.di.DAO;
import br.com.tksoft.financeiro.entity.ContratoParcela;

@DAO
public class ContasDAO {

	@Inject
	private EntityManager em;
	
	public List<ContratoParcela> parcelasReceberPeriodo(Date dataInicial, Date dataFinal){
		
		TypedQuery<ContratoParcela> query =  em.createQuery(
				"select cp from ContratoParcela cp "
				+ "where cp.vencimento BETWEEN :dataInicial and :dataFinal and cp.valorPagamento is null "
				+ "and cp.contrato.dataFim is null and cp.valor >  0 "
				+ "order by cp.contrato.cliente.nome, cp.vencimento", ContratoParcela.class);

		query.setParameter("dataInicial", dataInicial);
		query.setParameter("dataFinal", dataFinal);

		return query.getResultList();
		
	}

}
