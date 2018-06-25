package br.com.tksoft.financeiro.model;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import com.google.inject.Inject;

import br.com.tksoft.financeiro.di.DAO;
import br.com.tksoft.financeiro.entity.Plano;

@DAO
public class PlanoDAO {

	@Inject
	private EntityManager em;

	public List<Plano> getPlanos(Boolean ativos) {
		
		StringBuilder stmt = new StringBuilder();
		
		stmt.append("select plano from Plano plano");
		
		if (ativos != null) {
			
			if (ativos) {
				
				stmt.append(" where plano.dataFim is null");
			} else {
				
				stmt.append(" where not plano.dataFim is null");
			}
		}
		
		stmt.append(" order by plano.nome");

		return em.createQuery(stmt.toString(), Plano.class).getResultList();
	}

	public Plano salvar(Plano plano, Integer usuario) {

		if (plano.getId() == null) {

			plano.setDataInclusao(new Date());
			plano.setUsuarioInclusao(usuario);
		} else {

			plano.setDataAlteracao(new Date());
			plano.setUsuarioAlteracao(usuario);
		}

		return em.merge(plano);
	}

	public Plano getPlanoById(Long idPlano) {

		return em.find(Plano.class, idPlano);
	}

	public Plano excluir(Plano plano, Integer usuario) {
		
		plano.setDataFim(new Date());
		plano.setUsuarioAlteracao(usuario);
		
		return em.merge(plano);
	}
	
	public Long getSituacaoContrato(Long idPlano) {
		
		return (Long) em.createQuery("select count(*) from Contrato contrato where contrato.dataFim is null "
				+ "and contrato.plano.id = :idPlano").setParameter("idPlano", idPlano).getSingleResult();
		
	}
}
