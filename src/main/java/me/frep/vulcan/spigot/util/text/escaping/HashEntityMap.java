package me.frep.vulcan.spigot.util.text.escaping;

import java.util.HashMap;

public class HashEntityMap extends BidirectionalEntityMap {

    public HashEntityMap() {
        super(new HashMap<>(), new HashMap<>());
    }
}
