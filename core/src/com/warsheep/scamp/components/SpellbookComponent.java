package com.warsheep.scamp.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;

public class SpellbookComponent extends Component {

    public Entity lastSpellCast;
    public ArrayList<Entity> spellbook = new ArrayList<>();

}
