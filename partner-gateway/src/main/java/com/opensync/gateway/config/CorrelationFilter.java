package com.opensync.gateway.config;
import jakarta.servlet.*;import jakarta.servlet.http.*;import java.io.IOException;import java.util.UUID;import org.slf4j.MDC;import org.springframework.core.Ordered;import org.springframework.core.annotation.Order;import org.springframework.stereotype.Component;import org.springframework.web.filter.OncePerRequestFilter;
@Component @Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationFilter extends OncePerRequestFilter{
 @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{var correlation=req.getHeader("X-Correlation-Id");if(correlation==null||correlation.isBlank())correlation="corr-"+UUID.randomUUID();MDC.put("correlationId",correlation);res.setHeader("X-Correlation-Id",correlation);try{chain.doFilter(req,res);}finally{MDC.clear();}}
}
