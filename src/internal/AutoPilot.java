package internal;

import Autopilot.*;

/**
 * Created by Martijn on 14/10/2017.
 * Extended by Bart on 15/10/2017.
 */
public class AutoPilot implements Autopilot{

	@Override
	public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {

		// TODO Auto-generated method stub
		//bereken output naar drone en gebruik stream om in outputfile te plaatsen
		//geef config info door aan drone
		//AutopilotOutputsWriter.write(stream, value);
		return null;
	}


	@Override
	public AutopilotOutputs timePassed(AutopilotInputs inputs) {
		//update output drone
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void simulationEnded() {
		// TODO Auto-generated method stub
		
	}

}
