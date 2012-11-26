package com.github.bugra.cvclass.binaryimageproject;


/* 
 * Represents each pixel in the image, it has four properties.
 * It has getter, setter, equals() and toString() methods. It has four different
 * constructors for different number of input arguments.
 *  PROPERTIES:
 *  x, y are the location of the pixel. 
 *  
 *  value is the intensity of the pixel and by default it is one(represents object).
 *  
 *  dir property represents direction of the pixel. It is used only for finding perimeter
 *  purposes. Instead of carrying direction information in order to track boundary of
 *  the object, I assigned it into this property. Default is 0, which means for a 
 *  4-connected neighborhood, the pixel directs west. The reason is explained in 
 *  the other class.
 *  
 *  METHODS:
 *  getter and setter methods are as usual.
 *  
 *  equals() is overridden. For a pixel to be equal to any other pixel, I just look at
 *  their position in the image. Note that, for same image, value and dir properties
 *  should be already same.
 *  
 *  toString() is also overridden. It serves as a debugging tool. 
 *  
 *  */
public class Pixel {
	
	private int x;
	private int y;
	private int value;
	private int dir;
	
	public Pixel(){
		this.value = 1; // Assuming binary image, and it is object
		this.dir = 0; // Default is 0, which directs west.
	}
	
	public Pixel(int x, int y){
		this.x = x;
		this.y = y;
		this.dir = 0;
		this.value = 1; 
	}
	
	public Pixel(int x, int y, int value){
		this.x = x;
		this.y = y;
		this.dir = 0;
		this.value = value;
	}
	
	public Pixel(int x, int y, int value, int dir){
		this.x = x;
		this.y = y;
		this.value = value;
		this.dir = dir;
	}
	
	// Getter Methods
	public int getX(){return x;}
	public int getY(){return y;}
	public int getValue(){return value;}
	public int getDir(){ return dir;}
	
	// Setter Methods
	public void setX(int newX){this.x = newX;}
	public void setY(int newY){this.y = newY;}
	public void setValue(int newValue){ this.value = newValue;}
	public void setDir(int dir){ this.dir = dir;}
	
	// Which only compares the position of the pixels in the same image and does not compare 
	// value and the direction of the images.
	public boolean equals(Pixel newPixel){
		return ((getX() == newPixel.getX()) && getY() == newPixel.getY());
	}
	// Overriden toString() method
	public String toString(){
		return "Pixel " + "has a value of x:" + getX() + ", value of y: " + getY() + " direction is: " + getDir();
	}
}
