package net.renew_auto_plus;

import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.math.Vec3d;

public class WastesDimensionEffect extends DimensionEffects {
    public WastesDimensionEffect() {
        super(2016.0f, true, SkyType.NORMAL, false, false);
    }

    @Override
    public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
        return color.multiply((double)(sunHeight * 0.94f + 0.06f), (double)(sunHeight * 0.94f + 0.06f), (double)(sunHeight * 0.91f + 0.09f));
    }

    @Override
    public boolean useThickFog(int camX, int camY) {
        return false;
    }
}
