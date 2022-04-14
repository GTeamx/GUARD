package guard.utils;

import org.bukkit.Location;

public class TestBlockUtils {

    private static Location[] getBoundingBox(Location location) {
        Location[] locs = {location, location.clone().add(1,0,0), location.clone().add(0,0,1), location.clone().add(-1,0,0), location.clone().add(0,0,-1), location.clone().add(-1,0,-1), location.clone().add(1,0,1),location.clone().add(1,0,-1),location.clone().add(-1,0,1)};
        return locs;
    }
    private static boolean checkStand(double x1, double x2, double z1, double z2) {
        return ((Math.abs(x1-x2) < 1.3 && Math.abs(x1-x2) > 0.3) && ((Math.abs(z1-z2) < 1.3 && Math.abs(z1-z2) > 0.3))) || (Math.abs(x1-x2) < 0.3 || Math.abs(z1-z2) < 0.3);
    }
    public static boolean isOnGround(final Location location) {
        for (Location loc : getBoundingBox(location.clone().add(0,-1.5, 0))) {
            if (checkStand(loc.getX(), location.getX(), location.getZ(), loc.getZ()) && !loc.getBlock().isEmpty() && loc.clone().add(0,1,0).getBlock().isEmpty())
                if (loc.getBlock().getType().toString() == "COBBLE_WALL" || loc.getBlock().getType().toString().contains("FENCE"))
                    return true;
        }
        boolean besideswater = false;
        for (Location loc : getBoundingBox(location.clone().add(0,-0.065, 0))) {
            if (location.getY() - loc.getBlock().getY() > getBlockHeight(loc)+0.2) continue;
            if (!loc.getBlock().getType().toString().contains("WATER") && !loc.getBlock().getType().toString().contains("LAVA") && (loc.clone().add(0,1,0).getBlock().isEmpty() || !isFullBlock(loc.clone().add(0,1,0).getBlock().getType().toString())))
                besideswater = true;

        }
        for (Location loc : getBoundingBox(location.clone().add(0,-0.065, 0))) {
            if (location.getY() - loc.getBlock().getY() > getBlockHeight(loc)+0.2) continue;
            if (checkStand(loc.getX(), location.getX(), location.getZ(), loc.getZ()) && !loc.getBlock().isEmpty() && (loc.clone().add(0,1,0).getBlock().isEmpty() || !isFullBlock(loc.clone().add(0,1,0).getBlock().getType().toString())))
                if (besideswater && !loc.getBlock().getType().toString().contains("WATER") && !loc.getBlock().getType().toString().contains("LAVA"))
                    return true;
                else if (!besideswater) return false;
        }
        return false;
    }
    public static Location getStandOn(Location location) {
        for (Location loc : getBoundingBox(location.clone().add(0,-1.5, 0))) {
            if (checkStand(loc.getX(), location.getX(), location.getZ(), loc.getZ()) && !loc.getBlock().isEmpty() && loc.clone().add(0,1,0).getBlock().isEmpty())
                if (loc.getBlock().getType().toString() == "COBBLE_WALL" || loc.getBlock().getType().toString().contains("FENCE"))
                    return loc;
        }
        boolean besideswater = false;
        for (Location loc : getBoundingBox(location.clone().add(0,-0.065, 0))) {
            if (location.getY() - loc.getBlock().getY() > getBlockHeight(loc)+0.2) continue;
            if (!loc.getBlock().getType().toString().contains("WATER") && !loc.getBlock().getType().toString().contains("LAVA") && (loc.clone().add(0,1,0).getBlock().isEmpty() || !isFullBlock(loc.clone().add(0,1,0).getBlock().getType().toString())))
                besideswater = true;

        }
        for (Location loc : getBoundingBox(location.clone().add(0,-0.065, 0))) {
            if (location.getY() - loc.getBlock().getY() > getBlockHeight(loc)+0.2) continue;
            if (checkStand(loc.getX(), location.getX(), location.getZ(), loc.getZ()) && !loc.getBlock().isEmpty() && (loc.clone().add(0,1,0).getBlock().isEmpty() || !isFullBlock(loc.clone().add(0,1,0).getBlock().getType().toString())))
                if (besideswater && !loc.getBlock().getType().toString().contains("WATER") && !loc.getBlock().getType().toString().contains("LAVA"))
                    return loc;
                else if (!besideswater) return loc;
        }
        return null;
    }
    public static Location getGroundLevel(Location location) {
        for (int i=0;i<=location.getY();i++) {
            if (!location.clone().add(0,-i,0).getBlock().isEmpty() && !isLiquid(location.clone().add(0,-i,0))) return location.clone().add(0,-i+1,0);
        }
        return null;
    }
    public static boolean isLiquid(Location location) {
        if (location == null) return false;
        if (location.getBlock().isEmpty()) return false;
        return location.getBlock().getType().toString().contains("WATER") || location.getBlock().getType().toString().contains("LAVA");
    }
    public static boolean isInLiquid(Location location) {
        return isLiquid(getStandOn(location));
    }
    public static int isOnLadder(Location location) {
        if (getStandOn(location.clone().add(0,0.065, 0)) == null) return 0;
        if (getStandOn(location.clone().add(0,0.065, 0)).getBlock().getType().toString() == "LADDER") {
            if (getStandOn(location.clone().add(0,0.065, 0)).getBlock().getState().getData().toString().contains("WEST") && location.getX() % 1 >= 0.57) return 2;
            else if (getStandOn(location.clone().add(0,0.065, 0)).getBlock().getState().getData().toString().contains("EAST") && location.getX() % 1 <= 0.43) return 2;
            else if (getStandOn(location.clone().add(0,0.065, 0)).getBlock().getState().getData().toString().contains("NORTH") && location.getZ() % 1 >= 0.57) return 2;
            else if (getStandOn(location.clone().add(0,0.065, 0)).getBlock().getState().getData().toString().contains("SOUTH") && location.getZ() % 1 <= 0.43) return 2;
            else return 1;
        }
        return 0; //0 = nowhere near ladder, 1 = can descend slowly, 2 = full on ladder
    }
    @SuppressWarnings("deprecation")
    private static double computeSnowHeight(Location location) {
        return (location.getBlock().getData()-1) * 0.125 + 0.1;
    }
    public static boolean isFullBlock(String mat) {
        if (mat.contains("DOOR")) return false;
        else if (mat.contains("CHEST")) return false;
        else if (mat.contains("IRON_BARDING")) return false;
        else if (mat.contains("THIN_GLASS")) return false;
        else if (mat.contains("GLASS_PANE")) return false;
        else if (mat.contains("LADDER")) return false;
        else if (mat.contains("VINE")) return false;
        else if (mat.contains("FENCE")) return false;
        else if (mat.contains("ANVIL")) return false;
        else if (mat.contains("WATER")) return false;
        else if (mat.contains("LAVA")) return false;
        else if (mat.contains("SIGN")) return false;
        else if (mat.contains("WALL")) return false;
        else if (mat.contains("CARPET")) return false;
        return true;
    }
    public static double distance(Location loc1, Location loc2) {
        return Math.sqrt(Math.pow(loc1.getX()-loc2.getX(), 2) + Math.pow(loc1.getY()-loc2.getY(), 2) + Math.pow(loc1.getZ()-loc2.getZ(), 2));
    }
    public static double distance2D(Location loc1, Location loc2) {
        return Math.sqrt(Math.pow(loc1.getX()-loc2.getX(), 2)  + Math.pow(loc1.getZ()-loc2.getZ(), 2));
    }
    public static double getBlockHeight(Location loc) {
        String mat = loc.getBlock().getType().toString();
        if (mat.contains("STEP")) return 0.5;
        if (mat.contains("WALL")) return 1.5;
        if (mat.contains("FENCE")) return 1.5;
        else if (mat.contains("PORTAL_FRAME")) return 0.8125;
        else if (mat.contains("DAYLIGHT")) return 0.375;
        else if (mat.contains("CHEST")) return 0.875;
        else if (mat.contains("FENCE") || mat.contains("WALL")) return 1.5;
        else if (mat.contains("BED")) return 0.5625;
        else if (mat.contains("ENCHANT")) return 0.75;
        else if (mat.contains("SKULL")) return 0.5;
        else if (mat.contains("REPEATER")) return 0.125;
        else if (mat.contains("COMPARATOR")) return 0.125;
        else if (mat == "SNOW") return computeSnowHeight(loc);
        return 1;
    }
}
