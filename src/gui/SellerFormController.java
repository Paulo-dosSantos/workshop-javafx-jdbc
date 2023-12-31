package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller entity;

	private SellerService service;

	private DepartmentService departmentService;

	@FXML
	private ComboBox<Department> comboBoxDepartment;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker dpBirthDate;

	@FXML
	private TextField txtBaseSalary;

	@FXML
	private Label labelErrorName;

	@FXML
	private Label labelErrorEmail;

	@FXML
	private Label labelErrorBirthDate;

	@FXML
	private Label labelErrorBaseSalary;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	private ObservableList<Department> obsList;

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("A entidade está nula");
		}
		if (service == null) {
			throw new IllegalStateException("O serviço está nulo");

		}

		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListener();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorsMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Erro ao salvar objeto", null, e.getMessage(), AlertType.ERROR);

		}

	}

	private void notifyDataChangeListener() {
		for (DataChangeListener listener : dataChangeListeners) {
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
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		initializeComboBoxDepartment();

	}

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void updateFormData() {

		if (entity == null) {
			throw new IllegalStateException("Entidade nula");

		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));

		if (entity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		if(entity.getDepartment()==null) {
			comboBoxDepartment.getSelectionModel().selectFirst();
		}
		comboBoxDepartment.setValue(entity.getDepartment());

	}

	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	private Seller getFormData() {

		Seller obj = new Seller();
		ValidationException error = new ValidationException("Erro validado");

		obj.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			error.addErrors("name", "O campo não pode ser vazio");
		}
		
		
		
		
		
		
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			error.addErrors("email", "O campo não pode ser vazio");}
		
		
		if (error.getErrors().size() > 0) {
			throw error;
		}
		if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) {
			error.addErrors("baseSalary", "O campo não pode ser vazio");
		}
		
		if(dpBirthDate.getValue()==null) {
			error.addErrors("birthDate", "O campo não pode ser vazio");
			
		}
		obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));
		obj.setName(txtName.getText());
		obj.setEmail(txtEmail.getText());
		obj.setDepartment(comboBoxDepartment.getValue());
		
		Instant instant= Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
		obj.setBirthDate(Date.from(instant));
		return obj;
	}

	public void loadAssociatedObjects() {

		if (departmentService == null) {
			throw new IllegalStateException("Department Service está nulo");
		}
		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(obsList);
	}

	private void setErrorsMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		labelErrorName.setText(fields.contains("name")? errors.get("name"):"");
		labelErrorEmail.setText(fields.contains("email")? errors.get("email"):"");
		labelErrorBirthDate.setText(fields.contains("birthDate")? errors.get("birthDate"):"");
		labelErrorBaseSalary.setText(fields.contains("baseSalary")? errors.get("baseSalary"):"");

		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
		else {
			labelErrorName.setText("");
		}
		labelErrorName.setText(fields.contains("name")? errors.get("name"):"");
		if (fields.contains("email")) {
			labelErrorEmail.setText(errors.get("email"));
		}
		if (fields.contains("baseSalary")) {
			labelErrorBaseSalary.setText(errors.get("baseSalary"));
		}
		if(fields.contains("birthDate")) {
			labelErrorBirthDate.setText(errors.get("birthDate"));
		}
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}

}
