package me.theeninja.islandroyale.entity.building;

import com.badlogic.gdx.math.Vector2;
import me.theeninja.islandroyale.Island;
import me.theeninja.islandroyale.MatchMap;
import me.theeninja.islandroyale.ai.Player;
import me.theeninja.islandroyale.entity.Entity;
import me.theeninja.islandroyale.entity.controllable.TransportEntityType;

public class TransportGeneratorBuildingType extends OffenseBuildingType<TransportGeneratorBuildingType, TransportEntityType> {
    @Override
    public Entity<TransportEntityType> produceEntity(TransportEntityType entityType, Player player, Vector2 buildingPos, MatchMap matchMap) {
        Island associatedIsland = matchMap.getIsland(buildingPos);
        Vector2 islandPos = associatedIsland.getPositionOnMap();
        Vector2 relativeToIslandPos = buildingPos.cpy().sub(islandPos);

        int x = (int) relativeToIslandPos.x;
        int y = (int) relativeToIslandPos.y;

        while (associatedIsland.getRepr()[x][y] != null)
            x++;

        return new Entity<>(entityType, player, new Vector2(x, y));
    }
}
