package com.ecommerce.servlet;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/create-razorpay-order")
public class CreateRazorpayOrderServlet extends HttpServlet {
    private static final String RAZORPAY_KEY_ID = "rzp_test_AD8SpqQHaBJSqs";
    private static final String RAZORPAY_SECRET = "2XZ2JLpEsVfE7s9qGDJ44leV";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        JSONObject jsonRequest = new JSONObject(sb.toString());
        int amount = jsonRequest.getInt("amount");
        try {
            RazorpayClient razorpay = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_SECRET);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount);
            orderRequest.put("currency", "INR");
            orderRequest.put("payment_capture", 1);
            Order order = razorpay.orders.create(orderRequest);
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print(order.toString());
            out.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unable to create Razorpay order\"}");
        }
    }
}
