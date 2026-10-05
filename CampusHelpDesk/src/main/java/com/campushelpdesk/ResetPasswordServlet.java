package com.campushelpdesk;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html;charset=UTF-8");

        String userIdText = request.getParameter("userId");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Check required fields
        if (userIdText == null ||
            newPassword == null ||
            confirmPassword == null ||
            newPassword.trim().isEmpty() ||
            confirmPassword.trim().isEmpty()) {

            showMessage(
                response,
                "Invalid Request",
                "Please fill in all required fields.",
                "forgot-password.html",
                "Try Again"
            );

            return;
        }

        // Check whether passwords match
        if (!newPassword.equals(confirmPassword)) {

            showMessage(
                response,
                "Passwords Do Not Match",
                "The new password and confirmation password must be the same.",
                "javascript:history.back()",
                "Try Again"
            );

            return;
        }

        int userId;

        try {

            userId = Integer.parseInt(userIdText);

        } catch (NumberFormatException e) {

            showMessage(
                response,
                "Invalid Request",
                "Invalid user information.",
                "login.html",
                "Back to Login"
            );

            return;
        }

        // Update password
        String sql =
                "UPDATE users " +
                "SET password = ? " +
                "WHERE user_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setInt(2, userId);

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {

                showSuccess(response);

            } else {

                showMessage(
                    response,
                    "Reset Failed",
                    "We could not update your password. Please try again.",
                    "login.html",
                    "Back to Login"
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            showMessage(
                response,
                "Database Error",
                "Something went wrong while updating your password.",
                "login.html",
                "Back to Login"
            );
        }
    }


    // =========================
    // SUCCESS PAGE
    // =========================

    private void showSuccess(HttpServletResponse response)
            throws IOException {

        response.getWriter().println("""
            <!DOCTYPE html>
            <html lang="en">

            <head>

                <meta charset="UTF-8">

                <meta name="viewport"
                      content="width=device-width, initial-scale=1.0">

                <title>Password Reset Successful</title>

                <style>

                    * {
                        box-sizing: border-box;
                    }

                    body {
                        margin: 0;

                        font-family: Arial, sans-serif;

                        background-color: #f1f5f9;

                        min-height: 100vh;

                        display: flex;

                        justify-content: center;

                        align-items: center;
                    }

                    .card {

                        width: 90%;

                        max-width: 430px;

                        background: white;

                        padding: 40px;

                        border-radius: 15px;

                        text-align: center;

                        box-shadow:
                            0 5px 20px rgba(0, 0, 0, 0.08);
                    }

                    .icon {

                        width: 65px;

                        height: 65px;

                        margin: 0 auto 20px;

                        border-radius: 50%;

                        background-color: #dcfce7;

                        display: flex;

                        align-items: center;

                        justify-content: center;

                        font-size: 30px;
                    }

                    h2 {

                        margin: 0;

                        color: #15803d;

                        font-size: 27px;
                    }

                    p {

                        color: #64748b;

                        font-size: 14px;

                        line-height: 1.5;

                        margin-top: 12px;
                    }

                    .login-button {

                        display: inline-block;

                        margin-top: 20px;

                        padding: 13px 28px;

                        background-color: #243b55;

                        color: white;

                        text-decoration: none;

                        border-radius: 7px;

                        font-size: 15px;

                        font-weight: bold;
                    }

                    .login-button:hover {

                        background-color: #172a3a;
                    }

                </style>

            </head>

            <body>

                <div class="card">

                    <div class="icon">
                        ✓
                    </div>

                    <h2>
                        Password Reset Successful
                    </h2>

                    <p>
                        Your password has been updated successfully.
                    </p>

                    <p>
                        You can now login using your new password.
                    </p>

                    <a href="login.html"
                       class="login-button">
                        Go to Login
                    </a>

                </div>

            </body>

            </html>
            """);
    }


    // =========================
    // GENERAL MESSAGE PAGE
    // =========================

    private void showMessage(HttpServletResponse response,
                             String title,
                             String message,
                             String link,
                             String linkText)
            throws IOException {

        response.getWriter().println(
            "<!DOCTYPE html>" +
            "<html lang='en'>" +
            "<head>" +

            "<meta charset='UTF-8'>" +

            "<meta name='viewport' " +
            "content='width=device-width, initial-scale=1.0'>" +

            "<title>" + title + "</title>" +

            "<style>" +

            "* { box-sizing: border-box; }" +

            "body {" +
            "margin: 0;" +
            "font-family: Arial, sans-serif;" +
            "background-color: #f1f5f9;" +
            "min-height: 100vh;" +
            "display: flex;" +
            "justify-content: center;" +
            "align-items: center;" +
            "}" +

            ".card {" +
            "width: 90%;" +
            "max-width: 430px;" +
            "background: white;" +
            "padding: 40px;" +
            "border-radius: 15px;" +
            "text-align: center;" +
            "box-shadow: 0 5px 20px rgba(0,0,0,0.08);" +
            "}" +

            "h2 {" +
            "color: #b91c1c;" +
            "font-size: 25px;" +
            "}" +

            "p {" +
            "color: #64748b;" +
            "font-size: 14px;" +
            "line-height: 1.5;" +
            "}" +

            "a {" +
            "display: inline-block;" +
            "margin-top: 20px;" +
            "padding: 13px 28px;" +
            "background-color: #243b55;" +
            "color: white;" +
            "text-decoration: none;" +
            "border-radius: 7px;" +
            "font-weight: bold;" +
            "}" +

            "</style>" +

            "</head>" +

            "<body>" +

            "<div class='card'>" +

            "<h2>" + title + "</h2>" +

            "<p>" + message + "</p>" +

            "<a href='" + link + "'>" +
            linkText +
            "</a>" +

            "</div>" +

            "</body>" +

            "</html>"
        );
    }
}