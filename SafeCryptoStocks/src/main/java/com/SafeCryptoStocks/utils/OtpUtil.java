package com.SafeCryptoStocks.utils;

import java.security.SecureRandom;
import java.util.Random;

public class OtpUtil {

    private static final String CHAR_LIST = "0123456789";
    private static final int OTP_LENGTH = 6;

    public static String generateOtp() {
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        Random random = new SecureRandom();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(CHAR_LIST.charAt(random.nextInt(CHAR_LIST.length())));
        }
        return otp.toString();
    }
}
