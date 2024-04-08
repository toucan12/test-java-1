package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class Pepper {

    // Command injection
    public void runtime_exec(String cmd) {
        Process proc = null;
        BufferedReader br = null;
        try {
            proc = Runtime.getRuntime().exec(cmd);
            br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            System.out.println(br.readLine());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Insecure hash algorithm
    public byte[] md5_digest(String str) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(str.getBytes());
        return md.digest();
    }

    // SQL injection
    public void getBooks(String bookname, String bookauthor, Boolean bookread) {
        Statement statement = null;
        Connection conn = null;

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:database.sqlite");
            statement = conn.createStatement();
            String query = null;

            if (bookname != null) {
                query = "SELECT * FROM Books WHERE name LIKE '%" + bookname + "%'";
            } else if (bookauthor != null) {
                query = "SELECT * FROM Books WHERE author LIKE '%" + bookauthor + "%'";
            } else if (bookread != null) {
                Integer read = bookread ? 1 : 0;
                query = "SELECT * FROM Books WHERE read = '" + read.toString() + "'";
            } else {
                query = "SELECT * FROM Books";
            }

            ResultSet results = statement.executeQuery(query);
            while (results.next()) {
                System.out.println(results.getString("name"));
                System.out.println(results.getString("author"));
                System.out.println(results.getInt("read") == 1);
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
