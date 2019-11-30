package ru.otus.homework;

/**
 * Class for ATM's Cell, it consists of banknote's nominal and number of banknotes for this nominal
 */
public class Cell {
    private Nominal nominal;
    private int count = 0;

    public Cell(Nominal nominal) {
        this.nominal = nominal;
    }

    public void addBanknote() {
        count += 1;
    }

    public void removeBanknote() {
        count -= 1;
    }

    public void removeAllBanknotes() {
        count = 0;
    }

    public Nominal getNominal() {
        return nominal;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "nominal=" + nominal +
                ", count=" + count +
                '}';
    }
}
