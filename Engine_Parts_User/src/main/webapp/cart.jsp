<%@ include file="includes/header.jsp" %>

<div class="container">
    <h2 class="mb-4">Shopping Cart</h2>

    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert">
            ${error}
        </div>
    </c:if>

    <c:if test="${empty cartItems}">
        <div class="alert alert-info" role="alert">
            Your cart is empty. <a href="${pageContext.request.contextPath}/products">Continue shopping</a>
        </div>
    </c:if>

    <c:if test="${not empty cartItems}">
        <div class="table-responsive">
            <table class="table">
                <thead>
                    <tr>
                        <th>Product</th>
                        <th>Price</th>
                        <th>Quantity</th>
                        <th>Total</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${cartItems}" var="item">
                        <tr>
                            <td>${item.product.name}</td>
                            <td>$${item.product.price}</td>
                            <td>
                                <form action="${pageContext.request.contextPath}/cart/update" method="post" class="d-flex align-items-center">
                                    <input type="hidden" name="productId" value="${item.product.id}">
                                    <input type="number" name="quantity" value="${item.quantity}" min="1" max="${item.product.stock}" class="form-control form-control-sm" style="width: 70px;">
                                    <button type="submit" class="btn btn-sm btn-outline-primary ms-2">Update</button>
                                </form>
                            </td>
                            <td>$${item.product.price * item.quantity}</td>
                            <td>
                                <form action="${pageContext.request.contextPath}/cart/remove" method="post" class="d-inline">
                                    <input type="hidden" name="productId" value="${item.product.id}">
                                    <button type="submit" class="btn btn-sm btn-danger">Remove</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
                <tfoot>
                    <tr>
                        <td colspan="3" class="text-end"><strong>Total:</strong></td>
                        <td><strong>$${total}</strong></td>
                        <td></td>
                    </tr>
                </tfoot>
            </table>
        </div>

        <div class="d-flex justify-content-between mt-4">
            <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary">Continue Shopping</a>
            <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary">Proceed to Checkout</a>
        </div>
    </c:if>
</div>

<%@ include file="includes/footer.jsp" %> 