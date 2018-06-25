package br.com.tksoft.financeiro.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.material.application.ToastService;

import com.google.inject.Inject;

import br.com.tksoft.financeiro.di.DI;
import br.com.tksoft.financeiro.entity.Boleto;
import br.com.tksoft.financeiro.entity.ContratoParcela;
import br.com.tksoft.financeiro.model.ContratoDAO;
import br.com.tksoft.financeiro.util.SessionUtils;

@ManagedBean
@ViewScoped
public class BaixaBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private ContratoDAO contratoDAO;

	private ContratoParcela parcela;
	private Boleto boleto;

	private String tipoBaixa;
	private Long codigo;
	private Integer usuario;
	
	private boolean bloqueado = false;
	private boolean estorno = true;

	@PostConstruct
	public void postConstruct() {

		DI.inject(this);

		setTipoBaixa("B");

		usuario = SessionUtils.getUsuario().getId();
	}

	public void carregarParcela() {

		try {

			bloqueado = false;
			
			if (tipoBaixa.equals("B")) {

				boleto = contratoDAO.getBoletoById(codigo);

				parcela = boleto.getParcela();
			} else {

				parcela = contratoDAO.getParcelaById(codigo);
			}

			if (parcela == null) {
				throw new Exception();
			}
			
			if (parcela.getPagamento() != null) {
				
				bloqueado = true;
				ToastService.getInstance().newToast("Esta parcela já foi paga.", 5000);
			}
			
		} catch (Exception e) {

			ToastService.getInstance().newToast((tipoBaixa.equals("B") ? "Boleto" : "Carnê") + " não encontrado!", 5000);
		}
	}

	public void baixar() {

		if (!validarForm()) {

			return;
		}

		contratoDAO.baixarParcela(parcela, usuario);

		ToastService.getInstance().newToast("Parcela baixada com sucesso.", 5000);

		limparFormulario();
	}
	
	public void estornar() {

		contratoDAO.estornarParcela(parcela, usuario);

		ToastService.getInstance().newToast("Parcela estornada com sucesso.", 5000);

		limparFormulario();
		
		estorno = false;
	}

	private void limparFormulario() {

		codigo = null;
		boleto = null;
		parcela = null;
		bloqueado = false;
		estorno = false;
	}

	public boolean validarForm() {

		boolean retorno = true;

		try {

			if (parcela.getVencimento() == null) {
				
				ToastService.getInstance().newToast("Por favor, pesquisa por um boleto ou carnê.", 5000);
				retorno = false;
			}
			
			if (parcela.getPagamento() == null) {

				ToastService.getInstance().newToast("Por favor, informe a data do pagamento da parcela.", 5000);
				retorno = false;
			}

			if (parcela.getValorPagamento() == null) {

				ToastService.getInstance().newToast("Por favor, informe o valor do pagamento da parcela.", 5000);
				retorno = false;
			}
		} catch (Exception e) {

			retorno = false;
		}

		return retorno;
	}

	public String getTipoBaixa() {
		return tipoBaixa;
	}

	public void setTipoBaixa(String tipoBaixa) {
		this.tipoBaixa = tipoBaixa;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public ContratoParcela getParcela() {
		return parcela;
	}

	public void setParcela(ContratoParcela parcela) {
		this.parcela = parcela;
	}

	public Boleto getBoleto() {
		return boleto;
	}

	public void setBoleto(Boleto boleto) {
		this.boleto = boleto;
	}

	public Integer getUsuario() {
		return usuario;
	}

	public void setUsuario(Integer usuario) {
		this.usuario = usuario;
	}

	public boolean isBloqueado() {
		return bloqueado;
	}

	public void setBloqueado(boolean bloqueado) {
		this.bloqueado = bloqueado;
	}

	public boolean isEstorno() {
		return estorno;
	}

	public void setEstorno(boolean estorno) {
		this.estorno = estorno;
	}

}
