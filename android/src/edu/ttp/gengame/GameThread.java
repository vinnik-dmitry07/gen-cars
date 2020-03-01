package edu.ttp.gengame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;

import com.badlogic.gdx.physics.box2d.Body;

class GameThread extends Thread {
    private boolean running;
    private final GameSurface gameSurface;
    private final SurfaceHolder surfaceHolder;

    private Canvas ctx;

    private void simulationStep() {
        Game.currentRunner.getStep().run();
// TODO
//        showDistance(
//                Math.round(LeaderPosition.x * 100) / 100,
//                Math.round(LeaderPosition.y * 100) / 100
//        );
    }


    @SuppressWarnings({"EmptyMethod", "unused"})
    void showDistance(double distance, double height) {
// TODO: 2/18/2020
//        distanceMeter.innerHTML = distance + " meters<br />";
//        heightMeter.innerHTML = height + " meters";
//        if (distance > minimapfogdistance) {
//            fogdistance.width = 800 - Math.round(distance + 15) * minimapscale + "px";
//            minimapfogdistance = distance;
//        }
    }


    private void cw_drawScreen() {
        Body[] floorTiles = Game.currentRunner.getScene().getFloorTiles();
        ctx.drawColor(Color.WHITE);
        ctx.save(); // ???
        Game.cw_setCameraPosition();
        ctx.translate(Game.Camera.distanceToLeftBound - (Game.Camera.pos.x * Game.Camera.zoom),
                      Game.Camera.INSTANCE.getDistanceToTopBound() + (Game.Camera.pos.y * Game.Camera.zoom));
        ctx.scale(Game.Camera.zoom, -Game.Camera.zoom);
        Draw.cw_drawFloor(ctx, floorTiles);
//        ghost_draw_frame(ctx, ghost, camera);
        cw_drawCars();
        ctx.restore();
    }


    private void cw_drawCars() {
        for (cw_Car myCar : Game.carMap.values()) {
            Draw.drawCar(myCar, ctx);
        }
    }

    GameThread(GameSurface gameSurface, SurfaceHolder surfaceHolder) {
        this.gameSurface = gameSurface;
        this.surfaceHolder = surfaceHolder;
    }

    private long nextGameTick = System.nanoTime();

    @Override
    public void run() {
        while (running) {
            long loops = 0;
            while (System.nanoTime() > nextGameTick && loops < Game.maxFrameSkip) {
                nextGameTick += Game.skipTicks;
                loops++;
            }

            ctx = this.surfaceHolder.lockCanvas();
            this.gameSurface.draw(ctx);
            cw_drawScreen();
            simulationStep();

            this.surfaceHolder.unlockCanvasAndPost(ctx);
        }
    }

    void setRunning(boolean running) {
        this.running = running;
    }
}