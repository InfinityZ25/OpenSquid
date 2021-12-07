package me.aleiv.core.paper.Games;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Data;
import me.aleiv.core.paper.Core;

@Data   
public class PhoneGame {
    Core instance;

    List<UUID> targetList = new ArrayList<>();
    
    public PhoneGame(Core instance){
        this.instance = instance;

    }
}
