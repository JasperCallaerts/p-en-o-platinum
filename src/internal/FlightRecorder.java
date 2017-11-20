package internal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martijn on 19/11/2017.
 */
public class FlightRecorder {

    public FlightRecorder(int logSize, boolean recordOn){
        this.setLogSize(logSize);
        this.setRecordOn(recordOn);
    }

    public FlightRecorder(){
        // empty constructor
    }

    public boolean diagnoseWingIssues(float maxAOA){
        List<List<Float>> wingList = this.getWingRecords();
        boolean diagnosis = false;
        for(int i = 1; i < wingList.size(); i+= 2){
            for(Float elem: wingList.get(i)){
                if(Math.abs(elem)>= maxAOA){
                    diagnosis = true;
                    System.out.println("faulty Wing: " + i/2);
                    System.out.println("Inclination Issue: " + wingList.get(i-1));
                    System.out.println("AOA Issue: " + wingList.get(i));
                    //nothing to do here anymore, break the loop
                    break;
                }
            }
        }

        System.out.println("Controls: " + this.getControlActionsLog());

        return diagnosis;
    }

    public void appendPositionLog(Vector logVar){
        //if the size of the log is to great, delete the first element
        if(positionLog.size() >= this.getLogSize())
            positionLog.remove(0);
        this.positionLog.add(logVar);
    }
    public void appendVelocityLog(Vector logVar){
        if(velocityLog.size() >= this.getLogSize())
            velocityLog.remove(0);
        this.velocityLog.add(logVar);
    }
    public void appendOrientationLog(Vector logVar){
        if(orientationLog.size() >= this.getLogSize())
            orientationLog.remove(0);
        this.orientationLog.add(logVar);
    }
    public void appendRotationLog(Vector logVar){
        if(rotationLog.size() >= this.getLogSize())
            rotationLog.remove(0);
        this.rotationLog.add(logVar);
    }
    public void appendRightMainAOALog(float logVar){
        if(angleOfAttackRightMainWingLog.size() >= this.getLogSize())
            angleOfAttackRightMainWingLog.remove(0);
        this.angleOfAttackRightMainWingLog.add(logVar);
    }
    public void appendLeftMainAOALog(float logVar){
        if(angleOfAttackLeftMainWingLog.size() >= this.getLogSize())
            angleOfAttackLeftMainWingLog.remove(0);
        this.angleOfAttackLeftMainWingLog.add(logVar);
    }
    public void appendHorStabAOALog(float logVar){
        if(angleOfAttackHorStabWingLog.size() >= this.getLogSize())
            angleOfAttackHorStabWingLog.remove(0);
        this.angleOfAttackHorStabWingLog.add(logVar);
    }
    public void appendVerStabAOALog(float logVar){
        if(angleOfAttackVerStabWingLog.size() >= this.getLogSize())
            angleOfAttackVerStabWingLog.remove(0);
        this.angleOfAttackVerStabWingLog.add(logVar);
    }
    public void appendRightMainInclLog(float logVar){
        if(rightMainWingInclinationLog.size() >= this.getLogSize())
            rightMainWingInclinationLog.remove(0);
        this.rightMainWingInclinationLog.add(logVar);
    }
    public void appendLeftMainInclLog(float logVar){
        if(leftMainWingInclinationLog.size() >= this.getLogSize())
            leftMainWingInclinationLog.remove(0);
        this.leftMainWingInclinationLog.add(logVar);
    }
    public void appendHorStabInclLog(float logVar){
        if(horStabWingInclinationLog.size() >= this.getLogSize())
            horStabWingInclinationLog.remove(0);
        this.horStabWingInclinationLog.add(logVar);
    }
    public void appendVerStabInclLog(float logVar){
        if(verStabWingInclinationLog.size() >= this.getLogSize())
            verStabWingInclinationLog.remove(0);
        this.verStabWingInclinationLog.add(logVar);
    }

    public void appendControlLog(String logVar){
        if(controlActionsLog.size()>=this.getLogSize())
            controlActionsLog.remove(0);
        this.controlActionsLog.add(logVar);
    }

    public List<Vector> getPositionLog() {
        return positionLog;
    }

    public List<Vector> getVelocityLog() {
        return velocityLog;
    }

    public List<Vector> getOrientationLog() {
        return orientationLog;
    }

    public List<Vector> getRotationLog() {
        return rotationLog;
    }

    public List<Float> getAngleOfAttackRightMainWingLog() {
        return angleOfAttackRightMainWingLog;
    }

    public List<Float> getAngleOfAttackLeftMainWingLog() {
        return angleOfAttackLeftMainWingLog;
    }

    public List<Float> getAngleOfAttackHorStabWingLog() {
        return angleOfAttackHorStabWingLog;
    }

    public List<Float> getAngleOfAttackVerStabWingLog() {
        return angleOfAttackVerStabWingLog;
    }

    public List<Float> getRightMainWingInclinationLog() {
        return rightMainWingInclinationLog;
    }

    public List<Float> getLeftMainWingInclinationLog() {
        return leftMainWingInclinationLog;
    }

    public List<Float> getHorStabWingInclinationLog() {
        return horStabWingInclinationLog;
    }

    public List<Float> getVerStabWingInclinationLog() {
        return verStabWingInclinationLog;
    }

    public List<String> getControlActionsLog() {
        return controlActionsLog;
    }

    private int getLogSize() {
        return logSize;
    }

    private void setLogSize(int logSize) {
        this.logSize = logSize;
    }

    public boolean isRecordOn() {
        return recordOn;
    }

    public void setRecordOn(boolean recordOn) {
        this.recordOn = recordOn;
    }

    /**
     * Returns a list containing the records of all the wings
     * @return a list containing the records in the following order
     * first the Inclintation, then the Angle of attack of the
     * rightMain, leftMain, horizontalMain, verticalMain
     */
    public List<List<Float>>getWingRecords(){
        List<List<Float>> tempList = new ArrayList<>();
        tempList.add(this.getRightMainWingInclinationLog());
        tempList.add(this.getAngleOfAttackRightMainWingLog());
        tempList.add(this.getLeftMainWingInclinationLog());
        tempList.add(this.getAngleOfAttackLeftMainWingLog());
        tempList.add(this.getHorStabWingInclinationLog());
        tempList.add(this.getAngleOfAttackHorStabWingLog());
        tempList.add(this.getVerStabWingInclinationLog());
        tempList.add(this.getAngleOfAttackVerStabWingLog());

        return tempList;
    }

    /**
     * State logs of the drone itself
     */
    private List<Vector> positionLog = new ArrayList<>();
    private List<Vector> velocityLog = new ArrayList<>();
    private List<Vector> orientationLog = new ArrayList<>();
    private List<Vector> rotationLog = new ArrayList<>();

    /**
     * Wing state logs
     */
    private List<Float> angleOfAttackRightMainWingLog = new ArrayList<>();
    private List<Float> angleOfAttackLeftMainWingLog = new ArrayList<>();
    private List<Float> angleOfAttackHorStabWingLog = new ArrayList<>();
    private List<Float> angleOfAttackVerStabWingLog = new ArrayList<>();
    private List<Float> rightMainWingInclinationLog = new ArrayList<>();
    private List<Float> leftMainWingInclinationLog = new ArrayList<>();
    private List<Float> horStabWingInclinationLog = new ArrayList<>();
    private List<Float> verStabWingInclinationLog = new ArrayList<>();

    private List<String> controlActionsLog = new ArrayList<>();

    /**
     * variable that stores the maximum size of the log
     */
    private int logSize = 1000;

    /**
     * Flag that signals if the flight recorder is in record mode
     */
    private boolean recordOn = true;
}
