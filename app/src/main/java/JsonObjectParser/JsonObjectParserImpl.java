package JsonObjectParser;

import java.util.Stack;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.parsetools.JsonParser;
import io.vertx.core.streams.ReadStream;

class JsonObjectParserImpl implements JsonObjectParser {
    JsonParser jsonParser;
    Object json;
    Object object;
    Stack<Object> stack;

    JsonObjectParserImpl(ReadStream<Buffer> stream) {
        jsonParser = JsonParser.newParser(stream);
        stack = new Stack<>();

        this.registerHandler();
    }

    private void registerHandler() {
        jsonParser.endHandler(whenEnd -> {
            if (this.json == null) {
                throw new IllegalStateException("set exception handler");
            }
        });

        /**
         * JsonParser가 알려주는대로 JsonObject를 조립.
         */
        jsonParser.handler(event -> {
            switch(event.type()) {
            case END_ARRAY:
                object = stack.pop();
                if (stack.empty())
                    this.json = object;

                break;
            case END_OBJECT:
                object = stack.pop();
                if (stack.empty())
                    this.json = object;

                break;
            case START_ARRAY:
                JsonArray arr = new JsonArray();
                if (this.object == null) {
                    this.object = arr;
                } else {
                    ((JsonObject) this.object).put(event.fieldName(), arr);
                    this.object = arr;
                }
                stack.push(this.object);
                break;
            case START_OBJECT:
                if (this.object == null) {
                    this.object = new JsonObject();
                } else {
                    JsonObject obj = new JsonObject();
                    if (this.object instanceof JsonArray) {
                        ((JsonArray) this.object).add(obj);
                    }
                    this.object = obj;
                }

                stack.push(this.object);
                break;
            case VALUE:
                if (object instanceof JsonObject) {
                    ((JsonObject) this.object).put(event.fieldName(), event.value());
                } else if (object instanceof JsonArray) {
                    ((JsonArray) this.object).add(event.value());
                }
                break;
            default:
                break;

            }

            /** 최종 json 객체가 존재하면 사전에 등록된 handler에게 JsonObject를 전달  */
            if (this.json != null) this.handler.handle((JsonObject)this.json);

        });

    }

    Handler<JsonObject> handler;

    public ReadStream<JsonObject> exceptionHandler(Handler<Throwable> handler) {
        return this;
    }

    public ReadStream<JsonObject> handler(Handler<JsonObject> handler) {
        this.handler = handler;
        return this;
    }

    public ReadStream<JsonObject> pause() {
        return this;
    }

    public ReadStream<JsonObject> resume() {
        return this;
    }

    public ReadStream<JsonObject> fetch(long amount) {
        return this;
    }

    public ReadStream<JsonObject> endHandler(Handler<Void> endHandler) {
        return this;
    }

}

