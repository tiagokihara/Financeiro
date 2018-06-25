package br.com.tksoft.financeiro.model;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import com.google.inject.Inject;

import br.com.tksoft.financeiro.di.DAO;
import br.com.tksoft.financeiro.entity.Boleto;
import br.com.tksoft.financeiro.entity.Conta;
import br.com.tksoft.financeiro.entity.Remessa;
import br.com.tksoft.financeiro.entity.RemessaBoleto;
import br.com.tksoft.financeiro.enums.Status;

@DAO
public class RemessaDAO {

	@Inject
	private EntityManager em;

	public List<Remessa> getRemessas() {

		return em.createQuery("select remessa from Remessa remessa order by remessa.id desc", Remessa.class).getResultList();
	}

	public Remessa salvar(List<Boleto> boletos, Long conta, Integer usuario) {

		Date dataOperacao = new Date();

		Remessa remessa = new Remessa();

		Remessa ultimaRemessa = getUltimaRemessa();

		Conta contaBancaria = new Conta();
		contaBancaria.setId(conta);
		
		remessa.setConta(contaBancaria);
		remessa.setData(dataOperacao);
		remessa.setUsuario(usuario);
		int sequencia = 1;

		if (ultimaRemessa != null && ultimaRemessa.getSequencia().intValue() < 99) {
			sequencia = ultimaRemessa.getSequencia().intValue() + 1;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataOperacao);

		remessa.setNome(String.format("CB%02d%02d%02d.REM", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, sequencia));
		remessa.setSequencia(sequencia);

		remessa = em.merge(remessa);

		for (Boleto boleto : boletos) {

			RemessaBoleto remessaBoleto = new RemessaBoleto();

			remessaBoleto.setBoleto(boleto);
			remessaBoleto.setRemessa(remessa);

			em.merge(remessaBoleto);

			boleto.setStatus(Status.ENVIADO.getCodigo());

			em.merge(boleto);
		}

		return remessa;
	}

	public Remessa getUltimaRemessa() {

		return (Remessa) em.createQuery("select max(remessa) from Remessa remessa").getSingleResult();
	}

	public List<Boleto> getBoletosByRemessa(Long idRemessa) {

		return em.createQuery("select remBol.boleto from RemessaBoleto remBol where remBol.remessa.id = :idRemessa", Boleto.class).setParameter("idRemessa", idRemessa)
				.getResultList();
	}

}
