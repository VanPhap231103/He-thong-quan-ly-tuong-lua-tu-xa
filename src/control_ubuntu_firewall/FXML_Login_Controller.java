package control_ubuntu_firewall;

import java.sql.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class FXML_Login_Controller implements Initializable {

    @FXML private Button Connect_btn;
    @FXML private TextField ip;
    @FXML private PasswordField password;
    @FXML private TextField port;
    @FXML private TextField user_name;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Label connect_status;
    @FXML
    private TableView<SavedConnection> connectionTable;
    @FXML
    private TableColumn<SavedConnection, Integer> sttColumn;
    @FXML
    private TableColumn<SavedConnection, String> ipColumn;
    @FXML
    private TableColumn<SavedConnection, Integer> portColumn;
    @FXML
    private TableColumn<SavedConnection, String> usernameColumn;

    // Biến tĩnh để lưu thông tin đăng nhập
    public static String ipAddress;
    public static String username;
    public static String sudoPassword;
    public static int portNumber;

    private double x = 0;
    private double y = 0;
    
    @FXML
    public void signUp_Close() {
        System.exit(0);
    }

    @FXML
    public void signUp_minimize() {
        Stage stage = (Stage) Connect_btn.getScene().getWindow();
        stage.setIconified(true);
    }

    // Update the connectSSH method
public void connectSSH() {
    String ipAddressTemp = ip.getText();
    String portNumberTemp = port.getText();
    String usernameTemp = user_name.getText();
    String pwd = password.getText();

    if (ipAddressTemp.isEmpty() || portNumberTemp.isEmpty() || usernameTemp.isEmpty() || pwd.isEmpty()) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Message");
        alert.setContentText("Please fill all blank fields");
        alert.showAndWait();
        return;
    }

    // Update UI for connecting state
    connect_status.setText("Đang kết nối...");
    connect_status.getStyleClass().clear();
    connect_status.getStyleClass().add("status-connecting");
    
    progressIndicator.setVisible(true);
    progressIndicator.getStyleClass().clear();
    progressIndicator.getStyleClass().add("connection-progress-connecting");
    progressIndicator.setProgress(-1); // Indeterminate progress

    Task<Boolean> connectTask = new Task<>() {
        @Override
        protected Boolean call() {
            try {
                SSHUtil ssh = new SSHUtil(ipAddressTemp, usernameTemp, pwd, Integer.parseInt(portNumberTemp));
                ssh.connect();
                ssh.disconnect();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    };

    connectTask.setOnSucceeded(event -> {
        if (connectTask.getValue()) {
            // Update UI for connected state
            connect_status.setText("Đã kết nối");
            connect_status.getStyleClass().clear();
            connect_status.getStyleClass().add("status-connected");
            
            progressIndicator.getStyleClass().clear();
            progressIndicator.getStyleClass().add("connection-progress-connected");
            progressIndicator.setProgress(1); // Complete progress
            
            // Lưu thông tin đăng nhập
            ipAddress = ipAddressTemp;
            username = usernameTemp;
            sudoPassword = pwd;
            portNumber = Integer.parseInt(portNumberTemp);
            
            // Thêm phần lưu vào database - Cho phép trùng IP nếu username khác
            try {
                // Kiểm tra xem kết nối với IP và username này đã tồn tại chưa
                boolean connectionExists = DatabaseUtil.connectionExists(ipAddressTemp, usernameTemp);
                
                if (!connectionExists) {
                    // Chỉ lưu nếu kết hợp IP và username chưa tồn tại
                    DatabaseUtil.saveConnection(ipAddressTemp, usernameTemp, pwd, Integer.parseInt(portNumberTemp));
                    loadConnections(); // Refresh bảng
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Message");
            alert.setContentText("Successfully connected to server!");
            alert.showAndWait();

            try {
                Connect_btn.getScene().getWindow().hide();
                Parent root = FXMLLoader.load(getClass().getResource("DashBoard.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);

                root.setOnMousePressed(e -> {
                    x = e.getSceneX();
                    y = e.getSceneY();
                });
                root.setOnMouseDragged(e -> {
                    stage.setX(e.getScreenX() - x);
                    stage.setY(e.getScreenY() - y);
                });

                stage.initStyle(StageStyle.TRANSPARENT);
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Update UI for failed connection
            connect_status.setText("Kết nối thất bại");
            connect_status.getStyleClass().clear();
            connect_status.getStyleClass().add("status-disconnected");
            
            progressIndicator.getStyleClass().clear();
            progressIndicator.getStyleClass().add("connection-progress-failed");
            progressIndicator.setProgress(0); // Reset progress
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setContentText("Failed to connect to the server!");
            alert.showAndWait();
        }
    });

    connectTask.setOnFailed(event -> {
        // Update UI for failed connection
        connect_status.setText("Kết nối thất bại");
        connect_status.getStyleClass().clear();
        connect_status.getStyleClass().add("status-disconnected");
        
        progressIndicator.getStyleClass().clear();
        progressIndicator.getStyleClass().add("connection-progress-failed");
        progressIndicator.setProgress(0); // Reset progress
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Message");
        alert.setContentText("Something went wrong!");
        alert.showAndWait();
    });

    new Thread(connectTask).start();
}
    
    public void setupConnectionTable() {
        sttColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        ipColumn.setCellValueFactory(cellData -> cellData.getValue().ipAddressProperty());
        portColumn.setCellValueFactory(cellData -> cellData.getValue().portProperty().asObject());
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadConnections() {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM saved_connections")) {
            
            ObservableList<SavedConnection> connections = FXCollections.observableArrayList();
            while (rs.next()) {
                connections.add(new SavedConnection(
                    rs.getInt("id"),
                    rs.getString("ip_address"),
                    rs.getInt("port"),
                    rs.getString("username")
                ));
            }
            connectionTable.setItems(connections);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load saved connections");
        }
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupConnectionTable();
        loadConnections();
        
        // Set initial connection status
    connect_status.setText("Chưa kết nối");
    connect_status.getStyleClass().add("status-disconnected");
    
    // Show progress indicator in disconnected state
    progressIndicator.setVisible(false);
    progressIndicator.setProgress(0);
    progressIndicator.getStyleClass().add("connection-progress-failed");
    
    // Add selection listener for table
    connectionTable.getSelectionModel().selectedItemProperty().addListener(
        (obs, oldVal, newVal) -> {
            if (newVal != null) {
                ip.setText(newVal.getIpAddress());
                port.setText(String.valueOf(newVal.getPort()));
                user_name.setText(newVal.getUsername());
            }
        }
    );
    }
}

   