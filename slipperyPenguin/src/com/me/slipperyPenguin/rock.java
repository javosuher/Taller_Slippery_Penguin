package com.me.slipperyPenguin;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class rock { // Esta clase representa las rocas que tiene que esquivar nuestro pingüino.
	private static final float SPEED = 5; // Velocidad de movimiento de las rocas.
	
	private Vector2 posicion; // Representa la posición de la roca en el eje x y en el eje y
	private float anchura, altura; // Anchura y altura de la roca.
	private Rectangle bordes; // Rectangulo que ocupara el ancho y alto de la roca.
	
	public rock(Vector2 posicion, float anchura, float altura) {
		this.posicion = posicion;
		this.anchura = anchura;
		this.altura = altura;
		bordes = new Rectangle(posicion.x, posicion.y, anchura, altura);
	}
	
	public void update() {
		posicion.y = posicion.y + SPEED; // Se mueven hacia arriba en la pantalla
		
		// Actualizamos los bordes en el eje y, ya que las rocas solo se mueven en esa dirección.
		bordes.y = posicion.y;
	}
	
	// Getters -----------------------------------

	public Vector2 getPosicion() {
		return posicion;
	}

	public float getAnchura() {
		return anchura;
	}

	public float getAltura() {
		return altura;
	}

	public Rectangle getBordes() {
		return bordes;
	}
}
