package ru.otus.homework;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.homework.impl.AtmDepartmentImpl;
import ru.otus.homework.impl.AtmImpl;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static ru.otus.homework.impl.Nominal.*;

class AtmDepartmentTest {
    private ATM atm1;
    private ATM atm2;
    private ATM atm3;
    private AtmDepartment department;

    @BeforeEach
    public void init() {
        atm1 = new AtmImpl(Sets.newHashSet(ONE_HUNDRED, TWO_HUNDRED, FIVE_HUNDRED));
        atm2 = new AtmImpl(Sets.newHashSet(ONE_THOUSAND, TWO_THOUSAND, FIVE_THOUSAND));
        atm3 = new AtmImpl(Sets.newHashSet(ONE_HUNDRED, TWO_HUNDRED, FIVE_HUNDRED, ONE_THOUSAND, TWO_THOUSAND, FIVE_THOUSAND));
    }

    @Test
    public void checkCommonBalance() {
        department = new AtmDepartmentImpl(Arrays.asList(atm1, atm2, atm3));
        addBnaknotesToAtm();

        int balance = department.checkCommonBalance();

        assertEquals(18000, balance);
    }

    @Test
    public void restoreInitialState_whenDepartmentInitializedWithEmptyATM() {
        department = new AtmDepartmentImpl(Arrays.asList(atm1, atm2, atm3));
        addBnaknotesToAtm();
        assertEquals(18000, department.checkCommonBalance());

        department.restoreInitialState();

        assertEquals(0, department.checkCommonBalance());
    }

    @Test
    public void restoreInitialState_whenDepartmentInitializedWithNonEmptyATM() {
        addBnaknotesToAtm();
        department = new AtmDepartmentImpl(Arrays.asList(atm1, atm2, atm3));
        assertEquals(18000, department.checkCommonBalance());
        addBnaknotesToAtm();
        assertEquals(36000, department.checkCommonBalance());

        department.restoreInitialState();

        assertEquals(18000, department.checkCommonBalance());
    }

    @Test
    public void restorePreviousState() {
        department = new AtmDepartmentImpl(Arrays.asList(atm1, atm2, atm3));
        addBnaknotesToAtm();
        assertEquals(18000, department.checkCommonBalance());

        department.saveState();

        atm2.addBanknotes(new ArrayList<>(Arrays.asList(ONE_THOUSAND)));
        atm3.addBanknotes(new ArrayList<>(Arrays.asList(ONE_THOUSAND)));
        assertEquals(20000, department.checkCommonBalance());

        department.restorePreviousState();
        assertEquals(18000, department.checkCommonBalance());

        department.restorePreviousState();
        assertEquals(0, department.checkCommonBalance());
    }

    private void addBnaknotesToAtm() {
        atm1.addBanknotes(new ArrayList<>(Arrays.asList(FIVE_HUNDRED, FIVE_HUNDRED, FIVE_HUNDRED)));
        atm2.addBanknotes(new ArrayList<>(Arrays.asList(FIVE_THOUSAND, ONE_THOUSAND, TWO_THOUSAND)));
        atm3.addBanknotes(new ArrayList<>(Arrays.asList(FIVE_THOUSAND, ONE_THOUSAND, TWO_THOUSAND, ONE_HUNDRED, TWO_HUNDRED, TWO_HUNDRED)));
    }
}
