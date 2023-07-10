package dev.mrflyn.securelogin.databases;

import dev.mrflyn.securelogin.LoginData;

import java.util.UUID;

public class NoDb implements IDatabase{
    @Override
    public String name() {
        return null;
    }

    @Override
    public boolean connect() {
        return false;
    }

    @Override
    public void init() {

    }

    @Override
    public boolean isPresent(UUID uuid) {
        return false;
    }

    @Override
    public boolean isPresent(String name) {
        return false;
    }

    @Override
    public void setData(LoginData data) {

    }

    @Override
    public LoginData getData(UUID uuid) {
        return null;
    }

    @Override
    public LoginData getData(String name) {
        return null;
    }

    @Override
    public boolean deleteData(UUID uuid) {
        return false;
    }

    @Override
    public boolean deleteData(String name) {
        return false;
    }
}
