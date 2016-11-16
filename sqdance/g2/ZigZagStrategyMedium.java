package sqdance.g2;

import sqdance.sim.Point;

public class ZigZagStrategyMedium implements Strategy {
	
	@Override
	public Point[] generate_starting_locations() {
		return new ZigZagStrategySmall().generate_starting_locations();
	}

	@Override
<<<<<<< HEAD
	public Point[] play(Point[] dancers, int[] scores, 
			int[] partner_ids, int[] enjoyment_gained,
			int[] soulmate, int current_turn) {
=======
	public Point[] play(Point[] dancers, int[] scores,
			int[] partner_ids, int[] enjoyment_gained,
			int[] soulmate, int current_turn){
>>>>>>> aaee95b7de7d68b70f4e6a3ae561b0285ddb282f
		return null;
	}
}
