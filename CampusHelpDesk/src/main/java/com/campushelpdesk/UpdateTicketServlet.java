package com.campushelpdesk;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/update-ticket")
public class UpdateTicketServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws IOException {

        // Check login session
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.html");
            return;
        }

        // Check staff role
        String role = (String) session.getAttribute("role");

        if (!"staff".equalsIgnoreCase(role)) {

            response.setContentType("text/html;charset=UTF-8");

            response.getWriter().println(
                    "<h2>Access Denied</h2>"
            );

            response.getWriter().println(
                    "<p>Only staff members can update tickets.</p>"
            );

            response.getWriter().println(
                    "<a href='index.html'>Back to Home</a>"
            );

            return;
        }

        // Get logged-in staff information
        int staffId = (Integer) session.getAttribute("userId");

        String staffDepartment =
                (String) session.getAttribute("department");

        String ticketIdParameter =
                request.getParameter("ticketId");

        String newStatus =
                request.getParameter("status");

        String resolutionNotes =
                request.getParameter("resolutionNotes");

        if (ticketIdParameter == null || newStatus == null) {

            response.setContentType("text/html;charset=UTF-8");

            response.getWriter().println(
                    "<h2>Invalid Request</h2>"
            );

            response.getWriter().println(
                    "<p>Required ticket information is missing.</p>"
            );

            return;
        }

        int ticketId;

        try {

            ticketId = Integer.parseInt(ticketIdParameter);

        } catch (NumberFormatException e) {

            response.setContentType("text/html;charset=UTF-8");

            response.getWriter().println(
                    "<h2>Invalid Ticket ID</h2>"
            );

            return;
        }

        Connection connection = null;

        try {

            connection = DBConnection.getConnection();

            connection.setAutoCommit(false);

            /*
             * Update only if the ticket belongs to
             * the logged-in staff member's department.
             */
            String updateSQL =
                    "UPDATE tickets " +
                    "SET status = ?, resolution_notes = ? " +
                    "WHERE ticket_id = ? " +
                    "AND department = ?";

            int rowsUpdated;

            try (PreparedStatement ps =
                         connection.prepareStatement(updateSQL)) {

                ps.setString(1, newStatus);
                ps.setString(2, resolutionNotes);
                ps.setInt(3, ticketId);
                ps.setString(4, staffDepartment);

                rowsUpdated = ps.executeUpdate();
            }

            /*
             * If no row was updated, either:
             * 1. Ticket does not exist, or
             * 2. Ticket belongs to another department.
             */
            if (rowsUpdated == 0) {

                connection.rollback();

                response.setContentType("text/html;charset=UTF-8");

                response.getWriter().println(
                        "<h2>Access Denied</h2>"
                );

                response.getWriter().println(
                        "<p>You can only update tickets assigned "
                        + "to your department.</p>"
                );

                response.getWriter().println(
                        "<a href='staff-dashboard'>Back to Dashboard</a>"
                );

                return;
            }

            // Save status history
            String historySQL =
                    "INSERT INTO ticket_status_history " +
                    "(ticket_id, status, updated_by) " +
                    "VALUES (?, ?, ?)";

            try (PreparedStatement ps =
                         connection.prepareStatement(historySQL)) {

                ps.setInt(1, ticketId);
                ps.setString(2, newStatus);
                ps.setInt(3, staffId);

                ps.executeUpdate();
            }

            // Commit both operations
            connection.commit();

            response.sendRedirect("staff-dashboard");

        } catch (Exception e) {

            if (connection != null) {

                try {
                    connection.rollback();
                } catch (Exception ignored) {
                }
            }

            response.setContentType("text/html;charset=UTF-8");

            response.getWriter().println(
                    "<h2>Database Error</h2>"
            );

            response.getWriter().println(
                    "<p>" + e.getMessage() + "</p>"
            );

        } finally {

            if (connection != null) {

                try {
                    connection.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}