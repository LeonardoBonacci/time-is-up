package guru.bonacci.timesup.runmoved.health;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@ApplicationScoped  
public class Alive implements HealthCheck {

	@Inject KafkaHealthCheck health;

    @Override
    public HealthCheckResponse call() {
    	return health.check();
    }
}