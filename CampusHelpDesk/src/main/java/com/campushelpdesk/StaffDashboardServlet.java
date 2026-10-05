package com.campushelpdesk;

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

@WebServlet("/staff-dashboard")
public class StaffDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {

        // Check if user is logged in
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.html");
            return;
        }

        // Check if logged-in user is staff
        String role = (String) session.getAttribute("role");

        if (!"staff".equalsIgnoreCase(role)) {

            response.setContentType("text/html;charset=UTF-8");

            PrintWriter out = response.getWriter();

            out.println("<h2>Access Denied</h2>");
            out.println("<p>Only staff members can access the Staff Dashboard.</p>");
            out.println("<a href='index.html'>Back to Home</a>");

            return;
        }

        // Get staff department
        String staffDepartment =
                (String) session.getAttribute("department");

        // -----------------------------------------
        // Dashboard Statistics
        // -----------------------------------------

        int totalTickets = 0;
        int openTickets = 0;
        int inProgressTickets = 0;
        int resolvedTickets = 0;
        int highPriorityTickets = 0;

        String statsSQL =
                "SELECT " +
                "COUNT(*) AS total, " +
                "SUM(CASE WHEN status = 'Open' THEN 1 ELSE 0 END) AS open_count, " +
                "SUM(CASE WHEN status = 'In Progress' THEN 1 ELSE 0 END) AS progress_count, " +
                "SUM(CASE WHEN status = 'Resolved' THEN 1 ELSE 0 END) AS resolved_count, " +
                "SUM(CASE WHEN priority = 'High' THEN 1 ELSE 0 END) AS high_count " +
                "FROM tickets " +
                "WHERE department = ?";

        try (Connection statsConnection =
                     DBConnection.getConnection();
             PreparedStatement statsPS =
                     statsConnection.prepareStatement(statsSQL)) {

            statsPS.setString(1, staffDepartment);

            try (ResultSet statsRS = statsPS.executeQuery()) {

                if (statsRS.next()) {

                    totalTickets =
                            statsRS.getInt("total");

                    openTickets =
                            statsRS.getInt("open_count");

                    inProgressTickets =
                            statsRS.getInt("progress_count");

                    resolvedTickets =
                            statsRS.getInt("resolved_count");

                    highPriorityTickets =
                            statsRS.getInt("high_count");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        // -----------------------------------------
        // HTML START
        // -----------------------------------------

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");

        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");

        out.println("<title>Staff Dashboard - Campus AI Helpdesk</title>");

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

        out.println(".department {");
        out.println("display: inline-block;");
        out.println("background-color: #e2e8f0;");
        out.println("padding: 6px 12px;");
        out.println("border-radius: 20px;");
        out.println("font-size: 14px;");
        out.println("font-weight: bold;");
        out.println("}");

        // -----------------------------------------
        // Statistics
        // -----------------------------------------

        out.println(".stats {");
        out.println("display: grid;");
        out.println("grid-template-columns: repeat(5, 1fr);");
        out.println("gap: 18px;");
        out.println("margin-bottom: 28px;");
        out.println("}");

        out.println(".stat-box {");
        out.println("background: white;");
        out.println("border-radius: 12px;");
        out.println("padding: 22px;");
        out.println("box-shadow: 0 2px 8px rgba(0,0,0,0.06);");
        out.println("transition: transform 0.2s;");
        out.println("}");

        out.println(".stat-box:hover {");
        out.println("transform: translateY(-3px);");
        out.println("}");

        out.println(".stat-top {");
        out.println("font-size: 13px;");
        out.println("font-weight: bold;");
        out.println("color: #64748b;");
        out.println("text-transform: uppercase;");
        out.println("letter-spacing: 0.5px;");
        out.println("}");

        out.println(".stat-number {");
        out.println("font-size: 32px;");
        out.println("font-weight: bold;");
        out.println("margin-top: 10px;");
        out.println("color: #243b55;");
        out.println("}");

        out.println(".stat-description {");
        out.println("font-size: 12px;");
        out.println("color: #94a3b8;");
        out.println("margin-top: 5px;");
        out.println("}");

        // -----------------------------------------
        // Ticket section
        // -----------------------------------------

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

        out.println(".ticket-subtitle {");
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
        out.println("min-width: 1000px;");
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
        out.println("vertical-align: middle;");
        out.println("}");

        out.println("tr:hover {");
        out.println("background-color: #f8fafc;");
        out.println("}");

        // Ticket ID
        out.println(".ticket-id {");
        out.println("font-weight: bold;");
        out.println("color: #243b55;");
        out.println("}");

        // Priority badges
        out.println(".priority {");
        out.println("display: inline-block;");
        out.println("padding: 5px 10px;");
        out.println("border-radius: 20px;");
        out.println("font-size: 12px;");
        out.println("font-weight: bold;");
        out.println("}");

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

        // Status
        out.println(".status-select {");
        out.println("border: 1px solid #cbd5e1;");
        out.println("border-radius: 6px;");
        out.println("padding: 8px;");
        out.println("background: white;");
        out.println("cursor: pointer;");
        out.println("}");

        // Textarea
        out.println("textarea {");
        out.println("width: 190px;");
        out.println("height: 65px;");
        out.println("border: 1px solid #cbd5e1;");
        out.println("border-radius: 6px;");
        out.println("padding: 8px;");
        out.println("font-family: Arial, sans-serif;");
        out.println("font-size: 12px;");
        out.println("resize: vertical;");
        out.println("}");

        out.println("textarea:focus, select:focus {");
        out.println("outline: none;");
        out.println("border-color: #243b55;");
        out.println("}");

        // Update button
        out.println(".update-btn {");
        out.println("background-color: #243b55;");
        out.println("color: white;");
        out.println("border: none;");
        out.println("padding: 9px 16px;");
        out.println("border-radius: 6px;");
        out.println("cursor: pointer;");
        out.println("font-weight: bold;");
        out.println("}");

        out.println(".update-btn:hover {");
        out.println("background-color: #172a3a;");
        out.println("}");

        // Empty state
        out.println(".empty {");
        out.println("text-align: center;");
        out.println("padding: 35px;");
        out.println("color: #64748b;");
        out.println("}");

        // Responsive
        out.println("@media (max-width: 900px) {");

        out.println(".stats {");
        out.println("grid-template-columns: repeat(2, 1fr);");
        out.println("}");

        out.println("header {");
        out.println("padding: 25px;");
        out.println("}");

        out.println("nav a {");
        out.println("margin: 0 8px;");
        out.println("}");

        out.println("}");

        out.println("@media (max-width: 550px) {");

        out.println(".stats {");
        out.println("grid-template-columns: 1fr;");
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

        out.println("<p>Staff / Admin Dashboard</p>");

        out.println("</div>");

        out.println("</header>");

        // Navigation
        out.println("<nav>");

        out.println("<a href='index.html'>Home</a>");
        out.println("<a href='my-tickets'>My Tickets</a>");
        out.println("<a href='staff-dashboard'>Dashboard</a>");
        out.println("<a href='logout'>Logout</a>");

        out.println("</nav>");

        // Main container
        out.println("<div class='container'>");

        // Page heading
        out.println("<div class='page-heading'>");

        out.println("<h2>Support Overview</h2>");

        out.println("<p>Manage and resolve support requests assigned to your department.</p>");

        out.println("<span class='department'>Department: "
                + staffDepartment + "</span>");

        out.println("</div>");

        // -----------------------------------------
        // STATISTICS
        // -----------------------------------------

        out.println("<div class='stats'>");

        // Total
        out.println("<div class='stat-box'>");
        out.println("<div class='stat-top'>Total Tickets</div>");
        out.println("<div class='stat-number'>"
                + totalTickets + "</div>");
        out.println("<div class='stat-description'>All tickets</div>");
        out.println("</div>");

        // Open
        out.println("<div class='stat-box'>");
        out.println("<div class='stat-top'>Open</div>");
        out.println("<div class='stat-number'>"
                + openTickets + "</div>");
        out.println("<div class='stat-description'>Awaiting action</div>");
        out.println("</div>");

        // In Progress
        out.println("<div class='stat-box'>");
        out.println("<div class='stat-top'>In Progress</div>");
        out.println("<div class='stat-number'>"
                + inProgressTickets + "</div>");
        out.println("<div class='stat-description'>Currently handled</div>");
        out.println("</div>");

        // Resolved
        out.println("<div class='stat-box'>");
        out.println("<div class='stat-top'>Resolved</div>");
        out.println("<div class='stat-number'>"
                + resolvedTickets + "</div>");
        out.println("<div class='stat-description'>Completed tickets</div>");
        out.println("</div>");

        // High Priority
        out.println("<div class='stat-box'>");
        out.println("<div class='stat-top'>High Priority</div>");
        out.println("<div class='stat-number'>"
                + highPriorityTickets + "</div>");
        out.println("<div class='stat-description'>Needs attention</div>");
        out.println("</div>");

        out.println("</div>");

        // -----------------------------------------
        // TICKET SECTION
        // -----------------------------------------

        out.println("<div class='ticket-section'>");

        out.println("<h3>Support Tickets</h3>");

        out.println("<p class='ticket-subtitle'>");
        out.println("Update ticket status and add resolution notes.");
        out.println("</p>");

        out.println("<div class='table-wrapper'>");

        out.println("<table>");

        out.println("<tr>");
        out.println("<th>ID</th>");
        out.println("<th>Student</th>");
        out.println("<th>Issue</th>");
        out.println("<th>Category</th>");
        out.println("<th>Department</th>");
        out.println("<th>Priority</th>");
        out.println("<th>Status</th>");
        out.println("<th>Resolution Notes</th>");
        out.println("<th>Action</th>");
        out.println("</tr>");

        // -----------------------------------------
        // Ticket Query
        // -----------------------------------------

        String sql =
                "SELECT t.ticket_id, u.name, t.subject, t.category, " +
                "t.department, t.priority, t.status, t.resolution_notes " +
                "FROM tickets t " +
                "JOIN users u ON t.user_id = u.user_id " +
                "WHERE t.department = ? " +
                "ORDER BY t.ticket_id DESC";

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1, staffDepartment);

            try (ResultSet rs = ps.executeQuery()) {

                boolean found = false;

                while (rs.next()) {

                    found = true;

                    int ticketId =
                            rs.getInt("ticket_id");

                    String student =
                            rs.getString("name");

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

                    String notes =
                            rs.getString("resolution_notes");

                    if (notes == null) {
                        notes = "";
                    }

                    String formId =
                            "ticketForm" + ticketId;

                    // Determine priority class
                    String priorityClass = "priority-low";

                    if ("High".equalsIgnoreCase(priority)) {

                        priorityClass = "priority-high";

                    } else if ("Medium".equalsIgnoreCase(priority)) {

                        priorityClass = "priority-medium";
                    }

                    out.println("<tr>");

                    out.println("<td class='ticket-id'>#"
                            + ticketId + "</td>");

                    out.println("<td>"
                            + student + "</td>");

                    out.println("<td><b>"
                            + subject + "</b></td>");

                    out.println("<td>"
                            + category + "</td>");

                    out.println("<td>"
                            + department + "</td>");

                    out.println("<td>");

                    out.println("<span class='priority "
                            + priorityClass + "'>"
                            + priority + "</span>");

                    out.println("</td>");

                    // Status
                    out.println("<td>");

                    out.println("<select class='status-select' "
                            + "name='status' form='"
                            + formId + "'>");

                    out.println("<option value='Open' "
                            + ("Open".equals(status)
                            ? "selected" : "")
                            + ">Open</option>");

                    out.println("<option value='In Progress' "
                            + ("In Progress".equals(status)
                            ? "selected" : "")
                            + ">In Progress</option>");

                    out.println("<option value='Resolved' "
                            + ("Resolved".equals(status)
                            ? "selected" : "")
                            + ">Resolved</option>");

                    out.println("</select>");

                    out.println("</td>");

                    // Resolution Notes
                    out.println("<td>");

                    out.println("<textarea "
                            + "name='resolutionNotes' "
                            + "form='" + formId + "' "
                            + "placeholder='Enter resolution notes'>");

                    out.println(notes);

                    out.println("</textarea>");

                    out.println("</td>");

                    // Action
                    out.println("<td>");

                    out.println("<form id='" + formId
                            + "' action='update-ticket' method='post'>");

                    out.println("<input type='hidden' "
                            + "name='ticketId' value='"
                            + ticketId + "'>");

                    out.println("<button class='update-btn' "
                            + "type='submit'>Update</button>");

                    out.println("</form>");

                    out.println("</td>");

                    out.println("</tr>");
                }

                if (!found) {

                    out.println("<tr>");

                    out.println("<td colspan='9' class='empty'>");

                    out.println("No tickets found for your department.");

                    out.println("</td>");

                    out.println("</tr>");
                }
            }

        } catch (Exception e) {

            out.println("<tr>");

            out.println("<td colspan='9' class='empty'>");

            out.println("Database Error: " + e.getMessage());

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