package guru.bonacci.timesup.rpurge;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import guru.bonacci.timesup.rpurge.rest.TrackEndpoint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class TrackPurger {

	@Inject @RestClient TrackEndpoint client;

	LoadingCache<String, String> cache;

	public TrackPurger() {
		CacheLoader<String, String> loader;
		loader = new CacheLoader<String, String>() {
			@Override
			public String load(String key) {
				return "unused";
			}
		};
		cache = CacheBuilder.newBuilder().maximumSize(1000).build(loader);
	}	

	@Incoming("arrival-stream")
	public void stream(JsonNode arrival) { 
		final var trackingNumber = arrival.get("tracking_number").textValue();
		log.debug("pickup '{}'", arrival);

		if (cache.getIfPresent(trackingNumber) == null) {
			log.info("gotcha - track '{}'", trackingNumber);
			
			client.tombstone(trackingNumber);
			cache.put(trackingNumber, "unused");
		}
	}
}