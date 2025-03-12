package com.example.predatorsandpreys;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;

import java.util.*;

public class LivingSpace extends Pane {

    private ArrayList<ArrayList<Creature>> space_field;
    private int width, height;
    private int bunnyAmount;
    private int wolfMAmount;
    private int wolfFAmount;
    private int healthLimit;
    private int step_num; // Шаг N

    private Canvas canvas;
    private int widthpx, heightpx;
    private int cellxpx, cellypx;
    private GraphicsContext gc;
    private Image green, rabbit, wolfMale, wolfFemale;

    private String rabbitMoveSound, wolfMoveSound, rabbitMultiplySound, wolfMultiplySound, rabbitDeadSound, wolfDeadSound;
    private int rabbitMoveCount, wolfMoveCount, rabbitMultiplyCount, wolfMultiplyCount, rabbitDeadCount, wolfDeadCount;
    private AudioManager audioManager;

    public LivingSpace() {
        green = new Image(getClass().getResource("/com/example/predatorsandpreys/grass_img.png").toExternalForm());
        rabbit = new Image(getClass().getResource("/com/example/predatorsandpreys/bunny_img.png").toExternalForm());
        wolfMale = new Image(getClass().getResource("/com/example/predatorsandpreys/wolf_male_img.png").toExternalForm());
        wolfFemale = new Image(getClass().getResource("/com/example/predatorsandpreys/wolf_female_img.png").toExternalForm());
        rabbitMoveSound = "/com/example/predatorsandpreys/rabbit_move.mp3";
        wolfMoveSound = "/com/example/predatorsandpreys/wolf_move.mp3";
        rabbitMultiplySound = "/com/example/predatorsandpreys/rabbit_multiply.mp3";
        wolfMultiplySound = "/com/example/predatorsandpreys/wolf_multiply.mp3";
        rabbitDeadSound = "/com/example/predatorsandpreys/rabbit_dead.mp3";
        wolfDeadSound = "/com/example/predatorsandpreys/wolf_dead.mp3";
        this.widthpx = 500;
        this.heightpx = 500;
        audioManager = new AudioManager();
        reset();
    }

    public int getStep() {
        return step_num;
    }

    public void setWidthCells(int cells) { this.width = cells; }

    public void setHeightCells(int cells) {
        this.height = cells;
    }

    public void setWidth(int px) { }

    public void setDimension(Point cells) {
        this.width = cells.x;
        this.height = cells.y;
    }

    public void setHealthLimit(int hm) {
        this.healthLimit = hm;
    }
    public void setBunnyAmount(int amount) {
        if (wolfMAmount + amount <= space_field.size() * space_field.get(0).size())
            this.bunnyAmount = amount;
    }

    public void setWolfMAmount(int amount) {
        if (bunnyAmount + wolfFAmount + amount <= space_field.size() * space_field.get(0).size())
            this.wolfMAmount = amount;
    }

    public void setWolfFAmount(int amount) {
        if (bunnyAmount + wolfMAmount + amount <= space_field.size() * space_field.get(0).size())
            this.wolfFAmount = amount;
    }

    public void reset() {
        this.space_field = new ArrayList<>();
        this.bunnyAmount = 0;
        this.wolfMAmount = 0;
        this.wolfFAmount = 0;
        this.step_num = 0;
    }

    private void fieldInitialization() {
        for (int i = 0; i < height; i++) {
            ArrayList<Creature> tmp = new ArrayList<>();
            for (int j = 0; j < width; j++)
                tmp.add(null);
            this.space_field.add(tmp);
        }
    }

    public void simInitialization(int width, int height, int bunnyAmount, int wolfMAmount, int wolfFAmount) {
        reset();
        this.rabbitMoveCount = 0;
        this.wolfMoveCount = 0;
        this.rabbitMultiplyCount = 0;
        this.wolfMultiplyCount = 0;
        this.rabbitDeadCount = 0;
        this.wolfDeadCount = 0;
        setWidthCells(width);
        setHeightCells(height);
        fieldInitialization();
        System.out.println("Поле инициализировано.");
        setBunnyAmount(bunnyAmount);
        setWolfMAmount(wolfMAmount);
        setWolfFAmount(wolfFAmount);
        putCreatures();
        System.out.println("B" + this.bunnyAmount + " WM" + this.wolfMAmount + " WF" + this.wolfFAmount);
        this.cellxpx = this.widthpx / this.width;
        this.cellypx = this.heightpx / this.height;
        this.canvas = new Canvas(this.widthpx, this.heightpx);
        this.gc = this.canvas.getGraphicsContext2D();
        this.getChildren().add(canvas);
    }

    public void draw() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Рисуем фон клетки
                gc.drawImage(green, x * this.cellxpx, y * this.cellypx, this.cellxpx, this.cellypx);

                // Рисуем существо, если оно есть
                Creature creature = space_field.get(y).get(x);
                if (creature != null) {
                    Image creatureImage = null;
                    if (creature instanceof Bunny)
                        creatureImage = rabbit;
                    else if (creature instanceof Wolf)
                        if (((Wolf) creature).getMale())
                            creatureImage = wolfMale;
                        else
                            creatureImage = wolfFemale;
                    if (creatureImage != null) {
                        gc.drawImage(creatureImage, x * this.cellxpx, y * this.cellypx, this.cellxpx, this.cellypx);
                    }
                }
            }
        }
    }

    public void clear() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void putCreatures() {
        Random random = new Random();
        Point randomPoint = new Point(0, 0);
        int bunnyAmount = this.bunnyAmount;
        int wolfMAmount = this.wolfMAmount;
        int wolfFAmount = this.wolfFAmount;
        while (bunnyAmount + wolfMAmount + wolfFAmount > 0) {
            if (bunnyAmount > 0) {
                randomPoint.y = random.nextInt(this.height);
                randomPoint.x = random.nextInt(this.width);
                if (getFromPoint(randomPoint) == null) {
                    createCreature(randomPoint, 'B', false);
                    bunnyAmount--;
                    this.bunnyAmount--;
                }
            } if (wolfMAmount > 0) {
                randomPoint.y = random.nextInt(this.height);
                randomPoint.x = random.nextInt(this.width);
                if (getFromPoint(randomPoint) == null) {
                    createCreature(randomPoint, 'W', false);
                    wolfMAmount--;
                    this.wolfMAmount--;
                }
            } if (wolfFAmount > 0) {
                randomPoint.y = random.nextInt(this.height);
                randomPoint.x = random.nextInt(this.width);
                if (getFromPoint(randomPoint) == null) {
                    createCreature(randomPoint, 'w', false);
                    wolfFAmount--;
                    this.wolfFAmount--;
                }
            }
        }
    }
    public Creature getFromPoint(int x, int y) {
        if (x < 0  || y < 0  || y >= this.height ||  x >= this.width) {
            throw new IndexOutOfBoundsException("Координаты (" + x + ", " + y + ") выходят за границы поля.");
        }
        return space_field.get(y).get(x);
    }

    public Creature getFromPoint(Point point) {
        if (point.x < 0 ||  point.y < 0 ||  point.y >= space_field.size() || point.x >= space_field.get(0).size()) {
            throw new IndexOutOfBoundsException("Координаты (" + point.x + ", " + point.y + ") выходят за границы поля.");
        }
        return space_field.get(point.y).get(point.x);
    }

    public int getWidthCells() {
        if (height == 0)
            return 0;
        return width;
    }

    public int getHeightCells() {
        return height;
    }

    public void moveCreature(Point pos1, Point pos2) {
        if (getFromPoint(pos1) == null || getFromPoint(pos2) != null) {
            throw new IndexOutOfBoundsException("Клетка уже занята!");
        }
        if (getFromPoint(pos1) instanceof Bunny)
            rabbitMoveCount++;
        else if (getFromPoint(pos1) instanceof Wolf)
            wolfMoveCount++;
        space_field.get(pos2.y).set(pos2.x, space_field.get(pos1.y).get(pos1.x));
        space_field.get(pos1.y).set(pos1.x, null);
    }
    public void printSpace() {
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                if (space_field.get(i).get(j) == null)
                    System.out.print("_\t");
                else {
                    System.out.print(space_field.get(i).get(j).getSignSymbol());
                    System.out.print('\t');
                }
            }
            System.out.print('\n');
        }
    }

    public int getWolfCount() {
        return this.wolfFAmount + this.wolfMAmount;
    }

    public int getBunnyAmount() {
        return this.bunnyAmount;
    }

    public int getWolfMAmount() {
        return this.wolfMAmount;
    }

    public int getWolfFAmount() {
        return this.wolfFAmount;
    }

    // t - тип существа. B - кролик, W - волк.
    public void createCreature(Point point, char t, boolean newBorn) {
        if (getFromPoint(point) == null) {
            if (t == 'B') {
                this.space_field.get(point.y).set(point.x, new Bunny(this, point.x, point.y));
                this.space_field.get(point.y).get(point.x).newborn = true;
                this.bunnyAmount++;
                this.rabbitMultiplyCount++;
            } else if (t == 'W') {
                this.space_field.get(point.y).set(point.x, new Wolf(this, point.x, point.y, true, healthLimit));
                this.space_field.get(point.y).get(point.x).newborn = true;
                this.wolfMAmount++;
                this.wolfMultiplyCount++;
            } else if (t == 'w') {
                this.space_field.get(point.y).set(point.x, new Wolf(this, point.x, point.y, false, healthLimit));
                this.space_field.get(point.y).get(point.x).newborn = true;
                this.wolfFAmount++;
                this.wolfMultiplyCount++;
            }
        }
    }

    public void createCreature(char t, boolean newBorn) {
        Random random = new Random();
        Point point = new Point(random.nextInt(width), random.nextInt(height));
        while (bunnyAmount + wolfMAmount <= width * height) {
            if (getFromPoint(point) == null) {
                if (t == 'B') {
                    space_field.get(point.y).set(point.x, new Bunny(this, point.x, point.y));
                    this.space_field.get(point.y).get(point.x).newborn = true;
                    bunnyAmount++;
                    this.rabbitMultiplyCount++;
                }
                else if (t == 'W') {
                    space_field.get(point.y).set(point.y, new Wolf(this, point.x, point.y));
                    this.space_field.get(point.y).get(point.x).newborn = true;
                    this.wolfMultiplyCount++;
                    wolfMAmount++;
                }
                break;
            }
            point.x = random.nextInt(width);
            point.y = random.nextInt(height);
        }
    }

    public void killCreature(Point point) {
        Creature creature = getFromPoint(point);
        if (creature == null) {
            return; // Нет существа в этой точке
        }

        if (creature instanceof Bunny) {
            this.bunnyAmount--;
            this.rabbitDeadCount++;
        } else if (creature instanceof Wolf) {
            this.wolfDeadCount++;
            if (((Wolf) creature).getMale())
                this.wolfMAmount--;
            else
                this.wolfFAmount--;
            //System.out.println("DEAD WOLF AT " + point.x + ";" + point.y + ": " + getFromPoint(point) + ((Wolf) creature).getHealth());
        }
        space_field.get(point.y).set(point.x, null);
        //System.out.println("DEAD CREATURE AT " + point.x + ";" + point.y + ": " + getFromPoint(point) + '\n');
    }
    public void step() {
        Random random = new Random();
        this.rabbitMoveCount = 0;
        this.wolfMoveCount = 0;
        this.rabbitMultiplyCount = 0;
        this.wolfMultiplyCount = 0;
        this.rabbitDeadCount = 0;
        this.wolfDeadCount = 0;
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                if (getFromPoint(j, i) != null)
                    if (getFromPoint(j, i).stepped == false)
                        getFromPoint(j, i).move();
                    else
                        getFromPoint(j, i).stepped = false;
            }
        }
        step_num++;
        this.wolfMultiplyCount *= 8;
        this.rabbitDeadCount *= 7;
        this.wolfDeadCount *= 6;
        this.rabbitMultiplyCount *= 5;
        this.wolfMultiplyCount *= 5;
        HashMap<String, Integer> stats = new HashMap<String, Integer>();
        stats.put("bunny_move", rabbitMoveCount);
        stats.put("wolf_move", wolfMoveCount);
        stats.put("bunny_multiply", rabbitMultiplyCount);
        stats.put("wolf_multiply", wolfMultiplyCount);
        stats.put("bunny_dead", rabbitDeadCount);
        stats.put("wolf_dead", wolfDeadCount);
        System.out.println(stats);
        String maxKey = Collections.max(stats.entrySet(), Map.Entry.comparingByValue()).getKey();
        if (maxKey.equals("bunny_move")) {
            audioManager.addSound(rabbitMoveSound);
        } else if (maxKey.equals("wolf_move")) {
            audioManager.addSound(wolfMoveSound);
        } else if (maxKey.equals("bunny_multiply")) {
            audioManager.addSound(rabbitMultiplySound);
        } else if (maxKey.equals("wolf_multiply")) {
            audioManager.addSound(wolfMultiplySound);
        } else if (maxKey.equals("bunny_dead")) {
            audioManager.addSound(rabbitDeadSound);
        } else {
            audioManager.addSound(wolfDeadSound);
        }

        System.gc();
    }

    public void terminalRun() {
        while (step_num <= 3000) {
            System.out.println("Шаг " + step_num);
            System.out.println("Волков: " + getWolfCount() + "; кроликов: " + bunnyAmount);
            int mid = 0;
            int mid_count = 0;
            for (int i = 0; i < space_field.size(); i++) {
                for (int j = 0; j < space_field.get(i).size(); j++) {
                    if (space_field.get(i).get(j) instanceof Wolf) {
                        mid++;
                        mid_count += ((Wolf) space_field.get(i).get(j)).getHealth();
                    }
                }
            }
            if (mid > 0)
                System.out.println(mid_count / mid);
            printSpace();
            step();
            if (bunnyAmount + wolfMAmount + wolfFAmount > this.height * this.width) {
                System.out.println("Переполнение жизненного пространства!");
                break;
            }
            if (wolfMAmount + wolfFAmount <= 0) {
                System.out.println("Популяция волков вымерла. Экосистеме конец.");
                break;
            }
        }
        System.out.println("Симуляция завершена!");
    }
}