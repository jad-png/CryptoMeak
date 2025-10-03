-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 1️⃣NUM types
DO $$ BEGIN
    CREATE TYPE currency_enum AS ENUM ('BITCOIN', 'ETHEREUM');
EXCEPTION WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE txpriority_enum AS ENUM ('ECONOMY', 'STANDARD', 'FAST');
EXCEPTION WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE txstatus_enum AS ENUM ('CONFIRMED', 'PENDING', 'REJECTED');
EXCEPTION WHEN duplicate_object THEN null;
END $$;

-- Wallet table
CREATE TABLE IF NOT EXISTS wallets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    address TEXT NOT NULL UNIQUE,
    balance DOUBLE PRECISION DEFAULT 0.0,
    owner_name TEXT,
    wt_name TEXT,
    currency currency_enum NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Transaction table
CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    src_address TEXT NOT NULL REFERENCES wallets(address) ON DELETE CASCADE,
    dest_address TEXT NOT NULL REFERENCES wallets(address) ON DELETE CASCADE,
    amount NUMERIC(20, 8) NOT NULL,
    fee NUMERIC(20, 8) NOT NULL,
    status txstatus_enum NOT NULL,
    priority txpriority_enum NOT NULL,
    currency currency_enum NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    confirmed_at TIMESTAMP
);

--  Indexes
CREATE INDEX IF NOT EXISTS idx_transactions_src ON transactions(src_address);
CREATE INDEX IF NOT EXISTS idx_transactions_dest ON transactions(dest_address);
CREATE INDEX IF NOT EXISTS idx_wallets_currency ON wallets(currency);
