package me.theeninja.islandroyale.entity;


import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import me.theeninja.islandroyale.MatchMap;
import me.theeninja.islandroyale.ai.Player;

import java.util.HashMap;
import java.util.Map;

public class Entity<T extends EntityType<T>> extends Actor {
    private final Sprite sprite;

    /**
     * X is distance to move per secod
     * Y is angle relative to positive x axis that signifies direction, in radians
     */
    private final Vector2 velocityPerSecond = new Vector2(0, 0);

    private final Player owner;

    private final T entityType;

    private final Map<String, Object> properties = new HashMap<>();

    public Entity(T entityType, Player owner, Vector2 position) {
        this.entityType = entityType;
        this.owner = owner;
        this.sprite = new Sprite(getType().getTexture());

        setZIndex(getType().getDrawingPriority());

        getSprite().setSize(getType().getTexture().getWidth() / 16, getType().getTexture().getHeight() / 16);
        getSprite().setPosition(position.x, position.y);
        getSprite().setOriginCenter();

        getType().setUp(this);
    }

    public T getType() {
        return entityType;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void check(float delta, Player player, MatchMap matchMap) {
        getType().check(this, delta, player, matchMap);
    }

    public void present(Camera projector, Stage stage) {
        getType().present(this, projector, stage);
    }

    public boolean shouldRemove() {
        return getType().shouldRemove(this);
    }

    public Player getOwner() {
        return owner;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Vector2 getVelocityPerSecond() {
        return velocityPerSecond;
    }

    public void setSpeed(float speed) {
        getVelocityPerSecond().x = speed;
    }

    public void setDirection(float angle) {
        getVelocityPerSecond().y = angle;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        getSprite().draw(batch);
    }
}
