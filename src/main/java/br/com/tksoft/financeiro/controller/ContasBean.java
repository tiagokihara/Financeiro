package br.com.tksoft.financeiro.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.material.application.ToastService;

import com.google.inject.Inject;

import br.com.tksoft.financeiro.di.DI;
import br.com.tksoft.financeiro.entity.ContratoParcela;
import br.com.tksoft.financeiro.model.ContasDAO;
import br.com.tksoft.financeiro.util.RelatorioUtil;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@ManagedBean
@ViewScoped
public class ContasBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private ContasDAO contasDAO;
	
	private Date dataInicial;
	private Date dataFinal;
	
	@PostConstruct
	public void postConstruct() {

		DI.inject(this);
	}
	
	public String imprimirContasReceber() {

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
		String periodo = "Período de " + df.format(dataInicial) + " até " + df.format(dataFinal);
		
		FacesContext facesContext = FacesContext.getCurrentInstance();

		List<ContratoParcela> parcelas = contasDAO.parcelasReceberPeriodo(dataInicial, dataFinal);

		JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(parcelas);

		HashMap<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("PERIODO", periodo);

		RelatorioUtil.gerarRelatorioPdf(facesContext, "/relatorios/rptContasReceber.jasper", parametros, ds, "contasReceber.pdf");

		return null;
	}
	
	public void validarForm() {

		boolean retorno = true;

		if (dataInicial == null) {
				
			ToastService.getInstance().newToast("Por favor, informe a data inicial.", 5000);
			retorno = false;
		}else if (dataFinal == null) {

			ToastService.getInstance().newToast("Por favor, informe a data final.", 5000);
			retorno = false;
		}else if (dataInicial.after(dataFinal)) {

			ToastService.getInstance().newToast("A data inicial, deve ser menor que a final.", 5000);
			retorno = false;
		}

		RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("validouDatas", retorno);
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

}
