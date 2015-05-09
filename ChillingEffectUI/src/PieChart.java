import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import processing.core.PApplet;

public class PieChart extends PApplet {
	//Piechart for topics
	ArrayList<Float> angles = new ArrayList<Float>();
	ArrayList<String> topicText = new ArrayList<String>();
	Boolean savePics = true;
	Spike spike = new Spike();
	ArrayList<String> pieString = new ArrayList<String>();
	int[] color = { 127, 201, 127, 190, 174, 212, 253, 192, 134 };

	public void setup() {
		size(300, 500);
		noStroke();
		readFile();
		smooth();

	}

	public void setPieArray(String name) {
		name = name.replaceAll(",", " ");
		angles.clear();
		topicText.clear();
		int num=-1;
		for(int i=0;i<pieString.size();i++){
			if(name.equals(pieString.get(i).split(",")[0])){
				num = i;
				break;
			}
		}
		String[] pieArray = pieString.get(num).split(",");
		for (int i = 0; i < (pieArray.length - 4) / 2; i++) {
			angles.add(Float.parseFloat(pieArray[5 + i * 2]) * 360);
			topicText.add(pieArray[6 + i * 2]);
		}
	}

	public void readFile() {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("data/topics.txt"));

			String line = br.readLine();
			while (line != null) {
				pieString.add(line);
				line = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void draw() {
		background(250);
		pieChart(100, angles);
		fill(0);
		text("HOT TOPICS", 120, 20);
	}

	void pieChart(float diameter, ArrayList<Float> data) {
		float lastAngle = 0;
		for (int i = 0; i < data.size(); i++) {
			fill(color[3 * i], color[3 * i + 1], color[3 * i + 2]);
			rect(20, 160 + i * 100, 20, 10);
			arc(width / 2, 80, diameter, diameter, lastAngle, lastAngle
					+ radians(angles.get(i)));
			lastAngle += radians(angles.get(i));
			fill(0);

			text(topicText.get(i), 50, 150 + 100 * i, 200, 80);

		}
	}

}
