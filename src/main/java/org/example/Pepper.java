package org.example;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class Pepper extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
        String result = "";

        String cmd = req.getParameter("cmd");
        result = commandInjection(cmd);

        String id = req.getParameter("id");
        result = sqlInjection(id);

        byte[] md5 = md5Digest("hello");

        PrintWriter out = resp.getWriter();
        out.println(result);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    // Command injection
    public static String commandInjection(String cmd) {
        Process proc = null;
        BufferedReader br = null;
        String result = "";
        try {
            proc = Runtime.getRuntime().exec(cmd);
            br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            result = br.readLine();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    // Insecure hash algorithm
    public byte[] md5Digest(String str) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(str.getBytes());
        return md.digest();
    }

    // SQL injection
    public static String sqlInjection(String id) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String result = "";
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:database.sqlite");
            stmt = conn.createStatement();
            String query = null;
            if (id != null) {
                query = "SELECT * FROM Member WHERE id LIKE '%" + id + "%'";
            }
            else {
                query = "SELECT * FROM Member";
            }
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                result = rs.getString("id");
            }
        }
        catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
