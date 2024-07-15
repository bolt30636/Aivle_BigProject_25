package com.example.BigProject_25.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "user_id")
    private String userID;  // 사용자 아이디 컬럼 추가

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_num")
    private int phoneNum;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "user_type", nullable = false)
    private String userType;

    @Column(name = "password_changed_at")
    private Date passwordChangedAt;

    @Column(name = "is_account_locked")
    private boolean isAccountLocked;

    public User() {
        // 기본 생성자
    }

    public User(String name, String email, String userID, int phoneNum, String password, String userType) {
        this.name = name;
        this.email = email;
        this.userID = userID;
        this.phoneNum = phoneNum;
        this.password = password;
        this.userType = userType;
    }

    // getter 및 setter 메서드
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(int phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Date getPasswordChangedAt() {
        return passwordChangedAt;
    }

    public void setPasswordChangedAt(Date passwordChangedAt) {
        this.passwordChangedAt = passwordChangedAt;
    }

    public boolean isAccountLocked() {
        return isAccountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        isAccountLocked = accountLocked;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", userID='" + userID + '\'' +
                ", name='" + name + '\'' +
                ", phoneNum=" + phoneNum +
                ", password='" + password + '\'' +
                ", userType='" + userType + '\'' +
                ", passwordChangedAt=" + passwordChangedAt +
                ", isAccountLocked=" + isAccountLocked +
                '}';
    }
}
