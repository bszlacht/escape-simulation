import java.util.ArrayList;
import java.util.Random;
public class Point {

	public ArrayList<Point> neighbors;
	public static Integer []types ={0,1,2,3,4};
	public int type;
	public int staticField;
	public boolean isPedestrian;
	boolean blocked = false;
	boolean smoked = false;
	int smokedCounter;

	public Point() {
		type=0;
		staticField = 100000;
		neighbors= new ArrayList<Point>();
		smokedCounter = 0;
	}

	
	public void clear() {
		staticField = 100000;
	}

	public boolean calcStaticField() {
		int n = neighbors.size();
		int smallest = 10000;
		for(int i = 0; i < n; i++){
			if(neighbors.get(i).staticField < smallest){
				smallest = neighbors.get(i).staticField;
			}
		}
		if(this.staticField > smallest + 1){
			this.staticField = smallest + 1;
			return true;
		}
		return false;
	}
	
	public void move(){
		if(blocked){
			return;
		}
		if(this.type == 4){
			return;
		}
		if(smoked){
			Random random = new Random();
			int x = random.nextInt(4);
			if(x != 1){
				return; // potknal sie w dymie, traci kolejke
			}
		}
		int n = neighbors.size();
		int smallest = 10000;
		int idx = -1;
		ArrayList<Integer> Acc = new ArrayList<>();
		if(this.isPedestrian){
			for(int i = 0; i < n; i++){
				if(neighbors.get(i).staticField <= smallest && neighbors.get(i).type != 1 && !neighbors.get(i).isPedestrian && neighbors.get(i).type != 4){
					if(neighbors.get(i).staticField < smallest){
						Acc.clear();
						idx = i;
						smallest = neighbors.get(i).staticField;
					}
					Acc.add(i);
				}
			}
		}

		if(idx != -1){
			int AccLength = Acc.size();
			Random rand = new Random();
			idx = Acc.get(rand.nextInt(AccLength));
			if(neighbors.get(idx).type != 2){
				neighbors.get(idx).isPedestrian = true;
				neighbors.get(idx).blocked = true;
				this.isPedestrian = false;
			}
			else if(neighbors.get(idx).type == 2){
				this.isPedestrian = false;
				this.type = 0;
			}
		}
	}

	public void addNeighbor(Point nei) {
		neighbors.add(nei);
	}

}