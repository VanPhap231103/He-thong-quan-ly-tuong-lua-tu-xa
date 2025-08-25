package control_ubuntu_firewall;

import com.jcraft.jsch.ChannelShell;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.StageStyle;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.concurrent.Worker;
import netscape.javascript.JSObject;
import java.io.InputStream;
import java.io.OutputStream;

public class DashBoardController implements Initializable {

    // Add these field declarations with your other @FXML declarations
    @FXML
    private ComboBox<String> actionComboBox;
    @FXML
    private CheckBox logCheckbox;
    @FXML
    private TextField servicePortField;
    @FXML
    private ComboBox<String> protocolComboBox;
    @FXML
    private TextField sourceIPField;
    @FXML
    private TextField destIPField;
    @FXML
    private Button cancelButton;
    @FXML
    private Label networkInterface;
    @FXML
    private Label ipAddress;
    @FXML
    private Label macAddress;
    @FXML
    private Label gateway;
    @FXML
    private Label dnsServer;
    @FXML
    private Label linkStatus;
    @FXML
    private Button signOutButton;
    @FXML
    private Label userLabel;
    @FXML
    private AnchorPane all_firewall_anchor;
    @FXML
    private ToggleButton firewallToggle;
    @FXML
    private Button reloadButton;
    @FXML
    private AnchorPane firewallContent;
    @FXML
    private TableColumn<PortRule, String> ruleNumber_col;
    @FXML
    public AnchorPane dashboard;
    @FXML
    public AnchorPane menu;
    @FXML
    public AnchorPane serviceFormPane;
    @FXML
    public Button signin_close1;
    @FXML
    public Button signin_minimize1;
    @FXML
    public TableView<App> appTable;
    @FXML
    public TableColumn<App, String> colAppName;
    @FXML
    public TableColumn<App, String> colStatus;
    @FXML
    public TableColumn<App, Void> colAction;

    @FXML
    public TableView<PortRule> port_table;
    @FXML
    public TableColumn<PortRule, String> portProtocol_col;
    @FXML
    public TableColumn<PortRule, String> colAction2;
    @FXML
    public TableColumn<PortRule, String> from_col;
    @FXML
    private Button bang_quy_tac_menu;
    @FXML
    private Button tong_quan;
    @FXML
    private Button tai_khoan;
    @FXML
    private Button logs;
    @FXML
    private Button terminal;
    @FXML
    private Button service_ssh;
    @FXML
    private Button service_ftp;
    @FXML
    private Button service_http;
    @FXML
    private Button qldv_menu;
    @FXML
    private AnchorPane qldv_firewall;
    @FXML
    private AnchorPane table_quytac;

    // Các ô nhập liệu mới
    @FXML
    private TextField toField;
    @FXML
    private TextField fromField;
    @FXML
    private Button saveButton;
    @FXML
    private TextField portField;
    @FXML
    private AnchorPane overviewPane;
    @FXML
    private Label healthStatus;
    @FXML
    private ProgressBar cpuProgress;
    @FXML
    private Label cpuUsage;
    @FXML
    private ProgressBar memoryProgress;
    @FXML
    private Label memoryUsage;
    @FXML
    private Label modelInfo;
    @FXML
    private Label machineId;
    @FXML
    private Label uptime;
    @FXML
    private Label hostname;
    @FXML
    private Label systemTime;
    @FXML
    private Label performanceProfile;
    @FXML
    private CheckBox storeMetrics;

    @FXML
    private AnchorPane userManagementPane;
    @FXML
    private Button createUserButton;
    @FXML
    private FlowPane userCardsPane;
    @FXML
    private TextField newUsernameField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private Button saveNewUserButton;
    @FXML
    private AnchorPane editUserPane;
    @FXML
    private Label editingUserLabel;
    @FXML
    private PasswordField changePasswordField;
    @FXML
    private CheckBox sudoAccessCheckbox;
    @FXML
    private Button saveUserChangesButton;
    @FXML
    private Button deleteUserButton;

    @FXML
    private CheckBox loggingCheckbox;

    @FXML
    private Label currentUserLabel; // Label hiển thị tên người dùng hiện tại
    @FXML
    private FlowPane userCardsContainer; // Container chứa các card người dùng

    @FXML
    private ComboBox<String> serviceActionComboBox;
    @FXML
    private CheckBox serviceLogCheckbox;
    @FXML
    private TextField serviceNameField;
    @FXML
    private Button addServiceButton;
    @FXML
    private Button clearServiceFormButton;

    public void initializeServiceForm() {
        // Khởi tạo combobox hành động
        ObservableList<String> actions = FXCollections.observableArrayList("ALLOW", "DENY");
        serviceActionComboBox.setItems(actions);
        serviceActionComboBox.getSelectionModel().selectFirst(); // Mặc định chọn ALLOW

        // Mặc định không chọn log
        serviceLogCheckbox.setSelected(false);
    }

    @FXML
    public void addServiceRule() {
        try {
            // Kiểm tra dữ liệu nhập vào
            if (serviceNameField.getText() == null || serviceNameField.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập tên dịch vụ");
                return;
            }

            // Lấy giá trị từ các trường
            String action = serviceActionComboBox.getValue().toLowerCase();
            boolean logEnabled = serviceLogCheckbox.isSelected();
            String serviceName = serviceNameField.getText().trim();

            System.out.println("Thông tin dịch vụ: Action=" + action + ", Log=" + logEnabled + ", Service=" + serviceName);

            // Tạo lệnh UFW đơn giản cho dịch vụ
            String command = "sudo -S ufw " + action;

            if (logEnabled) {
                command += " log";
            }

            command += " " + serviceName;

            System.out.println("Lệnh UFW: " + command);

            // Hiển thị thông báo đang xử lý
            ProgressIndicator progress = new ProgressIndicator();
            Label messageLabel = new Label("Đang thêm quy tắc...");
            VBox progressBox = new VBox(10, messageLabel, progress);
            progressBox.setAlignment(Pos.CENTER);
            progressBox.setPadding(new Insets(20));
            progressBox.setStyle("-fx-background-color: white; -fx-background-radius: 5;");

            Popup progressPopup = new Popup();
            progressPopup.getContent().add(progressBox);
            progressPopup.setAutoHide(true);
            progressPopup.show(addServiceButton.getScene().getWindow());

            // Lưu lại lệnh cuối cùng để sử dụng trong luồng
            final String finalCommand = command;

            // Thực thi lệnh trong một luồng riêng biệt giống như sendCommand()
            new Thread(() -> {
                try {
                    String result;
                    // Sử dụng phương thức thích hợp cho lệnh sudo
                    result = sshUtil.executeCommand(finalCommand);

                    // Xử lý kết quả trong luồng UI
                    Platform.runLater(() -> {
                        // Đóng popup
                        progressPopup.hide();

                        // Kiểm tra kết quả
                        if (result != null && (result.toLowerCase().contains("error") || result.toLowerCase().contains("failed"))) {
                            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm quy tắc dịch vụ: " + result);
                        } else {
                            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm quy tắc dịch vụ thành công!");
                            // Làm mới bảng quy tắc
                            loadFirewallRules();
                            // Xóa dữ liệu đã nhập
                            clearServiceForm();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    // Xử lý lỗi trong luồng UI
                    Platform.runLater(() -> {
                        progressPopup.hide();
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi khi thêm quy tắc: " + e.getMessage());
                    });
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Đã xảy ra lỗi: " + e.getMessage());
        }
    }

// Phương thức để xóa dữ liệu trong form dịch vụ
    private void clearServiceForm() {
        if (serviceNameField != null) {
            serviceNameField.clear();
        }
        if (serviceActionComboBox != null) {
            serviceActionComboBox.getSelectionModel().selectFirst();
        }
        if (serviceLogCheckbox != null) {
            serviceLogCheckbox.setSelected(false);
        }
    }

    private UbuntuUser currentEditingUser;
    private ObservableList<UbuntuUser> userList = FXCollections.observableArrayList();

    public ObservableList<App> appList = FXCollections.observableArrayList();
    public SSHUtil sshUtil;
    private PortRule selectedRule; // Lưu quy tắc được chọn

    // Thêm phương thức convertToGB ở ngoài
    private double convertToGB(String memoryString) {
        double value = Double.parseDouble(memoryString.replaceAll("[^0-9.]", ""));
        if (memoryString.contains("Mi") || memoryString.contains("M")) {
            return value / 1024.0;
        } else if (memoryString.contains("Ki") || memoryString.contains("K")) {
            return value / (1024.0 * 1024.0);
        } else if (memoryString.contains("Gi") || memoryString.contains("G")) {
            return value;
        }
        return value;
    }

    @FXML
    private Label diskUsage;
    @FXML
    private Label timeZone;
    @FXML
    private Label osVersion;
    @FXML
    private Label kernelVersion;
    @FXML
    private Label lastUpdate;

    private void loadSystemInfo() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (!isCancelled()) {
                    // Lấy CPU usage và các thông tin cơ bản
                    String cpuOutput = sshUtil.executeCommand("top -bn1 | grep 'Cpu(s)'");
                    String memoryOutput = sshUtil.executeCommand("free -h | grep Mem");
                    String modelOutput = sshUtil.executeCommand("lscpu | grep 'Model name'");
                    String machineIdOutput = sshUtil.executeCommand("cat /etc/machine-id");
                    String uptimeOutput = sshUtil.executeCommand("uptime -p");
                    String hostnameOutput = sshUtil.executeCommand("hostname");
                    String systemTimeOutput = sshUtil.executeCommand("date");
                    String performanceProfileOutput = sshUtil.executeCommand("cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor || echo 'none'");

                    // Thêm các lệnh lấy thông tin mới
                    String diskOutput = sshUtil.executeCommand("df -h / | tail -1");
                    String timeZoneOutput = sshUtil.executeCommand("timedatectl | grep 'Time zone'");
                    String osVersionOutput = sshUtil.executeCommand("cat /etc/os-release | grep PRETTY_NAME");
                    String kernelVersionOutput = sshUtil.executeCommand("uname -r");
                    String lastUpdateOutput = sshUtil.executeCommand("ls -l --time-style=long-iso /var/lib/apt/periodic/update-success-stamp 2>/dev/null || echo 'N/A'");
                    String processedLastUpdate = "N/A";  // Sử dụng biến thường thay vì final

                    // Get network interface information
                    String interfaceInfo = sshUtil.executeCommand("ip -o link show | awk '{print $2}' | grep -v lo | head -n1 | tr -d ':'");
                    String ipInfo = sshUtil.executeCommand("ip -o -4 addr show | grep " + interfaceInfo + " | awk '{print $4}' | cut -d/ -f1");
                    String macInfo = sshUtil.executeCommand("ip link show " + interfaceInfo + " | grep link/ether | awk '{print $2}'");
                    String gatewayInfo = sshUtil.executeCommand("ip route | grep default | awk '{print $3}'");
                    String dnsInfo = sshUtil.executeCommand("cat /etc/resolv.conf | grep nameserver | head -n1 | awk '{print $2}'");
                    String linkInfo = sshUtil.executeCommand("ip link show " + interfaceInfo + " | grep -o \"state [A-Z]*\" | awk '{print $2}'");

                    if (!lastUpdateOutput.equals("N/A") && !lastUpdateOutput.trim().isEmpty()) {
                        try {
                            String[] parts = lastUpdateOutput.trim().split("\\s+");
                            processedLastUpdate = (parts.length >= 6) ? parts[5] + " " + parts[6] : "N/A";
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    final String finalLastUpdate = processedLastUpdate;

                    // Xử lý CPU usage
                    String[] cpuParts = cpuOutput.split(",");
                    double cpuIdle = Double.parseDouble(cpuParts[3].replaceAll("[^0-9.]", ""));
                    double cpuUsed = 100.0 - cpuIdle;

                    // Xử lý Memory usage
                    String[] memoryParts = memoryOutput.split("\\s+");
                    double memTotal = convertToGB(memoryParts[1]);
                    double memUsed = convertToGB(memoryParts[2]);

                    // Xử lý disk usage
                    String[] diskParts = diskOutput.trim().split("\\s+");
                    String diskUsageText = String.format("%s used of %s (%s free)", diskParts[2], diskParts[1], diskParts[3]);

                    Platform.runLater(() -> {
                        try {
                            // System Information
                            modelInfo.setText(modelOutput.split(":")[1].trim());
                            machineId.setText(machineIdOutput.trim());
                            uptime.setText(uptimeOutput.replace("up ", "").trim());

                            // Usage Information
                            cpuProgress.setProgress(cpuUsed / 100.0);
                            cpuUsage.setText(String.format("%.1f%% of 1 CPU", cpuUsed));
                            memoryProgress.setProgress(memUsed / memTotal);
                            memoryUsage.setText(String.format("%.1f / %.1f GiB", memUsed, memTotal));
                            diskUsage.setText(diskUsageText);

                            // Configuration
                            hostname.setText(hostnameOutput.trim());
                            systemTime.setText(systemTimeOutput.trim());
                            performanceProfile.setText(performanceProfileOutput.trim());
                            timeZone.setText(timeZoneOutput.split(":")[1].trim());
                            osVersion.setText(osVersionOutput.split("=")[1].replace("\"", "").trim());
                            kernelVersion.setText(kernelVersionOutput.trim());
                            lastUpdate.setText(finalLastUpdate);

                            // Update network information
                            networkInterface.setText(interfaceInfo.trim());
                            ipAddress.setText(ipInfo.trim());
                            macAddress.setText(macInfo.trim());
                            gateway.setText(gatewayInfo.trim());
                            dnsServer.setText(dnsInfo.trim());
                            // Set link status with appropriate style
                            String linkStatusText = linkInfo.trim();
                            linkStatus.setText(linkStatusText);

                            if (linkStatusText.equalsIgnoreCase("UP")) {
                                linkStatus.getStyleClass().clear();
                                linkStatus.getStyleClass().add("link-up");
                            } else {
                                linkStatus.getStyleClass().clear();
                                linkStatus.getStyleClass().add("link-down");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    Thread.sleep(2000); // Cập nhật mỗi 2 giây
                }
                return null;
            }
        };

        Thread updateThread = new Thread(task);
        updateThread.setDaemon(true); // Đảm bảo thread sẽ dừng khi đóng ứng dụng
        updateThread.start();
    }

    @FXML
    public void viewUsageDetails() {
        // TODO: Hiển thị chi tiết và lịch sử usage
    }

    @FXML
    public void viewHardwareDetails() {
        // TODO: Hiển thị chi tiết phần cứng
    }

    @FXML
    public void editHostname() {
        // TODO: Chỉnh sửa hostname
    }

    @FXML
    public void joinDomain() {
        // TODO: Tham gia domain
    }

    @FXML
    public void showFingerprints() {
        // TODO: Hiển thị SSH fingerprints
    }

    @FXML
    public void signUp_Close() {
        System.exit(0);
    }

    @FXML
    public void signUp_minimize() {
        Stage stage = (Stage) dashboard.getScene().getWindow();
        stage.setIconified(true);
    }

    public void setupTable() {
        colAppName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colAppName.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        colAction.setCellValueFactory(param -> null);
        colAction.setCellFactory(param -> new TableCell<>() {
            public final ToggleButton toggleButton = new ToggleButton();
            public final HBox hbox = new HBox(toggleButton);

            {
                hbox.setAlignment(Pos.CENTER);
                toggleButton.setOnAction(event -> {
                    App app = getTableView().getItems().get(getIndex());
                    toggleApp(app);
                    updateButtonStyle(toggleButton, app.getStatus());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    App app = getTableView().getItems().get(getIndex());
                    toggleButton.setText(app.getStatus());
                    updateButtonStyle(toggleButton, app.getStatus());
                    setGraphic(hbox);
                }
            }
        });
    }

    public void loadApplications() {
        try {
            String output = sshUtil.executeCommand("sudo -S ufw status");
            List<String> apps = new ArrayList<>();

            for (String line : output.split("\n")) {
                if (!line.trim().isEmpty()) {
                    // Loại bỏ các dòng tiêu đề
                    if (line.startsWith("Status:") || line.startsWith("To") || line.startsWith("--")) {
                        continue;
                    }

                    // Phân tích dòng để lấy tên dịch vụ đầy đủ
                    String[] parts = line.trim().split("\\s{2,}"); // Tách bằng 2 khoảng trắng trở lên
                    if (parts.length >= 2) {
                        String fullAppName = parts[0].trim(); // Lấy phần đầu tiên là tên dịch vụ đầy đủ
                        apps.add(fullAppName);
                    }
                }
            }

            appList.clear();
            for (String app : apps) {
                String status = checkAppStatus(app);
                appList.add(new App(app, status));
            }
            appTable.setItems(appList);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải danh sách dịch vụ: " + e.getMessage());
        }
    }

    public String checkAppStatus(String app) {
        try {
            String output = sshUtil.executeCommand("sudo -S ufw status");
            for (String line : output.split("\n")) {
                // Sử dụng so sánh chính xác hơn để tránh trùng lặp một phần tên
                String[] parts = line.trim().split("\\s{2,}");
                if (parts.length >= 2 && parts[0].trim().equals(app)) {
                    return parts[1].trim().equals("ALLOW") ? "Cho phép" : "Bị chặn";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Bị chặn"; // Mặc định là bị chặn nếu không tìm thấy
    }

    public void toggleApp(App app) {
        try {
            String appName = app.getName();
            // Xử lý tên dịch vụ có khoảng trắng hoặc ký tự đặc biệt
            if (appName.contains(" ") || appName.contains("(") || appName.contains(")")) {
                appName = "\"" + appName + "\"";
            }

            String command = app.getStatus().equals("Cho phép")
                    ? "sudo -S ufw deny " + appName
                    : "sudo -S ufw allow " + appName;

            String result = sshUtil.executeCommand(command);

            if (result.contains("ERROR") || result.contains("failed")) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thay đổi trạng thái dịch vụ: " + result);
            } else {
                app.setStatus(app.getStatus().equals("Cho phép") ? "Bị chặn" : "Cho phép");
                appTable.refresh();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thay đổi trạng thái dịch vụ: " + e.getMessage());
        }
    }

    public void updateButtonStyle(ToggleButton button, String status) {
        if (status.equals("Cho phép")) {
            button.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
            button.setText("Cho phép");
        } else {
            button.setStyle("-fx-background-color: #FF3D00; -fx-text-fill: white; -fx-font-weight: bold;");
            button.setText("Bị chặn");
        }
    }

    // Update the loadFirewallRules method to include delete buttons
    private void loadFirewallRules() {
        Task<List<PortRule>> task = new Task<List<PortRule>>() {
            @Override
            protected List<PortRule> call() throws Exception {
                return sshUtil.getNumberedFirewallRules();
            }
        };

        task.setOnSucceeded(e -> {
            List<PortRule> rules = task.getValue();
            ObservableList<PortRule> data = FXCollections.observableArrayList(rules);

            // Set up table columns
            portProtocol_col.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTo()));
            colAction2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAction()));
            from_col.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFrom()));

            // Add action buttons column
            TableColumn<PortRule, Void> actionCol = new TableColumn<>("Thao tác");
            actionCol.setPrefWidth(100);

            actionCol.setCellFactory(param -> new TableCell<PortRule, Void>() {
                private final Button deleteBtn = new Button();

                {
                    deleteBtn.getStyleClass().add("button-danger");
                    deleteBtn.setText("Xóa");
                    deleteBtn.setOnAction(event -> {
                        PortRule rule = getTableView().getItems().get(getIndex());
                        deleteRule(rule);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(deleteBtn);
                    }
                }
            });

            // Check if the column already exists to avoid duplicates
            boolean hasActionColumn = false;
            for (TableColumn<PortRule, ?> column : port_table.getColumns()) {
                if (column.getText().equals("Thao tác")) {
                    hasActionColumn = true;
                    break;
                }
            }

            if (!hasActionColumn) {
                port_table.getColumns().add(actionCol);
            }

            port_table.setItems(data);
        });

        task.setOnFailed(e -> {
            e.getSource().getException().printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Không thể tải quy tắc tường lửa: " + task.getException().getMessage());
            alert.showAndWait();
        });

        new Thread(task).start();
    }

    // Revised method to get UFW rules with better parsing
    public List<PortRule> getUfwRules() {
        List<PortRule> rules = new ArrayList<>();
        try {
            // Use numbered status for better parsing
            String output = sshUtil.executeCommand("sudo -S ufw status numbered");
            String[] lines = output.split("\n");
            boolean rulesStarted = false;
            int ruleNumber = 0;

            for (String line : lines) {
                // Skip header lines and empty lines
                if (line.trim().isEmpty() || line.contains("Status:")) {
                    continue;
                }

                // Start parsing after the header
                if (line.contains("To") && line.contains("Action") && line.contains("From")) {
                    rulesStarted = true;
                    continue;
                }

                if (rulesStarted && !line.startsWith("--")) {
                    // Extract rule number if present
                    if (line.contains("[") && line.contains("]")) {
                        String numStr = line.substring(line.indexOf("[") + 1, line.indexOf("]")).trim();
                        try {
                            ruleNumber = Integer.parseInt(numStr);
                        } catch (NumberFormatException e) {
                            ruleNumber = 0;
                        }
                    }

                    // Remove rule number and brackets if present
                    String cleanLine = line.replaceAll("^\\[\\s*\\d+\\]\\s*", "").trim();

                    if (!cleanLine.isEmpty()) {
                        String[] parts = cleanLine.split("\\s{2,}");
                        if (parts.length >= 3) {
                            String to = parts[0].trim();
                            String action = parts[1].trim();
                            String from = parts[2].trim();
                            String note = parts.length > 3 ? parts[3].trim() : "";

                            // Only add rules if action is ALLOW or DENY
                            if (action.equalsIgnoreCase("ALLOW") || action.equalsIgnoreCase("DENY")) {
                                PortRule rule = new PortRule(to, action, from);
                                rule.setRuleNumber(ruleNumber);

                                rules.add(rule);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lấy danh sách quy tắc: " + e.getMessage());
        }
        return rules;
    }

    // In your class, add or update these fields
    @FXML
    private TableColumn<PortRule, Integer> colRuleNumber;
    @FXML
    private TableColumn<PortRule, String> colTo;
    @FXML
    private TableColumn<PortRule, String> colRuleAction;
    @FXML
    private TableColumn<PortRule, String> colFrom;
    @FXML
    private TableColumn<PortRule, Void> colDelete;

    private void initializePortTable() {
        // Set up cell value factories - Fix the type conversion issue
        colRuleNumber.setCellValueFactory(new PropertyValueFactory<>("ruleNumber"));
        colTo.setCellValueFactory(new PropertyValueFactory<>("to"));
        colRuleAction.setCellValueFactory(new PropertyValueFactory<>("action"));
        colFrom.setCellValueFactory(new PropertyValueFactory<>("from"));

        // Set up the delete button column
        colDelete.setCellFactory(param -> new TableCell<PortRule, Void>() {
//            private final Button editButton = new Button();
            private final Button deleteButton = new Button();
            private final HBox hbox = new HBox(deleteButton);

            {
                // Set up edit button with icon
//                FontAwesomeIcon editIcon = new FontAwesomeIcon();
//                editIcon.setGlyphName("EDIT");
//                editIcon.setSize("1.2em");
//                editButton.setGraphic(editIcon);
//                editButton.getStyleClass().add("edit-button");
//                editButton.setStyle("-fx-background-color: #ffb508; -fx-padding: 2 5;");

                // Set up delete button with icon
                FontAwesomeIcon deleteIcon = new FontAwesomeIcon();
                deleteIcon.setGlyphName("TRASH");
                deleteIcon.setSize("1.2em");
                deleteButton.setGraphic(deleteIcon);
                deleteButton.getStyleClass().add("cancel-button");
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-padding: 2 5;");

                hbox.setAlignment(Pos.CENTER);

//                // Edit button action
//                editButton.setOnAction(event -> {
//                    PortRule rule = getTableView().getItems().get(getIndex());
//                    showEditForm(rule);
//                });
                // Delete button action
                deleteButton.setOnAction(event -> {
                    PortRule rule = getTableView().getItems().get(getIndex());
                    deleteRule(rule);
                });
            }

            // Phương thức hiển thị form chỉnh sửa
            private void showEditForm(PortRule rule) {
                formTitleLabel.setText("Chỉnh sửa quy tắc");
                toField.setText(rule.getTo());
                actionComboBox.setValue(rule.getAction());
                fromField.setText(rule.getFrom());

                // Hiển thị nút cập nhật, ẩn nút lưu
                saveButton.setVisible(false);
                updateButton.setVisible(true);

                // Hiển thị form
                ruleFormPane.setVisible(true);
            }

            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(hbox);
                }
            }
        });

        // Load the rules
        loadPortRules();
    }

//    public void setupPortTable() {
//        // Clear existing columns
//        port_table.getColumns().clear();
//
//        // Create columns
//        TableColumn<PortRule, Integer> numberCol = new TableColumn<>("STT");
//        numberCol.setCellValueFactory(cellData -> cellData.getValue().ruleNumberProperty().asObject());
//        numberCol.setPrefWidth(50);
//
//        TableColumn<PortRule, String> toCol = new TableColumn<>("Dịch vụ/Port(s)");
//        toCol.setCellValueFactory(cellData -> cellData.getValue().toProperty());
//        toCol.setPrefWidth(150);
//
//        TableColumn<PortRule, String> actionCol = new TableColumn<>("Hành động");
//        actionCol.setCellValueFactory(cellData -> cellData.getValue().actionProperty());
//        actionCol.setPrefWidth(100);
//        actionCol.setCellFactory(column -> new TableCell<>() {
//            @Override
//            protected void updateItem(String item, boolean empty) {
//                super.updateItem(item, empty);
//                if (item == null || empty) {
//                    setText(null);
//                    setStyle("");
//                } else {
//                    setText(item);
//                    if (item.equalsIgnoreCase("ALLOW")) {
//                        setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
//                    } else if (item.equalsIgnoreCase("DENY")) {
//                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
//                    } else if (item.equalsIgnoreCase("REJECT")) {
//                        setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
//                    }
//                }
//            }
//        });
//
//        TableColumn<PortRule, String> fromCol = new TableColumn<>("IP nguồn");
//        fromCol.setCellValueFactory(cellData -> cellData.getValue().fromProperty());
//        fromCol.setPrefWidth(150);
//
//        TableColumn<PortRule, String> noteCol = new TableColumn<>("Ghi chú");
//        noteCol.setCellValueFactory(cellData -> cellData.getValue().noteProperty());
//        noteCol.setPrefWidth(150);
//
//        // Action buttons column
//        TableColumn<PortRule, Void> actionsCol = new TableColumn<>("Thao tác");
//        actionsCol.setPrefWidth(100);
//        actionsCol.setCellFactory(param -> new TableCell<>() {
//            private final Button editButton = new Button("✏️");
//            private final Button deleteButton = new Button("🗑️");
//            private final HBox pane = new HBox(5, editButton, deleteButton);
//
//            {
//                // Style buttons
//                editButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
//                deleteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
//
//                // Set actions
//                editButton.setOnAction(event -> {
//                    PortRule rule = getTableView().getItems().get(getIndex());
//                    editRule(rule);
//                });
//
//                deleteButton.setOnAction(event -> {
//                    PortRule rule = getTableView().getItems().get(getIndex());
//                    confirmDeleteRule(rule);
//                });
//
//                pane.setAlignment(Pos.CENTER);
//            }
//
//            @Override
//            protected void updateItem(Void item, boolean empty) {
//                super.updateItem(item, empty);
//                setGraphic(empty ? null : pane);
//            }
//        });
//
//        // Add columns to table
//        port_table.getColumns().addAll(numberCol, toCol, actionCol, fromCol, noteCol, actionsCol);
//
//        // Add selection listener
//        port_table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
//            if (newSelection != null) {
//                selectedRule = newSelection;
//                populateRuleForm(newSelection);
//            }
//        });
//    }
    // Thêm các biến FXML mới
    @FXML
    private AnchorPane ruleFormPane;
    @FXML
    private Label formTitleLabel;
    @FXML
    private TextField noteField;
    @FXML
    private Button addRuleButton;
    @FXML
    private Button backupButton;
    @FXML
    private Button restoreButton;
    @FXML
    private Button updateButton;

    private void populateRuleForm(PortRule rule) {
        toField.setText(rule.getTo());
        actionComboBox.setValue(rule.getAction());
        fromField.setText(rule.getFrom());
    }

//    private void editRule(PortRule rule) {
//        selectedRule = rule;
//        populateRuleForm(rule);
//        // Scroll to form or show edit panel
//        showRuleForm(true);
//    }
//
//    private void confirmDeleteRule(PortRule rule) {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Xác nhận xóa");
//        alert.setHeaderText("Xóa quy tắc tường lửa");
//        alert.setContentText("Bạn có chắc chắn muốn xóa quy tắc này?\n"
//                + "Dịch vụ/Port: " + rule.getTo() + "\n"
//                + "Hành động: " + rule.getAction() + "\n"
//                + "IP nguồn: " + rule.getFrom());
//
//        Optional<ButtonType> result = alert.showAndWait();
//        if (result.isPresent() && result.get() == ButtonType.OK) {
//            deleteRuleByNumber(rule);
//        }
//    }
    private void deleteRuleByNumber(PortRule rule) {
        try {
            if (rule.getRuleNumber() > 0) {
                // Delete the rule by number
                String deleteCommand = "echo y | sudo -S ufw delete " + rule.getRuleNumber();
                String result = sshUtil.executeCommand(deleteCommand);

                if (result.toLowerCase().contains("error") || result.toLowerCase().contains("failed")) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa quy tắc: " + result);
                    return;
                }

                // Update the table
                loadPortRules();

                // Clear form fields
                clearRuleForm();

                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Quy tắc đã được xóa!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xác định số thứ tự của quy tắc để xóa!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa quy tắc: " + e.getMessage());
        }
    }

    public void clearRuleForm() {
        actionComboBox.setValue("ALLOW");
        logCheckbox.setSelected(false);
        servicePortField.clear();
        protocolComboBox.setValue("any");
        sourceIPField.setText("any");
        destIPField.setText("any");
    }

    public void cancelAddRule() {
        ruleFormPane.setVisible(false);
    }

    public void loadPortRules() {
        try {
            // Get the rules from SSHUtil
            List<PortRule> rules = sshUtil.getNumberedFirewallRules();

            // Update the table
            port_table.getItems().clear();
            port_table.getItems().addAll(rules);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Lỗi",
                    "Không thể tải danh sách quy tắc: " + e.getMessage());
        }
    }

    // Trong phương thức initialize, thêm đoạn code sau
    public void initializeRuleForm() {
        // Khởi tạo các giá trị cho ComboBox
        protocolComboBox.getItems().addAll("any", "tcp", "udp", "icmp");

        // Đặt giá trị mặc định
        actionComboBox.setValue("ALLOW");
        protocolComboBox.setValue("any");
        logCheckbox.setSelected(false);

        // Đặt giá trị mặc định cho các trường
        sourceIPField.setText("any");
        destIPField.setText("any");
    }

    private void deleteRule(PortRule rule) {
        // Show confirmation dialog
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Xác nhận xóa");
        confirmDialog.setHeaderText("Xóa quy tắc tường lửa");
        confirmDialog.setContentText("Bạn có chắc chắn muốn xóa quy tắc này không?\n"
                + "To: " + rule.getTo() + "\n"
                + "Action: " + rule.getAction() + "\n"
                + "From: " + rule.getFrom());

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Create a task to delete the rule in background
            Task<Boolean> deleteTask = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    try {
                        return sshUtil.deleteRule(rule);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            };

            // Handle task completion
            deleteTask.setOnSucceeded(event -> {
                Boolean success = deleteTask.getValue();
                if (success) {

                    // Show success message
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thành công");
                    alert.setHeaderText(null);
                    alert.setContentText("Đã xóa quy tắc thành công!");
                    alert.showAndWait();

                    // Refresh the rules table
                    initializePortTable();
                    loadFirewallRules();

                } else {
                    // Show error message
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi");
                    alert.setHeaderText(null);
                    alert.setContentText("Không thể xóa quy tắc. Vui lòng thử lại!");
                    alert.showAndWait();
                }
            });

            // Handle task failure
            deleteTask.setOnFailed(event -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Đã xảy ra lỗi khi xóa quy tắc: " + deleteTask.getException().getMessage());
                alert.showAndWait();
            });

            // Run the task in a new thread
            new Thread(deleteTask).start();
        }
    }

    // Thêm sự kiện khi chọn một quy tắc trong bảng
    private void setupTableSelection() {
        port_table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedRule = newSelection;
                toField.setText(newSelection.getTo());
                actionComboBox.setValue(newSelection.getAction());
                fromField.setText(newSelection.getFrom());
            }
        });
    }

    public void addNewRule() {
        if (!validateRuleForm()) {
            return;
        }

        String newTo = toField.getText();
        String newAction = actionComboBox.getValue();
        String newFrom = fromField.getText();
        String newNote = noteField != null ? noteField.getText() : "";

        try {
            String newCommand;

            // Handle different rule formats
            if (newTo.contains("/")) {
                // Port/protocol rule (e.g., 22/tcp)
                String[] parts = newTo.split("/");
                if (parts.length != 2 || (!parts[1].equalsIgnoreCase("tcp") && !parts[1].equalsIgnoreCase("udp"))) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi",
                            "Định dạng cổng/giao thức không hợp lệ! Vui lòng nhập dạng 'cổng/giao thức' (ví dụ: 22/tcp hoặc 80/udp).");
                    return;
                }

                try {
                    Integer.parseInt(parts[0]); // Validate port is a number
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Cổng phải là một số hợp lệ!");
                    return;
                }

                newCommand = "sudo -S ufw " + newAction.toLowerCase() + " from " + newFrom
                        + " to any port " + parts[0] + " proto " + parts[1];
            } else if (newTo.contains("(v6)")) {
                // IPv6 rule
                newCommand = "sudo -S ufw " + newAction.toLowerCase() + " from " + newFrom + " to " + newTo;
            } else {
                // Application or other rule
                newCommand = "sudo -S ufw " + newAction.toLowerCase() + " from " + newFrom + " to any " + newTo;
            }

            // Add comment if provided
            if (!newNote.isEmpty()) {
                newCommand += " comment \"" + newNote + "\"";
            }

            // Execute the command
            String result = sshUtil.executeCommand(newCommand);

            // Check if there was an error
            if (result.toLowerCase().contains("error") || result.toLowerCase().contains("failed")) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm quy tắc: " + result);
                return;
            }

            // Update the table
            loadPortRules();

            // Clear form fields
            clearRuleForm();

            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Quy tắc đã được thêm!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm quy tắc: " + e.getMessage());
        }
    }

    private boolean validateRuleForm() {
        String newTo = toField.getText();
        String newAction = actionComboBox.getValue();
        String newFrom = fromField.getText();

        if (newTo.isEmpty() || newAction == null || newFrom.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng điền đầy đủ thông tin!");
            return false;
        }

        // Validate IP format for From field
        if (!newFrom.equals("Anywhere") && !newFrom.equals("any") && !validateIpAddress(newFrom)) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Địa chỉ IP nguồn không hợp lệ! Vui lòng nhập IP hoặc CIDR hợp lệ (ví dụ: 192.168.1.0/24).");
            return false;
        }

        return true;
    }

    private boolean validateIpAddress(String ip) {
        // Simple validation for IP or CIDR
        return ip.matches("^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(\\/(\\d{1,2}))?$")
                || ip.matches("^[a-zA-Z0-9.-]+$"); // Allow hostnames too
    }

    public void showRuleForm(boolean isEditing) {
        // Show the rule form panel
        if (ruleFormPane != null) {
            ruleFormPane.setVisible(true);
            formTitleLabel.setText(isEditing ? "Chỉnh sửa quy tắc" : "Thêm quy tắc mới");

            // Show save or update button based on editing state
            saveButton.setVisible(!isEditing);
            updateButton.setVisible(isEditing);
        }
    }

    public void hideRuleForm() {
        if (ruleFormPane != null) {
            ruleFormPane.setVisible(false);
            clearRuleForm();
        }
    }

    public void backupRules() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu tệp backup");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Backup Files", "*.rules"));
        fileChooser.setInitialFileName("ufw_backup.rules");

        File file = fileChooser.showSaveDialog(dashboard.getScene().getWindow());
        if (file != null) {
            try {
                // Get the rules
                String rules = sshUtil.executeCommand("sudo -S ufw status numbered");

                // Write to file
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(rules);
                }

                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã sao lưu quy tắc tường lửa vào: " + file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể sao lưu quy tắc: " + e.getMessage());
            }
        }
    }

    public void restoreRules() {
        // This would be more complex and would require parsing the backup file
        // and applying each rule individually
        showAlert(Alert.AlertType.INFORMATION, "Thông báo",
                "Tính năng khôi phục quy tắc từ file backup đang được phát triển.");
    }

    public void addNewRuleButtonClicked() {
        ruleFormPane.setVisible(true);
    }

    // Helper method for showing alerts
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public void toggleFirewall() {
    // Lưu trạng thái hiện tại của toggle button
    boolean isSelected = firewallToggle.isSelected();

    // Vô hiệu hóa nút trong khi đang xử lý
    firewallToggle.setDisable(true);

    // Thực thi lệnh trong một luồng riêng biệt
    new Thread(() -> {
        try {
            // Tạo lệnh UFW với tham số --force để tránh yêu cầu xác nhận
            String command = "sudo -S ufw --force " + (isSelected ? "enable" : "disable");

            // Thực thi lệnh
            String result = sshUtil.executeCommand(command);

            // Xử lý kết quả trong luồng UI
            Platform.runLater(() -> {
                // Kích hoạt lại nút
                firewallToggle.setDisable(false);
                
                // Cập nhật giao diện
                setupTable();
                loadApplications();
                initializePortTable();
                loadFirewallRules();

                // Kiểm tra kết quả cụ thể hơn
                boolean success = false;
                if (isSelected && result.toLowerCase().contains("enabled")) {
                    success = true;
                    // Khi tường lửa được BẬT, các form nên được ENABLE để người dùng có thể thêm/sửa quy tắc
                    serviceFormPane.setDisable(false);
                    ruleFormPane.setDisable(false);
                } else if (!isSelected && result.toLowerCase().contains("disabled")) {
                    success = true;
                    // Khi tường lửa bị TẮT, các form nên được DISABLE vì không thể thêm/sửa quy tắc
                    serviceFormPane.setDisable(true);
                    ruleFormPane.setDisable(true);
                }

                if (!success) {
                    // Nếu có lỗi, đặt lại trạng thái toggle button
                    firewallToggle.setSelected(!isSelected);
                    System.out.println("Lỗi: " + result);
                } else {
                    // Cập nhật style cho toggle button
                    firewallToggle.getStyleClass().removeAll("toggle-on", "toggle-off");
                    firewallToggle.getStyleClass().add(isSelected ? "toggle-on" : "toggle-off");
                    System.out.println("Thành công: Đã " + (isSelected ? "bật" : "tắt") + " tường lửa");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

            // Xử lý lỗi trong luồng UI
            Platform.runLater(() -> {
                // Đặt lại trạng thái toggle button
                firewallToggle.setSelected(!isSelected);
                firewallToggle.setDisable(false);

                System.out.println("Lỗi: " + e.getMessage());
            });
        }
    }).start();
}

    private void checkFirewallStatus() {
        new Thread(() -> {
            try {
                String statusResult = sshUtil.executeCommand("sudo -S ufw status");
                boolean isActive = statusResult.contains("Status: active");

                Platform.runLater(() -> {
                    // Cập nhật trạng thái toggle button theo trạng thái thực tế của tường lửa
                    firewallToggle.setSelected(isActive);
                    firewallToggle.getStyleClass().removeAll("toggle-on", "toggle-off");
                    firewallToggle.getStyleClass().add(isActive ? "toggle-on" : "toggle-off");
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Phương thức tạo nội dung popup
    private StackPane createPopupContent(Node content) {
        StackPane popupContent = new StackPane(content);
        popupContent.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-border-radius: 5;");
        popupContent.setPrefWidth(250);
        popupContent.setPrefHeight(80);
        return popupContent;
    }

    public void reloadFirewall() {
        reloadButton.setDisable(true);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                sshUtil.executeCommand("sudo -S ufw reload");
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    loadPortRules();
                    reloadButton.setDisable(false);
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Tường lửa đã được khởi động lại!");
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    reloadButton.setDisable(false);
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể khởi động lại tường lửa: " + getException().getMessage());
                });
            }
        };

        new Thread(task).start();
    }

    private void loadUsers() {
        // Clear existing cards
        userCardsPane.getChildren().clear();

        // Get users from system
        List<UbuntuUser> users = sshUtil.getSystemUsers();
        userList.setAll(users);

        // Create user cards
        for (UbuntuUser user : users) {
            createUserCard(user);
        }
    }

    private void createUserCard(UbuntuUser user) {
        // Create card container
        VBox card = new VBox(5);
        card.getStyleClass().add("user-card");
        card.setPadding(new Insets(10));
        card.setPrefWidth(200);
        card.setAlignment(Pos.CENTER);

        // Add border and background to the card
        card.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1px; "
                + "-fx-border-radius: 5px; -fx-background-color: white; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        // Create circular container for user icon
        StackPane iconContainer = new StackPane();
        iconContainer.setMinSize(64, 64);
        iconContainer.setMaxSize(64, 64);
        iconContainer.setStyle("-fx-background-color: #f2f2f2; "
                + "-fx-background-radius: 32; "
                + "-fx-border-radius: 32; "
                + "-fx-border-color: #e0e0e0; "
                + "-fx-border-width: 1;");

        // User icon inside circular container
        Label userIcon = new Label("👤");
        userIcon.setStyle("-fx-font-size: 32px;");
        iconContainer.getChildren().add(userIcon);

        // Username
        Label usernameLabel = new Label(user.getUsername());
        usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // Container for status labels (current user and sudo)
        HBox statusContainer = new HBox(5);
        statusContainer.setAlignment(Pos.CENTER);

        // Current user indicator
        Label currentUserLabel = new Label();
        if (user.isCurrentUser()) {
            currentUserLabel.setText("Current User");
            currentUserLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 12px;");
            statusContainer.getChildren().add(currentUserLabel);
        }

        // Sudo indicator
        Label sudoLabel = new Label();
        if (user.isSudo()) {
            sudoLabel.setText("Admin");
            sudoLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 12px;");
            statusContainer.getChildren().add(sudoLabel);
        }

        // Edit button
        Button editButton = new Button("Edit");
        editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; "
                + "-fx-cursor: hand; -fx-padding: 5 15; -fx-background-radius: 3;");
        editButton.setOnAction(e -> showEditUserPane(user));

        // Add all elements to card with appropriate spacing
        VBox.setMargin(iconContainer, new Insets(5, 0, 5, 0));
        VBox.setMargin(usernameLabel, new Insets(5, 0, 0, 0));
        VBox.setMargin(statusContainer, new Insets(0, 0, 5, 0));
        VBox.setMargin(editButton, new Insets(5, 0, 0, 0));

        card.getChildren().addAll(iconContainer, usernameLabel, statusContainer, editButton);

        // Add card to flow pane with margin
        FlowPane.setMargin(card, new Insets(10));
        userCardsPane.getChildren().add(card);
    }

    @FXML
    private void clearCreateUserForm() {
        // Show create user form
        newUsernameField.clear();
        newPasswordField.clear();
    }

    @FXML
    private void createNewUser() {
        String username = newUsernameField.getText();
        String password = newPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên người dùng và mật khẩu không được để trống");
            return;
        }

        // Create user
        boolean success = sshUtil.createUser(username, password);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Người dùng đã được tạo thành công");
            loadUsers(); // Reload user list
            newUsernameField.clear();
            newPasswordField.clear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tạo người dùng");
        }
    }

    private void showEditUserPane(UbuntuUser user) {
        currentEditingUser = user;
        editingUserLabel.setText("Đang chỉnh sửa: " + user.getUsername());
        changePasswordField.clear();
        sudoAccessCheckbox.setSelected(user.isSudo());

        // Disable delete and sudo toggle for current user and root
        deleteUserButton.setDisable(user.isCurrentUser() || user.getUsername().equals("root"));
        sudoAccessCheckbox.setDisable(user.isCurrentUser() || user.getUsername().equals("root"));

        editUserPane.setVisible(true);
    }

    @FXML
    public void saveUserChanges() {
        if (currentEditingUser == null) {
            return;
        }

        // Change password if provided
        String newPassword = changePasswordField.getText();
        if (!newPassword.isEmpty()) {
            sshUtil.changePassword(currentEditingUser.getUsername(), newPassword);
        }

        // Update sudo access
        boolean shouldHaveSudo = sudoAccessCheckbox.isSelected();
        if (shouldHaveSudo != currentEditingUser.isSudo()) {
            sshUtil.grantSudoAccess(currentEditingUser.getUsername(), shouldHaveSudo);
            currentEditingUser.setSudo(shouldHaveSudo);
        }

        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Người dùng đã được cập nhật thành công");
        loadUsers(); // Reload user list
        editUserPane.setVisible(false);
    }

    @FXML
    private void deleteUser() {
        if (currentEditingUser == null) {
            return;
        }

        // Confirm deletion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Xóa người dùng");
        alert.setContentText("Bạn có chắc chắn muốn xóa người dùng " + currentEditingUser.getUsername() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = sshUtil.deleteUser(currentEditingUser.getUsername());

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Người dùng đã được xóa thành công");
                loadUsers(); // Reload user list
                editUserPane.setVisible(false);
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa người dùng");
            }
        }
    }

    @FXML
    public void cancelUserEdit() {
        editUserPane.setVisible(false);
    }

    @FXML
    private TextField searchLogsField;
    @FXML
    private ComboBox<String> actionFilterComboBox;
    @FXML
    private ComboBox<String> protocolFilterComboBox;
    @FXML
    private Button refreshButton;
    @FXML
    private Button exportButton;

    @FXML
    private AnchorPane logsPane;
    @FXML
//    private Button refreshLogsButton;
//    @FXML
//    private Button exportLogsButton;
//    @FXML
    private TableView<FirewallLog> logsTable;
    @FXML
    private TableColumn<FirewallLog, String> timeColumn;
    @FXML
    private TableColumn<FirewallLog, String> actionColumn;
    @FXML
    private TableColumn<FirewallLog, String> sourceIPColumn;
    @FXML
    private TableColumn<FirewallLog, String> destIPColumn;
    @FXML
    private TableColumn<FirewallLog, String> protocolColumn;
    @FXML
    private TableColumn<FirewallLog, String> sourcePortColumn;
    @FXML
    private TableColumn<FirewallLog, String> destPortColumn;
    @FXML
    private TableColumn<FirewallLog, String> directionColumn;
    @FXML
    private TableColumn<FirewallLog, Void> detailsColumn;
    @FXML
    private ComboBox<Integer> pageComboBox;
    @FXML
    private Label totalRowsLabel;
    @FXML
    private ComboBox<Integer> rowsPerPageComboBox;
    @FXML
    private Button prevPageButton;
    @FXML
    private Button nextPageButton;

    @FXML
    private TextArea terminalOutput;

    @FXML
    private TextField commandInput;

    @FXML
    private Button sendButton;

    @FXML
    private Button clearButton;

    private ObservableList<FirewallLog> allLogs = FXCollections.observableArrayList();
    private ObservableList<FirewallLog> filteredLogs = FXCollections.observableArrayList();
    private int currentPage = 1;
    private int rowsPerPage = 20;

    public void sendCommand() {
        String command = commandInput.getText().trim();
        if (command.isEmpty()) {
            return;
        }

        terminalOutput.appendText("$ " + command + "\n");
        commandInput.clear();

        // Thực thi lệnh trong một luồng riêng biệt
        new Thread(() -> {
            try {
                String result;
                if (command.contains("sudo")) {
                    // Sử dụng phương thức mới cho lệnh sudo
                    result = sshUtil.executeSudoCommand(command);
                } else {
                    // Sử dụng phương thức thông thường cho lệnh không phải sudo
                    result = sshUtil.executeCommand2(command);
                }

                Platform.runLater(() -> {
                    terminalOutput.appendText(result + "\n");
                    // Cuộn xuống cuối
                    terminalOutput.positionCaret(terminalOutput.getText().length());
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    terminalOutput.appendText("Lỗi: " + e.getMessage() + "\n");
                    terminalOutput.positionCaret(terminalOutput.getText().length());
                });
            }
        }).start();
    }

    @FXML
    public void clearTerminal() {
        terminalOutput.clear();
    }

    public void saveRule() {
        try {

            // Kiểm tra các trường nhập liệu
            if (actionComboBox == null || actionComboBox.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn hành động (ALLOW/DENY)");
                return;
            }

            // Lấy giá trị từ các trường
            String action = actionComboBox.getValue();
            boolean logEnabled = logCheckbox != null && logCheckbox.isSelected();

            // Lấy giá trị cổng/dịch vụ
            String port = "";
            if (servicePortField != null) {
                port = servicePortField.getText().trim();
            }

            // Lấy giá trị giao thức
            String protocol = "any";
            if (protocolComboBox != null && protocolComboBox.getValue() != null) {
                protocol = protocolComboBox.getValue();
            }

            // Lấy giá trị IP nguồn
            String sourceIP = "any";
            if (sourceIPField != null) {
                sourceIP = sourceIPField.getText().trim();
                if (sourceIP.isEmpty()) {
                    sourceIP = "any";
                }
            }

            // Lấy giá trị IP đích
            String destIP = "any";
            if (destIPField != null) {
                destIP = destIPField.getText().trim();
                if (destIP.isEmpty()) {
                    destIP = "any";
                }
            }

            System.out.println("Thông tin quy tắc: " + action + ", log: " + logEnabled
                    + ", port: " + port + ", protocol: " + protocol
                    + ", sourceIP: " + sourceIP + ", destIP: " + destIP);

            // Tạo đối tượng PortRule mới
            PortRule newRule = new PortRule(action, logEnabled, port, protocol, sourceIP, destIP);

            // Thực thi lệnh UFW
            String command = newRule.toUFWCommand();
            System.out.println("Lệnh UFW: " + command);

            // Kiểm tra kết nối SSH
            if (sshUtil == null) {
                System.out.println("Lỗi: sshUtil là null");
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Kết nối SSH chưa được thiết lập");
                return;
            }

            // Thực thi lệnh qua SSH
            String result = sshUtil.executeCommand(command);
            System.out.println("Kết quả thực thi: " + result);

            if (result != null && (result.toLowerCase().contains("error") || result.toLowerCase().contains("failed"))) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm quy tắc: " + result);
            } else {

                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm quy tắc thành công!");
                // Refresh the rules table
                initializePortTable();
                loadFirewallRules();
                clearRuleForm();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Đã xảy ra lỗi: " + e.getMessage());
        }
    }

    private void initLogsUI() {
        // Khởi tạo các cột trong bảng
        timeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());

        // Thay đổi phần này: Tùy chỉnh cột hành động với màu sắc
        actionColumn.setCellValueFactory(cellData -> cellData.getValue().actionProperty());
        actionColumn.setCellFactory(column -> {
            return new TableCell<FirewallLog, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);

                        // Đặt màu sắc dựa trên hành động
                        if (item.equals("BLOCK")) {
                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        } else if (item.equals("ALLOW")) {
                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                        } else if (item.equals("AUDIT")) {
                            setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;"); // Màu vàng
                        } else {
                            setStyle("");
                        }
                    }
                }
            };
        });

        sourceIPColumn.setCellValueFactory(cellData -> cellData.getValue().sourceIPProperty());
        destIPColumn.setCellValueFactory(cellData -> cellData.getValue().destIPProperty());
        protocolColumn.setCellValueFactory(cellData -> cellData.getValue().protocolProperty());
        sourcePortColumn.setCellValueFactory(cellData -> cellData.getValue().sourcePortProperty());
        destPortColumn.setCellValueFactory(cellData -> cellData.getValue().destPortProperty());
        directionColumn.setCellValueFactory(cellData -> cellData.getValue().directionProperty());

        // Tạo cột nút xem chi tiết
        detailsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button detailButton = new Button("Chi tiết");

            {
                detailButton.setOnAction(event -> {
                    FirewallLog log = getTableView().getItems().get(getIndex());
                    showLogDetails(log);
                });

                detailButton.getStyleClass().add("detail-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(detailButton);
                }
            }
        });

        // Khởi tạo ComboBox cho bộ lọc
        actionFilterComboBox.getItems().addAll("Tất cả", "ALLOW", "BLOCK");
        actionFilterComboBox.setValue("Tất cả");

        protocolFilterComboBox.getItems().addAll("Tất cả", "TCP", "UDP", "ICMP");
        protocolFilterComboBox.setValue("Tất cả");

        // Khởi tạo ComboBox cho phân trang
        rowsPerPageComboBox.getItems().addAll(10, 20, 50, 100);
        rowsPerPageComboBox.setValue(20);

        // Thêm listener cho các bộ lọc và tìm kiếm
        searchLogsField.textProperty().addListener((obs, oldVal, newVal) -> filterLogs());
        actionFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> filterLogs());
        protocolFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> filterLogs());
        rowsPerPageComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            rowsPerPage = newVal;
            updatePageComboBox();
            showPage(1);
        });

        // Thêm sự kiện cho các nút
        refreshButton.setOnAction(e -> loadFirewallLogs());
        exportButton.setOnAction(e -> exportLogs());
        prevPageButton.setOnAction(e -> showPage(currentPage - 1));
        nextPageButton.setOnAction(e -> showPage(currentPage + 1));

        // Thêm listener cho ComboBox trang
        pageComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showPage(newVal);
            }
        });
    }

    private void loadFirewallLogs() {
        Task<List<FirewallLog>> task = new Task<>() {
            @Override
            protected List<FirewallLog> call() throws Exception {
                // Lấy logs từ SSH
                String result = sshUtil.executeCommand("sudo -S cat /var/log/ufw.log");
                return parseFirewallLogs(result);
            }
        };

        task.setOnSucceeded(e -> {
            allLogs.clear();
            allLogs.addAll(task.getValue());
            filterLogs();
            updatePageComboBox();
            showPage(1);
        });

        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setContentText("Không thể tải logs: " + task.getException().getMessage());
            alert.showAndWait();
        });

        new Thread(task).start();
    }

    private List<FirewallLog> parseFirewallLogs(String logContent) {
        List<FirewallLog> logs = new ArrayList<>();

        // Phân tích từng dòng log
        for (String line : logContent.split("\n")) {
            // Tìm các thông tin cần thiết từ dòng log
            if (line.contains("[UFW ")) {
                try {
                    // Lấy thời gian
                    String time = line.substring(0, 15).trim();

                    // Lấy hành động (ALLOW, BLOCK, AUDIT)
                    String action = "";
                    if (line.contains("[UFW ALLOW]")) {
                        action = "ALLOW";
                    } else if (line.contains("[UFW BLOCK]")) {
                        action = "BLOCK";
                    } else if (line.contains("[UFW AUDIT]")) {
                        action = "AUDIT";
                    } else {
                        continue; // Bỏ qua nếu không phải log UFW
                    }

                    // Phân tích IP và port
                    String sourceIP = extractSourceIP(line);
                    String destIP = extractDestIP(line);
                    String protocol = extractProtocol(line);
                    String sourcePort = extractSourcePort(line);
                    String destPort = extractDestPort(line);
                    String direction = extractDirection(line);

                    logs.add(new FirewallLog(time, action, sourceIP, destIP, protocol,
                            sourcePort, destPort, direction, line));
                } catch (Exception e) {
                    System.err.println("Không thể phân tích log: " + line);
                    e.printStackTrace();
                }
            }
        }

        return logs;
    }

// Các phương thức trích xuất thông tin từ log
    private String extractTime(String log) {
        // Ví dụ: Apr 15 17:25:35
        String[] parts = log.split(" ");
        if (parts.length >= 3) {
            return parts[0] + " " + parts[1] + " " + parts[2];
        }
        return "";
    }

    private String extractSourceIP(String log) {
        // Tìm SRC=x.x.x.x trong log
        int srcIndex = log.indexOf("SRC=");
        if (srcIndex != -1) {
            int spaceIndex = log.indexOf(" ", srcIndex);
            if (spaceIndex != -1) {
                return log.substring(srcIndex + 4, spaceIndex);
            }
        }
        return "";
    }

    private String extractDestIP(String log) {
        // Tìm DST=x.x.x.x trong log
        int dstIndex = log.indexOf("DST=");
        if (dstIndex != -1) {
            int spaceIndex = log.indexOf(" ", dstIndex);
            if (spaceIndex != -1) {
                return log.substring(dstIndex + 4, spaceIndex);
            }
        }
        return "";
    }

    private String extractProtocol(String log) {
        // Tìm PROTO=xxx trong log
        int protoIndex = log.indexOf("PROTO=");
        if (protoIndex != -1) {
            int spaceIndex = log.indexOf(" ", protoIndex);
            if (spaceIndex != -1) {
                return log.substring(protoIndex + 6, spaceIndex);
            }
        }
        return "";
    }

    private String extractSourcePort(String log) {
        // Tìm SPT=xxx trong log
        int sptIndex = log.indexOf("SPT=");
        if (sptIndex != -1) {
            int spaceIndex = log.indexOf(" ", sptIndex);
            if (spaceIndex != -1) {
                return log.substring(sptIndex + 4, spaceIndex);
            }
        }
        return "";
    }

    private String extractDestPort(String log) {
        // Tìm DPT=xxx trong log
        int dptIndex = log.indexOf("DPT=");
        if (dptIndex != -1) {
            int spaceIndex = log.indexOf(" ", dptIndex);
            if (spaceIndex != -1) {
                return log.substring(dptIndex + 4, spaceIndex);
            }
        }
        return "";
    }

    private String extractDirection(String log) {
        if (log.contains("IN=") && !log.contains("OUT=")) {
            return "IN";
        } else if (log.contains("OUT=") && !log.contains("IN=")) {
            return "OUT";
        } else {
            return "";
        }
    }

//    private void filterLogs() {
//        String searchText = searchLogsField.getText().toLowerCase();
//        String actionFilter = actionFilterComboBox.getValue();
//        String protocolFilter = protocolFilterComboBox.getValue();
//
//        filteredLogs.clear();
//
//        for (FirewallLog log : allLogs) {
//            boolean matchesSearch = searchText.isEmpty()
//                    || log.getSourceIP().toLowerCase().contains(searchText)
//                    || log.getDestIP().toLowerCase().contains(searchText)
//                    || log.getSourcePort().toLowerCase().contains(searchText)
//                    || log.getDestPort().toLowerCase().contains(searchText);
//
//            boolean matchesAction = "Tất cả".equals(actionFilter)
//                    || log.getAction().equals(actionFilter);
//
//            boolean matchesProtocol = "Tất cả".equals(protocolFilter)
//                    || log.getProtocol().equals(protocolFilter);
//
//            if (matchesSearch && matchesAction && matchesProtocol) {
//                filteredLogs.add(log);
//            }
//        }
//
//        updatePageComboBox();
//        showPage(1);
//    }
    private void updatePageComboBox() {
        int totalPages = calculateTotalPages();

        // Lưu giá trị trang hiện tại
        Integer currentPageValue = pageComboBox.getValue();

        // Cập nhật danh sách trang
        pageComboBox.getItems().clear();
        for (int i = 1; i <= totalPages; i++) {
            pageComboBox.getItems().add(i);
        }

        // Đặt lại giá trị trang hiện tại nếu hợp lệ
        if (currentPageValue != null && currentPageValue <= totalPages) {
            pageComboBox.setValue(currentPageValue);
        } else {
            pageComboBox.setValue(1);
        }

        // Cập nhật trạng thái nút điều hướng
        prevPageButton.setDisable(currentPage <= 1);
        nextPageButton.setDisable(currentPage >= totalPages);

        // Cập nhật tổng số dòng
        totalRowsLabel.setText(String.valueOf(filteredLogs.size()));
    }

    private int calculateTotalPages() {
        if (filteredLogs.isEmpty()) {
            return 1;
        }
        return (int) Math.ceil((double) filteredLogs.size() / rowsPerPage);
    }
    // Thêm phương thức hiển thị trang

    private void showPage(int pageNumber) {
        int totalPages = calculateTotalPages();

        // Kiểm tra trang hợp lệ
        if (pageNumber < 1) {
            pageNumber = 1;
        } else if (pageNumber > totalPages) {
            pageNumber = totalPages;
        }

        currentPage = pageNumber;

        // Cập nhật ComboBox trang
        if (pageComboBox.getValue() == null || pageComboBox.getValue() != pageNumber) {
            pageComboBox.setValue(pageNumber);
        }

        // Tính toán chỉ số bắt đầu và kết thúc
        int fromIndex = (pageNumber - 1) * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, filteredLogs.size());

        // Cập nhật dữ liệu hiển thị
        ObservableList<FirewallLog> pageData = FXCollections.observableArrayList();
        if (!filteredLogs.isEmpty() && fromIndex < filteredLogs.size()) {
            pageData.addAll(filteredLogs.subList(fromIndex, toIndex));
        }

        // Cập nhật bảng
        logsTable.setItems(pageData);

        // Cập nhật trạng thái nút điều hướng
        prevPageButton.setDisable(pageNumber <= 1);
        nextPageButton.setDisable(pageNumber >= totalPages);

    }

//    private void showPage(int page) {
//        int totalPages = (int) Math.ceil((double) filteredLogs.size() / rowsPerPage);
//
//        if (page < 1 || page > totalPages) {
//            return;
//        }
//
//        currentPage = page;
//
//        int fromIndex = (page - 1) * rowsPerPage;
//        int toIndex = Math.min(fromIndex + rowsPerPage, filteredLogs.size());
//
//        ObservableList<FirewallLog> pageItems = FXCollections.observableArrayList(
//                filteredLogs.subList(fromIndex, toIndex));
//
//        logsTable.setItems(pageItems);
//
//        // Thêm phần này: Áp dụng CSS cho từng hàng dựa trên hành động
//        logsTable.setRowFactory(tv -> {
//            TableRow<FirewallLog> row = new TableRow<FirewallLog>() {
//                @Override
//                protected void updateItem(FirewallLog item, boolean empty) {
//                    super.updateItem(item, empty);
//
//                    // Xóa tất cả các class trước đó
//                    getStyleClass().removeAll("allow", "block", "audit");
//
//                    if (item == null || empty) {
//                        setStyle("");
//                    } else {
//                        // Áp dụng class dựa trên hành động
//                        if ("ALLOW".equals(item.getAction())) {
//                            getStyleClass().add("allow");
//                        } else if ("BLOCK".equals(item.getAction())) {
//                            getStyleClass().add("block");
//                        } else if ("AUDIT".equals(item.getAction())) {
//                            getStyleClass().add("audit");
//                        }
//                    }
//                }
//            };
//            return row;
//        });
//
//        // Cập nhật trạng thái nút
//        prevPageButton.setDisable(page == 1);
//        nextPageButton.setDisable(page == totalPages);
//    }
    // Phương thức để tạo dữ liệu mẫu cho việc kiểm tra
    private void loadSampleLogs() {
        allLogs.clear();

        // Thêm một số log mẫu với cả ALLOW, BLOCK và AUDIT
        allLogs.add(new FirewallLog("Apr 16 16:46:55", "ALLOW", "fe80:0000:0000:0000", "ff02:0000:0000:0000",
                "UDP", "5353", "5353", "IN", "Sample ALLOW log"));

        allLogs.add(new FirewallLog("Apr 16 16:46:55", "AUDIT", "fe80:0000:0000:0000", "ff02:0000:0000:0000",
                "UDP", "5353", "5353", "IN", "Sample AUDIT log"));

        allLogs.add(new FirewallLog("Apr 16 16:46:55", "BLOCK", "10.10.32.104", "224.0.0.251",
                "UDP", "5353", "5353", "IN", "Sample BLOCK log"));

        // Cập nhật bảng
        filterLogs();
        updatePageComboBox();
        showPage(1);
    }

    private void showLogDetails(FirewallLog log) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết Log");
        alert.setHeaderText("Thông tin chi tiết");

        // Tạo TextArea để hiển thị log gốc
        TextArea textArea = new TextArea(log.getRawLog());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(550);
        textArea.setPrefHeight(200);

        // Tạo GridPane để hiển thị thông tin chi tiết
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        int row = 0;
        grid.add(new Label("Thời gian:"), 0, row);
        grid.add(new Label(log.getTime()), 1, row++);

        grid.add(new Label("Hành động:"), 0, row);
        grid.add(new Label(log.getAction()), 1, row++);

        grid.add(new Label("IP nguồn:"), 0, row);
        grid.add(new Label(log.getSourceIP()), 1, row++);

        grid.add(new Label("IP đích:"), 0, row);
        grid.add(new Label(log.getDestIP()), 1, row++);

        grid.add(new Label("Giao thức:"), 0, row);
        grid.add(new Label(log.getProtocol()), 1, row++);

        grid.add(new Label("Cổng nguồn:"), 0, row);
        grid.add(new Label(log.getSourcePort()), 1, row++);

        grid.add(new Label("Cổng đích:"), 0, row);
        grid.add(new Label(log.getDestPort()), 1, row++);

        grid.add(new Label("Chiều:"), 0, row);
        grid.add(new Label(log.getDirection()), 1, row++);

        grid.add(new Label("Log gốc:"), 0, row);
        grid.add(textArea, 0, ++row, 2, 1);

        alert.getDialogPane().setContent(grid);
        alert.getDialogPane().setPrefWidth(600);

        alert.showAndWait();
    }

    // In your initialize method or a separate method for setting up the logs UI
    private void setupLogsFilters() {
        // Set up action filter
        ObservableList<String> actionOptions = FXCollections.observableArrayList(
                "Tất cả", "ALLOW", "BLOCK", "AUDIT"
        );
        actionFilterComboBox.setItems(actionOptions);
        actionFilterComboBox.setValue("Tất cả");

        // Set up protocol filter
        ObservableList<String> protocolOptions = FXCollections.observableArrayList(
                "Tất cả", "TCP", "UDP", "ICMP"
        );
        protocolFilterComboBox.setItems(protocolOptions);
        protocolFilterComboBox.setValue("Tất cả");
    }

// Phương thức lọc logs
    private void filterLogs() {
        String actionFilter = actionFilterComboBox.getValue();
        String protocolFilter = protocolFilterComboBox.getValue();
        String searchText = searchLogsField.getText().trim().toLowerCase();

        filteredLogs.clear();

        for (FirewallLog log : allLogs) {
            boolean includeLog = true;

            // Lọc theo hành động
            if (!"Tất cả".equals(actionFilter)) {
                if (!log.getAction().equals(actionFilter)) {
                    includeLog = false;
                }
            }

            // Lọc theo giao thức
            if (includeLog && !"Tất cả".equals(protocolFilter)) {
                if (!log.getProtocol().equals(protocolFilter)) {
                    includeLog = false;
                }
            }

            // Lọc theo IP (nguồn hoặc đích)
            if (includeLog && !searchText.isEmpty()) {
                boolean matchesSourceIP = log.getSourceIP().toLowerCase().contains(searchText);
                boolean matchesDestIP = log.getDestIP().toLowerCase().contains(searchText);

                if (!matchesSourceIP && !matchesDestIP) {
                    includeLog = false;
                }
            }

            // Thêm vào danh sách lọc nếu thỏa mãn tất cả điều kiện
            if (includeLog) {
                filteredLogs.add(log);
            }
        }

        // Cập nhật hiển thị
        updateLogsTable();
    }

    // Phương thức cập nhật bảng logs
    private void updateLogsTable() {
        // Cập nhật TableView với dữ liệu đã lọc
        logsTable.getItems().setAll(filteredLogs);

        // Áp dụng CSS class dựa trên loại hành động
        logsTable.setRowFactory(tv -> {
            TableRow<FirewallLog> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null) {
                    row.getStyleClass().removeAll("allow", "block", "audit");
                    if ("ALLOW".equals(newItem.getAction())) {
                        row.getStyleClass().add("allow");
                    } else if ("BLOCK".equals(newItem.getAction())) {
                        row.getStyleClass().add("block");
                    } else if ("AUDIT".equals(newItem.getAction())) {
                        row.getStyleClass().add("audit");
                    }
                }
            });
            return row;
        });
    }

    @FXML
    public void exportLogs() {
        try {
            // Tạo FileChooser để chọn vị trí lưu file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Lưu file logs");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                // Sử dụng OutputStreamWriter với UTF-8 để tránh lỗi font
                try (OutputStreamWriter writer = new OutputStreamWriter(
                        new FileOutputStream(file), StandardCharsets.UTF_8)) {

                    // Thêm BOM (Byte Order Mark) để Excel nhận diện UTF-8
                    writer.write('\uFEFF');

                    // Viết header
                    writer.write("Thời gian,Hành động,IP nguồn,IP đích,Giao thức,Cổng nguồn,Cổng đích,Chiều\n");

                    // Viết dữ liệu
                    for (FirewallLog log : allLogs) {
                        writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                                log.getTime(),
                                log.getAction(),
                                log.getSourceIP(),
                                log.getDestIP(),
                                log.getProtocol(),
                                log.getSourcePort(),
                                log.getDestPort(),
                                log.getDirection()));
                    }

                    writer.flush();
                }

                // Hiển thị thông báo thành công
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thành công");
                alert.setHeaderText(null);
                alert.setContentText("Đã xuất logs thành công!");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();

            // Hiển thị thông báo lỗi
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Không thể xuất logs: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void initializeLoggingCheckbox() {
        try {
            // Kiểm tra trạng thái logging thực tế
            boolean loggingEnabled = sshUtil.isFirewallLoggingEnabled();

            // Cập nhật UI trong thread chính
            Platform.runLater(() -> {
                // Tạm thời xóa listener để tránh trigger toggleLogging()
                loggingCheckbox.setOnAction(null);

                // Cập nhật trạng thái checkbox
                loggingCheckbox.setSelected(loggingEnabled);

                // Khôi phục listener
                loggingCheckbox.setOnAction(event -> toggleLogging());
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Không thể kiểm tra trạng thái logging: " + e.getMessage());
        }
    }

    public void updateLoggingStatus() {
        try {
            boolean loggingEnabled = sshUtil.isFirewallLoggingEnabled();

            Platform.runLater(() -> {
                loggingCheckbox.setOnAction(null);
                loggingCheckbox.setSelected(loggingEnabled);
                loggingCheckbox.setOnAction(event -> toggleLogging());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toggleLogging() {
        boolean enableLogging = loggingCheckbox.isSelected();

        // Show confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Firewall Logging");
        alert.setHeaderText(enableLogging ? "Bật ghi log Firewall?" : "Tắt ghi log Firewall?");
        alert.setContentText("Bạn có chắc chắn muốn " + (enableLogging ? "bật" : "tắt") + " ghi log firewall?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Execute the command
            boolean success = sshUtil.setFirewallLogging(enableLogging);

            if (success) {
                // Gọi lại hàm khởi tạo để cập nhật trạng thái thực tế
                initializeLoggingCheckbox();

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Thành công");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Logging đã được bật!.");
                successAlert.showAndWait();
            } else {
                // Gọi lại hàm khởi tạo để đảm bảo hiển thị đúng trạng thái thực tế
                initializeLoggingCheckbox();

                Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
                errorAlert.setTitle("Thành công");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Logging đã được tắt!");
                errorAlert.showAndWait();
            }
        } else {
            // User canceled, revert the checkbox state by reinitializing
            initializeLoggingCheckbox();
        }
    }

    private boolean terminalInitialized = false;

    @FXML
    private AnchorPane terminalPane;
    @FXML
    private WebView terminalWebView;

    @FXML
    private AnchorPane updateNotificationPane;
    @FXML
    private TextArea updateDetailsTextArea;
    @FXML
    private Button backButton;
    @FXML
    private AnchorPane updateNotificationPane1;
    @FXML
    private TextArea updateDetailsTextArea1;
    @FXML
    private Button backButton1;
    @FXML
    private AnchorPane updateNotificationPane2;
    @FXML
    private TextArea updateDetailsTextArea2;
    @FXML
    private Button backButton2;

    public void handleBackButton() {
        // Ẩn pane thông báo
        updateNotificationPane1.setVisible(false);
        updateNotificationPane2.setVisible(false);
        updateNotificationPane.setVisible(false);
        // Mặc định hiển thị overview nếu không có pane nào được lưu
        overviewPane.setVisible(true);
    }

    public void switchForm(ActionEvent event) {
        // Ẩn tất cả các pane trước khi hiển thị pane mới

        overviewPane.setVisible(false);
        all_firewall_anchor.setVisible(false);
        qldv_firewall.setVisible(false);
        table_quytac.setVisible(false);
        userManagementPane.setVisible(false);
        logsPane.setVisible(false);
        terminalPane.setVisible(false);
        updateNotificationPane2.setVisible(false);
        updateNotificationPane1.setVisible(false);
        updateNotificationPane.setVisible(false);

        if (event.getSource() == qldv_menu) {
            all_firewall_anchor.setVisible(true);
            qldv_firewall.setVisible(true);
            setupTable();
            loadApplications();
        } else if (event.getSource() == bang_quy_tac_menu) {
            all_firewall_anchor.setVisible(true);
            table_quytac.setVisible(true);
            initializePortTable();
            loadFirewallRules();
        } else if (event.getSource() == tong_quan) {
            overviewPane.setVisible(true);
            loadSystemInfo();
            setupTable();
            loadApplications();
            initializePortTable();
            loadFirewallRules();
        } else if (event.getSource() == tai_khoan) {
            userManagementPane.setVisible(true);
            loadUsers();
        } else if (event.getSource() == logs) {
            logsPane.setVisible(true);
            initializeLoggingCheckbox();
            loadFirewallLogs();
        } else if (event.getSource() == terminal) {
            terminalPane.setVisible(true);
        } else if (event.getSource() == service_ssh) {
            updateNotificationPane2.setVisible(true);
        } else if (event.getSource() == service_http) {
            updateNotificationPane1.setVisible(true);
        } else if (event.getSource() == service_ftp) {
            updateNotificationPane.setVisible(true);
        }
    }

    private double x = 0;
    private double y = 0;

    public void signOut() {
        try {
            // Đóng kết nối SSH hiện tại
            if (sshUtil != null) {
                sshUtil.disconnect();
            }

            // Đóng cửa sổ Dashboard hiện tại
            Stage currentStage = (Stage) signOutButton.getScene().getWindow();
            currentStage.close();

            // Mở lại giao diện đăng nhập
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML_Login.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            Scene scene = new Scene(root);

            // Thiết lập sự kiện kéo thả cho cửa sổ
            root.setOnMousePressed(e -> {
                x = e.getSceneX();
                y = e.getSceneY();
            });
            root.setOnMouseDragged(e -> {
                loginStage.setX(e.getScreenX() - x);
                loginStage.setY(e.getScreenY() - y);
            });

            loginStage.initStyle(StageStyle.TRANSPARENT);
            loginStage.setScene(scene);
            loginStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setContentText("Không thể quay lại màn hình đăng nhập: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (userLabel != null) {
            userLabel.setText(FXML_Login_Controller.username);
        }
        sshUtil = new SSHUtil(FXML_Login_Controller.ipAddress,
                FXML_Login_Controller.username,
                FXML_Login_Controller.sudoPassword,
                FXML_Login_Controller.portNumber);
        try {
            sshUtil.connect();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setContentText("Không thể kết nối đến server SSH!");
            alert.showAndWait();
            return;
        }

        // Khởi tạo ComboBox với các giá trị ALLOW và DENY
        actionComboBox.setItems(FXCollections.observableArrayList("ALLOW", "DENY"));

        // Kiểm tra trạng thái tường lửa khi khởi tạo
        try {
            String statusOutput = sshUtil.executeCommand("sudo -S ufw status");
            boolean isActive = statusOutput.contains("Status: active");
            firewallToggle.setSelected(isActive);
            firewallContent.setVisible(isActive);
            if (isActive) {
                loadFirewallRules();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setContentText("Không thể kiểm tra trạng thái tường lửa: " + e.getMessage());
            alert.showAndWait();
            // Nếu không kiểm tra được trạng thái, mặc định tắt
            firewallToggle.setSelected(false);
            firewallContent.setVisible(false);
        }

        overviewPane.setVisible(true);
        userManagementPane.setVisible(false);
        editUserPane.setVisible(false);
        all_firewall_anchor.setVisible(false);
        qldv_firewall.setVisible(false);
        table_quytac.setVisible(false);
        logsPane.setVisible(false);

        // Khởi động cập nhật thông tin hệ thống
        loadSystemInfo();
        initializeRuleForm();
        setupTable();
        loadApplications();
        initializePortTable();
        loadFirewallRules();
        initLogsUI();
        setupLogsFilters();
        initializeServiceForm();
    }
}
