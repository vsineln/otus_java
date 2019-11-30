package ru.otus.homework;

/**
 * Class for holding ATM instance with its id, id is set while adding ATM to department
 */
public class AtmPoint {
    private int id;
    private ATM atm;

    public AtmPoint(int id, ATM atm) {
        this.id = id;
        this.atm = atm;
    }

    public int getId() {
        return id;
    }

    public ATM getAtm() {
        return atm;
    }

    @Override
    public String toString() {
        return "AtmPoint{" +
                "id=" + id +
                ", atm=" + atm +
                '}';
    }
}
