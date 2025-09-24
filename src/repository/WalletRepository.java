package repository;

import config.Database;
import model.Wallet;
import repository.interfaces.IWalletRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class WalletRepository implements IWalletRepository {
    private final Connection conn;

    public WalletRepository(Connection conn) throws SQLException {
        try {
            this.conn = Database.getInstance().getConn();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Wallet wallet) {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO wallets (id, address, balance, type, created_at) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, wallet.getId());
            stmt.setString(2, wallet.getAddress());
            stmt.setDouble(3, wallet.getBalance());
            stmt.setString(4, wallet.getType().name());
            stmt.setTimestamp(5, Timestamp.valueOf(wallet.getCreatedAt()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Wallet> findById(String id) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM wallets WHERE id = ?")) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Wallet wallet = new Wallet(model.enums.WalletType.valueOf(rs.getString("type")),
                        rs.getString("address"));

                wallet.setBalance(rs.getDouble("balance"));
                return Optional.of(wallet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Wallet> findAll() {
            List<Wallet> wallets = new ArrayList<>();
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT * FROM wallets");
                while (rs.next()) {
                    Wallet wallet = new Wallet(model.enums.WalletType.valueOf(rs.getString("type")), rs.getString("address"));

                    wallet.setBalance(rs.getDouble("balance"));
                    wallets.add(wallet);
                }
        } catch (SQLException e) {
                e.printStackTrace();
            }
        return wallets;
    }

    @Override
    public void update(Wallet wallet) {
        try (PreparedStatement stmt = conn.prepareStatement("UPDATE wallets SET address = ?, balance = ?, type = ? WHERE id = ?")) {
            stmt.setString(1, wallet.getAddress());
            stmt.setDouble(2, wallet.getBalance());
            stmt.setString(3, wallet.getType().name());
            stmt.setString(4, wallet.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void delete(Wallet wallet) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM wallets WHERE id = ?")) {
            stmt.setString(1, wallet.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
