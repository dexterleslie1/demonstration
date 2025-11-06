package com.future.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
@Slf4j
public class DemoController {

    @Value("${oauth2.client_id}")
    String clientId;
    @Value("${oauth2.client_secret}")
    String clientSecret;
    @Value("${oauth2.redirect_uri}")
    String redirectUri;
    @Value("${oauth2.scope}")
    String scope;

    @GetMapping("/")
    public String index(Model model) {
        setOAuth2Config(model);
        return "index";
    }

    /**
     * Github成功授权后回调的url
     *
     * @return
     */
    @GetMapping("/github-callback")
    public String githubCallback() {
        return "github-callback";
    }

    /**
     * 用户登录成功后，会跳转到welcome页面
     *
     * @param authentication
     * @param model
     * @return
     */
    @GetMapping("/welcome")
    public String welcome(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "welcome";
    }

    @Autowired
    ObjectMapper objectMapper;

    /**
     * 使用OAuth2.0授权码获取Github令牌和用户信息并集成本系统登录流程
     *
     * @param code
     * @return
     */
    @PostMapping("loginWithAuthorizationCode")
    public ResponseEntity<ObjectNode> loginWithAuthorizationCode(@RequestParam("code") String code) {
        try {
            JsonNode userInfo = getGithubUserInfo(code);

            // 基于会话登录流程集成，也可以修改为不基于会话登录流程使用无状态令牌登录流程
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userInfo.get("login").asText(),
                            null,
                            AuthorityUtils.createAuthorityList("ROLE_USER")
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            ObjectNode response = objectMapper.createObjectNode();
            response.put("errorCode", 0);
            response.put("errorMessage", "");
            response.put("data", "登录成功");
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            ObjectNode response = objectMapper.createObjectNode();
            response.put("errorCode", 90000);
            response.put("errorMessage", "登录失败，原因：" + ex.getMessage());
            response.put("data", "");
            return ResponseEntity.ok(response);
        }
    }

    RestTemplate restTemplate = new RestTemplate();

    /**
     * 使用授权码获取Github用户信息
     *
     * @param authorizationCode
     * @return
     * @throws Exception
     */
    JsonNode getGithubUserInfo(String authorizationCode) throws Exception {
        // 使用授权码，向 GitHub 请求令牌
        String url = String.format("https://github.com/login/oauth/access_token?client_id=%s&client_secret=%s&code=%s",
                clientId, clientSecret, authorizationCode);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(new LinkedMultiValueMap<>(), headers);
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, JsonNode.class);
        JsonNode jsonNodeResponse = responseEntity.getBody();
        String error = jsonNodeResponse.get("error") == null ? null : jsonNodeResponse.get("error").asText();
        // 请求令牌失败
        // 样例：{"error":"bad_verification_code","error_description":"The code passed is incorrect or expired.","error_uri":"https://docs.github.com/apps/managing-oauth-apps/troubleshooting-oauth-app-access-token-request-errors/#bad-verification-code"}
        if (StringUtils.hasText(error)) {
            log.error("请求令牌失败，原因：{}", jsonNodeResponse);
            String errorDescription = jsonNodeResponse.get("error_description").asText();
            throw new Exception(errorDescription);
        }

        // 获取令牌成功
        // 样例：{"access_token":"xxx","token_type":"bearer","scope":"read:user,user:email"}
        String accessToken = jsonNodeResponse.get("access_token").asText();

        // 使用Access Token调用获取用户信息接口
        url = "https://api.github.com/user";
        headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        httpEntity = new HttpEntity<>(new LinkedMultiValueMap<>(), headers);
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, JsonNode.class);
            // 获取用户信息成功
            // {"login":"dexterleslie1","id":26960597,"node_id":"MDQ6VXNlcjI2OTYwNTk3","avatar_url":"https://avatars.githubusercontent.com/u/26960597?v=4","gravatar_id":"","url":"https://api.github.com/users/dexterleslie1","html_url":"https://github.com/dexterleslie1","followers_url":"https://api.github.com/users/dexterleslie1/followers","following_url":"https://api.github.com/users/dexterleslie1/following{/other_user}","gists_url":"https://api.github.com/users/dexterleslie1/gists{/gist_id}","starred_url":"https://api.github.com/users/dexterleslie1/starred{/owner}{/repo}","subscriptions_url":"https://api.github.com/users/dexterleslie1/subscriptions","organizations_url":"https://api.github.com/users/dexterleslie1/orgs","repos_url":"https://api.github.com/users/dexterleslie1/repos","events_url":"https://api.github.com/users/dexterleslie1/events{/privacy}","received_events_url":"https://api.github.com/users/dexterleslie1/received_events","type":"User","user_view_type":"private","site_admin":false,"name":"Dexterleslie.Chan","company":null,"blog":"","location":null,"email":"dexterleslie@gmail.com","hireable":null,"bio":null,"twitter_username":null,"notification_email":"dexterleslie@gmail.com","public_repos":18,"public_gists":0,"followers":0,"following":0,"created_at":"2017-04-06T09:32:43Z","updated_at":"2025-09-02T16:15:24Z","private_gists":0,"total_private_repos":24,"owned_private_repos":24,"disk_usage":155609,"collaborators":0,"two_factor_authentication":true,"plan":{"name":"pro","space":976562499,"collaborators":0,"private_repos":9999}}
            jsonNodeResponse = responseEntity.getBody();
            return jsonNodeResponse;
        } catch (HttpClientErrorException ex) {
            // 获取用户信息失败
            /*
            样例：
                org.springframework.web.client.HttpClientErrorException$Unauthorized: 401 Unauthorized: [{
                    "message": "Bad credentials",
                            "documentation_url": "https://docs.github.com/rest",
                            "status": "401"
                }]
            */
            log.error("获取用户信息失败，原因：{}", ex.getMessage());
            String errorDescription = ex.getMessage();
            throw new Exception(errorDescription);
        }
    }

    void setOAuth2Config(Model model) {
        model.addAttribute("client_id", clientId);
        model.addAttribute("redirect_uri", redirectUri);
        model.addAttribute("scope", scope);
    }
}
