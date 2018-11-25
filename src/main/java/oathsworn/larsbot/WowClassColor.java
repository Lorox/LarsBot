package oathsworn.larsbot;

import com.google.common.collect.ImmutableMap;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

final class WowClassColor {

    private static final Color DEATH_KNIGHT = new Color(196, 31, 59);
    private static final Color DEMON_HUNTER = new Color(163, 48, 201);
    private static final Color DRUID = new Color(255, 125, 10);
    private static final Color HUNTER = new Color(171, 212, 115);
    private static final Color MAGE = new Color(64, 199, 235);
    private static final Color MONK = new Color(0, 255, 150);
    private static final Color PALADIN = new Color(245, 140, 186);
    private static final Color PRIEST = new Color(255, 255, 255);
    private static final Color ROGUE = new Color(255, 245, 105);
    private static final Color SHAMAN = new Color(0, 112, 222);
    private static final Color WARLOCK = new Color(135, 135, 237);
    private static final Color WARRIOR = new Color(199, 156, 110);

    private final ImmutableMap<Integer, Color> classColors;


    public Color getClassColor(int id) {
        return classColors.getOrDefault(id, Color.BLACK);
    }

    public WowClassColor() {
        Map<Integer, Color> colorMap = new HashMap<>();
        colorMap.put(1, WARRIOR);
        colorMap.put(2, PALADIN);
        colorMap.put(3, HUNTER);
        colorMap.put(4, ROGUE);
        colorMap.put(5, PRIEST);
        colorMap.put(6, DEATH_KNIGHT);
        colorMap.put(7, SHAMAN);
        colorMap.put(8, MAGE);
        colorMap.put(9, WARLOCK);
        colorMap.put(10, MONK);
        colorMap.put(11, DRUID);
        colorMap.put(12, DEMON_HUNTER);

        classColors = ImmutableMap.copyOf(colorMap);
    }
}
