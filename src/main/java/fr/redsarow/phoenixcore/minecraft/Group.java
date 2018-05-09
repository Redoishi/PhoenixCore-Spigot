package fr.redsarow.phoenixcore.minecraft;

import org.bukkit.scoreboard.Team;

import java.util.*;

/**
 * @author redsarow
 * @since 1.0
 */
public class Group {

    public final static List<Group> GROUPS = new ArrayList<>();

    private String name;
    private Team team;
    private Map<String, Team> worlds;

    public Group(String name) {
        this.name = name;
        worlds= new HashMap<>();
    }

    /**
     * @return group or null
     */
    public static Group findWorld(String worldName){
        for (Group group : GROUPS) {
            if (group.getWorlds().contains(worldName)) {
                return group;
            }
        }
        return null;
    }

    //<editor-fold desc="set">
    public void setTeam(Team team) {
        this.team = team;
    }

    public void setWorldTeam(String worldName, Team team) {
        this.worlds.put(worldName, team);
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
    //</editor-fold>
}
