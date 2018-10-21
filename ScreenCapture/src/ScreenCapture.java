
/*
 * This software is OSI Certified Open Source Software
 * 
 * The MIT License (MIT)
 * Copyright 2000-2001 by Wet-Wired.com Ltd., Portsmouth England
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software. 
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 */

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class ScreenCapture extends JFrame implements ActionListener {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("_MM_dd_yyyy_HH_mm_ss");

	private JButton control;

	private static String fileName = "";

	public ScreenCapture() {

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				shutdown();
			}
		});

		control = new JButton("Start Screen Capture");
		control.setActionCommand("start");
		control.addActionListener(this);
		this.getContentPane().add(control, BorderLayout.WEST);

		/*
		 * text = new JLabel("Ready to record"); this.getContentPane().add(text,
		 * BorderLayout.SOUTH);
		 */

		this.pack();
		this.setVisible(true);
	}

	public void startRecording(String fileName) {

		setState(Frame.ICONIFIED);
		try {
			Robot robot = new Robot();

			String format = "jpg";

			fileName = fileName.replace(".jpg", sdf.format(new Timestamp(System.currentTimeMillis())) + ".jpg");

			Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
			ImageIO.write(screenFullImage, format, new File(fileName));

			System.out.println("A full screenshot saved to " + fileName);

		} catch (AWTException | IOException e1) {
		}

	}

	public void actionPerformed(ActionEvent ev) {
		if (ev.getActionCommand().equals("start")) {
			try {
				startRecording(fileName);
				control.setActionCommand("start");
				control.setText("Next Screen Capture");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {

		if ((args.length != 1)) {
			System.out.println("Usage: java -jar capture.jar <save_file.jpg>");
			return;
		}
		fileName = args[0];
		@SuppressWarnings("unused")
		ScreenCapture jRecorder = new ScreenCapture();

	}

	public void shutdown() {
		dispose();
	}

}
