package us.jcedeno.guns.event;

/**
 * An event designed to be fired when a player shoots a bullet from a
 * {@link us.jcedeno.guns.object.GunWrapper}.
 * 
 * @author jcedeno
 */
public class GunFiredShotEvent extends BaseEvent {

    public GunFiredShotEvent(boolean async) {
        super(async);
        //TODO Auto-generated constructor stub
    }

}
