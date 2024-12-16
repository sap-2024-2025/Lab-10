package thermostat.domain;

import cartago.*;

public class HVACDT extends Artifact implements HVACTwinningInterface {
	
	private HVACPhysicalTwinPort port;
		
	void init(){
		defineObsProperty("state","unconnected");	
	}
	
	@OPERATION void startHeating(){
		try {
			port.startHeating();
		} catch (Exception ex) {
			this.failed(ex.getMessage());
		}
	}
	
	@OPERATION void stopWorking(){
		try {
			port.stopWorking();
		} catch (Exception ex) {
			this.failed(ex.getMessage());
		}
	}

	@OPERATION void startCooling(){
		try {
			port.startCooling();
		} catch (Exception ex) {
			this.failed(ex.getMessage());
		}
	}

	
	public void connect(HVACPhysicalTwinPort port) {
		this.beginExtSession();
		try {
			this.port = port;
			this.getObsProperty("state").updateValue("connected");
			log("connected.");
			this.endExtSession();
		} catch (Exception ex){
			ex.printStackTrace();
			this.endExtSessionWithFailure();
		}
	}

	public void syncState(String state) {
		this.beginExtSession();
		try {
			this.getObsProperty("state").updateValue(state);
			this.endExtSession();
		} catch (Exception ex){
			ex.printStackTrace();
			this.endExtSessionWithFailure();
		}
	}

}
