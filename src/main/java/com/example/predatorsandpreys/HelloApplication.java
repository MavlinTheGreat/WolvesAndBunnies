package com.example.predatorsandpreys;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class HelloApplication extends Application {

    // ---ЭЛЕМЕНТЫ УПРАВЛЕНИЯ---

    // Параметры симуляции
    TextField widthField; // ячеек в ширину
    TextField heightField; // ячеек в высоту
    TextField rabbitField; // кроликов разместить
    TextField wolfMaleField; // мужиков волчар разместить
    TextField wolfFemaleField; // женщин волчар разместить
    TextField healthField; // здоровье волков

    // Выводы
    Label bunnyCountLabel;
    Label wolfMCountLabel;
    Label wolfFCountLabel;
    Label stepLabel;

    // Кнопки
    Button applyButton; // применение введённых параметров и перерисовка окна на шаге 0
    Button stepButton; // сделать один шаг
    Button runButton; // запуск симуляции
    Button stopButton; // остановка симуляции

    // Параметры графика
    CheckBox bunniesCheckbox;
    CheckBox wolfsCheckbox;
    CheckBox wolfMCheckbox;
    CheckBox wolfFCheckbox;

    // График
    LineChart<Number, Number> lineChart;
    XYChart.Series<Number, Number> bunniesSeries;
    XYChart.Series<Number, Number> wolfsSeries;
    XYChart.Series<Number, Number> wolfMSeries;
    XYChart.Series<Number, Number> wolfFSeries;

    // ---ЛОГИКА---
    LivingSpace livingSpace;
    private Timeline timeline;

    public int getNumberFromTextField(TextField textField) {
        try {
            return Integer.parseInt(textField.getText().trim());
        } catch (NumberFormatException e) {
            return 0; // Или можно вывести предупреждение
        }
    }


    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Волки и кролики");

        widthField = new TextField("10");
        heightField = new TextField("10");
        rabbitField = new TextField("3");
        wolfMaleField = new TextField("6");
        wolfFemaleField = new TextField("9");
        healthField = new TextField("10");

        bunnyCountLabel = new Label("Кроликов: 3");
        wolfMCountLabel = new Label("Волков: 6");
        wolfFCountLabel = new Label("Волчих: 9");
        stepLabel = new Label("Шаг 0");

        applyButton = new Button("Принять");
        stepButton = new Button("Шаг");
        runButton = new Button("Пуск");
        stopButton = new Button("Стоп");

        bunniesCheckbox = new CheckBox("Кролики");
        wolfsCheckbox = new CheckBox("Волки");
        wolfMCheckbox = new CheckBox("Волки (M)");
        wolfFCheckbox = new CheckBox("Волки (F)");
        bunniesCheckbox.setSelected(true);
        wolfsCheckbox.setSelected(true);
        wolfMCheckbox.setSelected(true);
        wolfFCheckbox.setSelected(true);

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Динамика популяции");
        bunniesSeries = new XYChart.Series<>();
        bunniesSeries.setName("Кролики");
        wolfsSeries = new XYChart.Series<>();
        wolfsSeries.setName("Волки");
        wolfMSeries = new XYChart.Series<>();
        wolfMSeries.setName("Волки (M)");
        wolfFSeries = new XYChart.Series<>();
        wolfFSeries.setName("Волки (F)");
        lineChart.getData().addAll(bunniesSeries, wolfsSeries, wolfMSeries, wolfFSeries);
        lineChart.setAnimated(false);

        livingSpace = new LivingSpace();
        livingSpace.setHealthLimit(10);
        livingSpace.simInitialization(10, 10, 3, 6, 9);
        livingSpace.draw();

        updateChart();

        HBox global_layout = new HBox(10);
        global_layout.setFillHeight(true);
        VBox setting_layout = new VBox(10);
        HBox control_layout = new HBox();
        HBox info_layout = new HBox(10, stepLabel, bunnyCountLabel, wolfMCountLabel, wolfFCountLabel);
        control_layout.getChildren().addAll(stepButton, runButton, stopButton);
        setting_layout.getChildren().addAll(new Label("Ширина поля"), widthField, new Label("Высота поля"), heightField,
                new Separator(),
                new Label("Число кроликов"), rabbitField, new Label("Число волков-мужчин"), wolfMaleField,
                new Label("Число волков-женщин"), wolfFemaleField,
                new Label("Здоровье волков"), healthField,
                new Separator(),
                applyButton, control_layout,
                new Separator(), info_layout, new Separator(),
                new Label("Вывод на график:"), bunniesCheckbox, wolfsCheckbox, wolfMCheckbox, wolfFCheckbox);
        global_layout.getChildren().addAll(new TitledPane("Настройки симуляции", setting_layout),
                new TitledPane("Жизненное пространство", livingSpace),
                new TitledPane("График", lineChart));

        applyButton.setOnAction(e -> {
            livingSpace.clear();
            stopSimulation(); // Останавливаем анимацию перед изменением параметров
            int w = getNumberFromTextField(widthField);
            int h = getNumberFromTextField(heightField);
            int r = getNumberFromTextField(rabbitField);
            int wM = getNumberFromTextField(wolfMaleField);
            int wF = getNumberFromTextField(wolfFemaleField);
            int he = getNumberFromTextField(healthField);
            livingSpace.setHealthLimit(he);
            livingSpace.simInitialization(w, h, r, wM, wF);
            livingSpace.draw();
            updateLabels();
            clearChart();
        });

        stepButton.setOnAction(e -> {
            stopSimulation(); // Останавливаем анимацию, если идёт
            livingSpace.step();
            livingSpace.draw();
            updateLabels();
            updateChart();
        });

        runButton.setOnAction(e -> {
            stopSimulation(); // Перед запуском новой симуляции останавливаем старую
            timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
                livingSpace.step();
                livingSpace.draw();
                updateLabels();
                updateChart();
                // Условие выхода из цикла
                if (livingSpace.getBunnyAmount() + livingSpace.getWolfMAmount() + livingSpace.getWolfFAmount() >
                        livingSpace.getHeightCells() * livingSpace.getWidthCells() ||
                        livingSpace.getWolfCount() <= 0 ||
                        livingSpace.getStep() >= 30000) {
                    stopSimulation();
                }
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        });

        stopButton.setOnAction(e -> stopSimulation());

        Scene scene = new Scene(global_layout, 1280, 720);
        stage.setScene(scene);
        stage.show();

        bunniesCheckbox.setOnAction(event -> {
            if (lineChart.getData().contains(bunniesSeries))
                lineChart.getData().remove(bunniesSeries);
            else
                lineChart.getData().add(bunniesSeries);
        });

        wolfMCheckbox.setOnAction(event -> {
            if (lineChart.getData().contains(wolfMSeries))
                lineChart.getData().remove(wolfMSeries);
            else
                lineChart.getData().add(wolfMSeries);
        });

        wolfFCheckbox.setOnAction(event -> {
            if (lineChart.getData().contains(wolfFSeries)) {
                lineChart.getData().remove(wolfFSeries);
                wolfFCheckbox.setSelected(false);
            }
            else {
                lineChart.getData().add(wolfFSeries);
                wolfFCheckbox.setSelected(true);
            }
        });
        wolfsCheckbox.setOnAction(event -> {
            if (lineChart.getData().contains(wolfsSeries)) {
                lineChart.getData().remove(wolfsSeries);
                wolfFCheckbox.setSelected(false);
            }
            else {
                lineChart.getData().add(wolfsSeries);
                wolfsCheckbox.setSelected(true);
            }
        });
    }

    private void stopSimulation() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }

    private void updateLabels() {
        stepLabel.setText("Шаг " + livingSpace.getStep());
        bunnyCountLabel.setText("Кроликов: " + livingSpace.getBunnyAmount());
        wolfMCountLabel.setText("Волков: " + livingSpace.getWolfMAmount());
        wolfFCountLabel.setText("Волчиц: " + livingSpace.getWolfFAmount());
    }

    private void updateChart() {
        bunniesSeries.getData().add(new XYChart.Data<>(livingSpace.getStep(), livingSpace.getBunnyAmount()));
        wolfsSeries.getData().add(new XYChart.Data<>(livingSpace.getStep(), livingSpace.getWolfMAmount() + livingSpace.getWolfFAmount()));
        wolfMSeries.getData().add(new XYChart.Data<>(livingSpace.getStep(), livingSpace.getWolfMAmount()));
        wolfFSeries.getData().add(new XYChart.Data<>(livingSpace.getStep(), livingSpace.getWolfFAmount()));
    }

    private void clearChart() {
        bunniesSeries.getData().clear();
        wolfsSeries.getData().clear();
        wolfMSeries.getData().clear();
        wolfFSeries.getData().clear();
        updateChart();
    }



    public static void main(String[] args) {
        launch();
    }
}