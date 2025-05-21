<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Checkout</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>
<body>
    <jsp:include page="includes/header.jsp" />
    
    <div class="container mt-4">
        <h2>Checkout</h2>
        
        <c:if test="${not empty message}">
            <div class="alert alert-info">${message}</div>
        </c:if>
        
        <div class="row">
            <div class="col-md-8">
                <div class="card mb-4">
                    <div class="card-header">
                        <h4>Shipping Information</h4>
                    </div>
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/order/place" method="post">
                            <div class="form-group">
                                <label for="fullName">Full Name</label>
                                <input type="text" class="form-control" id="fullName" name="fullName" required>
                            </div>
                            <div class="form-group">
                                <label for="address">Address</label>
                                <textarea class="form-control" id="address" name="address" rows="3" required></textarea>
                            </div>
                            <div class="form-row">
                                <div class="form-group col-md-6">
                                    <label for="city">City</label>
                                    <input type="text" class="form-control" id="city" name="city" required>
                                </div>
                                <div class="form-group col-md-6">
                                    <label for="state">State</label>
                                    <input type="text" class="form-control" id="state" name="state" required>
                                </div>
                            </div>
                            <div class="form-row">
                                <div class="form-group col-md-6">
                                    <label for="zipCode">ZIP Code</label>
                                    <input type="text" class="form-control" id="zipCode" name="zipCode" required>
                                </div>
                                <div class="form-group col-md-6">
                                    <label for="phone">Phone</label>
                                    <input type="tel" class="form-control" id="phone" name="phone" required>
                                </div>
                            </div>
                            
<button id="rzp-button" type="button" class="btn btn-primary btn-lg btn-block mt-4">Place Order</button>
<input type="hidden" id="razorpay_payment_id" name="razorpay_payment_id" />
<input type="hidden" id="razorpay_order_id" name="razorpay_order_id" />
<input type="hidden" id="razorpay_signature" name="razorpay_signature" />
                        </form>
                    </div>
                </div>
            </div>
            
            <div class="col-md-4">
                <div class="card">
                    <div class="card-header">
                        <h4>Order Summary</h4>
                    </div>
                    <div class="card-body">
                        <c:set var="total" value="0" />
                        <c:forEach var="item" items="${cartItems}">
                            <div class="d-flex justify-content-between mb-2">
                                <span>${item.product.name} x ${item.quantity}</span>
                                <span>$${item.product.price * item.quantity}</span>
                            </div>
                            <c:set var="total" value="${total + (item.product.price * item.quantity)}" />
                        </c:forEach>
                        <hr>
                        <div class="d-flex justify-content-between">
                            <strong>Total</strong>
                            <strong>$${total}</strong>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.4/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
<script src="https://checkout.razorpay.com/v1/checkout.js"></script>
<script>
    document.getElementById('rzp-button').onclick = function(e) {
        e.preventDefault();
        var total = Number('${total}') * 100; // Razorpay expects paise
        fetch('${pageContext.request.contextPath}/create-razorpay-order', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ amount: total })
        })
        .then(response => response.json())
        .then(data => {
            var options = {
                "key": "rzp_test_AD8SpqQHaBJSqs", // Enter the Key ID
                "amount": total,
                "currency": "INR",
                "name": "E-Commerce Checkout",
                "description": "Order Payment",
                "order_id": data.id,
                "handler": function (response){
                    document.getElementById('razorpay_payment_id').value = response.razorpay_payment_id;
                    document.getElementById('razorpay_order_id').value = response.razorpay_order_id;
                    document.getElementById('razorpay_signature').value = response.razorpay_signature;
                    document.querySelector('form').submit();
                },
                "theme": { "color": "#3399cc" }
            };
            var rzp1 = new Razorpay(options);
            rzp1.open();
        });
    }
</script>
</body>
</html> 