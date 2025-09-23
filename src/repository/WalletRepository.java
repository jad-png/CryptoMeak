package repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import db.model.Wallet;

public class WalletRepository implements IWalletRepository {

	@Override
	public Wallet save(Wallet wallet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Wallet> findById(UUID id) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<Wallet> findByAddress(String address) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public List<Wallet> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(UUID id) {
		// TODO Auto-generated method stub
		
	}
	
}
