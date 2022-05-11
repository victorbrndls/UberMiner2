package com.victorbrndls.uberminer2.item;

public enum OreAttractorTier {
    TIER_I(5, 64, 60),
    TIER_II(7, 160, 40),
    TIER_III(9, 400, 25);

    final int radius;
    final int durability;
    final int operationTime;

    OreAttractorTier(int radius, int durability, int operationTime) {
        this.radius = radius;
        this.durability = durability;
        this.operationTime = operationTime;
    }
}