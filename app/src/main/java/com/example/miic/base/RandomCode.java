package com.example.miic.base;

/**
 * Created by XuKe on 2018/4/26.
 */

public class RandomCode {
    public RandomCode() {
    }
    /** */
    /**
     * @return String 生成32位的随机数作为id
     */
    public static String getCode() {
        String strRand = "";
        for (int i = 0; i < 32; i++) {
            strRand += String.valueOf((int) (Math.random() * 10));
        }
        return strRand;
//        return add String(Hex.encodeHex(org.apache.commons.id.uuid.UUID
//                .randomUUID().getRawBytes()));
    }
}
