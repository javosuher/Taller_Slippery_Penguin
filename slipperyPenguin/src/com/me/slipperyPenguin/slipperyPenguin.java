package com.me.slipperyPenguin;

import com.badlogic.gdx.Game;

/*AplicationListener es una interfaz que proporciona metodos que se llaman cada vez que es necesario
 * crear, pausar, continuar, renderizar o destruir una aplicacion, nos permite ademas manejar los graficos
 */

/* Game es una clase que implementa de AplicationListener y que permite delegar en una Screen, 
 * es decir, que permite a la alicacion tener y manejar facilmente varias ventanas
 */

public class slipperyPenguin extends Game { // Clase principal del juego, la primera que se ejecuta al iniciarse el juego.
	@Override
	public void create() {
		setScreen(new GameScreen(this)); // Coloca la pantalla actual, se llama desde cualquier pantalla anterior y se llama a Screen.show desde la nueva pantalla
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
	@Override
	public void render() {		
		super.render();
	}
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}
	@Override
	public void pause() {
		super.pause();
	}
	@Override
	public void resume() {
		super.resume();
	}
}
