package com.ecommerce.servlet;

import com.ecommerce.dao.OrderDAO;
import com.ecommerce.model.Order;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet({"/admin/orders", "/admin/order/*"})
public class AdminOrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if admin is logged in
        Boolean isAdmin = (Boolean) request.getSession().getAttribute("admin");
        if (isAdmin == null || !isAdmin) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        try {
            // Get all orders
            List<Order> orders = orderDAO.getAllOrders();
            request.setAttribute("orders", orders);

            // Get orders by status for filtering
            request.setAttribute("pendingOrders", orderDAO.getOrdersByStatus("PENDING"));
            request.setAttribute("shippedOrders", orderDAO.getOrdersByStatus("SHIPPED"));
            request.setAttribute("deliveredOrders", orderDAO.getOrdersByStatus("DELIVERED"));
            request.setAttribute("cancelledOrders", orderDAO.getOrdersByStatus("CANCELLED"));

            request.getRequestDispatcher("/admin/orders.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if admin is logged in
        Boolean isAdmin = (Boolean) request.getSession().getAttribute("admin");
        if (isAdmin == null || !isAdmin) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.equals("/update-status")) {
            try {
                int orderId = Integer.parseInt(request.getParameter("orderId"));
                String status = request.getParameter("status");
                
                if (orderId <= 0 || status == null || status.trim().isEmpty()) {
                    request.getSession().setAttribute("error", "Invalid order ID or status");
                    response.sendRedirect(request.getContextPath() + "/admin/orders");
                    return;
                }

                boolean success;
                if ("CANCELLED".equals(status)) {
                    success = orderDAO.cancelOrder(orderId, "CANCELLED_BY_SELLER");
                } else {
                    success = orderDAO.updateOrderStatus(orderId, status);
                }

                if (success) {
                    request.getSession().setAttribute("message", 
                        "Order #" + orderId + " status updated to " + status);
                } else {
                    request.getSession().setAttribute("error", 
                        "Failed to update status for order #" + orderId);
                }
                response.sendRedirect(request.getContextPath() + "/admin/orders");
            } catch (SQLException e) {
                throw new ServletException("Database error", e);
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("error", "Invalid order ID format");
                response.sendRedirect(request.getContextPath() + "/admin/orders");
            }
        }
    }

    private boolean isAdmin(HttpServletRequest request) {
        Boolean isAdmin = (Boolean) request.getSession().getAttribute("admin");
        return isAdmin != null && isAdmin;
    }
} 