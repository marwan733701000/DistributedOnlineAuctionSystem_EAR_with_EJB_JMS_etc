<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
    Boolean isAdmin = (Boolean) session.getAttribute("admin");
    if (isAdmin != null && isAdmin) {
        response.sendRedirect("create-auction");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="css/bootstrap.css" rel="stylesheet">
    <link rel="icon" href="img/logo.png" />
    <title>Admin Login</title>
</head>
<body class="bg-light">

<div class="container">
    <div class="row justify-content-center align-items-center min-vh-100">
        <div class="col-md-6 col-lg-5">
            <div class="card shadow-lg">
                <div class="card-body text-center">
                    <img src="img/logo.png" alt="Admin Icon" class="img-fluid mb-3" style="max-height: 220px;">

                    <h3 class="card-title mb-4">Admin Login</h3>

                    <form action="admin-login" method="post">
                        <div class="mb-3 text-start">
                            <label for="username" class="form-label">Username</label>
                            <input type="text" name="username" class="form-control" id="username" autocomplete="off" required>
                        </div>

                        <div class="mb-3 text-start">
                            <label for="password" class="form-label">Password</label>
                            <input type="password" name="password" class="form-control" id="password" autocomplete="off" required>
                        </div>

                        <div class="d-grid">
                            <button type="submit" class="btn btn-primary">Login</button>
                        </div>
                    </form>

                    <% if (request.getParameter("error") != null) { %>
                    <div class="alert alert-danger mt-3" role="alert">
                        Invalid login credentials.
                    </div>
                    <% } %>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="js/bootstrap.bundle.js"></script>
</body>
</html>
