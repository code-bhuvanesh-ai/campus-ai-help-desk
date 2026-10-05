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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        String sql =
                "SELECT user_id, name, role, department " +
                "FROM users " +
                "WHERE email = ? AND password = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    int userId = rs.getInt("user_id");
                    String name = rs.getString("name");
                    String role = rs.getString("role");
                    String department = rs.getString("department");

                    // Create login session
                    HttpSession session = request.getSession();

                    session.setAttribute("userId", userId);
                    session.setAttribute("name", name);
                    session.setAttribute("role", role);
                    session.setAttribute("department", department);

                    // Redirect based on role
                    if ("student".equalsIgnoreCase(role)) {

                        response.sendRedirect("home.html");

                    } else if ("staff".equalsIgnoreCase(role)) {

                        response.sendRedirect("staff-dashboard");

                    } else {

                        response.setContentType("text/html;charset=UTF-8");

                        response.getWriter().println("""
                            <!DOCTYPE html>
                            <html lang="en">

                            <head>

                                <meta charset="UTF-8">

                                <meta name="viewport"
                                      content="width=device-width, initial-scale=1.0">

                                <title>Unknown Role - Campus AI Helpdesk</title>

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

                                    .error-card {
                                        width: 90%;
                                        max-width: 430px;

                                        background: white;

                                        padding: 40px;

                                        border-radius: 15px;

                                        text-align: center;

                                        box-shadow:
                                            0 5px 20px rgba(0, 0, 0, 0.08);
                                    }

                                    .error-icon {
                                        width: 65px;
                                        height: 65px;

                                        margin: 0 auto 20px;

                                        border-radius: 50%;

                                        background-color: #fef3c7;

                                        display: flex;
                                        align-items: center;
                                        justify-content: center;

                                        font-size: 30px;
                                    }

                                    h2 {
                                        margin: 0;

                                        color: #92400e;

                                        font-size: 27px;
                                    }

                                    p {
                                        color: #64748b;

                                        font-size: 14px;

                                        margin-top: 12px;

                                        line-height: 1.5;
                                    }

                                    .try-again {
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

                                    .try-again:hover {
                                        background-color: #172a3a;
                                    }

                                </style>

                            </head>

                            <body>

                                <div class="error-card">

                                    <div class="error-icon">
                                        ⚠️
                                    </div>

                                    <h2>
                                        Unknown User Role
                                    </h2>

                                    <p>
                                        Your account role is not recognized.
                                        Please contact the administrator.
                                    </p>

                                    <a href="login.html"
                                       class="try-again">
                                        Back to Login
                                    </a>

                                </div>

                            </body>

                            </html>
                            """);
                    }

                } else {

                    // Login failed
                    response.setContentType("text/html;charset=UTF-8");

                    response.getWriter().println("""
                        <!DOCTYPE html>
                        <html lang="en">

                        <head>

                            <meta charset="UTF-8">

                            <meta name="viewport"
                                  content="width=device-width, initial-scale=1.0">

                            <title>Login Failed - Campus AI Helpdesk</title>

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

                                .error-card {
                                    width: 90%;
                                    max-width: 430px;

                                    background: white;

                                    padding: 40px;

                                    border-radius: 15px;

                                    text-align: center;

                                    box-shadow:
                                        0 5px 20px rgba(0, 0, 0, 0.08);
                                }

                                .error-icon {
                                    width: 65px;
                                    height: 65px;

                                    margin: 0 auto 20px;

                                    border-radius: 50%;

                                    background-color: #fee2e2;

                                    display: flex;
                                    align-items: center;
                                    justify-content: center;

                                    font-size: 30px;
                                }

                                h2 {
                                    margin: 0;

                                    color: #b91c1c;

                                    font-size: 27px;
                                }

                                p {
                                    color: #64748b;

                                    font-size: 14px;

                                    margin-top: 12px;

                                    line-height: 1.5;
                                }

                                .try-again {
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

                                .try-again:hover {
                                    background-color: #172a3a;
                                }

                                .register {
                                    margin-top: 18px;

                                    font-size: 13px;

                                    color: #64748b;
                                }

                                .register a {
                                    color: #243b55;

                                    text-decoration: none;

                                    font-weight: bold;
                                }

                                .register a:hover {
                                    text-decoration: underline;
                                }

                            </style>

                        </head>

                        <body>

                            <div class="error-card">

                                <div class="error-icon">
                                    ⚠️
                                </div>

                                <h2>
                                    Login Failed
                                </h2>

                                <p>
                                    Invalid email or password.
                                    Please check your credentials
                                    and try again.
                                </p>

                                <a href="login.html"
                                   class="try-again">
                                    Try Again
                                </a>

                                <div class="register">

                                    Don't have a student account?

                                    <br>

                                    <a href="register.html">
                                        Create a Student Account
                                    </a>

                                </div>

                            </div>

                        </body>

                        </html>
                        """);
                }

            }

        } catch (Exception e) {

            e.printStackTrace();

            response.setContentType("text/html;charset=UTF-8");

            response.getWriter().println("""
                <!DOCTYPE html>
                <html lang="en">

                <head>

                    <meta charset="UTF-8">

                    <meta name="viewport"
                          content="width=device-width, initial-scale=1.0">

                    <title>Database Error - Campus AI Helpdesk</title>

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

                        .error-card {
                            width: 90%;
                            max-width: 430px;

                            background: white;

                            padding: 40px;

                            border-radius: 15px;

                            text-align: center;

                            box-shadow:
                                0 5px 20px rgba(0, 0, 0, 0.08);
                        }

                        .error-icon {
                            width: 65px;
                            height: 65px;

                            margin: 0 auto 20px;

                            border-radius: 50%;

                            background-color: #fee2e2;

                            display: flex;
                            align-items: center;
                            justify-content: center;

                            font-size: 30px;
                        }

                        h2 {
                            margin: 0;

                            color: #b91c1c;

                            font-size: 27px;
                        }

                        p {
                            color: #64748b;

                            font-size: 14px;

                            margin-top: 12px;

                            line-height: 1.5;
                        }

                        .try-again {
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

                        .try-again:hover {
                            background-color: #172a3a;
                        }

                    </style>

                </head>

                <body>

                    <div class="error-card">

                        <div class="error-icon">
                            ⚠️
                        </div>

                        <h2>
                            Database Error
                        </h2>

                        <p>
                            Something went wrong while connecting
                            to the database. Please try again.
                        </p>

                        <a href="login.html"
                           class="try-again">
                            Back to Login
                        </a>

                    </div>

                </body>

                </html>
                """);
        }
    }
}