
package br.com.tksoft.financeiro.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@NamedQuery(name = "Contrato.findAll", query = "SELECT c FROM Contrato c")
public class Contrato implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "CONTRATO_CODIGO_GENERATOR", sequenceName = "seq_contrato", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONTRATO_CODIGO_GENERATOR")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "id_cliente", referencedColumnName = "id")
	private Cliente cliente;

	@Column(name = "primeira_parcela")
	private Date primeiraParcela;

	@Column(name = "ultima_parcela")
	private Date ultimaParcela;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_inclusao")
	private Date dataInclusao;

	@Column(name = "id_usuario_inclusao")
	private Integer usuarioInclusao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_alteracao")
	private Date dataAlteracao;

	@Column(name = "id_usuario_alteracao")
	private Integer usuarioAlteracao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_fim")
	private Date dataFim;

//	@Column(name = "dia_vencimento")
//	private Integer diaVencimento;

	@Column(name = "data_assinatura")
	private Date dataAssinatura;

	@Column(name = "qtd_parcelas")
	private Integer quantidadeParcelas;

	@Column(name = "tipo_pagamento")
	private Integer tipoPagamento;

	@ManyToOne
	@JoinColumn(name = "id_plano", referencedColumnName = "id")
	private Plano plano;

	private BigDecimal valor;

	@Column(name = "plano_funeral")
	private Boolean planoFuneral;

	private BigDecimal desconto;

	private BigDecimal juros;

	private BigDecimal multa;

	@Transient
	private BigDecimal valorParcela;

	@Transient
	private Date validadeContrato;

	@ManyToOne
	@JoinColumn(name = "id_conta", referencedColumnName = "id")
	private Conta conta;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Date getPrimeiraParcela() {
		return primeiraParcela;
	}

	public void setPrimeiraParcela(Date primeiraParcela) {
		this.primeiraParcela = primeiraParcela;
	}

	public Date getUltimaParcela() {
		return ultimaParcela;
	}

	public void setUltimaParcela(Date ultimaParcela) {
		this.ultimaParcela = ultimaParcela;
	}

	public Date getDataInclusao() {
		return dataInclusao;
	}

	public void setDataInclusao(Date dataInclusao) {
		this.dataInclusao = dataInclusao;
	}

	public Integer getUsuarioInclusao() {
		return usuarioInclusao;
	}

	public void setUsuarioInclusao(Integer usuarioInclusao) {
		this.usuarioInclusao = usuarioInclusao;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public Integer getUsuarioAlteracao() {
		return usuarioAlteracao;
	}

	public void setUsuarioAlteracao(Integer usuarioAlteracao) {
		this.usuarioAlteracao = usuarioAlteracao;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

//	public Integer getDiaVencimento() {
//		return diaVencimento;
//	}
//
//	public void setDiaVencimento(Integer diaVencimento) {
//		this.diaVencimento = diaVencimento;
//	}

	public Date getDataAssinatura() {
		return dataAssinatura;
	}

	public void setDataAssinatura(Date dataAssinatura) {
		this.dataAssinatura = dataAssinatura;
	}

	public Integer getQuantidadeParcelas() {
		return quantidadeParcelas;
	}

	public void setQuantidadeParcelas(Integer quantidadeParcelas) {
		this.quantidadeParcelas = quantidadeParcelas;
	}

	public Integer getTipoPagamento() {
		return tipoPagamento;
	}

	public void setTipoPagamento(Integer tipoPagamento) {
		this.tipoPagamento = tipoPagamento;
	}

	public Plano getPlano() {
		return plano;
	}

	public void setPlano(Plano plano) {
		this.plano = plano;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public BigDecimal getValorParcela() {
		return valorParcela;
	}

	public void setValorParcela(BigDecimal valorParcela) {
		this.valorParcela = valorParcela;
	}

	public Boolean getPlanoFuneral() {
		return planoFuneral;
	}

	public void setPlanoFuneral(Boolean planoFuneral) {
		this.planoFuneral = planoFuneral;
	}

	public BigDecimal getDesconto() {
		return desconto;
	}

	public void setDesconto(BigDecimal desconto) {
		this.desconto = desconto;
	}

	public Date getValidadeContrato() {

		if (dataAssinatura != null) {

		Calendar validade = Calendar.getInstance();
		validade.setTime(dataAssinatura);
		validade.add(Calendar.MONTH, 12);

		return validade.getTime();
		}

		return null;
		}

	public void setValidadeContrato(Date validadeContrato) {
		this.validadeContrato = validadeContrato;
	}

	public BigDecimal getJuros() {
		return juros;
	}

	public void setJuros(BigDecimal juros) {
		this.juros = juros;
	}

	public BigDecimal getMulta() {
		return multa;
	}

	public void setMulta(BigDecimal multa) {
		this.multa = multa;
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

}
