/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control_ubuntu_firewall;

import javafx.beans.property.*;

/**
 *
 * @author ACER
 */
public class SavedConnection {

    private final IntegerProperty id;
    private final StringProperty ipAddress;
    private final IntegerProperty port;
    private final StringProperty username;

    public SavedConnection(int id, String ipAddress, int port, String username) {
        this.id = new SimpleIntegerProperty(id);
        this.ipAddress = new SimpleStringProperty(ipAddress);
        this.port = new SimpleIntegerProperty(port);
        this.username = new SimpleStringProperty(username);
    }

    // Getters
    public int getId() {
        return id.get();
    }

    public String getIpAddress() {
        return ipAddress.get();
    }

    public int getPort() {
        return port.get();
    }

    public String getUsername() {
        return username.get();
    }

    // Property getters
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty ipAddressProperty() {
        return ipAddress;
    }

    public IntegerProperty portProperty() {
        return port;
    }

    public StringProperty usernameProperty() {
        return username;
    }

}
