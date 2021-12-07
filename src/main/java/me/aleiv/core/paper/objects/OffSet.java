package me.aleiv.core.paper.objects;

import lombok.Data;

@Data
public class OffSet {
    
    double x;
    double y;
    double z;

    public OffSet(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;

    }
}
