package vn.aptech.c2304l.learning.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vn.aptech.c2304l.learning.Main;
import vn.aptech.c2304l.learning.constant.UserRole;
import vn.aptech.c2304l.learning.constant.UserStatus;
import vn.aptech.c2304l.learning.dal.UserDAO;
import vn.aptech.c2304l.learning.model.User;
import vn.aptech.c2304l.learning.utils.AlertNotification;
import vn.aptech.c2304l.learning.utils.UserSession;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class AuthenticationController implements Initializable {
    private User loggedInUser = UserSession.getInstance().getLoggedInUser();
    private AlertNotification alert = new AlertNotification();

    @FXML
    private VBox btnAuthentication;

    @FXML
    private VBox btnCategory;

    @FXML
    private VBox btnLogout;

    @FXML
    private VBox btnMenu;

    @FXML
    private VBox btnOrder;

    @FXML
    private VBox btnProduct;

    @FXML
    private VBox btnStatistic;

    @FXML
    private VBox btnTable;

    private UserDAO userDAO;

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, String> fullnameColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, UserRole> roleColumn;

    @FXML
    private TableColumn<User, UserStatus> statusColumn;

    @FXML
    private TextField searchField;

    @FXML
    private TextField fullnameField;

    @FXML
    private TextField usernameField;

    @FXML
    private ComboBox<UserStatus> statusComboBox;

    @FXML
    private ComboBox<UserStatus> statusFindComboBox;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Button updateButton;

    @FXML
    private Label labelFullName;
    private ObservableList<User> userList;

    private String role;

    public void setRole(String role) {
        this.role = role;
        updateUI();
    }

    private void updateUI() {
        if(Objects.equals(role, "ADMIN")) {
            btnProduct.setVisible(true);
            btnOrder.setVisible(true);
            btnStatistic.setVisible(true);
            btnAuthentication.setVisible(true);
            btnLogout.setVisible(true);
            btnMenu.setVisible(true);
            btnTable.setVisible(true);
            btnCategory.setVisible(true);
        } else {
            btnProduct.setVisible(false);
            btnOrder.setVisible(true);
            btnStatistic.setVisible(false);
            btnAuthentication.setVisible(false);
            btnLogout.setVisible(true);
            btnMenu.setVisible(true);
            btnTable.setVisible(false);
            btnCategory.setVisible(false);
        }
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (loggedInUser != null) {
            labelFullName.setText(loggedInUser.getFullname());
            this.setRole(loggedInUser.getRole().toString());
        }

        userDAO = new UserDAO();
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        fullnameColumn.setCellValueFactory(new PropertyValueFactory<>("fullname"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        roleComboBox.setItems(FXCollections.observableArrayList("ADMIN", "EMPLOYEE"));
        statusComboBox.setItems(FXCollections.observableArrayList(UserStatus.ACTIVE, UserStatus.INACTIVE));
        statusFindComboBox.setItems(FXCollections.observableArrayList(UserStatus.ACTIVE, UserStatus.INACTIVE));
        userList = getUserData();
        userTable.setItems(getUserData());
        userTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showUserDetails(newValue));
    }

    private void showUserDetails(User user) {
        if (user != null) {
            fullnameField.setText(user.getFullname());
            usernameField.setText(user.getUsername());
            statusComboBox.setValue(user.getStatus());
            roleComboBox.setValue(String.valueOf(user.getRole()));
        } else {
            fullnameField.setText("");
            usernameField.setText("");
            statusComboBox.setValue(null);
            roleComboBox.setValue(null);
        }
    }

    @FXML
    private void handleUpdateAction() {
        if(fullnameField.getText() == null || fullnameField.getText().isBlank()) {
            alert.showAlert("Thông báo", "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        User user = new User();
        user.setFullname(fullnameField.getText());
        user.setUsername(usernameField.getText());
        user.setRole(UserRole.valueOf(roleComboBox.getValue()));
        user.setStatus(statusComboBox.getValue());
        if(userDAO.update(user)) {
            alert.showAlert("Thông báo", "Cập nhật thành công.");
        } else {
            alert.showAlert("Thông báo", "Cập nhật thất bại.");
        }
        userTable.refresh();
        userTable.setItems(getUserData());
    }

    private ObservableList<User> getUserData() {
        return userDAO.findAll();
    }

    @FXML
    private void handleSearchAction() {
        String searchText = searchField.getText();
        String searchStatus = statusFindComboBox.getValue().name();
        ObservableList<User> filteredUsers = userDAO.findByUsernameAndStatus(searchText, searchStatus);
        userTable.setItems(filteredUsers);
    }

    @FXML
    public void redirectAuthentication() throws IOException {
        Parent root = FXMLLoader.load(Main.class.getResource("/authentication.fxml"));

        Scene scene = new Scene(root);


        Stage stage = (Stage) btnAuthentication.getScene().getWindow();
        stage.setTitle("Phân quyền");
        stage.setResizable(false);

        stage.setScene(scene);
        stage.show();

    }

    @FXML
    public void redirectMenu() {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource("/menu.fxml"));

            Scene scene = new Scene(root);


            Stage stage = (Stage) btnMenu.getScene().getWindow();
            stage.setTitle("Menu");
            stage.setResizable(false);

            stage.setScene(scene);
            stage.show();
        }  catch (Exception e) {
            System.out.println("redirectMenu(): " + e.getMessage());
        }
    }

    @FXML
    public void redirectTable() {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource("/table.fxml"));

            Scene scene = new Scene(root);


            Stage stage = (Stage) btnTable.getScene().getWindow();
            stage.setTitle("Bàn");
            stage.setResizable(false);

            stage.setScene(scene);
            stage.show();
        }  catch (Exception e) {
            System.out.println("redirectTable(): " + e.getMessage());
        }
    }

    @FXML
    public void redirectCategory() {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource("/category.fxml"));

            Scene scene = new Scene(root);


            Stage stage = (Stage) btnCategory.getScene().getWindow();
            stage.setTitle("Danh mục");
            stage.setResizable(false);

            stage.setScene(scene);
            stage.show();
        }  catch (Exception e) {
            System.out.println("redirectCategory(): " + e.getMessage());
        }
    }

    @FXML
    public void redirectOrder() {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource("/order.fxml"));

            Scene scene = new Scene(root);


            Stage stage = (Stage) btnOrder.getScene().getWindow();
            stage.setTitle("Hóa đơn");
            stage.setResizable(false);

            stage.setScene(scene);
            stage.show();
        }  catch (Exception e) {
            System.out.println("redirectOrder(): " + e.getMessage());
        }
    }

    @FXML
    public void redirectStatistic() {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource("/statistic.fxml"));

            Scene scene = new Scene(root);


            Stage stage = (Stage) btnStatistic.getScene().getWindow();
            stage.setTitle("Thông kê");
            stage.setResizable(false);

            stage.setScene(scene);
            stage.show();
        }  catch (Exception e) {
            System.out.println("redirectStatistic(): " + e.getMessage());
        }
    }

    @FXML
    public void redirectProduct() {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource("/product.fxml"));

            Scene scene = new Scene(root);


            Stage stage = (Stage) btnProduct.getScene().getWindow();
            stage.setTitle("Sản phẩm");
            stage.setResizable(false);

            stage.setScene(scene);
            stage.show();
        }  catch (Exception e) {
            System.out.println("redirectProduct(): " + e.getMessage());
        }
    }

    @FXML
    public void redirectLogin() {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource("/login.fxml"));

            Scene scene = new Scene(root);


            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setTitle("Đăng nhập");
            stage.setResizable(false);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("redirectLogin(): " + e.getMessage());
        }
    }

}
