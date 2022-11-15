package common;

import java.io.Serializable;

/**
 * @Author: xuan
 * @CreateTime: 2022-11-15  11:27
 * @Version: 1.0
 */
public class User implements Serializable {
    private String userId;
    private String password;

    public User() {
    }

    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
