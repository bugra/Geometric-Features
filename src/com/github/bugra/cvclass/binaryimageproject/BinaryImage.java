package com.github.bugra.cvclass.binaryimageproject;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
/*
 *  This class gets a binary image from a URL. Turns into a 2-D Matrix. 
 *  1) getArea() calculates area of the binary image.
 *  2) getCentroid() calculates the centroid of the binary image.
 *  3) getPerimeter() computes the perimeter of the binary image.
 *  4) getAxisofLeastInertia() computes the axis of least inertia.
 *  
 *  Other helper functions are explained further in their inline comments. 
 */
public class BinaryImage{

	public BufferedImage binaryImage; // Binary image, read from web
	public int[][] bwMatrix; // 2-D Binary image matrix, this one is used to compute geometric features
	
	/*
	 * Constructor takes a URL to read a binary image and converts this image into 
	 * 2-D matrix. get2dArray() is used to convert the binary image into 2-D matrix.
	 */
	public BinaryImage(String path){
		try {
			binaryImage = ImageIO.read(new URL(path));
			this.bwMatrix = get2DArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * By color of the pixels, it tries to determine whether pixel is 1 or 0.
	 * 
	 * The syntax below look a little weird and complicated. It is not. It is a shortcut
	 * for if-else. It checks condition, and if condition is true, then variable is assigned
	 * to the firstVariable, if it is false, then the varible is assigned
	 * to the secondVariable.
	 * **** someVariable = (condition) ? firstVariable: secondVariable;
	 */
	public int[][] get2DArray(){
		int w = binaryImage.getWidth();
		int h = binaryImage.getHeight();
		int matrix[][] = new int[w][h]; 
		for (int ii = 0; ii<binaryImage.getWidth(); ii++){
			for (int jj = 0; jj<binaryImage.getWidth(); jj++){
				Color rgbColor = new Color(binaryImage.getRGB(ii, jj));
				int redColor = rgbColor.getRed();
				int greenColor = rgbColor.getGreen();
				int blueColor = rgbColor.getBlue();
				matrix[ii][jj] = (redColor != 0 & greenColor != 0 & blueColor != 0) ? 1:0;  
			}
		}
		return matrix;
	}
	
	// Computes the area  of the 2-D matrix. It basically sums all the object intensity
	// values, assuming image is binary and has an intensity of 1.
	public int getArea(){
		int area = 0;
		for (int ii = 0; ii<bwMatrix.length; ii++){
			for (int jj = 0; jj<bwMatrix[0].length; jj++){
				area += (bwMatrix[ii][jj] == 1) ? 1:0;
			}
		}
		return area;
	}
	
	// Computes the centroid of the image. Returns a double array in which it stores
	// rowMean and columnMean.
	public double[] getCentroid(){
		double rowMean = 0;
		double columnMean = 0;
		double[] centroidArray = new double[2];
		for (int ii = 0; ii<bwMatrix.length; ii++){
			for (int jj = 0; jj<bwMatrix[0].length; jj++){
				rowMean += (bwMatrix[jj][ii] == 1) ? ii:0;
				columnMean += (bwMatrix[jj][ii] == 1) ? jj:0;
			}
		} 
		rowMean = (double)rowMean / getArea();
		columnMean = (double)columnMean / getArea();
		centroidArray[0] = rowMean;
		centroidArray[1] = columnMean;
		return centroidArray;
	}
	
	// Computes second order row moment. It will be used to compute axis of least inertia.
	public double getSecondOrderRowMoment(){
		double rowMean = getCentroid()[0];
		double muRR = 0;
		for (int ii = 0; ii<bwMatrix.length; ii++){
			for (int jj = 0; jj<bwMatrix[0].length; jj++){
				muRR += (bwMatrix[jj][ii] == 1) ? (ii - rowMean) * (ii - rowMean):0;
			}
		} 
		return muRR / getArea();
	}
	
	// Computes second order column moment. It will be used to compute axis of least inertia.
	public double getSecondOrderColumnMoment(){
		double columnMean = getCentroid()[1];
		double muCC = 0;
		for (int ii = 0; ii<bwMatrix.length; ii++){
			for (int jj = 0; jj<bwMatrix[0].length; jj++){
				muCC += (bwMatrix[jj][ii] == 1) ? (jj - columnMean) * (jj - columnMean):0;
			}
		} 
		return muCC / getArea();
	}
	
	// Computes second order mix moment. It will be used to compute axis of least inertia.
	public double getSecondOrderMixedMoment(){
		double rowMean = getCentroid()[0];
		double columnMean = getCentroid()[1];
		double muRC = 0;
		for (int ii = 0; ii<bwMatrix.length; ii++){
			for (int jj = 0; jj<bwMatrix[0].length; jj++){
				muRC += (bwMatrix[jj][ii] == 1) ? ((ii - rowMean) * (jj - columnMean)):0;
			}
		} 
		return muRC / getArea();
	}
	
	// Calculates alpha value as explained in lecture notes.
	public double getAlpha(){
		double tan2Alpha = (2 * getSecondOrderMixedMoment()) / (getSecondOrderRowMoment()-getSecondOrderColumnMoment());
		return Math.atan(tan2Alpha) / 2.0;
	}
	
	// As the minimization problem of variance with respect to second orders return two 
	// different alpha values(one of them is least inertia and second one is the best inertia
	// We need to compare the values of second moment about axis to find out which one 
	// is actually the least inertia. We know that alpha of least inertia and alpha of
	// best inertia difference is PI/2.
	public double getBestAlpha(){
		double alpha1 = getAlpha();
		double alpha2 = alpha1 + (Math.PI/2);
		double bestAlpha;
		bestAlpha = (getSecondMomentAboutAxis(alpha1) <= getSecondMomentAboutAxis(alpha2)) ? alpha1:alpha2;
		return bestAlpha;
	}
	
	// Calculates the second moment about axis given the parameter of alpha.
	public double getSecondMomentAboutAxis(double alpha){
		double cost = 0;
		double rowMean = getCentroid()[0];
		double columnMean = getCentroid()[1];
		for (int ii = 0; ii<bwMatrix.length; ii++){
			for (int jj = 0; jj<bwMatrix[0].length; jj++){
				cost += (bwMatrix[jj][ii] == 1) ? Math.pow((((jj - columnMean) * Math.sin(alpha))  + ((ii - rowMean) * Math.cos(alpha))),2):0;
			}
		} 
		return cost/getArea();
	}
	
	
	
	// Prints the binary matrix. For Debug purposes.
	public void printMatrix(){
		for (int ii = 0; ii<bwMatrix.length; ii++){
			for (int jj = 0; jj<bwMatrix[0].length; jj++){
				System.out.print(bwMatrix[jj][ii]);
			}
			System.out.println();
		}
	}

	// Computes the position of the first pixel of the object beginning top left corner.
	// It will be used for perimeter calculation.
	public Pixel getFirstPixelInBoundary(){
		Pixel firstPixel = new Pixel();
		boolean done = true;
		for (int ii = 0; ii<bwMatrix.length; ii++){
			for (int jj = 0; jj<bwMatrix[0].length; jj++){
				if (bwMatrix[jj][ii] == 1 && done)
				{
					firstPixel.setX(ii);
					firstPixel.setY(jj);
					done = false;
				}
			}
		} 
		return firstPixel;
	}

	/* Given a previous pixel, this method computes the next pixel in the boundary. 
	 * It will be used to compute perimeter.
	 * 
	 * ALGORITHM:
	 * 1. Find the starting pixel s belongs to S for the region using a systematic scan
	 * say from left to right and from top to bottom of the image.
	 * 2. Let the current pixel in boundary tracking be denoted by c. Set c=s and let the
	 * 4-neighbor to the west of s be b \in \bar{S}
	 * 3. Let the 8-neighbors of c starting with b in clockwise order be n_1, n_2, n_3,
	 * \ldots, n_8. Find n_i, for the first i that is in S.
	 * 4. Set c=n_i, and b = n_{i-1}
	 * 5. Repeat steps 3 and 4 until c=s 
	 * 
	 */
	public Pixel getNextPixel(Pixel previousPixel){
		int a = previousPixel.getX();
		int c = previousPixel.getY();
		int d = previousPixel.getDir();
		Pixel p = new Pixel(a, c);
		p.setDir(d);
		int dir = p.getDir();
		int b;
		Pixel tempPixel = new Pixel();
		Pixel prePixel = new Pixel();
		int nextDir = 0;
		final int[][] delta = {
				{ 0, -1}, { -1, -1}, {-1, 0}, {-1, 1}, 
				{0, 1}, {1, 1}, {1, 0}, { 1, -1}};
		for (int i = 0; i < 7; i++) {
			int x = p.getX() + delta[dir][0];
			int y = p.getY() + delta[dir][1];
			if (bwMatrix[y][x] == 0) {
				dir = (dir + 1) % 8;
			} 
			else {
				b = (dir == 0) ? delta.length - 1: dir - 1;
				prePixel.setX(p.getX() + delta[b][0]);
				prePixel.setY(p.getY() + delta[b][1]);
				tempPixel.setX(prePixel.getX() - x);
				tempPixel.setY(prePixel.getY() - y);
				for (int ii = 0; ii < delta.length; ii++){
					if(delta[ii][0] == tempPixel.getX() && delta[ii][1] == tempPixel.getY()){
						nextDir = ii;
						break;
					}
				}
				p.setDir(nextDir);
				p.setX(x); 
				p.setY(y); 
				break;
			}
		}
		
		return p;
	}
	
	// It calculates the euclidean distance of the two pixels. It is used to compute
	// perimeter.
	public static double getEuclideanDistance(Pixel p1, Pixel p2){
		return (double)Math.sqrt(Math.pow((p2.getX() - p1.getX()), 2) + Math.pow((p2.getY()-p1.getY()), 2));
	}
	
	// Computes perimeter. It gets firstPixel of the object and traces boundary along
	// the object. Then until it reaches firstPixel, it adds to the perimeter the euclidean
	// distance of adjacent pixels.
	public double getPerimeter(){
		double perimeter = 0;
		Pixel firstPixel = getFirstPixelInBoundary();
		Pixel tempPixel = getNextPixel(firstPixel);
		perimeter += getEuclideanDistance(firstPixel, tempPixel);
		Pixel nextPixel = getNextPixel(tempPixel);
		boolean done = nextPixel.equals(firstPixel);
		while(!done){
			perimeter += getEuclideanDistance(tempPixel, nextPixel);
			tempPixel = nextPixel;
			nextPixel = getNextPixel(nextPixel);
			done = nextPixel.equals(firstPixel);
			if (done){
				perimeter += getEuclideanDistance(firstPixel, tempPixel);
				break;
			}
		}
		return perimeter;
	}
	public static double radianToDegree(double radian){
		return ((180/Math.PI) * radian);
	}
	public double convertCoordinate(double yCoordinate){
		return binaryImage.getWidth() - yCoordinate;
	}
	// Writes the geometric features into a text file which is "output.txt"
	public static void writeFeaturesIntoTextFile(){
		
		final String outputFileName = "output.txt";
		String raw0 = "0http://i.imgur.com/ebVCv.png"; // O.png
		String raw1 = "1http://i.imgur.com/eNWdi.png"; // 1.png
		String raw2 = "2http://i.imgur.com/tPuVQ.png"; // 2.png
		String raw3 = "3http://i.imgur.com/LWXFO.png"; // 3.png
		String raw4 = "4http://i.imgur.com/6OcB4.png"; // 4.png
		String raw5 = "5http://i.imgur.com/XpgM0.png"; // 5.png
		List<String> imageList = new ArrayList<String>();
		imageList.add(raw0);
		imageList.add(raw1);
		imageList.add(raw2);
		imageList.add(raw3);
		imageList.add(raw4);
		imageList.add(raw5);
		String text  = "";
		boolean update = false;
		for (String s: imageList){
			BinaryImage bwMatrix = new BinaryImage(s.substring(1));
			int area = bwMatrix.getArea();
			System.out.println("Area is calculated");
			double[] rowColumn = bwMatrix.getCentroid();
			System.out.println("Centroid is calculated");
	        double rCenter = rowColumn[0];
	        double cCenter = rowColumn[1];
			double perimeter = bwMatrix.getPerimeter();
			System.out.println("Perimeter is calculated");
			text = text + "For the image: " + s.substring(0, 1) + ".raw" + "\n\n" + 
						  "Area is:  " +  area + "\n" +
						  "Centroid is: "+ "x= " + cCenter + " and " + "y= " + bwMatrix.convertCoordinate(rCenter) + "\n" +
						  "Perimeter is: " + perimeter + "\n" +
						  "Axis of Least Inertia:" + "\n\t\t" + "alpha = "  + radianToDegree(bwMatrix.getBestAlpha()) + " in degrees" + "\n" +
						  "\t\t x of centroid = " + cCenter + " and y of centroid = " + bwMatrix.convertCoordinate(rCenter) + "\n" +
						  "-------------------------------------------------\n";
			System.out.println(text);
			System.out.println();
		}
		writeFile(outputFileName, text, update);
	}
	public static void writeFile(String fileName, String text, boolean update){
		Writer writer = null;
		try {
            writer = new BufferedWriter(new FileWriter(fileName, update));
            writer.write(text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	public static void main(String[] args) {
        writeFeaturesIntoTextFile();
	}

}
