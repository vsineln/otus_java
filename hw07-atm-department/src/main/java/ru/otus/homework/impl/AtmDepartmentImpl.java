package ru.otus.homework.impl;

import org.apache.commons.collections4.CollectionUtils;
import ru.otus.homework.ATM;
import ru.otus.homework.AtmConfiguration;
import ru.otus.homework.AtmDepartment;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Class for operating with a range of ATMs
 */
public class AtmDepartmentImpl implements AtmDepartment {
    private static final AtomicInteger refId = new AtomicInteger();
    private AtmConfiguration atmConfiguration = new AtmConfigurationImpl();
    private AtmInteractionImpl atmInteraction;

    public AtmDepartmentImpl(List<ATM> atmList) {
        atmInteraction = new AtmInteractionImpl(createAtmPoints(atmList));
        saveState();
    }

    @Override
    public int checkCommonBalance() {
        return atmInteraction.getBalance();
    }

    @Override
    public void saveState() {
        Map<Integer, List<Nominal>> savedState = atmInteraction.getState();
        atmConfiguration.saveConfiguration(savedState);
    }

    @Override
    public void restorePreviousState() {
        Map<Integer, List<Nominal>> savedState = atmConfiguration.rollBackConfiguration();
        atmInteraction.updateState(savedState);
    }

    @Override
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
