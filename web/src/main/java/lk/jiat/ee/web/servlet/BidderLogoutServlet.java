package lk.jiat.ee.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/bidder-logout")
public class BidderLogoutServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        Object bidderObj = (session != null) ? session.getAttribute("bidder") : null;

        if (bidderObj != null) {
            session.invalidate();
            response.sendRedirect("bidder-login.jsp");
        } else {
            response.sendRedirect("index.jsp");
        }
    }
}
