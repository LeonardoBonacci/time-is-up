package guru.bonacci.timesup.home.config;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;

@Dependent
public class KsqlConfiguration {

	@Default
	@Produces
	Client ksqlClient(@ConfigProperty(name = "ksql.host") String host, 
					  @ConfigProperty(name = "ksql.port", defaultValue = "8088") int port) {

		var options = ClientOptions.create().setHost(host).setPort(port);
		return Client.create(options);
	}
}
