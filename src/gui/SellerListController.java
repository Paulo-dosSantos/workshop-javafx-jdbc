package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener{
	
	private SellerService service;
	
	
	@FXML
	private TableView<Seller> tableViewSeller;
	
	
	@FXML
	private TableColumn<Seller, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Seller, String> tableColumnName;
	
	@FXML
	private TableColumn<Seller, String> tableColumnEmail;
	
	@FXML
	private TableColumn<Seller, Date> tableColumnBirthDate;
	
	@FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary;
	
	
	
	@FXML
	private Button btNew;
	
	private ObservableList<Seller>obsList;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE;
	
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage= Utils.currentStage(event);
		Seller obj= new Seller();
		createDialogForm(obj,"/gui/SellerForm.fxml",parentStage );
	}
	
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		initializeNodes();
		
		
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);
		Stage stage=(Stage)Main.getMainScene().getWindow();
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
		
		
	}
	public void setSellerService(SellerService service) {
		this.service=service;
	}
	public void updateTableView() {
		if(service==null) {
			throw new IllegalStateException("o serviço está nulo");
		}
		List<Seller>list= service.findAll();
		obsList= FXCollections.observableArrayList(list);
		tableViewSeller.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}
	private void createDialogForm(Seller obj,String absoluteName, Stage parentStage) {
			
		try {
			FXMLLoader loader= new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane= loader.load();
			
			SellerFormController controller= loader.getController();
			controller.setSeller(obj);
			
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			controller.setServices(new SellerService(), new DepartmentService());
			controller.loadAssociatedObjects();
			Stage dialogStage= new Stage();
			dialogStage.setTitle("Entre com os dados do departamento: ");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
			
			
			
		}
		catch(IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "erro ao carregar página", e.getMessage(), AlertType.ERROR);
			
		}
	}



	@Override
	public void onDataChanged() {
		updateTableView();
		
	}
	
	private void initEditButtons() { 
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue())); 
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() { 
		 private final Button button = new Button("editar"); 
		 @Override
		 protected void updateItem(Seller obj, boolean empty) { 
		 super.updateItem(obj, empty); 
		 if (obj == null) { 
		 setGraphic(null); 
		 return; 
		 } 
		 setGraphic(button); 
		 button.setOnAction( 
		 event -> createDialogForm( 
		 obj, "/gui/SellerForm.fxml",Utils.currentStage(event))); 
		 } 
		 });
	
	

	}
	private void initRemoveButtons() { 
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue())); 
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() { 
		 private final Button button = new Button("remover"); 
		 @Override
		 protected void updateItem(Seller obj, boolean empty) { 
		 super.updateItem(obj, empty); 
		 if (obj == null) { 
		 setGraphic(null); 
		 return; 
		 } 
		 setGraphic(button); 
		 button.setOnAction(event -> removeEntity(obj)); 
		 }
	private void removeEntity(Seller obj) {
		Optional<ButtonType>result= Alerts.showConfirmation("Confirmação", "Você tem certeza que deseja deletar? ");
		
		if(result.get()==ButtonType.OK) {
			if(service==null) {
				throw new IllegalStateException("O serviço está nulo");
			}
			try {
				service.remove(obj);
			}
			catch(DbIntegrityException e) {
				Alerts.showAlert("Erro ao remover objeto", null, e.getMessage(), AlertType.ERROR);
				
			}
		}
		} 
		 }); 
		} 

}
