package com.example.predatorsandpreys;

import java.util.ArrayList;
import java.util.Random;

public class Wolf extends Creature {
    private int health, health_limit;
    private int hungerBorder;
    private int matingBorder;
    private int multiplyChance;
    private boolean isMale;

    public Wolf(LivingSpace lm, int x, int y) {
        super(lm, x, y);
        Random random = new Random();
        this.isMale = random.nextBoolean();
        if (isMale)
            setSignSymbol('W');
        else
            setSignSymbol('w');
        this.health_limit = 8;
        this.health = this.health_limit;
        this.multiplyChance = 75;
        this.hungerBorder = this.health_limit / 2;
        this.matingBorder = this.health_limit / 4 * 3;
    }

    public Wolf(LivingSpace lm, int x, int y, boolean male, int hm) {
        super(lm, x, y);
        this.isMale = male;
        if (isMale)
            setSignSymbol('W');
        else
            setSignSymbol('w');
        this.health_limit = hm;
        this.health = this.health_limit / 4 * 3;
        this.multiplyChance = 75;
        this.hungerBorder = this.health_limit / 2;
        this.matingBorder = this.health_limit / 4 * 3;

    }

    public boolean getMale() {
        return isMale;
    }
    public int getHealth() {
        // System.out.println("Health: " + health);
        return health;
    }

    private void checkPoint(Point point, ArrayList<Point> wolf_choices, ArrayList<Point> move_choices, ArrayList<Point> bunny_choices) {
        Creature cell_check = livingSpace.getFromPoint(point.x, point.y);
        if (cell_check == null)
            move_choices.add(point);
        else if (cell_check.getClass() == Wolf.class && ((Wolf) cell_check).getMale() != isMale)
            wolf_choices.add(point);
        else if (cell_check.getClass() == Bunny.class)
            bunny_choices.add(point);

    }
    public void move() {
        if (newborn) {
            this.newborn = false;
            return;
        }
        Random random = new Random();
        ArrayList<Point> wolf_choices = new ArrayList<>();
        ArrayList<Point> move_choices = new ArrayList<>();
        ArrayList<Point> bunny_choices = new ArrayList<>();
        if (x > 0)
            checkPoint(new Point(x - 1, y), wolf_choices, move_choices, bunny_choices);
        if (y > 0)
            checkPoint(new Point(x, y - 1), wolf_choices, move_choices, bunny_choices);
        if (x > 0 && y > 0)
            checkPoint(new Point(x - 1, y - 1), wolf_choices, move_choices, bunny_choices);
        if (x < livingSpace.getWidthCells() - 1)
            checkPoint(new Point(x + 1, y), wolf_choices, move_choices, bunny_choices);
        if (y < livingSpace.getHeightCells() - 1)
            checkPoint(new Point(x, y + 1), wolf_choices, move_choices, bunny_choices);
        if (x < livingSpace.getWidthCells() - 1 && y > 0)
            checkPoint(new Point(x + 1, y - 1), wolf_choices, move_choices, bunny_choices);
        if (x < livingSpace.getWidthCells() - 1 && y < livingSpace.getHeightCells() - 1)
            checkPoint(new Point(x + 1, y + 1), wolf_choices, move_choices, bunny_choices);
        if (x > 0 && y < livingSpace.getHeightCells() - 1)
            checkPoint(new Point(x - 1, y + 1), wolf_choices, move_choices, bunny_choices);
        if (this.health <= hungerBorder && bunny_choices.size() > 0) {
            Point toKill = bunny_choices.get(random.nextInt(bunny_choices.size()));
            livingSpace.killCreature(toKill);
            livingSpace.moveCreature(new Point(x, y), toKill);
            this.x = toKill.x;
            this.y = toKill.y;
            if (toKill.y > y || (toKill.y == y && toKill.x > x))
                this.stepped = true;
            this.health = health_limit;
        } else if (this.health > matingBorder && wolf_choices.size() > 0 && move_choices.size() > 0) {
            if (random.nextInt(100) <= this.multiplyChance) {
                int numtoBorn = random.nextInt(move_choices.size());
                Point toBorn = move_choices.get(numtoBorn);
                move_choices.remove(numtoBorn);
                boolean newbornMale = random.nextBoolean();
                if (newbornMale)
                    livingSpace.createCreature(toBorn, 'W', true);
                else
                    livingSpace.createCreature(toBorn, 'w', true);

            }
            if (move_choices.size() > 0) {
                Point toMove = move_choices.get(random.nextInt(move_choices.size()));
                livingSpace.moveCreature(new Point(x, y), toMove);
                if (toMove.y > y || (toMove.y == y && toMove.x > x))
                    this.stepped = true;
                this.x = toMove.x;
                this.y = toMove.y;
            }
        } else if (move_choices.size() > 0) {
            Point toMove = move_choices.get(random.nextInt(move_choices.size()));
            livingSpace.moveCreature(new Point(x, y), toMove);
            if (toMove.y > y || (toMove.y == y && toMove.x > x))
                this.stepped = true;
            this.x = toMove.x;
            this.y = toMove.y;
        }
        health--;
        if (health == 0)
            livingSpace.killCreature(new Point(x, y));
    }

    public void reproduce() {

    }
}