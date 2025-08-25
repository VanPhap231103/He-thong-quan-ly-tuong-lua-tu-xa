package control_ubuntu_firewall;

import java.sql.*;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/ubuntu_firewall";
    private static final String USER = "root";
    private static final String PASS = "";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
    
    public static boolean connectionExists(String ipAddress, String username) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT COUNT(*) FROM saved_connections WHERE ip_address = ? AND username = ?")) {
            
            stmt.setString(1, ipAddress);
            stmt.setString(2, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static void saveConnection(String ip, String username, String password, int port) {
        // Kiểm tra IP đã tồn tại chưa
        String checkSql = "SELECT COUNT(*) FROM saved_connections WHERE ip_address = ?";
        String insertSql = "INSERT INTO saved_connections (ip_address, username, password, port) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setString(1, ip);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                // IP đã tồn tại, cập nhật thông tin
                String updateSql = "UPDATE saved_connections SET username = ?, password = ?, port = ? WHERE ip_address = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, username);
                    updateStmt.setString(2, password);
                    updateStmt.setInt(3, port);
                    updateStmt.setString(4, ip);
                    updateStmt.executeUpdate();
                }
            } else {
                // IP chưa tồn tại, thêm mới
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, ip);
                    insertStmt.setString(2, username);
                    insertStmt.setString(3, password);
                    insertStmt.setInt(4, port);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}