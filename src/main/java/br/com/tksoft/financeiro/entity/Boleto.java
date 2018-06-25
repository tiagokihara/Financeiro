package br.com.tksoft.financeiro.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

@Entity
@NamedQuery(name = "Boleto.findAll", query = "SELECT c FROM Boleto c")
public class Boleto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "BOLETO_CODIGO_GENERATOR", sequenceName = "seq_boleto", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOLETO_CODIGO_GENERATOR")
	private Long id;

	@OneToOne
	@JoinColumn(name = "id_contrato_parcela", referencedColumnName = "id")
	private ContratoParcela parcela;

	private Integer status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ContratoParcela getParcela() {
		return parcela;
	}

	public void setParcela(ContratoParcela parcela) {
		this.parcela = parcela;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}