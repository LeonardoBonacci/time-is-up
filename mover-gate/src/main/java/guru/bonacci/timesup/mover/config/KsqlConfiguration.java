package guru.bonacci.timesup.mover.config;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.ws.rs.Produces;

import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;

@Dependent
public class KsqlConfiguration {

//	static final String KSQLDB_SERVER_HOST = "ksqldb-server";
	static final String KSQLDB_SERVER_HOST = "localhost";
	static final int KSQLDB_SERVER_HOST_PORT = 8088;

	@Default
	@Produces
	Client ksqlClient() {
		ClientOptions options = ClientOptions.create().setHost(KSQLDB_SERVER_HOST).setPort(KSQLDB_SERVER_HOST_PORT);
		return Client.create(options);
	}
}
