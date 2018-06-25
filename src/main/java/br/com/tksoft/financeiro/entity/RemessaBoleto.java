package br.com.tksoft.financeiro.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "remessa_boleto")
@NamedQuery(name = "RemessaBoleto.findAll", query = "SELECT c FROM RemessaBoleto c")
public class RemessaBoleto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "REMESSA_BOLETO_CODIGO_GENERATOR", sequenceName = "seq_remessa_boleto", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REMESSA_BOLETO_CODIGO_GENERATOR")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "id_remessa", referencedColumnName = "id")
	private Remessa remessa;

	@ManyToOne
	@JoinColumn(name = "id_boleto", referencedColumnName = "id")
	private Boleto boleto;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Remessa getRemessa() {
		return remessa;
	}

	public void setRemessa(Remessa remessa) {
		this.remessa = remessa;
	}

	public Boleto getBoleto() {
		return boleto;
	}

	public void setBoleto(Boleto boleto) {
		this.boleto = boleto;
	}

}