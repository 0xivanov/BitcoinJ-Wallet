package com.ivanov.btcwallet;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * A delegate that implements JavaFX {@link Application}
 */
public interface AppDelegate {
    /**
     * Implement this method if you have code to run during {@link Application#init()} or
     * if you need a reference to the actual {@code Application object}
     * @param application a reference to the actual {@code Application} object
     * @throws Exception something bad happened
     */
    default void init(Application application) throws Exception {
    }
    void start(Stage primaryStage) throws Exception;
    void stop() throws Exception;
}