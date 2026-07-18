package com.opensync.publisher.service;
import static org.assertj.core.api.Assertions.assertThat;import com.opensync.contracts.EventEnvelope;import org.junit.jupiter.api.Test;import org.springframework.retry.annotation.Retryable;
class MarketplacePublicationPolicyTest{
 @Test void usesOneTwoFourSecondBackoffAcrossFourAttempts()throws Exception{var method=MarketplacePublicationService.class.getMethod("publish",EventEnvelope.class,String.class);var retry=method.getAnnotation(Retryable.class);assertThat(retry.maxAttempts()).isEqualTo(4);assertThat(retry.backoff().delay()).isEqualTo(1000);assertThat(retry.backoff().multiplier()).isEqualTo(2.0);assertThat(retry.retryFor()).contains(MarketplaceUnavailableException.class);}
}
