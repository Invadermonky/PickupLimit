package com.invadermonky.pickuplimit.util;

import com.invadermonky.pickuplimit.PickupLimit;

public class StringHelper {
    public static String getTranslationKey(String unloc, String type, String... params) {
        StringBuilder str = new StringBuilder(type + "." + PickupLimit.MOD_ID + ":" + unloc);
        for(String param : params) {
            str.append(".").append(param);
        }
        return str.toString();
    }
}
