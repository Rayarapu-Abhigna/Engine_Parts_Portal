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
import java.util.List;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // List all products
            List<Product> products = productDAO.getAllProducts();
            request.setAttribute("products", products);
            request.getRequestDispatcher("/products.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        try {
            if (pathInfo.equals("/add")) {
                // Add new product
                Product product = new Product();
                product.setName(request.getParameter("name"));
                product.setDescription(request.getParameter("description"));
                product.setPrice(Double.parseDouble(request.getParameter("price")));
                product.setStock(Integer.parseInt(request.getParameter("stock")));

                if (productDAO.addProduct(product)) {
                    response.sendRedirect(request.getContextPath() + "/admin/products");
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
                    response.sendRedirect(request.getContextPath() + "/admin/products");
                } else {
                    request.setAttribute("error", "Failed to update product");
                    request.setAttribute("product", product);
                    request.getRequestDispatcher("/admin/edit-product.jsp").forward(request, response);
                }
            } else if (pathInfo.equals("/delete")) {
                // Delete product
                int productId = Integer.parseInt(request.getParameter("id"));
                if (productDAO.deleteProduct(productId)) {
                    response.sendRedirect(request.getContextPath() + "/admin/products");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/products?error=delete-failed");
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        } catch (NumberFormatException e) {
            throw new ServletException("Invalid number format", e);
        }
    }

    private boolean isAdmin(HttpServletRequest request) {
        Boolean isAdmin = (Boolean) request.getSession().getAttribute("admin");
        return isAdmin != null && isAdmin;
    }
}