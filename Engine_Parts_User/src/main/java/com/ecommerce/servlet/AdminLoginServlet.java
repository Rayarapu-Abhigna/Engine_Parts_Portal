package com.ecommerce.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/admin/login")
public class AdminLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "admin";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String error = null;

        if (ADMIN_EMAIL.equals(email) && ADMIN_PASSWORD.equals(password)) {
            HttpSession session = request.getSession();
            // Clear any existing session attributes
            session.invalidate();
            session = request.getSession(true);
            // Set admin attribute and ensure user attribute is not set
            session.setAttribute("admin", true);
            session.removeAttribute("user");
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }
        
        error = "Invalid admin credentials";
        request.setAttribute("error", error);
        request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
    }
} 