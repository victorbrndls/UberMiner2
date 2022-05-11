package com.victorbrndls.uberminer2.item;

public enum OreAttractorTier {
    TIER_I(5, 60),
    TIER_II(7, 40),
    TIER_III(9, 25);

    final int radius;
    final int operationTime;

    OreAttractorTier(int radius, int operationTime) {
        this.radius = radius;
        this.operationTime = operationTime;
    }
}