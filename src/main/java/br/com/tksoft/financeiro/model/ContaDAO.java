package br.com.tksoft.financeiro.model;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import com.google.inject.Inject;

import br.com.tksoft.financeiro.di.DAO;
import br.com.tksoft.financeiro.entity.Conta;

@DAO
public class ContaDAO {

	@Inject
	private EntityManager em;

	public List<Conta> getContas() {

		return em.createQuery("select conta from Conta conta order by conta.nome", Conta.class).getResultList();
	}

	public Conta salvar(Conta conta, Integer usuario) {

		if (conta.getId() == null) {

			conta.setDataInclusao(new Date());
			conta.setUsuarioInclusao(usuario);
		} else {

			conta.setDataAlteracao(new Date());
			conta.setUsuarioAlteracao(usuario);
		}

		return em.merge(conta);
	}

	public Conta getConta(Long idConta) {

		return em.find(Conta.class, idConta);
	}
}
