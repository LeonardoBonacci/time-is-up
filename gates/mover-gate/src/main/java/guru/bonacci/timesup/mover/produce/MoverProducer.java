package guru.bonacci.timesup.mover.produce;

import java.util.concurrent.CompletableFuture;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import guru.bonacci.timesup.mover.model.Mover;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.KsqlObject;

@ApplicationScoped
public class MoverProducer {

	private final Logger log = Logger.getLogger(MoverProducer.class);

	@ConfigProperty(name = "ksql.stream.name", defaultValue = "MOVER") 
	String ksqlStream;
	
	@Inject
	Validator validator;

	@Inject
	Client client;

	
	public CompletableFuture<Void> send(final Mover mover) {
		if (mover == null || !validator.validate(mover).isEmpty()) {
			log.warn("Suspicious incoming request");
			return CompletableFuture.failedFuture(
				new ValidationException("mid 16th century (earlier than valid ): from Latin invalidus, from in- 'not' + validus 'strong' "));
		}

		log.infof("Producing record: %s", mover);
		var row = new KsqlObject().put("ID", mover.ID).put("LAT", Double.valueOf(mover.LAT)).put("LON", Double.valueOf(mover.LON));
		return client.insertInto(ksqlStream, row);
	}
}
