package com.ecommerce.dao;

import com.ecommerce.util.DBUtil;

import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    private ProductDAO productDAO = new ProductDAO();
    
    public boolean addToCart(int userId, CartItem cartItem) throws SQLException {
        String sql = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, cartItem.getProduct().getId());
            stmt.setInt(3, cartItem.getQuantity());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateCartItemQuantity(int userId, int productId, int quantity) throws SQLException {
        String sql = "UPDATE cart SET quantity = ? WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantity);
            stmt.setInt(2, userId);
            stmt.setInt(3, productId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean removeFromCart(int userId, int productId) throws SQLException {
        String sql = "DELETE FROM cart WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean clearCart(int userId) throws SQLException {
        String sql = "DELETE FROM cart WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public List<CartItem> getCartItems(int userId) throws SQLException {
        List<CartItem> cartItems = new ArrayList<>();
        String sql = "SELECT c.quantity, p.* FROM cart c INNER JOIN products p ON c.product_id = p.id WHERE c.user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = extractProductFromResultSet(rs);
                    CartItem item = new CartItem();
                    item.setProduct(product);
                    item.setQuantity(rs.getInt("quantity"));
                    cartItems.add(item);
                }
            }
        }
        return cartItems;
    }
    
    public CartItem getCartItem(int userId, int productId) throws SQLException {
        String sql = "SELECT c.quantity, p.* FROM cart c INNER JOIN products p ON c.product_id = p.id WHERE c.user_id = ? AND c.product_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Product product = extractProductFromResultSet(rs);
                    CartItem item = new CartItem();
                    item.setProduct(product);
                    item.setQuantity(rs.getInt("quantity"));
                    return item;
                }
            }
        }
        return null;
    }

    private Product extractProductFromResultSet(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getDouble("price"));
        product.setStock(rs.getInt("stock"));
        return product;
    }
} 