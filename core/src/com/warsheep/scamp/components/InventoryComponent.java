package com.warsheep.scamp.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;

public class InventoryComponent extends Component {

    public ArrayList<Entity> inventoryItems = new ArrayList<>();

}
