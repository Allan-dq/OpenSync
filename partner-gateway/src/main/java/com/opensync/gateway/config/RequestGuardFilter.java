package com.opensync.gateway.config;
import jakarta.servlet.*;import jakarta.servlet.http.*;import java.io.IOException;import java.time.*;import java.util.concurrent.TimeUnit;import org.springframework.beans.factory.annotation.Value;import org.springframework.core.annotation.Order;import org.springframework.data.redis.core.StringRedisTemplate;import org.springframework.stereotype.Component;import org.springframework.web.filter.OncePerRequestFilter;
@Component @Order(2)
public class RequestGuardFilter extends OncePerRequestFilter{
 private final StringRedisTemplate redis;private final long limit;private final long maxBytes;
 public RequestGuardFilter(StringRedisTemplate r,@Value("${opensync.security.requests-per-minute:1200}") long limit,@Value("${opensync.security.max-payload-bytes:1048576}") long maxBytes){redis=r;this.limit=limit;this.maxBytes=maxBytes;}
 @Override protected boolean shouldNotFilter(HttpServletRequest r){return !r.getRequestURI().startsWith("/api/v1/partners/")||!"POST".equals(r.getMethod());}
 @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
  if(req.getContentLengthLong()>maxBytes){res.sendError(413,"Payload exceeds configured limit");return;}var parts=req.getRequestURI().split("/");var partner=parts.length>4?parts[4]:"unknown";
  try{var bucket=Instant.now().getEpochSecond()/60;var key="rate-limit:"+partner+":"+bucket;var count=redis.opsForValue().increment(key);if(count!=null&&count==1)redis.expire(key,2,TimeUnit.MINUTES);if(count!=null&&count>limit){res.setStatus(429);res.setHeader("Retry-After","60");return;}}catch(Exception ignored){/* PostgreSQL remains authoritative; Redis outages do not stop ingestion. */}
  chain.doFilter(req,res);
 }
}
