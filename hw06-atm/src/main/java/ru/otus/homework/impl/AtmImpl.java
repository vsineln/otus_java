package ru.otus.homework.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.ATM;
import ru.otus.homework.exception.ATMException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Class for ATM, which holds a number of Cell(s) with default values or custom ones
 */
public class AtmImpl implements ATM {
    private static final Logger log = LoggerFactory.getLogger(AtmImpl.class);
    private final Comparator<Cell> compareCells = Comparator.comparing(Cell::getNominal, Comparator.reverseOrder());
    private static final Set<Nominal> defaulNominals = EnumSet.allOf(Nominal.class);
    private Set<Cell> cells = new TreeSet<>(compareCells);

    public AtmImpl() {
        initiateCells(defaulNominals);
    }

    public AtmImpl(Set<Nominal> banknotesNominals) {
        initiateCells(banknotesNominals);
    }

    @Override
    public void addBanknotes(List<Nominal> banknotes) {
        log.debug("Add banknotes");
        if (CollectionUtils.isEmpty(banknotes)) {
            throw new IllegalArgumentException("No banknotes to add");
        }

        List<Nominal> knownBanknotes = findKnownBanknotes(banknotes);

        for (Nominal banknote : knownBanknotes) {
            Cell cell = findCell(banknote);
            cell.addBanknote();
        }

        if (CollectionUtils.disjunction(banknotes, knownBanknotes).size() > 0) {
            throw new ATMException("Banknotes with unknown nominal were found");
        }
    }

    @Override
    public List<Nominal> getBanknotes(int sum) {
        log.debug("Get banknotes");
        if (sum > getBalance()) {
            throw new ATMException("Not enough money");
        }

        List<Nominal> result = getSum(sum);
        for (Nominal nominal : result) {
            Cell cell = findCell(nominal);
            cell.removeBanknote();
        }
        return result;
    }

    @Override
    public List<Nominal> getAllBanknote() {
        log.debug("Get all banknotes");
        if (CollectionUtils.isEmpty(cells)) {
            throw new ATMException("ATM is empty");
        }
        if (getBalance() == 0) {
            throw new ATMException("Balance is empty");
        }
        List<Nominal> banknotesToReturn = getBanknotes();
        refreshCells();
        return banknotesToReturn;
    }

    @Override
    public int getBalance() {
        return cells.stream().map(cell -> cell.getNominal().getValue() * cell.getCount()).mapToInt(Integer::intValue).sum();
    }

    @Override
    public List<Nominal> getState() {
        return getBanknotes();
    }

    @Override
    public void updateState(List<Nominal> banknotes) {
        refreshCells();
        if (CollectionUtils.isNotEmpty(banknotes)) {
            addBanknotes(banknotes);
        }
    }

    @Override
    public String toString() {
        return "ATM{" + cells + '}';
    }

    private void initiateCells(Set<Nominal> banknotesNominals) {
        for (Nominal nominal : banknotesNominals) {
            cells.add(new Cell(nominal));
        }
    }

    private Cell findCell(Nominal nominal) {
        return cells.stream().filter(cell -> cell.getNominal() == nominal).findAny().get();
    }

    private List<Nominal> findKnownBanknotes(List<Nominal> banknotes) {
        return banknotes.stream().
                filter(banknote -> getNominals().contains(banknote)).collect(Collectors.toList());
    }

    private Set<Nominal> getNominals() {
        return cells.stream().map(Cell::getNominal).collect(Collectors.toSet());
    }

    private void refreshCells() {
        cells.forEach(Cell::removeAllBanknotes);
    }

    private List<Nominal> getSum(int sum) {
        List<Integer> result = new ArrayList<>();
        for (Cell cell : cells) {
            int nominal = cell.getNominal().getValue();
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
        return result.stream().map(Nominal::valueOf).collect(Collectors.toList());
    }

    private List<Nominal> getBanknotes() {
        return cells.stream().
                flatMap(cell -> Collections.nCopies(cell.getCount(), cell.getNominal()).stream()).
                collect(Collectors.toList());
    }
}
