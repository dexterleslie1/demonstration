package com.future.demo.security.uaa;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@SessionAttributes("authorizationRequest")
public class DemoController {

    @Autowired
    ClientDetailsService clientDetailsService;

    @GetMapping(value = "/login")
    public String login() {
        return "login";
    }

    @GetMapping("/custom/confirm")
    public String confirmAccess(Model model,
                                @ModelAttribute("authorizationRequest") AuthorizationRequest authorizationRequest,
                                Principal principal) {
        // 获取客户端信息
        ClientDetails client = clientDetailsService.loadClientByClientId(authorizationRequest.getClientId());

        model.addAttribute("client", client);
        model.addAttribute("scopes", getScopeDescriptions(authorizationRequest.getScope()));
        model.addAttribute("principal", principal);

        // 自定义授权确认页面
        return "confirm";
    }

    private List<ScopeDescription> getScopeDescriptions(Set<String> scopes) {
        Map<String, String> scopeDescriptions = new HashMap<>();
        scopeDescriptions.put("read", "读取您的个人信息");
        scopeDescriptions.put("write", "修改您的个人信息");
        scopeDescriptions.put("api", "访问API接口");
        scopeDescriptions.put("all", "权限标识all");

        return scopes.stream()
                .map(scope -> new ScopeDescription(scope, scopeDescriptions.getOrDefault(scope, "未知权限")))
                .collect(Collectors.toList());
    }

    @Data
    @AllArgsConstructor
    public static class ScopeDescription {
        private String scope;
        private String description;
    }
}
