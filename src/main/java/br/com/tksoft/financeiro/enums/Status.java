package br.com.tksoft.financeiro.enums;

public enum Status {
	
	PENDENTE(1, "Pendente"),
	ENVIADO(2, "Enviado"),
	BAIXADO(3, "Baixado");

	
	private Integer codigo;
	private String nome;
	
	private Status(Integer codigo, String nome) {
		this.codigo = codigo;
		this.nome = nome;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}
