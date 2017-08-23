package com.hellowo.teamfinder.utils;

import android.text.TextUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * 리스트를 배열 스트링 형태로 추출
     * @param list 리스트
     * @return 스트링
     */
    public static String ListToString(List<?> list){
        String result = "[";
        for (int i = 0; i < list.size(); i++) {
            if(i > 0){
                result += ",";
            }
            result += list.get(i).toString();
        }
        return result + "]";
    }

    /**
     * url 에서 도메인 추출
     */
    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    /**
     * url 에서 도메인 추출
     */
    public static String[] splitTextToTitleAndMemo(String text) {
        String[] result = new String[2];
        if(text != null){
            int secondLinePos = text.indexOf("\n");
            if(secondLinePos == -1 && text.length() < 50){
                result[0] = text;
                result[1] = null;
            }else{
                if(secondLinePos == -1){
                    result[0] = text.substring(0, 20).trim();
                    result[1] = text.substring(20, text.length()).trim();
                }else{
                    result[0] = text.substring(0, secondLinePos).trim();
                    result[1] = text.substring(secondLinePos, text.length()).trim();
                }
            }
        }
        return result;
    }


    /**
     * 이메일 형식 검사
     */
    public static boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Set 데이터를 :로 나누어지는 스트링 형태로 만듬
     * @param set Set
     * @return 스트링
     */
    public static String setToColonDevidedString(Set<String> set) {
        String result = "";
        try{
            for (String cate : set){
                result = result + ":" +cate;
            }
            if(result.length() > 0){
                result = result.substring(1, result.length());
            }
        }catch (Exception ignore){}
        return result;
    }

    private static char[] c = new char[]{'k', 'm', 'b', 't'};

    /**
     * 1000 => 1k
     5821 => 5.8k
     10500 => 10k
     101800 => 101k
     2000000 => 2m
     7800000 => 7.8m
     92150000 => 92m
     123200000 => 123m
     9999999 => 9.9m
     */
    private static String coolFormat(double n, int iteration) {
        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) %10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99)? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + "" + c[iteration])
                : coolFormat(d, iteration+1));

    }
}
