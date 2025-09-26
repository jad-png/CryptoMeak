package repository;

import config.Database;
import repository.interfaces.IAuthRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class AuthRepositoryImpl implements IAuthRepository {
    public final Connection conn;

    public  AuthRepositoryImpl(Connection conn) throws SQLException {
        this.conn = Database.getInstance().getConn();
    }

    @Override
    public void saveWalletAuth(String wtAddress, String passwordHash) {
        String sql = "INSERT INTO wallet_auth (wallet_address, password_hash, created_at, last_login)" + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, wtAddress);
            stmt.setString(2, passwordHash);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving wallet authentication", e);
        }
    }

    @Override
    public Optional<String> getPasswordHash(String wtAddress) {
        String sql = "SELECT password_hash FROM wallet_auth WHERE wallet_address = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, wtAddress);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(rs.getString("password_hash"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting password hash", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean walletExists(String wtAddress) {
        String sql = "SELECT COUNT(*) FORM wallet_auth WHERE wallet_addres = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, wtAddress);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking wallet existence", e);
        }
        return false;
    }

    @Override
    public void updateLastLogin(String wtAddress) {
        String sql = "UPDATE wallet_auth SET last_login = ? WHERE wallet_address = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(2, wtAddress);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating last login", e);
        }
    }

    @Override
    public void updatePassword(String wtAddress, String newHashPassword) {
        String sql = "UPDATE wallet_auth SET password_hash = ? WHERE wallet_address = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newHashPassword);
            stmt.setString(2, wtAddress);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating password", e);
        }
    }

    @Override
    public boolean deleteWtAuth(String wtAddress) {
        String sql = "DELETE FROM wallet_auth WHERE wallet_address = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, wtAddress);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting wallet auth", e);
        }
    }
}
