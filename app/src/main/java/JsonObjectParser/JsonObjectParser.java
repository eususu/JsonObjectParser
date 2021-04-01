package JsonObjectParser;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;

public interface JsonObjectParser extends ReadStream<JsonObject> {
    public static JsonObjectParser newParser(ReadStream<Buffer> stream) {
        final JsonObjectParserImpl parser = new JsonObjectParserImpl(stream);
        return parser;
    }

}

