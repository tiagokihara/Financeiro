package br.com.tksoft.financeiro.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.jrimum.vallia.AbstractCPRFValidator;
import org.primefaces.material.application.ToastService;

import com.google.inject.Inject;

import br.com.tksoft.financeiro.di.DI;
import br.com.tksoft.financeiro.entity.Cliente;
import br.com.tksoft.financeiro.entity.Dependente;
import br.com.tksoft.financeiro.entity.EstadoCivil;
import br.com.tksoft.financeiro.entity.Parentesco;
import br.com.tksoft.financeiro.enums.Estados;
import br.com.tksoft.financeiro.model.ClienteDAO;
import br.com.tksoft.financeiro.util.SessionUtils;

@ManagedBean
@ViewScoped
public class ClienteBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private ClienteDAO clienteDAO;

	private Cliente cliente;
	private Cliente clienteFiltro;
	private Dependente dependente;

	private Integer usuario;
	private Boolean dependentesAtivos;
	private Boolean clientesAtivos;

	private List<Cliente> clientes;
	private List<Dependente> dependentes;

	//private boolean mostrarCard;

	@PostConstruct
	public void postConstruct() {

		DI.inject(this);

		usuario = SessionUtils.getUsuario().getId();

		cliente = new Cliente();
		cliente.setTipo("F");
	}

	public void carregarCliente() {

		if (cliente.getId() != null)
			cliente = clienteDAO.getClienteById(cliente.getId());
	}

	public void carregarDependentes() {

		if (cliente.getId() != null) {

			setCliente(clienteDAO.getClienteById(cliente.getId()));
			setDependentes(clienteDAO.getDependentesByCliente(cliente.getId(), dependentesAtivos));
			//listarDependentes();
		}
	}

//	public void listarDependentes() {
//
//		if (cliente.getId() != null) {
//			setDependentes(clienteDAO.getDependentesByCliente(cliente.getId(), dependentesAtivos));
//		}
//	}
	
	public void carregarDependenteEdicao() {
		
		if (dependente != null && dependente.getId() != null && cliente.getId() != null){
			setCliente(clienteDAO.getClienteById(cliente.getId()));
			dependente = clienteDAO.getDependenteById(dependente.getId());
		}else if (cliente.getId() != null){
			setCliente(clienteDAO.getClienteById(cliente.getId()));
		}
	
	}
	
	public void carregarClientes() {

		//if (clientes == null)
			setClientes(clienteDAO.getClientes(clientesAtivos));
	}

	public void pesquisar() {

		setClientes(clienteDAO.getClientesByFiltro(clienteFiltro));
	}

	public Estados[] getEstados() {

		return Estados.values();
	}

	public List<EstadoCivil> getEstadosCivis() {

		return clienteDAO.getEstadosCivis();
	}
	
	public List<Cliente> getEmpresas() {
		
		return clienteDAO.getEmpresas();
	}

	public List<Parentesco> getParentescos() {

		return clienteDAO.getParentescos();
	}

//	public void mostrarCardInclusao() {
//
//		carregarDependenteEdicao();
//		
//		mostrarCard = true;
//
//	}

	public void salvar() {

		Boolean inserindo = cliente.getId() == null;

		try {

			if (!validouCliente() )
				return;

			clienteDAO.salvar(cliente, usuario);
			ToastService.getInstance().newToast("Cliente " + (inserindo ? "incluído" : "alterado") + " com sucesso!", 5000);
			cliente = new Cliente();
			cliente.setTipo("F");
		} catch (Exception e) {

			e.printStackTrace();
			ToastService.getInstance().newToast("Erro ao " + (inserindo ? "incluir" : "alterar") + " cliente!", 5000);
		}
	}

	public void salvarDependente() {

		Boolean inserindo = dependente.getId() == null;

		try {

			if (!validouDependente())
				return;

			clienteDAO.salvar(dependente, cliente, usuario);
			ToastService.getInstance().newToast("Dependente " + (inserindo ? "incluído" : "alterado") + " com sucesso!", 5000);
			dependente = null;
			//setDependentes(clienteDAO.getDependentesByCliente(cliente.getId(), null));
		} catch (Exception e) {

			e.printStackTrace();
			ToastService.getInstance().newToast("Erro ao " + (inserindo ? "incluir" : "alterar") + " dependente!", 5000);
		}
	}
	
	public void excluir() {
		
		try {
			
			if (cliente.getId() == null ) {
				
				ToastService.getInstance().newToast("Não há cliente selecionado!", 5000);
				return;
			}else if (clienteDAO.getSituacaoContrato(cliente.getId()) > 0){
				ToastService.getInstance().newToast("Não é possível excluir cliente que possua contrato ativo!", 5000);
				return;
			}
			
			clienteDAO.excluir(cliente, usuario);
			ToastService.getInstance().newToast("Cliente excluído com sucesso!", 5000);
			
			cliente = new Cliente();
			cliente.setTipo("F");
			
		} catch (Exception e) {
			
			e.printStackTrace();
			ToastService.getInstance().newToast("Erro ao excluir cliente!", 5000);
		}
	}
	
	public void excluirDependente() {
		
		try {
			
			if (dependente.getId() == null ) {
				
				ToastService.getInstance().newToast("Não há dependente selecionado!", 5000);
				return;
			}
			
			clienteDAO.excluirDependente(dependente, usuario);
			ToastService.getInstance().newToast("Dependente excluído com sucesso!", 5000);
			
			dependente = null;
		} catch (Exception e) {
			
			e.printStackTrace();
			ToastService.getInstance().newToast("Erro ao dependente plano!", 5000);
		}
	}
	
	public void getValidatorMessage(String mensagem) {
		ToastService.getInstance().newToast("Por favor, informe um " + mensagem + " válido!", 5000);
	}
	
	private Boolean validouCliente(){
		
		Boolean validou = true;
		
		if (cliente.getNome() == null) {

			ToastService.getInstance().newToast("Por favor, informe o nome do cliente!", 5000);
			validou = false;
		}
		
		if(cliente.getTipo().equals("F")){ //Pessoa Física
			if(cliente.getRg() == null){
				ToastService.getInstance().newToast("Por favor, informe o RG do cliente!", 5000);
				validou = false;
			} 
			
			if(cliente.getCpf() == null){
				ToastService.getInstance().newToast("Por favor, informe o CPF do cliente!", 5000);
				validou = false;
			} else {
				
				AbstractCPRFValidator cpf = org.jrimum.vallia.AbstractCPRFValidator.create(cliente.getCpf());
				if (!cpf.isValido()) {
					
					ToastService.getInstance().newToast("Por favor, informe um CPF válido!", 5000);
					validou = false;
				}
			}
			
			if(cliente.getNascimento() == null){
				ToastService.getInstance().newToast("Por favor, informe o nascimento do cliente!", 5000);
				validou = false;
			}
			
			if(cliente.getEstadoCivil().getId() == null){
				ToastService.getInstance().newToast("Por favor, informe o estado civil do cliente!", 5000);
				validou = false;
			}
		}else{
			if(cliente.getCnpj() == null){
				ToastService.getInstance().newToast("Por favor, informe o cnpj do cliente!", 5000);
				validou = false;
			}else {
				
				AbstractCPRFValidator cnpj = org.jrimum.vallia.AbstractCPRFValidator.create(cliente.getCnpj());
				if (!cnpj.isValido()) {
					
					ToastService.getInstance().newToast("Por favor, informe um CNPJ válido!", 5000);
					validou = false;
				}
			}
		}
		
		if(cliente.getLogradouro() == null){
			ToastService.getInstance().newToast("Por favor, informe o logradouro do cliente!", 5000);
			validou = false;
		}
		
		if(cliente.getNumero() == null){
			ToastService.getInstance().newToast("Por favor, informe o número do endereço do cliente!", 5000);
			validou = false;
		}
		
		if(cliente.getBairro() == null){
			ToastService.getInstance().newToast("Por favor, informe o bairro do cliente!", 5000);
			validou = false;
		}
		
		if(cliente.getCidade() == null){
			ToastService.getInstance().newToast("Por favor, informe a cidade do cliente!", 5000);
			validou = false;
		}
		
		if(cliente.getUf() == null){
			ToastService.getInstance().newToast("Por favor, informe o UF da cidade do cliente!", 5000);
			validou = false;
		}
		
		if(cliente.getCep() == null){
			ToastService.getInstance().newToast("Por favor, informe o CEP da cidade do cliente!", 5000);
			validou = false;
		}
		
		return validou;
		
	}

	private Boolean validouDependente() {

		Boolean validou = true;

		if (dependente.getNome() == null) {

			ToastService.getInstance().newToast("Por favor, informe o nome do dependente!", 5000);
			validou = false;
		}

		if (dependente.getNascimento() == null) {

			ToastService.getInstance().newToast("Por favor, informe o nascimento do dependente!", 5000);
			validou = false;
		}

		if (dependente.getParentesco() == null || dependente.getParentesco().getId() == null) {

			ToastService.getInstance().newToast("Por favor, informe o parentesco do dependente!", 5000);
			validou = false;
		}

		return validou;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public Integer getUsuario() {
		return usuario;
	}

	public void setUsuario(Integer usuario) {
		this.usuario = usuario;
	}

	public Cliente getClienteFiltro() {
		return clienteFiltro;
	}

	public void setClienteFiltro(Cliente clienteFiltro) {
		this.clienteFiltro = clienteFiltro;
	}

	public List<Dependente> getDependentes() {
		return dependentes;
	}

	public void setDependentes(List<Dependente> dependentes) {
		this.dependentes = dependentes;
	}

	public Dependente getDependente() {
		return dependente;
	}

	public void setDependente(Dependente dependente) {
		this.dependente = dependente;
	}

//	public boolean isMostrarCard() {
//		return mostrarCard;
//	}
//
//	public void setMostrarCard(boolean mostrarCard) {
//		this.mostrarCard = mostrarCard;
//	}
//	
	public Boolean getDependentesAtivos() {
		return dependentesAtivos;
	}

	public void setDependentesAtivos(Boolean dependentesAtivos) {
		this.dependentesAtivos = dependentesAtivos;
	}

	public Boolean getClientesAtivos() {
		return clientesAtivos;
	}

	public void setClientesAtivos(Boolean clientesAtivos) {
		this.clientesAtivos = clientesAtivos;
	}

}
