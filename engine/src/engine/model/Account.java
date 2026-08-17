package engine.model;

import java.io.Serializable;

public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    private double balance;

    public Account() {
        this.balance = 0.0;
    }

    public void deposit(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot deposit a negative amount: " + amount);
        }
        balance += amount;
    }

    public void withdraw(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot withdraw a negative amount: " + amount);
        }
        balance -= amount;
    }

    public double getBalance() {
        return balance;
    }
}
