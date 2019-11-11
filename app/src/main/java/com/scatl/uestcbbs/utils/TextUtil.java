package com.scatl.uestcbbs.utils;

import android.text.Editable;

/**
 * author: sca_tl
 * description:
 * date: 2019/07/06 15:53
 */
public class TextUtil {
    /**
     * author: sca_tl
     * description: unicode解码
     */
    public static String decodeUnicode(final String unicodeStr) {
        if (unicodeStr == null) {
            return null;
        }
        StringBuilder retBuf = new StringBuilder();
        int maxLoop = unicodeStr.length();
        for (int i = 0; i < maxLoop; i++) {
            if (unicodeStr.charAt(i) == '\\') {
                if ((i < maxLoop - 5) && ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr.charAt(i + 1) == 'U')))
                    try {
                        retBuf.append((char) Integer.parseInt(unicodeStr.substring(i + 2, i + 6), 16));
                        i += 5;
                    } catch (NumberFormatException localNumberFormatException) {
                        retBuf.append(unicodeStr.charAt(i));
                    }
                else
                    retBuf.append(unicodeStr.charAt(i));
            } else {
                retBuf.append(unicodeStr.charAt(i));
            }
        }
        return retBuf.toString();
    }

    /**
     * author: sca_tl
     * description: 判断edittable是否为空
     */
    public static boolean isEditableNull(Editable editable) {
        return editable == null;
    }


}