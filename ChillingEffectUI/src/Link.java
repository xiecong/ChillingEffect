
public class Link{
	//link of the node link graph
	Node v;
	Node u;
	FDP canvas = null;
	String count;
	float r;

	Link(Node _s, Node _e, String count, FDP canvas) {
		v = _s;
		u = _e;
		this.count = count;
		this.canvas = canvas;
		r = (float) (Math.random()*40);

	}

	public void draw() {
		canvas.stroke(r+180, r+180, r+180);
		if(!count.equals("")){
			canvas.strokeWeight((float) (3*Math.log(2 + Integer.valueOf(count)/50.0)));
		}else{
			canvas.noStroke();
		}
		canvas.bezier(v.pos.x, v.pos.y, v.oldpos[2].x, v.oldpos[2].y, u.oldpos[2].x,
				u.oldpos[2].y, u.pos.x, u.pos.y);
		canvas.text(count, 0.3f*v.pos.x+0.7f*u.pos.x, 0.3f*v.pos.y+0.7f*u.pos.y);
		canvas.noStroke();
	}
}
