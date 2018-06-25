package br.com.tksoft.financeiro.controller;

import java.io.File;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.jrimum.bopepo.exemplo.banco.bradesco.NossoNumero;
import org.jrimum.bopepo.pdf.Files;
import org.jrimum.texgit.FlatFile;
import org.jrimum.texgit.Record;
import org.jrimum.texgit.Texgit;
import org.primefaces.context.RequestContext;
import org.primefaces.material.application.ToastService;

import com.google.inject.Inject;

import br.com.tksoft.financeiro.di.DI;
import br.com.tksoft.financeiro.entity.Boleto;
import br.com.tksoft.financeiro.entity.Conta;
import br.com.tksoft.financeiro.entity.Remessa;
import br.com.tksoft.financeiro.enums.Status;
import br.com.tksoft.financeiro.model.ContaDAO;
import br.com.tksoft.financeiro.model.ContratoDAO;
import br.com.tksoft.financeiro.model.RemessaDAO;
import br.com.tksoft.financeiro.util.SessionUtils;

@ManagedBean
@ViewScoped
public class RemessaBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private RemessaDAO remessaDAO;
	@Inject
	private ContratoDAO contratoDAO;
	@Inject
	private ContaDAO contaDAO;

	private Remessa remessa;

	private List<Remessa> remessas;
	private List<Boleto> boletos;
	private List<Boleto> boletosTemp;
	private List<Conta> contas;

	private Long idConta;
	private Long idContaTemp;
	private Integer usuario;

	@PostConstruct
	public void postConstruct() {

		DI.inject(this);
		setRemessas(remessaDAO.getRemessas());
		setContas(contaDAO.getContas());

		if (idConta != null) {

			carregarBoletos();
		}

		usuario = SessionUtils.getUsuario().getId();
	}

	public void carregarBoletos() {

		setBoletos(contratoDAO.getBoletosByStatusConta(Status.PENDENTE.getCodigo(), idConta));
	}

	public void validar(ActionEvent event) {
		RequestContext context = RequestContext.getCurrentInstance();

		boolean valido = validarFormulario();

		context.addCallbackParam("valido", valido);

		boletosTemp = new ArrayList<Boleto>();

		boletosTemp.addAll(boletos);
		setIdContaTemp(idConta);

		limparFormulario();
	}

	public void gerarRemessa() {
		
		remessa = remessaDAO.salvar(boletosTemp, idContaTemp, usuario);

		createFlatFile();
	}

	public void limparFormulario() {

		setBoletos(null);
		setIdConta(null);
	}

	public String baixarRemessa() {
		
		boletosTemp = remessaDAO.getBoletosByRemessa(remessa.getId());
		
		idContaTemp = boletosTemp.get(0).getParcela().getContrato().getConta().getId();

		return createFlatFile();
	}

	public String createFlatFile() {

		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

		try {

			Conta conta = contaDAO.getConta(idContaTemp);

			File layout = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/templates/LayoutBradescoCNAB400Envio.xml"));

			FlatFile<Record> remessaJrimum = Texgit.createFlatFile(layout);

			Record header = remessaJrimum.createRecord("Header");

			header.setValue("CodigoDaEmpresa", conta.getCodigoEmpresa());

			if (conta.getNome().length() > 30)
				header.setValue("NomeDaEmpresa", conta.getNome().substring(0, 30).toUpperCase());
			else
				header.setValue("NomeDaEmpresa", conta.getNome().toUpperCase());

			header.setValue("DataGravacaoArquivo", remessa.getData());
			header.setValue("NumeroSequencialRemessa", remessa.getId());
			header.setValue("NumeroSequencialRegistro", 1);

			remessaJrimum.addRecord(header);

			int cont = 0;

			for (Boleto boleto : boletosTemp) {

				cont++;

				Record titulo = remessaJrimum.createRecord("TransacaoTitulo");

				titulo.setValue("IdCarteira", conta.getCarteira());
				titulo.setValue("IdAgencia", conta.getAgencia());
				titulo.setValue("IdContaCorrente", conta.getConta());
				titulo.setValue("IdDigitoContaCorrente", conta.getDigitoConta());

				if (boleto.getParcela().getContrato().getMulta() != null) {

					titulo.setValue("TemMulta", 2);
					titulo.setValue("PercentualMulta", String.format("%04d", boleto.getParcela().getContrato().getMulta().multiply(new BigDecimal("100")).intValue()));
				} else {

					titulo.setValue("TemMulta", 0);
					titulo.setValue("PercentualMulta", 0);
				}

				titulo.setValue("NossoNumero", boleto.getId());
				titulo.setValue("DigitoNossoNumero", NossoNumero.valueOf(boleto.getId(), conta.getCarteira()).getDv());
				titulo.setValue("Ocorrencia", 01); // Remessa
				titulo.setValue("NumeroDocumento", boleto.getParcela().getId().toString());
				titulo.setValue("Vencimento", boleto.getParcela().getVencimento());
				titulo.setValue("Valor", boleto.getParcela().getValor());

				if (boleto.getParcela().getContrato().getJuros() != null) {

					BigDecimal jurosReal = boleto.getParcela().getContrato().getJuros().divide(new BigDecimal("100"));
					BigDecimal valorJuros = boleto.getParcela().getValor().multiply(jurosReal).setScale(2, RoundingMode.HALF_UP);

					titulo.setValue("JurosDeMora", String.valueOf(valorJuros).replace(".", ""));
				}

				titulo.setValue("DataDaEmissao", boleto.getParcela().getDataInclusao());

				if (boleto.getParcela().getContrato().getDesconto() != null) {

					titulo.setValue("DataLimiteDesconto", boleto.getParcela().getVencimento());
					titulo.setValue("DescontoConcedido", boleto.getParcela().getContrato().getDesconto().setScale(2, RoundingMode.HALF_UP));
				}

				if (boleto.getParcela().getContrato().getCliente().getTipo().equals("F")) {

					titulo.setValue("TipoPagador", 1);
					titulo.setValue("InscricaoPagador", Long.valueOf(boleto.getParcela().getContrato().getCliente().getCpf().replaceAll("[^0-9]", "")));
				} else {

					titulo.setValue("TipoPagador", 2);
					titulo.setValue("InscricaoPagador", Long.valueOf(boleto.getParcela().getContrato().getCliente().getCnpj().replaceAll("[^0-9]", "")));
				}

				if (boleto.getParcela().getContrato().getCliente().getNome().length() > 40) {

					titulo.setValue("NomePagador", boleto.getParcela().getContrato().getCliente().getNome().substring(0, 40));
				} else {

					titulo.setValue("NomePagador", boleto.getParcela().getContrato().getCliente().getNome());
				}

				if (boleto.getParcela().getContrato().getCliente().getEnderecoCompleto().length() > 40) {

					titulo.setValue("EnderecoPagador", boleto.getParcela().getContrato().getCliente().getEnderecoCompleto().substring(0, 40));
				} else {

					titulo.setValue("EnderecoPagador", boleto.getParcela().getContrato().getCliente().getEnderecoCompleto());
				}

				titulo.setValue("Cep", boleto.getParcela().getContrato().getCliente().getCep().replaceAll("[^0-9]", ""));

				titulo.setValue("NumeroSequencialRegistro", cont);

				remessaJrimum.addRecord(titulo);
			}

			Record trailler = remessaJrimum.createRecord("Trailler");

			trailler.setValue("sequencia", cont);

			remessaJrimum.addRecord(trailler);

			FileUtils.writeLines(new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/templates") + "/TEMP.TST"), remessaJrimum.write(),
					"\r\n");

			File arquivoRemessa = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/templates/TEMP.TST"));

			FacesContext.getCurrentInstance().responseComplete();

			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "content; filename=" + remessa.getNome());

			OutputStream output = response.getOutputStream();
			output.write(Files.fileToBytes(arquivoRemessa));
			response.flushBuffer();

			output.close();

			if (!arquivoRemessa.delete()) {

				arquivoRemessa.deleteOnExit();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private boolean validarFormulario() {
		boolean retorno = true;

		if (idConta == null) {

			ToastService.getInstance().newToast("Por favor, informe uma conta.", 5000);
			retorno = false;
		} else if (boletos == null || boletos.size() < 1) {

			ToastService.getInstance().newToast("Não há boletos para gerar remessa.", 5000);
			retorno = false;
		}

		return retorno;
	}

	public List<Remessa> getRemessas() {
		return remessas;
	}

	public void setRemessas(List<Remessa> remessas) {
		this.remessas = remessas;
	}

	public List<Boleto> getBoletos() {
		return boletos;
	}

	public void setBoletos(List<Boleto> boletos) {
		this.boletos = boletos;
	}

	public List<Conta> getContas() {
		return contas;
	}

	public void setContas(List<Conta> contas) {
		this.contas = contas;
	}

	public Integer getUsuario() {
		return usuario;
	}

	public void setUsuario(Integer usuario) {
		this.usuario = usuario;
	}

	public Remessa getRemessa() {
		return remessa;
	}

	public void setRemessa(Remessa remessa) {
		this.remessa = remessa;
	}

	public Long getIdConta() {
		return idConta;
	}

	public void setIdConta(Long idConta) {
		this.idConta = idConta;
	}

	public List<Boleto> getBoletosTemp() {
		return boletosTemp;
	}

	public void setBoletosTemp(List<Boleto> boletosTemp) {
		this.boletosTemp = boletosTemp;
	}

	public Long getIdContaTemp() {
		return idContaTemp;
	}

	public void setIdContaTemp(Long idContaTemp) {
		this.idContaTemp = idContaTemp;
	}

}
