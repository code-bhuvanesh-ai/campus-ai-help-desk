package com.campushelpdesk;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html;charset=UTF-8");

        String email = request.getParameter("email");

        if (email == null || email.trim().isEmpty()) {

            showMessage(
                    response,
                    "Invalid Request",
                    "Please enter your registered email address.",
                    "forgot-password.html",
                    "Try Again"
            );

            return;
        }

        email = email.trim();

        String sql =
                "SELECT user_id, name FROM users WHERE email = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    int userId = rs.getInt("user_id");
                    String name = rs.getString("name");

                    showResetPasswordPage(
                            response,
                            name,
                            userId
                    );

                } else {

                    showMessage(
                            response,
                            "Email Not Found",
                            "We could not find an account registered with this email address.",
                            "forgot-password.html",
                            "Try Again"
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            showMessage(
                    response,
                    "Database Error",
                    "Unable to connect to the database or process your request.",
                    "forgot-password.html",
                    "Try Again"
            );
        }
    }

    private void showResetPasswordPage(
            HttpServletResponse response,
            String name,
            int userId) throws IOException {

        String safeName = escapeHtml(name);

        String html = """
            <!DOCTYPE html>
            <html lang="en">

            <head>

                <meta charset="UTF-8">

                <meta name="viewport"
                      content="width=device-width, initial-scale=1.0">

                <title>Reset Password - Campus AI Helpdesk</title>

                <style>

                    * {
                        box-sizing: border-box;
                    }

                    body {
                        margin: 0;
                        font-family: Arial, sans-serif;
                        background: #f1f5f9;
                        min-height: 100vh;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                    }

                    .card {
                        width: 90%;
                        max-width: 430px;
                        background: white;
                        padding: 38px;
                        border-radius: 15px;
                        box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
                    }

                    .icon {
                        width: 65px;
                        height: 65px;
                        margin: 0 auto 20px;
                        border-radius: 50%;
                        background: #e2e8f0;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        font-size: 30px;
                    }

                    h2 {
                        text-align: center;
                        margin: 0;
                        color: #243b55;
                        font-size: 27px;
                    }

                    .welcome {
                        text-align: center;
                        color: #64748b;
                        font-size: 14px;
                        line-height: 1.5;
                        margin: 12px 0 25px;
                    }

                    label {
                        display: block;
                        margin-bottom: 7px;
                        font-size: 14px;
                        font-weight: bold;
                        color: #334155;
                    }

                    input {
                        width: 100%;
                        padding: 13px;
                        margin-bottom: 18px;
                        border: 1px solid #cbd5e1;
                        border-radius: 7px;
                        font-size: 14px;
                    }

                    input:focus {
                        outline: none;
                        border-color: #243b55;
                    }

                    button {
                        width: 100%;
                        padding: 13px;
                        background: #243b55;
                        color: white;
                        border: none;
                        border-radius: 7px;
                        font-size: 15px;
                        font-weight: bold;
                        cursor: pointer;
                    }

                    button:hover {
                        background: #172a3a;
                    }

                    .back {
                        text-align: center;
                        margin-top: 20px;
                    }

                    .back a {
                        color: #243b55;
                        text-decoration: none;
                        font-size: 14px;
                        font-weight: bold;
                    }

                    .back a:hover {
                        text-decoration: underline;
                    }

                </style>

            </head>

            <body>

                <div class="card">

                    <div class="icon">
                        🔑
                    </div>

                    <h2>
                        Reset Password
                    </h2>

                    <p class="welcome">
                        Hello NAME_PLACEHOLDER! Create a new password
                        for your account.
                    </p>

                    <form action="reset-password" method="post">

                        <input type="hidden"
                               name="userId"
                               value="USERID_PLACEHOLDER">

                        <label>
                            New Password
                        </label>

                        <input
                            type="password"
                            name="newPassword"
                            placeholder="Enter new password"
                            required>

                        <label>
                            Confirm Password
                        </label>

                        <input
                            type="password"
                            name="confirmPassword"
                            placeholder="Confirm new password"
                            required>

                        <button type="submit">
                            Reset Password
                        </button>

                    </form>

                    <div class="back">

                        <a href="login.html">
                            ← Back to Login
                        </a>

                    </div>

                </div>

            </body>

            </html>
            """;

        html = html.replace("NAME_PLACEHOLDER", safeName);
        html = html.replace(
                "USERID_PLACEHOLDER",
                String.valueOf(userId)
        );

        response.getWriter().println(html);
    }

    private void showMessage(
            HttpServletResponse response,
            String title,
            String message,
            String link,
            String linkText) throws IOException {

        String html = """
            <!DOCTYPE html>
            <html lang="en">

            <head>

                <meta charset="UTF-8">

                <meta name="viewport"
                      content="width=device-width, initial-scale=1.0">

                <title>Campus AI Helpdesk</title>

                <style>

                    * {
                        box-sizing: border-box;
                    }

                    body {
                        margin: 0;
                        font-family: Arial, sans-serif;
                        background: #f1f5f9;
                        min-height: 100vh;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                    }

                    .card {
                        width: 90%;
                        max-width: 500px;
                        background: white;
                        padding: 40px;
                        border-radius: 15px;
                        text-align: center;
                        box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
                    }

                    .icon {
                        font-size: 45px;
                        margin-bottom: 15px;
                    }

                    h2 {
                        color: #b91c1c;
                        margin-bottom: 12px;
                    }

                    p {
                        color: #64748b;
                        font-size: 14px;
                        line-height: 1.5;
                    }

                    a {
                        display: inline-block;
                        margin-top: 20px;
                        padding: 13px 25px;
                        background: #243b55;
                        color: white;
                        text-decoration: none;
                        border-radius: 7px;
                        font-weight: bold;
                    }

                    a:hover {
                        background: #172a3a;
                    }

                </style>

            </head>

            <body>

                <div class="card">

                    <div class="icon">
                        ⚠️
                    </div>

                    <h2>
                        TITLE_PLACEHOLDER
                    </h2>

                    <p>
                        MESSAGE_PLACEHOLDER
                    </p>

                    <a href="LINK_PLACEHOLDER">
                        LINK_TEXT_PLACEHOLDER
                    </a>

                </div>

            </body>

            </html>
            """;

        html = html.replace(
                "TITLE_PLACEHOLDER",
                escapeHtml(title)
        );

        html = html.replace(
                "MESSAGE_PLACEHOLDER",
                escapeHtml(message)
        );

        html = html.replace(
                "LINK_PLACEHOLDER",
                link
        );

        html = html.replace(
                "LINK_TEXT_PLACEHOLDER",
                escapeHtml(linkText)
        );

        response.getWriter().println(html);
    }

    private String escapeHtml(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}