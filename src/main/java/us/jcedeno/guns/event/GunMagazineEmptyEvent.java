package us.jcedeno.guns.event;

/**
 * An event designed to be fired when a gun magazine is emptied after having
 * shot a certain amount of bullets. This should be called right after
 * {@link us.jcedeno.guns.event.GunFiredShotEvent}.
 * 
 * @author jcedeno
 */
public class GunMagazineEmptyEvent extends BaseEvent {

    public GunMagazineEmptyEvent(boolean async) {
        super(async);
        //TODO Auto-generated constructor stub
    }
}
