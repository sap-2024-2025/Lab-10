package thermostat.infra;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;

import io.vertx.core.json.JsonObject;
import thermostat.domain.HVACPhysicalTwinPort;
import thermostat.domain.HVACTwinningInterface;

public class HVACPhysicalTwinAdapter extends CartagoAdapter implements HVACPhysicalTwinPort {

	private HVACTwinningInterface artifact;

	private String thingURI;
	private HttpClient client;

	public HVACPhysicalTwinAdapter(String artifactName, String thingURI){
		artifact = (HVACTwinningInterface) discover(artifactName);
		this.thingURI = thingURI;
		client = HttpClient.newHttpClient();	
	}

	public void init() {
		artifact.connect(this);
		artifact.syncState("idle");
		log("ready.");
	}
	
	public void startHeating() throws Exception {
		this.doStartHeating();
		artifact.syncState("heating");
		// this.execInternalOp("heatingProc");
	}
	
	public void stopWorking() throws Exception {
		this.doStopWorking();
		artifact.syncState("idle");
	}
		

	public void startCooling() throws Exception {
		this.doStartCooling();
		artifact.syncState("cooling");
	}

	// 
	
	
	private void doStartHeating() throws Exception {
		JsonObject obj = new JsonObject();
		doPostBlocking(thingURI + "/actions/startHeating", Optional.of(obj));
	}
	
	private void doStartCooling() throws Exception {
		JsonObject obj = new JsonObject();
		doPostBlocking(thingURI + "/actions/startCooling", Optional.of(obj));
	}
	
	private void doStopWorking() throws Exception {
		JsonObject obj = new JsonObject();
		doPostBlocking(thingURI + "/actions/stopWorking", Optional.of(obj));
	}
	
	// aux actions

	private JsonObject doGetBlocking(String uri) throws Exception {
		try {
			var request = HttpRequest.newBuilder()
					.uri(URI.create(uri))
					.build();		

			var response = client.send(request,  BodyHandlers.ofString());
			return new JsonObject(response.body());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	private JsonObject doPostBlocking(String uri, Optional<JsonObject> body) throws Exception {
		HttpRequest req = null;
		// log("doing a post at " + "http://" + uri);
		if (!body.isEmpty()) {
			req  = HttpRequest.newBuilder()
					.uri(URI.create(uri))
					.POST(BodyPublishers.ofString(body.get().toString()))
					.build();		

		} else {
			req = HttpRequest.newBuilder()
					.uri(URI.create(uri))
					.POST(BodyPublishers.noBody())
					.build();		
		}

		var response = client.send(req, BodyHandlers.ofString());
		// log(" >> " + response.statusCode());
		return null; // new JsonObject(response.body());
	}
	
	/*
	private void doSubscribe() {
		Vertx vertx = Vertx.vertx();
		RoomDigitalTwinArtifact art = this;
		log("Subscribing...");
		vertx.createHttpClient().websocket(port, host, this.EVENTS, ws -> {	
			log("Connected!");
			ws.handler(msg -> {
				try {
					JsonObject ev = new JsonObject(msg.toString());		    	  
					String msgType = ev.getString("event");
					if (msgType.equals("propertyStatusChanged")) {
						JsonObject data = ev.getJsonObject("data");
						Double newTemperature = data.getDouble("temperature");
						String newState = data.getString("state");

						// log("updating artifact state with " + newTemperature + " " + newState);

						art.beginExtSession();							
						if (newTemperature != null) {
							ObsProperty tprop = getObsProperty("temperature");
							tprop.updateValue(newTemperature);
						}
						if (newState != null) {
							ObsProperty sprop = getObsProperty("state");
							sprop.updateValue(newState);
						}
						art.endExtSession();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});		
		});
		
	}*/		
	
	
	private void log(String msg) {
		System.out.println("[HVAC-PhysTwin-Adapter] " + msg);
	}
	

}
