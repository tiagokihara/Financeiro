package br.com.tksoft.financeiro.controller;

import java.io.File;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.jrimum.bopepo.Boleto;
import org.jrimum.bopepo.view.BoletoViewer;
import org.primefaces.material.application.ToastService;

import com.google.inject.Inject;

import br.com.tksoft.financeiro.di.DI;
import br.com.tksoft.financeiro.entity.Cliente;
import br.com.tksoft.financeiro.entity.Conta;
import br.com.tksoft.financeiro.entity.Contrato;
import br.com.tksoft.financeiro.entity.ContratoParcela;
import br.com.tksoft.financeiro.entity.Plano;
import br.com.tksoft.financeiro.enums.TipoPagamento;
import br.com.tksoft.financeiro.model.ClienteDAO;
import br.com.tksoft.financeiro.model.ContaDAO;
import br.com.tksoft.financeiro.model.ContratoDAO;
import br.com.tksoft.financeiro.model.PlanoDAO;
import br.com.tksoft.financeiro.util.BoletoUtil;
import br.com.tksoft.financeiro.util.ContratoDataModel;
import br.com.tksoft.financeiro.util.RelatorioUtil;
import br.com.tksoft.financeiro.util.SessionUtils;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@ManagedBean
@ViewScoped
public class ContratoBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private ClienteDAO clienteDAO;
	@Inject
	private ContratoDAO contratoDAO;
	@Inject
	private PlanoDAO planoDAO;
	@Inject
	private ContaDAO contaDAO;

	private ContratoDataModel contratosModel;

	private Cliente cliente;
	private Contrato contrato;
	private Contrato contratoSelecionado;
	private String mensagemValidade;
	private boolean consultou;
	private String mensagemAtraso;

	private boolean editando = false;

	private List<Contrato> contratosSelecionados;
	private List<Cliente> clientes;
	private List<Plano> planos;
	private List<Conta> contas;
	private List<ContratoParcela> parcelas;

	private TipoPagamento[] tiposPagamentos;

	private final Integer BOLETO = 1;

	@PostConstruct
	public void postConstruct() {

		DI.inject(this);
	}

	public void carregarClienteContrato() {

		if (contrato != null && contrato.getId() != null) {

			contrato = contratoDAO.getContrato(contrato.getId());
			cliente = clienteDAO.getClienteById(contrato.getCliente().getId());
			setParcelas(contratoDAO.getParcelasByContrato(contrato.getId()));
			atualizarPrimeiraUltimaParcela();
			editando = true;
		} else if (cliente != null && cliente.getId() != null && !cliente.getId().equals(0L)) {

			cliente = clienteDAO.getClienteById(cliente.getId());
			setContratosModel(new ContratoDataModel(cliente.getId()));
		} else {

			setContratosModel(new ContratoDataModel(null));
		}

		setPlanos(planoDAO.getPlanos(true));
		setContas(contaDAO.getContas());
		setTiposPagamentos(TipoPagamento.values());
	}

	public void carregarClientes() {

		setClientes(clienteDAO.getClientes(true));
	}

	public Long getIdCliente() {

		if (cliente != null && cliente.getId() != null)
			return cliente.getId();

		return 0L;
	}

	public void salvar() {

		try {

			if (!validarCliente()) {

				return;
			}

			contratoDAO.salvar(contrato, cliente, SessionUtils.getUsuario().getId());
			ToastService.getInstance().newToast("Contrato incluído com sucesso!", 5000);
			contrato = null;

		} catch (Exception e) {

			e.printStackTrace();
			ToastService.getInstance().newToast("Erro ao incluir contrato!", 5000);
		}
	}

	public void excluir() {

		try {

			if (contrato.getId() == null) {

				ToastService.getInstance().newToast("Não há contrato selecionado!", 5000);
				return;
			}
			// else if (contratoDAO.getSituacaoRemessa(contrato.getId()) > 0){
			// ToastService.getInstance().newToast("Não é possível excluir um
			// contrato que faz parte de uma remessa de boletos!", 5000);
			// return;
			// }

			contratoDAO.excluir(contrato, SessionUtils.getUsuario().getId());
			ToastService.getInstance().newToast("Contrato excluído com sucesso!", 5000);

			contrato = new Contrato();
			parcelas = null;
			editando = false;

		} catch (Exception e) {

			e.printStackTrace();
			ToastService.getInstance().newToast("Erro ao excluir contrato!", 5000);
		}
	}

	public Boolean validarCliente() {

		Boolean retorno = true;

		if (cliente.getId() == null || cliente.getId().equals(0L)) {

			ToastService.getInstance().newToast("Por favor, informe um cliente!", 5000);
			retorno = false;
		}

		if (contrato.getPlano() == null) {

			ToastService.getInstance().newToast("Por favor, informe um plano!", 5000);
			retorno = false;
		}

		if (contrato.getPrimeiraParcela() == null) {

			ToastService.getInstance().newToast("Por favor, informe todos os dados do contrato!", 5000);
			retorno = false;
		}

		return retorno;
	}

	public void atualizarPrimeiraUltimaParcela() {

		if (contrato.getQuantidadeParcelas() != null && contrato.getDataAssinatura() != null && contrato.getValor() != null) {

			Calendar primeiraParcela = Calendar.getInstance();
			primeiraParcela.setTime(contrato.getPrimeiraParcela());
			// primeiraParcela.setTime(contrato.getDataAssinatura());

			// primeiraParcela.set(Calendar.DAY_OF_MONTH,
			// contrato.getDiaVencimento());
			// primeiraParcela.add(Calendar.MONTH, 1);

			Calendar ultimaParcela = Calendar.getInstance();
			ultimaParcela.setTime(primeiraParcela.getTime());

			ultimaParcela.add(Calendar.MONTH, contrato.getQuantidadeParcelas() - 1);

			contrato.setPrimeiraParcela(primeiraParcela.getTime());
			contrato.setUltimaParcela(ultimaParcela.getTime());
			contrato.setValorParcela(contrato.getValor().divide(new BigDecimal(contrato.getQuantidadeParcelas().toString()), 2, RoundingMode.HALF_UP));
		} else {

			contrato.setPrimeiraParcela(null);
			contrato.setUltimaParcela(null);
		}
	}

	public void imprimirCarteirinhasSelecionadas() {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) facesContext.getExternalContext().getContext();

		Calendar dataAssinatura = Calendar.getInstance();

		if (contratosSelecionados != null && contratosSelecionados.size() > 0) {

			for (Contrato contr : contratosSelecionados) {

				contr.getCliente().setDependentes(clienteDAO.getDependentesByCliente(contr.getCliente().getId(), true));
				// contr.setValidadeContrato(contratoDAO.getUltimaParcelaContrato(contr.getId()));

				dataAssinatura.setTime(contr.getDataAssinatura());
				dataAssinatura.add(Calendar.MONTH, 12);

				contr.setValidadeContrato(dataAssinatura.getTime());
			}
		}

		JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(contratosSelecionados);

		HashMap<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("BASE", context.getResourceAsStream("/resources/imagens/logo_azul.png"));

		RelatorioUtil.gerarRelatorioPdf(facesContext, "/relatorios/rptCarteirinha.jasper", parametros, ds, "carteirinha.pdf");
	}

	public String imprimirCarteirinha() {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) facesContext.getExternalContext().getContext();

		contrato.getCliente().setDependentes(clienteDAO.getDependentesByCliente(contrato.getCliente().getId(), true));

		// contrato.setValidadeContrato(contratoDAO.getUltimaParcelaContrato(contrato.getId()));

		Calendar dataAssinatura = Calendar.getInstance();
		dataAssinatura.setTime(contrato.getDataAssinatura());
		dataAssinatura.add(Calendar.MONTH, 12);

		contrato.setValidadeContrato(dataAssinatura.getTime());

		JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(Arrays.asList(contrato));

		HashMap<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("BASE", context.getResourceAsStream("/resources/imagens/logo_azul.png"));

		RelatorioUtil.gerarRelatorioPdf(facesContext, "/relatorios/rptCarteirinha.jasper", parametros, ds, "carteirinha.pdf");

		return null;
	}

	public String imprimirCarnes() {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) facesContext.getExternalContext().getContext();

		List<ContratoParcela> parcelas = new ArrayList<ContratoParcela>();

		parcelas = contratoDAO.getParcelasByContrato(contrato.getId());

		JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(parcelas);

		HashMap<String, Object> parametros = new HashMap<String, Object>();

		parametros.put("BASE", context.getResourceAsStream("/resources/imagens/logo_essencial_vida.jpg"));

		RelatorioUtil.gerarRelatorioPdf(facesContext, "/relatorios/rptCarne.jasper", parametros, ds, "carne.pdf");

		return null;
	}

	public String imprimirBoletosCarnes() {

		if (contrato.getTipoPagamento().equals(BOLETO)) {

			return imprimirBoletos();
		} else {

			return imprimirCarnes();
		}
	}

	public String imprimirBoletos() {

		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

		FacesContext.getCurrentInstance().responseComplete();

		try {

			File template = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/templates/BoletoTemplate.pdf"));

			List<Boleto> boletos = BoletoUtil.gerarBoletosByContrato(contrato);

			BoletoViewer boletoViewer = new BoletoViewer(boletos.get(0));
			boletoViewer.setTemplate(template);

			List<byte[]> boletosEmBytes = new ArrayList<byte[]>(boletos.size());

			// Adicionando os PDF, em forma de array de bytes, na lista.
			for (Boleto bop : boletos) {
				boletosEmBytes.add(boletoViewer.setBoleto(bop).getPdfAsByteArray());
			}

			// Criando o arquivo com os boletos da lista
			byte[] boletosPorPagina = BoletoUtil.mergeFilesInPages(boletosEmBytes);

			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "inline; filename=boleto.pdf");

			OutputStream output = response.getOutputStream();
			output.write(boletosPorPagina);
			response.flushBuffer();

			output.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public boolean getRenderizarConta() {

		try {

			if (contrato.getTipoPagamento().equals(1)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return true;
		}
	}

	public void consultarCarteirinha() {

		contrato = contratoDAO.getUltimoContratoByCliente(cliente.getId(), cliente.getCpf());
		setMensagemAtraso(null);
		mensagemValidade = null;

		if (contrato != null) {

			List<ContratoParcela> parcelas = contratoDAO.getParcelasVencidasByContrato(contrato.getId());

			for (ContratoParcela parc : parcelas) {

				if (parc.getPagamento() == null) {

					setMensagemAtraso("Atendimento Sob Consulta Prévia! Telefones: (17)3632-7302 ou (17)997054242.");
					break;
				}
			}

			Date dataAtual = new Date();

			if (dataAtual.after(contrato.getDataAssinatura()) && dataAtual.before(contrato.getValidadeContrato())) {
				if(mensagemAtraso == null){
					mensagemValidade = "Atendimento Liberado!";
				}
				
			} else {

				//mensagemValidade = "Carteirinha vencida!";
				setMensagemAtraso("Atendimento Sob Consulta Prévia! Telefones: (17)3632-7302 ou (17)997054242.");
			}
		} else {

			mensagemValidade = "Carteirinha não encontrada!"; 
		}

		consultou = true;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<Contrato> getContratosSelecionados() {
		return contratosSelecionados;
	}

	public void setContratosSelecionados(List<Contrato> contratosSelecionados) {
		this.contratosSelecionados = contratosSelecionados;
	}

	public List<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public Contrato getContrato() {
		return contrato;
	}

	public void setContrato(Contrato contrato) {
		this.contrato = contrato;
	}

	public List<Plano> getPlanos() {
		return planos;
	}

	public void setPlanos(List<Plano> planos) {
		this.planos = planos;
	}

	public ContratoDataModel getContratosModel() {
		return contratosModel;
	}

	public void setContratosModel(ContratoDataModel contratosModel) {
		this.contratosModel = contratosModel;
	}

	public Contrato getContratoSelecionado() {
		return contratoSelecionado;
	}

	public void setContratoSelecionado(Contrato contratoSelecionado) {
		this.contratoSelecionado = contratoSelecionado;
	}

	public List<Conta> getContas() {
		return contas;
	}

	public void setContas(List<Conta> contas) {
		this.contas = contas;
	}

	public TipoPagamento[] getTiposPagamentos() {
		return tiposPagamentos;
	}

	public void setTiposPagamentos(TipoPagamento[] tiposPagamentos) {
		this.tiposPagamentos = tiposPagamentos;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}

	public List<ContratoParcela> getParcelas() {
		return parcelas;
	}

	public void setParcelas(List<ContratoParcela> parcelas) {
		this.parcelas = parcelas;
	}

	public String getMensagemValidade() {
		return mensagemValidade;
	}

	public void setMensagemValidade(String mensagemValidade) {
		this.mensagemValidade = mensagemValidade;
	}

	public boolean isConsultou() {
		return consultou;
	}

	public void setConsultou(boolean consultou) {
		this.consultou = consultou;
	}

	public String getMensagemAtraso() {
		return mensagemAtraso;
	}

	public void setMensagemAtraso(String mensagemAtraso) {
		this.mensagemAtraso = mensagemAtraso;
	}

}
