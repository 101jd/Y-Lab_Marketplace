package org.y_lab;

import org.y_lab.adapter.in.view.ConsoleView;
import org.y_lab.adapter.in.view.Menu;
import org.y_lab.application.model.User;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Menu menu = new Menu(new ConsoleView(), new Scanner(System.in));

        menu.menu();
    }
}