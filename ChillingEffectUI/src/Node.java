import java.awt.Color;

public class Node{
	//node of the node link graph
	Vector2D pos;
	Vector2D disp;
	Vector2D[] oldpos;
	float mass;
	float newmass;
	Color mycolor;
	boolean trail;
	boolean ball;
	FDP canvas = null;
	String name;	
	int type;
	int posx = 800;
	int posy = 550;
	int size = 0;


	Node(String name, int type, float _mass, FDP canvas) {
		this.name = name;
		this.type = type;
		pos = new Vector2D((int)(Math.random()*posx), (int)(Math.random()*posy));
		disp = new Vector2D();
		mass = _mass;
		oldpos = new Vector2D[8];
		for (int i = 0; i < oldpos.length; i++)
			oldpos[i] = pos.clone();
		if (type == -1){
		mycolor = new Color(255, 255, 255,255);
		}else if (type == 0){
			mycolor = new Color(0, 0, 255);
		}else{
			mycolor = new Color(255, 0, 0);
		}
		ball = true;
		trail = true;
		this.canvas = canvas;
	}

	public void addSize(int a){
		size += a;
		mass = size/100 + 10;
	}
	void incrMass(float nm) {
		newmass = mass + nm;
	}

	void setBall(boolean ball) {
		this.ball = ball;
	}

	void setTrail(boolean trail) {
		this.trail = trail;
	}

	void update() {
		for (int i = oldpos.length - 1; i > 0; i--)
			oldpos[i] = oldpos[i - 1];
		oldpos[0] = pos.clone();
		pos.addSelf(disp);
		disp.clear();
	}

	public void draw() {
		if (mass < newmass)
			mass += .2f;
		if (trail)
			for (int i = 0; i < oldpos.length; i++) {
				float perc = (((float) oldpos.length - i) / oldpos.length);
				canvas.fill((mycolor.getRed()), (mycolor.getGreen()), (mycolor.getBlue()), perc * 240);
				canvas.ellipse(oldpos[i].x, oldpos[i].y, 2 * mass * perc, 2 * mass
						* perc);
			}
		if (ball) {
			canvas.fill((mycolor.getRed()), (mycolor.getGreen()), (mycolor.getBlue()));
			canvas.ellipse(pos.x, pos.y, mass * 2, mass * 2);
			canvas.fill(240, 240, 240);
			canvas.ellipse(pos.x, pos.y, mass * 1.5f, mass * 1.5f);
			//canvas.fill((mycolor.getRed()), (mycolor.getGreen()), (mycolor.getBlue()));
			//canvas.ellipse(pos.x, pos.y, mass, mass);
		}
		canvas.fill(0);
		canvas.text(name, pos.x, pos.y);
	}

	void costrain(float x0, float x1, float y0, float y1) {
		pos.x = canvas.min(x1, canvas.max(x0, pos.x));
		pos.y = canvas.min(y1, canvas.max(y0, pos.y));
	}

	public String toString() {
		return pos + "";
	}
}
