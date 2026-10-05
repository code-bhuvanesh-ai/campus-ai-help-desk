package com.campushelpdesk;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet("/create-ticket")
public class CreateTicketServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        // Check login session
        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("userId") == null) {

            response.sendRedirect("login.html");
            return;
        }

        // Get logged-in user's ID
        int userId =
                (Integer) session.getAttribute("userId");

        String studentName =
                request.getParameter("studentName");

        String subject =
                request.getParameter("subject");

        String description =
                request.getParameter("description");

        String module =
                request.getParameter("module");

        // -----------------------------------------
        // AI AUTO-TRIAGE
        // -----------------------------------------

        String text =
                (subject + " " + description).toLowerCase();

        String category = module;

        String department = module;

        String priority = "Low";

        int highestWeight = 0;

        try (Connection connection =
                     DBConnection.getConnection()) {

            // -----------------------------------------
            // Read Triage Keywords
            // -----------------------------------------

            String keywordSQL =
                    "SELECT keyword, category, priority_weight " +
                    "FROM triage_keywords";

            try (PreparedStatement ps =
                         connection.prepareStatement(keywordSQL);
                 ResultSet rs =
                         ps.executeQuery()) {

                while (rs.next()) {

                    String keyword =
                            rs.getString("keyword");

                    String keywordCategory =
                            rs.getString("category");

                    int weight =
                            rs.getInt("priority_weight");

                    if (text.contains(
                            keyword.toLowerCase())
                            && weight > highestWeight) {

                        highestWeight = weight;

                        category = keywordCategory;
                    }
                }
            }

            // -----------------------------------------
            // Determine Department
            // -----------------------------------------

            if (category.contains("IT")) {

                department = "IT Support";

            } else if (category.equals("Hostel")) {

                department = "Hostel";

            } else if (category.equals("Academics")) {

                department = "Academics";

            } else if (category.equals("Finance")) {

                department = "Finance";

            } else {

                department = module;
            }

            // -----------------------------------------
            // Determine Priority
            // -----------------------------------------

            if (highestWeight >= 3) {

                priority = "High";

            } else if (highestWeight >= 2) {

                priority = "Medium";

            } else {

                priority = "Low";
            }

            // -----------------------------------------
            // Insert Ticket
            // -----------------------------------------

            String insertSQL =
                    "INSERT INTO tickets " +
                    "(user_id, subject, description, category, " +
                    "department, priority, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, 'Open')";

            int ticketId = 0;

            try (PreparedStatement ps =
                         connection.prepareStatement(
                                 insertSQL,
                                 Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, userId);

                ps.setString(2, subject);

                ps.setString(3, description);

                ps.setString(4, category);

                ps.setString(5, department);

                ps.setString(6, priority);

                ps.executeUpdate();

                // Get newly generated Ticket ID
                try (ResultSet generatedKeys =
                             ps.getGeneratedKeys()) {

                    if (generatedKeys.next()) {

                        ticketId =
                                generatedKeys.getInt(1);
                    }
                }
            }

            // -----------------------------------------
            // SUCCESS PAGE
            // -----------------------------------------

            response.setContentType(
                    "text/html;charset=UTF-8");

            var out = response.getWriter();

            out.println("<!DOCTYPE html>");

            out.println("<html lang='en'>");

            out.println("<head>");

            out.println("<meta charset='UTF-8'>");

            out.println(
                    "<meta name='viewport' " +
                    "content='width=device-width, initial-scale=1.0'>"
            );

            out.println(
                    "<title>Ticket Submitted - Campus AI Helpdesk</title>"
            );

            // -----------------------------------------
            // CSS
            // -----------------------------------------

            out.println("<style>");

            out.println("* {");
            out.println("box-sizing: border-box;");
            out.println("}");

            out.println("body {");
            out.println("margin: 0;");
            out.println("font-family: Arial, sans-serif;");
            out.println("background-color: #f1f5f9;");
            out.println("color: #1e293b;");
            out.println("}");

            // Header
            out.println("header {");
            out.println(
                    "background: linear-gradient(135deg, #243b55, #141e30);"
            );
            out.println("color: white;");
            out.println("text-align: center;");
            out.println("padding: 30px 20px;");
            out.println("}");

            out.println("header h1 {");
            out.println("margin: 0;");
            out.println("font-size: 30px;");
            out.println("}");

            out.println("header p {");
            out.println("margin: 8px 0 0;");
            out.println("opacity: 0.85;");
            out.println("}");

            // Main
            out.println(".container {");
            out.println("width: 94%;");
            out.println("max-width: 700px;");
            out.println("margin: 50px auto;");
            out.println("}");

            // Success card
            out.println(".success-card {");
            out.println("background: white;");
            out.println("border-radius: 15px;");
            out.println("padding: 40px;");
            out.println("text-align: center;");
            out.println(
                    "box-shadow: 0 4px 15px rgba(0,0,0,0.08);"
            );
            out.println("}");

            // Check icon
            out.println(".success-icon {");
            out.println("width: 70px;");
            out.println("height: 70px;");
            out.println("margin: 0 auto 20px;");
            out.println("border-radius: 50%;");
            out.println("background-color: #dcfce7;");
            out.println("color: #15803d;");
            out.println("font-size: 38px;");
            out.println("display: flex;");
            out.println("align-items: center;");
            out.println("justify-content: center;");
            out.println("}");

            out.println(".success-card h2 {");
            out.println("margin: 0;");
            out.println("font-size: 27px;");
            out.println("color: #15803d;");
            out.println("}");

            out.println(".success-message {");
            out.println("color: #64748b;");
            out.println("margin: 12px 0 25px;");
            out.println("line-height: 1.5;");
            out.println("}");

            // Ticket ID
            out.println(".ticket-id {");
            out.println("background-color: #f8fafc;");
            out.println("border: 1px solid #e2e8f0;");
            out.println("border-radius: 10px;");
            out.println("padding: 18px;");
            out.println("margin-bottom: 25px;");
            out.println("}");

            out.println(".ticket-id-label {");
            out.println("font-size: 12px;");
            out.println("color: #64748b;");
            out.println("text-transform: uppercase;");
            out.println("font-weight: bold;");
            out.println("letter-spacing: 0.5px;");
            out.println("}");

            out.println(".ticket-id-number {");
            out.println("font-size: 30px;");
            out.println("font-weight: bold;");
            out.println("color: #243b55;");
            out.println("margin-top: 5px;");
            out.println("}");

            // Details
            out.println(".details {");
            out.println("text-align: left;");
            out.println("border-top: 1px solid #e2e8f0;");
            out.println("padding-top: 20px;");
            out.println("}");

            out.println(".detail-row {");
            out.println("display: flex;");
            out.println("justify-content: space-between;");
            out.println("gap: 20px;");
            out.println("padding: 11px 0;");
            out.println("border-bottom: 1px solid #f1f5f9;");
            out.println("}");

            out.println(".detail-label {");
            out.println("color: #64748b;");
            out.println("font-size: 14px;");
            out.println("}");

            out.println(".detail-value {");
            out.println("font-weight: bold;");
            out.println("font-size: 14px;");
            out.println("text-align: right;");
            out.println("}");

            // Badges
            out.println(".badge {");
            out.println("display: inline-block;");
            out.println("padding: 5px 10px;");
            out.println("border-radius: 20px;");
            out.println("font-size: 12px;");
            out.println("font-weight: bold;");
            out.println("}");

            out.println(".high {");
            out.println("background-color: #fee2e2;");
            out.println("color: #b91c1c;");
            out.println("}");

            out.println(".medium {");
            out.println("background-color: #fef3c7;");
            out.println("color: #b45309;");
            out.println("}");

            out.println(".low {");
            out.println("background-color: #dcfce7;");
            out.println("color: #15803d;");
            out.println("}");

            out.println(".open {");
            out.println("background-color: #fef3c7;");
            out.println("color: #b45309;");
            out.println("}");

            // Buttons
            out.println(".actions {");
            out.println("margin-top: 30px;");
            out.println("display: flex;");
            out.println("justify-content: center;");
            out.println("gap: 12px;");
            out.println("flex-wrap: wrap;");
            out.println("}");

            out.println(".button {");
            out.println("display: inline-block;");
            out.println("padding: 12px 20px;");
            out.println("border-radius: 7px;");
            out.println("text-decoration: none;");
            out.println("font-size: 14px;");
            out.println("font-weight: bold;");
            out.println("}");

            out.println(".primary {");
            out.println("background-color: #243b55;");
            out.println("color: white;");
            out.println("}");

            out.println(".secondary {");
            out.println("background-color: #e2e8f0;");
            out.println("color: #243b55;");
            out.println("}");

            out.println(".button:hover {");
            out.println("opacity: 0.9;");
            out.println("}");

            // Responsive
            out.println("@media (max-width: 600px) {");

            out.println(".success-card {");
            out.println("padding: 25px;");
            out.println("}");

            out.println(".detail-row {");
            out.println("flex-direction: column;");
            out.println("gap: 4px;");
            out.println("}");

            out.println(".detail-value {");
            out.println("text-align: left;");
            out.println("}");

            out.println("}");

            out.println("</style>");

            out.println("</head>");

            // -----------------------------------------
            // BODY
            // -----------------------------------------

            out.println("<body>");

            out.println("<header>");

            out.println("<h1>Campus AI Helpdesk</h1>");

            out.println(
                    "<p>Smart Campus Support System</p>"
            );

            out.println("</header>");

            out.println("<div class='container'>");

            out.println("<div class='success-card'>");

            // Success icon
            out.println(
                    "<div class='success-icon'>✓</div>"
            );

            out.println(
                    "<h2>Ticket Submitted Successfully!</h2>"
            );

            out.println(
                    "<p class='success-message'>"
                    + "Your support request has been submitted "
                    + "and automatically analyzed by our system."
                    + "</p>"
            );

            // Ticket ID
            out.println("<div class='ticket-id'>");

            out.println(
                    "<div class='ticket-id-label'>Your Ticket ID</div>"
            );

            out.println(
                    "<div class='ticket-id-number'>#"
                    + ticketId
                    + "</div>"
            );

            out.println("</div>");

            // Details
            out.println("<div class='details'>");

            // Student
            out.println("<div class='detail-row'>");

            out.println(
                    "<span class='detail-label'>Student</span>"
            );

            out.println(
                    "<span class='detail-value'>"
                    + studentName
                    + "</span>"
            );

            out.println("</div>");

            // Category
            out.println("<div class='detail-row'>");

            out.println(
                    "<span class='detail-label'>Category</span>"
            );

            out.println(
                    "<span class='detail-value'>"
                    + category
                    + "</span>"
            );

            out.println("</div>");

            // Priority
            String priorityClass = "low";

            if ("High".equalsIgnoreCase(priority)) {

                priorityClass = "high";

            } else if ("Medium".equalsIgnoreCase(priority)) {

                priorityClass = "medium";
            }

            out.println("<div class='detail-row'>");

            out.println(
                    "<span class='detail-label'>Priority</span>"
            );

            out.println(
                    "<span class='detail-value'>"
                    + "<span class='badge "
                    + priorityClass
                    + "'>"
                    + priority
                    + "</span>"
                    + "</span>"
            );

            out.println("</div>");

            // Department
            out.println("<div class='detail-row'>");

            out.println(
                    "<span class='detail-label'>Department</span>"
            );

            out.println(
                    "<span class='detail-value'>"
                    + department
                    + "</span>"
            );

            out.println("</div>");

            // Status
            out.println("<div class='detail-row'>");

            out.println(
                    "<span class='detail-label'>Status</span>"
            );

            out.println(
                    "<span class='detail-value'>"
                    + "<span class='badge open'>"
                    + "Open"
                    + "</span>"
                    + "</span>"
            );

            out.println("</div>");

            out.println("</div>");

            // Buttons
            out.println("<div class='actions'>");

            out.println(
                    "<a class='button primary' "
                    + "href='my-tickets'>"
                    + "View My Tickets →"
                    + "</a>"
            );

            out.println(
                    "<a class='button secondary' "
                    + "href='index.html'>"
                    + "Back to Home"
                    + "</a>"
            );

            out.println("</div>");

            out.println("</div>");

            out.println("</div>");

            out.println("</body>");

            out.println("</html>");

        } catch (Exception e) {

            e.printStackTrace();

            response.setContentType(
                    "text/html;charset=UTF-8");

            response.getWriter().println(
                    "<h2>Database Error</h2>"
            );

            response.getWriter().println(
                    "<p>" + e.getMessage() + "</p>"
            );
        }
    }
}