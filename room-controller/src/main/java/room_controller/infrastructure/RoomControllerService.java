package room_controller.infrastructure;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.*;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import room_controller.domain.RoomControllerInterface;
import room_controller.domain.RoomListener;

public class RoomControllerService extends AbstractVerticle implements RoomListener {

	private int port;
	private RoomControllerInterface domainLayer;
    static Logger logger = Logger.getLogger("[RoomControllerService]");	
    
    private static final String PROPERTY_TEMPERATURE = "/properties/temperature";
	private static final String PROPERTY_STATE = "/properties/state";
	private static final String ACTION_STARTHEATING = "/actions/startHeating";
	private static final String ACTION_STARTCOOLING = "/actions/startCooling";
	private static final String ACTION_STOPWORKING = "/actions/stopWorking";
	private static final String ACTION_SETTEMPERATURE = "/actions/setTemperature";
	private static final String EVENTS = "/events";
	
	private static final String EVENT_TOPIC = "events";
	
	    
	public RoomControllerService(RoomControllerInterface domainLayer) {
		this.port = 8080;
		this.domainLayer = domainLayer;
		logger.setLevel(Level.INFO);
	}

	public void launch() {
    	Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(this);		
	}
	
	public void start() {
		logger.log(Level.INFO, "RoomControllerService initializing...");
		HttpServer server = vertx.createHttpServer();
		Router router = Router.router(vertx);

		/* static files by default searched in "webroot" directory */
		router.route("/static/*").handler(StaticHandler.create().setCachingEnabled(false));
		router.route(HttpMethod.GET, PROPERTY_TEMPERATURE).handler(this::processGetTemperatureProperty);
		router.route(HttpMethod.POST, ACTION_SETTEMPERATURE).handler(this::processSetTemperatureAction);
		router.route(HttpMethod.GET, PROPERTY_STATE).handler(this::processGetHVACStateProperty);
		router.route(HttpMethod.POST, ACTION_STARTHEATING).handler(this::processStartHeatingAction);
		router.route(HttpMethod.POST, ACTION_STARTCOOLING).handler(this::processStartCoolingAction);
		router.route(HttpMethod.POST, ACTION_STOPWORKING).handler(this::processStopWorkingAction);
		
		server.webSocketHandler(webSocket -> {
			logger.log(Level.INFO, "New connection requested: " + webSocket.path());
			  if (webSocket.path().equals(EVENTS)) {
				webSocket.accept();
				logger.log(Level.INFO, "New observer registered.");
		    	EventBus eb = vertx.eventBus();
		    	eb.consumer(EVENT_TOPIC, msg -> {
		    		JsonObject ev = (JsonObject) msg.body();
			    	logger.log(Level.INFO, "New event to dispatch: " + ev.encodePrettily());
		    		webSocket.writeTextMessage(ev.encodePrettily());
		    	});
			  } else {
				  logger.log(Level.INFO, "Observer rejected.");
				  webSocket.reject();
			  }
			});		
		
		server
		.requestHandler(router)
		.listen(port);

		logger.log(Level.INFO, "RoomControllerService ready - port: " + port);
	}

	protected void processGetTemperatureProperty(RoutingContext context) {
		logger.log(Level.INFO, "New GetTemperature Property " + context.currentRoute().getPath());
		JsonObject reply = new JsonObject();
		double res = domainLayer.getTemperature();
		reply.put("temperature", res);
		sendReply(context, reply);
	}
	
	protected void processGetHVACStateProperty(RoutingContext context) {
		logger.log(Level.INFO, "New Get HVAC State Property" + context.currentRoute().getPath());
		JsonObject reply = new JsonObject();
		String state = domainLayer.getHVACState();
		reply.put("state", state);
		sendReply(context, reply);
	}	

	
	protected void processStartHeatingAction(RoutingContext context) {
		logger.log(Level.INFO, "New request - Start Heating Action " + context.currentRoute().getPath());
		JsonObject reply = new JsonObject();
		domainLayer.startHeating();
		reply.put("result", "ok");
		sendReply(context, reply);
	}
	
	protected void processStartCoolingAction(RoutingContext context) {
		logger.log(Level.INFO, "New request - Start Cooling Action " + context.currentRoute().getPath());
		JsonObject reply = new JsonObject();
		domainLayer.startCooling();
		reply.put("result", "ok");
		sendReply(context, reply);
	}

	protected void processStopWorkingAction(RoutingContext context) {
		logger.log(Level.INFO, "New request - Stop Working Action " + context.currentRoute().getPath());
		JsonObject reply = new JsonObject();
		domainLayer.stopWorking();
		reply.put("result", "ok");
		sendReply(context, reply);
	}

	protected void processSetTemperatureAction(RoutingContext context) {
		logger.log(Level.INFO, "New request - Set temperature Action " + context.currentRoute().getPath());
		JsonObject reply = new JsonObject();
		var f = context.request().body();
		f.onSuccess(buf -> {
			JsonObject tempInfo = buf.toJsonObject();
			double v = tempInfo.getDouble("value");
			domainLayer.setTemperature(v);
			sendReply(context, reply);
		});	
	}

	
	public void notifyTemperatureChanged(double newTemp) {
		EventBus eb = vertx.eventBus();
		JsonObject obj = new JsonObject();
		obj.put("event", "temperature-changed");
		obj.put("value", newTemp);		
    	eb.publish(EVENT_TOPIC, obj);
	}

	public void notifyHVACStateChanged(String newState) {
		EventBus eb = vertx.eventBus();
		JsonObject obj = new JsonObject();
		obj.put("event", "hvac-state-changed");
		obj.put("value", newState);		
    	eb.publish(EVENT_TOPIC, obj);
	}
	
	private void sendReply(RoutingContext request, JsonObject reply) {
		HttpServerResponse response = request.response();
		response.putHeader("content-type", "application/json");
		response.end(reply.toString());
	}
	
}
