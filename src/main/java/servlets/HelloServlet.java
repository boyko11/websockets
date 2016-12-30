package servlets;

/**
 * Created by boyko on 12/30/16.
 */
import java.io.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.*;

@WebServlet("/test")
public class HelloServlet extends HttpServlet {
    public void doGet (HttpServletRequest req,
                       HttpServletResponse res)
            throws ServletException, IOException {
        PrintWriter out = res.getWriter();

        out.println("Test");
        out.close();
    }
}
