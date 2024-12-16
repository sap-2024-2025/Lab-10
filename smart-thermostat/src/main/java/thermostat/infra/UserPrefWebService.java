package thermostat.infra;

import java.util.logging.Level;
import java.util.logging.Logger;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.*;
import io.vertx.ext.web.handler.StaticHandler;

public class UserPrefWebService extends AbstractVerticle {

	private int port;
    static Logger logger = Logger.getLogger("[ThermostatWebService]");	
    private UserPrefAdapter adapter;
    
	public UserPrefWebService(UserPrefAdapter adapter, int port) {
		this.port = port;
		this.adapter = adapter;
		logger.setLevel(Level.INFO);
	}

	public void launch() {
    	Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(this);		
	}
	
	public void start() {
		logger.log(Level.INFO, "Initializing User Pref ws...");
		HttpServer server = vertx.createHttpServer();
		Router router = Router.router(vertx);

		router.route("/static/*").handler(StaticHandler.create().setCachingEnabled(false));
		// router.route().handler(BodyHandler.create());		
		router.route(HttpMethod.GET, "/api/preferred-temperature").handler(this::getPrefTemperatureHandler);
		router.route(HttpMethod.POST, "/api/preferred-temperature/update").handler(this::setPrefTemperatureHandler);
				
		server
		.requestHandler(router)
		.listen(port);

		logger.log(Level.INFO, "User Pref web service ready - port: " + port);
	}

	protected void getPrefTemperatureHandler(RoutingContext context) {
		logger.log(Level.INFO, "New Get User Preferred Temperature  from " + context.currentRoute().getPath());
		JsonObject reply = new JsonObject();
		double res = adapter.getCurrentPreferredTemp();
		reply.put("userPrefTemperature", res);
		sendReply(context, reply);
	}
	
	protected void setPrefTemperatureHandler(RoutingContext context) {
		logger.log(Level.INFO, "New request - Set User Pref Temp from " + context.currentRoute().getPath());
		JsonObject reply = new JsonObject();
		context
		.request().body()
		.onSuccess(buf -> {
			JsonObject tempInfo = buf.toJsonObject();
			double newPrefTemp = tempInfo.getDouble("user-pref-temperature");
			adapter.notifyNewPreferredTemp(newPrefTemp);
			sendReply(context, reply);
		});	
	}

	
	private void sendReply(RoutingContext request, JsonObject reply) {
		HttpServerResponse response = request.response();
		response.putHeader("content-type", "application/json");
		response.end(reply.toString());
	}
	
}
