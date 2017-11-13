package com.sctjsj.forestcommunity.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * Œ¢–≈÷ß∏∂«©√˚
 * @author iYjrg_xiebin
 * @date 2015ƒÍ11‘¬25»’œ¬ŒÁ4:47:07
 * */
public class WXSignUtils {
    private static String Key = "kxbsnpwdxhyzdznzbzrftyfhhg125984";

    /**
     * Œ¢–≈÷ß∏∂«©√˚À„∑®sign
     * @param characterEncoding
     * @param parameters
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String createSign(String characterEncoding,SortedMap<Object,Object> parameters){
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();//À˘”–≤Œ”Î¥´≤Œµƒ≤Œ ˝∞¥’’accsii≈≈–Ú£®…˝–Ú£©
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + Key);
        String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
        return sign;
    }



}
