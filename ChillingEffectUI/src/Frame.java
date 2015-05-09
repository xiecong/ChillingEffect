import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class Frame {
//System Interface

	JLabel statusLabel;
	JTextField startText;
	JTextField endText;
	JTextField nameText;
	JTextField typeText;
	int canvasWidth = 1200, canvasHeight = 800;
	FDP canvas = new FDP();;
	PieChart topicGraph = new PieChart();
	LineGraph lineGraph = new LineGraph();

	public Frame() {
	}


	public static void main(String[] argv) {
		Frame mf = new Frame();
		JFrame frame = mf.demo();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public JFrame demo() {
		JFrame frame = new JFrame("Chilling Effect");
		final Container pane = frame.getContentPane();
		JPanel queryPanel= new JPanel();
		queryPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		JLabel startLabel = new JLabel("start time");
		startText = new JTextField("2012-07-06");
		JLabel endLabel = new JLabel("end time");
		endText = new JTextField("2012-07-13");

		JLabel nameLabel = new JLabel("user name");
		nameText = new JTextField("Copyright Integrity International");//("Google, Inc.");

		JLabel typeLabel = new JLabel("user type");
		typeText = new JTextField("sender");
		
		this.statusLabel = new JLabel();
		JButton button = new JButton("query");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				statusLabel.setText("Submit Button clicked.");
				//canvas.setGraph(startText.getText(), endText.getText());
				canvas.setUserNet(startText.getText(), endText.getText(),nameText.getText(),typeText.getText());
				topicGraph.setPieArray(nameText.getText());
				lineGraph.setGraph(nameText.getText());
			}
		});
		FlowLayout flowLayout = new FlowLayout();
		queryPanel.setLayout(flowLayout);
		queryPanel.add(startLabel);
		queryPanel.add(startText);
		queryPanel.add(endLabel);
		queryPanel.add(endText);
		queryPanel.add(nameLabel);
		queryPanel.add(nameText);
		queryPanel.add(typeLabel);
		queryPanel.add(typeText);
		queryPanel.add(button);
		queryPanel.add(lineGraph);
		pane.add(queryPanel, BorderLayout.NORTH);
		pane.add(topicGraph, BorderLayout.EAST);
		pane.add(canvas, BorderLayout.CENTER);
		frame.setSize(canvasWidth, canvasHeight);
		canvas.init();
		topicGraph.init();
		lineGraph.init();
		return frame;
	}
}
