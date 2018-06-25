package br.com.tksoft.financeiro.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.jrimum.bopepo.BancosSuportados;
import org.jrimum.bopepo.Boleto;
import org.jrimum.bopepo.exemplo.banco.bradesco.NossoNumero;
import org.jrimum.domkee.comum.pessoa.endereco.Endereco;
import org.jrimum.domkee.comum.pessoa.endereco.UnidadeFederativa;
import org.jrimum.domkee.financeiro.banco.febraban.Agencia;
import org.jrimum.domkee.financeiro.banco.febraban.Carteira;
import org.jrimum.domkee.financeiro.banco.febraban.Cedente;
import org.jrimum.domkee.financeiro.banco.febraban.ContaBancaria;
import org.jrimum.domkee.financeiro.banco.febraban.NumeroDaConta;
import org.jrimum.domkee.financeiro.banco.febraban.Sacado;
import org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo;
import org.jrimum.domkee.financeiro.banco.febraban.Titulo;
import org.jrimum.domkee.financeiro.banco.febraban.Titulo.Aceite;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

import br.com.tksoft.financeiro.di.DI;
import br.com.tksoft.financeiro.entity.Contrato;
import br.com.tksoft.financeiro.model.ContratoDAO;

public class BoletoUtil {

	public static List<Boleto> gerarBoletosByContrato(Contrato contrato) {
		
		ContratoDAO contratoDAO = DI.get(ContratoDAO.class);

		/*
		 * INFORMANDO DADOS SOBRE O CEDENTE.
		 */
		Cedente cedente = new Cedente(contrato.getConta().getNome(), contrato.getConta().getCpfCnpj());

		/*
		 * INFORMANDO DADOS SOBRE O SACADO.
		 */
		Sacado sacado = new Sacado(contrato.getCliente().getNome(),
				contrato.getCliente().getTipo().equals("F") ? contrato.getCliente().getCpf() : contrato.getCliente().getCnpj());

		// Informando o endereço do sacado.
		Endereco enderecoSac = new Endereco();
		enderecoSac.setUF(UnidadeFederativa.valueOf(contrato.getCliente().getUf()));
		enderecoSac.setLocalidade(contrato.getCliente().getCidade());
		enderecoSac.setCep(contrato.getCliente().getCep());
		enderecoSac.setBairro(contrato.getCliente().getBairro());
		enderecoSac.setLogradouro(contrato.getCliente().getLogradouro());
		enderecoSac.setNumero(contrato.getCliente().getNumero());
		sacado.addEndereco(enderecoSac);

		/*
		 * INFORMANDO OS DADOS SOBRE O TÍTULO.
		 */

		// Informando dados sobre a conta bancária do título.
		ContaBancaria contaBancaria = new ContaBancaria(BancosSuportados.BANCO_BRADESCO.create());
		contaBancaria.setNumeroDaConta(new NumeroDaConta(contrato.getConta().getConta(), contrato.getConta().getDigitoConta()));
		contaBancaria.setCarteira(new Carteira(contrato.getConta().getCarteira()));
		contaBancaria.setAgencia(new Agencia(contrato.getConta().getAgencia(), contrato.getConta().getDigitoAgencia()));

		List<br.com.tksoft.financeiro.entity.Boleto> boletosParcelas = contratoDAO.getBoletosByContrato(contrato.getId());

		Boleto boleto;
		Titulo titulo;
		List<Boleto> boletos = new ArrayList<Boleto>();

		for (br.com.tksoft.financeiro.entity.Boleto boletoParcela : boletosParcelas) {

			/*
			 * INFORMANDO OS DADOS SOBRE O BOLETO.
			 */
			titulo = new Titulo(contaBancaria, sacado, cedente);

			titulo.setNumeroDoDocumento(String.format("%d", boletoParcela.getId()));
			titulo.setNossoNumero(String.format("%011d", boletoParcela.getId()));
			titulo.setDigitoDoNossoNumero(NossoNumero.valueOf(boletoParcela.getId(), contaBancaria.getCarteira().getCodigo()).getDv());
			titulo.setValor(boletoParcela.getParcela().getValor());
			titulo.setDataDoDocumento(boletoParcela.getParcela().getDataInclusao());
			titulo.setDataDoVencimento(boletoParcela.getParcela().getVencimento());
			titulo.setTipoDeDocumento(TipoDeTitulo.DS_DUPLICATA_DE_SERVICO);
			titulo.setAceite(Aceite.N);

			boleto = new Boleto(titulo);

			boleto.setLocalPagamento("PAGÁVEL PREFERENCIALMENTE NA REDE BRADESCO");

			if (contrato.getDesconto() != null)
				boleto.setInstrucao1("DESCONTO DE R$ " + (contrato.getDesconto().setScale(2, RoundingMode.HALF_UP)).toString() + " ATÉ O VENCIMENTO");

			if (contrato.getMulta() != null || contrato.getJuros() != null) {

				if (contrato.getMulta() != null && contrato.getJuros() != null) {

					boleto.setInstrucao2("VENCIDO, COBRAR MULTA DE " + contrato.getMulta().setScale(2, RoundingMode.HALF_UP) + "% + JUROS DE "
							+ contrato.getJuros().setScale(2, RoundingMode.HALF_UP) + "% MÊS");
				} else {

					if (contrato.getMulta() != null) {

						boleto.setInstrucao2("VENCIDO, COBRAR MULTA DE " + contrato.getMulta().setScale(2, RoundingMode.HALF_UP) + "%");
					} else {

						boleto.setInstrucao2("VENCIDO, COBRAR JUROS DE " + contrato.getJuros().setScale(2, RoundingMode.HALF_UP) + "% MÊS");
					}
				}

			}

			boleto.setInstrucao3(contrato.getConta().getMensagem1());
			boleto.setInstrucao4(contrato.getConta().getMensagem2());

			boleto.addTextosExtras("txtFcCedente", String.format("%s %s %s", boleto.getTitulo().getCedente().getNome(),
					boleto.getTitulo().getCedente().getCPRF().getCodigoFormatado(), "R. 10, 2740-CENTRO-JALES/SP"));

			String nossoNumero = String.format("%02d/%s-%s", boleto.getTitulo().getContaBancaria().getCarteira().getCodigo(), boleto.getTitulo().getNossoNumero(),
					boleto.getTitulo().getDigitoDoNossoNumero());

			boleto.addTextosExtras("txtFcNossoNumero", nossoNumero);
			boleto.addTextosExtras("txtRsNossoNumero", nossoNumero);

			boletos.add(boleto);
		}

		return boletos;
	}
	
	public static byte[] mergeFilesInPages(List<byte[]> pdfFilesAsByteArray) throws DocumentException, IOException {

		Document document = new Document();
		ByteArrayOutputStream byteOS = new ByteArrayOutputStream();

		PdfWriter writer = PdfWriter.getInstance(document, byteOS);

		document.open();

		PdfContentByte cb = writer.getDirectContent();
		float positionAnterior = 421;

		// Para cada arquivo da lista, cria-se um PdfReader, responsável por ler
		// o arquivo PDF e recuperar informações dele.
		for (byte[] pdfFile : pdfFilesAsByteArray) {

			PdfReader reader = new PdfReader(pdfFile);

			// Faz o processo de mesclagem por página do arquivo, começando pela
			// de número 1.
			for (int i = 1; i <= reader.getNumberOfPages(); i++) {

				// Importa a página do PDF de origem
				PdfImportedPage page = writer.getImportedPage(reader, i);
				float pagePosition = positionAnterior;

				/*
				 * Se a altura restante no documento de destino for menor que a
				 * altura do documento, cria-se uma nova página no documento de
				 * destino.
				 */
				if (positionAnterior < 0f) {
					document.newPage();
					pagePosition = 421;
					positionAnterior = 421;
				}

				// Adiciona a página ao PDF destino
				cb.addTemplate(page, 0, pagePosition);

				positionAnterior -= page.getHeight();
			}
		}

		byteOS.flush();
		document.close();

		byte[] arquivoEmBytes = byteOS.toByteArray();
		byteOS.close();

		return arquivoEmBytes;
	}
}
