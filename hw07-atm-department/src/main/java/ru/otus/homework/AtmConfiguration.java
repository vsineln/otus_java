package ru.otus.homework;

import ru.otus.homework.impl.Nominal;

import java.util.List;
import java.util.Map;

public interface AtmConfiguration {

    /**
     * Save state of ATMs for department
     *
     * @param savedState map with id and list of banknotes of every ATM
     */
    void saveConfiguration(Map<Integer, List<Nominal>> savedState);

    /**
     * Return department's last saved configuration
     *
     * @return map of all ATMs' ids with lists of banknotes
     */
    Map<Integer, List<Nominal>> rollBackConfiguration();

    /**
     * Return department's initial configuration
     *
     * @return map of all ATMs' ids with lists of banknotes
     */
    Map<Integer, List<Nominal>> restoreConfiguration();
}
