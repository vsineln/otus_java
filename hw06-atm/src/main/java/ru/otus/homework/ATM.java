package ru.otus.homework;

import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
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
    private final Comparator<Cell> compareCells = Comparator.comparing(Cell::getNominal, Comparator.reverseOrder());
    private static final EnumSet<DEFAULT_NOMINALS> defaultNominals = EnumSet.allOf(DEFAULT_NOMINALS.class);
    private Set<Integer> banknotesNominals = new HashSet<>();
    private Set<Cell> cells;

    public ATM() {
        initiateWithDefaultNominals();
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
    public void addBanknotes(List<Integer> banknotes) {
        log.info("Add banknotes");
        if (CollectionUtils.isEmpty(banknotes)) {
            throw new IllegalArgumentException("No banknotes to add");
        }

        List<Integer> knownBanknotes = findKnownBanknotes(banknotes);

        for (Integer banknote : knownBanknotes) {
            Cell cell = findCell(banknote);
            cell.addBanknote();
        }

        if (CollectionUtils.disjunction(banknotes, knownBanknotes).size() > 0) {
            throw new ATMException("Banknotes with unknown nominal were found");
        }
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

    private void initiateWithDefaultNominals() {
        Set<Integer> nominalValues = defaultNominals.stream().map(n -> n.getValue()).collect(Collectors.toSet());
        banknotesNominals.addAll(nominalValues);
    }

    private void initiateNominals(Set<Integer> nominals) {
        banknotesNominals.addAll(nominals);
    }

    private Cell findCell(Integer nominal) {
        return cells.stream().filter(cell -> cell.getNominal() == nominal).findAny().get();
    }

    private List<Integer> findKnownBanknotes(List<Integer> banknotes) {
        return banknotes.stream().
                filter(banknote -> banknotesNominals.contains(banknote)).collect(Collectors.toList());
    }
}
