package my.wirelesseye.humanity.entity.ai.sensor;

import my.wirelesseye.humanity.util.RegistryHelper;
import net.minecraft.entity.ai.brain.sensor.SensorType;

public class AllSensorTypes {
    public static final SensorType<NearestMonstersSensor> NEAREST_MONSTERS =
            RegistryHelper.registerSensorType("nearest_monsters", NearestMonstersSensor::new);
}
