/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control_ubuntu_firewall;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author ACER
 */
public class App {

    private final SimpleStringProperty name;
    private final SimpleStringProperty status;

    public App(String name, String status) {
        this.name = new SimpleStringProperty(name);
        this.status = new SimpleStringProperty(status);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public void setStatus(String newStatus) {
        status.set(newStatus);
    }

}
