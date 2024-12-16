package thermostat.infra;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;

import io.vertx.core.json.JsonObject;
import thermostat.domain.TempSensorTwinningInterface;

public class TempSensorPhysicalTwinAdapter extends CartagoAdapter {

	private TwinningProcess twinning;
	
	public TempSensorPhysicalTwinAdapter(String artifactName, String thingURI) {
		twinning = new TwinningProcess(artifactName, thingURI);
	}

	
	public void init() {
		twinning.start();
	}

	class TwinningProcess extends Thread {
		
		private String artifactName;
		private String remoteSensorURI;
		private HttpClient client;

		public TwinningProcess(String artifactName, String remoteSensorURI) {
			this.remoteSensorURI = remoteSensorURI;
			this.artifactName = artifactName;
			client = HttpClient.newHttpClient();	
		}
		
		public void run() {
			log("Discovering " + artifactName);
			TempSensorTwinningInterface art = (TempSensorTwinningInterface) discover(artifactName);
			log("Discovered. Connecting...");
			try {
				double currentTemp = getTemperature();
				art.connect(currentTemp);
				log("Connected.");
				while (true) {
					try {
						Thread.sleep(500);
						double value = getTemperature();
						if (value != currentTemp) {
							currentTemp = value;
							art.syncState(currentTemp);
						}
					} catch (Exception ex) {};
				}	
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
			
		//
		
		private double getTemperature() throws Exception {
			var jobj =  doGetBlocking(remoteSensorURI + "/properties/temperature");
			return jobj.getDouble("temperature");			
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
			// System.out.println("[TWIN-PROC] " + msg);
		}	
	}
}
