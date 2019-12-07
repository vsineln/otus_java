package ru.otus.homework;

public interface AtmDepartment {

    /**
     * Get balance for all ATMs in department
     *
     * @return balance value
     */
    int checkCommonBalance();

    /**
     * Save configuration for ATMs in department
     */
    void saveState();

    /**
     * Restore previously saved configuration of ATMs in department
     */
    void restorePreviousState();

    /**
     * Restore initial configuration of ATMs in department
     */
    void restoreInitialState();
}
