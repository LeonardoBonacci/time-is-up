package guru.bonacci.timesup.runmoved.health;

import java.util.concurrent.ExecutionException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped  
public class KafkaHealthCheck {

	@Inject AdminClient kafka;

	static final int ADMIN_CLIENT_TIMEOUT_MS = 5000;           
	 
    public HealthCheckResponse check() {
    	 HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("Kafka connection health check");
    	 
    	try {
    		kafka.listTopics(new ListTopicsOptions().timeoutMs(ADMIN_CLIENT_TIMEOUT_MS)).listings().get();
    		responseBuilder.up();
    	} catch (ExecutionException | InterruptedException ex) {
    		log.error(ex.getMessage());
            responseBuilder.down();
    	}    
    	
    	return responseBuilder.build();
    }
}