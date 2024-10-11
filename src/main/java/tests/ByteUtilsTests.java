package tests;


import me.trololo11.chestlockplugin.utils.ByteUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class ByteUtilsTests {

    @Test
    @DisplayName("UUID should be correct")
    public void checkUUID(){
        UUID uuid1 = UUID.fromString("0ef1bb9f-dd8e-419f-8c96-1d6fb9992fef");
        UUID uuid2 = UUID.fromString("11866155-9c52-41de-97df-208fba6cdab4");
        UUID uuid3 = UUID.fromString("4999d07a-4610-4b1e-84a6-ab9e1fdaa30c");
        UUID uuid4 = UUID.fromString("110430fd-2fc1-4b4a-8678-171efef8aade");
        UUID uuid5 = UUID.fromString("6e1ab7f7-c368-45f4-baa9-fb1419088f14");

        Assertions.assertEquals(uuid1, ByteUtils.getUUIDFromBytes(ByteUtils.getBytesFromUUID(uuid1)));
        Assertions.assertEquals(uuid2, ByteUtils.getUUIDFromBytes(ByteUtils.getBytesFromUUID(uuid2)));
        Assertions.assertEquals(uuid3, ByteUtils.getUUIDFromBytes(ByteUtils.getBytesFromUUID(uuid3)));
        Assertions.assertEquals(uuid4, ByteUtils.getUUIDFromBytes(ByteUtils.getBytesFromUUID(uuid4)));
        Assertions.assertEquals(uuid5, ByteUtils.getUUIDFromBytes(ByteUtils.getBytesFromUUID(uuid5)));
    }

}
