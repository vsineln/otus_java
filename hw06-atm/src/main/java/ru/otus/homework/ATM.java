package ru.otus.homework;

import ru.otus.homework.impl.Nominal;

import java.util.List;

public interface ATM extends Listener {

    /**
     * Adds banknotes in appropriate Cells in ATM
     *
     * @param banknotes to add
     */
    void addBanknotes(List<Nominal> banknotes);

    /**
     * Gets requested sum from ATM
     *
     * @param sum to get
     * @return List of banknotes which covers requested sum
     */
    List<Nominal> getBanknotes(int sum);

    /**
     * Gets all banknotes from ATM
     *
     * @return List of all banknotes from ATM
     */
    List<Nominal> getAllBanknote();

    /**
     * Gets the balance
     *
     * @return balance of the ATM
     */
    int getBalance();
}
