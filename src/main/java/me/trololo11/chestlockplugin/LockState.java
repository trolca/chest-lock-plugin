package me.trololo11.chestlockplugin;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class LockState {

    private UUID owner;
    public final Location originLocation;
    public Location secondPartLocation;
    private final ArrayList<UUID> playersAdded;

    public LockState(@NotNull UUID owner, @NotNull Location originLocation, @Nullable Location secondPartLocation, ArrayList<UUID> playersAdded){
        this.owner = owner;
        this.originLocation = originLocation;
        this.secondPartLocation = secondPartLocation;
        this.playersAdded = playersAdded;
    }

    public UUID getOwner(){
        return this.owner;
    }

    public void changeOwner(UUID newOwner){
        this.owner = newOwner;
    }

    public List<UUID> getAddedPlayers(){
        return Collections.unmodifiableList(playersAdded);
    }

    public void addPlayer(UUID player){
        playersAdded.add(player);
    }

    public void removePlayer(OfflinePlayer player){
        playersAdded.remove(player);
    }

    public boolean isChestDouble(){
        return secondPartLocation == null;
    }

    public boolean isChestLocked(Location chestLocation){
        if(secondPartLocation == null){
            return chestLocation.equals(originLocation);
        }

        return chestLocation.equals(originLocation) || chestLocation.equals(secondPartLocation);
    }
}
