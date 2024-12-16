package thermostat.domain;


public interface HVACTwinningInterface {

	void connect(HVACPhysicalTwinPort port);
	
	void syncState(String state);
	
}
