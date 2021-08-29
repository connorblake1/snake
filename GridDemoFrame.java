import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class GridDemoFrame extends JFrame implements Constants
{
	GridDemoPanel thePanel;
	JLabel scoreLabel, messageLabel;
	public GridDemoFrame()
	{
		super("Flow");
		
		setSize(600,600+24+16);
		
		this.getContentPane().setLayout(new BorderLayout());
		thePanel = new GridDemoPanel(this);
		scoreLabel = new JLabel("Current Highest Score: 0");
		messageLabel = new JLabel("");
		Box southPanel = Box.createHorizontalBox();
		
		this.getContentPane().add(thePanel,BorderLayout.CENTER);
		this.getContentPane().add(southPanel, BorderLayout.SOUTH);
		southPanel.add(Box.createHorizontalStrut(10));
		southPanel.add(scoreLabel, BorderLayout.SOUTH);
		southPanel.add(Box.createGlue());
		southPanel.add(messageLabel, BorderLayout.SOUTH);
		southPanel.add(Box.createHorizontalStrut(10));
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);	
		thePanel.initiateAnimationLoop(); // uncomment this line if your program uses animation.
	}
	
	public void updateMessage(String message)
	{
		messageLabel.setText(message);
		messageLabel.repaint();
	}
	
	public void updateScore(int score)
	{
		scoreLabel.setText("Current Highest Score: "+score);
		scoreLabel.repaint();
	}
}
