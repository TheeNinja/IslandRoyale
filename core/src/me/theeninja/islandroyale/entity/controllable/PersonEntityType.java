package me.theeninja.islandroyale.entity.controllable;

import com.badlogic.gdx.graphics.g2d.Batch;
import me.theeninja.islandroyale.MatchMap;
import me.theeninja.islandroyale.Player;
import me.theeninja.islandroyale.entity.Entity;
import me.theeninja.islandroyale.entity.EntityType;
import me.theeninja.islandroyale.entity.Offensive;

public class PersonEntityType extends ControllableEntityType<PersonEntityType> implements Offensive<PersonEntityType> {
    public static final String PERSON_DIRECTORY = "person/";

    private float baseDamage;
    private float baseFireRate;

    public static final String IS_CARRIED_LABEL = "isCarried";

    private final static String TIME_LEFT_LABEL = "timeLeft";

    @Override
    public void initialize(Entity<PersonEntityType> entity) {
        entity.getProperties().put(TIME_LEFT_LABEL, 1 / getBaseFireRate());
    }

    @Override
    public void check(Entity<PersonEntityType> entity, float delta, Player player, MatchMap matchMap) {
        boolean isCarried = getProperty(entity, IS_CARRIED_LABEL);

        // If this person is being carried by transporation, let the transporter
        // take care of moving this entity
        if (!isCarried)
            updateMoveAttributes(entity, delta);

        Entity<? extends EntityType> currentTargetEntity = getProperty(entity, ATTACKING_TARGET_LABEL);

        // If the current target entity has expired, i.e a new target entity is required
        if (currentTargetEntity == null || currentTargetEntity.getHealth() <= 0)
            setProperty(entity,ATTACKING_TARGET_LABEL, getNewTargetEntity(entity, matchMap, getBaseDamage()));

        performDamageCheck(entity, delta);
    }

    private void performDamageCheck(Entity<PersonEntityType> entity, float delta) {
        float timeLeft = getProperty(entity, TIME_LEFT_LABEL);
        timeLeft -= delta;

        if (timeLeft <= 0) {
            setProperty(entity, TIME_LEFT_LABEL, 1 / getBaseFireRate());
            Entity<? extends EntityType> targetEntity = getProperty(entity, ATTACKING_TARGET_LABEL);
            targetEntity.dealDamage(getBaseDamage());
        }
        else
            setProperty(entity, TIME_LEFT_LABEL, 1 / getBaseFireRate());
    }

    @Override
    public void present(Entity<PersonEntityType> entity, Batch batch, float centerPixelX, float centerPixelY) {

    }

    public float getBaseDamage() {
        return baseDamage;
    }

    public float getBaseFireRate() {
        return baseFireRate;
    }
}