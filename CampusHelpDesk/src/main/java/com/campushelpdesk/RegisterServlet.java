package com.campushelpdesk;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        String checkSQL =
                "SELECT user_id FROM users WHERE email = ?";

        String insertSQL =
                "INSERT INTO users " +
                "(name, email, password, role, department) " +
                "VALUES (?, ?, ?, 'student', 'IT Support')";

        try (Connection connection = DBConnection.getConnection()) {

            // Check whether email already exists
            try (PreparedStatement check =
                         connection.prepareStatement(checkSQL)) {

                check.setString(1, email);

                try (ResultSet rs = check.executeQuery()) {

                    if (rs.next()) {

                        response.getWriter().println("""
                            <!DOCTYPE html>
                            <html>
                            <head>
                                <title>Registration Failed</title>
                            </head>
                            <body>
                                <h2>Registration Failed</h2>
                                <p>This email is already registered.</p>
                                <a href="register.html">Try Again</a>
                            </body>
                            </html>
                            """);

                        return;
                    }
                }
            }


            // Create student account
            try (PreparedStatement insert =
                         connection.prepareStatement(insertSQL)) {

                insert.setString(1, name);
                insert.setString(2, email);
                insert.setString(3, password);

                insert.executeUpdate();
            }


            // Registration successful
            response.getWriter().println("""
                <!DOCTYPE html>
                <html>

                <head>

                    <title>Registration Successful</title>

                    <style>

                        body {
                            font-family: Arial, sans-serif;
                            background: #f1f5f9;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            min-height: 100vh;
                        }

                        .card {
                            background: white;
                            padding: 40px;
                            border-radius: 18px;
                            text-align: center;
                            box-shadow: 0 10px 30px rgba(0,0,0,0.10);
                            max-width: 450px;
                        }

                        h2 {
                            color: #16a34a;
                        }

                        p {
                            color: #475569;
                            margin: 10px 0;
                        }

                        a {
                            display: inline-block;
                            margin-top: 20px;
                            padding: 12px 24px;
                            background: #2563eb;
                            color: white;
                            text-decoration: none;
                            border-radius: 8px;
                        }

                    </style>

                </head>

                <body>

                    <div class="card">

                        <h2>
                            ✓ Registration Successful!
                        </h2>

                        <p>
                            Your student account has been created.
                        </p>

                        <p>
                            You can now login using your email and password.
                        </p>

                        <a href="login.html">
                            Go to Login
                        </a>

                    </div>

                </body>

                </html>
                """);

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().println(
                "<h2>Database Error</h2>"
            );

            response.getWriter().println(
                "<p>" + e.getMessage() + "</p>"
            );
        }
    }
}