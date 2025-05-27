package com.example.easybuy.model;

public class Admin {
    private int id; // Thêm trường id
    private String fullName;
    private String email;
    private String password;

    // Constructor đầy đủ với id
    public Admin(int id, String fullName, String email, String password) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    // Constructor không có id (dùng khi thêm admin mới, id sẽ tự động tăng trong database)
    public Admin(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    // Constructor không tham số (dùng cho SQLite hoặc khởi tạo rỗng)
    public Admin() {
    }

    // Getter và Setter cho id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Các Getter và Setter khác
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}