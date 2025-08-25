package control_ubuntu_firewall;

import com.jcraft.jsch.*;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SSHUtil {

    private String host;
    private String username;
    private String password;
    private int port;
    private Session session;

    public String executeCommand2(String command) throws JSchException, IOException {
        if (session == null || !session.isConnected()) {
            connect();
        }

        ChannelExec channel = (ChannelExec) session.openChannel("exec");

        // Sửa đổi lệnh sudo để sử dụng -S và tự động cung cấp mật khẩu
        if (command.contains("sudo") && !command.contains("sudo -S")) {
            command = command.replace("sudo", "sudo -S");
        }

        channel.setCommand(command);
        channel.setInputStream(null);

        // Lấy cả output và error stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        channel.setOutputStream(outputStream);

        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        channel.setErrStream(errorStream);

        // Chuẩn bị input stream để gửi mật khẩu
        OutputStream in = channel.getOutputStream();

        channel.connect();

        // Gửi mật khẩu nếu lệnh dùng sudo
        if (command.contains("sudo -S")) {
            in.write((password + "\n").getBytes());
            in.flush();
        }

        // Đợi lệnh hoàn thành
        while (!channel.isClosed()) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                // Ignore
            }
        }

        channel.disconnect();

        // Kết hợp output và error
        String output = outputStream.toString();
        String error = errorStream.toString();

        // Loại bỏ thông báo "sudo: a password is required"
        String result = output + error;
        result = result.replaceAll("sudo: a password is required", "");
        result = result.replaceAll("sudo: a terminal is required to read the password; either use the -S option to read from standard input or configure an askpass helper", "");

        return result.trim();
    }

    public String executeSudoCommand(String command) throws Exception {
        if (session == null || !session.isConnected()) {
            connect();
        }

        // Đảm bảo lệnh bắt đầu bằng sudo -S
        if (!command.startsWith("sudo")) {
            command = "sudo -S " + command;
        } else if (!command.contains("-S")) {
            command = command.replace("sudo", "sudo -S");
        }

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);

        // Thiết lập các luồng
        OutputStream in = channel.getOutputStream();
        InputStream out = channel.getInputStream();
        InputStream err = channel.getErrStream();

        channel.connect();

        // Gửi mật khẩu
        in.write((password + "\n").getBytes());
        in.flush();

        // Đọc kết quả
        StringBuilder output = new StringBuilder();
        byte[] tmp = new byte[1024];

        while (true) {
            while (out.available() > 0) {
                int i = out.read(tmp, 0, 1024);
                if (i < 0) {
                    break;
                }
                output.append(new String(tmp, 0, i));
            }

            while (err.available() > 0) {
                int i = err.read(tmp, 0, 1024);
                if (i < 0) {
                    break;
                }
                output.append(new String(tmp, 0, i));
            }

            if (channel.isClosed()) {
                if (out.available() > 0 || err.available() > 0) {
                    continue;
                }
                break;
            }

            try {
                Thread.sleep(100);
            } catch (Exception e) {
                // Ignore
            }
        }

        channel.disconnect();

        // Loại bỏ thông báo về mật khẩu
        String result = output.toString();
        result = result.replaceAll("\\[sudo\\] password for.*?\\s*", "");

        return result.trim();
    }

    // Add this method to get the current session
    public Session getSession() {
        return this.session;
    }

    // Add this method to get the host
    public String getHost() {
        return this.host;
    }

    public List<PortRule> getNumberedFirewallRules() {
        List<PortRule> rules = new ArrayList<>();
        try {
            // Use sudo ufw status numbered to get the rules with numbers

            String command = "sudo -S ufw status numbered";
            String output = executeCommand(command);

            // Parse the output
            String[] lines = output.split("\n");
            boolean startParsing = false;

            for (String line : lines) {
                // Skip header lines until we find the column headers
                if (line.contains("To") && line.contains("Action") && line.contains("From")) {
                    startParsing = true;
                    continue;
                }

                // Skip lines until we find the headers
                if (!startParsing) {
                    continue;
                }

                // Skip empty lines or lines without rule numbers
                if (line.trim().isEmpty() || !line.trim().startsWith("[")) {
                    continue;
                }

                try {
                    // Extract the rule number
                    int startIndex = line.indexOf("[") + 1;
                    int endIndex = line.indexOf("]");
                    int ruleNumber = Integer.parseInt(line.substring(startIndex, endIndex).trim());

                    // Parse the rule info - the format is different from what we expected
                    String ruleInfo = line.substring(endIndex + 1).trim();

                    // Split by multiple spaces to separate columns
                    String[] parts = ruleInfo.split("\\s+");

                    if (parts.length >= 3) {
                        // The first column is "To" (service/port)
                        StringBuilder toBuilder = new StringBuilder();
                        int i = 0;
                        while (i < parts.length && !parts[i].equals("ALLOW") && !parts[i].equals("DENY")
                                && !parts[i].equals("REJECT") && !parts[i].equals("LIMIT")) {
                            toBuilder.append(parts[i]).append(" ");
                            i++;
                        }
                        String to = toBuilder.toString().trim();

                        // The next part is the action (ALLOW/DENY)
                        String action = "";
                        if (i < parts.length) {
                            if (parts[i].equals("ALLOW") || parts[i].equals("DENY")
                                    || parts[i].equals("REJECT") || parts[i].equals("LIMIT")) {
                                // Check if it's followed by "IN" or "OUT"
                                if (i + 1 < parts.length && (parts[i + 1].equals("IN") || parts[i + 1].equals("OUT"))) {
                                    action = parts[i] + " " + parts[i + 1];
                                    i += 2;
                                } else {
                                    action = parts[i];
                                    i++;
                                }
                            }
                        }

                        // The rest is the "From" (source)
                        StringBuilder fromBuilder = new StringBuilder();
                        while (i < parts.length) {
                            fromBuilder.append(parts[i]).append(" ");
                            i++;
                        }
                        String from = fromBuilder.toString().trim();

                        // Create the PortRule object with the correct order of fields
                        PortRule rule = new PortRule(to, action, from);
                        rule.setRuleNumber(ruleNumber);
                        rules.add(rule);
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid lines
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rules;
    }

    public boolean addFirewallRuleWithFullSyntax(String command) {
        try {
            // Thực thi lệnh
            String result = executeCommand(command);

            // Kiểm tra kết quả
            if (result.toLowerCase().contains("error") || result.toLowerCase().contains("failed")) {
                System.err.println("Thêm quy tắc tường lửa thất bại: " + result);
                return false;
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRule(PortRule rule) {
        try {
            // Lấy số thứ tự của quy tắc
            int ruleNumber = rule.getRuleNumber();

            // Sử dụng số thứ tự để xóa quy tắc thay vì cố gắng tạo lại lệnh
            String command = "sudo -S ufw --force delete " + ruleNumber;

            // Thực thi lệnh và kiểm tra kết quả
            String result = executeCommand(command);

            // Kiểm tra kết quả
            if (result.toLowerCase().contains("error") || result.toLowerCase().contains("failed")) {
                System.err.println("Failed to delete rule: " + result);
                return false;
            }

//        // Thêm lệnh reload để đảm bảo thay đổi được áp dụng
//        executeCommand("sudo -S ufw reload");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

// Add a new method to execute commands with timeout
    public String executeCommandWithTimeout(String command, int timeoutSeconds) {
        StringBuilder outputBuffer = new StringBuilder();

        try {
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            // Set up streams
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();
            InputStream err = ((ChannelExec) channel).getErrStream();

            // Connect with timeout
            channel.connect();

            // Write password if needed
            if (command.contains("sudo -S")) {
                out.write((password + "\n").getBytes());
                out.flush();
            }

            // Read with timeout
            long startTime = System.currentTimeMillis();
            byte[] tmp = new byte[1024];

            while (true) {
                // Check timeout
                if ((System.currentTimeMillis() - startTime) > timeoutSeconds * 1000) {
                    channel.disconnect();
                    return "ERROR: Command execution timed out after " + timeoutSeconds + " seconds";
                }

                // Check if data is available
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    outputBuffer.append(new String(tmp, 0, i));
                }

                // Check error stream
                while (err.available() > 0) {
                    int i = err.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    outputBuffer.append(new String(tmp, 0, i));
                }

                // Check if channel is closed
                if (channel.isClosed()) {
                    if (in.available() > 0 || err.available() > 0) {
                        continue;
                    }
                    break;
                }

                // Small delay to prevent CPU hogging
                try {
                    Thread.sleep(100);
                } catch (Exception ee) {
                    // Ignore
                }
            }

            channel.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }

        return outputBuffer.toString();
    }

    public boolean isFirewallLoggingEnabled() {
        try {
            // Lấy thông tin cấu hình UFW
            String command = "sudo -S grep -i 'LOGLEVEL=' /etc/ufw/ufw.conf";
            String result = executeCommand(command);

            // Kiểm tra kết quả
            if (result.toLowerCase().contains("loglevel=off")) {
                return false;
            } else {
                // Nếu LOGLEVEL là bất kỳ giá trị nào khác (low, medium, high, full), logging đang bật
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Mặc định trả về false nếu có lỗi
            return false;
        }
    }

    public boolean setFirewallLogging(boolean enable) {
        try {
            // Command to enable or disable logging
            String command = "sudo -S ufw logging " + (enable ? "on" : "off");
            String result = executeCommand(command);

            // Check if the command was successful
            if (result.toLowerCase().contains("error") || result.toLowerCase().contains("failed")) {
                System.err.println("Firewall logging change failed: " + result);
                return false;
            }
            
            // Kiểm tra lại trạng thái thực tế sau khi thay đổi
            return isFirewallLoggingEnabled();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getFirewallLogs() {
        try {
            // Lấy logs từ file ufw.log
            String command = "sudo -S cat /var/log/ufw.log";
            return executeCommand(command);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getFirewallLogs(int lines) {
        try {
            // Lấy n dòng logs cuối cùng
            String command = "sudo -S tail -n " + lines + " /var/log/ufw.log";
            return executeCommand(command);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public List<UbuntuUser> getSystemUsers() {
        List<UbuntuUser> users = new ArrayList<>();
        String currentUser = username; // Lưu username hiện tại

        try {
            // Lấy danh sách người dùng từ /etc/passwd, loại bỏ các tài khoản hệ thống
            String command = "cat /etc/passwd | grep -v nologin | grep -v false | cut -d: -f1,6,7";
            String result = executeCommand(command);

            // Lấy danh sách người dùng có quyền sudo
            String sudoersCommand = "sudo -S cat /etc/sudoers.d/* /etc/sudoers 2>/dev/null | grep -v '#' | grep 'ALL=(ALL:ALL)' | cut -d' ' -f1";
            String sudoersResult = executeCommand(sudoersCommand);
            Set<String> sudoUsers = new HashSet<>(Arrays.asList(sudoersResult.split("\n")));

            // Phân tích kết quả và tạo đối tượng UbuntuUser
            for (String line : result.split("\n")) {
                String[] parts = line.split(":");
                if (parts.length >= 3) {
                    String user = parts[0];
                    String home = parts[1];
                    String shell = parts[2];
                    boolean isSudo = sudoUsers.contains(user) || user.equals("root");
                    boolean isCurrentUser = user.equals(currentUser);

                    users.add(new UbuntuUser(user, home, shell, isSudo, isCurrentUser));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public boolean createUser(String newUsername, String password) {
        try {
            // Tạo người dùng mới với thư mục home và shell bash
            String createCommand = "sudo -S useradd -m -s /bin/bash " + newUsername;
            executeCommand(createCommand);

            // Đặt mật khẩu cho người dùng mới
            String passwordCommand = "echo '" + newUsername + ":" + password + "' | sudo -S chpasswd";
            executeCommand(passwordCommand);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean changePassword(String username, String newPassword) {
        try {
            // Sử dụng phương pháp đơn giản nhất: echo newpassword | sudo passwd username --stdin
            // Đối với Ubuntu, tùy chọn --stdin có thể không được hỗ trợ, nên sử dụng chpasswd

            // Escape các ký tự đặc biệt trong mật khẩu
            String escapedPassword = newPassword.replace("'", "'\\''");

            // Sử dụng chpasswd để thay đổi mật khẩu
            String command = "echo '" + username + ":" + escapedPassword + "' | sudo -S chpasswd";

            // Thực thi lệnh
            String result = executeCommand(command);

            // Kiểm tra kết quả
            if (result.toLowerCase().contains("error") || result.toLowerCase().contains("failed")) {
                System.err.println("Password change failed: " + result);
                return false;
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean grantSudoAccess(String username, boolean grant) {
        try {
            if (grant) {
                // Cấp quyền sudo bằng cách tạo file trong /etc/sudoers.d/
                String command = "echo '" + username + " ALL=(ALL:ALL) ALL' | sudo -S tee /etc/sudoers.d/" + username;
                executeCommand(command);
                // Đặt quyền đúng cho file sudoers
                executeCommand("sudo -S chmod 0440 /etc/sudoers.d/" + username);
            } else {
                // Thu hồi quyền sudo bằng cách xóa file
                String command = "sudo -S rm -f /etc/sudoers.d/" + username;
                executeCommand(command);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(String username) {
        try {
            // Xóa file sudoers nếu có
            executeCommand("sudo -S rm -f /etc/sudoers.d/" + username);

            // Xóa người dùng và thư mục home
            String command = "sudo -S userdel -r " + username;
            executeCommand(command);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public SSHUtil(String host, String username, String password, int port) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    // Kết nối SSH
    public void connect() throws JSchException {
        JSch jsch = new JSch();
        session = jsch.getSession(username, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
    }

    // Ngắt kết nối SSH
    public void disconnect() {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }
    
    public boolean addServiceRule(String action, boolean logEnabled, String serviceName) {
    try {
        // Tạo lệnh UFW
        String command = "sudo -S ufw " + action.toLowerCase();
        if (logEnabled) {
            command += " log";
        }
        command += " " + serviceName;
        
        // Thực thi lệnh
        String result = executeCommand(command);
        
        // Kiểm tra kết quả
        if (result.toLowerCase().contains("error") || result.toLowerCase().contains("failed")) {
            System.err.println("Thêm quy tắc dịch vụ thất bại: " + result);
            return false;
        }
        
        return true;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

    // Chạy lệnh từ xa và trả về đầu ra
    public String executeCommand(String command) throws Exception {
        if (session == null || !session.isConnected()) {
            connect();
        }

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        channel.setInputStream(null);
        channel.setErrStream(System.err);

        OutputStream out = channel.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
        channel.connect();

        // Gửi mật khẩu nếu lệnh dùng sudo
        if (command.contains("sudo")) {
            out.write((password + "\n").getBytes());
            out.flush();
        }

        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        // Đợi lệnh hoàn thành
        while (!channel.isClosed()) {
            Thread.sleep(100);
        }

        channel.disconnect();
        return output.toString().trim();
    }
}
