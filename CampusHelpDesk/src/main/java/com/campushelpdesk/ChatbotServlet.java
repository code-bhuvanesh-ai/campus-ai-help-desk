package com.campushelpdesk;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/chatbot")
public class ChatbotServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String message = request.getParameter("message");

        if (message == null || message.trim().isEmpty()) {

            sendResponse(
                    response,
                    "Please type your question."
            );

            return;
        }

        String text = message.toLowerCase().trim();

        HttpSession session = request.getSession(true);


        // ==================================================
        // GET USER ID
        // ==================================================

        Integer userId = getUserId(session);


        // ==================================================
        // CHECK WHETHER WE ARE CURRENTLY CREATING A TICKET
        // ==================================================

        String ticketStep =
                (String) session.getAttribute("ticketStep");


        if (ticketStep != null) {

            handleTicketConversation(
                    response,
                    session,
                    userId,
                    message,
                    text,
                    ticketStep
            );

            return;
        }


        // ==================================================
        // START TICKET CREATION
        // ==================================================

        if (isCreateTicketRequest(text)) {

            if (userId == null) {

                sendResponse(
                        response,
                        "Please log in first before creating a ticket."
                );

                return;
            }


            startTicketCreation(
                    response,
                    session
            );

            return;
        }


        // ==================================================
        // GET PREVIOUS TOPIC
        // ==================================================

        String lastTopic =
                (String) session.getAttribute("lastTopic");


        // ==================================================
        // YES RESPONSE
        // ==================================================

        if (isYes(text)) {

            if ("IT".equals(lastTopic)
                    || "HOSTEL".equals(lastTopic)
                    || "MAINTENANCE".equals(lastTopic)
                    || "ACADEMICS".equals(lastTopic)) {

                if (userId == null) {

                    sendResponse(
                            response,
                            "Please log in first before creating a ticket."
                    );

                    return;
                }


                startTicketCreation(
                        response,
                        session
                );

                return;
            }


            String reply =
                    CampusAIEngine.getResponse(
                            message,
                            lastTopic
                    );


            sendResponse(
                    response,
                    reply
            );

            return;
        }


        // ==================================================
        // NO RESPONSE
        // ==================================================

        if (isNo(text)) {

            session.removeAttribute("lastTopic");

            sendResponse(
                    response,
                    "No problem! 😊 If you need help later, "
                    + "just ask me about the Campus Helpdesk."
            );

            return;
        }


        // ==================================================
        // DETECT TOPIC
        // ==================================================

        String currentTopic =
                detectTopic(text);


        if (currentTopic != null) {

            session.setAttribute(
                    "lastTopic",
                    currentTopic
            );
        }


        // ==================================================
        // REAL TICKET QUESTION
        // ==================================================

        if (isMyTicketQuestion(text)) {

            if (userId == null) {

                sendResponse(
                        response,
                        "Please log in first so I can check your tickets."
                );

                return;
            }


            String ticketReply =
                    getLatestTicket(userId);


            sendResponse(
                    response,
                    ticketReply
            );

            return;
        }


        // ==================================================
        // NORMAL AI RESPONSE
        // ==================================================

        String reply =
                CampusAIEngine.getResponse(
                        message,
                        currentTopic
                );


        sendResponse(
                response,
                reply
        );
    }


    // ==================================================
    // START TICKET CREATION
    // ==================================================

    private void startTicketCreation(
            HttpServletResponse response,
            HttpSession session)
            throws IOException {

        session.setAttribute(
                "ticketStep",
                "TITLE"
        );


        sendResponse(
                response,
                "Sure! 🎫 Let's create your support ticket.\n\n"
                + "What should be the title of your issue?"
        );
    }


    // ==================================================
    // HANDLE TICKET CONVERSATION
    // ==================================================

    private void handleTicketConversation(
            HttpServletResponse response,
            HttpSession session,
            Integer userId,
            String message,
            String text,
            String ticketStep)
            throws IOException {


        if (userId == null) {

            clearTicketSession(session);

            sendResponse(
                    response,
                    "Please log in again before creating a ticket."
            );

            return;
        }


        // ==================================================
        // STEP 1 — ISSUE TITLE
        // ==================================================

        if ("TITLE".equals(ticketStep)) {

            session.setAttribute(
                    "ticketSubject",
                    message.trim()
            );


            session.setAttribute(
                    "ticketStep",
                    "DESCRIPTION"
            );


            sendResponse(
                    response,
                    "Got it! 👍\n\n"
                    + "Now please describe the problem in detail.\n\n"
                    + "For example: "
                    + "\"The WiFi is completely unavailable "
                    + "in my hostel room.\""
            );

            return;
        }


        // ==================================================
        // STEP 2 — DESCRIPTION
        // ==================================================

        if ("DESCRIPTION".equals(ticketStep)) {

            session.setAttribute(
                    "ticketDescription",
                    message.trim()
            );


            String subject =
                    (String) session.getAttribute(
                            "ticketSubject"
                    );


            String topic =
                    (String) session.getAttribute(
                            "lastTopic"
                    );


            TriageResult result =
                    analyzeTicket(
                            subject + " " + message,
                            topic
                    );


            session.setAttribute(
                    "ticketCategory",
                    result.category
            );


            session.setAttribute(
                    "ticketDepartment",
                    result.department
            );


            session.setAttribute(
                    "ticketPriority",
                    result.priority
            );


            session.setAttribute(
                    "ticketStep",
                    "CONFIRM"
            );


            sendResponse(
                    response,

                    "Perfect! 👍 I analyzed your issue.\n\n"

                    + "Issue: "
                    + subject
                    + "\n"

                    + "Category: "
                    + result.category
                    + "\n"

                    + "Department: "
                    + result.department
                    + "\n"

                    + "Priority: "
                    + result.priority
                    + "\n\n"

                    + "Would you like me to create this ticket?"
                    + "\n\n"
                    + "Reply with YES or NO."
            );

            return;
        }


        // ==================================================
        // STEP 3 — CONFIRMATION
        // ==================================================

        if ("CONFIRM".equals(ticketStep)) {


            if (isYes(text)) {

                String result =
                        createTicket(
                                session,
                                userId
                        );


                clearTicketSession(session);


                sendResponse(
                        response,
                        result
                );

                return;
            }


            if (isNo(text)) {

                clearTicketSession(session);


                sendResponse(
                        response,
                        "No problem! 👍 The ticket was not created. "
                        + "You can start again anytime by saying "
                        + "\"create a ticket\"."
                );

                return;
            }


            sendResponse(
                    response,
                    "Please reply with YES to create the ticket "
                    + "or NO to cancel it."
            );
        }
    }


    // ==================================================
    // CREATE REAL DATABASE TICKET
    // ==================================================

    private String createTicket(
            HttpSession session,
            int userId) {


        String subject =
                (String) session.getAttribute(
                        "ticketSubject"
                );


        String description =
                (String) session.getAttribute(
                        "ticketDescription"
                );


        String category =
                (String) session.getAttribute(
                        "ticketCategory"
                );


        String department =
                (String) session.getAttribute(
                        "ticketDepartment"
                );


        String priority =
                (String) session.getAttribute(
                        "ticketPriority"
                );


        String sql =
                "INSERT INTO tickets "
                + "(user_id, subject, description, category, "
                + "department, priority, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, 'Open')";


        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        connection.prepareStatement(
                                sql,
                                java.sql.Statement.RETURN_GENERATED_KEYS
                        )
        ) {


            ps.setInt(
                    1,
                    userId
            );


            ps.setString(
                    2,
                    subject
            );


            ps.setString(
                    3,
                    description
            );


            ps.setString(
                    4,
                    category
            );


            ps.setString(
                    5,
                    department
            );


            ps.setString(
                    6,
                    priority
            );


            int rows =
                    ps.executeUpdate();


            if (rows == 0) {

                return "Sorry, I could not create the ticket. "
                        + "Please try again.";
            }


            try (
                    ResultSet keys =
                            ps.getGeneratedKeys()
            ) {


                if (keys.next()) {

                    int ticketId =
                            keys.getInt(1);


                    return "✅ Ticket created successfully!\n\n"

                            + "Ticket ID: #"
                            + ticketId
                            + "\n"

                            + "Issue: "
                            + subject
                            + "\n"

                            + "Category: "
                            + category
                            + "\n"

                            + "Department: "
                            + department
                            + "\n"

                            + "Priority: "
                            + priority
                            + "\n"

                            + "Status: Open\n\n"

                            + "You can view this ticket anytime "
                            + "from the My Tickets section.";
                }
            }


            return "✅ Ticket created successfully. "
                    + "You can check it from My Tickets.";


        } catch (Exception e) {

            e.printStackTrace();


            return "Sorry, I couldn't create the ticket "
                    + "because of a database error. "
                    + "Please try again.";
        }
    }


    // ==================================================
    // AI TRIAGE
    // ==================================================

    private TriageResult analyzeTicket(
            String text,
            String topic) {


        String lower =
                text.toLowerCase();


        String category =
                "General";


        String department =
                "IT Support";


        String priority =
                "Low";


        int highestWeight =
                0;


        String sql =
                "SELECT keyword, category, priority_weight "
                + "FROM triage_keywords";


        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        connection.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {


            while (rs.next()) {

                String keyword =
                        rs.getString("keyword")
                                .toLowerCase();


                String foundCategory =
                        rs.getString("category");


                int weight =
                        rs.getInt(
                                "priority_weight"
                        );


                if (lower.contains(keyword)
                        && weight > highestWeight) {

                    highestWeight =
                            weight;

                    category =
                            foundCategory;
                }
            }


        } catch (Exception e) {

            e.printStackTrace();
        }


        // ==================================================
        // DEPARTMENT
        // ==================================================

        if (category.toLowerCase().contains("it")) {

            department =
                    "IT Support";

        } else if (
                category.toLowerCase().contains("hostel")) {

            department =
                    "Hostel";

        } else if (
                category.toLowerCase().contains("academic")) {

            department =
                    "Academics";

        } else if (
                category.toLowerCase().contains("maintenance")) {

            department =
                    "Maintenance";

        } else if (topic != null) {

            if (topic.equals("IT")) {

                department =
                        "IT Support";

            } else if (topic.equals("HOSTEL")) {

                department =
                        "Hostel";

            } else if (topic.equals("ACADEMICS")) {

                department =
                        "Academics";

            } else if (topic.equals("MAINTENANCE")) {

                department =
                        "Maintenance";
            }
        }


        // ==================================================
        // PRIORITY
        // ==================================================

        if (highestWeight >= 3) {

            priority =
                    "High";

        } else if (highestWeight >= 2) {

            priority =
                    "Medium";

        } else {

            priority =
                    "Low";
        }


        return new TriageResult(
                category,
                department,
                priority
        );
    }


    // ==================================================
    // TICKET RESULT CLASS
    // ==================================================

    private static class TriageResult {

        String category;

        String department;

        String priority;


        TriageResult(
                String category,
                String department,
                String priority) {

            this.category =
                    category;

            this.department =
                    department;

            this.priority =
                    priority;
        }
    }


    // ==================================================
    // DETECT TOPIC
    // ==================================================

    private String detectTopic(
            String text) {


        if (containsAny(
                text,
                "wifi",
                "wi-fi",
                "internet",
                "network",
                "computer",
                "laptop",
                "desktop",
                "printer",
                "printing",
                "software",
                "technical",
                "server")) {

            return "IT";
        }


        if (containsAny(
                text,
                "hostel",
                "roommate",
                "warden",
                "bed",
                "room allocation")) {

            return "HOSTEL";
        }


        if (containsAny(
                text,
                "fan",
                "light",
                "bulb",
                "electricity",
                "power",
                "switch",
                "socket",
                "water",
                "plumbing",
                "leakage",
                "repair",
                "broken",
                "maintenance",
                "bathroom",
                "toilet",
                "ac not working")) {

            return "MAINTENANCE";
        }


        if (containsAny(
                text,
                "exam",
                "examination",
                "timetable",
                "class",
                "course",
                "subject",
                "faculty",
                "professor",
                "teacher",
                "attendance",
                "marks",
                "grade",
                "result",
                "academic",
                "academics",
                "assignment")) {

            return "ACADEMICS";
        }


        return null;
    }


    // ==================================================
    // CREATE TICKET REQUEST
    // ==================================================

    private boolean isCreateTicketRequest(
            String text) {

        return containsAny(
                text,

                "create a ticket",
                "create ticket",
                "creating a ticket",
                "raise a ticket",
                "raise ticket",
                "submit a ticket",
                "submit ticket",
                "new ticket",
                "open a ticket",
                "report a problem",
                "report an issue",
                "raise a complaint",
                "submit a complaint"
        );
    }


    // ==================================================
    // YES
    // ==================================================

    private boolean isYes(
            String text) {

        return text.equals("yes")
                || text.equals("yeah")
                || text.equals("yep")
                || text.equals("yup")
                || text.equals("sure")
                || text.equals("okay")
                || text.equals("ok")
                || text.equals("yes please")
                || text.equals("sure please")
                || text.equals("please do");
    }


    // ==================================================
    // NO
    // ==================================================

    private boolean isNo(
            String text) {

        return text.equals("no")
                || text.equals("nope")
                || text.equals("not now")
                || text.equals("no thanks")
                || text.equals("cancel");
    }


    // ==================================================
    // GET USER ID
    // ==================================================

    private Integer getUserId(
            HttpSession session) {


        if (session == null) {

            return null;
        }


        Object value =
                session.getAttribute(
                        "userId"
                );


        if (value == null) {

            return null;
        }


        if (value instanceof Integer) {

            return (Integer) value;
        }


        try {

            return Integer.parseInt(
                    value.toString()
            );

        } catch (Exception e) {

            return null;
        }
    }


    // ==================================================
    // MY TICKET QUESTION
    // ==================================================

    private boolean isMyTicketQuestion(
            String text) {


        boolean hasTicketWord =
                text.contains("ticket")
                || text.contains("complaint")
                || text.contains("request");


        boolean hasPersonalWord =
                text.contains("my")
                || text.contains("mine")
                || text.contains("latest")
                || text.contains("recent")
                || text.contains("submitted");


        boolean asksStatus =
                text.contains("status")
                || text.contains("progress")
                || text.contains("update")
                || text.contains("resolved")
                || text.contains("closed")
                || text.contains("open");


        boolean asksDetails =
                text.contains("details")
                || text.contains("information")
                || text.contains("show")
                || text.contains("what");


        return hasTicketWord
                && hasPersonalWord
                && (asksStatus || asksDetails);
    }


    // ==================================================
    // GET LATEST TICKET
    // ==================================================

    private String getLatestTicket(
            int userId) {


        String sql =
                "SELECT ticket_id, subject, category, "
                + "department, priority, status, "
                + "resolution_notes "
                + "FROM tickets "
                + "WHERE user_id = ? "
                + "ORDER BY ticket_id DESC "
                + "LIMIT 1";


        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        connection.prepareStatement(sql)
        ) {


            ps.setInt(
                    1,
                    userId
            );


            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {


                if (rs.next()) {


                    int ticketId =
                            rs.getInt(
                                    "ticket_id"
                            );


                    String subject =
                            rs.getString(
                                    "subject"
                            );


                    String category =
                            rs.getString(
                                    "category"
                            );


                    String department =
                            rs.getString(
                                    "department"
                            );


                    String priority =
                            rs.getString(
                                    "priority"
                            );


                    String status =
                            rs.getString(
                                    "status"
                            );


                    String resolution =
                            rs.getString(
                                    "resolution_notes"
                            );


                    if (resolution == null
                            || resolution.trim().isEmpty()) {

                        resolution =
                                "No resolution notes have been added yet.";
                    }


                    return "Here is your latest ticket: 🎫\n\n"

                            + "Ticket ID: #"
                            + ticketId
                            + "\n"

                            + "Issue: "
                            + subject
                            + "\n"

                            + "Category: "
                            + category
                            + "\n"

                            + "Department: "
                            + department
                            + "\n"

                            + "Priority: "
                            + priority
                            + "\n"

                            + "Status: "
                            + status
                            + "\n"

                            + "Resolution: "
                            + resolution;
                }


                return "You don't have any submitted tickets yet.";
            }


        } catch (Exception e) {

            e.printStackTrace();


            return "I'm unable to access your ticket information "
                    + "right now. Please try again.";
        }
    }


    // ==================================================
    // SEND JSON
    // ==================================================

    private void sendResponse(
            HttpServletResponse response,
            String reply)
            throws IOException {


        String json =
                "{\"reply\":\""
                + escapeJson(reply)
                + "\"}";


        response.getWriter().write(
                json
        );
    }


    // ==================================================
    // JSON ESCAPE
    // ==================================================

    private String escapeJson(
            String text) {


        if (text == null) {

            return "";
        }


        return text
                .replace(
                        "\\",
                        "\\\\"
                )
                .replace(
                        "\"",
                        "\\\""
                )
                .replace(
                        "\n",
                        "\\n"
                )
                .replace(
                        "\r",
                        "\\r"
                );
    }


    // ==================================================
    // CLEAR TICKET SESSION
    // ==================================================

    private void clearTicketSession(
            HttpSession session) {

        session.removeAttribute(
                "ticketStep"
        );

        session.removeAttribute(
                "ticketSubject"
        );

        session.removeAttribute(
                "ticketDescription"
        );

        session.removeAttribute(
                "ticketCategory"
        );

        session.removeAttribute(
                "ticketDepartment"
        );

        session.removeAttribute(
                "ticketPriority"
        );
    }


    // ==================================================
    // KEYWORD CHECK
    // ==================================================

    private boolean containsAny(
            String text,
            String... keywords) {


        for (String keyword : keywords) {

            if (text.contains(keyword)) {

                return true;
            }
        }


        return false;
    }
}