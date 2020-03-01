package edu.ttp.gengame

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World

internal class Scene(val world: World, val floorTiles: Array<Body>, private val finishLine: Double)
