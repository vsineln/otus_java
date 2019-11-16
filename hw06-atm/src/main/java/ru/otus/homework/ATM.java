package ru.otus.homework;

import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Class for ATM, which holds a number of Cell(s) with default values or custom ones
 */
@Log
public class ATM {
    private Comparator<Cell> compareCells = Comparator.comparing(Cell::getNominal, Comparator.reverseOrder());
    private Set<Integer> defaultNominals = new HashSet<>(Arrays.asList(100, 200, 500, 1000, 5000));
    private Set<Integer> banknotesNominals = new HashSet<>();
    private Set<Cell> cells;

    public ATM() {
        initiateNominals(defaultNominals);
        initiateCells();
    }

    public ATM(Set<Integer> nominals) {
        initiateNominals(nominals);
        initiateCells();
    }

    /**
     * Adds banknotes in appropriate Cells in ATM
     *
     * @param banknotes to add
     * @return Collection of unknown banknotes
     */
    public Collection<Integer> addBanknotes(List<Integer> banknotes) {
        log.info("Add banknotes");
        if (CollectionUtils.isEmpty(banknotes)) {
            throw new ATMException("No banknotes to add");
        }

        List<Integer> knownBanknotes = banknotes.stream().
                filter(banknote -> banknotesNominals.contains(banknote)).collect(Collectors.toList());
        for (Integer banknote : knownBanknotes) {
            Cell cell = findCell(banknote);
            cell.addBanknote();
        }
        return CollectionUtils.disjunction(banknotes, knownBanknotes);
    }

    /**
     * Gets requested sum from ATM
     *
     * @param sum to get
     * @return List of banknotes which covers requested sum
     */
    public List<Integer> getBanknotes(int sum) {
        log.info("Get banknotes");
        if (sum > getBalance()) {
            throw new ATMException("Not enough money");
        }

        List<Integer> result = new ArrayList<>();
        for (Cell cell : cells) {
            int nominal = cell.getNominal();
            int count = cell.getCount();
            while (sum - nominal >= 0 && count > 0) {
                result.add(nominal);
                sum -= nominal;
                count--;
            }
        }
        if (sum > 0) {
            throw new ATMException("Not enough banknotes for requested sum");
        }
        for (Integer nominal : result) {
            Cell cell = findCell(nominal);
            cell.removeBanknote();
        }
        return result;
    }

    /**
     * Gets all banknotes from ATM
     *
     * @return List of all banknotes from ATM
     */
    public List<Integer> getAllBanknote() {
        log.info("Get all banknotes");
        if (CollectionUtils.isEmpty(cells)) {
            throw new ATMException("ATM is empty");
        }
        if (getBalance() == 0) {
            throw new ATMException("Balance is empty");
        }
        List<Integer> banknotesToReturn = cells.stream().
                flatMap(cell -> Collections.nCopies(cell.getCount(), cell.getNominal()).stream()).
                collect(Collectors.toList());
        initiateCells();
        return banknotesToReturn;
    }

    /**
     * Gets the balance
     *
     * @return balance of the ATM
     */
    public int getBalance() {
        return cells.stream().map(cell -> cell.getNominal() * cell.getCount()).mapToInt(Integer::intValue).sum();
    }

    private void initiateCells() {
        cells = new TreeSet<>(compareCells);
        for (Integer nominal : banknotesNominals) {
            cells.add(new Cell(nominal));
        }

    }

    private void initiateNominals(Set<Integer> nominals) {
        if (CollectionUtils.isEmpty(nominals)) {
            banknotesNominals.addAll(defaultNominals);
        } else {
            banknotesNominals.addAll(nominals);
        }
    }

    private Cell findCell(Integer nominal) {
        return cells.stream().filter(cell -> cell.getNominal() == nominal).findAny().get();
    }
}
