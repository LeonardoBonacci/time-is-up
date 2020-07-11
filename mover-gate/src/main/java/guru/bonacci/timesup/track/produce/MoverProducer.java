package guru.bonacci.timesup.track.produce;

import java.util.concurrent.ExecutionException;

import javax.enterprise.context.ApplicationScoped;

import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import io.confluent.ksql.api.client.KsqlObject;

@ApplicationScoped
public class MoverProducer {

	static final String KSQLDB_SERVER_HOST = "ksqldb-server";
	static final int KSQLDB_SERVER_HOST_PORT = 8088;

	Client client;
	
	MoverProducer() {
		ClientOptions options = ClientOptions.create().setHost(KSQLDB_SERVER_HOST).setPort(KSQLDB_SERVER_HOST_PORT);
		client = Client.create(options);
	}

	public void ping() throws InterruptedException, ExecutionException {
		KsqlObject row = new KsqlObject().put("ID", "moverid").put("LAT", 0.90).put("LON", 0.90);
		client.insertInto("MOVER", row).get();
	}
}
