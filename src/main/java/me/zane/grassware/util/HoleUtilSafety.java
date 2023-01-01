package me.zane.grassware.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class HoleUtilSafety
implements Util {
    private static final /* synthetic */ Block[] NO_BLAST;
    public static /* synthetic */ BlockPos[] holeOffsets;
    private static final /* synthetic */ Vec3i[] OFFSETS_2x2;

    public static boolean is2x2(BlockPos blockPos) {
        return HoleUtilSafety.is2x2(blockPos, true);
    }

    public static boolean isObbyHole(BlockPos blockPos) {
        boolean bl = true;
        int n = 0;
        for (BlockPos blockPos2 : holeOffsets) {
            Block block = HoleUtilSafety.mc.world.getBlockState(blockPos.add((Vec3i)blockPos2)).getBlock();
            if (!HoleUtilSafety.isSafeBlock(blockPos.add((Vec3i)blockPos2))) {
                bl = false;
                continue;
            }
            if (block != Blocks.OBSIDIAN && block != Blocks.ENDER_CHEST && block != Blocks.ANVIL) continue;
            ++n;
        }
        if (HoleUtilSafety.mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock() != Blocks.AIR || HoleUtilSafety.mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock() != Blocks.AIR) {
            bl = false;
        }
        if (n < 1) {
            bl = false;
        }
        return bl;
    }

    public static Hole isDoubleHole(BlockPos blockPos) {
        if (HoleUtilSafety.checkOffset(blockPos, 1, 0)) {
            return new Hole(false, true, blockPos, blockPos.add(1, 0, 0));
        }
        if (HoleUtilSafety.checkOffset(blockPos, 0, 1)) {
            return new Hole(false, true, blockPos, blockPos.add(0, 0, 1));
        }
        return null;
    }

    public static boolean isHole(BlockPos blockPos) {
        boolean bl = false;
        int n = 0;
        for (BlockPos blockPos2 : holeOffsets) {
            if (HoleUtilSafety.mc.world.getBlockState(blockPos.add((Vec3i)blockPos2)).getMaterial().isReplaceable()) continue;
            ++n;
        }
        if (n == 5) {
            bl = true;
        }
        return bl;
    }

    public static List<Hole> getHoles(double d, BlockPos blockPos, boolean bl) {
        ArrayList<Hole> arrayList = new ArrayList<Hole>();
        List<BlockPos> list = HoleUtilSafety.getSphere(d, blockPos, true, false);
        for (BlockPos blockPos2 : list) {
            Hole hole;
            if (HoleUtilSafety.mc.world.getBlockState(blockPos2).getBlock() != Blocks.AIR) continue;
            if (HoleUtilSafety.isObbyHole(blockPos2)) {
                arrayList.add(new Hole(false, false, blockPos2));
                continue;
            }
            if (HoleUtilSafety.isBedrockHoles(blockPos2)) {
                arrayList.add(new Hole(true, false, blockPos2));
                continue;
            }
            if (!bl || (hole = HoleUtilSafety.isDoubleHole(blockPos2)) == null || HoleUtilSafety.mc.world.getBlockState(hole.pos1.add(0, 1, 0)).getBlock() != Blocks.AIR && HoleUtilSafety.mc.world.getBlockState(hole.pos2.add(0, 1, 0)).getBlock() != Blocks.AIR) continue;
            arrayList.add(hole);
        }
        return arrayList;
    }

    public static boolean is2x2(BlockPos blockPos, boolean bl) {
        if (bl && !BlockUtil.isAir(blockPos)) {
            return false;
        }
        if (HoleUtilSafety.is2x2Partial(blockPos)) {
            return true;
        }
        BlockPos blockPos2 = blockPos.add(-1, 0, 0);
        boolean bl2 = BlockUtil.isAir(blockPos2);
        if (bl2 && HoleUtilSafety.is2x2Partial(blockPos2)) {
            return true;
        }
        BlockPos blockPos3 = blockPos.add(0, 0, -1);
        boolean bl3 = BlockUtil.isAir(blockPos3);
        if (bl3 && HoleUtilSafety.is2x2Partial(blockPos3)) {
            return true;
        }
        return (bl2 || bl3) && HoleUtilSafety.is2x2Partial(blockPos.add(-1, 0, -1));
    }

    static {
        holeOffsets = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(0, -1, 0)};
        OFFSETS_2x2 = new Vec3i[]{new Vec3i(0, 0, 0), new Vec3i(1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(1, 0, 1)};
        NO_BLAST = new Block[]{Blocks.BEDROCK, Blocks.OBSIDIAN, Blocks.ANVIL, Blocks.ENDER_CHEST};
    }

    public static boolean is2x1(BlockPos blockPos, boolean bl) {
        if (bl) {
            if (!BlockUtil.isAir(blockPos)) {
                return false;
            }
            if (!BlockUtil.isAir(blockPos.up())) {
                return false;
            }
            if (BlockUtil.isAir(blockPos.down())) {
                return false;
            }
        }
        int n = 0;
        for (EnumFacing enumFacing : EnumFacing.HORIZONTALS) {
            BlockPos blockPos2 = blockPos.offset(enumFacing);
            if (BlockUtil.isAir(blockPos2)) {
                if (!BlockUtil.isAir(blockPos2.up())) {
                    return false;
                }
                if (BlockUtil.isAir(blockPos2.down())) {
                    return false;
                }
                for (EnumFacing enumFacing2 : EnumFacing.HORIZONTALS) {
                    if (enumFacing2 == enumFacing.getOpposite()) continue;
                    IBlockState iBlockState = HoleUtilSafety.mc.world.getBlockState(blockPos2.offset(enumFacing2));
                    if (!Arrays.stream(NO_BLAST).noneMatch(block -> block == iBlockState.getBlock())) continue;
                    return false;
                }
                ++n;
            }
            if (n <= 0) continue;
            return false;
        }
        return n == 0;
    }

    public static boolean is2x1(BlockPos blockPos) {
        return HoleUtilSafety.is2x1(blockPos, true);
    }

    public static boolean[] is1x1(BlockPos blockPos) {
        return HoleUtilSafety.is1x1(blockPos, new boolean[]{false, true});
    }

    public static boolean is2x2Partial(BlockPos blockPos) {
        HashSet<BlockPos> hashSet = new HashSet<BlockPos>();
        for (Vec3i arrenumFacing : OFFSETS_2x2) {
            hashSet.add(blockPos.add(arrenumFacing));
        }
        boolean bl = false;
        for (BlockPos blockPos2 : hashSet) {
            if (BlockUtil.isAir(blockPos2) && BlockUtil.isAir(blockPos2.up()) && !BlockUtil.isAir(blockPos2.down())) {
                if (BlockUtil.isAir(blockPos2.up(2))) {
                    bl = true;
                }
                for (EnumFacing enumFacing : EnumFacing.HORIZONTALS) {
                    BlockPos blockPos3 = blockPos2.offset(enumFacing);
                    if (hashSet.contains((Object)blockPos3)) continue;
                    IBlockState iBlockState = HoleUtilSafety.mc.world.getBlockState(blockPos3);
                    if (!Arrays.stream(NO_BLAST).noneMatch(block -> block == iBlockState.getBlock())) continue;
                    return false;
                }
                continue;
            }
            return false;
        }
        return bl;
    }

    static boolean isSafeBlock(BlockPos blockPos) {
        return HoleUtilSafety.mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN || HoleUtilSafety.mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || HoleUtilSafety.mc.world.getBlockState(blockPos).getBlock() == Blocks.ENDER_CHEST;
    }

    public static List<BlockPos> getSphere(double d, BlockPos blockPos, boolean bl, boolean bl2) {
        ArrayList<BlockPos> arrayList = new ArrayList<BlockPos>();
        int n = blockPos.getX();
        int n2 = blockPos.getY();
        int n3 = blockPos.getZ();
        int n4 = n - (int)d;
        while ((double)n4 <= (double)n + d) {
            int n5 = n3 - (int)d;
            while ((double)n5 <= (double)n3 + d) {
                int n6 = bl ? n2 - (int)d : n2;
                while (true) {
                    double d2 = n6;
                    double d3 = bl ? (double)n2 + d : (double)n2 + d;
                    double d4 = d3;
                    if (!(d2 < d3)) break;
                    double d5 = (n - n4) * (n - n4) + (n3 - n5) * (n3 - n5) + (bl ? (n2 - n6) * (n2 - n6) : 0);
                    if (!(!(d5 < d * d) || bl2 && d5 < (d - 1.0) * (d - 1.0))) {
                        BlockPos blockPos2 = new BlockPos(n4, n6, n5);
                        arrayList.add(blockPos2);
                    }
                    ++n6;
                }
                ++n5;
            }
            ++n4;
        }
        return arrayList;
    }

    public static boolean checkOffset(BlockPos blockPos, int n, int n2) {
        return HoleUtilSafety.mc.world.getBlockState(blockPos).getBlock() == Blocks.AIR && HoleUtilSafety.mc.world.getBlockState(blockPos.add(n, 0, n2)).getBlock() == Blocks.AIR && HoleUtilSafety.isSafeBlock(blockPos.add(0, -1, 0)) && HoleUtilSafety.isSafeBlock(blockPos.add(n, -1, n2)) && HoleUtilSafety.isSafeBlock(blockPos.add(n * 2, 0, n2 * 2)) && HoleUtilSafety.isSafeBlock(blockPos.add(-n, 0, -n2)) && HoleUtilSafety.isSafeBlock(blockPos.add(n2, 0, n)) && HoleUtilSafety.isSafeBlock(blockPos.add(-n2, 0, -n)) && HoleUtilSafety.isSafeBlock(blockPos.add(n, 0, n2).add(n2, 0, n)) && HoleUtilSafety.isSafeBlock(blockPos.add(n, 0, n2).add(-n2, 0, -n));
    }

    public static boolean[] isHole(BlockPos blockPos, boolean bl) {
        boolean[] arrbl = new boolean[]{false, true};
        if (!BlockUtil.isAir(blockPos) || !BlockUtil.isAir(blockPos.up()) || bl && !BlockUtil.isAir(blockPos.up(2))) {
            return arrbl;
        }
        return HoleUtilSafety.is1x1(blockPos, arrbl);
    }

    public static boolean[] is1x1(BlockPos blockPos, boolean[] arrbl) {
        for (EnumFacing enumFacing : EnumFacing.values()) {
            BlockPos blockPos2;
            IBlockState iBlockState;
            if (enumFacing == EnumFacing.UP || (iBlockState = HoleUtilSafety.mc.world.getBlockState(blockPos2 = blockPos.offset(enumFacing))).getBlock() == Blocks.BEDROCK) continue;
            if (Arrays.stream(NO_BLAST).noneMatch(block -> block == iBlockState.getBlock())) {
                return arrbl;
            }
            arrbl[1] = false;
        }
        arrbl[0] = true;
        return arrbl;
    }

    public static boolean isBedrockHoles(BlockPos blockPos) {
        boolean bl = true;
        for (BlockPos blockPos2 : holeOffsets) {
            Block block = HoleUtilSafety.mc.world.getBlockState(blockPos.add((Vec3i)blockPos2)).getBlock();
            if (block == Blocks.BEDROCK) continue;
            bl = false;
        }
        if (HoleUtilSafety.mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock() != Blocks.AIR || HoleUtilSafety.mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock() != Blocks.AIR) {
            bl = false;
        }
        return bl;
    }

    public static class Hole {
        public /* synthetic */ BlockPos pos1;
        public /* synthetic */ BlockPos pos2;
        public /* synthetic */ boolean doubleHole;
        public /* synthetic */ boolean bedrock;

        public Hole(boolean bl, boolean bl2, BlockPos blockPos, BlockPos blockPos2) {
            this.bedrock = bl;
            this.doubleHole = bl2;
            this.pos1 = blockPos;
            this.pos2 = blockPos2;
        }

        public Hole(boolean bl, boolean bl2, BlockPos blockPos) {
            this.bedrock = bl;
            this.doubleHole = bl2;
            this.pos1 = blockPos;
        }
    }
}
