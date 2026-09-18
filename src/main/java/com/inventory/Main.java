package com.inventory;

import com.inventory.ui.MainMenuUI;
import com.inventory.util.DataSeeder;

/**
 * Application entry point for the Inventory & Sales Management System.
 */
public class Main {

    public static void main(String[] args) {
        DataSeeder seeder = new DataSeeder();
        seeder.seedDemoData();

        MainMenuUI app = new MainMenuUI();
        app.start();
    }
}
