package com.opensync.marketplace.api;
import static org.assertj.core.api.Assertions.*;import org.junit.jupiter.api.Test;
class MarketplaceControllerTest{@Test void rejectsInvalidFailureRate(){assertThatThrownBy(()->new MarketplaceController.FailureMode(true,1.1,0)).isInstanceOf(IllegalArgumentException.class);}}
