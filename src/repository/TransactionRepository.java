package repository;

import config.Database;
import model.Transaction;
import model.enums.Currency;
import model.enums.TxPriority;
import model.enums.TxStatus;
import repository.interfaces.ITransactionRepository;
import repository.interfaces.Repository;

import java.sql.*;
import java.util.*;

public class TransactionRepository implements ITransactionRepository {
    private final Connection conn;

    public TransactionRepository() throws SQLException {
        this.conn = Database.getInstance().getConn();
    }

    @Override
    public void save(Transaction tx) {
        String sql = "INSERT INTO transactions (id, source_address, destination_address, amount, fee, " +
                "status, priority, wallet_type, created_at, confirmed_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tx.getId().toString());
            stmt.setString(2, tx.getSourceAddress());
            stmt.setString(3, tx.getDestinationAddress());
            stmt.setBigDecimal(4, tx.getAmount());
            stmt.setBigDecimal(5, tx.getFee());
            stmt.setString(6, tx.getStatus().name());
            stmt.setString(7, tx.getPriority().name());
            stmt.setString(8, tx.getCurrency().name());
            stmt.setTimestamp(9, Timestamp.valueOf(tx.getCreatedAt()));
            stmt.setTimestamp(10, tx.getConfirmedAt() != null ? Timestamp.valueOf(tx.getConfirmedAt()) : null);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Database error while adding transaction", e);
        }
    }

    @Override
    public Optional<Transaction> findById(String id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToTransaction(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error while loading your transaction", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> txs = new ArrayList<>();
        String sql = "SELECT * FROM transactions";

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                txs.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while loading transactions", e);
        }
        return txs;
    }

    @Override
    public List<Transaction> findBySrcAddress(String srcAddress) {
        return findByAddressField("source_address", srcAddress);
    }

    @Override
    public List<Transaction> findByDestAddress(String destAddress) {
        return findByAddressField("destination_address", destAddress);
    }

    @Override
    public List<Transaction> findByStatus(TxStatus status) {
        List<Transaction> txs = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE status = ? ORDER BY created_at DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                txs.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while loading transactions", e);
        }
        return txs;
    }

    @Override
    public List<Transaction> findByCurrency(Currency currency) {
        List<Transaction> txs = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE currency = ? ORDER BY created_at DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, currency.name());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                txs.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while loading transactions", e);
        }

        return txs;
    }

    @Override
    public void update(Transaction tx) {
        String sql = "UPDATE transactions SET source_address = ?, destination_address = ?, \" +\n" +
                "                    \"amount = ?, fee = ?, status = ?, priority = ?, wallet_type = ?, \" +\n" +
                "                    \"created_at = ?, confirmed_at = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tx.getSourceAddress());
            stmt.setString(2, tx.getDestinationAddress());
            stmt.setBigDecimal(3, tx.getAmount());
            stmt.setBigDecimal(4, tx.getFee());
            stmt.setString(5, tx.getStatus().name());
            stmt.setString(6, tx.getPriority().name());
            stmt.setString(7, tx.getCurrency().name());
            stmt.setTimestamp(8, Timestamp.valueOf(tx.getCreatedAt()));
            stmt.setTimestamp(9, tx.getConfirmedAt() != null ? Timestamp.valueOf(tx.getConfirmedAt()) : null);
            stmt.setString(10, tx.getId().toString());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database error while updating transaction", e);
        }
    }

    @Override
    public void delete(Transaction tx) {
        String sql = "DELETE FROM transactions WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tx.getId().toString());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Database error while deleting transaction", e);
        }
    }

    @Override
    public boolean existingPendingTx(String srcAddress) {
        // TODO: add foreign key of wallet in the transactions table for ez extracting
        // exisiting pending txs
        String sql = "SELECT * FROM transactions WHERE status = ? ORDER BY created_at DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, srcAddress);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error while loading transactions", e);
        }
        return false;
    }

    public List<Transaction> findByAddressField(String fieldName, String address) {
        List<Transaction> txs = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE %s = ? ORDER BY created_at DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, address);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                txs.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while loading transactions", e);
        }
        return txs;
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
                UUID.fromString(rs.getString("id")),
                rs.getString("source_address"),
                rs.getString("destination_address"),
                rs.getBigDecimal("amount"),
                rs.getBigDecimal("fee"),
                TxStatus.valueOf(rs.getString("status")),
                TxPriority.valueOf(rs.getString("priority")),
                Currency.valueOf(rs.getString("wallet_type")),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("confirmed_at") != null ? rs.getTimestamp("confirmed_at").toLocalDateTime() : null);
    }

    // mempool specific methods
    public List<Transaction> findPendingTransactionsSortedByFees() {
        // find pen txs and sorted based on fees
        List<Transaction> txs = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE status = 'PENDING' ORDER BY fee DESC, created_at ASC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                txs.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while loading pending transactions", e);
        }

        return txs;
    }

    public List<Transaction> findPendingTransactionsByAddress(String address) {
        // finding pending txs based on wallet address
        List<Transaction> txs = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE status = 'PENDING' AND address = ? ORDER BY fee DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, address);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                txs.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("", e);
        }

        return txs;
    }

    public int getTransactionPosition(String txId) {
        String sql = "SELECT position FROM (" +
                "SELECT id, ROW_NUMBER() OVER (ORDER BY fee DESC, created_at ASC) as position " +
                "FROM transactions WHERE status = 'PENDING'" +
                ") AS ranked WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, txId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("position");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while getting transaction position", e);
        }
        return -1; // not found
    }

    public int countPendingTransactions() {
        // count the total of pending txs
        String sql = "SELECT COUNT (*) as count FROM transactions WHERE status = 'PENDING'";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while counting pending transactions", e);
        }
        return 0;
    }
}
