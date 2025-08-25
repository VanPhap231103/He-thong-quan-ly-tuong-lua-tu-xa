/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control_ubuntu_firewall;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FirewallLog {

    private final StringProperty time;
    private final StringProperty action;
    private final StringProperty sourceIP;
    private final StringProperty destIP;
    private final StringProperty protocol;
    private final StringProperty sourcePort;
    private final StringProperty destPort;
    private final StringProperty direction;
    private final StringProperty rawLog; // Lưu log gốc để hiển thị chi tiết

    public FirewallLog(String time, String action, String sourceIP, String destIP,
            String protocol, String sourcePort, String destPort,
            String direction, String rawLog) {
        this.time = new SimpleStringProperty(time);
        this.action = new SimpleStringProperty(action);
        this.sourceIP = new SimpleStringProperty(sourceIP);
        this.destIP = new SimpleStringProperty(destIP);
        this.protocol = new SimpleStringProperty(protocol);
        this.sourcePort = new SimpleStringProperty(sourcePort);
        this.destPort = new SimpleStringProperty(destPort);
        this.direction = new SimpleStringProperty(direction);
        this.rawLog = new SimpleStringProperty(rawLog);
    }

    // Getters và Properties
    public String getTime() {
        return time.get();
    }

    public StringProperty timeProperty() {
        return time;
    }

    public String getAction() {
        return action.get();
    }

    public StringProperty actionProperty() {
        return action;
    }

    public String getSourceIP() {
        return sourceIP.get();
    }

    public StringProperty sourceIPProperty() {
        return sourceIP;
    }

    public String getDestIP() {
        return destIP.get();
    }

    public StringProperty destIPProperty() {
        return destIP;
    }

    public String getProtocol() {
        return protocol.get();
    }

    public StringProperty protocolProperty() {
        return protocol;
    }

    public String getSourcePort() {
        return sourcePort.get();
    }

    public StringProperty sourcePortProperty() {
        return sourcePort;
    }

    public String getDestPort() {
        return destPort.get();
    }

    public StringProperty destPortProperty() {
        return destPort;
    }

    public String getDirection() {
        return direction.get();
    }

    public StringProperty directionProperty() {
        return direction;
    }

    public String getRawLog() {
        return rawLog.get();
    }

    public StringProperty rawLogProperty() {
        return rawLog;
    }
}
