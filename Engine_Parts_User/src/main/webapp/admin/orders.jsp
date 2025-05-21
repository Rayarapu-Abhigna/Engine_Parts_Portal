<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../includes/header.jsp" %>

<div class="container py-4">
    <h2 class="mb-4">Order Management</h2>

    <c:if test="${not empty message}">
        <div class="alert alert-info">${message}</div>
    </c:if>

    <!-- All Orders -->
    <div class="card mb-4">
        <div class="card-header bg-primary text-white">
            <h4 class="mb-0">All Orders</h4>
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Order ID</th>
                            <th>Customer</th>
                            <th>Address</th>
                            <th>Phone</th>
                            <th>Total Amount</th>
                            <th>Status</th>
                            <th>Order Date</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${orders}" var="order">
                            <tr>
                                <td>${order.id}</td>
                                <td>${order.userName}</td>
                                <td>${order.address}, ${order.city}, ${order.pincode}</td>
                                <td>${order.phone}</td>
                                <td>$${String.format("%.2f", order.total)}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${order.status == 'CANCELLED_BY_USER'}">
                                            <span class="badge bg-danger">Cancelled by User</span>
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
                                </td>
                                <td>${order.orderDate}</td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-info" 
                                                data-bs-toggle="modal" 
                                                data-bs-target="#orderDetails${order.id}">
                                            View Details
                                        </button>
                                        <form action="${pageContext.request.contextPath}/admin/order/update-status" 
                                              method="post" style="display: inline;">
                                            <input type="hidden" name="orderId" value="${order.id}">
                                            <select name="status" class="form-select form-select-sm d-inline-block w-auto" onchange="this.form.submit()">
                                                <option value="PENDING" ${order.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                                                <option value="SHIPPED" ${order.status == 'SHIPPED' ? 'selected' : ''}>Shipped</option>
                                                <option value="DELIVERED" ${order.status == 'DELIVERED' ? 'selected' : ''}>Delivered</option>
                                                <option value="CANCELLED" ${order.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                                            </select>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- Order Details Modals -->
<c:forEach items="${orders}" var="order">
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
                            <h6>Customer Information</h6>
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

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 