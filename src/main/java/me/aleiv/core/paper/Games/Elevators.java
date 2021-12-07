package me.aleiv.core.paper.Games;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.aleiv.core.paper.AnimationTools;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.objects.Elevator;

public class Elevators {
    Core instance;

    HashMap<ElevatorType, Elevator> elevators = new HashMap<>();
    
    public Elevators(Core instance){
        this.instance = instance;

    }

    public enum ElevatorType {
        ONE, TWO, THREE, DOLL, HIDE_SEEK, POTATO, EXIT1, EXIT2, CHICKEN1, CHICKEN2, GOCHICKEN, GLASS,
        VIP_MAIN, VIP_ADMIN, VIP_GLASS, VIP_ROPE, VIP_DOLL, MAIN_ROOM
    }

    public void elevatorTravel(Player player, ElevatorType elevator2, Boolean bool){

        if(elevators.isEmpty()){
            registerElevators();
        }

        var ele = elevators.entrySet().stream().filter(entry -> entry.getValue().containsPlayer(player)).findAny().orElse(null);

        if(ele != null){
            var elevator1 = ele.getKey();
            if(elevators.containsKey(elevator1) && elevators.containsKey(elevator2)){
                var elev1 = elevators.get(elevator1);
                var elev2 = elevators.get(elevator2);
    
                elev1.travel(elev2, bool);
                instance.getGame().setLights(bool);
            }
        }

    }

    public void elevatorDoor(ElevatorType elevatorType, Boolean bool){
        switch (elevatorType) {
            case ONE ->{
                elevator1(bool);
            }
            case TWO ->{
                elevator2(bool);
            }
            case THREE ->{
                elevator3(bool);
            }
            case DOLL ->{
                dollElevator(bool);
            }
            case HIDE_SEEK ->{
                hideSeekElevator(bool);
            }
            case POTATO ->{
                potatoElevator(bool);
            }
            case EXIT1 ->{
                exitElevator1(bool);
            }

            case EXIT2 ->{
                exitElevator2(bool);
            }

            case CHICKEN1 ->{
                chickenElevator1(bool);
            }

            case CHICKEN2 ->{
                chickenElevator2(bool);
            }

            case GOCHICKEN ->{
                goChickenElevator(bool);
            }

            case GLASS ->{
                glassElevator(bool);
            }
            case VIP_MAIN ->{
                moveElevator28("VIP_MAIN", bool);
            }
            case VIP_ADMIN ->{
                moveElevator28("VIP_ADMIN", bool);
            }
            case VIP_GLASS ->{
                moveElevator28("VIP_GLASS", bool);
            }
            case VIP_ROPE ->{
                moveElevator28("VIP_ROPE", bool);
            }
            case VIP_DOLL ->{
                moveElevator28("VIP_DOLL", bool);
            }
        case MAIN_ROOM -> throw new UnsupportedOperationException("Unimplemented case: " + elevatorType);
        default -> throw new IllegalArgumentException("Unexpected value: " + elevatorType);

        }
    }

    public void moveElevator28(String name, Boolean bool){
        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");
        var loc1 = AnimationTools.parseLocation(specialObjects.get(name + "_POS1"), world);
        var loc2 = AnimationTools.parseLocation(specialObjects.get(name + "_POS2"), world);

        if(bool){

            AnimationTools.fill(loc1, loc2, Material.AIR);

            AnimationTools.playSoundDistance(loc1, 10, "squid:sfx.tp_elevator_open", 1f, 1f);

            AnimationTools.move(name + "_RIGHT", name + "_LEFT", 28, 1, 'x', 0.1f);

        }else{

            AnimationTools.playSoundDistance(loc1, 10, "squid:sfx.tp_elevator_close", 1f, 1f);

            var task = AnimationTools.move(name + "_LEFT", name + "_RIGHT", 28, 1, 'x', 0.1f);

            task.thenAccept(action ->{
                Bukkit.getScheduler().runTask(instance, tk ->{
                    AnimationTools.fill(loc1, loc2, Material.PRISMARINE_WALL);
                });
            });
        }
    }

    public void glassElevator(Boolean bool){
        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");
        var loc1 = AnimationTools.parseLocation(specialObjects.get("GLASS_ELEVATOR_POS1"), world);
        var loc2 = AnimationTools.parseLocation(specialObjects.get("GLASS_ELEVATOR_POS2"), world);

        if(bool){

            AnimationTools.fill(loc1, loc2, Material.AIR);

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_open", 1f, 1f);

            AnimationTools.move("GLASS_ELEVATOR_RIGHT", "GLASS_ELEVATOR_LEFT", 28, 1, 'x', 0.1f);

        }else{

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_close", 1f, 1f);

            var task = AnimationTools.move("GLASS_ELEVATOR_LEFT", "GLASS_ELEVATOR_RIGHT", 28, 1, 'x', 0.1f);

            task.thenAccept(action ->{
                Bukkit.getScheduler().runTask(instance, tk ->{
                    AnimationTools.fill(loc1, loc2, Material.PRISMARINE_WALL);
                });
            });
        }
    }

    public void goChickenElevator(Boolean bool){
        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");
        var loc1 = AnimationTools.parseLocation(specialObjects.get("GOCHICKEN_ELEVATOR_POS1"), world);
        var loc2 = AnimationTools.parseLocation(specialObjects.get("GOCHICKEN_ELEVATOR_POS2"), world);

        if(bool){

            AnimationTools.fill(loc1, loc2, Material.AIR);

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_open", 1f, 1f);

            AnimationTools.move("GOCHICKEN_ELEVATOR_LEFT", "GOCHICKEN_ELEVATOR_RIGHT", 28, 1, 'x', 0.1f);

        }else{

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_close", 1f, 1f);

            var task = AnimationTools.move("GOCHICKEN_ELEVATOR_RIGHT", "GOCHICKEN_ELEVATOR_LEFT", 28, 1, 'x', 0.1f);

            task.thenAccept(action ->{
                Bukkit.getScheduler().runTask(instance, tk ->{
                    AnimationTools.fill(loc1, loc2, Material.PRISMARINE_WALL);
                });
            });
        }
    }

    public void chickenElevator1(Boolean bool){
        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");
        var loc1 = AnimationTools.parseLocation(specialObjects.get("CHICKEN_ELEVATOR_POS1"), world);
        var loc2 = AnimationTools.parseLocation(specialObjects.get("CHICKEN_ELEVATOR_POS2"), world);

        if(bool){

            AnimationTools.fill(loc1, loc2, Material.AIR);

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_open", 1f, 1f);

            AnimationTools.move("CHICKEN_ELEVATOR_LEFT", "CHICKEN_ELEVATOR_RIGHT", 28, 1, 'x', 0.1f);

        }else{

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_close", 1f, 1f);

            var task = AnimationTools.move("CHICKEN_ELEVATOR_RIGHT", "CHICKEN_ELEVATOR_LEFT", 28, 1, 'x', 0.1f);

            task.thenAccept(action ->{
                Bukkit.getScheduler().runTask(instance, tk ->{
                    AnimationTools.fill(loc1, loc2, Material.PRISMARINE_WALL);
                });
            });
        }
    }

    public void chickenElevator2(Boolean bool){
        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");
        var loc1 = AnimationTools.parseLocation(specialObjects.get("CHICKEN_ELEVATOR2_POS1"), world);
        var loc2 = AnimationTools.parseLocation(specialObjects.get("CHICKEN_ELEVATOR2_POS2"), world);

        if(bool){

            AnimationTools.fill(loc1, loc2, Material.AIR);

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_open", 1f, 1f);

            AnimationTools.move("CHICKEN_ELEVATOR2_RIGHT", "CHICKEN_ELEVATOR2_LEFT", 28, 1, 'x', 0.1f);

        }else{

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_close", 1f, 1f);

            var task = AnimationTools.move("CHICKEN_ELEVATOR2_LEFT", "CHICKEN_ELEVATOR2_RIGHT", 28, 1, 'x', 0.1f);

            task.thenAccept(action ->{
                Bukkit.getScheduler().runTask(instance, tk ->{
                    AnimationTools.fill(loc1, loc2, Material.PRISMARINE_WALL);
                });
            });
        }
    }

    public void potatoElevator(Boolean bool){
        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");
        var loc1 = AnimationTools.parseLocation(specialObjects.get("POTATO_ELEVATOR_POS1"), world);
        var loc2 = AnimationTools.parseLocation(specialObjects.get("POTATO_ELEVATOR_POS2"), world);

        if(bool){

            AnimationTools.fill(loc1, loc2, Material.AIR);

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_open", 1f, 1f);

            AnimationTools.move("POTATO_ELEVATOR_LEFT", "POTATO_ELEVATOR_RIGHT", 28, 1, 'x', 0.1f);

        }else{

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_close", 1f, 1f);

            var task = AnimationTools.move("POTATO_ELEVATOR_RIGHT", "POTATO_ELEVATOR_LEFT", 28, 1, 'x', 0.1f);

            task.thenAccept(action ->{
                Bukkit.getScheduler().runTask(instance, tk ->{
                    AnimationTools.fill(loc1, loc2, Material.PRISMARINE_WALL);
                });
            });

        }
    }

    public void exitElevator1(Boolean bool){
        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");
        var loc1 = AnimationTools.parseLocation(specialObjects.get("EXIT1_ELEVATOR_POS1"), world);
        var loc2 = AnimationTools.parseLocation(specialObjects.get("EXIT1_ELEVATOR_POS2"), world);

        if(bool){

            AnimationTools.fill(loc1, loc2, Material.AIR);

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_open", 1f, 1f);

            AnimationTools.move("EXIT1_ELEVATOR_LEFT", "EXIT1_ELEVATOR_RIGHT", 28, 1, 'x', 0.1f);

        }else{

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_close", 1f, 1f);

            var task = AnimationTools.move("EXIT1_ELEVATOR_RIGHT", "EXIT1_ELEVATOR_LEFT", 28, 1, 'x', 0.1f);

            task.thenAccept(action ->{
                Bukkit.getScheduler().runTask(instance, tk ->{
                    AnimationTools.fill(loc1, loc2, Material.PRISMARINE_WALL);
                });
            });
        }
    }

    public void exitElevator2(Boolean bool){
        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");
        var loc1 = AnimationTools.parseLocation(specialObjects.get("EXIT2_ELEVATOR_POS1"), world);
        var loc2 = AnimationTools.parseLocation(specialObjects.get("EXIT2_ELEVATOR_POS2"), world);

        if(bool){

            AnimationTools.fill(loc1, loc2, Material.AIR);

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_open", 1f, 1f);

            AnimationTools.move("EXIT2_ELEVATOR_RIGHT", "EXIT2_ELEVATOR_LEFT", 28, 1, 'x', 0.1f);

        }else{

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_close", 1f, 1f);

            var task = AnimationTools.move("EXIT2_ELEVATOR_LEFT", "EXIT2_ELEVATOR_RIGHT", 28, 1, 'x', 0.1f);

            task.thenAccept(action ->{
                Bukkit.getScheduler().runTask(instance, tk ->{
                    AnimationTools.fill(loc1, loc2, Material.PRISMARINE_WALL);
                });
            });
        }
    }

    public void dollElevator(Boolean bool){
        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");
        var loc1 = AnimationTools.parseLocation(specialObjects.get("DOLL_ELEVATOR_POS1"), world);
        var loc2 = AnimationTools.parseLocation(specialObjects.get("DOLL_ELEVATOR_POS2"), world);

        if(bool){

            AnimationTools.fill(loc1, loc2, Material.AIR);

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_open", 1f, 1f);

            AnimationTools.move("DOLL_ELEVATOR_LEFT", "DOLL_ELEVATOR_RIGHT", 32, 1, 'z', 0.1f);

        }else{

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_close", 1f, 1f);

            var task = AnimationTools.move("DOLL_ELEVATOR_RIGHT", "DOLL_ELEVATOR_LEFT", 32, 1, 'z', 0.1f);

            task.thenAccept(action ->{
                Bukkit.getScheduler().runTask(instance, tk ->{
                    AnimationTools.fill(loc1, loc2, Material.PRISMARINE_WALL);
                });
            });

        }
    }

    public void hideSeekElevator(Boolean bool){
        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");
        var loc1 = AnimationTools.parseLocation(specialObjects.get("HIDE_SEEK_ELEVATOR_POS1"), world);
        var loc2 = AnimationTools.parseLocation(specialObjects.get("HIDE_SEEK_ELEVATOR_POS2"), world);

        if(bool){

            AnimationTools.fill(loc1, loc2, Material.AIR);

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_open", 1f, 1f);

            AnimationTools.move("HIDE_SEEK_ELEVATOR_RIGHT", "HIDE_SEEK_ELEVATOR_LEFT", 32, 1, 'z', 0.1f);

        }else{
            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_close", 1f, 1f);

            var task = AnimationTools.move("HIDE_SEEK_ELEVATOR_LEFT", "HIDE_SEEK_ELEVATOR_RIGHT", 32, 1, 'z', 0.1f);

            task.thenAccept(action ->{
                Bukkit.getScheduler().runTask(instance, tk ->{
                    AnimationTools.fill(loc1, loc2, Material.PRISMARINE_WALL);
                });
            });

        }
    }

    public void elevator1(Boolean bool){
        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");
        var loc1 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR1_POS1"), world);
        var loc2 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR1_POS2"), world);

        if(bool){

            AnimationTools.fill(loc1, loc2, Material.AIR);

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_open", 1f, 1f);

            AnimationTools.move("ELEVATOR1_RIGHT", "ELEVATOR1_LEFT", 32, 1, 'z', 0.1f);

        }else{

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_close", 1f, 1f);

            var task = AnimationTools.move("ELEVATOR1_LEFT", "ELEVATOR1_RIGHT", 32, 1, 'z', 0.1f);

            task.thenAccept(action ->{
                Bukkit.getScheduler().runTask(instance, tk ->{
                    AnimationTools.fill(loc1, loc2, Material.PRISMARINE_WALL);
                });
            });
        }
    }
    
    public void elevator2(Boolean bool){
        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");
        var loc1 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR2_POS1"), world);
        var loc2 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR2_POS2"), world);

        if(bool){

            AnimationTools.fill(loc1, loc2, Material.AIR);

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_open", 1f, 1f);

            AnimationTools.move("ELEVATOR2_LEFT", "ELEVATOR2_RIGHT", 32, 1, 'z', 0.1f);

        }else{

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_close", 1f, 1f);

            var task = AnimationTools.move("ELEVATOR2_RIGHT", "ELEVATOR2_LEFT", 32, 1, 'z', 0.1f);
            
            task.thenAccept(action ->{
                Bukkit.getScheduler().runTask(instance, tk ->{
                    AnimationTools.fill(loc1, loc2, Material.PRISMARINE_WALL);
                });
            });

        }
    }

    public void elevator3(Boolean bool){
        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");
        var loc1 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR3_POS1"), world);
        var loc2 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR3_POS2"), world);

        if(bool){

            AnimationTools.fill(loc1, loc2, Material.AIR);

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_open", 1f, 1f);

            AnimationTools.move("ELEVATOR3_LEFT", "ELEVATOR3_RIGHT", 28, 1, 'x', 0.1f);

        }else{

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.tp_elevator_close", 1f, 1f);

            var task = AnimationTools.move("ELEVATOR3_RIGHT", "ELEVATOR3_LEFT", 28, 1, 'x', 0.1f);

            task.thenAccept(action ->{
                Bukkit.getScheduler().runTask(instance, tk ->{
                    AnimationTools.fill(loc1, loc2, Material.PRISMARINE_WALL);
                });
            });

        }
    }

    public void registerElevators(){
        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");
        var loc1 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR1_LOC1"), world);
        var loc2 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR1_LOC2"), world);

        elevators.put(ElevatorType.ONE, new Elevator(loc1, loc2));

        loc1 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR2_LOC1"), world);
        loc2 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR2_LOC2"), world);

        elevators.put(ElevatorType.TWO, new Elevator(loc1, loc2));

        loc1 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR3_LOC1"), world);
        loc2 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR3_LOC2"), world);

        elevators.put(ElevatorType.THREE, new Elevator(loc1, loc2));

        loc1 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR_DOLL_LOC1"), world);
        loc2 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR_DOLL_LOC2"), world);

        elevators.put(ElevatorType.DOLL, new Elevator(loc1, loc2));

        loc1 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR_POTATO_LOC1"), world);
        loc2 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR_POTATO_LOC2"), world);

        elevators.put(ElevatorType.POTATO, new Elevator(loc1, loc2));

        loc1 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR_HIDE_SEEK_LOC1"), world);
        loc2 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR_HIDE_SEEK_LOC2"), world);

        elevators.put(ElevatorType.HIDE_SEEK, new Elevator(loc1, loc2));

        loc1 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR_CHICKEN_LOC1"), world);
        loc2 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR_CHICKEN_LOC2"), world);

        elevators.put(ElevatorType.CHICKEN1, new Elevator(loc1, loc2));

        loc1 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR2_CHICKEN_LOC1"), world);
        loc2 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR2_CHICKEN_LOC2"), world);

        elevators.put(ElevatorType.CHICKEN2, new Elevator(loc1, loc2));

        loc1 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR_EXIT1_LOC1"), world);
        loc2 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR_EXIT1_LOC2"), world);

        elevators.put(ElevatorType.EXIT1, new Elevator(loc1, loc2));

        loc1 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR_EXIT2_LOC1"), world);
        loc2 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR_EXIT2_LOC2"), world);

        elevators.put(ElevatorType.EXIT2, new Elevator(loc1, loc2));

        loc1 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR_GOCHICKEN_LOC1"), world);
        loc2 = AnimationTools.parseLocation(specialObjects.get("ELEVATOR_GOCHICKEN_LOC2"), world);

        elevators.put(ElevatorType.GOCHICKEN, new Elevator(loc1, loc2));

        loc1 = AnimationTools.parseLocation(specialObjects.get("GLASS_ELEVATOR_LOC1"), world);
        loc2 = AnimationTools.parseLocation(specialObjects.get("GLASS_ELEVATOR_LOC2"), world);

        elevators.put(ElevatorType.GLASS, new Elevator(loc1, loc2));

        loc1 = AnimationTools.parseLocation(specialObjects.get("VIP_MAIN_LOC1"), world);
        loc2 = AnimationTools.parseLocation(specialObjects.get("VIP_MAIN_LOC2"), world);

        elevators.put(ElevatorType.VIP_MAIN, new Elevator(loc1, loc2));

        loc1 = AnimationTools.parseLocation(specialObjects.get("VIP_DOLL_LOC1"), world);
        loc2 = AnimationTools.parseLocation(specialObjects.get("VIP_DOLL_LOC2"), world);

        elevators.put(ElevatorType.VIP_DOLL, new Elevator(loc1, loc2));

        loc1 = AnimationTools.parseLocation(specialObjects.get("VIP_ADMIN_LOC1"), world);
        loc2 = AnimationTools.parseLocation(specialObjects.get("VIP_ADMIN_LOC2"), world);

        elevators.put(ElevatorType.VIP_ADMIN, new Elevator(loc1, loc2));

        loc1 = AnimationTools.parseLocation(specialObjects.get("VIP_GLASS_LOC1"), world);
        loc2 = AnimationTools.parseLocation(specialObjects.get("VIP_GLASS_LOC2"), world);

        elevators.put(ElevatorType.VIP_GLASS, new Elevator(loc1, loc2));

        loc1 = AnimationTools.parseLocation(specialObjects.get("VIP_ROPE_LOC1"), world);
        loc2 = AnimationTools.parseLocation(specialObjects.get("VIP_ROPE_LOC2"), world);

        elevators.put(ElevatorType.VIP_ROPE, new Elevator(loc1, loc2));

        loc1 = AnimationTools.parseLocation(specialObjects.get("MAIN_ROOM_POS1"), world);
        loc2 = AnimationTools.parseLocation(specialObjects.get("MAIN_ROOM_POS2"), world);

        elevators.put(ElevatorType.MAIN_ROOM, new Elevator(loc1, loc2));

        

    }
}
