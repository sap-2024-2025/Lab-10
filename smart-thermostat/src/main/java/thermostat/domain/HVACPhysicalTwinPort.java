package thermostat.domain;

public interface HVACPhysicalTwinPort {

	void startHeating() throws Exception;
	
	void stopWorking() throws Exception;

	void startCooling() throws Exception;


}
