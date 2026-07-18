package com.opensync.gateway.api;
import com.opensync.gateway.service.DeadLetterReplayService;import com.opensync.gateway.ai.FailureExplanationService;import java.util.*;import org.springframework.http.*;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/admin")
public class AdminController{
 private final DeadLetterReplayService replay;private final FailureExplanationService explanations;public AdminController(DeadLetterReplayService r,FailureExplanationService e){replay=r;explanations=e;}
 @PostMapping("/dead-letter/{messageId}/replay") ResponseEntity<Map<String,String>> replay(@PathVariable UUID messageId){replay.replay(messageId);return ResponseEntity.accepted().body(Map.of("messageId",messageId.toString(),"status","REPLAYING"));}
 @PostMapping("/failures/{messageId}/explain") Object explain(@PathVariable UUID messageId){return explanations.explain(messageId);}
}
