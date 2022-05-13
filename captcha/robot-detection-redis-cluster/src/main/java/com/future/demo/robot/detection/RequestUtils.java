package com.future.demo.robot.detection;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class RequestUtils {

    public static int getInt(HttpServletRequest request,String parameterName){
        String val=request.getParameter(parameterName);
        int retVal=0;
        try{
            if(!StringUtils.isBlank(val))
                retVal=Integer.parseInt(val);
        }catch(NumberFormatException ex){
        }
        return retVal;
    }

    /**
     *
     * @param request
     * @param parameterName
     * @return
     */
    public static double getDouble(HttpServletRequest request,String parameterName){
        String val=request.getParameter(parameterName);
        double retVal=0;
        try{
            if(!StringUtils.isBlank(val))
                retVal=Double.parseDouble(val);
        }catch(NumberFormatException ex){
        }
        return retVal;
    }

    /**
     *
     * @param request
     * @param parameterName
     * @return
     */
    public static float getFloat(HttpServletRequest request,String parameterName){
        String val=request.getParameter(parameterName);
        float retVal=0;
        try{
            if(!StringUtils.isBlank(val))
                retVal=Float.parseFloat(val);
        }catch(NumberFormatException ex){
        }
        return retVal;
    }

    /**
     *
     * @param request
     * @param parameterName
     * @return
     */
    public static long getLong(HttpServletRequest request,String parameterName){
        String val=request.getParameter(parameterName);
        long retVal=0;
        try{
            if(!StringUtils.isBlank(val))
                retVal=Long.parseLong(val);
        }catch(NumberFormatException ex){
        }
        return retVal;
    }
    public static boolean getBoolean(HttpServletRequest request,String parameterName){
        String val=request.getParameter(parameterName);
        boolean retVal=false;
        try{
            if(!StringUtils.isBlank(val))
                retVal=Boolean.parseBoolean(val);
        }catch(Exception ex){
        }
        return retVal;
    }

    public static String getString(HttpServletRequest request,String parameterName){
        String val=request.getParameter(parameterName);
        if(val==null||"null".equals(val))
            return "";
        val.replace("'", "''");
        return val;
    }
    public static String getString2(HttpServletRequest request,String parameterName){
        String val="";
        try {
            if(parameterName!="" && parameterName!=null){
                val = new String(request.getParameter(parameterName).getBytes("ISO-8859-1"),"UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(val==null||"null".equals(val))
            return "";
        val.replace("'", "''");
        return val;
    }

    /**
     * 獲取Session屬性
     * @param request
     * @param attributeName
     * @return
     */
    public static String getSessionAttributeString(HttpServletRequest request,String attributeName){
        if(request==null)
            return null;
        Object obj=request.getSession().getAttribute(attributeName);
        if(obj==null)
            return null;
        String val=obj.toString();
        return val;
    }

    /**
     * 讀取保存在Session中的對象
     * @param request
     * @param attributeName
     * @return
     */
    public static Object getSessionObject(HttpServletRequest request,String attributeName){
        Object obj=request.getSession().getAttribute(attributeName);
        return obj;
    }

    /**
     * 保存對象到Session中
     * @param request
     * @param attributeName
     * @param obj
     */
    public static void setSessionAttribute(HttpServletRequest request,String attributeName,Object obj){
        request.getSession().setAttribute(attributeName, obj);
    }

    /**
     * 獲取SessionID
     * @param request
     * @return
     */
    public static String getSessionID(HttpServletRequest request){
        String sessionID=request.getSession().getId();
        return sessionID;
    }

    public static boolean isParaInQuery(HttpServletRequest request,String param){
        Object obj=request.getParameter(param);
        return obj!=null;
    }
    public static void removeSessionAttribute(HttpServletRequest request,String attributeName){
        if(StringUtils.isBlank(attributeName))
            return;
        request.getSession().removeAttribute(attributeName);
    }

    public static String getRemoteAddress(HttpServletRequest request) {
        // 所有openresty前段都已經配置x-forwarded-for，通常沒有可能x-forwarded-for沒有值
        // CDN提供商通常情況會包含x-forwarded-for值
        String ip = request.getHeader("x-forwarded-for");
        if(StringUtils.isBlank(ip)) {
            ip = request.getRemoteAddr();
        }

        // 獲取第一個ip地址為客戶端真實ip地址
        if(!StringUtils.isBlank(ip) && ip.contains(",")){
            ip = ip.split(",")[0];
        }

        if(!StringUtils.isBlank(ip) && ip.length()>50) {
            ip=ip.substring(0,49);
        }
        return ip;
    }

    public static String getProxyIP(HttpServletRequest request){
        String ip = request.getHeader("X-Proxy-IP");
//		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//		    ip = request.getHeader("Proxy-Client-IP");
//		}
//		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//		    ip = request.getHeader("WL-Proxy-Client-IP");
//		}
//		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//		    ip = request.getRemoteAddr();
//		}
//		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//		    ip = request.getHeader("X-Real-IP");
//		}
        if(StringUtils.isBlank(ip))
            ip="127.0.0.1";
        if(!StringUtils.isBlank(ip)){
            if(ip.contains(","))
                ip=ip.substring(0,ip.indexOf(","));
        }

        if(!StringUtils.isBlank(ip)&&ip.length()>50)
            ip=ip.substring(0,49);
        return ip;
    }

    public static String getCookie(HttpServletRequest request,String cookieName){
        if(StringUtils.isBlank(cookieName))
            return null;
        Cookie []cookies=request.getCookies();
        if(cookies!=null&&cookies.length>0){
            for(int i=0;i<cookies.length;i++){
                Cookie cookieTemp=cookies[i];
                String cookieNameTemp=cookieTemp.getName();
                String cookieValTemp=cookieTemp.getValue();
                if(cookieName.equals(cookieNameTemp))
                    return cookieValTemp;
            }
        }
        return null;
    }

    public static String getServerName(HttpServletRequest request){
        String s=request.getServerName();
        return s;
    }
}
