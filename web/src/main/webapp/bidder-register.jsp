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
  <title>Bidder Registration</title>
</head>
<body class="bg-light">

<div class="container">
  <div class="row justify-content-center align-items-center min-vh-100">
    <div class="col-md-6 col-lg-5">
      <div class="card shadow-lg">
        <div class="card-body text-center">
          <img src="img/logo.png" alt="Logo" class="img-fluid mb-3" style="max-height: 220px;">
          <h3 class="card-title mb-4">Bidder Registration</h3>

          <form action="bidder-register" method="post">

            <div class="mb-3 text-start">
              <label for="email" class="form-label">Email</label>
              <input type="email" name="email" class="form-control" id="email" autocomplete="off" required>
            </div>

            <div class="mb-3 text-start">
              <label for="name" class="form-label">Full Name</label>
              <input type="text" name="name" class="form-control" id="name" autocomplete="off" required>
            </div>

            <div class="mb-3 text-start">
              <label for="password" class="form-label">Password</label>
              <input type="password" name="password" class="form-control" id="password" autocomplete="off" placeholder="More than 5 both letters and numbers" required>
            </div>

            <div class="d-grid">
              <button type="submit" class="btn btn-primary">Finish Registration</button>
            </div>
          </form>

          <% if (request.getParameter("error") != null) { %>
          <div class="alert alert-danger mt-3" role="alert">
            Has already signed up or credentials are invalid
          </div>
          <% } else if (request.getParameter("success") != null) { %>
          <div class="alert alert-success mt-3" role="alert">
            Registration successful. Please <a href="bidder-login.jsp">login</a>.
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
