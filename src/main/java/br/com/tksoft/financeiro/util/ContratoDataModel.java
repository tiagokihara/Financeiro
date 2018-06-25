package br.com.tksoft.financeiro.util;

import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SelectableDataModel;
import org.primefaces.model.SortOrder;

import com.google.inject.Inject;

import br.com.tksoft.financeiro.di.DI;
import br.com.tksoft.financeiro.entity.Contrato;
import br.com.tksoft.financeiro.model.ContratoDAO;

public class ContratoDataModel extends LazyDataModel<Contrato> implements SelectableDataModel<Contrato> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private ContratoDAO contratoDAO;

	private Long idCliente;

	public ContratoDataModel() {
	}

	public ContratoDataModel(Long idCliente) {

		DI.inject(this);
		this.idCliente = idCliente;
	}

	@Override
	public Contrato getRowData(String rowKey) {

		try {

			return contratoDAO.getContrato(Long.parseLong(rowKey));
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Object getRowKey(Contrato contrato) {

		return contrato.getId();
	}

	@Override
	public List<Contrato> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

		try {

			setRowCount(contratoDAO.getContratosRowCount(idCliente));

			return contratoDAO.getContratos(first, pageSize, idCliente);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}