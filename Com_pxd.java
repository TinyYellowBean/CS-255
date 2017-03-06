import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;
import javax.swing.*;

import java.awt.event.*;

import javax.swing.event.*;
import javax.xml.bind.annotation.XmlSchemaType.DEFAULT;

// OK this is not best practice - maybe you'd like to create
// a volume data class?
// I won't give extra marks for that though.

public class Com_pxd extends JFrame {

	JButton mip_button, thumbnail_button; // an example button to switch to MIP
											// mode
	JLabel image_icon1; // using JLabel to display an image (check online
						// documentation)
	JLabel image_icon2; // using JLabel to display an image (check online
						// documentation)
	JLabel image_icon3; // top view
	JLabel image_icon4; // side view
	Label label1,label2,label3,label4,label5,label6;
	Panel panel1,panel2,panel3;
	JSlider zslice_slider, yslice_slider, xslice_slider, expand_slider1,
			expand_slider2, expand_slider3; // sliders to step through the
											// slices (z and y
											// directions) (remember 113 slices
											// in z direction
											// 0-112)
	BufferedImage image1, image2, image3, image4; // storing the image in memory
	short cthead[][][]; // store the 3D volume data set
	short min, max; // min/max value in the 3D volume data set

	/*
	 * This function sets up the GUI and reads the data set
	 */
	public void Example() throws IOException {
		// File name is hardcoded here - much nicer to have a dialog to select
		// it and capture the size from the user
		File file = new File("CThead");

		// Create a BufferedImage to store the image data
		image1 = new BufferedImage(256, 256, BufferedImage.TYPE_3BYTE_BGR);
		image2 = new BufferedImage(512, 512, BufferedImage.TYPE_3BYTE_BGR);
		image3 = new BufferedImage(256, 113, BufferedImage.TYPE_3BYTE_BGR);
		image4 = new BufferedImage(256, 113, BufferedImage.TYPE_3BYTE_BGR);

		// Read the data quickly via a buffer (in C++ you can just do a single
		// fread - I couldn't find the equivalent in Java)
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(file)));

		int i, j, k; // loop through the 3D data set

		min = Short.MAX_VALUE;
		max = Short.MIN_VALUE; // set to extreme values
		short read; // value read in
		int b1, b2; // data is wrong Endian (check wikipedia) for Java so we
					// need to swap the bytes around

		cthead = new short[113][256][256]; // allocate the memory - note this is
											// fixed for this data set
		// loop through the data reading it in
		for (k = 0; k < 113; k++) {
			for (j = 0; j < 256; j++) {
				for (i = 0; i < 256; i++) {
					// because the Endianess is wrong, it needs to be read byte
					// at a time and swapped
					b1 = ((int) in.readByte()) & 0xff; // the 0xff is because
														// Java does not have
														// unsigned types (C++
														// is so much easier!)
					b2 = ((int) in.readByte()) & 0xff; // the 0xff is because
														// Java does not have
														// unsigned types (C++
														// is so much easier!)
					read = (short) ((b2 << 8) | b1); // and swizzle the bytes
														// around
					if (read < min)
						min = read; // update the minimum
					if (read > max)
						max = read; // update the maximum
					cthead[k][j][i] = read; // put the short into memory (in C++
											// you can replace all this code
											// with one fread)
				}
			}
		}
		System.out.println(min + " " + max); // diagnostic - for CThead this
												// should be -1117, 2248
		// (i.e. there are 3366 levels of grey (we are trying to display on 256
		// levels of grey)
		// therefore histogram equalization would be a good thing

		// Set up the simple GUI
		// First the container:
		panel1 = new Panel();
		
		Container container = getContentPane();
		container.setLayout(new FlowLayout());
		
		panel1.setLayout(new GridLayout(4,3));
		
		// Then our image (as a label icon)
		image_icon1 = new JLabel(new ImageIcon(image1), JLabel.CENTER);
		container.add(image_icon1);

		image_icon3 = new JLabel(new ImageIcon(image3), JLabel.CENTER);
		container.add(image_icon3);
		image_icon4 = new JLabel(new ImageIcon(image4), JLabel.CENTER);
		container.add(image_icon4);

		image_icon2 = new JLabel(new ImageIcon(image2), JLabel.CENTER);
		container.add(image_icon2);
        label1 = new Label("control_top");
        panel1.add(label1);
        label2 = new Label("control_front");
        panel1.add(label2);
        label3 = new Label("control_side");
        panel1.add(label3);
		// Then the invert button
		mip_button = new JButton("MIP");
		container.add(mip_button);
		thumbnail_button = new JButton("Thumbnail");
		container.add(thumbnail_button);
		// Zslice slider
		zslice_slider = new JSlider(0, 112);
		panel1.add(zslice_slider);
		yslice_slider = new JSlider(0, 255);
		panel1.add(yslice_slider);
		xslice_slider = new JSlider(0, 255);
		panel1.add(xslice_slider);
		label4 = new Label("Resize_top");
	    panel1.add(label4);
	    label5 = new Label("Resize_front");
	    panel1.add(label5);
	    label6 = new Label("Resize_side");
	    panel1.add(label6);
		expand_slider1 = new JSlider(0, 512);
		panel1.add(expand_slider1);
		expand_slider2 = new JSlider(0, 512);
		panel1.add(expand_slider2);
		expand_slider3 = new JSlider(0, 512);
		panel1.add(expand_slider3);
		panel1.setVisible(true);
		
		//container.add(rotation_slider);
		container.add(panel1);

		// Add labels (y slider as example)
		zslice_slider.setMajorTickSpacing(50);
		zslice_slider.setMinorTickSpacing(10);
		zslice_slider.setPaintTicks(true);
		zslice_slider.setPaintLabels(true);
		yslice_slider.setMajorTickSpacing(50);
		yslice_slider.setMinorTickSpacing(10);
		yslice_slider.setPaintTicks(true);
		yslice_slider.setPaintLabels(true);
		xslice_slider.setMajorTickSpacing(50);
		xslice_slider.setMinorTickSpacing(10);
		xslice_slider.setPaintTicks(true);
		xslice_slider.setPaintLabels(true);
		expand_slider1.setMajorTickSpacing(100);
		expand_slider1.setMinorTickSpacing(20);
		expand_slider1.setPaintTicks(true);
		expand_slider1.setPaintLabels(true);
		expand_slider2.setMajorTickSpacing(100);
		expand_slider2.setMinorTickSpacing(20);
		expand_slider2.setPaintTicks(true);
		expand_slider2.setPaintLabels(true);
		expand_slider3.setMajorTickSpacing(100);
		expand_slider3.setMinorTickSpacing(20);
		expand_slider3.setPaintTicks(true);
		expand_slider3.setPaintLabels(true);
		
		// see
		// https://docs.oracle.com/javase/7/docs/api/javax/swing/JSlider.html
		// for documentation (e.g. how to get the value, how to display
		// vertically if you want)

		// Now all the handlers class
		GUIEventHandler handler = new GUIEventHandler();

		// associate appropriate handlers
		mip_button.addActionListener(handler);
		thumbnail_button.addActionListener(handler);
		yslice_slider.addChangeListener(handler);
		zslice_slider.addChangeListener(handler);
		xslice_slider.addChangeListener(handler);
		expand_slider1.addChangeListener(handler);
		expand_slider2.addChangeListener(handler);
		expand_slider3.addChangeListener(handler);
		//rotation_slider.addChangeListener(handler);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle bounds = new Rectangle(screenSize);

		// ... and display everything
		pack();
		setLocationRelativeTo(null);
		setBounds(bounds);
		setVisible(true);
	}

	/*
	 * This is the event handler for the application
	 */
	private class GUIEventHandler implements ActionListener, ChangeListener {
		BufferedImage combine1 = new BufferedImage(256, 256,
				BufferedImage.TYPE_3BYTE_BGR);
		BufferedImage combine2 = new BufferedImage(256, 113,
				BufferedImage.TYPE_3BYTE_BGR);
		BufferedImage combine3 = new BufferedImage(256, 113,
				BufferedImage.TYPE_3BYTE_BGR);
		Graphics g1 = combine1.getGraphics();
		Graphics g2 = combine2.getGraphics();
		Graphics g3 = combine3.getGraphics();
		// Change handler (e.g. for sliders)
		public void stateChanged(ChangeEvent e) {
			System.out.println(yslice_slider.getValue());
			System.out.println(zslice_slider.getValue());
			System.out.println(expand_slider1.getValue());
			// e.g. do something to change the image here
			if (e.getSource() == zslice_slider) {
				image1 = getImage1(image1, zslice_slider.getValue());
				image_icon1.setIcon(new ImageIcon(image1));
			}
			if (e.getSource() == yslice_slider) {

				image3 = getImage2(image3, yslice_slider.getValue());
				image_icon3.setIcon(new ImageIcon(image3));
			}
			if (e.getSource() == xslice_slider) {

				image4 = getImage3(image4, xslice_slider.getValue());
				image_icon4.setIcon(new ImageIcon(image4));
			}
			if (e.getSource() == expand_slider1) {
				image2 = new BufferedImage(512, 512,
						BufferedImage.TYPE_3BYTE_BGR);

				image2 = resizeImage1(image2, expand_slider1.getValue(),
						zslice_slider.getValue());
				image_icon2.setIcon(new ImageIcon(image2));

			}
			if (e.getSource() == expand_slider2) {
				image2 = new BufferedImage(512, 512,
						BufferedImage.TYPE_3BYTE_BGR);

				image2 = resizeImage2(image2, expand_slider2.getValue(),
						yslice_slider.getValue());
				image_icon2.setIcon(new ImageIcon(image2));

				// image_icon2.setIcon(null);

			}
			if (e.getSource() == expand_slider3) {
				image2 = new BufferedImage(512, 512,
						BufferedImage.TYPE_3BYTE_BGR);

				image2 = resizeImage3(image2, expand_slider3.getValue(),
						xslice_slider.getValue());
				image_icon2.setIcon(new ImageIcon(image2));

			}
			/*if (e.getSource() == rotation_slider) {
				image3 = MIP2_rotation(image3,rotation_slider.getValue());
				image_icon3.setIcon(new ImageIcon(image3));

			}*/
		}

		// action handlers (e.g. for buttons)
		public void actionPerformed(ActionEvent event) {
			if (event.getSource() == mip_button) {
				// e.g. do something to change the image here
				// e.g. call MIP function
				image1 = MIP1(image1); // (although mine is called MIP, it
										// doesn't do MIP)
				image3 = MIP2(image3);
				image4 = MIP3(image4);
				// Update image
				image_icon1.setIcon(new ImageIcon(image1));
				image_icon3.setIcon(new ImageIcon(image3));
				image_icon4.setIcon(new ImageIcon(image4));
			}
			if (event.getSource() == thumbnail_button) {

				image1 = new BufferedImage(256, 256,
						BufferedImage.TYPE_3BYTE_BGR);
				image3 = new BufferedImage(256, 113,
						BufferedImage.TYPE_3BYTE_BGR);
				image4 = new BufferedImage(256, 113,
						BufferedImage.TYPE_3BYTE_BGR);
				for (int i = 0; i < 113; i++) {

					image1 = thumbnail_Image_top(image1, i);
					if (i >= 0 && i <= 10) {
						g1.drawImage(image1, 24 * i, 0, null);
					}
					if (i >= 11 && i <= 21) {
						g1.drawImage(image1, 24 * (i - 11), 24, null);
					}
					if (i >= 22 && i <= 32) {
						g1.drawImage(image1, 24 * (i - 22), 48, null);
					}
					if (i >= 33 && i <= 43) {
						g1.drawImage(image1, 24 * (i - 33), 72, null);
					}
					if (i >= 44 && i <= 54) {
						g1.drawImage(image1, 24 * (i - 44), 96, null);
					}
					if (i >= 55 && i <= 65) {
						g1.drawImage(image1, 24 * (i - 55), 120, null);
					}
					if (i >= 66 && i <= 76) {
						g1.drawImage(image1, 24 * (i - 66), 144, null);
					}
					if (i >= 77 && i <= 87) {
						g1.drawImage(image1, 24 * (i - 77), 168, null);
					}
					if (i >= 88 && i <= 98) {
						g1.drawImage(image1, 24 * (i - 88), 192, null);
					}
					if (i >= 99 && i <= 109) {
						g1.drawImage(image1, 24 * (i - 99), 192, null);
					}
					if (i >= 110 && i <= 112) {
						g1.drawImage(image1, 24 * (i - 110), 216, null);
					}
					
				}
			       for(int i = 0 ;i<256;i=i+2){
	    	   
					image3 = thumbnail_Image_front(image3, i);
					if (i >= 0 && i <= 20) {
						g2.drawImage(image3, 24 * (i/2), 0, null);
					}
					if (i >= 22 && i <= 42) {
						g2.drawImage(image3, 24 * ((i/2) - 11), 8, null);
					}
					if (i >= 44 && i <= 64) {
						g2.drawImage(image3, 24 * ((i/2) - 22), 16, null);
					}
					if (i >= 66 && i <= 86) {
						g2.drawImage(image3, 24 * ((i/2) - 33), 24, null);
					}
					if (i >= 88 && i <= 108) {
						g2.drawImage(image3, 24 * ((i/2) - 44), 32, null);
					}
					if (i >= 110 && i <= 130) {
						g2.drawImage(image3, 24 * ((i/2) - 55), 40, null);
					}
					if (i >= 132 && i <= 152) {
						g2.drawImage(image3, 24 * ((i/2) - 66), 48, null);
					}
					if (i >= 154 && i <= 174) {
						g2.drawImage(image3, 24 * ((i/2) - 77), 56, null);
					}
					if (i >= 176 && i <= 196) {
						g2.drawImage(image3, 24 * ((i/2) - 88), 64, null);
					}
					if (i >= 198 && i <= 218) {
						g2.drawImage(image3, 24 * ((i/2) - 99), 72, null);
					}
					if (i >= 220 && i <= 240) {
						g2.drawImage(image3, 24 * ((i/2) - 110), 81, null);
					}
					if (i >= 242 && i <= 256) {
						g2.drawImage(image3, 24 * ((i/2) - 121), 90, null);
					}
					
				}
					
			       for(int i = 0 ;i<256;i=i+2){
			    	   
						image4 = thumbnail_Image_side(image4, i);
						if (i >= 0 && i <= 20) {
							g3.drawImage(image4, 24 * (i/2), 0, null);
						}
						if (i >= 22 && i <= 42) {
							g3.drawImage(image4, 24 * ((i/2) - 11), 8, null);
						}
						if (i >= 44 && i <= 64) {
							g3.drawImage(image4, 24 * ((i/2) - 22), 16, null);
						}
						if (i >= 66 && i <= 86) {
							g3.drawImage(image4, 24 * ((i/2) - 33), 24, null);
						}
						if (i >= 88 && i <= 108) {
							g3.drawImage(image4, 24 * ((i/2) - 44), 32, null);
						}
						if (i >= 110 && i <= 130) {
							g3.drawImage(image4, 24 * ((i/2) - 55), 40, null);
						}
						if (i >= 132 && i <= 152) {
							g3.drawImage(image4, 24 * ((i/2) - 66), 48, null);
						}
						if (i >= 154 && i <= 174) {
							g3.drawImage(image4, 24 * ((i/2) - 77), 56, null);
						}
						if (i >= 176 && i <= 196) {
							g3.drawImage(image4, 24 * ((i/2) - 88), 64, null);
						}
						if (i >= 198 && i <= 218) {
							g3.drawImage(image4, 24 * ((i/2) - 99), 72, null);
						}
						if (i >= 220 && i <= 240) {
							g3.drawImage(image4, 24 * ((i/2) - 110), 81, null);
						}
						if (i >= 242 && i <= 256) {
							g3.drawImage(image4, 24 * ((i/2) - 121), 90, null);
						}
						
					}						
				
				image_icon1.setIcon(new ImageIcon(combine1));
				image_icon3.setIcon(new ImageIcon(combine2));
				image_icon4.setIcon(new ImageIcon(combine3));
			}

		}

	}

	/*
	 * This function will return a pointer to an array of bytes which represent
	 * the image data in memory. Using such a pointer allows fast access to the
	 * image data for processing (rather than getting/setting individual pixels)
	 */
	public static byte[] GetImageData(BufferedImage image) {
		WritableRaster WR = image.getRaster();
		DataBuffer DB = WR.getDataBuffer();
		if (DB.getDataType() != DataBuffer.TYPE_BYTE)
			throw new IllegalStateException("That's not of type byte");

		return ((DataBufferByte) DB).getData();
	}

	/*
	 * This function shows how to carry out an operation on an image. It obtains
	 * the dimensions of the image, and then loops through the image carrying
	 * out the copying of a slice of data into the image.
	 */
	public BufferedImage MIP1(BufferedImage image) {
		// Get image dimensions, and declare loop variables
		int w = image.getWidth(), h = image.getHeight(), i, j, c, k;
		// Obtain pointer to data for fast processing
		byte[] data = GetImageData(image);
		float col;
		short datum = 0;
		short max1 = -112;
		// Shows how to loop through each pixel and colour
		// Try to always use j for loops in y, and i for loops in x
		// as this makes the code more readable
		for (j = 0; j < h; j++) {
			for (i = 0; i < w; i++) {
				max1 = -113;
				for (k = 0; k < 113; k++) {
					max1 = (short) Math.max(cthead[k][j][i], max1);
				}
				col = (255.0f * ((float) max1 - (float) min) / ((float) (max - min)));
				for (c = 0; c < 3; c++) {
					// and now we are looping through the bgr components of the
					// pixel
					// set the colour component c of pixel (i,j)
					data[c + 3 * i + 3 * j * w] = (byte) col;
				} // colour loop

			} // column loop

		} // row loop

		return image;
	}

	public BufferedImage MIP2(BufferedImage image) {
		// Get image dimensions, and declare loop variables
		int w = image.getWidth(), h = image.getHeight(), i, j, c, k;
		// Obtain pointer to data for fast processing
		byte[] data = GetImageData(image);
		float col;
		short datum = 0;
		short max1 = cthead[0][0][0];
		// Shows how to loop through each pixel and colour
		// Try to always use j for loops in y, and i for loops in x
		// as this makes the code more readable
		for (j = 0; j < h; j++) {
			for (i = 0; i < w; i++) {
				max1 = -113;
				for (k = 0; k < 256; k++) {

					max1 = (short) Math.max(cthead[j][k][i], max1);
				}
				col = (255.0f * ((float) max1 - (float) min) / ((float) (max - min)));
				for (c = 0; c < 3; c++) {
					// and now we are looping through the bgr components of the
					// pixel
					// set the colour component c of pixel (i,j)
					data[c + 3 * i + 3 * j * w] = (byte) col;
				} // colour loop
			} // column loop
			// row loop
		}
		return image;
	}

	public BufferedImage MIP3(BufferedImage image) {
		// Get image dimensions, and declare loop variables
		int w = image.getWidth(), h = image.getHeight(), i, j, c, k;
		// Obtain pointer to data for fast processing
		byte[] data = GetImageData(image);
		float col;
		short datum = 0;
		short max1;
		// Shows how to loop through each pixel and colour
		// Try to always use j for loops in y, and i for loops in x
		// as this makes the code more readable
		for (j = 0; j < h; j++) {
			for (i = 0; i < w; i++) {
				max1 = -113;
				for (k = 0; k < 256; k++) {
					// at this point (i,j) is a single pixel in the image
					// here you would need to do something to (i,j) if the image
					// size
					// does not match the slice size (e.g. during an image
					// resizing operation
					// If you don't do this, your j,i could be outside the array
					// bounds
					// In the framework, the image is 256x256 and the data set
					// slices are 256x256
					// so I don't do anything - this also leaves you something
					// to do for the assignment
					max1 = (short) Math.max(cthead[j][i][k], max1);
				}
				col = (255.0f * ((float) max1 - (float) min) / ((float) (max - min)));
				for (c = 0; c < 3; c++) {
					// and now we are looping through the bgr components of the
					// pixel
					// set the colour component c of pixel (i,j)
					data[c + 3 * i + 3 * j * w] = (byte) col;
				} // colour loop

			} // column loop
		} // row loop

		return image;
	}

	public BufferedImage getImage1(BufferedImage image, int value) {
		// Get image dimensions, and declare loop variables
		int w = image.getWidth(), h = image.getHeight(), i, j, c, k;
		// Obtain pointer to data for fast processing
		byte[] data = GetImageData(image);
		float col;
		short max1 = 0;
		short datum;
		// Shows how to loop through each pixel and colour
		// Try to always use j for loops in y, and i for loops in x
		// as this makes the code more readable
		for (j = 0; j < h; j++) {
			for (i = 0; i < w; i++) {
				// at this point (i,j) is a single pixel in the image
				// here you would need to do something to (i,j) if the image
				// size
				// does not match the slice size (e.g. during an image resizing
				// operation
				// If you don't do this, your j,i could be outside the array
				// bounds
				// In the framework, the image is 256x256 and the data set
				// slices are 256x256
				// so I don't do anything - this also leaves you something to do
				// for the assignment
				datum = cthead[value][j][i]; // get values from slice 76 (change
												// this in your assignment)
				// calculate the colour by performing a mapping from [min,max]
				// -> [0,255]
				col = (255.0f * ((float) datum - (float) min) / ((float) (max - min)));
				for (c = 0; c < 3; c++) {
					// and now we are looping through the bgr components of the
					// pixel
					// set the colour component c of pixel (i,j)
					data[c + 3 * i + 3 * j * w] = (byte) col;
				} // colour loop
			} // column loop
		} // row loop

		return image;
	}

	public BufferedImage getImage2(BufferedImage image, int value) {
		// Get image dimensions, and declare loop variables
		int w = image.getWidth(), h = image.getHeight(), i, j, c, k;
		// Obtain pointer to data for fast processing
		byte[] data = GetImageData(image);
		float col;
		short datum;
		// Shows how to loop through each pixel and colour
		// Try to always use j for loops in y, and i for loops in x
		// as this makes the code more readable
		for (j = 0; j < h; j++) {
			for (i = 0; i < w; i++) {
				// at this point (i,j) is a single pixel in the image
				// here you would need to do something to (i,j) if the image
				// size
				// does not match the slice size (e.g. during an image resizing
				// operation
				// If you don't do this, your j,i could be outside the array
				// bounds
				// In the framework, the image is 256x256 and the data set
				// slices are 256x256
				// so I don't do anything - this also leaves you something to do
				// for the assignment
				datum = cthead[j][value][i]; // get values from slice 76 (change
												// this in your assignment)
				// calculate the colour by performing a mapping from [min,max]
				// -> [0,255]
				// System.out.println(datum);
				col = (255.0f * ((float) datum - (float) min) / ((float) (max - min)));
				for (c = 0; c < 3; c++) {
					// and now we are looping through the bgr components of the
					// pixel
					// set the colour component c of pixel (i,j)
					data[c + 3 * i + 3 * j * w] = (byte) col;
				} // colour loop
			} // column loop
		} // row loop

		return image;
	}

	public BufferedImage getImage3(BufferedImage image, int value) {
		// Get image dimensions, and declare loop variables
		int w = image.getWidth(), h = image.getHeight(), i, j, c, k;
		// Obtain pointer to data for fast processing
		byte[] data = GetImageData(image);
		float col;
		short datum;
		// Shows how to loop through each pixel and colour
		// Try to always use j for loops in y, and i for loops in x
		// as this makes the code more readable
		for (j = 0; j < h; j++) {
			for (i = 0; i < w; i++) {
				// at this point (i,j) is a single pixel in the image
				// here you would need to do something to (i,j) if the image
				// size
				// does not match the slice size (e.g. during an image resizing
				// operation
				// If you don't do this, your j,i could be outside the array
				// bounds
				// In the framework, the image is 256x256 and the data set
				// slices are 256x256
				// so I don't do anything - this also leaves you something to do
				// for the assignment
				datum = cthead[j][i][value]; // get values from slice 76 (change
												// this in your assignment)
				// calculate the colour by performing a mapping from [min,max]
				// -> [0,255]
				col = (255.0f * ((float) datum - (float) min) / ((float) (max - min)));
				for (c = 0; c < 3; c++) {
					// and now we are looping through the bgr components of the
					// pixel
					// set the colour component c of pixel (i,j)
					data[c + 3 * i + 3 * j * w] = (byte) col;
				} // colour loop

			} // column loop
		} // row loop

		return image;
	}

	public BufferedImage resizeImage1(BufferedImage image, int value,
			int value_z) {

		int j, i, c;
		int x2,y2;
		float x;
		float y;
		float datum;
		byte[] data = GetImageData(image);
		float col;
		for (j = 0; j < value; j++) {
			for (i = 0; i < value; i++) {
				x = (j * 256) / value;
				y = (i * 256) / value;	
			    x2 = (int) x;
			    y2 = (int) y;
			    if((x2-x == 0)&&(y2-y)==0){
			    	datum =cthead[value_z][x2][y2];
			    	col = (255.0f * ((float) datum - (float) min) / ((float) (max - min)));
			    }
			    else{
			    	datum = cthead[value_z][x2][y2]+(cthead[value_z][x2+1][y2+1]-cthead[value_z][x2][y2])*(y-y2);
			    	col = (255.0f * ((float) datum - (float) min) / ((float) (max - min)));
			    }	    	
					//datum = cthead[value_z][(int) x][(int) y];
				  // col = (255.0f * ((float) datum - (float) min) / ((float) (max - min)));
				for (c = 0; c < 3; c++) {
					data[c + 3 * i + 3 * j * 512] = (byte) col;
				}
			}
		}

		return image;

	}

	public BufferedImage resizeImage2(BufferedImage image, int value,
			int value_y) {

		int j, i, c;
		int x2,y2;
		float x;
		float y;
		float datum;
		byte[] data = GetImageData(image);
		float col;
		for (j = 0; j < value/2 ; j++) {
			for (i = 0; i < value; i++) {
				x = (j * 113) / (value/2 );
				y = (i * 256) / value;
				x2 = (int) x;
			    y2 = (int) y;
			    if((x2-x == 0)&&(y2-y)==0){
			    	datum =cthead[x2][value_y][y2];
			    	col = (255.0f * ((float) datum - (float) min) / ((float) (max - min)));
			    }
			    else{
			    	datum = cthead[x2][value_y][y2]+(cthead[x2+1][value_y][y2+1]-cthead[x2][value_y][y2])*(x-x2);
			    	col = (255.0f * ((float) datum - (float) min) / ((float) (max - min)));
			    }		    	
				for (c = 0; c < 3; c++) {
					data[c + 3 * i + 3 * j * 512] = (byte) col;
				}
			}
		}

		return image;

	}

	public BufferedImage resizeImage3(BufferedImage image, int value,
			int value_x) {

		int j, i, c;
		float x;
		float y;
		int x2 ,y2;
		float datum;
		byte[] data = GetImageData(image);
		float col;
		for (j = 0; j < value/2; j++) {
			for (i = 0; i < value; i++) {
				x = (j * 113) / (value/2 );
				y = (i * 256) / value;
				x2 = (int) x;
			    y2 = (int) y;
				if((x2-x == 0)&&(y2-y)==0){
			    	datum =cthead[x2][y2][value_x];
			    	col = (255.0f * ((float) datum - (float) min) / ((float) (max - min)));
			    }
			    else{
			    	datum = cthead[x2][y2][value_x]+(cthead[x2+1][y2+1][value_x]-cthead[x2][y2][value_x])*(x-x2);
			    	col = (255.0f * ((float) datum - (float) min) / ((float) (max - min)));
			    }		    	
				for (c = 0; c < 3; c++) {
					data[c + 3 * i + 3 * j * 512] = (byte) col;
				}
			}
		}

		return image;

	}

	// The size of top thumbnail is 24*24
	public BufferedImage thumbnail_Image_top(BufferedImage image, int value) {
		int w = image.getWidth(), h = image.getHeight();
		int j, i, c;
		float x;
		float y;
		short datum;
		byte[] data = GetImageData(image);
		float col;
		for (j = 0; j < 24; j++) {
			for (i = 0; i < 24; i++) {
				x = (j * h) / 24;
				y = (i * w) / 24;
				datum = cthead[value][(int) x][(int) y];
				col = (255.0f * ((float) datum - (float) min) / ((float) (max - min)));
				for (c = 0; c < 3; c++) {
					data[c + 3 * i + 3 * j * 256] = (byte) col;
				}
			}
		}

		return image;

	}

	// The size of front thumbnail is 24*8
	public BufferedImage thumbnail_Image_front(BufferedImage image, int value) {
		int w = image.getWidth(), h = image.getHeight();
		int j, i, c;
		float x;
		float y;
		short datum;
		byte[] data = GetImageData(image);
		float col;
		for (j = 0; j < 8; j++) {
			for (i = 0; i < 24; i++) {
				x = (j * h) / 8;
				y = (i * w) / 24;
				datum = cthead[(int) x][value][(int) y];
				col = (255.0f * ((float) datum - (float) min) / ((float) (max - min)));
				for (c = 0; c < 3; c++) {
					data[c + 3 * i + 3 * j * 256] = (byte) col;
				}
			}
		}

		return image;

	}

	// The side of front thumbnail is 24*8
	public BufferedImage thumbnail_Image_side(BufferedImage image, int value) {
		int w = image.getWidth(), h = image.getHeight();
		int j, i, c;
		float x;
		float y;
		short datum;
		byte[] data = GetImageData(image);
		float col;
		for (j = 0; j < 8; j++) {
			for (i = 0; i < 24; i++) {
				x = (j * h) / 8;
				y = (i * w) / 24;
				datum = cthead[(int) x][(int) y][value];
				col = (255.0f * ((float) datum - (float) min) / ((float) (max - min)));
				for (c = 0; c < 3; c++) {
					data[c + 3 * i + 3 * j * 256] = (byte) col;
				}
			}
		}

		return image;

	}
	
	public static void main(String[] args) throws IOException {

		Com_pxd e = new Com_pxd();
		e.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		e.Example();
	}
}