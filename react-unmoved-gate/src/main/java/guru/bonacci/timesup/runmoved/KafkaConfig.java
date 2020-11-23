package guru.bonacci.timesup.runmoved;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.google.common.collect.ImmutableMap;

@Dependent
public class KafkaConfig {

	@Produces
	public AdminClient kafkaAdminClient(@ConfigProperty(name = "kafka.bootstrap.servers") String bootstrapServers) {
		var configs =  new NullSafeMapBuilder<String, Object>()
			.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
            .build();

		return AdminClient.create(configs);
	}

	class NullSafeMapBuilder<K, V> extends ImmutableMap.Builder<K, V> {
		public NullSafeMapBuilder<K, V> putIfValueNotNull(K key, V value) {
			if (value != null) super.put(key, value);
			return this;
		}
	}
}
