package thermostat.domain;

import cartago.*;

public class UserPrefPanel extends Artifact implements UserPrefPort {
			
	void init(){
		defineObsProperty("state","unconnected");	
	}	
	
	public void connect(double preferredTemp) {
		this.beginExtSession();
		try {
	 		defineObsProperty("preferred_temperature",preferredTemp);
			log("connected.");
			this.endExtSession();
		} catch (Exception ex){
			ex.printStackTrace();
			this.endExtSessionWithFailure();
		}
	}

	public void notifyNewPreferredTemp(double preferredTemp) {
		this.beginExtSession();
		try {
	 		this.getObsProperty("preferred_temperature").updateValue(preferredTemp);
			this.endExtSession();
		} catch (Exception ex){
			ex.printStackTrace();
			this.endExtSessionWithFailure();
		}
	}

}
