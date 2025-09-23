package repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import db.model.Wallet;

public interface IWalletRepository {
	Wallet save(Wallet wallet);
    Optional<Wallet> findById(UUID id);
    Optional<Wallet> findByAddress(String address);
    List<Wallet> findAll();
    void delete(UUID id);
}