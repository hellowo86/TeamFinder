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
    public final static String ListToString(List<?> list){
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

}
