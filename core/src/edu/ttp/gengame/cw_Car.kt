package edu.ttp.gengame

import com.badlogic.gdx.math.Vector2

class cw_Car internal constructor(internal val car: CarRunner) {
    //private int frames;
    internal var alive: Boolean = false
    internal val is_elite: Boolean

    //cw_Car.prototype.getPosition = function () {
    //  return this.car.car.chassis.GetPosition();
    //}
    internal val position: Vector2 = this.car.car.chassis.body.position

    init {
        //this.frames = 0;
        this.alive = true
        this.is_elite = car.def.is_elite
        //  this.healthBar = document.getElementById("health" + car_def.index).style;
        //  this.healthBarText = document.getElementById("health" + car_def.index).nextSibling.nextSibling;
        //  this.healthBarText.innerHTML = car_def.index;
        //  this.minimapmarker = document.getElementById("bar" + car_def.index);
        //
        //  if (this.is_elite) {
        //    this.healthBar.backgroundColor = "#3F72AF";
        //    this.minimapmarker.style.borderLeft = "1px solid #3F72AF";
        //    this.minimapmarker.innerHTML = car_def.index;
        //  } else {
        //    this.healthBar.backgroundColor = "#F7C873";
        //    this.minimapmarker.style.borderLeft = "1px solid #F7C873";
        //    this.minimapmarker.innerHTML = car_def.index;
        //  }
    }

    internal fun kill(currentRunner: Run) {
        //this.minimapmarker.style.borderLeft = "1px solid #3F72AF";
        //        var finishLine = currentRunner.scene.finishLine
        //        var max_car_health = constants.max_car_health;

        // Always -1 ???
        //        switch(status){
        //            case 1: {
        //                this.healthBar.width = "0";
        //                break
        //            }
        //            case -1: {
        //                this.healthBarText.innerHTML = "&dagger;";
        //                this.healthBar.width = "0";
        //                break
        //            }
        //        }
        this.alive = false
    }
}
