package me.anfanik.asuko.build;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class FileCredentials {

    private final String username;
    private final String password;
    private final String encoded;

    public FileCredentials(String username, String password) {
        this.username = username;
        this.password = password;
        encoded = Base64.getEncoder().encodeToString((username + ":" + password)
                .getBytes(StandardCharsets.UTF_8));
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEncoded() {
        return encoded;
    }

    @Override
    public String toString() {
        return "FileCredentials{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}
