package example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gatling.javaapi.core.CheckBuilder;

import static io.gatling.javaapi.core.CoreDsl.bodyString;

public class ResponseUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 解析响应
     *
     * @param checkBizException 是否检测业务异常，如果是并有业务异常时抛出RuntimeException
     */
    public static CheckBuilder.Validate<Object> parseResponse(
            TypeReference typeReference, boolean checkBizException) {
        return bodyString().transform(body -> {
            try {
                BaseResponse response = (BaseResponse) OBJECT_MAPPER.readValue(
                        body,
                        typeReference);
                int errorCode = response.getErrorCode();
                String errorMessage = response.getErrorMessage();
                if (checkBizException && errorCode > 0) {
                    errorMessage = String.format("响应错误，errorCode=%d,errorMessage=%s", errorCode, errorMessage);
                    throw new RuntimeException(errorMessage);
                }
                return response;
            } catch (Exception e) {
                if (!(e instanceof RuntimeException)) {
                    throw new RuntimeException(e);
                }
                throw (RuntimeException) e;
            }
        });
    }
}

