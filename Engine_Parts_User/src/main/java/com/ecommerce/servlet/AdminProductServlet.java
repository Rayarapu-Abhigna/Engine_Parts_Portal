package com.ecommerce.servlet;

import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/product/*")
public class AdminProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        try {
            if (pathInfo.equals("/add")) {
                // Show add product form
                request.getRequestDispatcher("/admin/add-product.jsp").forward(request, response);
            } else if (pathInfo.equals("/edit")) {
                // Show edit product form
                int productId = Integer.parseInt(request.getParameter("id"));
                Product product = productDAO.getProductById(productId);
                if (product != null) {
                    request.setAttribute("product", product);
                    request.getRequestDispatcher("/admin/edit-product.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo.equals("/add")) {
                // Add new product
                Product product = new Product();
                product.setName(request.getParameter("name"));
                product.setDescription(request.getParameter("description"));
                product.setPrice(Double.parseDouble(request.getParameter("price")));
                product.setStock(Integer.parseInt(request.getParameter("stock")));

                if (productDAO.addProduct(product)) {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                } else {
                    request.setAttribute("error", "Failed to add product");
                    request.getRequestDispatcher("/admin/add-product.jsp").forward(request, response);
                }
            } else if (pathInfo.equals("/edit")) {
                // Update existing product
                Product product = new Product();
                product.setId(Integer.parseInt(request.getParameter("id")));
                product.setName(request.getParameter("name"));
                product.setDescription(request.getParameter("description"));
                product.setPrice(Double.parseDouble(request.getParameter("price")));
                product.setStock(Integer.parseInt(request.getParameter("stock")));

                if (productDAO.updateProduct(product)) {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                } else {
                    request.setAttribute("error", "Failed to update product");
                    request.setAttribute("product", product);
                    request.getRequestDispatcher("/admin/edit-product.jsp").forward(request, response);
                }
            } else if (pathInfo.equals("/delete")) {
                // Delete product
                int productId = Integer.parseInt(request.getParameter("id"));
                if (productDAO.deleteProduct(productId)) {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=delete-failed");
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid number format");
            request.getRequestDispatcher("/admin/add-product.jsp").forward(request, response);
        }
    }

    private boolean isAdmin(HttpServletRequest request) {
        Boolean isAdmin = (Boolean) request.getSession().getAttribute("admin");
        return isAdmin != null && isAdmin;
    }
} 