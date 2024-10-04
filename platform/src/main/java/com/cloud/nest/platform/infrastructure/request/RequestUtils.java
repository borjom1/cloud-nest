package com.cloud.nest.platform.infrastructure.request;

import com.cloud.nest.platform.model.request.ClientRequestDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@UtilityClass
public class RequestUtils {

    public static final String X_FORWARDED_FOR = "X-Forwarded-For";

    public static String getClientIpAddress(@NotNull HttpServletRequest request) {
        final String ipAddress = request.getHeader(X_FORWARDED_FOR);
        return ipAddress != null ? ipAddress : request.getRemoteAddr();
    }

    public static String getUserAgent(@NotNull HttpServletRequest request) {
        return request.getHeader(HttpHeaders.USER_AGENT);
    }

    @NotNull
    public static ClientRequestDetails getRequestClientDetails() {
        final HttpServletRequest request = getServletRequestAttributes().getRequest();
        return new ClientRequestDetails(
                getClientIpAddress(request),
                getUserAgent(request)
        );
    }

    @NotNull
    private static ServletRequestAttributes getServletRequestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    }

}
