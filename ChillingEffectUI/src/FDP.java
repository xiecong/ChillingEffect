import java.util.ArrayList;

import org.bson.BasicBSONObject;

import processing.core.PApplet;

import com.mongodb.AggregationOutput;
import com.mongodb.DBObject;

public class FDP extends PApplet {
	/* Force-Direct Graph of Sender and Recipient
	 */
	int canvasWidth = 900, canvasHeight = 500;
	float mouseMass = 30;

	boolean renderTrail = true;
	boolean mouseAttract = false;
	boolean mouseRepulse = true;
	boolean renderBalls = true;

	int vel = 5;
	ArrayList<Node> ns = new ArrayList<Node>();
	ArrayList<Link> as = new ArrayList<Link>();
	float k, k2;
	float tMass;
	int curn, nn;
	float curMass;
	MongoDBQuery mg = new MongoDBQuery();
	Spike spike = new Spike();

	public void setup() {
		size(canvasWidth, canvasHeight, JAVA2D);
		smooth();
		textAlign(CENTER, CENTER);
		curMass = mouseMass;
		tMass = 1;
		curn = 0;
	}

	public void mousePressed() {
		curMass = 0;
		tMass = 0;
		redraw();
	}

	float fa(float m1, float m2, float z) {
		return .00007f * pow(k - m1 - m2 - z, 2);
	}

	float fr(float m1, float m2, float z) {
		return 8.0f * pow(m1 + m2 + k, 2) / pow(z, 2);
	}

	public void draw() {
		background(255);

		if (tMass < 1) {
			tMass += .1;
			curMass = sin(PI * tMass) * 600 * (1 - tMass);
		}

		curMass = max(curMass, mouseMass);

		noStroke();
		for (int i = 0; i < ns.size(); i++) {
			Node u = (Node) ns.get(i);

			for (int j = 0; j < ns.size(); j++) {
				Node v = (Node) ns.get(j);
				if (u != v) {
					Vector2D delta = v.pos.sub(u.pos);
					if (delta.norm() != 0) {
						v.disp.addSelf(delta.versor().mult(
								fr(v.mass, u.mass, delta.norm())));
					}
				}
			}
		}

		for (int i = 0; i < as.size(); i++) {
			Link e = (Link) as.get(i);
			Vector2D delta = e.v.pos.sub(e.u.pos);
			if (delta.norm() != 0) {
				e.v.disp.subSelf(delta.versor().mult(
						fa(e.v.mass, e.u.mass, delta.norm())));
				e.u.disp.addSelf(delta.versor().mult(
						fa(e.v.mass, e.u.mass, delta.norm())));
			}
		}

		for (int i = 0; i < ns.size(); i++) {
			Node u = (Node) ns.get(i);
			if (mouseAttract) {
				Vector2D mousepos = new Vector2D(mouseX, mouseY);
				Vector2D delta = u.pos.sub(mousepos);
				if (delta.norm() != 0) {
					u.disp.subSelf(delta.versor().mult(
							fa(u.mass, curMass, delta.norm())));
					stroke(0, 0, 0, 20);
					line(u.pos.x, u.pos.y, mouseX, mouseY);
					noStroke();
				}
			}
			if (mouseRepulse) {
				Vector2D mousepos = new Vector2D(mouseX, mouseY);
				Vector2D delta = u.pos.sub(mousepos);
				if (delta.norm() < curMass + u.mass + 100) {
					u.disp.addSelf(delta.versor().mult(
							fr(u.mass, curMass, delta.norm())));
				}
			}
			u.update();
			u.costrain(0, width, 0, height);
		}

		for (int i = 0; i < as.size(); i++) {
			Link a = (Link) as.get(i);
			a.draw();
		}
		for (int i = 0; i < ns.size(); i++) {
			Node u = (Node) ns.get(i);
			if (renderTrail)
				u.setTrail(true);
			else
				u.setTrail(false);
			if (renderBalls)
				u.setBall(true);
			else
				u.setBall(false);
			u.draw();

		}
		noFill();
		stroke(200, 100, 0, 20);
		ellipse(mouseX, mouseY, curMass, curMass);

	}

	void drawTopic() {

	}

	int getIndex(String s) {
		for (int i = 0; i < ns.size(); i++) {
			if (s.equals(ns.get(i).name)) {
				return i;
			}
		}
		return -1;
	}

	public void setUserNet(String startTime, String endTime, String userName,
			String userType) {
		ns.clear();
		as.clear();

		Node n = new Node("", -1, 1, this);
		ns.add(n);

		AggregationOutput output = mg.getUserNet(startTime + "T00:00:00Z",
				endTime + "T00:00:00Z", userName, userType);

		for (DBObject result : output.results()) {
			int count = Integer.parseInt(result.get("count").toString());
			if (((count > 5 && userType.equals("recipient")) || userType
					.equals("sender"))
					&& ((BasicBSONObject) result.get("_id")).get("sender_name") != null) {
				// System.out.println(result);
				String senderName = ((BasicBSONObject) result.get("_id")).get(
						"sender_name").toString();
				String recipientName = ((BasicBSONObject) result.get("_id"))
						.get("recipient_name").toString();
				int senderIndex = getIndex(senderName);
				int recipientIndex = getIndex(recipientName);
				if (senderIndex == -1) {
					senderIndex = ns.size();
					Node n1 = new Node(senderName, 0, 10, this);
					ns.add(n1);
				}
				if (recipientIndex == -1) {
					recipientIndex = ns.size();
					Node n1 = new Node(recipientName, 1, 10, this);
					ns.add(n1);
					as.add(new Link(ns.get(0), n1, "", this));
				}
				as.add(new Link(ns.get(recipientIndex), ns.get(senderIndex),
						result.get("count").toString(), this));
				ns.get(recipientIndex).addSize(count);
				ns.get(senderIndex).addSize(count);
			}
		}
		output = null;
	}

	public void setGraph(String startTime, String endTime) {
		ns.clear();
		as.clear();

		Node n = new Node("", -1, 1, this);
		ns.add(n);

		AggregationOutput output = mg.getFrequentPair(startTime + "T00:00:00Z",
				endTime + "T00:00:00Z");

		for (DBObject result : output.results()) {
			int count = Integer.parseInt(result.get("count").toString());
			if (count > 80
					&& ((BasicBSONObject) result.get("_id")).get("sender_name") != null) {
				// System.out.println(result);
				String senderName = ((BasicBSONObject) result.get("_id")).get(
						"sender_name").toString();
				String recipientName = ((BasicBSONObject) result.get("_id"))
						.get("recipient_name").toString();
				int senderIndex = getIndex(senderName);
				int recipientIndex = getIndex(recipientName);
				if (senderIndex == -1) {
					senderIndex = ns.size();
					Node n1 = new Node(senderName, 0, 10, this);
					ns.add(n1);
				}
				if (recipientIndex == -1) {
					recipientIndex = ns.size();
					Node n1 = new Node(recipientName, 1, 10, this);
					ns.add(n1);
					as.add(new Link(ns.get(0), n1, "", this));
				}
				as.add(new Link(ns.get(recipientIndex), ns.get(senderIndex),
						result.get("count").toString(), this));
				ns.get(recipientIndex).addSize(count);
				ns.get(senderIndex).addSize(count);
			}
		}
		output = null;

	}
}
