package ru.otus.homework;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.otus.homework.Nominal.*;

class ATMTest {
    private ATM atm;

    @Test
    void addBanknotesOfKnownNominal() {
        atm = new ATM();
        assertEquals(0, atm.getBalance());

        atm.addBanknotes(
                new ArrayList<>(Arrays.asList(ONE_HUNDRED, ONE_THOUSAND, ONE_HUNDRED, TWO_HUNDRED, FIVE_HUNDRED, FIVE_HUNDRED)));

        assertEquals(2400, atm.getBalance());
    }

    @Test
    void addBanknotesOfUnknownNominal() {
        atm = new ATM(Sets.newHashSet(ONE_HUNDRED, TWO_HUNDRED, FIVE_HUNDRED));
        assertEquals(0, atm.getBalance());

        assertThrows(ATMException.class, () -> atm.addBanknotes(
                new ArrayList<>(Arrays.asList(FIVE_HUNDRED, ONE_THOUSAND))));

        assertEquals(500, atm.getBalance());
    }

    @Test
    void getBanknotesBySum() {
        atm = new ATM();
        atm.addBanknotes(new ArrayList<>(Arrays.asList(ONE_HUNDRED, TWO_HUNDRED, ONE_HUNDRED, FIVE_HUNDRED, ONE_THOUSAND, FIVE_HUNDRED, ONE_HUNDRED)));
        assertEquals(2500, atm.getBalance());

        List<Nominal> returnedBanknotes = atm.getBanknotes(300);

        assertEquals(Arrays.asList(TWO_HUNDRED, ONE_HUNDRED), returnedBanknotes);
        assertEquals(300, getSum(returnedBanknotes));
        assertEquals(2200, atm.getBalance());
    }

    @Test
    public void getBanknotesBySumIsImpossible() {
        atm = new ATM();
        atm.addBanknotes(new ArrayList<>(Arrays.asList(ONE_HUNDRED, TWO_HUNDRED, ONE_HUNDRED, FIVE_HUNDRED, ONE_THOUSAND, FIVE_HUNDRED, ONE_HUNDRED)));
        assertEquals(2500, atm.getBalance());

        assertThrows(ATMException.class, () -> atm.getBanknotes(350));
        assertEquals(2500, atm.getBalance());
    }

    @Test
    void getAllBanknote() {
        atm = new ATM();
        atm.addBanknotes(new ArrayList<>(Arrays.asList(ONE_HUNDRED, ONE_HUNDRED, ONE_HUNDRED, TWO_HUNDRED, FIVE_HUNDRED)));
        assertEquals(1000, atm.getBalance());

        List<Nominal> returnedBanknotes = atm.getAllBanknote();

        assertEquals(5, returnedBanknotes.size());
        assertEquals(1000, getSum(returnedBanknotes));
        assertEquals(0, atm.getBalance());
    }

    @Test
    public void testDefaultNominals() {
        atm = new ATM();
        atm.addBanknotes(new ArrayList<>(Arrays.asList(TWO_HUNDRED, ONE_HUNDRED, ONE_HUNDRED, TWO_HUNDRED, TWO_HUNDRED, TWO_HUNDRED, ONE_THOUSAND)));
        assertEquals(2000, atm.getBalance());

        List<Nominal> returnedBanknotes = atm.getAllBanknote();

        assertEquals(7, returnedBanknotes.size());
        assertEquals(2000, getSum(returnedBanknotes));
        assertEquals(0, atm.getBalance());
    }


    private int getSum(List<Nominal> banknotes) {
        return banknotes.stream().map(banknote -> banknote.getValue()).mapToInt(Integer::intValue).sum();
    }
}
