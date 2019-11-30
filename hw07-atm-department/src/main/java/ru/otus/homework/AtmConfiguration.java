package ru.otus.homework;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Class for holding ATM department configurations, all its ATMs' ids with available banknotes
 */
public class AtmConfiguration {
    private Deque<Map<Integer, List<Nominal>>> stack = new ArrayDeque<>();

    /**
     * Save state of ATMs for department
     *
     * @param savedState map with id and list of banknotes of every ATM
     */
    void saveConfiguration(Map<Integer, List<Nominal>> savedState) {
        stack.push(savedState);
    }

    /**
     * Return department's last saved configuration
     *
     * @return map of all ATMs' ids with lists of banknotes
     */
    Map<Integer, List<Nominal>> rollBackConfiguration() {
        try {
            return stack.pop();
        } catch (NoSuchElementException e) {
            throw new ATMException("No atm to restore");
        }
    }

    /**
     * Return department's initial configuration
     *
     * @return map of all ATMs' ids with lists of banknotes
     */
    Map<Integer, List<Nominal>> restoreConfiguration() {
        try {
            Map<Integer, List<Nominal>> savedState = stack.getLast();
            stack = new ArrayDeque<>();
            return savedState;
        } catch (NoSuchElementException e) {
            throw new ATMException("No atm to restore");
        }
    }
}
