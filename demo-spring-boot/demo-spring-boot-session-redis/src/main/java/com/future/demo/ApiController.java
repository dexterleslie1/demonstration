package com.future.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
public class ApiController {
    @RequestMapping(value = "/post", method = RequestMethod.GET)
    public Map<String, Object> firstResp (HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();
        request.getSession().setAttribute("datum", request.getRequestURL());
        map.put("request Url", request.getRequestURL());
        return map;
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Object sessions (HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();
        map.put("sessionId", request.getSession().getId());
        map.put("datum", request.getSession().getAttribute("datum"));
        return map;
    }
}
