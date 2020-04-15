package model;

// Tao class Point de logic code
public class Point {

	public float x;
	public float y;

	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}

	// Chuyen cai nay vao day nhe, moi diem deu co hanh vi nay
	public double euclidDistanceTo(Point p) {
		return Math.sqrt(Math.pow((this.x - p.x), 2) + Math.pow((this.y - p.y), 2));
	} 

}
