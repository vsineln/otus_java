package ru.otus.homework;

/**
 * Class for ATM's Cell, it consists of banknote's nominal and number of banknotes for this nominal
 */
public class Cell {
    private int nominal;
    private int count= 0;

    public Cell(int nominal) {
        this.nominal = nominal;
    }

    public void addBanknote(){
        count +=1;
    }

    public void removeBanknote(){
        count -=1;
    }

    public int getNominal() {
        return nominal;
    }

    public int getCount() {
        return count;
    }
}
