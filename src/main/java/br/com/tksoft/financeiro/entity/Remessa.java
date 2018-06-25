package br.com.tksoft.financeiro.entity;

import java.io.Serializable;
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

@Entity
@NamedQuery(name = "Remessa.findAll", query = "SELECT c FROM Remessa c")
public class Remessa implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "REMESSA_CODIGO_GENERATOR", sequenceName = "seq_remessa", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REMESSA_CODIGO_GENERATOR")
	private Long id;
	
	private String nome;

	@Temporal(TemporalType.TIMESTAMP)
	private Date data;

	@Column(name = "id_usuario")
	private Integer usuario;

	private Integer sequencia;
	
	@ManyToOne
	@JoinColumn(name = "id_conta", referencedColumnName = "id")
	private Conta conta;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getUsuario() {
		return usuario;
	}

	public void setUsuario(Integer usuario) {
		this.usuario = usuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Integer getSequencia() {
		return sequencia;
	}

	public void setSequencia(Integer sequencia) {
		this.sequencia = sequencia;
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

}