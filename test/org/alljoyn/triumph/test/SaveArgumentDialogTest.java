package org.alljoyn.triumph.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.alljoyn.triumph.model.components.arguments.Argument.DIRECTION;
import org.alljoyn.triumph.model.components.arguments.ArgumentFactory;
import org.alljoyn.triumph.model.components.arguments.IntegerArrayArgument;
import org.alljoyn.triumph.view.SaveArgumentDialog;
import org.alljoyn.triumph.view.SaveArgumentDialog.CloseListener;

public class SaveArgumentDialogTest extends Application implements CloseListener{

    /**
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    private Stage mStage;
    
    @Override
    public void start(Stage stage) throws Exception {
        IntegerArrayArgument arg = (IntegerArrayArgument) ArgumentFactory.getArgument("ai", "array of ints", DIRECTION.IN);
        arg.setValue(new int[] {1, 2, 3, 4});
        stage.setScene(new Scene(new SaveArgumentDialog(arg, this)));
        stage.show();
        mStage = stage;
    }

    @Override
    public void onRequestClose() {
        if (mStage != null)
            mStage.close();
    }

}
