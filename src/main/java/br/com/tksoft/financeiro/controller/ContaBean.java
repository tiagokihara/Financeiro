package br.com.tksoft.financeiro.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.tksoft.financeiro.di.DI;

@ManagedBean
@ViewScoped
public class ContaBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@PostConstruct
	public void postConstruct() {
		
		DI.inject(this);
	}
}
