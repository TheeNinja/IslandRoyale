package me.theeninja.islandroyale.gui.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import me.theeninja.islandroyale.entity.Entity;
import me.theeninja.islandroyale.entity.EntityType;
import me.theeninja.islandroyale.entity.InteractableEntityType;
import me.theeninja.islandroyale.entity.controllable.TransportEntityType;

import static me.theeninja.islandroyale.gui.screens.MatchScreen.*;

public class MatchScreenInputListener implements InputProcessor {

    private final MatchScreen matchScreen;

    private static final int SHOW_MAP_KEY = Input.Keys.M;
    private boolean mapShown;

    MatchScreenInputListener(MatchScreen matchScreen) {
        this.matchScreen = matchScreen;
    }

    @Override
    public boolean keyDown( int keyCode) {
        switch (keyCode) {
            case SHOW_MAP_KEY: {
                System.out.println("Showing map");
                setMapShown(true);
                return true;
            }

            default: return false;
        }
    }

    @Override
    public boolean keyUp(int keyCode) {
        switch (keyCode) {
            case SHOW_MAP_KEY: {
                System.out.println("Hiding map");
                setMapShown(false);
                return true;
            }

            default: return false;
        }
    }

    public MatchScreen getMatchScreen() {
        return matchScreen;
    }

    public boolean isMapShown() {
        return mapShown;
    }

    public void setMapShown(boolean mapShown) {
        this.mapShown = mapShown;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 checkEntityCoords = new Vector3(screenX, screenY, 0);
        Vector3 checkDescriptorCoords = checkEntityCoords.cpy();

        getMatchScreen().getMapCamera().unproject(checkEntityCoords);
        getMatchScreen().getHudCamera().unproject(checkDescriptorCoords);

        System.out.println("Checking entity coordinate positions " + checkEntityCoords);
        System.out.println("Checking descriptor coordinate positions " + checkDescriptorCoords);
        System.out.println("Checking screen coordinate positions " + screenX + " " + screenY);

        boolean touchedEntity = false;

        for (Entity<? extends EntityType<?>> entity : getMatchScreen().getMatchMap().getEntities()) {
            // Entity is not interactable, no need to handle attempted interaction
            if (!(entity.getType() instanceof InteractableEntityType))
                continue;

            // If entity is transporter and a person entity type has requested to board this transporter,
            // do not bring up descriptor and allow listeners further down to handle this event
            if (entity.getType() instanceof TransportEntityType)
                if (EntityType.getProperty(entity, TransportEntityType.TRANSPORT_REQUEST_LABEL) != null)
                    continue;

            boolean touchInEntityBounds = entity.getSprite().getBoundingRectangle().contains(checkEntityCoords.x, checkEntityCoords.y);
            boolean touchInDescriptorBounds;

            boolean descriptorShown = EntityType.getProperty(entity, InteractableEntityType.DESCRIPTOR_SHOWN_LABEL);

            // If the descriptor is currently not shown, no need to check for its bounds containing mouse click
            if (!descriptorShown)
                touchInDescriptorBounds = false;

            else {
                Actor descriptor = EntityType.getProperty(entity, InteractableEntityType.TABLE_LABEL);

                boolean xInBounds = descriptor.getX() < checkDescriptorCoords.x && checkDescriptorCoords.x < descriptor.getX() + descriptor.getWidth();
                boolean yInBounds = descriptor.getY() < checkDescriptorCoords.y && checkDescriptorCoords.y < descriptor.getY() + descriptor.getHeight();

                System.out.println("Descriptor start " + descriptor.getX());
                System.out.println("Descriptor end " + (descriptor.getX() + descriptor.getWidth()));

                touchInDescriptorBounds = xInBounds && yInBounds;
            }

            EntityType.setProperty(entity, InteractableEntityType.DESCRIPTOR_SHOWN_LABEL, touchInEntityBounds || touchInDescriptorBounds);

            if (touchInEntityBounds || touchInDescriptorBounds)
                touchedEntity = true;
        }

        return touchedEntity;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
