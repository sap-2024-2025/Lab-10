package thermostat;

import thermostat.infra.HVACPhysicalTwinAdapter;
import thermostat.infra.TempSensorPhysicalTwinAdapter;
import thermostat.infra.UserPrefAdapter;

public class LaunchSmartThermostat {

	static final String TEMPERATURE_SENSOR_ARTIFACT_NAME = "tempSensor";
	static final String HVAC_ARTIFACT_NAME = "hvac";
	static final String USER_PREF_TEMPERATURE_PANEL_ARTIFACT_NAME = "userPrefPanel";
	
	static final String ROOM_CONTROLLER_SERVICE_ADDRESS = "http://localhost:8080";
	static final String TEMPERATURE_SENSOR_THING_ADDRESS = ROOM_CONTROLLER_SERVICE_ADDRESS;
	static final String HVAC_THING_ADDRESS = ROOM_CONTROLLER_SERVICE_ADDRESS;
	
	static final int USER_PREF_TEMPERATURE_PANEL_SERVICE_PORT = 8082;
	static final double USER_PREF_TEMPERATURE_INITIAL_TEMP = 17;
	
    public static void main (String args[]) throws Exception {

    	/* install the domain layer */
    	
    	launchMAS("src/main/java/thermostat/domain/smart_thermostat.mas2j");
    	
    	/* install the infrastructure layer */
    	
    	var tempSensorAdapter = new TempSensorPhysicalTwinAdapter(TEMPERATURE_SENSOR_ARTIFACT_NAME, TEMPERATURE_SENSOR_THING_ADDRESS);
    	tempSensorAdapter.init();
    	
    	var hvacAdapter = new HVACPhysicalTwinAdapter(HVAC_ARTIFACT_NAME, HVAC_THING_ADDRESS);
    	hvacAdapter.init();

    	var userPrefAdapter = new UserPrefAdapter(USER_PREF_TEMPERATURE_INITIAL_TEMP, USER_PREF_TEMPERATURE_PANEL_ARTIFACT_NAME, USER_PREF_TEMPERATURE_PANEL_SERVICE_PORT);
    	userPrefAdapter.init();
    	
    }
    
    private static void launchMAS(String mas2jFilePath) {
    	new Thread(() -> {
    		try {
    			jason.infra.local.RunLocalMAS.main(new String[]{mas2jFilePath  });
    		} catch (Exception ex) {
    			ex.printStackTrace();
    		}
    	}).start();
    }

}
