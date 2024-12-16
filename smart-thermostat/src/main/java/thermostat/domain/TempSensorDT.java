package thermostat.domain;

import cartago.*;

public class TempSensorDT extends Artifact implements TempSensorTwinningInterface {

	
	public void init() {
		defineObsProperty("state","unconnected");	
		log("Init - State: unconnected.");
	}
	
	public void connect(double temp) {
		this.beginExtSession();
		try {
			defineObsProperty("temperature",temp);		
			this.getObsProperty("state").updateValue("connected");
			log("connected.");
			this.endExtSession();
		} catch (Exception ex){
			ex.printStackTrace();
			this.endExtSessionWithFailure();
		}
	}

	public void syncState(double newValue){
		this.beginExtSession();
		ObsProperty prop = getObsProperty("temperature");
		prop.updateValue(newValue);
		// log("sync");
		this.endExtSession();
	}
	
}
