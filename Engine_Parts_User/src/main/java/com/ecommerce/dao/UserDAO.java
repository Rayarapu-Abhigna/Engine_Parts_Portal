package com.ecommerce.dao;

import com.ecommerce.util.DBUtil;

import com.ecommerce.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    public User authenticate(String email, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        }
        return null;
    }
    
    public boolean validatePassword(int userId, String password) throws SQLException {
        String sql = "SELECT password FROM users WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    // TODO: Use a proper hashing comparison (e.g., BCrypt.checkpw)
                    return storedPassword != null && storedPassword.equals(password);
                }
            }
        }
        return false;
    }
    
    public boolean register(User user) throws SQLException {
        String sql = "INSERT INTO users (email, password, phone, name, city, pincode, address) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getPhone());
            stmt.setString(4, user.getName());
            stmt.setString(5, user.getCity());
            stmt.setString(6, user.getPincode());
            stmt.setString(7, user.getAddress());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean isEmailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public boolean isPhoneExists(String phone) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE phone = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, phone);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public User getUserById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        }
        return null;
    }
    
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE email != 'admin@gmail.com'";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        }
        return users;
    }
    
    public boolean updateUser(User user) throws SQLException {
        boolean updatePassword = user.getPassword() != null && !user.getPassword().trim().isEmpty();
        
        String sql;
        if (updatePassword) {
            sql = "UPDATE users SET name = ?, email = ?, phone = ?, password = ?, city = ?, pincode = ?, address = ? WHERE id = ?";
        } else {
            sql = "UPDATE users SET name = ?, email = ?, phone = ?, city = ?, pincode = ?, address = ? WHERE id = ?";
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhone());
            
            if (updatePassword) {
                stmt.setString(4, user.getPassword());
                stmt.setString(5, user.getCity());
                stmt.setString(6, user.getPincode());
                stmt.setString(7, user.getAddress());
                stmt.setInt(8, user.getId());
            } else {
                stmt.setString(4, user.getCity());
                stmt.setString(5, user.getPincode());
                stmt.setString(6, user.getAddress());
                stmt.setInt(7, user.getId());
            }
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setName(rs.getString("name"));
        user.setCity(rs.getString("city"));
        user.setPincode(rs.getString("pincode"));
        user.setAddress(rs.getString("address"));
        return user;
    }
} 