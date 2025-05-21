package com.ecommerce.servlet;

import com.ecommerce.dao.CartDAO;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/cart/*")
public class CartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProductDAO productDAO;
    private CartDAO cartDAO;

    @Override
    public void init() {
        productDAO = new ProductDAO();
        cartDAO = new CartDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        try {
            List<CartItem> cartItems = cartDAO.getCartItems(user.getId());
            session.setAttribute("cartItems", cartItems);
            session.setAttribute("cartTotal", calculateTotal(cartItems));
            request.getRequestDispatcher("/cart.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        String pathInfo = request.getPathInfo();
        try {
            if ("/add".equals(pathInfo)) {
                int productId = Integer.parseInt(request.getParameter("productId"));
                int quantity = Integer.parseInt(request.getParameter("quantity"));
                
                Product product = productDAO.getProductById(productId);
                if (product != null) {
                    CartItem cartItem = new CartItem();
                    cartItem.setProduct(product);
                    cartItem.setQuantity(quantity);
                    
                    cartDAO.addToCart(user.getId(), cartItem);
                }
            } else if ("/update".equals(pathInfo)) {
                int productId = Integer.parseInt(request.getParameter("productId"));
                int quantity = Integer.parseInt(request.getParameter("quantity"));
                
                if (quantity > 0) {
                    cartDAO.updateCartItemQuantity(user.getId(), productId, quantity);
                } else {
                    cartDAO.removeFromCart(user.getId(), productId);
                }
            } else if ("/remove".equals(pathInfo)) {
                int productId = Integer.parseInt(request.getParameter("productId"));
                cartDAO.removeFromCart(user.getId(), productId);
            }

            // Refresh cart items in session
            List<CartItem> cartItems = cartDAO.getCartItems(user.getId());
            session.setAttribute("cartItems", cartItems);
            session.setAttribute("cartTotal", calculateTotal(cartItems));
            
            response.sendRedirect(request.getContextPath() + "/cart");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private double calculateTotal(List<CartItem> cartItems) {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        return total;
    }
} 