package fr.redsarow.phoenixcore.minecraft;

import org.bukkit.GameMode;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author redsarow
 * @since 1.0
 */
public class WorldGroup {

    public final static List<WorldGroup> GROUPS = new ArrayList<>();

    private String name;
    private Team team;
    private Map<String, Team> worlds;
    private boolean scoreboard;
    private boolean deadCount;
    private GameMode gameMode;

    public WorldGroup(String name) {
        this.name = name;
        worlds= new HashMap<>();
        scoreboard = false;
        deadCount = false;
        GROUPS.add(this);
    }

    /**
     * @return group or null
     */
    public static WorldGroup findWorldGroupByWorldName(String worldName){
        for (WorldGroup group : GROUPS) {
            if (group.getWorlds().contains(worldName)) {
                return group;
            }
        }
        return null;
    }

    /**
     * @return group or null
     */
    public static WorldGroup findWorldGroupByName(String worldGroupName){
        for (WorldGroup group : GROUPS) {
            if (group.getName().equalsIgnoreCase(worldGroupName)) {
                return group;
            }
        }
        return null;
    }

    /**
     * @return list of group name
     */
    public static List<String> getListNameGroups(){
        return GROUPS.stream().map(WorldGroup::getName).collect(Collectors.toList());
    }

    //<editor-fold desc="set">
    public void setTeam(Team team) {
        this.team = team;
    }

    public void setWorldTeam(String worldName, Team team) {
        this.worlds.put(worldName, team);
    }

    public void setScoreboard(boolean scoreboard) {
        this.scoreboard = scoreboard;
    }

    public void setDeadCount(boolean deadCount) {
        this.deadCount = deadCount;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = GameMode.valueOf(gameMode);
    }

    //</editor-fold>

    //<editor-fold desc="get">
    public Team getTeamForWorld(String worldName){
        Team team = worlds.get(worldName);
        return team == null? this.team : team;
    }
    public Set<String> getWorlds() {
        return worlds.keySet();
    }

    public Team getWorldTeam(String worldName) {
        return worlds.get(worldName);
    }

    public String getName() {
        return name;
    }

    public Team getTeam() {
        return team;
    }

    public Map<String, Team> getWorldsMap() {
        return worlds;
    }

    public boolean isScoreboard() {
        return scoreboard;
    }

    public boolean isDeadCount() {
        return deadCount;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    //</editor-fold>
}
