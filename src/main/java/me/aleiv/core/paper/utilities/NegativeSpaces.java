package me.aleiv.core.paper.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class NegativeSpaces {

    private static HashMap<Integer, String> negativeSpaces = new HashMap<>();
    
    private static int getMax(int i){
        var nums = negativeSpaces.keySet().stream().filter(ne -> ne > 0 && ne <= i).mapToInt(v -> v).max();

        return nums.isPresent() ? nums.getAsInt() : 0;
    }

    public static String get(int number){

        if(number == 0) return "";

        var neg = number < 0;
        var n = Math.abs(number);
        final var fn = n;
        
        var nums = negativeSpaces.keySet().stream().filter(nu -> nu > 0 && nu <= fn).collect(Collectors.toList());
        List<Integer> count = new ArrayList<>();
        var negativeSpace = new StringBuilder();

        while(!nums.isEmpty()){
            var r = getMax(n);

            n -= r;
            count.add(r);
            final var fn2 = n;
            nums.clear();
            nums = negativeSpaces.keySet().stream().filter(nu -> nu > 0 && nu <= fn2).collect(Collectors.toList());

        }

        if(neg){
            for (var i : count) {
                negativeSpace.append(negativeSpaces.get(i-i*2));
            }
        }else{
            for (var i : count) {
                negativeSpace.append(negativeSpaces.get(i));
            }
        }

        return negativeSpace.toString();

    }

    public static void registerCodes(){
        negativeSpaces.clear();
        negativeSpaces.put(-1, Character.toString('\uF801'));
        negativeSpaces.put(-2, Character.toString('\uF802'));
        negativeSpaces.put(-3, Character.toString('\uF803'));
        negativeSpaces.put(-4, Character.toString('\uF804'));
        negativeSpaces.put(-5, Character.toString('\uF805'));
        negativeSpaces.put(-6, Character.toString('\uF806'));
        negativeSpaces.put(-7, Character.toString('\uF807'));
        negativeSpaces.put(-8, Character.toString('\uF808'));

        negativeSpaces.put(-16, Character.toString('\uF809'));
        negativeSpaces.put(-32, Character.toString('\uF80A'));
        negativeSpaces.put(-64, Character.toString('\uF80B'));
        negativeSpaces.put(-128, Character.toString('\uF80C'));
        negativeSpaces.put(-256, Character.toString('\uF80D'));
        negativeSpaces.put(-512, Character.toString('\uF80E'));
        negativeSpaces.put(-1024, Character.toString('\uF80F'));

        negativeSpaces.put(1, Character.toString('\uF821'));
        negativeSpaces.put(2, Character.toString('\uF822'));
        negativeSpaces.put(3, Character.toString('\uF823'));
        negativeSpaces.put(4, Character.toString('\uF824'));
        negativeSpaces.put(5, Character.toString('\uF825'));
        negativeSpaces.put(6, Character.toString('\uF826'));
        negativeSpaces.put(7, Character.toString('\uF827'));
        negativeSpaces.put(8, Character.toString('\uF828'));

        negativeSpaces.put(16, Character.toString('\uF829'));
        negativeSpaces.put(32, Character.toString('\uF82A'));
        negativeSpaces.put(64, Character.toString('\uF82B'));
        negativeSpaces.put(128, Character.toString('\uF82C'));
        negativeSpaces.put(256, Character.toString('\uF82D'));
        negativeSpaces.put(512, Character.toString('\uF82E'));
        negativeSpaces.put(1024, Character.toString('\uF82F'));
        
    }
    
}
