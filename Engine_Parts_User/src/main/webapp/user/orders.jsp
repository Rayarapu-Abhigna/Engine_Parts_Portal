<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../includes/header.jsp" %>

<div class="container py-4">
    <h2 class="mb-4">My Orders</h2>

    <c:if test="${not empty orderSuccess}">
        <div class="alert alert-success">
            ${orderSuccess}
            <c:remove var="orderSuccess" scope="session" />
        </div>
    </c:if>

    <c:if test="${empty orders}">
        <div class="alert alert-info">
            You haven't placed any orders yet. <a href="${pageContext.request.contextPath}/products">Browse products</a> to make your first purchase.
        </div>
    </c:if>

    <c:if test="${not empty orders}">
        <div class="row">
            <c:forEach items="${orders}" var="order">
                <div class="col-md-6 mb-4">
                    <div class="card">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <h5 class="mb-0">Order #${order.id}</h5>
                            <c:choose>
    <c:when test="${order.status == 'CANCELLED_BY_USER'}">
        <span class="badge bg-danger">Cancelled by You</span>
    </c:when>
    <c:when test="${order.status == 'CANCELLED_BY_SELLER'}">
        <span class="badge bg-dark">Cancelled by Seller</span>
    </c:when>
    <c:otherwise>
        <span class="badge ${order.status == 'PENDING' ? 'bg-warning' : 
                                              order.status == 'SHIPPED' ? 'bg-info' : 
                                              order.status == 'DELIVERED' ? 'bg-success' : 'bg-secondary'}">
            ${order.status}
        </span>
    </c:otherwise>
</c:choose>
                        </div>
                        <div class="card-body">
                            <p class="card-text">
                                <strong>Order Date:</strong> ${order.orderDate}<br>
                                <strong>Total Amount:</strong> $${String.format("%.2f", order.total)}<br>
                                <strong>Shipping Address:</strong> ${order.address}, ${order.city}, ${order.pincode}
                            </p>
                            <button type="button" class="btn btn-primary" 
                                    data-bs-toggle="modal" 
                                    data-bs-target="#orderDetails${order.id}">
                                View Details
                            </button>
                            <c:if test="${order.status == 'PENDING'}">
                                <form method="post" action="${pageContext.request.contextPath}/order/cancel" style="display:inline;">
                                    <input type="hidden" name="orderId" value="${order.id}" />
                                    <button type="submit" class="btn btn-danger" onclick="return confirm('Are you sure you want to cancel this order?');">
                                        Cancel Order
                                    </button>
                                </form>
                            </c:if>
                        </div>
                    </div>
                </div>

                <!-- Order Details Modal -->
                <div class="modal fade" id="orderDetails${order.id}" tabindex="-1" aria-hidden="true">
                    <div class="modal-dialog modal-lg">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">Order Details #${order.id}</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <div class="row mb-3">
                                    <div class="col-md-6">
                                        <h6>Shipping Information</h6>
                                        <p>Name: ${order.userName}<br>
                                        Address: ${order.address}, ${order.city}, ${order.pincode}<br>
                                        Phone: ${order.phone}</p>
                                    </div>
                                    <div class="col-md-6">
                                        <h6>Order Information</h6>
                                        <p>Order Date: ${order.orderDate}<br>
                                        Status: ${order.status}<br>
                                        Total Amount: $${String.format("%.2f", order.total)}</p>
                                    </div>
                                </div>
                                <h6>Order Items</h6>
                                <table class="table table-sm">
                                    <thead>
                                        <tr>
                                            <th>Product</th>
                                            <th>Price</th>
                                            <th>Quantity</th>
                                            <th>Subtotal</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${order.items}" var="item">
                                            <tr>
                                                <td>${item.product.name}</td>
                                                <td>$${String.format("%.2f", item.price)}</td>
                                                <td>${item.quantity}</td>
                                                <td>$${String.format("%.2f", item.price * item.quantity)}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                    <tfoot>
                                        <tr>
                                            <th colspan="3" class="text-end">Total:</th>
                                            <th>$${String.format("%.2f", order.total)}</th>
                                        </tr>
                                    </tfoot>
                                </table>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:if>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 