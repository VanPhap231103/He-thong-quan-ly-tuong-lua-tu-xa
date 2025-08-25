package control_ubuntu_firewall;

public class PortRule {

    private int ruleNumber;
    private String to;
    private String action;
    private String from;
    private boolean logEnabled;
    private String protocol;
    private String port;
    private String sourceIP;
    private String destIP;

    // Constructor cơ bản
    public PortRule(String to, String action, String from) {
        this.to = to;
        this.action = action;
        this.from = from;
        this.logEnabled = false;
        this.protocol = "any";
        this.port = "";
        this.sourceIP = from;
        this.destIP = "any";
    }

    // Constructor đầy đủ
    public PortRule(String action, boolean logEnabled, String port, String protocol,
            String sourceIP, String destIP) {
        this.action = action;
        this.logEnabled = logEnabled;
        this.port = port;
        this.protocol = protocol;
        this.sourceIP = sourceIP;
        this.destIP = destIP;

        // Tạo chuỗi hiển thị cho to và from
        this.to = buildToString();
        this.from = sourceIP.isEmpty() ? "Anywhere" : sourceIP;
    }

    // Phương thức để tạo chuỗi hiển thị cho cột "To"
    private String buildToString() {
        StringBuilder sb = new StringBuilder();
        if (!port.isEmpty()) {
            sb.append(port);
            if (!protocol.equals("any")) {
                sb.append("/").append(protocol);
            }
        } else if (!protocol.equals("any")) {
            sb.append(protocol);
        } else {
            sb.append("Any");
        }

        if (!destIP.equals("any") && !destIP.isEmpty()) {
            sb.append(" to ").append(destIP);
        }

        return sb.toString();
    }

    // Tạo chuỗi lệnh UFW sử dụng toán tử + thay vì StringBuilder
// Tạo chuỗi lệnh UFW sử dụng toán tử + thay vì StringBuilder
    public String toUFWCommand() {
        String command = "sudo -S ufw ";

        // Thêm hành động (allow/deny/reject)
        command += action.toLowerCase();

        // Thêm log nếu được bật (log phải viết sau allow/deny)
        if (logEnabled) {
            command += " log";
        }

        // Thêm IP nguồn nếu có
        if (sourceIP != null && !sourceIP.isEmpty() && !sourceIP.equalsIgnoreCase("any") && !sourceIP.equalsIgnoreCase("anywhere")) {
            command += " from " + sourceIP;
        }

        // Thêm IP đích
        if (destIP != null && !destIP.isEmpty() && !destIP.equalsIgnoreCase("any") && !destIP.equalsIgnoreCase("anywhere")) {
            command += " to " + destIP;
        } else {
            command += " to any";
        }

        // Thêm cổng nếu có
        if (port != null && !port.isEmpty()) {
            command += " port " + port;
        }

        // Thêm giao thức nếu không phải any
        if (protocol != null && !protocol.equalsIgnoreCase("any")) {
            command += " proto " + protocol;
        }

        System.out.println("Lệnh UFW được tạo: " + command);
        return command;
    }

    // Getters and setters
    public int getRuleNumber() {
        return ruleNumber;
    }

    public void setRuleNumber(int ruleNumber) {
        this.ruleNumber = ruleNumber;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public void setLogEnabled(boolean logEnabled) {
        this.logEnabled = logEnabled;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public void setSourceIP(String sourceIP) {
        this.sourceIP = sourceIP;
    }

    public String getDestIP() {
        return destIP;
    }

    public void setDestIP(String destIP) {
        this.destIP = destIP;
    }
}
