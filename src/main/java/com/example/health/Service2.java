package com.example.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.HealthIndicator;

/**
 * This is a contrived sub-component which our main component uses to determine
 * if the component is available or not
 * 
 * @author JIm Marshall
 *
 */
public class Service2 implements HealthIndicator {

	private Boolean isUp;

	public Service2() {
		isUp = Boolean.TRUE;
	}

	@Override
	public Health health() {
		Builder state = isUp ? Health.up() : Health.down();
		return state.withDetail("Detail2", "World").build();
	}

	// Someone can call this to set it as up or down, see MyHelloWorldController
	public void setAsUp(Boolean isUp) {
		this.isUp = isUp;
	}

}
