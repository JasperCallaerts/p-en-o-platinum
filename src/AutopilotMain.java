import Autopilot.AutopilotConfig;
import Autopilot.AutopilotInputs;
import Autopilot.AutopilotOutputs;
import internal.AutoPilot;
import internal.AutoPilotConfig;

import java.io.IOException;

/**
 * Created by Martijn on 13/11/2017.
 * supervised by bart
 */
public class AutopilotMain {

    public AutopilotMain() {
        this.setAutoPilot(new AutoPilot());
    }

    public AutopilotOutputs autopilotStep(AutopilotInputs inputs) throws IOException {

        AutopilotOutputs outputs;

        outputs = this.getAutoPilot().timePassed(inputs);

        return outputs;
    }

    public AutopilotOutputs autopilotConfigStep(AutopilotInputs inputs, AutopilotConfig config) throws IOException {
        AutopilotOutputs outputs;

        outputs = this.getAutoPilot().simulationStarted(config, inputs);

        return outputs;
    }



    public AutoPilot getAutoPilot() {
        return autoPilot;
    }

    public void setAutoPilot(AutoPilot autoPilot) {
        this.autoPilot = autoPilot;
    }

    public AutopilotConfig getAutopilotConfig() {
        return autopilotConfig;
    }

    public void setAutopilotConfig(AutopilotConfig autopilotConfig) {
        this.autopilotConfig = autopilotConfig;
    }

    public AutopilotInputs getAutopilotInputs() {
        return autopilotInputs;
    }

    public void setAutopilotInputs(AutopilotInputs autopilotInputs) {
        this.autopilotInputs = autopilotInputs;
    }

    private AutoPilot autoPilot;
    private AutopilotConfig autopilotConfig;
    private AutopilotInputs autopilotInputs;

}
