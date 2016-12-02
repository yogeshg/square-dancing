package sqdance.g2;

import sqdance.sim.Point;

import java.io.*;
import java.util.*;

// "Round" table approach

public class RoundTablePlayer implements sqdance.sim.Player {


    // random generator
    private Random random = null;

	//private final double cell_range = 0.002;
	//private final double grid_length = 0.5 + 3 * cell_range;
	private final double grid_length = 0.5;
	private final double fluctuation = 0.001;


	private Point[][] round_table;


    // simulation parameters
    private int d = -1;
    private double room_side = -1;

	// permutation 0: even; 1: odd
	private int permutation = 0;
	// mode 1: detect; mode 0: move
	private int mode = 0;
	// In mode 1: target of each player
	private Point[] target;

	// Indicate whether a player is in the round table
	private boolean[] in_round_table;
	// player at each position
	private List<Integer> round_table_list;
	// position of each player
	private int[] position;
	// id of a player at certain position
	private int[] occupier;
	// used in max flow part
	private boolean[] vis;

    //private int[] idle_turns;

    // init function called once with simulation parameters before anything else is called
    public void init(int d, int room_side) {
		this.d = d;
		this.room_side = (double) room_side;

		if (d > 910)
			round_table = Utils.generate_round_table((double)room_side, (double)room_side, grid_length, fluctuation);
		else
			round_table = Utils.generate_round_table_double_spiral_line((double)room_side, (double)room_side, grid_length, fluctuation);

		random = new Random();
		occupier = new int[round_table.length];
		vis = new boolean[round_table.length];

		for (int i = 0; i < round_table.length; ++ i)
			occupier[i] = -1;

		in_round_table = new boolean[d];
		target = new Point[d];
		round_table_list = new ArrayList<Integer>();
		position = new int[d];
    }

    // setup function called once to generate initial player locations
    // note the dance caller does not know any player-player relationships, so order doesn't really matter in the Point[] you return. Just make sure your player is consistent with the indexing

    public Point[] generate_starting_locations(int d) {
    	init(d, 20);
		Point[] L  = new Point [d];
		for (int i = 0 ; i < d ; ++i) {
			L[i] = round_table[i][1 - (i % 2)];
			target[i] = L[i];
			position[i] = i;
			round_table_list.add(i);
			in_round_table[i] = true;
		}
		return L;
    }

    // play function
    // dancers: array of locations of the dancers
    // scores: cumulative score of the dancers
    // partner_ids: index of the current dance partner. -1 if no dance partner
    // enjoyment_gained: integer amount (-5,0,3,4, or 6) of enjoyment gained in the most recent 6-second interval
    public Point[] play(Point[] dancers, int[] scores, int[] partner_ids, int[] enjoyment_gained, int[] soulmate, int turn, int[][] remenj) {

		Point[] instructions = new Point[d];

		//Default move: stay chill
		for (int i = 0; i < d; ++ i)
			instructions[i] = new Point(0, 0);

		if (mode == 0) {
			// Move round
			boolean finished = true;
			for (int i = 0; i < d; ++ i) {
				if (Utils.distance(target[i], dancers[i]) <= 1e-8) continue;
				if (in_round_table[i]) finished = false;

				//System.err.println("Player " + i + " moving from (" + dancers[i].x + "," + dancers[i].y + ") to (" + target[i].x + "," + target[i].y + ")");
			}
			if (finished) mode = 1;
		} else {
			// Detect soulmate
			List<Integer> found = new ArrayList<Integer>();
			for (int i = permutation; i + 1 < round_table_list.size(); i += 2) {
				int l = round_table_list.get(i);
				int r = round_table_list.get(i + 1);

				if (partner_ids[l] != r) {
					// Assessment
					//System.err.println("Fatal #" + i + ": " + l + "(position " + position[l] + ") isn't dancing with " + r + "(position " + position[r] + ")");
					//System.err.println("l: (" + dancers[l].x + "," + dancers[l].y + ") r: (" + dancers[r].x + "," + dancers[r].y + ")");

					/*
					for (int k = 0; k < round_table_list.size(); ++ k) {
						int p = round_table_list.get(k);
						System.err.println( k + " (" + dancers[p].x + "," + dancers[p].y + ")");
					}
					*/
				}

				if (enjoyment_gained[l] == 6) {
					// Soul mate found!
					found.add(l); found.add(r);
				}
			}
			
			int oldd = round_table_list.size();
			int newd = round_table_list.size() - found.size();

			if (found.size() > 0) {
				Collections.reverse(found);
				for (int i = 0; i < found.size(); i += 2) {
					// For each pair of soul mates, find a suitable place for them
					int l = found.get(i), r = found.get(i + 1);
					in_round_table[l] = in_round_table[r] = false;

					Arrays.fill(vis, false);
					int dst = settleSoulMate(newd, position[l]);

					if (dst == -1) {
						for (int p = newd; p < round_table.length; p += 2) {
							if (occupier[p] != -1 || occupier[p + 1] != -1) continue;
							double dd = Utils.distance(dancers[l], round_table[p][1]);
							if (dst == -1 || dd < Utils.distance(dancers[l], round_table[dst][1]))
								dst = p;
						}
					}
					//System.err.println(i + " " + dst + " " + distance(dancers[l], round_table[dst][1]));

					target[l] = round_table[dst][1];
					target[r] = round_table[dst + 1][0];
					occupier[dst] = l; occupier[dst + 1] = r;
				}

				round_table_list.removeAll(found);
			}

			// Apply a permutation
			for (int i = permutation; i + 1 < round_table_list.size(); i += 2) {
				int l = round_table_list.get(i);
				int r = round_table_list.get(i + 1);

				position[l] = i + 1; position[r] = i;
				//System.err.println("Swapping " + l + " " + r);

				target[l] = round_table[i + 1][1];
				target[r] = round_table[i][0];

				round_table_list.set(i + 1, l);
				round_table_list.set(i, r);
			}

			if (permutation == 1 && newd > 0) {
				int i = round_table_list.get(newd - 1);
				target[i] = round_table[newd - 1][0];
				position[i] = newd - 1;
			}

			permutation = 1 - permutation;

			// permutation
			mode = 0;
		}

		// Move towards the target
		for (int i = 0; i < d; ++ i)
			instructions[i] = Utils.getDirection(target[i], dancers[i]);

		return instructions;
	}

	// Soul mate origin and origin + 1, find an empty place after offset
	// use max flow approach
	private int settleSoulMate(int offset, int origin) {
		vis[origin] = true;
		int dst = -1;
		for (int i = offset; i + 1 < round_table.length; i += 2) {
			double dd = Utils.distance(round_table[origin][1], round_table[i][1]);
			if (dst == -1 || dd < Utils.distance(round_table[origin][1], round_table[dst][1]))
				dst = i;
		}
		//System.err.println("!" + origin + " " + dst + " " + distance(round_table[origin][1], round_table[dst][1]));
		//System.err.println(distance(round_table[origin][1], round_table[dst][1]));
		if (dst == -1 || vis[dst]) return -1;

		int k = settleSoulMate(offset, dst);
		if (k == -1) return -1;
		target[occupier[dst]] = round_table[k][1];
		target[occupier[dst + 1]] = round_table[k + 1][0];
		occupier[k] = occupier[dst];
		occupier[k + 1] = occupier[dst + 1];

		return dst;
	}
	public class Utils {
	public static Point add(Point a, Point b) {
		return new Point(a.x + b.x, a.y + b.y);
	}

	public static Point subtract(Point a, Point b) {
		return new Point(a.x - b.x, a.y - b.y);
	}

	public static Point multiply(Point a, double b) {
		return new Point(a.x * b, a.y * b);
	}

	public static double distance(Point a, Point b) {
		return Math.hypot(a.x - b.x, a.y - b.y);
	}

	public static Point direction(Point a) {
		double l = Math.hypot(a.x, a.y);
		if (l <= 2) return a;
		else return new Point(a.x / l, a.y / l);
	}

	public static Point getDirection(Point dst, Point src) {
		return direction(subtract(dst, src));
	}

	public static List<List<Point>> draw_grid(double h, double w, double radius) {
		List<List<Point>> grid = new ArrayList<List<Point>>();

		double shift = radius, lineGap = 2 * radius * Math.sin(Math.PI / 3.0);

		//System.out.println(radius + " " + lineGap);

		int mode = 0;
		for (double x = 0; x < h; x += lineGap) {
			List<Point> line = new ArrayList<Point>();
			for (double y = shift * mode; y < w; y += 2 * radius)
				line.add(new Point(x, y));
			grid.add(line);
			mode = 1 - mode;
			//System.err.println(radius + " " + w + " " + line.size() + " " + line.get(line.size() - 1).y);
		}
		return grid;
	}

	// spiral line from outward to inward
	public static Point[][] generate_round_table(double h, double w, double dist, double fluctuation) {
		List<List<Point>> grid = draw_grid(h, w, dist / 2.0 + fluctuation * 2);
		int numPoints = 0, width = 1000;
		for (List<Point> line : grid) {
			width = Math.min(line.size(), width);
		}
		numPoints = grid.size() * width;

		boolean vis[][] = new boolean[grid.size()][width];

		//System.out.println(width + " " + grid.size());
		//System.err.println(h + " " + w + " " + grid.size() + " " + width);

		if (numPoints % 2 == 1) -- numPoints;

		int dx = 0, dy = 1;
		int x = 0, y = 0;
		Point[][] ret = new Point[numPoints][2];

		ret[0][0] = grid.get(x).get(y);
		for (int i = 0; i < numPoints; ++ i) {
			vis[x][y] = true;

			int ox = x, oy = y;

			// Turn
			if (dx == 1 && (x + dx >= grid.size() || vis[x + dx][y + dy])) {
				dx = 0; dy = -1;
			} else if (dx == -1 && (x + dx < 0 || vis[x + dx][y + dy])) {
				dx = 0; dy = 1;
			} else if (dy == 1 && (y + dy >= width || vis[x + dx][y + dy])) {
				dx = 1; dy = 0;
			} else if (dy == -1 && (y + dy < 0 || vis[x + dx][y + dy])) {
				dx = -1; dy = 0;
			}
			x += dx; y += dy;

/*
			if (y - dy == 37) {
				System.err.println(i + " " + numPoints + " " + (x - dx) + " " + (y - dy));
				System.err.println(x + " " + y);
				System.err.println(grid.get(x - dx).size() + " " + grid.get(x).size());
			}*/

			if (i == numPoints - 1)
				ret[i][1] = grid.get(ox).get(oy);
			else {
				ret[i][1] = add(grid.get(ox).get(oy), multiply(getDirection(grid.get(x).get(y), grid.get(ox).get(oy)), 0.5 * fluctuation));
				ret[i + 1][0] = add(grid.get(x).get(y),
								multiply(getDirection(grid.get(ox).get(oy), grid.get(x).get(y)), 0.5 * fluctuation));
			}

		}

		for (int i = 0; i < numPoints; ++ i) {
			ret[i][0] = new Point(ret[i][0].y, ret[i][0].x);
			ret[i][1] = new Point(ret[i][1].y, ret[i][1].x);
		}

		return ret;
	}

	// double spiral line from outward to inward
	public static Point[][] generate_round_table_double_spiral_line(double h, double w, double dist, double fluctuation) {
		List<List<Point>> grid = draw_grid(h, w, dist / 2.0 + fluctuation * 2);
		int numPoints = 0, width = 0;
		for (List<Point> line : grid) {
			numPoints += line.size();
			width = Math.max(line.size(), width);
		}
		boolean vis[][] = new boolean[grid.size()][width];

		int dx = 0, dy = 1;
		int x = 0, y = 0;
		Point[][] ret = new Point[numPoints][2];
		int sep = 0;

		ret[0][0] = grid.get(x).get(y);
		for (int i = 0; i < numPoints; ++ i) {
			vis[x][y] = true;

			// Turn
			if (dx == 1 && (x + 2 * dx >= grid.size() || vis[x + 2 * dx][y + 2 * dy])) {
				dx = 0; dy = -1;
			} else if (dx == -1 && (x + 2 * dx < 0 || vis[x + 2 * dx][y + 2 * dy])) {
				dx = 0; dy = 1;
			} else if (dy == 1 && (y + 2 * dy >= grid.get(x).size() || vis[x + 2 * dx][y + 2 * dy])) {
				dx = 1; dy = 0;
			} else if (dy == -1 && (y + 2 * dy < 0 || vis[x + 2 * dx][y + 2 * dy])) {
				dx = -1; dy = 0;
			}

			if (x + 2 * dx < grid.size() && x + 2 * dx >= 0 && y + 2 * dy < grid.get(x).size() && y + 2 * dy >= 0 && vis[x + 2 * dx][y + 2 * dy]) {
				sep = i;
			}

			x += dx; y += dy;

			if (sep > 0) {
				ret[i][1] = grid.get(x - dx).get(y - dy);
				break;
			} else {
				ret[i][1] = add(grid.get(x - dx).get(y - dy), multiply(getDirection(grid.get(x).get(y), grid.get(x - dx).get(y - dy)), 0.5 * fluctuation));
				ret[i + 1][0] = add(grid.get(x).get(y),
								multiply(getDirection(grid.get(x - dx).get(y - dy), grid.get(x).get(y)), 0.5 * fluctuation));
			}
		}

		//System.err.println(sep);

		x = 0; y = grid.get(x).size() - 1;
		dx = 1; dy = 0;

		ret[numPoints - 1][1] = grid.get(x).get(y);
		for (int i = numPoints - 1; i > sep; -- i) {
			vis[x][y] = true;
			if (dx == 1 && (x + dx >= grid.size() || vis[x + dx][y + dy])) {
				dx = 0; dy = -1;
			} else if (dx == -1 && (x + dx < 0 || vis[x + dx][y + dy])) {
				dx = 0; dy = 1;
			} else if (dy == 1 && (y + dy >= grid.get(x).size() || vis[x + dx][y + dy])) {
				dx = 1; dy = 0;
			} else if (dy == -1 && (y + dy < 0 || vis[x + dx][y + dy])) {
				dx = -1; dy = 0;
			}
			x += dx; y += dy;

			ret[i][0] = add(grid.get(x - dx).get(y - dy), multiply(getDirection(grid.get(x).get(y), grid.get(x - dx).get(y - dy)), 0.5 * fluctuation));
			ret[i - 1][1] = add(grid.get(x).get(y),
							multiply(getDirection(grid.get(x - dx).get(y - dy), grid.get(x).get(y)), 0.5 * fluctuation));
		}

		for (int i = 0; i < numPoints; ++ i) {
			ret[i][0] = new Point(ret[i][0].y, ret[i][0].x);
			ret[i][1] = new Point(ret[i][1].y, ret[i][1].x);
		}

		return ret;
	}
}


}

