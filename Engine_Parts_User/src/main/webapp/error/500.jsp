<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Server Error</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>
<body>
    <div class="container mt-5 text-center">
        <h1 class="display-1">500</h1>
        <h2>Server Error</h2>
        <p class="lead">An internal server error has occurred. Please try again later.</p>
        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Go to Homepage</a>
    </div>
</body>
</html> 