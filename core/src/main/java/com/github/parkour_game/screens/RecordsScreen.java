package com.github.parkour_game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.parkour_game.Main;
import com.github.parkour_game.GameManager.GameManager;
import com.github.parkour_game.data.db.GameScore;

import java.util.ArrayList;
import java.util.List;

public class RecordsScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final Texture background;
    private final BitmapFont font;
    private final GameManager gameManager;
    private final Texture backButtonTexture;
    private final Texture recordsTitle;
    private final Texture bestScoresTitle;
    private final Texture recentScoresTitle;


    private List<GameScore> topScores = new ArrayList<>();
    private List<GameScore> recentScores = new ArrayList<>();
    private boolean scoresLoaded = false;




    public RecordsScreen(Main game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.background = new Texture("background2.png");
        this.font = new BitmapFont();
        font.getData().setScale(5f);
        this.gameManager = game.getGameManager();
        backButtonTexture = new Texture("back_button.png");
        this.recordsTitle = new Texture("records_button.png");
        this.bestScoresTitle = new Texture("best_scores_title.png");
        this.recentScoresTitle = new Texture("recent_scores_title.png");

        loadScores();
    }

    @Override
    public void show() {
        refreshScores();
    }

    private void refreshScores() {
        scoresLoaded = false;
        gameManager.refreshScores(() -> scoresLoaded = true);
    }

    private void loadScores() {
        // Загрузка топовых результатов
        gameManager.getDbHelper().getTopScores(5, scores -> {
            topScores = scores;
            checkScoresLoaded();
        });

        // Загрузка последних результатов
        gameManager.getDbHelper().getRecentScores(5, scores -> {
            recentScores = scores;
            checkScoresLoaded();
        });
    }

    private void checkScoresLoaded() {
        scoresLoaded = !topScores.isEmpty() && !recentScores.isEmpty();
        if (scoresLoaded) {
            Gdx.app.postRunnable(() -> {
                // Обновляем UI в основном потоке
            });
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Заголовок экрана
        batch.draw(recordsTitle,
            Gdx.graphics.getWidth() / 2f - 150,
            Gdx.graphics.getHeight() - 200,
            320,
            150);

        // Лучшие результаты
        batch.draw(bestScoresTitle,
            Gdx.graphics.getWidth()/4f - 150,
            Gdx.graphics.getHeight() - 350,
            320, 152);

        // Последние результаты
        batch.draw(recentScoresTitle,
            3*Gdx.graphics.getWidth()/4f - 150,
            Gdx.graphics.getHeight() - 350,
            320, 152);

        // Отрисовка результатов
        drawScores();

        // Кнопка назад
        batch.draw(backButtonTexture, 50, 50, 300, 120);

        // Обработка нажатий
        if (Gdx.input.justTouched()) {
            int touchX = Gdx.input.getX();
            int touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (touchX >= 50 && touchX <= 250 && touchY >= 50 && touchY <= 180) {
                game.setScreen(game.getMainMenuScreen());
            }
        }

        batch.end();
    }

    private void drawScores() {
        if (!scoresLoaded) {
            font.draw(batch, "Loading...", Gdx.graphics.getWidth()/2f - 100,
                Gdx.graphics.getHeight()/2f);
            return;
        }

        // Отрисовка лучших результатов
        List<GameScore> topScores = gameManager.getTopScores();
        for (int i = 0; i < topScores.size(); i++) {
            font.draw(batch, (i+1) + ". " + topScores.get(i).getScore(),
                Gdx.graphics.getWidth()/4f - 120,
                Gdx.graphics.getHeight() - 450 - i*80);
        }

        // Отрисовка последних результатов
        List<GameScore> recentScores = gameManager.getRecentScores();
        for (int i = 0; i < recentScores.size(); i++) {
            font.draw(batch, (i+1) + ". " + recentScores.get(i).getScore(),
                3*Gdx.graphics.getWidth()/4f - 100,
                Gdx.graphics.getHeight() - 450 - i*80);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        font.dispose();
        backButtonTexture.dispose();
        recordsTitle.dispose();
        bestScoresTitle.dispose();
        recentScoresTitle.dispose();
    }

    //@Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
