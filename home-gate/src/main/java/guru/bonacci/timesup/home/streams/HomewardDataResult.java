package guru.bonacci.timesup.home.streams;

import java.util.Optional;
import java.util.OptionalInt;


public class HomewardDataResult {

    private static HomewardDataResult NOT_FOUND = new HomewardDataResult(null, null, null);

    private final HomewardData result;
    private final String host;
    private final Integer port;

    private HomewardDataResult(HomewardData result, String host, Integer port) {
        this.result = result;
        this.host = host;
        this.port = port;
    }

    public static HomewardDataResult found(HomewardData data) {
        return new HomewardDataResult(data, null, null);
    }

    public static HomewardDataResult foundRemotely(String host, int port) {
        return new HomewardDataResult(null, host, port);
    }

    public static HomewardDataResult notFound() {
        return NOT_FOUND;
    }

    public Optional<HomewardData> getResult() {
        return Optional.ofNullable(result);
    }

    public Optional<String> getHost() {
        return Optional.ofNullable(host);
    }

    public OptionalInt getPort() {
        return port != null ? OptionalInt.of(port) : OptionalInt.empty();
    }
}