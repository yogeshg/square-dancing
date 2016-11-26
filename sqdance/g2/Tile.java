package sqdance.g2;

import java.util.HashMap;
import java.util.Set;

import sqdance.sim.Point;
import sqdance.g2.DummyStrategy;

public class Tile {

    private int[] dancer_ids;
    public int num_dancers;
    private Point[] locations;
    // private HashMap<Integer, Integer> dancer_at_point;
    private TileType type;
    private Strategy med;

    public String toString() {
        return "Tile: "+type.name()+" num_dancers:"+num_dancers;
    }
    
    public Tile(TileType type, int[] dancer_ids, Point top_left_corner, Point bottom_right_corner) {
        this.dancer_ids = new int[dancer_ids.length];
        for(int i=0; i<dancer_ids.length; ++i) {
            this.dancer_ids[i] = dancer_ids[i];
        }
        this.num_dancers = dancer_ids.length;
        this.locations = new Point[this.num_dancers];
        this.type = type;
        if(this.type==TileType.DANCING) {
            med = new ZigZagStrategyMedium();
        } else {
            med = new DummyStrategy();
        }
        Point[] locs = med.generate_starting_locations(this.num_dancers);
        Point p;
        for(int i=0; i<this.num_dancers; ++i) {
            p = locs[i].add(top_left_corner);
            locations[i] = p;
            // System.out.println(p);
            // dancer_at_point.put(p,i);
        }

        // TODO generate points to dance at and the map of who is dancing where
    }

    public Point[] play(Point[] dancers, int[] scores,
            int[] partner_ids, int[] enjoyment_gained,
            int[] soulmate, int current_turn) {
        Point[] r = new Point[dancers.length];

        for(int i=0;i<dancers.length;++i) {
            r[i]=new Point(0,0);
        }
        if(this.type == TileType.DANCING) {
            Point[] dancers_sub = new Point[this.num_dancers];
            int[] scores_sub = new int[this.num_dancers];
            int[] partner_ids_sub = new int[this.num_dancers];
            int[] enjoyment_gained_sub = new int[this.num_dancers];
            int[] soulmate_sub = new int[this.num_dancers];

            for(int i=0; i<this.num_dancers; ++i) {
                dancers_sub[i] = dancers[dancer_ids[i]];
                scores_sub[i] = scores[dancer_ids[i]];
                partner_ids_sub[i] = partner_ids[dancer_ids[i]];
                enjoyment_gained_sub[i] = enjoyment_gained[dancer_ids[i]];
                soulmate_sub[i] = soulmate[dancer_ids[i]];
            }

            Point[] r_sub = med.play(dancers_sub, scores_sub, partner_ids_sub, enjoyment_gained_sub, soulmate_sub, current_turn);

            Point p;
            Point q;
            for(int i=0; i<this.num_dancers; ++i) {
                p = r_sub[i];
                q = locations[i];
                locations[i] = q.add(p);
                // dancer_at_point.put(locations[i],i);
                // dancer_at_point.remove(q);
                r[i] = p;
            }
        }
        return r;

    }

    public Point getPoint(int idx) {
        return locations[idx];
    }

    // public Set<Integer> getPointKeys() {
    //  return dancer_at_point.keySet();
    // }
    
    public int getDancerAt(int idx) {
        return this.dancer_ids[idx];
    }

    public void setDancerAt(int idx, int id) {
        this.dancer_ids[idx] = id;
    }
}

enum TileType {
    DANCING, RESTING
}
