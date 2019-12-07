package ru.otus.homework.impl;

import ru.otus.homework.AtmConfiguration;
import ru.otus.homework.exception.ATMException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Class for holding ATM department configurations, all its ATMs' ids with available banknotes
 */
public class AtmConfigurationImpl implements AtmConfiguration {
    private Deque<Map<Integer, List<Nominal>>> stack = new ArrayDeque<>();

    @Override
    public void saveConfiguration(Map<Integer, List<Nominal>> savedState) {
        stack.push(savedState);
    }

    @Override
    public Map<Integer, List<Nominal>> rollBackConfiguration() {
        try {
            return stack.pop();
        } catch (NoSuchElementException e) {
            throw new ATMException("No atm to restore");
        }
    }

    @Override
    public Map<Integer, List<Nominal>> restoreConfiguration() {
        try {
            Map<Integer, List<Nominal>> savedState = stack.getLast();
            stack = new ArrayDeque<>();
            return savedState;
        } catch (NoSuchElementException e) {
            throw new ATMException("No atm to restore");
        }
    }
}
