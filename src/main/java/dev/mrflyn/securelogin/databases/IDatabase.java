package dev.mrflyn.securelogin.databases;


import dev.mrflyn.securelogin.LoginData;
import java.util.UUID;

public interface IDatabase {

    String name();

    boolean connect();

    void init();

    boolean isPresent(UUID uuid);

    boolean isPresent(String name);

    void setData(LoginData data);

    LoginData getData(UUID uuid);

    LoginData getData(String name);

    boolean deleteData(UUID uuid);

    boolean deleteData(String name);



}
