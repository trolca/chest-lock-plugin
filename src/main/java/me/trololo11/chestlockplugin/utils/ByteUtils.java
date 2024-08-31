package me.trololo11.chestlockplugin.utils;

import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.UUID;

public class ByteUtils {

    public static byte[] intToBytes(int num){
        return ByteBuffer.allocate(4).putInt(num).array();
    }

    public static byte[] getBytesFromUUID(UUID uuid){
        return ByteBuffer.allocate(16).putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits()).array();
    }

    @Nullable
    public static UUID getUUIDFromBytes(byte @Nullable [] bytes){
        if(bytes == null) return null;
        if(bytes.length != 16) return null;

        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }

}
