package com.ecommerce.servlet;

import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/user/login")
public class UserLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/user/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String error = null;

        try {
            User user = userDAO.authenticate(email, password);
            if (user != null) {
                HttpSession session = request.getSession();
                // Clear any existing session attributes
                session.invalidate();
                session = request.getSession(true);
                // Set user attribute and ensure admin attribute is not set
                session.setAttribute("user", user);
                session.removeAttribute("admin");
                response.sendRedirect(request.getContextPath() + "/products");
            } else {
                error = "Invalid email or password";
                request.setAttribute("error", error);
                request.getRequestDispatcher("/user/login.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            error = "Database error: " + e.getMessage();
            request.setAttribute("error", error);
            request.getRequestDispatcher("/user/login.jsp").forward(request, response);
        }
    }
} 