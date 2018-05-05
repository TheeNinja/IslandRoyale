package me.theeninja.islandroyale.entity.building;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import me.theeninja.islandroyale.MatchMap;
import me.theeninja.islandroyale.Player;
import me.theeninja.islandroyale.entity.Entity;
import me.theeninja.islandroyale.entity.EntityType;
import me.theeninja.islandroyale.entity.InteractableEntityType;
import me.theeninja.islandroyale.entity.controllable.ControllableEntityType;
import me.theeninja.islandroyale.gui.screens.MatchScreen;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @param <T> the <b>t</b>ype of the class that is subclassing OffensiveBuildingType
 * @param <P> the entity type that this offense building <b>p</b>roduces (most likely between
 *           {@link me.theeninja.islandroyale.entity.controllable.PersonEntityType} and
 *           {@link me.theeninja.islandroyale.entity.controllable.TransportEntityType}
 */
public abstract class OffenseBuildingType<T extends OffenseBuildingType<T, P>, P extends ControllableEntityType<P>> extends BuildingEntityType<T> {
    private List<Integer> entityIDsProduced;

    private static final String ENTITY_IDS_IN_QUEUE = "entityIDs";
    private static final String TIMER_LABEL = "timer";
    private static final String QUEUE_BUTTON_LISTENER_LABEL = "queueButtonListener";

    @Override
    public void setUp(Entity<T> entity) {
        super.setUp(entity);
        setProperty(entity, ENTITY_IDS_IN_QUEUE, new LinkedList<Integer>());
    }

    @Override
    public void configureEditor(Entity<T> entity, VerticalGroup verticalGroup) {
        List<QueueButtonListener> queueButtonListeners = new ArrayList<>();

        for (int entityID : getEntityIDsProduced()) {
            TextButton queueButton = new TextButton("Queue " + entityID, MatchScreen.FLAT_EARTH_SKIN);
            queueButton.setBounds(0, 0, queueButton.getWidth(), queueButton.getHeight());

            QueueButtonListener queueButtonListener = new QueueButtonListener(entityID);
            queueButton.addListener(queueButtonListener);

            verticalGroup.addActor(queueButton);

            queueButtonListeners.add(queueButtonListener);
        }

        setProperty(entity, QUEUE_BUTTON_LISTENER_LABEL, queueButtonListeners);
    }

    private void checkQueues(Entity<T> entity) {
        List<QueueButtonListener> queueButtonListeners = getProperty(entity, QUEUE_BUTTON_LISTENER_LABEL);
        List<Integer> entityIdsInQueue = getProperty(entity, ENTITY_IDS_IN_QUEUE);

        System.out.println("Number of queue checks " + queueButtonListeners.size());

        for (QueueButtonListener queueButtonListener : queueButtonListeners) {
            if (queueButtonListener.shouldQueryEntity()) {

                // If there are no other entities queued, queue this entity with its associated time required
                if (entityIdsInQueue.isEmpty())
                    setProperty(entity, TIMER_LABEL, 2f);
                // Else, we still add it, however we do not modify the time as that is being handled by the
                // below check method

                entityIdsInQueue.add(queueButtonListener.getId());
                queueButtonListener.setShouldQueryEntity(false);
            }

        }
    }

    @Override
    public void check(Entity<T> entity, float delta, Player player, MatchMap matchMap) {
        checkQueues(entity);

        LinkedList<Integer> entityIdsInQueue = getProperty(entity, ENTITY_IDS_IN_QUEUE);

        System.out.println("Number of entities in queue " + entityIdsInQueue.size());

        // No entities left to process
        if (entityIdsInQueue.isEmpty())
            return;

        float secondsLeft = getProperty(entity, TIMER_LABEL);
        secondsLeft -= delta;

        if (secondsLeft > 0) {
            setProperty(entity, TIMER_LABEL, secondsLeft);
            return;
        }

        int entityTypeID = entityIdsInQueue.pollFirst();
        P producedEntityType = InteractableEntityType.getEntityType(entityTypeID);

        Vector2 position = new Vector2(entity.getSprite().getX(), entity.getSprite().getY());
        Entity<? extends InteractableEntityType<?>> newEntity = produceEntity(producedEntityType, player, position, matchMap);

        matchMap.getEntities().add(newEntity);

        // No more entities left to process
        if (entityIdsInQueue.isEmpty())
            return;

        int nextEntityID = entityIdsInQueue.peekFirst();
        float requiredTime = 2;

        setProperty(entity, TIMER_LABEL, requiredTime);
    }

    public Entity<P> produceEntity(int entityIndex, Player player, Vector2 buildingPos, MatchMap matchMap) {
        int entityId = getEntityIDsProduced().get(entityIndex);
        P entityType = EntityType.getEntityType(entityId);

        return produceEntity(entityType, player, buildingPos, matchMap);
    }

    abstract Entity<P> produceEntity(P entityType, Player player, Vector2 buildingPos, MatchMap matchMap);

    @Override
    public void present(Entity<T> entity, Stage stage) {
        super.present(entity, stage);
    }

    public List<Integer> getEntityIDsProduced() {
        return entityIDsProduced;
    }
}
