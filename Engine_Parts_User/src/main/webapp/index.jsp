<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="includes/header.jsp" %>

<div class="container py-4">
    <!-- Hero Section -->
    <div class="row mb-4">
        <div class="col-md-6">
            <h1>Welcome to Engine Parts Store</h1>
            <p class="lead">Your one-stop shop for quality engine parts and accessories.</p>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary btn-lg">Shop Now</a>
        </div>
        <div class="col-md-6">
            <img src="https://www.enginelabs.com/wp-content/uploads/2016/02/Engine-Parts.jpg" 
                 alt="Engine Parts" class="img-fluid rounded shadow">
        </div>
    </div>

    <!-- Featured Products -->
    <div class="row">
        <div class="col-12">
            <h2 class="mb-4">Featured Products</h2>
        </div>
        <c:forEach items="${products}" var="product">
            <div class="col-md-3 mb-4">
                <div class="card h-100">

                    <div class="card-body">
                        <h5 class="card-title">${product.name}</h5>
                        <p class="card-text">${product.description}</p>
                        <p class="card-text"><strong>$${String.format("%.2f", product.price)}</strong></p>
                        <form action="${pageContext.request.contextPath}/cart/add" method="post" style="display:inline;">
                            <input type="hidden" name="productId" value="${product.id}">
                            <input type="hidden" name="quantity" value="1">
                            <button type="submit" class="btn btn-primary">Add to Cart</button>
                        </form>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<%@ include file="includes/footer.jsp" %> 