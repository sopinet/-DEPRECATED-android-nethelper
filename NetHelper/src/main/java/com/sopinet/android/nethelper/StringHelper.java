package com.sopinet.android.nethelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {
    private static final char[] HEXADECIMAL;
    private static Pattern pattern;
    private static Matcher matcher;

    static
    {
        char[] arrayOfChar = new char[16];
        arrayOfChar[0] = 48;
        arrayOfChar[1] = 49;
        arrayOfChar[2] = 50;
        arrayOfChar[3] = 51;
        arrayOfChar[4] = 52;
        arrayOfChar[5] = 53;
        arrayOfChar[6] = 54;
        arrayOfChar[7] = 55;
        arrayOfChar[8] = 56;
        arrayOfChar[9] = 57;
        arrayOfChar[10] = 97;
        arrayOfChar[11] = 98;
        arrayOfChar[12] = 99;
        arrayOfChar[13] = 100;
        arrayOfChar[14] = 101;
        arrayOfChar[15] = 102;
        HEXADECIMAL = arrayOfChar;
    }

    public static String md5(String paramString)
    {
        String str;
        try
        {
            byte[] arrayOfByte = MessageDigest.getInstance("MD5").digest(paramString.getBytes());
            StringBuilder localStringBuilder = new StringBuilder(2 * arrayOfByte.length);
            for (int i = 0; ; i++)
            {
                if (i >= arrayOfByte.length)
                {
                    str = localStringBuilder.toString();
                    break;
                }
                int j = 0xF & arrayOfByte[i];
                int k = (0xF0 & arrayOfByte[i]) >> 4;
                localStringBuilder.append(HEXADECIMAL[k]);
                localStringBuilder.append(HEXADECIMAL[j]);
            }
        }
        catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
        {
            str = null;
        }
        return str;
    }

    public static boolean validateEmail(String paramString)
    {
        pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        matcher = pattern.matcher(paramString);
        return matcher.matches();
    }

    public static String getVerticalString(String text) {
        String newtext = "";
        for (int i = 0; i < text.length(); i++) {
            newtext += text.charAt(i) + "\n";
        }
        return newtext;
    }
}