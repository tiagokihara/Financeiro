package br.com.tksoft.financeiro.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.material.application.ToastService;

import com.google.inject.Inject;

import br.com.tksoft.financeiro.di.DI;
import br.com.tksoft.financeiro.entity.Plano;
import br.com.tksoft.financeiro.model.PlanoDAO;
import br.com.tksoft.financeiro.util.SessionUtils;

@ManagedBean
@ViewScoped
public class PlanoBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private PlanoDAO planoDAO;

	private Plano plano;
	
	private Integer usuario;
	private Boolean ativos;

	private List<Plano> planos;

	@PostConstruct
	public void postConstruct() {

		DI.inject(this);
		
		usuario = SessionUtils.getUsuario().getId();
		
		setPlanos(planoDAO.getPlanos(null));
	}
	
	public void carregarPlanos() {

		setPlanos(planoDAO.getPlanos(ativos));
	}
	
	public void salvar() {
		
		try {
			
			if (plano.getNome() == null ) {
				
				ToastService.getInstance().newToast("Por favor, informe o nome do plano!", 5000);
				return;
			}
			
			Boolean inserindo = plano.getId() == null;
			
			planoDAO.salvar(plano, usuario);
			ToastService.getInstance().newToast("Plano " + (inserindo ? "incluído" : "alterado") + " com sucesso!", 5000);
			
			plano = null;
		} catch (Exception e) {
			
			e.printStackTrace();
			ToastService.getInstance().newToast("Erro ao incluir plano!", 5000);
		}
	}

	public void excluir() {
		
		try {
			
			if (plano.getId() == null ) {
				
				ToastService.getInstance().newToast("Não há plano selecionado!", 5000);
				return;
			}else if (planoDAO.getSituacaoContrato(plano.getId()) > 0){
				ToastService.getInstance().newToast("Não é possível excluir um plano que possua contrato ativo!", 5000);
				return;
			}
			
			planoDAO.excluir(plano, usuario);
			ToastService.getInstance().newToast("Plano excluído com sucesso!", 5000);
			
			plano = null;
		} catch (Exception e) {
			
			e.printStackTrace();
			ToastService.getInstance().newToast("Erro ao excluir plano!", 5000);
		}
	}
	
	public void carregarPlano() {
		
		if (plano != null && plano.getId() != null)
			plano = planoDAO.getPlanoById(plano.getId());
	}

	public Plano getPlano() {
		return plano;
	}

	public void setPlano(Plano plano) {
		this.plano = plano;
	}

	public List<Plano> getPlanos() {
		return planos;
	}

	public void setPlanos(List<Plano> planos) {
		this.planos = planos;
	}

	public Integer getUsuario() {
		return usuario;
	}

	public void setUsuario(Integer usuario) {
		this.usuario = usuario;
	}

	public Boolean getAtivos() {
		return ativos;
	}

	public void setAtivos(Boolean ativos) {
		this.ativos = ativos;
	}
}