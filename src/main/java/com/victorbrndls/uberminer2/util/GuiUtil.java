package com.victorbrndls.uberminer2.util;

public class GuiUtil {

    public static int lerp(int min, int max, float amt) {
        amt = clamp(amt, 0F, 1F);
        return (int) ((max - min) * amt);
    }

    private static float clamp(float val, float min, float max) {
        return val > max ? max : Math.max(val, min);
    }

}
