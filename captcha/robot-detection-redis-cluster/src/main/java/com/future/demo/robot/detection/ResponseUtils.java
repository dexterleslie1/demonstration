package com.future.demo.robot.detection;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public class ResponseUtils {
    /**
     * 寫JSON 字符串到響應流
     * @param response
     * @param jsonString
     */
    public static void write(HttpServletResponse response,String jsonString){
        ServletOutputStream os=null;
        try {
            os=response.getOutputStream();
            os.write(jsonString.getBytes("utf-8"));
            os.flush();
            os.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(os!=null){
                try {
                    //os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
