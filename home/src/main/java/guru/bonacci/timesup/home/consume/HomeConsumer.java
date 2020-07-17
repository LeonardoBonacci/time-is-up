package guru.bonacci.timesup.home.consume;

import java.util.concurrent.ExecutionException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import guru.bonacci.timesup.home.model.Trace;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.Row;
import io.smallrye.mutiny.Multi;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class HomeConsumer {

	@ConfigProperty(name = "ksql.query") 
	String query;

	@Inject
	Client client;
	
	
	public Multi<Trace> traces(final String unmovedId) throws InterruptedException, ExecutionException {
		log.debug("executing query %s", String.format(query, unmovedId));
		Multi<Row> rows = Multi.createFrom().publisher(client.streamQuery(String.format(query, unmovedId)).get());
		return rows.map((Row r) -> new Trace(r.getString("TRACKING_NUMBER"), r.getString("UNMOVED_ID"), r.getInteger("TOGO_MS")));
	}
}
