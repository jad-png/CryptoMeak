package repository;

import config.Database;
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

    public void save(Wallet wallet) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO wallets (id, address, owner_name, wt_name, balance, currency, created_at, password) VALUES (?::uuid, ?, ?, ?, ?, ?::currency_enum, ?, ?)")) {
            stmt.setString(1, wallet.getId());
            stmt.setString(2, wallet.getAddress());
            stmt.setString(3, wallet.getOwnerName());
            stmt.setString(4, wallet.getWtName());
            stmt.setBigDecimal(5, wallet.getBalance());
            stmt.setString(6, wallet.getCurrency().name());
            stmt.setTimestamp(7, Timestamp.valueOf(wallet.getCreatedAt()));
            stmt.setString(8, wallet.getPasswordHash());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Wallet> findById(String id) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM wallets WHERE id = ?::uuid")) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Wallet wallet = new Wallet(model.enums.Currency.valueOf(rs.getString("type")),
                        rs.getString("address"));

                wallet.setBalance(rs.getBigDecimal("balance"));
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

                wallet.setBalance(rs.getBigDecimal("balance"));
                wallets.add(wallet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wallets;
    }

    @Override
    public void update(Wallet wallet) {
        try (PreparedStatement stmt = conn
                .prepareStatement(
                        "UPDATE wallets SET address = ?, balance = ?, currency = ?::currency_enu, WHERE id = ?::uuid")) {
            stmt.setString(1, wallet.getAddress());
            stmt.setBigDecimal(2, wallet.getBalance());
            stmt.setString(3, wallet.getCurrency().name());
            stmt.setString(4, wallet.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Wallet wallet) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM wallets WHERE id = ?::uuid")) {
            stmt.setString(1, wallet.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean existsByAddress(String address) {
        String sql = "SELECT * FROM wallets WHERE address = ?";

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

    // TODO: util method to map results into wallet entity
    private Wallet mapResultSetToWallet(ResultSet rs) throws SQLException {
        return new Wallet.Builder()
                .id(rs.getString("id"))
                .address(rs.getString("address"))
                .ownerName(rs.getString("owner_name"))
                .wtName(rs.getString("wt_name"))
                .balance(rs.getBigDecimal("balance"))
                .currency(Currency.valueOf(rs.getString("currency").toUpperCase()))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .passwordHash(rs.getString("password"))
                .build();
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
            throw new RuntimeException("Database error while finding wallet by address" + e.getMessage());
        }

        return Optional.empty();
    }

    public List<Wallet> findByCurrency(Currency currency) {
        List<Wallet> wallets = new ArrayList<>();
        String sql = "SELECT * FROM wallets WHERE currency = ?::currency_enum";

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
