package repository;

import config.Database;
import jdk.internal.util.xml.impl.ReaderUTF8;
import model.Wallet;
import model.enums.Currency;
import repository.interfaces.IWalletRepository;

import javax.xml.transform.Result;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class WalletRepository implements IWalletRepository {
    private final Connection conn;

    public WalletRepository() throws SQLException {
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
                Wallet wallet = new Wallet(model.enums.Currency.valueOf(rs.getString("type")),
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
                    Wallet wallet = new Wallet(model.enums.Currency.valueOf(rs.getString("type")), rs.getString("address"));

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

    @Override
    public boolean existsByAddress(String address) {
        String sql = "SELECT COUNT(*) FROM wallets WHERE address = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, address);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while checking wallet existence", e);
        }
        return false;
    }

    @Override
    public Currency findCurrencyByAddress(String address) {
        String sql = "SELECT currency FROM wallets WHERE address = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, address);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return model.enums.Currency.valueOf(rs.getString("currency").toUpperCase());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while finding wallet currency", e);
        }
        throw new IllegalArgumentException("Wallet not found with address: " + address);
    }

    @Override
    public BigDecimal getBalance(String address) {
        String sql = "SELECT balance FROM wallets WHERE address = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, address);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal("balance");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while getting wallet balance", e);
        }
        throw new IllegalArgumentException("Wallet not found with address: " + address);
    }

    @Override
    public void subtractBalance(String address, BigDecimal amount) {
        String sql = "UPDATE wallets SET balance = balance - ? WHERE address = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, amount);
            stmt.setString(2, address);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new IllegalArgumentException("Wallet not found with address: " + address);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while subtracting balance", e);
        }
    }

    @Override
    public void addBalance(String address, BigDecimal amount) {
        String sql = "UPDATE wallets SET balance = balance + ? WHERE address = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, amount);
            stmt.setString(2, address);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new IllegalArgumentException("Wallet not found with address: " + address);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while subtracting balance", e);
        }
    }

    //TODO: util method to map results into wallet entity
    private Wallet mapResultSetToWallet(ResultSet rs) throws SQLException {
        return new Wallet(
                UUID.fromString(rs.getString("id")),
                rs.getString("address"),
                Currency.valueOf(rs.getString("currency")),
                rs.getString("owner_name"),
                rs.getBigDecimal("balance"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }

    public Optional<Wallet> findByAddress(String address) {
        String sql = "SELECT * FROM wallets WHERE address = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, address);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToWallet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while finding wallet by address", e);
        }

        return Optional.empty();
    }

    public List<Wallet> findByCurrency(Currency currency) {
        List<Wallet> wallets = new ArrayList<>();
        String sql = "SELECT * FROM wallets WHERE currency = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, currency.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                wallets.add(mapResultSetToWallet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while finding wallets by currency", e);
        }
        return wallets;
    }

    public boolean hasSufficientBalance(String address, BigDecimal requiredAmount) {
        BigDecimal balance = getBalance(address);
        return balance.compareTo(requiredAmount) >= 0;
    }
}
