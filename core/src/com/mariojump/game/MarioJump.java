package com.mariojump.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.omg.CORBA.BAD_TYPECODE;

import java.util.Random;

public class MarioJump extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture[] mario;
	private Texture cano;
	private Texture gameOver;
	private Texture cenario;
	private Texture fundo;

	private float move;
	private int velocidadeCenario;
	private int velocidadeQueda;
	private int nivelPiso;
	private int alturaMario;
	private float posicaoCano;
	private int posicaoCenario;

	//colisões
	private Rectangle colisaoMario;
	private Rectangle colisaoCano;

	//Teste colisões
	//private ShapeRenderer shape;

	//pontuação
	private int pontuacao = 0;
	private boolean marcouPonto = false;

	private int estadoDoJogo;

	private BitmapFont fonte;
	private BitmapFont mensagem;

	//config
	private float larguraDisplay;
	private float alturaDisplay;

	//camera
	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH = 1024;
	private final float VIRTUAL_HEIGHT = 768;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		mario = new Texture[3];

		colisaoMario = new Rectangle();
		colisaoCano = new Rectangle();
		///teste colisões
		//shape = new ShapeRenderer();

		mario[0] = new Texture("mario1.png");
		mario[1] = new Texture("mario2.png");
		mario[2] = new Texture("mariojump.png");
		cano = new Texture("cano_baixo.png");
		gameOver = new Texture("game_over.png");
		cenario = new Texture("cenario.png");
		fundo = new Texture("fundo.png");

		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

		fonte = new BitmapFont();
		fonte.setColor(Color.WHITE);
		fonte.getData().setScale(6);

		mensagem = new BitmapFont();
		mensagem.setColor(Color.WHITE);
		mensagem.getData().setScale(3);

		larguraDisplay = VIRTUAL_WIDTH;
		alturaDisplay = VIRTUAL_HEIGHT;
		move = 0;
		velocidadeQueda = 0;
		nivelPiso = 200;
		alturaMario = 200;
		velocidadeCenario = 0;
		posicaoCano = larguraDisplay;
		posicaoCenario = 0;

		estadoDoJogo = 0;
	}

	@Override
	public void render () {

		//atualiza camera
		camera.update();

		//limpa frames anteriores
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		if(estadoDoJogo == 0){
			if(Gdx.input.justTouched()){
				estadoDoJogo = 1;
			}
		} else{

			velocidadeQueda++;
			velocidadeCenario = 15;

			if(estadoDoJogo == 1){

				posicaoCano -= velocidadeCenario;
				posicaoCenario -= velocidadeCenario;
				move += 0.2;

				Random random = new Random();

				if(move > 2){
					move = 0;
				}

				if(alturaMario > nivelPiso || velocidadeQueda < 0){
					alturaMario -= velocidadeQueda;
					move = 2;
				}

				if(Gdx.input.justTouched() && alturaMario == nivelPiso){
					velocidadeQueda = -20;
				}

				if(posicaoCano < -200){
					posicaoCano = larguraDisplay + 200 + random.nextInt(2000);
					marcouPonto = false;
				}

				if(posicaoCenario < -larguraDisplay){
					posicaoCenario = 0;
				}

				//pontuação
				if (posicaoCano < 200 && !marcouPonto){
					pontuacao++;
					velocidadeCenario++;
					marcouPonto = true;
				}
			} else {

				if(Gdx.input.justTouched()){
					estadoDoJogo = 0;
					pontuacao = 0;
					move = 0;
					alturaMario = 200;
					posicaoCano = larguraDisplay;
					marcouPonto = false;
				}

			}
		}

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		batch.draw(fundo, 0, 0, larguraDisplay, alturaDisplay);
		batch.draw(mario[(int)move], 200, alturaMario, mario[(int)move].getWidth()*5 , mario[(int)move].getHeight()*5);
		batch.draw(cano, posicaoCano,-300);
		batch.draw(cenario, posicaoCenario,0, larguraDisplay*2, 200);
		fonte.draw(batch, String.valueOf(pontuacao), larguraDisplay/2, alturaDisplay - 100);
		if (estadoDoJogo == 2) {
			batch.draw(gameOver, (larguraDisplay - gameOver.getWidth())/2, (alturaDisplay - gameOver.getHeight())/2);
			mensagem.draw(batch, "Toque na tela para Reiniciar", larguraDisplay / 2 - 250, alturaDisplay/2 - gameOver.getHeight() );
		}
		batch.end();

		colisaoMario.set(200, alturaMario, mario[(int)move].getWidth()*5, mario[(int)move].getHeight()*5);
		colisaoCano.set(posicaoCano, -300, cano.getWidth(), cano.getHeight());

		//Teste para colisões
		/*
		shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.rect(colisaoMario.x, colisaoMario.y, mario[(int)move].getWidth()*5, mario[(int)move].getHeight()*5);
		shape.rect(colisaoCano.x, colisaoCano.y, cano.getWidth(), cano.getHeight());
		shape.setColor(Color.BLUE);
		shape.end();
		*/

		//Verificar colisao
		if (Intersector.overlaps(colisaoCano, colisaoMario)){
			estadoDoJogo = 2;
		}


	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
}
