package edu.ttp.gengame

import java.util.ArrayList

 class Ghost {

    private val enableGhost = true

     inner class Replay(val num_frames: Int, val frames: List<Int>)

    fun ghostCreateReplay(): Replay? {
        return if (!enableGhost) null else Replay(0, ArrayList())

    }

    //    function ghost_add_replay_frame(replay, car) {
    //        if (!enable_ghost)
    //            return;
    //        if (replay == null)
    //            return;
    //
    //        var frame = ghost_get_frame(car);
    //        replay.frames.push(frame);
    //        replay.num_frames++;
    //    }
}
