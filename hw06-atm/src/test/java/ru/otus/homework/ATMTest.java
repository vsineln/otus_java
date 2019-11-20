package ru.otus.homework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ATMTest {
    private ATM atm;
    private Set<Integer> nominals = new HashSet<>();

    @BeforeEach
    public void init(){
        nominals = Arrays.stream(DEFAULT_NOMINALS.values()).map(n -> n.getValue()).collect(Collectors.toSet());
    }

    @Test
    void addBanknotesOfKnownNominal() {
        atm = new ATM(nominals);
        assertEquals(0, atm.getBalance());

        atm.addBanknotes(
                new ArrayList<>(Arrays.asList(100, 1000, 100, 200, 500, 500)));

        assertEquals(2400, atm.getBalance());
    }

    @Test
    void addBanknotesOfUnknownNominal() {
        atm = new ATM(nominals);
        assertEquals(0, atm.getBalance());

        assertThrows(ATMException.class, () -> atm.addBanknotes(
                new ArrayList<>(Arrays.asList(500, 700))));

        assertEquals(500, atm.getBalance());
    }

    @Test
    void getBanknotesBySum() {
        atm = new ATM(nominals);
        atm.addBanknotes(new ArrayList<>(Arrays.asList(100, 200, 100, 500, 1000, 500, 100)));
        assertEquals(2500, atm.getBalance());

        List<Integer> returnedBanknotes = atm.getBanknotes(300);

        assertEquals(Arrays.asList(200, 100), returnedBanknotes);
        assertEquals(300, getSum(returnedBanknotes));
        assertEquals(2200, atm.getBalance());
    }

    @Test
    public void getBanknotesBySumIsImpossible() {
        atm = new ATM(nominals);
        atm.addBanknotes(new ArrayList<>(Arrays.asList(100, 200, 100, 500, 1000, 500, 100)));
        assertEquals(2500, atm.getBalance());

        assertThrows(ATMException.class, () -> atm.getBanknotes(350));
        assertEquals(2500, atm.getBalance());
    }

    @Test
    void getAllBanknote() {
        atm = new ATM();
        atm.addBanknotes(new ArrayList<>(Arrays.asList(100, 100, 100, 200, 500)));
        assertEquals(1000, atm.getBalance());

        List<Integer> returnedBanknotes = atm.getAllBanknote();

        assertEquals(5, returnedBanknotes.size());
        assertEquals(1000, getSum(returnedBanknotes));
        assertEquals(0, atm.getBalance());
    }

    @Test
    public void testDefaultNominals() {
        atm = new ATM();
        atm.addBanknotes(new ArrayList<>(Arrays.asList(200, 100, 100, 200, 200, 200, 1000)));
        assertEquals(2000, atm.getBalance());

        List<Integer> returnedBanknotes = atm.getAllBanknote();

        assertEquals(7, returnedBanknotes.size());
        assertEquals(2000, getSum(returnedBanknotes));
        assertEquals(0, atm.getBalance());
    }


    private int getSum(List<Integer> banknotes) {
        return banknotes.stream().mapToInt(Integer::intValue).sum();
    }
}
