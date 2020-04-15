package model;

// Comment: Giua location va sensor co 2 thuoc tinh chung x, y, co the viet dang tai su dung du lieu bang ket tap hoac ke thua.
// Nen dung them class phu la Point. No se logic hon
public class Sensor extends Point {

	public float r;

	public Sensor(float x, float y, float r) {
		super(x, y);
		this.r = r;
	}

}
