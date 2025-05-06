package org.firstinspires.ftc.teamcode.subsystems.stateportable;

import org.firstinspires.ftc.teamcode.ufpackages.CommandBased.Subsystem;
import org.firstinspires.ftc.teamcode.rcfeatures.States;

import java.util.Collection;
import java.util.HashMap;

public class Robot extends StatePortableSubsystem{
    private final HashMap<Class<?extends Subsystem>, StatePortableSubsystem> subsystems;
    private static Robot singleton;
    public static synchronized Robot getInstance(){
        if(singleton==null)singleton = new Robot();
        return singleton;
    }
    private Robot(){
        this.subsystems = new HashMap<>();
    }
    public void addSubsystems(StatePortableSubsystem subsystem){
        subsystems.put(subsystem.getClass(), subsystem);
    }
    @Override
    public void actuateSubsystem(States state) {
        for(StatePortableSubsystem subsystem : subsystems.values()){
            subsystem.actuateSubsystem(state);
        }
    }
    public StatePortableSubsystem getByClass(Class<? extends Subsystem> sClass){
        return subsystems.get(sClass);
    }

    @Override
    public void actuateSubsystem(double value) {
        for(StatePortableSubsystem subsystem : subsystems.values()){
            subsystem.actuateSubsystem(value);
        }
    }

    @Override
    public void finalAction() {
        for(StatePortableSubsystem subsystem : subsystems.values()){
            subsystem.finalAction();
        }
    }
    public Collection<StatePortableSubsystem> getRequirements(){return subsystems.values(); }
    public Collection<Class<? extends Subsystem>> getClasses(){return subsystems.keySet(); }
}
