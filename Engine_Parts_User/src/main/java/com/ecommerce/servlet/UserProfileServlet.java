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

@WebServlet({"/user/profile", "/user/update-profile"})
public class UserProfileServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }
        
        request.getRequestDispatcher("/user/profile.jsp").forward(request, response);
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

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        try {
            boolean passwordChangeRequested = newPassword != null && !newPassword.trim().isEmpty();

            // Validate current password if trying to change password
            if (passwordChangeRequested) {
                if (currentPassword == null || currentPassword.trim().isEmpty() || !userDAO.validatePassword(user.getId(), currentPassword)) {
                    session.setAttribute("error", "Current password is incorrect or missing");
                    response.sendRedirect(request.getContextPath() + "/user/profile");
                    return;
                }
                // Validate new password confirmation
                if (!newPassword.equals(confirmPassword)) {
                    session.setAttribute("error", "New passwords do not match");
                    response.sendRedirect(request.getContextPath() + "/user/profile");
                    return;
                }
            }
            
            // Create a user object with updated info (potentially including new password)
            User updatedUser = new User();
            updatedUser.setId(user.getId());
            updatedUser.setName(name);
            updatedUser.setEmail(email);
            updatedUser.setPhone(phone);
            // Set other fields from the original user object if not updated
            updatedUser.setCity(user.getCity()); 
            updatedUser.setPincode(user.getPincode());
            updatedUser.setAddress(user.getAddress());

            // Only set the password field if a valid change was requested
            if (passwordChangeRequested) {
                updatedUser.setPassword(newPassword); 
            } else {
                updatedUser.setPassword(null); // Ensure password isn't updated if not requested
            }
            
            if (userDAO.updateUser(updatedUser)) {
                // Update session with potentially updated user info
                User freshUser = userDAO.getUserById(user.getId()); // Fetch updated user data (without password)
                session.setAttribute("user", freshUser); 
                session.setAttribute("message", "Profile updated successfully");
            } else {
                session.setAttribute("error", "Failed to update profile");
            }
            
        } catch (SQLException e) {
            session.setAttribute("error", "Database error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        
        response.sendRedirect(request.getContextPath() + "/user/profile");
    }
} 