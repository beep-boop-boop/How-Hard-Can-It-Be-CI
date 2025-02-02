package com.mygdx.game.Entitys;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Components.Pirate;
import com.mygdx.game.Components.Renderable;
import com.mygdx.game.Components.RigidBody;
import com.mygdx.game.Components.Transform;
import com.mygdx.game.Managers.EntityManager;
import com.mygdx.game.Managers.GameManager;
import com.mygdx.game.Managers.RenderLayer;
import com.mygdx.game.Managers.ResourceManager;
import com.mygdx.game.Physics.CollisionCallBack;
import com.mygdx.game.Physics.CollisionInfo;
import com.mygdx.game.Physics.PhysicsBodyType;

import static com.mygdx.utils.Constants.TILE_SIZE;

/**
 * Cannonball entity and the methods to get it flying.
 */
public class CannonBall extends Entity implements CollisionCallBack {
    private static float speed;
    private boolean toggleLife;
    private int life_remaining;
    private Entity shooter;

    public CannonBall() {
        super(3);
        setName("ball");
        toggleLife = false;
        Transform t = new Transform();
        t.setPosition(10000, 10000);
        t.setScale(0.5f, 0.5f);
        Renderable r = new Renderable(4, "ball", RenderLayer.Transparent);
        RigidBody rb = new RigidBody(PhysicsBodyType.Dynamic, r, t, true);
        rb.setCallback(this);
        life_remaining = (int) (7 / EntityManager.getDeltaTime()); // cannonballs remain active for max 7 seconds (otherwise it is hard to hit buildings deep into islands)
        addComponents(t, r, rb);
        speed = GameManager.getSettings().get("starting").getFloat("cannonSpeed");
        r.hide();
    }

    @Override
    public void update() {
        if(life_remaining > 0){
            life_remaining --;
        }
        else{
            kill();
            getComponent(Renderable.class).setTexture(ResourceManager.getSprite(4, "ball")); // reset to default skin once removed
        }
        super.update();
        removeOnCollision();
    }

    /**
     * Removes the cannonball offscreen once it hits a target or its lifetime expires.
     */
    private void removeOnCollision() {
        if (toggleLife || life_remaining == 0) {
            getComponent(Renderable.class).hide();
            Transform t = getComponent(Transform.class);
            t.setPosition(10000, 10000);

            RigidBody rb = getComponent(RigidBody.class);
            rb.setPosition(t.getPosition());
            rb.setVelocity(0, 0);
            toggleLife = false;
        }
    }

    /**
     * Teleport the cannonball in from offscreen and set in flying away from the firing entity.
     * Changes sprite for cannonballs shot by a duck monster
     * For cannonballs shot by ships, changes sprite to default cannonball sprite.
     * @param pos    2D vector location from where it sets off
     * @param dir    2D vector direction for its movement **NOT THE LOCATION OF THE TARGET**
     * @param sender entity firing it
     */
    public void fire(Vector2 pos, Vector2 dir, Entity sender) {
        shooter = sender;
        if (sender instanceof DuckMonster){
            getComponent(Renderable.class).setTexture(ResourceManager.getSprite(ResourceManager.getId("Eggball.txt"), "egg"));
        }
        else{
            getComponent(Renderable.class).setTexture(ResourceManager.getSprite(4, "ball"));
        }
        Transform t = getComponent(Transform.class);
        t.setPosition(pos);
        RigidBody rb = getComponent(RigidBody.class);
        Vector2 ta = dir.cpy().scl(speed * EntityManager.getDeltaTime());
        Vector2 o = new Vector2(TILE_SIZE * t.getScale().x, TILE_SIZE * t.getScale().y);
        Vector2 v = ta.cpy().sub(o);

        rb.setVelocity(v);

        getComponent(Renderable.class).show();
        life_remaining = (int) (7 / EntityManager.getDeltaTime());
        shooter = sender;
    }

    /**
     * Marks cannonball for removal on next update.
     */
    public void kill() {
        toggleLife = true;
    }
    public Vector2 getPosition(){
        return getComponent(Transform.class).getPosition();
    }

    public Entity getShooter() {
        return shooter;
    }

    public boolean isActive(){return toggleLife;}
    @Override
    public void BeginContact(CollisionInfo info) {

    }

    @Override
    public void EndContact(CollisionInfo info) {

    }

    @Override
    public void EnterTrigger(CollisionInfo info) {

    }

    @Override
    public void ExitTrigger(CollisionInfo info) {

    }
}
