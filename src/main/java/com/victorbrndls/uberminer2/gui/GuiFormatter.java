package com.victorbrndls.uberminer2.gui;

public class GuiFormatter {

    /**
     * @param progress between 0..1
     */
    public static String formatProgress(float progress) {
        if (Float.isNaN(progress)) progress = 0f;
        return String.format("%.0f%%", progress * 100);
    }

}
