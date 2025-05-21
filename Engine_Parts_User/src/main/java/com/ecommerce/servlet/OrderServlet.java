package com.ecommerce.servlet;

import com.ecommerce.dao.CartDAO;
import com.ecommerce.dao.OrderDAO;
import com.ecommerce.dao.UserDAO;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet({"/orders", "/order/*"})
public class OrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private OrderDAO orderDAO = new OrderDAO();
    private CartDAO cartDAO = new CartDAO();
    private UserDAO userDAO = new UserDAO();
    private ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        User user = (User) request.getSession().getAttribute("user");
        
        // Check if user is logged in
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        try {
            String servletPath = request.getServletPath();
            if (servletPath.equals("/orders")) {
                // Show user's orders
                List<Order> orders = orderDAO.getOrdersByUserId(user.getId());
                request.setAttribute("orders", orders);
                request.getRequestDispatcher("/user/orders.jsp").forward(request, response);
            } else if (pathInfo != null && pathInfo.equals("/checkout")) {
                // Show checkout page with cart items
                List<CartItem> cartItems = cartDAO.getCartItems(user.getId());
                if (cartItems.isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/cart?error=empty-cart");
                    return;
                }
                request.setAttribute("cartItems", cartItems);
                request.getRequestDispatcher("/checkout.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        // --- Handle order cancellation ---
        if (pathInfo != null && pathInfo.equals("/cancel")) {
            try {
                int orderId = Integer.parseInt(request.getParameter("orderId"));
                boolean success = orderDAO.cancelOrder(orderId);
                if (success) {
                    request.getSession().setAttribute("orderSuccess", "Order cancelled successfully.");
                } else {
                    request.getSession().setAttribute("orderSuccess", "Failed to cancel order. Please try again.");
                }
                response.sendRedirect(request.getContextPath() + "/orders");
            } catch (Exception e) {
                request.getSession().setAttribute("orderSuccess", "Error while cancelling order.");
                response.sendRedirect(request.getContextPath() + "/orders");
            }
            return;
        }
        // --- End order cancellation ---

        // Razorpay payment verification
        String razorpayPaymentId = request.getParameter("razorpay_payment_id");
        String razorpayOrderId = request.getParameter("razorpay_order_id");
        String razorpaySignature = request.getParameter("razorpay_signature");
        boolean paymentVerified = false;
        if (razorpayPaymentId != null && razorpayOrderId != null && razorpaySignature != null) {
            try {
                String secret = "2XZ2JLpEsVfE7s9qGDJ44leV";
                String payload = razorpayOrderId + '|' + razorpayPaymentId;
                javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
                mac.init(new javax.crypto.spec.SecretKeySpec(secret.getBytes(), "HmacSHA256"));
                byte[] hash = mac.doFinal(payload.getBytes());
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                String generatedSignature = hexString.toString();
                paymentVerified = generatedSignature.equals(razorpaySignature);
            } catch (Exception ex) {
                paymentVerified = false;
            }
        }

        if (!paymentVerified) {
            request.getSession().setAttribute("error", "Payment verification failed. Order not placed.");
            response.sendRedirect(request.getContextPath() + "/order/checkout");
            return;
        }

        try {
            if (pathInfo != null && pathInfo.equals("/place")) {
                // Get cart items
                List<CartItem> cartItems = cartDAO.getCartItems(user.getId());
                if (cartItems.isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/cart?error=empty-cart");
                    return;
                }

                // Create order
                Order order = new Order();
                order.setUserId(user.getId());
                order.setUserName(request.getParameter("fullName"));
                order.setAddress(request.getParameter("address"));
                order.setCity(request.getParameter("city"));
                order.setPincode(request.getParameter("zipCode"));
                order.setPhone(request.getParameter("phone"));
                order.setStatus("PENDING"); // Set initial status

                double total = 0;
                List<OrderItem> orderItems = new ArrayList<>();

                for (CartItem cartItem : cartItems) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProductId(cartItem.getProduct().getId());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getProduct().getPrice());
                    total += orderItem.getPrice() * orderItem.getQuantity();
                    orderItems.add(orderItem);
                }

                order.setTotal(total);
                order.setItems(orderItems);

                // Place order and clear cart
                if (orderDAO.createOrder(order)) {
                    // Update stock for each item
                    boolean stockUpdated = true;
                    for (OrderItem item : orderItems) {
                        if (!productDAO.updateStock(item.getProductId(), item.getQuantity())) {
                            // Handle stock update failure (e.g., log error, potentially rollback order?)
                            // For now, we'll just mark it as failed and proceed
                            System.err.println("Failed to update stock for product ID: " + item.getProductId());
                            stockUpdated = false; 
                            // Decide on error handling: maybe prevent order completion or notify admin?
                        }
                    }
                    
                    // If stock update failed for any item, perhaps add a specific error message?
                    if (!stockUpdated) {
                         request.getSession().setAttribute("error", "Order placed, but failed to update stock for some items. Please contact support.");
                         // Optionally redirect to a different page or show a specific error view
                    } else {
                         request.getSession().setAttribute("message", "Order placed successfully! Your order ID is: " + order.getId());
                    }
                    
                    cartDAO.clearCart(user.getId()); // Clear cart after stock update attempt
                    response.sendRedirect(request.getContextPath() + "/orders");
                } else {
                    request.getSession().setAttribute("error", "Failed to place order. Please try again.");
                    response.sendRedirect(request.getContextPath() + "/order/checkout");
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
} 