package lk.jiat.ee.web.servlet;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import lk.jiat.ee.core.model.Bidder;
import lk.jiat.ee.ejb.remote.BidderService;
import java.io.IOException;

@WebServlet("/bidder-register")
public class BidderRegisterServlet extends HttpServlet {

    @EJB
    private BidderService bidderService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Bidder bidder = new Bidder(email, name, password);

        boolean success = bidderService.registerBidder(bidder);
        if (success) {
            response.sendRedirect("bidder-register.jsp?success=true");
        } else {
            response.sendRedirect("bidder-register.jsp?error=true");
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("bidder-register.jsp");
    }
}
