<%@ include file="includes/header.jsp" %>

<div class="row mb-4">
    <div class="col">
        <h2>Products</h2>
    </div>
    <c:if test="${not empty sessionScope.admin}">
        <div class="col text-end">
            <a href="${pageContext.request.contextPath}/admin/product/add" class="btn btn-primary">
                <i class="fas fa-plus"></i> Add New Product
            </a>
        </div>
    </c:if>
</div>

<c:if test="${not empty error}">
    <div class="alert alert-danger" role="alert">
        ${error}
    </div>
</c:if>

<div class="row row-cols-1 row-cols-md-3 g-4">
    <c:forEach items="${products}" var="product">
        <div class="col">
            <div class="card h-100">
                <div class="card-body">
                    <h5 class="card-title">${product.name}</h5>
                    <p class="card-text">${product.description}</p>
                    <div class="d-flex justify-content-between align-items-center">
                        <h6 class="mb-0">$${product.price}</h6>
                        <small class="text-muted">Stock: ${product.stock}</small>
                    </div>
                </div>
                <div class="card-footer bg-transparent">
                    <c:choose>
                        <c:when test="${not empty sessionScope.admin}">
                            <div class="btn-group w-100" role="group">
                                <a href="${pageContext.request.contextPath}/product/edit?id=${product.id}" 
                                   class="btn btn-outline-primary">
                                    <i class="fas fa-edit"></i> Edit
                                </a>
                                <button type="button" class="btn btn-outline-danger" 
                                        onclick="deleteProduct(${product.id})">
                                    <i class="fas fa-trash"></i> Delete
                                </button>
                            </div>
                        </c:when>
                        <c:when test="${not empty sessionScope.user}">
                            <c:choose>
                                <c:when test="${product.stock > 0}">
                                    <form action="${pageContext.request.contextPath}/cart/add" method="post" 
                                          class="d-flex gap-2">
                                        <input type="hidden" name="productId" value="${product.id}">
                                        <input type="number" name="quantity" value="1" min="1" 
                                               max="${product.stock}" class="form-control" required>
                                        <button type="submit" class="btn btn-primary">
                                            <i class="fas fa-cart-plus"></i> Add to Cart
                                        </button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <button class="btn btn-secondary w-100" disabled>Out of Stock</button>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/user/login" class="btn btn-primary w-100">
                                Login to Purchase
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </c:forEach>
</div>

<c:if test="${empty products}">
    <div class="alert alert-info" role="alert">
        No products available.
    </div>
</c:if>

<script>
function deleteProduct(productId) {
    if (confirm('Are you sure you want to delete this product?')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '${pageContext.request.contextPath}/admin/product/delete';
        
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'id';
        input.value = productId;
        
        form.appendChild(input);
        document.body.appendChild(form);
        form.submit();
    }
}
</script>

<%@ include file="includes/footer.jsp" %> 