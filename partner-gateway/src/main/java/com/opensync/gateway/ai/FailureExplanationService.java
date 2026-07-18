package com.opensync.gateway.ai;
import com.opensync.contracts.*;import com.opensync.gateway.api.GatewayException;import com.opensync.persistence.repository.DeadLetterMessageRepository;import java.util.*;import org.springframework.stereotype.Service;
@Service
public class FailureExplanationService{
 private final DeadLetterMessageRepository dlq;private final LlmClient llm;public FailureExplanationService(DeadLetterMessageRepository d,LlmClient l){dlq=d;llm=l;}
 public FailureExplanation explain(UUID id){var m=dlq.findById(id).orElseThrow(()->new GatewayException(ErrorCode.INVALID_PAYLOAD,"Failure not found"));var sanitized=m.getPayload().replaceAll("(?i)\\\"(api[_-]?key|secret|token)\\\"\\s*:\\s*\\\"[^\\\"]*\\\"","\"$1\":\"[REDACTED]\"");return llm.explainFailure(new FailureContext(m.getErrorCode(),List.of(m.getErrorMessage()),sanitized,"MARKETPLACE_PUBLISH",List.of("attempts="+m.getRetryCount())));}
}
