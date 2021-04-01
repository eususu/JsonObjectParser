package JsonObjectParser;



import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ClientAuth;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.JsonParser;
import io.vertx.core.streams.ReadStream;

public class TCPServerVerticle extends AbstractVerticle {
	int servicePort;

    @Override
    public void start(Promise<Void> promise) {

        this.servicePort = 2000;

        // NetServer configurations
        NetServerOptions options = new NetServerOptions();

        // create NetServer and register handlers
        NetServer server =vertx.createNetServer(options);
        server.connectHandler(this::onConnect);
        server.exceptionHandler(this::onException);
        server.listen(servicePort, this::onListen);

        // finish promise
        promise.complete();
    }

    protected void onException(Throwable t) {
			t.printStackTrace();
		}

    protected void onConnect(NetSocket socket) {
        socket.exceptionHandler(this::onException);

				// attach my parser
        JsonObjectParser parser = JsonObjectParser.newParser(socket);
        parser.handler(json -> 
						// now handle json data
            onRequest(socket, json)
        );
    }
    protected void onRequest(NetSocket socket, JsonObject request) {
			System.out.println("-> " + request);

			JsonObject response = new JsonObject();
			response.put("result", "ok");
			response.put("requestLength", request.toString().length());

			Buffer responseBuffer = response.toBuffer();
			System.out.println("<- " + responseBuffer.toString());
			socket.write(responseBuffer);
			socket.close();
		}
    protected void onListen(AsyncResult<NetServer> result) {

			System.out.println("start verticle with servicePort=" + servicePort);
		}
}

