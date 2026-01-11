<%@ page import="java.util.List" %>
<%@ page import="lk.jiat.ee.core.model.Auction" %>
<%@ page import="lk.jiat.ee.core.model.Bidder" %>
<%@ page import="lk.jiat.ee.ejb.remote.AuctionService" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.time.ZoneId" %>

<%
    Boolean isAdmin = (Boolean) session.getAttribute("admin");
    if (isAdmin == null || !isAdmin) {
        response.sendRedirect("admin-login");
        return;
    }
    List<Auction> auctions;
    try {
        InitialContext ctx = new InitialContext();
        AuctionService auctionService = (AuctionService) ctx.lookup("java:global/ee-ear/lk.jiat.ee-ejb-1.0/AuctionSessionBean!lk.jiat.ee.ejb.remote.AuctionService");
        auctions = auctionService.getAllAuctions();
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd 'of' yyyy 'at' hh:mm a", Locale.ENGLISH);
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Created Auctions</title>
    <link rel="icon" href="img/logo.png" />
    <link href="css/bootstrap.css" rel="stylesheet">
</head>
<body>
<div class="container-fluid mt-4">
    <div class="row">
        <div class="col-12">
            <div class="text-center mb-4">
                <img src="img/logo.png" alt="Logo" class="img-fluid mb-3" style="max-height: 220px;" />
                <div>
                    <h2 class="text-primary mb-2">Created Auctions</h2>
                    <span class="badge bg-secondary"><%=auctions.size()%> Listed So Far</span>
                </div>
            </div>
            <div class="auction-container" style="max-height: calc(100vh - 200px); overflow-y: auto; overflow-x: hidden;">
                <div class="row g-4">
                    <%
                        for (Auction auction : auctions) {
                    %>
                    <div class="col-lg-6 col-xl-4">
                        <div class="card h-100 shadow-sm border-0">
                            <div class="card-header bg-light border-0">
                                <h5 class="card-title text-primary mb-0"><%=auction.getProductName()%></h5>
                            </div>
                            <div class="card-body">
                                <p class="card-text text-muted mb-3"><%=auction.getDescription()%></p>

                                <div class="bg-light rounded p-3 mb-3">
                                    <div class="row text-center">
                                        <div class="col-6">
                                            <small class="text-muted d-block">Bids Started at</small>
                                            <strong class="text-success">Rs. <%=String.format("%.2f", auction.getStartingBid())%></strong>
                                        </div>
                                        <div class="col-6">
                                            <small class="text-muted d-block">Auction ID</small>
                                            <strong class="text-danger">Rs. <%= String.format("%05d", auction.getId()) %></strong>
                                        </div>
                                    </div>
                                    <hr class="my-2">
                                    <div class="text-center">
                                        <small class="text-muted d-block">Current Highest Bid</small>
                                        <h4 class="text-primary mb-1">Rs. <span id="currentBid<%=auction.getId()%>"><%=String.format("%.2f", auction.getCurrentBid())%></span></h4>
                                        <small class="text-muted">
                                             <span id="highestBidder<%=auction.getId()%>">
                                                    <%= auction.getHighestBidderEmail() != null ? "by " + auction.getHighestBidderEmail() : "No bids yet" %>
                                                </span>
                                        </small>
                                    </div>
                                </div>
                                <div class="text-center mb-3">
                                    <div class="alert alert-info py-2 mb-2">
                                        <small><strong>Time Remaining:</strong></small>
                                        <div id="timer<%=auction.getId()%>" class="fw-bold"></div>
                                    </div>
                                    <small class="text-muted">
                                        Ends: <%=auction.getEndTime().format(formatter)%>
                                    </small>
                                </div>
                                <div id="buttonSection<%=auction.getId()%>" class="text-center">
                                    <% if (auction.isActive()) { %>
                                    <button class="btn btn-danger" onclick="disableAuction(<%=auction.getId()%>)">
                                        Disable Auction
                                    </button>
                                    <% } else { %>
                                    <button class="btn btn-success" onclick="disableAuction(<%=auction.getId()%>)">
                                        Enable Auction
                                    </button>
                                    <% } %>
                                </div>
                                <div id="auctionEnded<%=auction.getId()%>" class="text-center" style="display: none;">
                                    <div class="alert alert-warning">
                                        <h6 class="mb-0">Bidding Has Ended</h6>
                                        <small>This auction is now closed</small>
                                    </div>
                                </div>
                            </div>
                            <div class="card-footer bg-transparent border-0 pt-0">
                                <small class="text-muted">
                                    <i class="bi bi-clock"></i> Started: <%=auction.getStartTime().format(formatter)%>
                                </small>
                            </div>
                        </div>
                    </div>
                    <%
                        }
                    %>
                </div>
            </div>
        </div>
    </div>
</div>
<div aria-live="polite" aria-atomic="true" class="position-relative">
    <div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 9999">
    </div>
</div>

<script src="js/bootstrap.bundle.js"></script>
<script>
    var socket = new WebSocket("ws://" + window.location.host + "/ee-app/auction-updates");

    socket.onmessage = function(event) {
        var data = JSON.parse(event.data);

        if (data.type === 'NEW_AUCTION') {
            handleNewAuction(data.auction);
        } else if (data.type === 'AUCTION_STATUS_UPDATE') {
            handleAuctionStatusUpdate(data);
        }else {
            var bid = data;
            var bidElement = document.getElementById("currentBid" + bid.auctionId);
            var nextBidElement = document.getElementById("nextBid" + bid.auctionId);
            var highestBidderElement = document.getElementById("highestBidder" + bid.auctionId);
            var bidInput = document.getElementById("bidInput" + bid.auctionId);

            if (bidElement) {
                bidElement.textContent = bid.amount.toFixed(2);
            }

            if (nextBidElement) {
                nextBidElement.textContent = (bid.amount + 2).toFixed(2);
            }

            if (highestBidderElement && bid.bidderEmail) {
                highestBidderElement.textContent = "by " + bid.bidderEmail;
            }
            if (bidInput) {
                bidInput.min = (bid.amount + 2).toFixed(2);
                bidInput.placeholder = "Min: " + (bid.amount + 2).toFixed(2);
            }
        }
    };

    function formatDateTime(dateTimeString) {
        try {
            var date = new Date(dateTimeString);
            var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

            var month = months[date.getMonth()];
            var day = String(date.getDate()).padStart(2, '0');
            var year = date.getFullYear();
            var hours = date.getHours();
            var minutes = String(date.getMinutes()).padStart(2, '0');
            var ampm = hours >= 12 ? 'PM' : 'AM';
            hours = hours % 12;
            hours = hours ? hours : 12;
            hours = String(hours).padStart(2, '0');

            return month + ' ' + day + ' of ' + year + ' at ' + hours + ':' + minutes + ' ' + ampm;
        } catch (e) {
            console.error('Date formatting error:', e);
            return 'Invalid Date';
        }
    }

    function handleNewAuction(auction) {
        showToast('New auction added: ' + auction.productName, 'success');
        var countBadge = document.querySelector('.badge.bg-secondary');
        if (countBadge) {
            var currentCount = parseInt(countBadge.textContent.split(' ')[0]);
            countBadge.textContent = (currentCount + 1) + ' Listed So Far';
        }
        var startTimeFormatted = formatDateTime(auction.startTime);
        var endTimeFormatted = formatDateTime(auction.endTime);

        var newAuctionHtml =
            '<div class="col-lg-6 col-xl-4">' +
            '<div class="card h-100 shadow-sm border-0">' +
            '<div class="card-header bg-light border-0">' +
            '<h5 class="card-title text-primary mb-0">' + auction.productName + '</h5>' +
            '</div>' +
            '<div class="card-body">' +
            '<p class="card-text text-muted mb-3">' + auction.description + '</p>' +
            '<div class="bg-light rounded p-3 mb-3">' +
            '<div class="row text-center">' +
            '<div class="col-6">' +
            '<small class="text-muted d-block">Bids Started at</small>' +
            '<strong class="text-success">Rs. ' + auction.startingBid.toFixed(2) + '</strong>' +
            '</div>' +
            '<div class="col-6">' +
            '<small class="text-muted d-block">Auction ID</small>' +
            '<strong class="text-danger">Rs. ' + String(auction.id).padStart(5, '0') + '</strong>' +
            '</div>' +
            '</div>' +
            '<hr class="my-2">' +
            '<div class="text-center">' +
            '<small class="text-muted d-block">Current Highest Bid</small>' +
            '<h4 class="text-primary mb-1">Rs. <span id="currentBid' + auction.id + '">' + auction.startingBid.toFixed(2) + '</span></h4>' +
            '<small class="text-muted">' +
            '<span id="highestBidder' + auction.id + '">No bids yet</span>' +
            '</small>' +
            '</div>' +
            '</div>' +
            '<div class="text-center mb-3">' +
            '<div class="alert alert-info py-2 mb-2">' +
            '<small><strong>Time Remaining:</strong></small>' +
            '<div id="timer' + auction.id + '" class="fw-bold"></div>' +
            '</div>' +
            '<small class="text-muted">' +
            'Ends: ' + endTimeFormatted +
            '</small>' +
            '</div>' +
            '<div id="buttonSection' + auction.id + '" class="text-center">' +
            '<button class="btn btn-danger" onclick="disableAuction(' + auction.id + ')">' +
            'Disable Auction' +
            '</button>' +
            '</div>' +
            '<div id="auctionEnded' + auction.id + '" class="text-center" style="display: none;">' +
            '<div class="alert alert-warning">' +
            '<h6 class="mb-0">Bidding Has Ended</h6>' +
            '<small>This auction is now closed</small>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="card-footer bg-transparent border-0 pt-0">' +
            '<small class="text-muted">' +
            '<i class="bi bi-clock"></i> Started: ' + startTimeFormatted +
            '</small>' +
            '</div>' +
            '</div>' +
            '</div>';
        var auctionRow = document.querySelector('.auction-container .row');
        if (auctionRow) {
            auctionRow.insertAdjacentHTML('beforeend', newAuctionHtml);
            var endTimeISO = new Date(auction.endTime).toISOString();
            startCountdown(endTimeISO, "timer" + auction.id, auction.id);
        }
    }

    socket.onerror = function(error) {
        console.error('WebSocket error:', error);
    };

    socket.onclose = function(event) {
        console.log('WebSocket connection closed');
    };

    function showToast(message, type = 'success') {
        console.log('showToast called with:', message, type);
        const toast = document.createElement('div');
        toast.className = 'toast show';
        toast.setAttribute('role', 'alert');
        toast.setAttribute('aria-live', 'assertive');
        toast.setAttribute('aria-atomic', 'true');
        toast.style.position = 'relative';
        const toastHeader = document.createElement('div');
        toastHeader.className = 'toast-header';
        const bgClass = type === 'success' ? 'bg-success' : 'bg-danger';
        const textClass = 'text-white';
        const strong = document.createElement('strong');
        strong.className = `me-auto ${textClass}`;
        strong.textContent = type === 'success' ? 'Success' : 'Error';
        const closeButton = document.createElement('button');
        closeButton.type = 'button';
        closeButton.className = 'btn-close btn-close-white';
        closeButton.setAttribute('data-bs-dismiss', 'toast');
        closeButton.setAttribute('aria-label', 'Close');
        toastHeader.appendChild(strong);
        toastHeader.appendChild(closeButton);
        const toastBody = document.createElement('div');
        toastBody.className = 'toast-body';
        toastBody.textContent = message;
        toast.appendChild(toastHeader);
        toast.appendChild(toastBody);
        toastHeader.classList.add(bgClass, textClass);
        const toastContainer = document.querySelector('.toast-container');
        toastContainer.appendChild(toast);
        const bsToast = new bootstrap.Toast(toast, {
            autohide: true,
            delay: 5000
        });
        bsToast.show();
        toast.addEventListener('hidden.bs.toast', function() {
            toast.remove();
        });
    }

    function handleAuctionStatusUpdate(data) {
        var auctionId = data.auctionId;
        var isActive = data.active;

        var buttonSection = document.getElementById("buttonSection" + auctionId);
        if (buttonSection) {
            if (isActive) {
                buttonSection.innerHTML = '<button class="btn btn-danger" onclick="disableAuction(' + auctionId + ')">Disable Auction</button>';
            } else {
                buttonSection.innerHTML = '<button class="btn btn-success" onclick="disableAuction(' + auctionId + ')">Enable Auction</button>';
            }
        }

        showToast('Auction ' + (isActive ? 'enabled' : 'disabled') + ' successfully', 'success');
    }

    function disableAuction(auctionId) {
        console.log('Attempting to toggle auction status:', auctionId);
        const button = document.querySelector(`#buttonSection${auctionId} button`);
        if (button) {
            button.disabled = true;
            button.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Processing...';
        }
        fetch('/ee-app/toggle-auction?id=' + auctionId, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        })
            .then(response => response.json())
            .then(data => {
                if (!data.success) {
                    showToast(data.message || 'Failed to toggle auction status', 'error');
                }
            })
            .catch(error => {
                console.error('Toggle auction error:', error);
                showToast('Failed to toggle auction status', 'error');
            })
            .finally(() => {
                if (button) {
                    button.disabled = false;
                }
            });
    }

    function startCountdown(endTimeString, elementId, auctionId) {
        function updateCountdown() {
            var endTime = new Date(endTimeString).getTime();
            var now = new Date().getTime();
            var distance = endTime - now;

            if (distance < 0) {
                document.getElementById(elementId).innerHTML = '<span class="text-danger fw-bold">Auction Ended</span>';
                var buttonSection = document.getElementById("buttonSection" + auctionId);
                var auctionEndedSection = document.getElementById("auctionEnded" + auctionId);
                if (buttonSection) buttonSection.style.display = 'none';
                if (auctionEndedSection) auctionEndedSection.style.display = 'block';
                clearInterval(interval);
                return;
            }

            var days = Math.floor(distance / (1000 * 60 * 60 * 24));
            var hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
            var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
            var seconds = Math.floor((distance % (1000 * 60)) / 1000);

            var timeString = "";
            if (days > 0) timeString += days + "d ";
            timeString += hours + "h " + minutes + "m " + seconds + "s";

            document.getElementById(elementId).innerHTML = '<span class="text-success fw-bold">' + timeString + '</span>';
        }

        updateCountdown();
        var interval = setInterval(updateCountdown, 1000);
    }
    <%
        for (Auction auction : auctions) {
    %>
    startCountdown("<%=auction.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toString()%>", "timer<%=auction.getId()%>", <%=auction.getId()%>);
    <%
        }
    %>
</script>
</body>
</html>