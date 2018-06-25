package br.com.tksoft.financeiro.enums;

public enum TipoPagamento {

	BOLETO(1, "Boleto"), CARNE(2, "Carnê");

	private Integer codigo;
	private String nome;

	private TipoPagamento(Integer codigo, String nome) {
		this.codigo = codigo;
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

}
