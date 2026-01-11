<%
  response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
  response.setHeader("Pragma", "no-cache");
  response.setDateHeader("Expires", 0);
  if (session.getAttribute("bidder") != null) {
    response.sendRedirect("index.jsp");
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
  <title>Bidder Login</title>
</head>
<body class="bg-light">

<div class="container">
  <div class="row justify-content-center align-items-center min-vh-100">
    <div class="col-md-6 col-lg-5">
      <div class="card shadow-lg">
        <div class="card-body text-center">
          <img src="img/logo.png" alt="Logo" class="img-fluid mb-3" style="max-height: 220px;">
          <h3 class="card-title mb-4">Bidder Login</h3>

          <form action="bidder-login" method="post">
            <div class="mb-3 text-start">
              <label for="email" class="form-label">Email</label>
              <input type="email" name="email" class="form-control" id="email" autocomplete="off" required>
            </div>

            <div class="mb-3 text-start">
              <label for="password" class="form-label">Password</label>
              <input type="password" name="password" class="form-control" id="password" autocomplete="off" required>
            </div>

            <div class="d-grid">
              <button type="submit" class="btn btn-primary">Continue Logging In</button>
            </div>
          </form>

          <div class="d-grid">
            <a href="bidder-register.jsp" class="btn btn-success mt-3">New Here? Register</a>
          </div>

          <% if (request.getParameter("error") != null) { %>
          <div class="alert alert-danger mt-3" role="alert">
            Invalid email or password.
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
