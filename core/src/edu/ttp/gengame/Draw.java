package edu.ttp.gengame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.ShortArray;


class Draw {
    static ShapeRenderer renderer = new ShapeRenderer();
    private static EarClippingTriangulator ear = new EarClippingTriangulator();

    private static Vector2[] getVertices(PolygonShape shape) {
        Vector2[] vertices = new Vector2[shape.getVertexCount()];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = new Vector2();
            shape.getVertex(i, vertices[i]);
        }
        return vertices;
    }

    private static void cwDrawCircle(Body body, Vector2 center, float radius, float angle,
                                     Color fillColor, @SuppressWarnings("SameParameterValue") Color strokeColor) {

        Vector2 p = body.getWorldPoint(center);

        renderer.setColor(fillColor);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.circle(p.x, p.y, radius, 35);
        renderer.end();

        renderer.setColor(strokeColor);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.circle(p.x, p.y, radius, 35);
        renderer.end();

        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.line(p.x, p.y, p.x + radius * (float) Math.cos(angle),
                p.y + radius * (float) Math.sin(angle));
        renderer.end();
    }

    private static void cwDrawVirtualPoly(Body body, Vector2[] vtx, Color fillColor, Color strokeColor) {
        float[] v = new float[vtx.length * 2];
        for (int i = 0; i < vtx.length; i++) {
            Vector2 p = body.getWorldPoint(vtx[i]);
            v[2 * i] = p.x;
            v[2 * i + 1] = p.y;
        }

        ShortArray arrRes = ear.computeTriangles(v);

        renderer.setColor(fillColor);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < arrRes.size - 2; i = i + 3) {
            float x1 = v[arrRes.get(i) * 2];
            float y1 = v[(arrRes.get(i) * 2) + 1];

            float x2 = v[(arrRes.get(i + 1)) * 2];
            float y2 = v[(arrRes.get(i + 1) * 2) + 1];

            float x3 = v[arrRes.get(i + 2) * 2];
            float y3 = v[(arrRes.get(i + 2) * 2) + 1];

            renderer.triangle(x1, y1, x2, y2, x3, y3);
        }
        renderer.end();

        renderer.setColor(strokeColor);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.polygon(v);
        renderer.end();
    }

    static void cwDrawFloor(Body[] cw_floorTiles) {
        float camera_x = Game.Camera.pos.x;

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
                        cwDrawVirtualPoly(b, getVertices(s), new Color(119/255f, 119/255f, 119/255f, 1f), Color.BLACK);
                    } else {
                        break outer_loop;
                    }
                }
            }
        }
    }

    static void drawCar(CwCar myCar) {
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
                float color = (float) (1 - (1 * (f.getDensity() - wheelMinDensity)) / wheelDensityRange);
                cwDrawCircle(b, s.getPosition(), s.getRadius(), b.getAngle(), new Color(color, color, color, 1f), Color.BLACK);
            }
        }

        Color fillColor, strokeColor;
        if (myCar.isElite()) {
            fillColor = new Color(219/255f, 226/255f, 239/255f, 1f);
            strokeColor = new Color(63/255f, 114/255f, 175/255f, 1f);
        } else {
            fillColor = new Color(250/255f, 235/255f, 205/255f, 1f);
            strokeColor = new Color(247/255f, 200/255f, 115/255f, 1f);
        }

        CarSchema.Chassis chassis = myCar.getCar().car.chassis;

        for (Fixture f : chassis.body.getFixtureList()) {
            PolygonShape cs = (PolygonShape) f.getShape();

            cwDrawVirtualPoly(chassis.body, getVertices(cs), fillColor, strokeColor);
        }
    }
}
