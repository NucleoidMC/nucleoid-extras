package xyz.nucleoid.extras.integrations.http;

import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.util.Identifier;
import xyz.nucleoid.extras.NucleoidExtrasConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static java.time.temporal.ChronoUnit.SECONDS;

public class NucleoidHttpClient {
    private static final Codec<Map<String, Map<Identifier, Number>>> PLAYER_STATS_CODEC = Codec.unboundedMap(Codec.STRING, Codec.unboundedMap(Identifier.CODEC, (Codec<Number>) (Object) Codec.DOUBLE));
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private static URI baseUri;

    public static CompletableFuture<List<LeaderboardEntry>> getLeaderboard(Identifier id) {
        return sendSimple("/leaderboard/" + id.toString(), LeaderboardEntry.CODEC, List::of);
    }

    public static CompletableFuture<Map<Identifier, RankingsEntry>> getPlayerRankings(UUID uuid) {
        return sendSimple("/player/" + uuid.toString() + "/rankings", RankingsEntry.CODEC, Map::of);
    }

    public static CompletableFuture<Map<String, Map<Identifier, Number>>> getPlayerStats(UUID uuid) {
        return sendSimple("/stats/player/" + uuid.toString() + "/rankings", PLAYER_STATS_CODEC, Map::of);
    }

    private static <T> CompletableFuture<T> sendSimple(String path, Codec<T> codec, Supplier<T> fallback) {
        if (baseUri == null) {
            return CompletableFuture.supplyAsync(fallback);
        }

        var request = HttpRequest.newBuilder()
            .uri(baseUri.resolve(path))
            .GET()
            .timeout(Duration.of(10, SECONDS))
            .build();

        return send(request, codec, fallback);

    }

    private static <T> CompletableFuture<T> send(HttpRequest request, Codec<T> codec, Supplier<T> fallback) {
        return CLIENT.sendAsync(request, codecHandler(codec)).handle((response, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return fallback.get();
            } else if (response.statusCode() != 200) {
                return fallback.get();
            }
            return response.body();
        });
    }


    private static <T> HttpResponse.BodyHandler<T> codecHandler(Codec<T> codec) {
        return x -> HttpResponse.BodySubscribers.mapping(HttpResponse.BodySubscribers.ofString(charsetFrom(x.headers())), s -> codec.decode(JsonOps.INSTANCE, JsonParser.parseString(s)).result().get().getFirst());
    }

    public static Charset charsetFrom(HttpHeaders headers) {
        String type = headers.firstValue("Content-type")
            .orElse("text/html; charset=utf-8");
        int i = type.indexOf(";");
        if (i >= 0) type = type.substring(i + 1);
        try {
            i = type.toLowerCase(Locale.ROOT).indexOf("charset=");
            var i2 = type.toLowerCase(Locale.ROOT).indexOf(";");
            String value = null;
            if (i != -1) {
                if (i2 != -1) {
                    value = type.substring(i, i2);
                } else {
                    value = type.substring(i);
                }
            }

            if (value == null) return StandardCharsets.UTF_8;
            return Charset.forName(value);
        } catch (Throwable x) {
            return StandardCharsets.UTF_8;
        }
    }

    public static void register() {
        baseUri = NucleoidExtrasConfig.get().httpApi();
    }
}
