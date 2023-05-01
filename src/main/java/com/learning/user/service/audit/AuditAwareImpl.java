package com.learning.user.service.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class AuditAwareImpl implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Optional<HttpServletRequest> optionalRequest = getCurrentHttpRequest();
        if(optionalRequest.isPresent()){
            HttpServletRequest request = optionalRequest.get();
            String userId = request.getHeader("userId");
            if (userId != null)
                return Optional.of(Long.valueOf(userId));
        }
        return Optional.of(0L);
    }

    private Optional<HttpServletRequest> getCurrentHttpRequest(){
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest);
    }
}
