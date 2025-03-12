package com.example.predatorsandpreys;

import java.util.ArrayList;
import java.util.Random;

public class Bunny extends Creature {

    private int multiplyChance;

    public Bunny(LivingSpace lm, int x, int y) {
        super(lm, x, y);
        setSignSymbol('B');
        multiplyChance = 10;
    }
    public void move() {
        if (newborn) {
            this.newborn = false;
            return;
        }
        ArrayList<Point> choices = new ArrayList<>();
        if (x > 0 && livingSpace.getFromPoint(x - 1, y) == null)
            choices.add(new Point(x - 1, y));
        if (y > 0 && livingSpace.getFromPoint(x, y - 1) == null)
            choices.add(new Point(x, y - 1));
        if (x < livingSpace.getWidthCells() - 1 && livingSpace.getFromPoint(x + 1, y) == null)
            choices.add(new Point(x + 1, y));
        if (y < livingSpace.getHeightCells() - 1 && livingSpace.getFromPoint(x, y + 1) == null)
            choices.add(new Point(x, y + 1));
        if (x > 0 && y > 0 && livingSpace.getFromPoint(x - 1, y - 1) == null)
            choices.add(new Point(x - 1, y - 1));
        if (x < livingSpace.getWidthCells() - 1 && y > 0 && livingSpace.getFromPoint(x + 1, y - 1) == null)
            choices.add(new Point(x + 1 , y - 1));
        if (x > 0 && y < livingSpace.getHeightCells() - 1 && livingSpace.getFromPoint(x - 1, y + 1) == null)
            choices.add(new Point(x - 1, y + 1));
        if (x < livingSpace.getWidthCells() - 1 && y < livingSpace.getHeightCells() - 1 && livingSpace.getFromPoint(x + 1, y + 1) == null)
            choices.add(new Point(x + 1, y + 1));
        if (choices.size() == 0)
            return;
        Random random = new Random();
        int choice = random.nextInt(choices.size());
        livingSpace.moveCreature(new Point(x, y), choices.get(choice));
        if (random.nextInt(100) < multiplyChance)
            livingSpace.createCreature('B', true);
        int new_x = choices.get(choice).x;
        int new_y = choices.get(choice).y;
        if (new_y > y || (new_y == y && new_x > x))
            this.stepped = true;
        x = new_x;
        y = new_y;
    }
}
