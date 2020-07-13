package guru.bonacci.timesup.home.rest;

import java.util.Optional;
import java.util.OptionalInt;

import guru.bonacci.timesup.home.streams.UnmovedData;


public class UnmovedDataResult {

    private static UnmovedDataResult NOT_FOUND = new UnmovedDataResult(null, null, null);

    private final UnmovedData result;
    private final String host;
    private final Integer port;

    private UnmovedDataResult(UnmovedData result, String host, Integer port) {
        this.result = result;
        this.host = host;
        this.port = port;
    }

    public static UnmovedDataResult found(UnmovedData data) {
        return new UnmovedDataResult(data, null, null);
    }

    public static UnmovedDataResult foundRemotely(String host, int port) {
        return new UnmovedDataResult(null, host, port);
    }

    public static UnmovedDataResult notFound() {
        return NOT_FOUND;
    }

    public Optional<UnmovedData> getResult() {
        return Optional.ofNullable(result);
    }

    public Optional<String> getHost() {
        return Optional.ofNullable(host);
    }

    public OptionalInt getPort() {
        return port != null ? OptionalInt.of(port) : OptionalInt.empty();
    }
}