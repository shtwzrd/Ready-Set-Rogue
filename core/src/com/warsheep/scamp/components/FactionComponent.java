package com.warsheep.scamp.components;

import com.badlogic.ashley.core.Component;

import java.util.Arrays;
import java.util.List;

public class FactionComponent extends Component {

    public List<Faction> factions = Arrays.asList(Faction.EVIL);

    public enum Faction {
        GOOD, EVIL, NEUTRAL
    }

}
