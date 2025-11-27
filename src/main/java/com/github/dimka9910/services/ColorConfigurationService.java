package com.github.dimka9910.services;

import com.github.dimka9910.dto.ColorEnum;

public class ColorConfigurationService {


    public static ColorEnum getColorByAccountNameAndUserName(String accountName, String userName) {

        if (userName.toUpperCase().trim().equals("DIMA")) {
            if (accountName.equals("CARD_DIMA_VISA_RAIF")) {
                return ColorEnum.YELLOW;
            } else {
                return ColorEnum.GREEN;
            }
        } else {
            if (accountName.equals("CARD_KIKI_RAIF")) {
                return ColorEnum.PINK;
            } else {
                return ColorEnum.RED;
            }
        }

    }


}
