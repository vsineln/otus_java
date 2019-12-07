package ru.otus.homework;

import ru.otus.homework.impl.Nominal;

import java.util.List;
import java.util.Map;

public interface AtmInteraction {

    /**
     * Calculate common balance for all known ATM
     *
     * @return balance value
     */
    int getBalance();

    /**
     * Get state of all known ATM
     *
     * @return id of ATM with list of all its banknotes
     */
    Map<Integer, List<Nominal>> getState();

    /**
     * Update state of all known ATM
     *
     * @param stateToUpdate id of ATM and list of banknotes which should be hold in it
     */
    void updateState(Map<Integer, List<Nominal>> stateToUpdate);
}
