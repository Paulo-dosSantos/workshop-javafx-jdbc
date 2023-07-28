package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

public class SellerFormController implements Initializable{
	
	private Seller entity;
	
	private SellerService service;
	
	private List<DataChangeListener>dataChangeListeners= new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if(entity==null)	{
			throw new IllegalStateException("A entidade está nula");
		}
		if(service==null) {
			throw new IllegalStateException("O serviço está nulo");
			
			
		}
		
		try {
			entity=getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListener();
			Utils.currentStage(event).close();
		}
		catch(ValidationException e) {
			setErrorsMessages(e.getErrors());
		}
		catch(DbException e) {
			Alerts.showAlert("Erro ao salvar objeto", null, e.getMessage(), AlertType.ERROR);
			
		}
		
	}
	
	private void notifyDataChangeListener() {
		for(DataChangeListener listener: dataChangeListeners) {
			listener.onDataChanged();
			
		}
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		System.out.println("cancel");
		Utils.currentStage(event).close();
	}
	
	
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		initializeNodes();
		
		
	}
	private void initializeNodes() {
		
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
		
	}
	public void setSeller(Seller entity) {
		this.entity=entity;
	}
	public void updateFormData() {
		
		if (entity==null){
			throw new IllegalStateException("Entidade nula");
			
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		
	}
	public void setSellerService(SellerService service) {
		this.service=service;
	}
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	private Seller getFormData() {
		
		Seller obj= new Seller();
		ValidationException error= new ValidationException("Erro validado");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtName.getText()==null || txtName.getText().trim().equals("")) {
			error.addErrors("name", "O campo não pode ser vazio");
		}
		if(error.getErrors().size()>0) {
			throw error;
		}
		
		obj.setName(txtName.getText());
			
			return obj;
	}
	private void setErrorsMessages(Map<String,String>errors) {
		Set<String>fields= errors.keySet();
		
		if(fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}
	
	

}
