package lk.jiat.ee.web.servlet;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import lk.jiat.ee.core.model.Bidder;
import lk.jiat.ee.ejb.remote.BidderService;

import java.io.IOException;

@WebServlet("/bidder-login")
public class BidderLoginServlet extends HttpServlet {

    @EJB
    private BidderService bidderService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Bidder bidder = bidderService.login(email, password);

        if (bidder != null) {
            HttpSession session = request.getSession();
            session.setAttribute("bidder", bidder);
            response.sendRedirect("index.jsp");
        } else {
            response.sendRedirect("bidder-login.jsp?error=true");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession().getAttribute("bidder") != null) {
            response.sendRedirect("index.jsp");
        } else {
            response.sendRedirect("bidder-login.jsp");
        }
    }
}
