<%@ include file="../includes/header.jsp" %>

<div class="row justify-content-center">
    <div class="col-md-6">
        <div class="card">
            <div class="card-header">
                <h4 class="mb-0">Admin Login</h4>
            </div>
            <div class="card-body">
                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert">
                        ${error}
                    </div>
                </c:if>
                <form action="${pageContext.request.contextPath}/admin/login" method="post">
                    <div class="mb-3">
                        <label for="email" class="form-label">Email address</label>
                        <input type="email" class="form-control" id="email" name="email" value="admin@gmail.com" required>
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Password</label>
                        <input type="password" class="form-control" id="password" name="password" value="admin" required>
                    </div>
                    <button type="submit" class="btn btn-primary">Login as Admin</button>
                </form>
                <div class="mt-3">
                    <div class="alert alert-info" role="alert">
                        <h5 class="alert-heading">Default Admin Credentials</h5>
                        <p class="mb-0">Email: admin@gmail.com<br>Password: admin</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="../includes/footer.jsp" %> 