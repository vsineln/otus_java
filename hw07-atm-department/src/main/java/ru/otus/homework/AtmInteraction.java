package ru.otus.homework;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class for retrieving information from ATMs
 */
public class AtmInteraction {
    private List<AtmPoint> atmPoints;

    public AtmInteraction(List<AtmPoint> atmPoints) {
        this.atmPoints = atmPoints;
    }

    public int getBalance() {
        return getState().values().stream().flatMap(Collection::stream).mapToInt(Nominal::getValue).sum();
    }

    public Map<Integer, List<Nominal>> getState() {
        return atmPoints.stream().collect(Collectors.
                toMap(atmPoint -> atmPoint.getId(), atmPoint -> atmPoint.getAtm().getState()));
    }

    public void updateState(Map<Integer, List<Nominal>> stateToUpdate) {
        Set<Integer> currentId = atmPoints.stream().map(atmPoint -> atmPoint.getId()).collect(Collectors.toSet());
        if (MapUtils.isEmpty(stateToUpdate) || !CollectionUtils.isEqualCollection(currentId, stateToUpdate.keySet())) {
            throw new ATMException("Atm update failure");
        }

        atmPoints.forEach(atmPoint -> {
            int id = atmPoint.getId();
            List<Nominal> banknotes = stateToUpdate.get(id);
            atmPoint.getAtm().updateState(banknotes);
        });
    }
}
