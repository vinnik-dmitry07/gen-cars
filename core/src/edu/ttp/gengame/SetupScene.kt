package edu.ttp.gengame

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import kotlin.math.cos
import kotlin.math.sin

 object SetupScene {
    fun setupScene(): Scene {
        //        var world = new b2World(world_def.gravity, world_def.doSleep);
        val world = World(Game.WordDef.gravity, Game.WordDef.doSleep)

        //        var floorTiles = cw_createFloor(
        //                world,
        //                world_def.floorseed,
        //                world_def.tileDimensions,
        //                world_def.maxFloorTiles,
        //                world_def.mutable_floor
        //        );
        val floorTiles = cwCreateFloor(
                world,
                Game.WordDef.floorseed
        )

        //        var last_tile = floorTiles[
        //                floorTiles.length - 1
        //                ];
        //        var last_fixture = last_tile.GetFixtureList();
        //        var tile_position = last_tile.GetWorldPoint(
        //                last_fixture.GetShape().m_vertices[3]
        //        );
        //        world.finishLine = tile_position.x; ???
        val lastTile = floorTiles[floorTiles.size - 1]
        val lastFixture = lastTile.fixtureList.get(lastTile.fixtureList.size - 1)
        val tmp = Vector2()
        (lastFixture.shape as PolygonShape).getVertex(0, tmp)

        return Scene(world, floorTiles)
    }

    private fun cwCreateFloor(world: World, floorseed: Long): Array<Body> {
        var lastTile: Body
        var tilePosition = Vector2(-Game.spaceLeftToCam, 0f)
        val cwFloorTiles = arrayOfNulls<Body>(Game.WordDef.maxFloorTiles)
        Game.random.setSeed(floorseed)

        for (k in 0 until Game.WordDef.maxFloorTiles) {
            val angle: Double
            if (!Game.WordDef.mutable_floor) {
                // keep old impossible tracks if not using mutable floors
                angle = (Game.random.nextDouble() * 3 - 1.5) * 1.5 * k.toDouble() / Game.WordDef.maxFloorTiles
                lastTile = cwCreateFloorTile(
                        world, tilePosition, angle
                )
            } else {
                // if path is mutable over races, create smoother tracks
                angle = (Game.random.nextDouble() * 3 - 1.5) * 1.2 * k.toDouble() / Game.WordDef.maxFloorTiles
                lastTile = cwCreateFloorTile(
                        world, tilePosition, angle
                )
            }
            cwFloorTiles[k] = lastTile
            val lastFixture = lastTile.fixtureList
            val temp = Vector2()
            (lastFixture.get(0).shape as PolygonShape).getVertex(if (angle >= 0) 1 else 0, temp) // ?
            tilePosition = lastTile.getWorldPoint(temp)
        }
        return cwFloorTiles.requireNoNulls()
    }

    private fun cwCreateFloorTile(world: World, position: Vector2, angle: Double): Body {
        val bodyDef = BodyDef()

        bodyDef.position.set(position.x, position.y)
        val body = world.createBody(bodyDef)
        val fixDef = FixtureDef()
        fixDef.shape = PolygonShape()
        fixDef.friction = 0.5f

        val coords : Array<Vector2> = arrayOf(
            Vector2(0f, 0f),
            Vector2(0f, -Game.WordDef.tileDimensions.y),
            Vector2(Game.WordDef.tileDimensions.x, -Game.WordDef.tileDimensions.y),
            Vector2(Game.WordDef.tileDimensions.x, 0f)
        )

        val center = Vector2(0f, 0f)

        val newcoords = cwRotateFloorTile(coords, center, angle)

        (fixDef.shape as PolygonShape).set(newcoords)

        body.createFixture(fixDef)
        return body
    }

    private fun cwRotateFloorTile(coords: Array<Vector2>, center: Vector2, angle: Double): Array<Vector2> {
        return coords.map{ coord ->
            Vector2(
                    cos(angle).toFloat() * (coord.x - center.x) - sin(angle).toFloat() * (coord.y - center.y) + center.x,
                    sin(angle).toFloat() * (coord.x - center.x) + cos(angle).toFloat() * (coord.y - center.y) + center.y
            )
        }.toTypedArray()
    }
}



