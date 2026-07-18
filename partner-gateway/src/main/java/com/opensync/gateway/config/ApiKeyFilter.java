package com.opensync.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensync.contracts.ApiError;
import com.opensync.persistence.repository.PartnerRepository;
import jakarta.servlet.*; import jakarta.servlet.http.*;
import java.io.IOException; import java.time.Instant; import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

@Component @Order(1)
public class ApiKeyFilter extends OncePerRequestFilter {
 private final PartnerRepository partners;private final PasswordEncoder encoder;private final ObjectMapper json;
 public ApiKeyFilter(PartnerRepository p,PasswordEncoder e,ObjectMapper j){partners=p;encoder=e;json=j;}
 @Override protected boolean shouldNotFilter(HttpServletRequest r){return !r.getRequestURI().startsWith("/api/v1/partners/")||"GET".equals(r.getMethod());}
 @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
  var parts=req.getRequestURI().split("/");var partnerId=parts.length>4?parts[4]:"";var key=req.getHeader("X-API-Key");
  var valid=partners.findByPartnerIdAndActiveTrue(partnerId).filter(p->key!=null&&encoder.matches(key,p.getApiKeyHash())).isPresent();
  if(valid){chain.doFilter(req,res);return;}res.setStatus(401);res.setContentType(MediaType.APPLICATION_JSON_VALUE);json.writeValue(res.getWriter(),new ApiError(Instant.now(),req.getHeader("X-Correlation-Id"),"INVALID_API_KEY","Invalid partner API key",List.of()));
 }
}
