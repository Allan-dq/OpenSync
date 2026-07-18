package com.opensync.gateway.config;
import jakarta.servlet.*;import jakarta.servlet.http.*;import java.io.IOException;import java.nio.charset.StandardCharsets;import java.security.MessageDigest;import org.springframework.beans.factory.annotation.Value;import org.springframework.stereotype.Component;import org.springframework.web.filter.OncePerRequestFilter;
@Component
public class AdminApiKeyFilter extends OncePerRequestFilter{
 private final String key;public AdminApiKeyFilter(@Value("${opensync.security.admin-api-key:}") String key){this.key=key;}
 @Override protected boolean shouldNotFilter(HttpServletRequest r){return !r.getRequestURI().startsWith("/api/v1/admin/");}
 @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{var supplied=req.getHeader("X-Admin-API-Key");if(!key.isBlank()&&supplied!=null&&MessageDigest.isEqual(key.getBytes(StandardCharsets.UTF_8),supplied.getBytes(StandardCharsets.UTF_8))){chain.doFilter(req,res);return;}res.sendError(401,"Invalid admin API key");}
}
