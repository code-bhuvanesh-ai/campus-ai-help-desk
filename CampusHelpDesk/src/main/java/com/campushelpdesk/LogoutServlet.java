package com.campushelpdesk;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {

        // Get the current session
        HttpSession session = request.getSession(false);

        // Destroy the session
        if (session != null) {
            session.invalidate();
        }

        // Send user back to login page
        response.sendRedirect("login.html");
    }
}