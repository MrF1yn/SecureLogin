package dev.mrflyn.securelogin;

import java.util.UUID;

public class LoginData {

    private UUID uuid;
    private String userName;
    private String password;
    private String ip;

    public LoginData(UUID uuid, String userName, String password, String ip) {
        this.uuid = uuid;
        this.userName = userName;
        this.password = password;
        this.ip = ip;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getIp() {
        return ip;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
