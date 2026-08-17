package ui;

import engine.api.GMEngine;
import engine.core.GuessMarketEngine;

public class Main {

    public static void main(String[] args) {
        GMEngine engine = new GuessMarketEngine();
        ConsoleUI ui = new ConsoleUI(engine);
        ui.run();
    }
}
