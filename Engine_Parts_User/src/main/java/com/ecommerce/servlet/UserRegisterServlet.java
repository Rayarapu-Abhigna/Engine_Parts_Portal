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

@WebServlet("/user/register")
public class UserRegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/user/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        String name = request.getParameter("name");
        String error = null;

        try {
            // Check if email already exists
            if (userDAO.isEmailExists(email)) {
                error = "Email already registered";
                request.setAttribute("error", error);
                request.getRequestDispatcher("/user/register.jsp").forward(request, response);
                return;
            }

            // Check if phone already exists
            if (userDAO.isPhoneExists(phone)) {
                error = "Phone number already registered";
                request.setAttribute("error", error);
                request.getRequestDispatcher("/user/register.jsp").forward(request, response);
                return;
            }

            // Create new user
            User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            user.setPhone(phone);
            user.setName(name);

            if (userDAO.register(user)) {
                // Auto login after registration
                user = userDAO.authenticate(email, password);
                if (user != null) {
                    HttpSession session = request.getSession();
                    // Clear any existing session attributes
                    session.invalidate();
                    session = request.getSession(true);
                    // Set user attribute only
                    session.setAttribute("user", user);
                    session.removeAttribute("admin"); // Ensure admin attribute is not set
                    response.sendRedirect(request.getContextPath() + "/products");
                } else {
                    error = "Registration successful but auto-login failed";
                    request.setAttribute("error", error);
                    request.getRequestDispatcher("/user/login.jsp").forward(request, response);
                }
            } else {
                error = "Registration failed";
                request.setAttribute("error", error);
                request.getRequestDispatcher("/user/register.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            error = "Database error: " + e.getMessage();
            request.setAttribute("error", error);
            request.getRequestDispatcher("/user/register.jsp").forward(request, response);
        }
    }
} 