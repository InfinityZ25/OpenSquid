package me.aleiv.core.paper.objects;

import java.util.UUID;

import lombok.Data;
import me.aleiv.core.paper.Game.Role;

@Data   
public class Participant {
    
    UUID uuid;
    Role role;
    boolean dead = false;

    public Participant(UUID uuid, Role role){
        this.uuid = uuid;
        this.role = role;
    }

    public Participant(UUID uuid, Role role, boolean dead){
        this.uuid = uuid;
        this.role = role;
        this.dead = dead;
    }

}
