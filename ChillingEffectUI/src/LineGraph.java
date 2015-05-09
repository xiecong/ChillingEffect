import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import processing.core.PApplet;

public class LineGraph extends PApplet {
//line chart for anormaly score and num of notices
	String[] calendar = { "2010-01-01", "2012-12-17", "2011-12-02",
			"2012-11-16", "2013-11-01", "2014-10-17" };
	Float[] anomalyScore = new Float[250];
	Float[] noticeNum = new Float[250];
	float anomalyScoreScale = 1;
	float noticeNumScale = 1;
	ArrayList<String> dic = new ArrayList<String>();
	int canvasWith =1200;

	public void setup() {
		size(canvasWith, 200);
		smooth();
		// An array of random values

		for (int i = 0; i < anomalyScore.length; i++) {
			anomalyScore[i] = noticeNum[i] = 0f;
		}
	}

	public void setGraph(String name) {
		int i = getNameIndex(name);
		if (i != -1) {
			readFile("data/individual/" + i + "AnomalyScore", anomalyScore);
			readFile("data/individual/" + i + "Data", noticeNum);
			countScale();
		}
	}

	public void countScale() {
		for (int i = 0; i < anomalyScore.length; i++) {
			if (anomalyScoreScale < anomalyScore[i]) {
				anomalyScoreScale = anomalyScore[i];
			}
			if (noticeNumScale < noticeNum[i]) {
				noticeNumScale = noticeNum[i];
			}
		}
		anomalyScoreScale = 130 / anomalyScoreScale;
		noticeNumScale = 130 / noticeNumScale;
	}

	public int getNameIndex(String name) {
		BufferedReader br;

		int reval = -1;
		try {
			br = new BufferedReader(new FileReader("data/filenameDic"));

			String line = br.readLine();
			while (line != null) {
				dic.add(line);
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

		for (int i = 0; i < dic.size(); i++) {
			String s = dic.get(i).split("_")[0];
			if (name.equals(s)) {
				reval = Integer.valueOf(dic.get(i).split("\t")[1]);
				break;
			}
		}
		return reval;

	}

	public void readFile(String fileName, Float[] v) {
		int i = 0;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(fileName));

			String line = br.readLine();
			while (line != null) {
				v[i] = Float.valueOf(line);
				line = br.readLine();
				i++;
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

		background(240);
		// Draw lines connecting all points
		fill(0);
		stroke(0);
		for (int i = 0; i < calendar.length; i++) {
			text(calendar[i], 20 + (canvasWith/5.5f) * i, 130);
			line(10 + (canvasWith/5.5f) * i, 140, 10 + (canvasWith/5.5f) * i, 130);
		}

		for (int i = 0; i < 4; i++) {
			line(10, 110 - i * 30, 14, 110 - i * 30);
			text((int) (30 * (i + 1) * anomalyScoreScale), 15, 120 - i * 30);
			line((canvasWith -10), 110 - i * 30, (canvasWith -14), 110 - i * 30);
			text((int) (30 * (i + 1) * noticeNumScale), (canvasWith -30), 120 - i * 30);
		}
		line(10, 0, 10, 140);
		line((canvasWith -10), 0, (canvasWith -10), 140);

		line(10, 140, (canvasWith -10), 140);

		for (int i = 0; i < anomalyScore.length - 1; i++) {
			stroke(255, 0, 0);
			line(10 + i * (canvasWith -20) / anomalyScore.length, 140 - anomalyScore[i] * anomalyScoreScale, 10
					+ (i + 1) * (canvasWith -20) / anomalyScore.length, 140 - anomalyScore[i + 1]
					* anomalyScoreScale);

			stroke(0, 0, 255);
			line(10 + i * (canvasWith -20) / noticeNum.length, 140 - noticeNum[i] * noticeNumScale,
					10 + (i + 1) * (canvasWith -20) / noticeNum.length, 140 - noticeNum[i + 1]
							* noticeNumScale);
		}
		stroke(255, 0, 0);
		line(55, 25, 40, 25);
		stroke(0, 0, 255);
		line(55, 45, 40, 45);
		stroke(0);

		text("Abnormaly Score", 60, 30);
		text("Number of Notices", 60, 50);

		text("Number of Notices", 10, 10);
		text("Abnormaly Score", canvasWith-110, 10);

	}
}
