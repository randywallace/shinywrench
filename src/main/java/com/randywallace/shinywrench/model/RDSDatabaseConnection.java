package com.randywallace.shinywrench.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

public class RDSDatabaseConnection {
    private StringProperty hostname;
    private ObjectProperty<Integer> port;
    private StringProperty username;

    public RDSDatabaseConnection(String hostname, Integer port, String username) {
      this.setHostname(hostname);
      this.setPort(port);
      this.setUsername(username);
    }

    public String getHostname() {
        return hostname.get();
    }

    public StringProperty hostnameProperty() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname.set(hostname);
    }

    public Integer getPort() {
        return port.get();
    }

    public ObjectProperty<Integer> portProperty() {
        return port;
    }

    public void setPort(Integer port) {
        this.port.set(port);
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public static Callback<RDSDatabaseConnection, Observable[]> extractor() {
        return (RDSDatabaseConnection d) -> new Observable[]{
                d.hostnameProperty(),
                d.portProperty(),
                d.usernameProperty()
        };
    }
}
