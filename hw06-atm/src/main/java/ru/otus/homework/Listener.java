package ru.otus.homework;

import ru.otus.homework.impl.Nominal;

import java.util.List;

/**
 * Interface for notifying ATM about department's request
 */
public interface Listener {

    /**
     * Request list of available banknotes from ATM
     *
     * @return list of available banknotes
     */
    List<Nominal> getState();

    /**
     * Update state of ATM with list of banknotes
     *
     * @param banknotes for holding in ATM
     */
    void updateState(List<Nominal> banknotes);
}
