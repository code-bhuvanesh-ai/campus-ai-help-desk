package com.campushelpdesk;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/my-tickets")
public class MyTicketsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        // Check login session
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.html");
            return;
        }

        // Get logged-in user's ID
        int userId = (Integer) session.getAttribute("userId");

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        // -----------------------------------------
        // HTML START
        // -----------------------------------------

        out.println("<!DOCTYPE html>");
        out.println("<html>");

        out.println("<head>");

        out.println("<meta charset='UTF-8'>");

        out.println("<meta name='viewport' "
                + "content='width=device-width, initial-scale=1.0'>");

        out.println("<title>My Tickets - Campus AI Helpdesk</title>");

        // -----------------------------------------
        // CSS
        // -----------------------------------------

        out.println("<style>");

        out.println("* {");
        out.println("box-sizing: border-box;");
        out.println("}");

        out.println("body {");
        out.println("font-family: Arial, sans-serif;");
        out.println("background-color: #f1f5f9;");
        out.println("margin: 0;");
        out.println("color: #1e293b;");
        out.println("}");

        // Header
        out.println("header {");
        out.println("background: linear-gradient(135deg, #243b55, #141e30);");
        out.println("color: white;");
        out.println("padding: 28px 40px;");
        out.println("}");

        out.println(".header-content {");
        out.println("max-width: 1250px;");
        out.println("margin: auto;");
        out.println("}");

        out.println("header h1 {");
        out.println("margin: 0;");
        out.println("font-size: 30px;");
        out.println("}");

        out.println("header p {");
        out.println("margin: 8px 0 0;");
        out.println("opacity: 0.85;");
        out.println("font-size: 15px;");
        out.println("}");

        // Navigation
        out.println("nav {");
        out.println("background-color: #172a3a;");
        out.println("padding: 15px;");
        out.println("text-align: center;");
        out.println("}");

        out.println("nav a {");
        out.println("color: white;");
        out.println("text-decoration: none;");
        out.println("margin: 0 18px;");
        out.println("font-size: 15px;");
        out.println("font-weight: 500;");
        out.println("}");

        out.println("nav a:hover {");
        out.println("text-decoration: underline;");
        out.println("}");

        // Main container
        out.println(".container {");
        out.println("width: 94%;");
        out.println("max-width: 1250px;");
        out.println("margin: 35px auto;");
        out.println("}");

        // Page heading
        out.println(".page-heading {");
        out.println("background: white;");
        out.println("padding: 25px;");
        out.println("border-radius: 12px;");
        out.println("box-shadow: 0 2px 8px rgba(0,0,0,0.06);");
        out.println("margin-bottom: 25px;");
        out.println("}");

        out.println(".page-heading h2 {");
        out.println("margin: 0 0 8px;");
        out.println("font-size: 25px;");
        out.println("}");

        out.println(".page-heading p {");
        out.println("margin: 0;");
        out.println("color: #64748b;");
        out.println("font-size: 14px;");
        out.println("}");

        // Ticket section
        out.println(".ticket-section {");
        out.println("background: white;");
        out.println("border-radius: 12px;");
        out.println("padding: 25px;");
        out.println("box-shadow: 0 2px 8px rgba(0,0,0,0.06);");
        out.println("}");

        out.println(".ticket-section h3 {");
        out.println("margin: 0;");
        out.println("font-size: 20px;");
        out.println("}");

        out.println(".section-subtitle {");
        out.println("color: #64748b;");
        out.println("font-size: 14px;");
        out.println("margin-top: 6px;");
        out.println("}");

        // Table wrapper
        out.println(".table-wrapper {");
        out.println("overflow-x: auto;");
        out.println("margin-top: 20px;");
        out.println("}");

        out.println("table {");
        out.println("width: 100%;");
        out.println("border-collapse: collapse;");
        out.println("min-width: 1100px;");
        out.println("}");

        out.println("th {");
        out.println("background-color: #243b55;");
        out.println("color: white;");
        out.println("padding: 13px;");
        out.println("text-align: left;");
        out.println("font-size: 13px;");
        out.println("}");

        out.println("td {");
        out.println("border-bottom: 1px solid #e2e8f0;");
        out.println("padding: 13px;");
        out.println("font-size: 13px;");
        out.println("vertical-align: top;");
        out.println("}");

        out.println("tr:hover {");
        out.println("background-color: #f8fafc;");
        out.println("}");

        // Ticket ID
        out.println(".ticket-id {");
        out.println("font-weight: bold;");
        out.println("color: #243b55;");
        out.println("}");

        // Issue
        out.println(".issue-title {");
        out.println("font-weight: bold;");
        out.println("color: #1e293b;");
        out.println("}");

        // Badge
        out.println(".badge {");
        out.println("display: inline-block;");
        out.println("padding: 5px 10px;");
        out.println("border-radius: 20px;");
        out.println("font-size: 12px;");
        out.println("font-weight: bold;");
        out.println("white-space: nowrap;");
        out.println("}");

        // Priority badges
        out.println(".priority-high {");
        out.println("background-color: #fee2e2;");
        out.println("color: #b91c1c;");
        out.println("}");

        out.println(".priority-medium {");
        out.println("background-color: #fef3c7;");
        out.println("color: #b45309;");
        out.println("}");

        out.println(".priority-low {");
        out.println("background-color: #dcfce7;");
        out.println("color: #15803d;");
        out.println("}");

        // Status badges
        out.println(".status-open {");
        out.println("background-color: #fef3c7;");
        out.println("color: #b45309;");
        out.println("}");

        out.println(".status-progress {");
        out.println("background-color: #dbeafe;");
        out.println("color: #1d4ed8;");
        out.println("}");

        out.println(".status-resolved {");
        out.println("background-color: #dcfce7;");
        out.println("color: #15803d;");
        out.println("}");

        // Resolution notes
        out.println(".notes {");
        out.println("max-width: 220px;");
        out.println("line-height: 1.5;");
        out.println("color: #475569;");
        out.println("}");

        out.println(".no-notes {");
        out.println("color: #94a3b8;");
        out.println("font-style: italic;");
        out.println("}");

        // History
        out.println(".history {");
        out.println("min-width: 200px;");
        out.println("}");

        out.println(".history-item {");
        out.println("border-left: 3px solid #cbd5e1;");
        out.println("padding: 5px 0 8px 12px;");
        out.println("margin-bottom: 8px;");
        out.println("}");

        out.println(".history-status {");
        out.println("font-weight: bold;");
        out.println("font-size: 12px;");
        out.println("color: #334155;");
        out.println("}");

        out.println(".history-date {");
        out.println("font-size: 11px;");
        out.println("color: #94a3b8;");
        out.println("margin-top: 3px;");
        out.println("}");

        out.println(".no-history {");
        out.println("color: #94a3b8;");
        out.println("font-style: italic;");
        out.println("font-size: 12px;");
        out.println("}");

        // Empty state
        out.println(".empty {");
        out.println("text-align: center;");
        out.println("padding: 40px;");
        out.println("color: #64748b;");
        out.println("}");

        // Responsive
        out.println("@media (max-width: 700px) {");

        out.println("header {");
        out.println("padding: 25px;");
        out.println("}");

        out.println("nav a {");
        out.println("margin: 0 8px;");
        out.println("}");

        out.println(".container {");
        out.println("width: 96%;");
        out.println("}");

        out.println("}");

        out.println("</style>");

        out.println("</head>");

        // -----------------------------------------
        // BODY
        // -----------------------------------------

        out.println("<body>");

        // Header
        out.println("<header>");

        out.println("<div class='header-content'>");

        out.println("<h1>Campus AI Helpdesk</h1>");

        out.println("<p>Smart Campus Support System</p>");

        out.println("</div>");

        out.println("</header>");

        // Navigation
        out.println("<nav>");

        out.println("<a href='index.html'>Home</a>");
        out.println("<a href='index.html'>Create Ticket</a>");
        out.println("<a href='my-tickets'>My Tickets</a>");
        out.println("<a href='logout'>Logout</a>");

        out.println("</nav>");

        // Main
        out.println("<div class='container'>");

        // Page heading
        out.println("<div class='page-heading'>");

        out.println("<h2>My Tickets</h2>");

        out.println("<p>");
        out.println("Track your submitted tickets, status, priority, "
                + "resolution notes and status history.");
        out.println("</p>");

        out.println("</div>");

        // Ticket section
        out.println("<div class='ticket-section'>");

        out.println("<h3>Your Support Requests</h3>");

        out.println("<p class='section-subtitle'>");
        out.println("Latest tickets are shown first.");
        out.println("</p>");

        out.println("<div class='table-wrapper'>");

        out.println("<table>");

        // Table header
        out.println("<tr>");

        out.println("<th>Ticket ID</th>");
        out.println("<th>Issue</th>");
        out.println("<th>Category</th>");
        out.println("<th>Department</th>");
        out.println("<th>Priority</th>");
        out.println("<th>Status</th>");
        out.println("<th>Resolution Notes</th>");
        out.println("<th>Status History</th>");
        out.println("<th>Created At</th>");

        out.println("</tr>");

        // -----------------------------------------
        // Ticket Query
        // -----------------------------------------

        String sql =
                "SELECT ticket_id, subject, category, department, " +
                "priority, status, resolution_notes, created_at " +
                "FROM tickets " +
                "WHERE user_id = ? " +
                "ORDER BY created_at DESC";

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                boolean found = false;

                while (rs.next()) {

                    found = true;

                    int ticketId =
                            rs.getInt("ticket_id");

                    String subject =
                            rs.getString("subject");

                    String category =
                            rs.getString("category");

                    String department =
                            rs.getString("department");

                    String priority =
                            rs.getString("priority");

                    String status =
                            rs.getString("status");

                    String resolutionNotes =
                            rs.getString("resolution_notes");

                    String createdAt =
                            rs.getString("created_at");

                    // -----------------------------------------
                    // Priority Class
                    // -----------------------------------------

                    String priorityClass =
                            "priority-low";

                    if ("High".equalsIgnoreCase(priority)) {

                        priorityClass =
                                "priority-high";

                    } else if ("Medium".equalsIgnoreCase(priority)) {

                        priorityClass =
                                "priority-medium";
                    }

                    // -----------------------------------------
                    // Status Class
                    // -----------------------------------------

                    String statusClass =
                            "status-open";

                    if ("In Progress".equalsIgnoreCase(status)) {

                        statusClass =
                                "status-progress";

                    } else if ("Resolved".equalsIgnoreCase(status)) {

                        statusClass =
                                "status-resolved";
                    }

                    out.println("<tr>");

                    // Ticket ID
                    out.println("<td class='ticket-id'>#"
                            + ticketId + "</td>");

                    // Issue
                    out.println("<td class='issue-title'>"
                            + subject + "</td>");

                    // Category
                    out.println("<td>"
                            + category + "</td>");

                    // Department
                    out.println("<td>"
                            + department + "</td>");

                    // Priority
                    out.println("<td>");

                    out.println("<span class='badge "
                            + priorityClass + "'>"
                            + priority
                            + "</span>");

                    out.println("</td>");

                    // Status
                    out.println("<td>");

                    out.println("<span class='badge "
                            + statusClass + "'>"
                            + status
                            + "</span>");

                    out.println("</td>");

                    // Resolution Notes
                    out.println("<td class='notes'>");

                    if (resolutionNotes == null ||
                            resolutionNotes.trim().isEmpty()) {

                        out.println(
                                "<span class='no-notes'>"
                                + "Not available yet"
                                + "</span>"
                        );

                    } else {

                        out.println(resolutionNotes);
                    }

                    out.println("</td>");

                    // -----------------------------------------
                    // Status History
                    // -----------------------------------------

                    out.println("<td class='history'>");

                    String historySQL =
                            "SELECT status, updated_at " +
                            "FROM ticket_status_history " +
                            "WHERE ticket_id = ? " +
                            "ORDER BY updated_at ASC";

                    try (PreparedStatement historyPS =
                                 connection.prepareStatement(historySQL)) {

                        historyPS.setInt(1, ticketId);

                        try (ResultSet historyRS =
                                     historyPS.executeQuery()) {

                            boolean historyFound = false;

                            while (historyRS.next()) {

                                historyFound = true;

                                String historyStatus =
                                        historyRS.getString("status");

                                String updatedAt =
                                        historyRS.getString("updated_at");

                                out.println(
                                        "<div class='history-item'>"
                                );

                                out.println(
                                        "<div class='history-status'>"
                                        + historyStatus
                                        + "</div>"
                                );

                                out.println(
                                        "<div class='history-date'>"
                                        + updatedAt
                                        + "</div>"
                                );

                                out.println("</div>");
                            }

                            if (!historyFound) {

                                out.println(
                                        "<span class='no-history'>"
                                        + "No status changes yet."
                                        + "</span>"
                                );
                            }
                        }

                    } catch (Exception historyError) {

                        out.println(
                                "<span class='no-history'>"
                                + "History unavailable."
                                + "</span>"
                        );
                    }

                    out.println("</td>");

                    // Created At
                    out.println("<td>"
                            + createdAt + "</td>");

                    out.println("</tr>");
                }

                // No tickets
                if (!found) {

                    out.println("<tr>");

                    out.println(
                            "<td colspan='9' class='empty'>"
                    );

                    out.println(
                            "You haven't created any tickets yet."
                    );

                    out.println("</td>");

                    out.println("</tr>");
                }
            }

        } catch (Exception e) {

            out.println("<tr>");

            out.println(
                    "<td colspan='9' class='empty'>"
            );

            out.println(
                    "Database Error: " + e.getMessage()
            );

            out.println("</td>");

            out.println("</tr>");
        }

        out.println("</table>");

        out.println("</div>");

        out.println("</div>");

        out.println("</div>");

        out.println("</body>");

        out.println("</html>");
    }
}