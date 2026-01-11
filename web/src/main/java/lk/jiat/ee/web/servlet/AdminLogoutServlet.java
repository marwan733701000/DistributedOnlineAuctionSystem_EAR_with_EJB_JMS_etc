package lk.jiat.ee.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin-logout")
public class AdminLogoutServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        Boolean isAdmin = (session != null) ? (Boolean) session.getAttribute("admin") : null;

        if (isAdmin != null && isAdmin) {
            session.invalidate();
            response.sendRedirect("admin-login.jsp");
        } else {
            response.sendRedirect("index.jsp");
        }
    }
}
