package edu.ttp.gengame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import static android.graphics.Color.rgb;


class Draw {
    private static final Paint fillPaint;
    private static final Paint strokePaint;

    static {
        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setAntiAlias(true);

        strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(1f / Game.Camera.zoom);
        strokePaint.setAntiAlias(true);
    }


    private static Vector2[] getVertices(PolygonShape shape) {
        Vector2[] vertices = new Vector2[shape.getVertexCount()];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = new Vector2();
            shape.getVertex(i, vertices[i]);
        }
        return vertices;
    }

    private static void cw_drawCircle(android.graphics.Canvas ctx, Body body, Vector2 center, float radius, float angle) {
        Vector2 p = body.getWorldPoint(center);

        ctx.drawCircle(p.x, p.y, radius, fillPaint);
        ctx.drawCircle(p.x, p.y, radius, strokePaint);

        ctx.drawLine(p.x, p.y, p.x + radius * (float) Math.cos(angle),
                p.y + radius * (float) Math.sin(angle), strokePaint);
    }

    private static void cw_drawVirtualPoly(Canvas ctx, Body body, Vector2[] vtx) {
        Vector2 p0 = body.getWorldPoint(vtx[0]);
        Path path = new Path();
        path.moveTo(p0.x, p0.y);
        for (int i = 1; i < vtx.length; i++) {
            Vector2 p = body.getWorldPoint(vtx[i]);
            path.lineTo(p.x, p.y);
        }
        path.close();
        ctx.drawPath(path, fillPaint);
        ctx.drawPath(path, strokePaint);
    }

    static void cw_drawFloor(Canvas ctx, Body[] cw_floorTiles) {
        float camera_x = Game.Camera.pos.x;

        fillPaint.setColor(rgb(119, 119, 119));
        strokePaint.setColor(Color.BLACK);

        int k;
//        if (Game.Camera.pos.x - 10 > 0) {
//            k = (int) Math.floor((Game.Camera.pos.x - 10) / 1.5);
//        } else {
//            k = 0;
//        }
k=0;
        // console.log(k);

        outer_loop:
        for (; k < cw_floorTiles.length; k++) {
            Body b = cw_floorTiles[k];
            for (Fixture f : b.getFixtureList()) {
                PolygonShape s = (PolygonShape) f.getShape();
                Vector2 tmp = new Vector2();
                s.getVertex(0, tmp);

                float shapePosition = b.getWorldPoint(tmp).x;
                if (shapePosition > (camera_x - Game.spaceLeftToCam)) {
                    if (shapePosition < camera_x + 30) {
                        cw_drawVirtualPoly(ctx, b, getVertices(s));
                    } else {
                        break outer_loop;
                    }
                }
            }
        }
    }

    static void drawCar(CwCar myCar, Canvas ctx) {
        double wheelMinDensity = CarSchema.CarConstants.wheelMinDensity;
        double wheelDensityRange = CarSchema.CarConstants.wheelDensityRange;

        if (!myCar.getAlive()) {
            return;
        }
        Vector2 myCarPos = myCar.getPosition();

        if (myCarPos.x < (Game.Camera.pos.x - 5)) {
            // too far behind, don't draw
            return;
        }

        Body[] wheels = myCar.getCar().car.wheels;

        for (Body b : wheels) {
            for (Fixture f : b.getFixtureList()) {
                CircleShape s = (CircleShape) f.getShape();
                int color = (int) Math.round(255 - (255 * (f.getDensity() - wheelMinDensity)) / wheelDensityRange);
                int rgbcolor = rgb(color, color, color);

                fillPaint.setColor(rgbcolor);
                strokePaint.setColor(rgb(68, 68, 68));
                cw_drawCircle(ctx, b, s.getPosition(), s.getRadius(), b.getAngle());
            }
        }

        if (myCar.isElite()) {
            strokePaint.setColor(rgb(63, 114, 175));
            fillPaint.setColor(rgb(219, 226, 239));
        } else {
            strokePaint.setColor(rgb(247, 200, 115));
            fillPaint.setColor(rgb(250, 235, 205));
        }

        CarSchema.Chassis chassis = myCar.getCar().car.chassis;

        for (Fixture f : chassis.body.getFixtureList()) {
            PolygonShape cs = (PolygonShape) f.getShape();

            cw_drawVirtualPoly(ctx, chassis.body, getVertices(cs));
        }
    }
}
