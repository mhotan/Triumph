package org.alljoyn.triumph.test;

import org.alljoyn.triumph.model.components.arguments.Argument.DIRECTION;
import org.alljoyn.triumph.model.components.arguments.ArgumentFactory;
import org.alljoyn.triumph.model.components.arguments.IntegerArrayArgument;
import org.alljoyn.triumph.model.components.arguments.ObjectArrayArgument;
import org.alljoyn.triumph.view.SaveArgumentDialog2;
import org.alljoyn.triumph.view.SaveArgumentDialog2.CloseListener;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        stage.setScene(new Scene(new SaveArgumentDialog2(arg, this)));
        stage.show();
        mStage = stage;
    }

    @Override
    public void onRequestClose() {
        if (mStage != null)
            mStage.close();
    }

}
