package com.future.demo.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1")
public class ControllerApi {
    @GetMapping(value = "redirect")
    public String redirect(@RequestParam(value = "type", defaultValue = "0") int type,
                           Model model){
        if(type!=1 && type!=2) {
            model.addAttribute("reason","支付类型值" + type + "错误 1、转账到银行卡 2、转账到支付宝账号");
            return "error";
        }

        String url = "";
        if(type==1) {
            // 银行开账号
            String cardNo = "";
            // 持卡人姓名
            String cardHolder = "";
            // 转账金额
            int amount = 0;
            // 银行代码
            String bankMark = "";
            // 银行名称
            String bankName = "";
            String cardIndex = "";
            url = "https://www.alipay.com/" +
                    "?appId=09999988&actionType=toCard&sourceId=bill&" +
                    "bankAccount=" + cardHolder + "&cardNo=" + cardNo + "&" +
                    "money=" + amount+ "&amount=" + amount + "&bankMark=" + bankMark + "&bankName=" + bankName + "&" +
                    "cardIndex=" + cardIndex + "&cardNoHidden=true&cardChannel=HISTORY_CARD&orderSource=from";
        } else if(type==2) {
            String memo = null;
            try {
                memo = URLEncoder.encode("中文备注1", "utf-8");
            } catch (Exception ex) {

            }
            String userId = "2088802052386424";
            double amount = 34.23;
            url = "https://www.alipay.com/?appId=09999988&actionType=toAccount&userId=" + userId + "&goBack=YES&amount=" + amount + "&memo=" + (memo==null?"":memo);
        }

        url = "redirect:" + url;
        return url;
    }
}
