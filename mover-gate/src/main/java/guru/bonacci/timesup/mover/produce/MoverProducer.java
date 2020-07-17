package guru.bonacci.timesup.mover.produce;

import java.util.concurrent.CompletableFuture;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.jboss.logging.Logger;

import guru.bonacci.timesup.mover.model.Mover;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import io.confluent.ksql.api.client.KsqlObject;

@ApplicationScoped
public class MoverProducer {

	private final Logger log = Logger.getLogger(MoverProducer.class);
	
//	static final String KSQLDB_SERVER_HOST = "ksqldb-server";
	static final String KSQLDB_SERVER_HOST = "localhost";
	static final int KSQLDB_SERVER_HOST_PORT = 8088;

	@Inject
	Validator validator;
	
	Client client;
	
	MoverProducer() {
		ClientOptions options = ClientOptions.create().setHost(KSQLDB_SERVER_HOST).setPort(KSQLDB_SERVER_HOST_PORT);
		client = Client.create(options);
	}

	public CompletableFuture<Void> send(final Mover mover) {
		if (mover == null || !validator.validate(mover).isEmpty()) {
			log.warn("Suspicious incoming request");
			return CompletableFuture.failedFuture(
					new ValidationException("mid 16th century (earlier than valid ): from Latin invalidus, from in- 'not' + validus 'strong' "));
		}
		
		log.infof("Producing record: %s", mover);
		KsqlObject row = new KsqlObject().put("ID", mover.id).put("LAT", Double.valueOf(mover.lat)).put("LON", Double.valueOf(mover.lon));
		return client.insertInto("MOVER", row);
	}
}
