package com.miriamlaurel.jcarb.model.portfolio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Portfolio {
    private final List<Position> positions = new ArrayList<>();

    public synchronized void addPosition(Position position) {
        positions.add(position);
    }

    public synchronized void removePosition(Position position) {
        positions.remove(position);
    }

    public boolean isEmpty() {
        return positions.isEmpty();
    }

    public synchronized void clear() {
        positions.clear();
    }

    public synchronized List<Position> getPositions() {
        return Collections.unmodifiableList(positions);
    }

    public synchronized BigDecimal totalProfitLoss(BigDecimal bid, BigDecimal ask) {
        // todo use global market! Won't work now unless all positions are homogeneous
        BigDecimal sum = BigDecimal.ZERO;
        for (Position position : positions) {
            BigDecimal price = position.getPrimary().compareTo(BigDecimal.ZERO) > 0 ? bid : ask;
            sum = sum.add(position.getProfitLoss(price));
        }
        return sum;
    }
}
