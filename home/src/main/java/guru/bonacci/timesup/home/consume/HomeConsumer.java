package guru.bonacci.timesup.home.consume;

import java.util.concurrent.ExecutionException;

import javax.enterprise.context.ApplicationScoped;

import guru.bonacci.timesup.home.model.Trace;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import io.confluent.ksql.api.client.Row;
import io.smallrye.mutiny.Multi;

@ApplicationScoped
public class HomeConsumer {

//	static final String KSQLDB_SERVER_HOST = "ksqldb-server";
	static final String KSQLDB_SERVER_HOST = "localhost";
	static final int KSQLDB_SERVER_HOST_PORT = 8088;

	private final String QUERY = "select TRACKING_NUMBER, UNMOVED_ID, TOGO_MS from HOMEWARD where unmoved_id = '%s' emit changes;";

	Client client;
	
	HomeConsumer() {
		ClientOptions options = ClientOptions.create().setHost(KSQLDB_SERVER_HOST).setPort(KSQLDB_SERVER_HOST_PORT);
		client = Client.create(options);
	}

	
	public Multi<Trace> traces(final String unmovedId) throws InterruptedException, ExecutionException {
		Multi<Row> rows = Multi.createFrom().publisher(client.streamQuery(String.format(QUERY, unmovedId)).get());
		return rows.map((Row r) -> new Trace(r.getString("TRACKING_NUMBER"), r.getString("UNMOVED_ID"), r.getInteger("TOGO_MS")));
	}
}
