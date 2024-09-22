package com.mycompany.myapp;

import static com.codename1.ui.CN.*;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Dialog;
import com.codename1.ui.Label;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import com.codename1.io.Log;
import com.codename1.ui.Toolbar;
import java.io.IOException;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.io.NetworkEvent;
import com.codename1.ui.Button;
import com.codename1.ui.ButtonGroup;
import com.codename1.ui.CheckBox;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.RadioButton;
import com.codename1.ui.TextField;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.spinner.Picker;
import com.codename1.util.Callback;

public class AddLabTest implements FontInterface, Colors {

    private Form current;
    private Resources theme;

    public void init(Object context) {
        // use two network threads instead of one
        updateNetworkThreadCount(2);

        theme = UIManager.initFirstTheme("/theme");

        // Enable Toolbar on all Forms by default
        Toolbar.setGlobalToolbar(true);

        // Pro only feature
        Log.bindCrashProtection(true);

        addNetworkErrorListener(err -> {
            // prevent the event from propagating
            err.consume();
            if (err.getError() != null) {
                Log.e(err.getError());
            }
            Log.sendLogAsync();
            Dialog.show("Connection Error", "There was a networking error in the connection to " + err.getConnectionRequest().getUrl(), "OK", null);
        });
    }

    Form previousForm = current;

    DatabaseHelper databaseHelper = new DatabaseHelper();

    public void start() {

        if (current != null) {
            current.show();
            return;
        }

        previousForm = current;

        Form form = new Form("Add LabTest", new BoxLayout(BoxLayout.Y_AXIS));

        Toolbar toolbar = new Toolbar();
        form.setToolbar(toolbar);

        // Add a back button to the Toolbar
        toolbar.addCommandToLeftBar("Back", null, evt -> {

            MyApplication home = new MyApplication();

            home.start();

            this.destroy();

        });

        Container testTypeSelectedContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));

        testTypeSelectedContainer.getAllStyles().setPadding(Component.TOP, 10);  // Padding from top
        testTypeSelectedContainer.getAllStyles().setMargin(Component.TOP, 20);   // Margin from top
// Create a Picker (spinner) for selecting between test types
        Picker testTypePicker = new Picker();
        testTypePicker.setStrings("Radiological Test", "Pathological Test"); // Set options
        testTypePicker.setSelectedString("Pathological Test"); // Set default selected value

        TextField extraInput = new TextField("", "Reagent", 34, TextField.ANY);

        extraInput.getAllStyles().setFont(font);
        extraInput.getAllStyles().setMargin(Component.TOP, 5);
        extraInput.getAllStyles().setBorder(Border.createLineBorder(5, Black));

        // Improve Picker visibility by adding padding and margin
        testTypePicker.getAllStyles().setPadding(Component.TOP, 5);
        testTypePicker.getAllStyles().setPadding(Component.BOTTOM, 5);
        testTypePicker.getAllStyles().setPadding(Component.LEFT, 10);
        testTypePicker.getAllStyles().setPadding(Component.RIGHT, 10);
        testTypePicker.getAllStyles().setMargin(Component.BOTTOM, 10); // Space below the picker

        // Optionally set a background color or border to the Picker for better visibility
        testTypePicker.getAllStyles().setBgColor(0xCCCCCC); // Light gray background
        testTypePicker.getAllStyles().setBgTransparency(255); // Opaque background

        testTypePicker.addActionListener(e -> {

            if (testTypePicker.getSelectedString().equals("Radiological Test")) {

                extraInput.setHint("Plate Dimention");

            } else {

                extraInput.setHint("Reagent");

            }

            extraInput.repaint();

        });

        // Add picker to the container
        testTypeSelectedContainer.add(testTypePicker);

        form.add(testTypeSelectedContainer);

        TextField title = new TextField("", "Title", 34, TextField.ANY);

        title.getAllStyles().setFont(font);
        title.getAllStyles().setMargin(Component.TOP, 5);
        title.getAllStyles().setBorder(Border.createLineBorder(5, Black));

        TextField cost = new TextField("", "Cost", 34, TextField.DECIMAL);

        cost.getAllStyles().setFont(font);
        cost.getAllStyles().setMargin(Component.TOP, 5);
        cost.setConstraint(TextField.DECIMAL);
        cost.getAllStyles().setBorder(Border.createLineBorder(5, Black));

        cost.addActionListener(evt -> {
            String currentText = cost.getText();

            if (!perfect(currentText)) {
                Dialog.show("Invalid Input", "Please enter a valid number.", "OK", null);
                cost.setText("");
            }
        });

        CheckBox isAvailable = new CheckBox("Available");

        isAvailable.getAllStyles().setFont(font);
        isAvailable.getAllStyles().setMargin(Component.TOP, 5);

        Button add = new Button("Add Test");

        add.getAllStyles().setFont(font);
        add.getAllStyles().setMargin(Component.TOP, 5);
        add.getAllStyles().setPadding(Component.LEFT, 5);
        add.getAllStyles().setPadding(Component.RIGHT, 5);
        add.getAllStyles().setBgColor(RoyalBlue);
        add.getAllStyles().setBgTransparency(255);
        add.getAllStyles().setFgColor(Black);

        add.addActionListener(e -> {

            if (title.getText().isEmpty()
                    || cost.getText().isEmpty()
                    || extraInput.getText().isEmpty()) {

                Dialog.show("No Input", "Please fill all the field.", "OK", null);
                return;

            }

            try {

                if (testTypePicker.getSelectedString().equals("Pathological Test")) {

                    Callback<String> callback = new Callback<String>() {
                        @Override
                        public void onSucess(String value) {

                            Dialog.show("Add request", value, "OK", null);

                        }

                        @Override
                        public void onError(Object sender, Throwable err, int errorCode, String errorMessage) {

                            Dialog.show("Invalid Input", errorMessage, "OK", null);

                        }
                    };

                    PathologicalTest pathologicalTest = new PathologicalTest(
                            title.getText().trim(),
                            Double.parseDouble(cost.getText()),
                            isAvailable.isSelected(),
                            extraInput.getText());

                    databaseHelper.addPathologicalTest(pathologicalTest, callback);

                    //Dialog.show("Add Request", response, "OK", null);
                } else {

                    RadioLogicalTest radiologicalTest = new RadioLogicalTest(
                            title.getText().trim(),
                            Double.parseDouble(cost.getText()),
                            isAvailable.isSelected(),
                            extraInput.getText());

                    Callback<String> callback = new Callback<String>() {
                        @Override
                        public void onSucess(String value) {

                            Dialog.show("Add request", value, "OK", null);

                        }

                        @Override
                        public void onError(Object sender, Throwable err, int errorCode, String errorMessage) {

                            Dialog.show("Invalid Input", errorMessage, "OK", null);

                        }
                    };

                    databaseHelper.addRadiologicalTest(radiologicalTest, callback);

                    //Dialog.show("Add Request", response, "OK", null);
                }

            } catch (NumberFormatException ex) {

                Dialog.show("Invalid Input", "Please enter a valid number.", "OK", null);
            }

        });

        form.addAll(title, cost, isAvailable, extraInput, add);

        form.show();

    }

    public void destroy() {
    }

    boolean perfect(String input) {

        char ch[] = input.toCharArray();

        for (char i : ch) {

            if (!Character.isDigit(i)) {

                return false;

            }

        }

        return true;

    }

    String makePerfect(String text) {

        char ch[] = text.toCharArray();

        StringBuilder sb = new StringBuilder();

        for (char i : ch) {

            if (Character.isDigit(i)) {

                sb.append(i);

            }

        }

        return sb.toString();

    }

}
