package mc.duzo.vortex.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Holds a name, otherwise
 * Identical to {@link AbsoluteBlockPos.Directed}
 *
 * From MDTeam's AIT, however I made this class in that mod so
 * @author duzo
 */
public class Waypoint extends AbsoluteBlockPos.Directed {
    private String name;

    public Waypoint(int x, int y, int z, SerialDimension dimension, Direction direction) {
        super(x, y, z, dimension, direction);
    }

    public Waypoint(BlockPos pos, SerialDimension dimension, Direction direction) {
        super(pos, dimension, direction);
    }

    public Waypoint(AbsoluteBlockPos pos, Direction direction) {
        super(pos, direction);
    }

    public Waypoint(int x, int y, int z, World world, Direction direction) {
        super(x, y, z, world, direction);
    }

    public Waypoint(BlockPos pos, World world, Direction direction) {
        super(pos, world, direction);
    }

    public Waypoint setName(String name) {
        this.name = name;
        return this;
    }
    public String name() {
        return this.name;
    }
    public boolean hasName() {
        return this.name != null;
    }

    @Override
    public NbtCompound toNbt() {
        NbtCompound nbt = super.toNbt();

        if (this.hasName())
            nbt.putString("Name", this.name);

        return nbt;
    }
    public static Waypoint fromNbt(NbtCompound nbt) {
        AbsoluteBlockPos.Directed directed = AbsoluteBlockPos.Directed.fromNbt(nbt);

        String name = nbt.contains("Name") ? nbt.getString("Name") : "Nameless";

        Waypoint waypoint = fromDirected(directed);
        waypoint.setName(name);

        return waypoint;
    }

    public static Waypoint fromDirected(AbsoluteBlockPos.Directed pos) {
        return new Waypoint(pos, pos.getDirection());
    }
    public static Waypoint fromEntity(LivingEntity entity) {
        World world = entity.getWorld();
        BlockPos pos = entity.getBlockPos();
        Direction dir = entity.getHorizontalFacing();

        return new Waypoint(new AbsoluteBlockPos(pos, world), dir);
    }
}
