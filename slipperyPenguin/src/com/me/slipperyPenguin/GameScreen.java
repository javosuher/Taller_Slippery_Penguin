package com.me.slipperyPenguin;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class GameScreen implements Screen { // Implementa la interfaz de Screen, es decir, se comportara con las caracteristicas de una pantalla
	// sus funciones se llaman automaticamente cuando ocurre el evento al que estan asociadas (renderizar,
	//reescalar, pausar, resumir...) menos con dispose, para liberar los recursos hay que llamar a dispose manualmente
	
	private slipperyPenguin sp;
	private Texture texturaFondo, texturaPinguino, texturaPinguinoMuerto, texturaRoca; // Una Texture es una clase que envuelve una textura estandar de OpenGL, se utiliza para imagenes simples.
	private SpriteBatch batch; // "Grupo de Sprites (imagenes)" nos permite dibujar rectagulos como referencias a texturas, es necesario para mostrar todo por pantalla.
	
	// Personajes del juego
	private penguin pinguino; // Nuestro pingüino.
	private rock roca1, roca2, roca3; // Las rocas que usaremos. Se iran creando y reutilizando conforme se esquiven por el pingüino.
	
	// Atributos que permiten la generación de rocas aleatorias.
	private Random random; // Sirve para obtener un numero aleatorio que permitirá poner las rocas en ubicaciones diferentes.
	private int limite; // Variable que delimita el límite de los valores que tiene que tomar random para ubicar las rocas.
	private boolean roca1Activa, roca2Activa, roca3Activa; // Booleanos que permitirán saber si las rocas estan usandose.
	
	// Atributos para la puntuación, osea, la de rocas que se han esquivado sin chocarte.
	private Preferences preferencias; // Nos permite almacenar datos en el dispositivo que se esté jugando el juego. Nos servirá para guardar la puntuación del jugador.
	private int puntuacion; // Puntuación que tenemos en el juego.
	private int puntuacionMaxima; // Puntuación máxima que se ha conseguido.
	private BitmapFont font; // Sirve para mostrar los letras y números por la pantalla.
	
	// Atributos para la música y sonidos del juego.
	private Music musica; // Música del juego.
	private Sound grito; // Grito que da el pingüino cuando muere.
	private Sound golpe; // Sonido del golpe de la roca con el pingüino.
	
	private boolean gameOver; // Sirve para en el render dibujar el juego normal, o mostrar el game over.
	
	public GameScreen(slipperyPenguin slipperypenguin) { // Constructor de la clase.
		this.sp = slipperypenguin;
		random = new Random(); // Se inializa random.
		
		//Creamos las texturas, primero el fondo del juego.
		texturaFondo = new Texture("data/background.png"); // Asociamos la textura con la imagen correspondiente
		texturaFondo.setFilter(TextureFilter.Linear, TextureFilter.Linear); // Con setFilter controlamos la forma en la que la imagen se 
		//reescala, le añadimos el parametro TextureFilter.Linear en ambos casos, para que este reescalado sea lineal.
		
		// Creamos el pingüino
		texturaPinguino = new Texture("data/pinguin.png");
		texturaPinguino.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		texturaPinguinoMuerto = new Texture("data/deadPinguin.png"); // La textura del pingüino muerto.
		texturaPinguinoMuerto.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		pinguino = new penguin(new Vector2(0, Gdx.graphics.getHeight() - texturaPinguino.getHeight() - 10), texturaPinguino.getWidth(), texturaPinguino.getHeight());
		
		// Creamos los datos necesarios de las rocas.
		texturaRoca = new Texture("data/rock.png");
		texturaRoca.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		limite = Gdx.graphics.getWidth() - texturaRoca.getWidth();
		roca1 = new rock(new Vector2(random.nextInt(limite), 0 - texturaRoca.getHeight()), texturaRoca.getWidth(), texturaRoca.getHeight());
		roca1Activa = true;
		
		// Datos de preferencias.
		preferencias = Gdx.app.getPreferences("-_PreferencesSlipperyPenguin-_"); // Obtenemos los datos del fichero, o si no está creado, se crea automaticamente
		puntuacionMaxima = preferencias.getInteger("Record", 0); // Asignamos la puntuación máxima que se encuentra en el fichero de preferencias. Si no existe el campo lo crea a 0.
		font = new BitmapFont(Gdx.files.internal("data/arial.fnt"), Gdx.files.internal("data/arial.png"), false); // Asignamos a font el tipo de letra Arial.
		
		// Creamos la música.
		musica = Gdx.audio.newMusic(Gdx.files.internal("data/musica.mp3"));
		grito = Gdx.audio.newSound(Gdx.files.internal("data/grito.wav"));
		golpe = Gdx.audio.newSound(Gdx.files.internal("data/golpe.ogg"));
		musica.setLooping(true); // Se pone para que la música nunca pare.
		musica.play(); // Ponemos la música.
		
		gameOver = false;
		
		batch = new SpriteBatch(); // Es recomendable tener solo uno por juego.
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1); //Gdx es una clase con la que podemos acceder a variables que hacen referencia a todos los subsitemas, como son graficos, audio, ficheros, entrada y aplicaciones
		// gl es una variable de tipo GL, nos permite acceder a metodos de GL10, GL11 y GL20
		//En este caso glClearColor es un bucle (game loop) que establecera el fondo de la pantalla negro (0,0,0) con transparencia 1
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT); // Despues de la funcion anterior es necesario ejecutar esta, para que se lleve a cabo
		
		if(gameOver) // Si se ha perdido
			renderGameOver();
		else
			renderJuegoNormal();
	}
	
	private void renderJuegoNormal() {
		pinguino.update(); // Actualizamos el pingüino.
		actualizarRocas(); // Actualizamos las rocas.
		
		comprobarMuertePinguino(); // Comprobamos la muerte del pingüino.
		
		batch.begin(); // Aqui por fin comenzamos el renderizado
		
		// Dibujamos el fondo en la posición x = 0 e y = 0.
		batch.draw(texturaFondo, 0, 0, texturaFondo.getWidth(), texturaFondo.getHeight()); 
		// Dibujamos el pingüino.
		batch.draw(texturaPinguino, pinguino.getPosicion().x, pinguino.getPosicion().y, pinguino.getAnchura(), pinguino.getAltura());
		
		// Dibujamos las rocas.
		if(roca1Activa) // Si está roca1 activa
			batch.draw(texturaRoca, roca1.getPosicion().x, roca1.getPosicion().y, roca1.getAnchura(), roca1.getAltura());
		if(roca2Activa) // Si está roca2 activa
			batch.draw(texturaRoca, roca2.getPosicion().x, roca2.getPosicion().y, roca2.getAnchura(), roca2.getAltura());
		if(roca3Activa) // Si está roca3 activa
			batch.draw(texturaRoca, roca3.getPosicion().x, roca3.getPosicion().y, roca3.getAnchura(), roca3.getAltura());
		
		// Pintamos la puntuación y puntuación máxima.
		font.setScale(0.6f); // Tamaño de la letra.
		font.setColor(Color.BLACK); // Permite cambiar el color de la fuente.
		font.draw(batch, "SCORE: " + Integer.toString(puntuacion), 10, 30);
		font.setColor(Color.RED);
		font.draw(batch, "MAX: " + Integer.toString(puntuacionMaxima), Gdx.graphics.getWidth() - 100, 30);
		
		batch.end(); // Terminamos el renderizado.
	}
	private void renderGameOver() {
		if (Gdx.input.isTouched()) { // Si se pulsa la pantalla se vuelve a empezar el juego.
			pinguino.setPosicion(0, Gdx.graphics.getHeight() - texturaPinguino.getHeight() - 10); // Ponemos al pingüino en su posición inicial.
			roca2Activa = roca3Activa = false; // Desactivamos las rocas
			roca1Activa = true; // Activamos la primera roca.
			roca1 = new rock(new Vector2(random.nextInt(limite), 0 - texturaRoca.getHeight()), texturaRoca.getWidth(), texturaRoca.getHeight()); // Creamos la primera roca.
			puntuacion = 0; // Reseteamos la puntuación.
			musica.play(); // Volvemos a poner la música.
			gameOver = false;
		}
		
		batch.begin(); // Aqui por fin comenzamos el renderizado
		
		// Dibujamos el fondo en la posición x = 0 e y = 0.
		batch.draw(texturaFondo, 0, 0, texturaFondo.getWidth(), texturaFondo.getHeight()); 
		// Dibujamos el pingüino muerto.
		batch.draw(texturaPinguinoMuerto, pinguino.getPosicion().x, pinguino.getPosicion().y, pinguino.getAnchura(), pinguino.getAltura());
		
		// Pintamos la puntuación y puntuación máxima.
		font.setScale(0.6f); // Tamaño de la letra.
		font.setColor(Color.BLACK); // Permite cambiar el color de la fuente.
		font.draw(batch, "SCORE: " + Integer.toString(puntuacion), 10, 30);
		font.setColor(Color.RED);
		font.draw(batch, "MAX: " + Integer.toString(puntuacionMaxima), Gdx.graphics.getWidth() - 100, 30);
		
		// Pintamos Game over en la pantalla
		font.setScale(2f); // Tamaño de la letra.
		font.setColor(Color.BLACK); // Permite cambiar el color de la fuente.
		font.draw(batch, "GAME OVER", Gdx.graphics.getWidth() / 2 - 190, Gdx.graphics.getHeight() / 2 + 20);
		
		batch.end(); // Terminamos el renderizado.
	}
	
	private void actualizarRocas() { // Método que sirve para ir actualizando los valores de las rocas. Se van reutilizando.
		if(roca1Activa) { // Si la roca1 esta activa
			roca1.update(); // Actualizamos
			if(!roca2Activa && roca1.getPosicion().y > Gdx.graphics.getHeight() / 2) { // Si la roca2 no está activa y la roca1 va por la mitad de la pantalla
				roca2Activa = true;
				roca2 = new rock(new Vector2(random.nextInt(limite), 0 - texturaRoca.getHeight()), texturaRoca.getWidth(), texturaRoca.getHeight());
			}
			else if(roca1.getPosicion().y > Gdx.graphics.getHeight()) { // Si la roca1 sale de la pantalla.
				roca1Activa = false;
				puntuacion++;
			}
		}
		if(roca2Activa) { // Si la roca2 esta activa
			roca2.update();
			if(!roca3Activa && roca2.getPosicion().y > Gdx.graphics.getHeight() / 2) { // Si la roca3 no está activa y la roca2 va por la mitad de la pantalla
				roca3Activa = true;
				roca3 = new rock(new Vector2(random.nextInt(limite), 0 - texturaRoca.getHeight()), texturaRoca.getWidth(), texturaRoca.getHeight());
			}
			else if(roca2.getPosicion().y > Gdx.graphics.getHeight()) { // Si la roca2 sale de la pantalla.
				roca2Activa = false;
				puntuacion++;
			}
		}
		if(roca3Activa) { // Si la roca3 esta activa
			roca3.update();
			if(!roca1Activa && roca3.getPosicion().y > Gdx.graphics.getHeight() / 2) { // Si la roca1 no está activa y la roca3 va por la mitad de la pantalla
				roca1Activa = true;
				roca1 = new rock(new Vector2(random.nextInt(limite), 0 - texturaRoca.getHeight()), texturaRoca.getWidth(), texturaRoca.getHeight());
			}
			else if(roca3.getPosicion().y > Gdx.graphics.getHeight()) { // Si la roca3 sale de la pantalla.
				roca3Activa = false;
				puntuacion++;
			}
		}
	}
	
	private void comprobarMuertePinguino() { // Método que comprueba si el pingüino se choca con alguna roca.
		if(roca1Activa) { // Si roca1 está activa
			if(pinguino.choqueConRoca(roca1)) // Si choca
				muerte();
		}
		if(roca2Activa) { // Si roca2 está activa
			if(pinguino.choqueConRoca(roca2)) // Si choca
				muerte();
		}
		if(roca3Activa) { // Si roca3 está activa
			if(pinguino.choqueConRoca(roca3)) // Si choca
				muerte();
		}
	}
	private void muerte() { // Método que realiza la muerte del pingüino.
		if(puntuacion > puntuacionMaxima) { // Si nuestra puntuación es mayor que la máxima.
			puntuacionMaxima = puntuacion;
			preferencias.putInteger("Record", puntuacionMaxima); // Guardamos la puntuación máxima en el fichero de preferencias.
			preferencias.flush();
		}
		gameOver = true; // Se pierde la partida.
		musica.stop(); // Paramos la música.
		golpe.play(); // Reproducimos el golpe.
		grito.play(); // Suena el grito del pingüino.
	}
	
	@Override
	public void show() { // Método que se ejecuta cuando la pantalla actual pasa a estar activa.
		// TODO Auto-generated method stub
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void dispose() { // Cuando se termina la aplicación
		// TODO Auto-generated method stub
	}
}
