package br.com.tksoft.financeiro.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "estado_civil")
@NamedQuery(name = "EstadoCivil.findAll", query = "SELECT e FROM EstadoCivil e")
public class EstadoCivil implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;
	private String descricao;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
