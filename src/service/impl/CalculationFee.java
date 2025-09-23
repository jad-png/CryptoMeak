package service.impl;

import db.model.FeePriority;
import db.model.Wallet;

public interface CalculationFee {
	public double calculateFee(double amount, FeePriority priority, Wallet wallet);
}
