package com.warsheep.scamp.adt;

import com.badlogic.gdx.math.Vector2;

public interface Container {
   public int x();
   public int y();
   public int width();
   public int height();
   public Vector2 center();
}
