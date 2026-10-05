package com.campushelpdesk;

public class CampusAIEngine {

    public static String getResponse(String message) {

        return getResponse(message, null);
    }


    public static String getResponse(
            String message,
            String lastTopic) {

        if (message == null || message.trim().isEmpty()) {

            return "Please type your question. I am here to help with the Campus Helpdesk.";
        }


        String text =
                message.toLowerCase().trim();


        // ============================================
        // CONVERSATION: YES
        // ============================================

        if (isYes(text)) {

            if ("IT".equals(lastTopic)) {

                return "Absolutely! 💻 "
                        + "You can create an IT Support ticket from "
                        + "the Create Ticket section on the Home page. "
                        + "Describe your Wi-Fi or network problem clearly "
                        + "and our system will automatically analyze and "
                        + "route the ticket to IT Support.";
            }


            if ("HOSTEL".equals(lastTopic)) {

                return "Sure! 🏠 "
                        + "You can create a Hostel-related ticket from "
                        + "the Create Ticket section. Include your hostel "
                        + "block, room details, and a clear description "
                        + "of the problem.";
            }


            if ("MAINTENANCE".equals(lastTopic)) {

                return "Absolutely! 🔧 "
                        + "You can create a Maintenance ticket from "
                        + "the Create Ticket section. Please mention the "
                        + "exact location and describe what needs to be "
                        + "repaired.";
            }


            if ("ACADEMICS".equals(lastTopic)) {

                return "Sure! 📚 "
                        + "You can create an Academics ticket from "
                        + "the Create Ticket section. Include the relevant "
                        + "course, subject, examination, or faculty details.";
            }


            return "Sure! 😊 Please use the Create Ticket section "
                    + "on the Home page and describe your problem clearly.";
        }


        // ============================================
        // CONVERSATION: NO
        // ============================================

        if (isNo(text)) {

            return "No problem! 😊 If you need help later, "
                    + "just ask me about the Campus Helpdesk.";
        }


        // ============================================
        // GREETING
        // ============================================

        if (containsAny(
                text,
                "hello",
                "hi",
                "hey",
                "good morning",
                "good afternoon",
                "good evening")) {

            return "Hello! 👋 I am your Campus AI Assistant. "
                    + "I can help you with tickets, IT Support, Hostel, "
                    + "Academics, Maintenance, account problems, "
                    + "ticket status, and other Campus Helpdesk questions.";
        }


        // ============================================
        // CREATE TICKET
        // ============================================

        if (containsAny(
                text,
                "create a ticket",
                "create ticket",
                "creating a ticket",
                "raise a ticket",
                "raise ticket",
                "raising a ticket",
                "submit a ticket",
                "submit ticket",
                "submitting a ticket",
                "new ticket",
                "open a ticket",
                "open ticket",
                "report a problem",
                "report an issue",
                "raise a complaint",
                "submit a complaint",
                "make a complaint")) {

            return "Sure! 🎫 You can create a new support ticket "
                    + "using the Create Ticket section on the Home page. "
                    + "Enter the issue title and describe your problem clearly. "
                    + "Our system will automatically analyze the issue and "
                    + "identify the category, priority, and responsible department.";
        }


        // ============================================
        // TICKET STATUS
        // ============================================

        if (containsAny(
                text,
                "ticket status",
                "status of my ticket",
                "check my ticket",
                "check ticket",
                "track my ticket",
                "track ticket",
                "tracking my ticket",
                "where is my ticket",
                "my ticket status",
                "ticket progress",
                "ticket history",
                "previous tickets",
                "submitted tickets",
                "my complaints",
                "check my complaint",
                "complaint status")) {

            return "You can check your submitted tickets from the "
                    + "My Tickets section. There you can view the ticket "
                    + "status, department, priority, resolution notes, "
                    + "and complete status history.";
        }


        // ============================================
        // IT SUPPORT
        // ============================================

        if (containsAny(
                text,
                "wifi",
                "wi-fi",
                "internet",
                "network",
                "campus network",
                "computer",
                "computer lab",
                "laptop",
                "desktop",
                "printer",
                "printing",
                "software",
                "system problem",
                "technical problem",
                "technical issue",
                "server",
                "email not working",
                "college email",
                "website not working",
                "portal not working")) {

            return "This appears to be an IT Support issue. 💻 "
                    + "Please check that your device is connected to the "
                    + "campus network and try reconnecting once. "
                    + "If the problem continues, you can create an IT Support "
                    + "ticket through the Campus Helpdesk. "
                    + "Would you like help creating a ticket?";
        }


        // ============================================
        // HOSTEL
        // ============================================

        if (containsAny(
                text,
                "hostel",
                "hostel room",
                "hostel block",
                "roommate",
                "warden",
                "hostel facility",
                "hostel facilities",
                "hostel complaint",
                "hostel problem",
                "hostel issue",
                "bed",
                "room allocation")) {

            return "This appears to be related to Hostel services. 🏠 "
                    + "You can raise a Hostel-related ticket through the "
                    + "Campus Helpdesk. Include your hostel block, room "
                    + "details, and a clear description of the problem. "
                    + "Would you like help creating a ticket?";
        }


        // ============================================
        // MAINTENANCE
        // ============================================

        if (containsAny(
                text,
                "fan",
                "ceiling fan",
                "light",
                "bulb",
                "electricity",
                "power",
                "switch",
                "socket",
                "water",
                "water leakage",
                "leakage",
                "plumbing",
                "pipe",
                "tap",
                "toilet",
                "bathroom",
                "repair",
                "broken",
                "maintenance",
                "ac not working",
                "air conditioner",
                "air conditioning",
                "furniture",
                "chair",
                "table")) {

            return "This looks like a Maintenance issue. 🔧 "
                    + "You can create a Maintenance ticket through the "
                    + "Campus Helpdesk. Please mention the exact location "
                    + "and describe what needs to be repaired. "
                    + "Would you like help creating a ticket?";
        }


        // ============================================
        // ACADEMICS
        // ============================================

        if (containsAny(
                text,
                "exam",
                "examination",
                "exam schedule",
                "exam timetable",
                "timetable",
                "class",
                "classes",
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
                "assignment",
                "internal marks",
                "semester",
                "academic issue")) {

            return "This appears to be an Academics-related query. 📚 "
                    + "You can raise an Academics ticket through the "
                    + "Campus Helpdesk and provide the relevant course, "
                    + "subject, examination, or faculty details. "
                    + "Would you like help creating a ticket?";
        }


        // ============================================
        // PASSWORD / ACCOUNT
        // ============================================

        if (containsAny(
                text,
                "forgot password",
                "forgot my password",
                "reset password",
                "change password",
                "password problem",
                "password issue",
                "account problem",
                "account issue",
                "cannot login",
                "can't login",
                "unable to login",
                "login problem",
                "login issue",
                "sign in problem",
                "sign in issue")) {

            return "If you are having an account or password problem 🔐, "
                    + "use the Forgot Password option on the login page. "
                    + "You can enter your registered email address and "
                    + "reset your password.";
        }


        // ============================================
        // PRIORITY
        // ============================================

        if (containsAny(
                text,
                "what is priority",
                "priority meaning",
                "priority",
                "high priority",
                "medium priority",
                "low priority")) {

            return "Ticket priority indicates how urgently an issue "
                    + "needs attention. 🔴 High priority issues are more "
                    + "urgent, while Medium and Low priority issues can "
                    + "generally be handled with less urgency.";
        }


        // ============================================
        // STATUS MEANINGS
        // ============================================

        if (containsAny(
                text,
                "what does open mean",
                "what is open status",
                "what does in progress mean",
                "what is in progress",
                "what does resolved mean",
                "what is resolved",
                "what does closed mean",
                "what is closed status")) {

            return "Ticket statuses describe the progress of your request. "
                    + "Open means the issue has been submitted. "
                    + "In Progress means staff are working on it. "
                    + "Resolved means a solution has been provided, "
                    + "and Closed means the ticket has been completed.";
        }


        // ============================================
        // DEPARTMENTS
        // ============================================

        if (containsAny(
                text,
                "which department",
                "what department",
                "responsible department",
                "who handles",
                "who will handle",
                "where should i report",
                "which team",
                "which support")) {

            return "The Campus Helpdesk routes issues to the appropriate "
                    + "department automatically. 💡 Common departments "
                    + "include IT Support, Hostel, Academics, and Maintenance.";
        }


        // ============================================
        // HOW SYSTEM WORKS
        // ============================================

        if (containsAny(
                text,
                "how does the helpdesk work",
                "how does this work",
                "how helpdesk works",
                "how does the system work",
                "what happens after creating a ticket",
                "what happens after submitting",
                "ticket process",
                "helpdesk process",
                "workflow")) {

            return "The process is simple: 🎫 First, create a support "
                    + "ticket and describe your problem. The system then "
                    + "analyzes the issue, identifies its category and "
                    + "priority, and routes it to the appropriate department. "
                    + "Staff can update the ticket, and you can track its "
                    + "status and resolution notes from My Tickets.";
        }


        // ============================================
        // WHAT CAN YOU DO?
        // ============================================

        if (containsAny(
                text,
                "what can you do",
                "what can i ask",
                "what do you do",
                "how can you help",
                "what help do you provide",
                "what services",
                "helpdesk services")) {

            return "I can help you with the Campus Helpdesk in several ways. 🤖 "
                    + "You can ask me about creating and tracking tickets, "
                    + "IT Support, Hostel problems, Academics, Maintenance, "
                    + "password and account issues, ticket priority, "
                    + "ticket status, and the helpdesk workflow.";
        }


        // ============================================
        // GENERAL HELP
        // ============================================

        if (containsAny(
                text,
                "help me",
                "i need help",
                "need help",
                "campus help",
                "helpdesk help")) {

            return "Of course! 😊 Please describe your campus problem "
                    + "and I will try to guide you. You can ask about "
                    + "IT Support, Hostel, Academics, Maintenance, "
                    + "tickets, passwords, or the Campus Helpdesk process.";
        }


        // ============================================
        // DEFAULT
        // ============================================

        return "I understand that you are asking about the Campus Helpdesk, "
                + "but I need a little more information to give you the right "
                + "answer. Please describe your problem in more detail. "
                + "For example, you can ask about creating a ticket, "
                + "checking ticket status, Wi-Fi, hostel problems, "
                + "maintenance, academics, or your account.";
    }


    // ============================================
    // YES
    // ============================================

    private static boolean isYes(String text) {

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


    // ============================================
    // NO
    // ============================================

    private static boolean isNo(String text) {

        return text.equals("no")
                || text.equals("nope")
                || text.equals("not now")
                || text.equals("no thanks")
                || text.equals("cancel");
    }


    // ============================================
    // KEYWORD CHECK
    // ============================================

    private static boolean containsAny(
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