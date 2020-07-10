package guru.bonacci.timesup.track.producers;

import java.util.concurrent.ExecutionException;

import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import io.confluent.ksql.api.client.KsqlObject;


public class SampleMoverProducer {

	public static String KSQLDB_SERVER_HOST = "127.0.0.1";
	public static int KSQLDB_SERVER_HOST_PORT = 8088;
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ClientOptions options = ClientOptions.create().setHost(KSQLDB_SERVER_HOST).setPort(KSQLDB_SERVER_HOST_PORT);
		Client client = Client.create(options);

		KsqlObject row = new KsqlObject().put("ID", "moverid").put("LAT", 0.90).put("LON", 0.90);

		client.insertInto("MOVER", row).get();

		client.close();
	}
}