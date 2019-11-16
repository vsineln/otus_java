package ru.otus.homework;

import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ATMTest {
    private ATM atm;

    @Test
    void addBanknotesOfKnownNominal() {
        atm = new ATM(ImmutableSet.of(100, 200, 500, 1000));
        assertEquals(0, atm.getBalance());

        Collection<Integer> returnedBanknotes = atm.addBanknotes(
                new ArrayList<>(Arrays.asList(100, 1000, 100, 200, 500, 500)));

        assertEquals(2400, atm.getBalance());
        assertEquals(0, returnedBanknotes.size());
    }

    @Test
    void addBanknotesOfUnknownNominal() {
        atm = new ATM(ImmutableSet.of(100, 200, 500, 1000));
        assertEquals(0, atm.getBalance());

        Collection<Integer> returnedBanknotes = atm.addBanknotes(
                new ArrayList<>(Arrays.asList(500, 300)));

        assertEquals(500, atm.getBalance());
        assertTrue(returnedBanknotes.contains(300));
    }

    @Test
    void getBanknotesBySum() {
        atm = new ATM(ImmutableSet.of(100, 200, 500, 1000, 300));
        atm.addBanknotes(new ArrayList<>(Arrays.asList(100, 200, 100, 500, 1000, 500, 100)));
        assertEquals(2500, atm.getBalance());

        List<Integer> returnedBanknotes = atm.getBanknotes(300);

        assertEquals(Arrays.asList(200, 100), returnedBanknotes);
        assertEquals(300, getSum(returnedBanknotes));
        assertEquals(2200, atm.getBalance());
    }

    @Test
    public void getBanknotesBySumIsImpossible() {
        atm = new ATM(ImmutableSet.of(100, 200, 500, 1000, 300));
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

    private int getSum(List<Integer> banknotes) {
        return banknotes.stream().mapToInt(Integer::intValue).sum();
    }
}
