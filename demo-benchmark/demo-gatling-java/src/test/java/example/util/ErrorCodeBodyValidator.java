package example.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gatling.javaapi.core.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于 Gatling bodyString().validate 的公共校验工具：解析 JSON 响应体，校验 errorCode。
 * errorCode &lt;= 0 视为成功，&gt; 0 视为失败并抛出 RuntimeException（含 errorMessage）。
 */
public final class ErrorCodeBodyValidator {

    private static final Logger logger = LoggerFactory.getLogger(ErrorCodeBodyValidator.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ErrorCodeBodyValidator() {
    }

    /**
     * 校验登录（或通用）响应：要求 JSON 含 errorCode，且 errorCode &lt;= 0 为成功。
     * 用于 .check(bodyString().validate("是否成功登录", ErrorCodeBodyValidator::validateErrorCode))
     *
     * @param body    响应体字符串
     * @param session 当前虚拟用户 Session（可选使用）
     * @return 校验通过时返回 body
     * @throws RuntimeException 响应体为空、非合法 JSON、缺少 errorCode、或 errorCode &gt; 0
     */
    public static String validateErrorCode(String body, Session session) {
        if (body == null || body.isBlank()) {
            throw new RuntimeException("响应体为空");
        }
        try {
            JsonNode root = OBJECT_MAPPER.readTree(body);
            if (!root.has("errorCode")) {
                throw new RuntimeException("响应中缺少 errorCode 字段");
            }
            int errorCode = root.get("errorCode").asInt();
            if (errorCode > 0) {
                String errorMessage = root.has("errorMessage")
                        ? root.get("errorMessage").asText()
                        : "";
                throw new RuntimeException("请求失败，errorCode=" + errorCode + ", errorMessage=" + errorMessage);
            }
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            logger.error(e.getMessage(), e);
            throw new RuntimeException("解析响应失败，响应为" + body, e);
        }
        return body;
    }
}
