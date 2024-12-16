!boot.
  

temperature_sensor_artifact_name("tempSensor").
hvac_artifact_name("hvac").
user_pref_artifact_name("userPrefPanel").

tolerance(0.4).

temperature_in_range(T)
	:- not now_is_colder_than(T) & not now_is_warmer_than(T).

now_is_colder_than(T)
	:- temperature(C) & tolerance(DT) & (T - C) > DT.

now_is_warmer_than(T)
	:- temperature(C) & tolerance(DT) & (C - T) > DT.
  
+!boot <-
	!setup;
	.wait(2000);
	println("setup completed.");	
	!achieve_and_keep_preferred_temperature.
		
+!setup : temperature_sensor_artifact_name(S) & 
		  hvac_artifact_name(H) &
		  user_pref_artifact_name(U)
<-  makeArtifact(S, "thermostat.domain.TempSensorDT",[],TS);
    focus(TS);
	makeArtifact(H, "thermostat.domain.HVACDT",[],HVAC);
	focus(HVAC);
	makeArtifact(U, "thermostat.domain.UserPrefPanel",[],UP);
	focus(UP).
	
   	  
+!achieve_and_keep_preferred_temperature : preferred_temperature(T) <-
	!temperature(T);
	println("preferred temperature ", T, " achieved.").

+!temperature(T): temperature_in_range(T).
		
+!temperature(T): now_is_colder_than(T) <-  
	println("too cold, going to heat.");
	startHeating;
	!heat_until(T).

+!heat_until(T): temperature_in_range(T) <- 
	stopWorking.

+!heat_until(T): now_is_colder_than(T) <-  
	.wait(100);
	!heat_until(T).

+!heat_until(T): now_is_warmer_than(T) <-  
	.wait(100);
	!temperature(T).

+!temperature(T): now_is_warmer_than(T) <-  
	println("too hot, going to cool.");
	startCooling;
	!cool_until(T).
		
+!cool_until(T): temperature_in_range(T) <- 
	stopWorking.

+!cool_until(T): now_is_warmer_than(T)  <-  
	.wait(100);
	!cool_until(T).

+!cool_until(T): now_is_colder_than(T)  <-  
	.wait(100);
	!temperature(T).

//

@change_temp_plan [atomic]
+temperature(T) : preferred_temperature(T2) & not temperature_in_range(T2) & not .intend(temperature(T2))
    <- 	println("Temperature changed (", T, "), going to keep the user preferred temperature...");
		!achieve_and_keep_preferred_temperature.
 	  

@change_preferred_temp_plan [atomic]
+preferred_temperature(T)
	<- 	.drop_intention(temperature(_));
	    println("new preferred temperature: ", T, ", going to achieve it..");
		!achieve_and_keep_preferred_temperature.
	    	
//

+!discoverArtifact(Id)
 	<- 	lookupArtifact(Id,A);
		focus(A);
		println("found ", Id).
		
-!discoverArtifact(Id)
	<-	.wait(200);
		!discoverArtifact(Id).
