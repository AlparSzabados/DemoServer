package com.alpar.szabados.hibernate.server.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "user")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, unique = true)
    private long userId;

    @Column(name = "user_name", nullable = false, unique = true)
    private String userName;

    @Column(name = "encoded_password")
    private String encodedPassword;

    public User() {
    }

    public User(String userName) {
        this.userName = userName;
    }

    public User(String userName, String encodedPassword) {
        this.userName = userName;
        this.encodedPassword = encodedPassword;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEncodedPassword() {
        return encodedPassword;
    }

    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    @Override
    public String toString() {
        return String.format("User{userId=%d, userName='%s', encodedPassword='%s'}", userId, userName, encodedPassword);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId &&
                Objects.equals(userName, user.userName) &&
                Objects.equals(encodedPassword, user.encodedPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userName, encodedPassword);
    }
}
