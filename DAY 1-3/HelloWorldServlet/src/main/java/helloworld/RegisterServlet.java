package helloworld;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String gender = request.getParameter("gender");
        String ageStr = request.getParameter("age");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        int age = Integer.parseInt(ageStr);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/login_app", // DB URL
                "root", // DB user
                "your_db_password" // DB password
            );

            String sql = "INSERT INTO users (name, gender, age, username, password, role) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, gender);
            pst.setInt(3, age);
            pst.setString(4, username);
            pst.setString(5, password);
            pst.setString(6, role);

            int rows = pst.executeUpdate();

            pst.close();
            con.close();

            if (rows > 0) {
                response.sendRedirect("Login.jsp");
            } else {
                response.sendRedirect("registerError.jsp");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("registerError.jsp");
        }
    }
}

