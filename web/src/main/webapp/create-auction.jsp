<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
  response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
  response.setHeader("Pragma", "no-cache");
  response.setDateHeader("Expires", 0);
  Boolean isAdmin = (Boolean) session.getAttribute("admin");
  if (isAdmin == null || !isAdmin) {
    response.sendRedirect("admin-login");
    return;
  }
  LocalDateTime now = LocalDateTime.now();
  LocalDateTime minEndTime = now.plusMinutes(5);
%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <link href="css/bootstrap.css" rel="stylesheet" />
  <link rel="icon" href="img/logo.png" />
  <title>Create Auction</title>
</head>
<body class="bg-light">

<div class="container">
  <div class="row justify-content-center align-items-center min-vh-100">
    <div class="col-md-7 col-lg-10">
      <div class="card shadow-lg">
        <div class="card-body">
          <div class="text-center mb-4">
            <img src="img/logo.png" alt="Logo" class="img-fluid" style="max-height: 220px;" />
            <h3 class="mt-3">Create New Auction</h3>
          </div>

          <%
            String success = request.getParameter("success");
            if ("true".equals(success)) {
              Integer createdAuctionId = (Integer) session.getAttribute("createdAuctionId");
              String createdProductName = (String) session.getAttribute("createdProductName");
              session.removeAttribute("createdAuctionId");
              session.removeAttribute("createdProductName");
          %>
          <div class="alert alert-success mt-3" role="alert">
            Auction for <strong><%= createdProductName %></strong> was created successfully with ID
            <strong><%= createdAuctionId %></strong>.
          </div>
          <%
          } else if ("true".equals(request.getParameter("error"))) {
            String errorMessage = (String) session.getAttribute("errorMessage");
            session.removeAttribute("errorMessage");
          %>
          <div class="alert alert-danger mt-3" role="alert">
            <%= errorMessage != null ? errorMessage : "Failed to create auction. Please try again." %>
          </div>
          <% } %>

          <form action="create-auction" method="post" class="needs-validation" novalidate>
            <div class="row">
              <div class="mb-3 col-12">
                <label for="productName" class="form-label">Product Name</label>
                <input type="text" class="form-control" id="productName" name="productName" required />
                <div class="invalid-feedback">Please enter the product name.</div>
              </div>
            </div>

            <div class="row">
              <div class="mb-3 col-12 col-lg-6">
                <label for="startingBid" class="form-label">Starting Bid (Rs.)</label>
                <input type="number" step="0.01" min="1" class="form-control" id="startingBid" name="startingBid" required />
                <div class="invalid-feedback">Please enter a valid starting bid (minimum 1).</div>
              </div>

              <div class="mb-3 col-12 col-lg-6">
                <label for="endTime" class="form-label">End Time</label>
                <input type="datetime-local" class="form-control" id="endTime" name="endTime" required
                       min="<%= minEndTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).substring(0, 16) %>" />
                <div class="invalid-feedback">Please select an end time at least 5 minutes from now.</div>
                <small class="text-muted">Minimum must be 5 mins ahead</small>
              </div>
            </div>

            <div class="row">
              <div class="mb-4 col-12">
                <label for="description" class="form-label">Description</label>
                <textarea class="form-control" id="description" name="description" rows="3" required></textarea>
                <div class="invalid-feedback">Please enter the description.</div>
              </div>
            </div>

            <div class="d-grid">
              <button type="submit" class="btn btn-primary btn-lg">Create Auction</button>
            </div>
          </form>

          <div class="d-grid">
            <a href="admin-auction-viewer.jsp" class="btn btn-success mt-4 btn-lg">View Auctions</a>
          </div>

          <form action="admin-logout" method="post" class="mt-4">
            <div class="d-grid">
              <button type="submit" class="btn btn-outline-danger btn-lg">Sign Out</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>

<script src="js/bootstrap.bundle.js"></script>
<script>
  (() => {
    'use strict'
    const forms = document.querySelectorAll('.needs-validation')
    Array.from(forms).forEach(form => {
      form.addEventListener('submit', event => {
        if (!form.checkValidity()) {
          event.preventDefault()
          event.stopPropagation()
        }
        form.classList.add('was-validated')
      }, false)
    })
  })()
</script>
</body>
</html>