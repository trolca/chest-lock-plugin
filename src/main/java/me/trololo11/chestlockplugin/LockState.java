package me.trololo11.chestlockplugin;

import me.trololo11.chestlockplugin.utils.ByteUtils;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class LockState {

    private UUID owner;
    private byte[] hashCode;
    private final Location originLocation;
    private Location secondPartLocation;
    private final ArrayList<UUID> playersAdded;

    public LockState(@NotNull UUID owner, @NotNull Location originLocation, @Nullable Location secondPartLocation, ArrayList<UUID> playersAdded){
        this.owner = owner;
        this.originLocation = originLocation;
        this.secondPartLocation = secondPartLocation;
        this.playersAdded = playersAdded;
        setHashCode();
    }

    public UUID getOwner(){
        return this.owner;
    }

    public void changeOwner(UUID newOwner){
        this.owner = newOwner;
    }

    @NotNull
    public Location getOriginLocation(){
        return originLocation.clone();
    }

    @Nullable
    public Location getSecondPartLocation(){
        return secondPartLocation == null ? null : secondPartLocation.clone();
    }

    public List<UUID> getAddedPlayers(){
        return Collections.unmodifiableList(playersAdded);
    }

    public void addPlayer(UUID player){
        playersAdded.add(player);
    }

    public void removePlayer(UUID player){
        playersAdded.remove(player);
    }

    public boolean isChestDouble(){
        return secondPartLocation == null;
    }

    public byte[] getHashCode(){
        return hashCode;
    }

    public boolean isChestLocked(Location chestLocation){
        if(secondPartLocation == null){
            return chestLocation.equals(originLocation);
        }

        return chestLocation.equals(originLocation) || chestLocation.equals(secondPartLocation);
    }

    /**
     * Creates a hash based on the locations of this lock state.
     * It uses the MD5 algorithm to hash all the locations and
     * writes the result to the {@link LockState#hashCode} variable
     */
    private void setHashCode(){

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(ByteUtils.getBytesFromUUID(originLocation.getWorld().getUID()));
            baos.write(ByteUtils.intToBytes(originLocation.getBlockX()));
            baos.write(ByteUtils.intToBytes(originLocation.getBlockY()));
            baos.write(ByteUtils.intToBytes(originLocation.getBlockZ()));

            if(secondPartLocation != null){
                baos.write(ByteUtils.getBytesFromUUID(secondPartLocation.getWorld().getUID()));
                baos.write(ByteUtils.intToBytes(secondPartLocation.getBlockX()));
                baos.write(ByteUtils.intToBytes(secondPartLocation.getBlockY()));
                baos.write(ByteUtils.intToBytes(secondPartLocation.getBlockZ()));
            }

            hashCode = messageDigest.digest(baos.toByteArray());
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }


}
