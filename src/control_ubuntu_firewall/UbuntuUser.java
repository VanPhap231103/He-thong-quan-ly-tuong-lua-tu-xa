/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control_ubuntu_firewall;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UbuntuUser {
    private final StringProperty username;
    private final StringProperty homeDirectory;
    private final StringProperty shell;
    private final BooleanProperty isSudo;
    private final BooleanProperty isCurrentUser;
    
    public UbuntuUser(String username, String homeDirectory, String shell, boolean isSudo, boolean isCurrentUser) {
        this.username = new SimpleStringProperty(username);
        this.homeDirectory = new SimpleStringProperty(homeDirectory);
        this.shell = new SimpleStringProperty(shell);
        this.isSudo = new SimpleBooleanProperty(isSudo);
        this.isCurrentUser = new SimpleBooleanProperty(isCurrentUser);
    }
    
    // Các getter và setter
    public String getUsername() {
        return username.get();
    }
    
    public StringProperty usernameProperty() {
        return username;
    }
    
    public String getHomeDirectory() {
        return homeDirectory.get();
    }
    
    public StringProperty homeDirectoryProperty() {
        return homeDirectory;
    }
    
    public String getShell() {
        return shell.get();
    }
    
    public StringProperty shellProperty() {
        return shell;
    }
    
    public boolean isSudo() {
        return isSudo.get();
    }
    
    public BooleanProperty isSudoProperty() {
        return isSudo;
    }
    
    public boolean isCurrentUser() {
        return isCurrentUser.get();
    }
    
    public BooleanProperty isCurrentUserProperty() {
        return isCurrentUser;
    }
    
    public void setSudo(boolean sudo) {
        this.isSudo.set(sudo);
    }
}
