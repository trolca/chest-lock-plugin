package me.trololo11.chestlockplugin.managers;

import me.trololo11.chestlockplugin.LockState;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class ChestLockingManager {

    private final ArrayList<LockState> lockStates;

    public ChestLockingManager(){
        this.lockStates = new ArrayList<>();
    }

    public void addLockState(LockState lockState){
        lockStates.add(lockState);
    }

    public void removeLockState(LockState lockState){
        lockStates.remove(lockState);
    }

    public boolean isChestLocked(Location chestLocation){
        return getLockedState(chestLocation) != null;
    }

    @Nullable
    public LockState getLockedState(Location chestLocation){
        for(LockState lockState : lockStates){
            if(lockState.isChestLocked(chestLocation))
                return lockState;
        }

        return null;
    }
}
