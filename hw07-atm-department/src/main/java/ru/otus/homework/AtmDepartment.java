package ru.otus.homework;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Class for operating with a range of ATMs
 */
public class AtmDepartment {
    private static final AtomicInteger refId = new AtomicInteger();
    private AtmConfiguration atmConfiguration = new AtmConfiguration();
    private AtmInteraction atmInteraction;

    public AtmDepartment(List<ATM> atmList) {
        atmInteraction = new AtmInteraction(createAtmPoints(atmList));
        saveState();
    }

    /**
     * Get balance for all ATMs in department
     *
     * @return balance value
     */
    public int checkCommonBalance() {
        return atmInteraction.getBalance();
    }

    /**
     * Save configuration for ATMs in department
     */
    public void saveState() {
        Map<Integer, List<Nominal>> savedState = atmInteraction.getState();
        atmConfiguration.saveConfiguration(savedState);
    }

    /**
     * Restore previously saved configuration of ATMs in department
     */
    public void restorePreviousState() {
        Map<Integer, List<Nominal>> savedState = atmConfiguration.rollBackConfiguration();
        atmInteraction.updateState(savedState);
    }

    /**
     * Restore initial configuration of ATMs in department
     */
    public void restoreInitialState() {
        Map<Integer, List<Nominal>> savedState = atmConfiguration.restoreConfiguration();
        atmInteraction.updateState(savedState);
    }

    private List<AtmPoint> createAtmPoints(List<ATM> atmList) {
        if (CollectionUtils.isEmpty(atmList)) {
            throw new IllegalArgumentException("No atm to restore");
        }

        return atmList.stream().map(atm -> new AtmPoint(refId.incrementAndGet(), atm)).collect(Collectors.toList());
    }
}
