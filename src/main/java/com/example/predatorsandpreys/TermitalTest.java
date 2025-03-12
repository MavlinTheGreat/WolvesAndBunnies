package com.example.predatorsandpreys;

public class TermitalTest {
    public static void main(String[] args) {
        LivingSpace livingSpace = new LivingSpace();
        livingSpace.simInitialization(30, 10, 10, 20, 10);
        livingSpace.terminalRun();
    }
}
