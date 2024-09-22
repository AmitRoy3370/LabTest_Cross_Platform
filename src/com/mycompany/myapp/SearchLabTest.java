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
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.List;
import com.codename1.ui.RadioButton;
import com.codename1.ui.TextField;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.spinner.Picker;
import com.codename1.util.Callback;
import static com.mycompany.myapp.Colors.Black;
import static com.mycompany.myapp.Colors.RoyalBlue;
import static com.mycompany.myapp.FontInterface.font;

public class SearchLabTest implements FontInterface {

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

    DatabaseHelper database = new DatabaseHelper();

    public void start() {

        if (current != null) {
            current.show();
            return;
        }

        previousForm = current;

        Form form = new Form("Search LabTest", new BoxLayout(BoxLayout.Y_AXIS));

        Toolbar toolbar = new Toolbar();
        form.setToolbar(toolbar);

        // Add a back button to the Toolbar
        toolbar.addCommandToLeftBar("Back", null, evt -> {

            MyApplication home = new MyApplication();

            home.start();

            this.destroy();

        });

        Picker testTypePicker = new Picker();
        testTypePicker.setStrings("All", "Radiological Test", "Pathological Test");

        testTypePicker.setSelectedString("All");

        testTypePicker.getAllStyles().setFont(font);
        testTypePicker.getAllStyles().setPadding(Component.BOTTOM, 5);
        testTypePicker.getAllStyles().setPadding(Component.RIGHT, 5);
        testTypePicker.getAllStyles().setPadding(Component.LEFT, 5);
        testTypePicker.getAllStyles().setPadding(Component.TOP, 5);
        testTypePicker.getAllStyles().setMargin(Component.BOTTOM, 5);

        Container searchTypeFinder = new Container(new FlowLayout());

        RadioButton title = new RadioButton("Title");
        RadioButton cost = new RadioButton("Cost");

        title.getAllStyles().setFont(font);
        title.getAllStyles().setBorder(Border.createLineBorder(5, Black));
        cost.getAllStyles().setFont(font);
        cost.getAllStyles().setBorder(Border.createLineBorder(5, Black));

        ButtonGroup group = new ButtonGroup();

        group.add(title);
        group.add(cost);

        searchTypeFinder.add(cost);
        searchTypeFinder.add(title);

        searchTypeFinder.getAllStyles().setMargin(Component.BOTTOM, 5);
        //searchTypeFinder.getAllStyles().
        // setBorder(Border.createLineBorder(5, Black));

        TextField inputTitle = new TextField("", "Title", 34, TextField.ANY);
        inputTitle.getAllStyles().setBorder(Border.createLineBorder(5, Black));
        inputTitle.getAllStyles().setFont(font);
        inputTitle.setVisible(false);

        TextField inputCost = new TextField("", "Cost", 34, TextField.DECIMAL);
        inputCost.getAllStyles().setBorder(Border.createLineBorder(5, Black));
        inputCost.getAllStyles().setFont(font);
        //inputCost.setConstraint(TextField.DECIMAL);
        inputCost.setVisible(false);

        inputCost.addActionListener(evt -> {
            String currentText = inputCost.getText();

            if (!perfect(currentText)) {
                Dialog.show("Invalid Input", "Please enter a valid number.", "OK", null);
                inputCost.setText("");
            }
        });

        try {

            // Action Listeners for RadioButtons to show/hide input fields
            title.addActionListener(e -> {
                inputTitle.setVisible(true);
                inputCost.setVisible(false);
                form.revalidate();  // Refresh the form layout
            });

            cost.addActionListener(e -> {
                inputCost.setVisible(true);
                inputTitle.setVisible(false);
                form.revalidate();  // Refresh the form layout
            });

            group.addActionListener(e -> {
                if (!title.isSelected() && !cost.isSelected()) {
                    inputCost.setVisible(false);
                    inputTitle.setVisible(false);
                    form.revalidate();  // Refresh the form layout
                }
                form.revalidate();  // In case no radio button is selected
            });

        } catch (Exception e) {

        }

        Button search = new Button("Search");

        search.getAllStyles().setFont(font);
        search.getAllStyles().setMargin(Component.TOP, 5);
        search.getAllStyles().setPadding(Component.LEFT, 5);
        search.getAllStyles().setPadding(Component.RIGHT, 5);
        search.getAllStyles().setBgColor(RoyalBlue);
        search.getAllStyles().setBgTransparency(255);
        search.getAllStyles().setFgColor(Black);

        DefaultListModel listModel = new DefaultListModel();

        List list = new List(listModel);

        try {

            search.addActionListener(e -> {

                listModel.getList().clear();

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

                if (testTypePicker.getSelectedString().equals("All")) {

                    if (!cost.isSelected() && !title.isSelected()) {

                        database.getAllPathologicalTest(list, callback);

                        form.revalidate();

                        database.getAllRadiologicalTest(list, callback);

                        form.revalidate();

                    } else if (cost.isSelected()) {

                        database.searchPathologicalTestByCost(Integer.parseInt(inputCost.getText()), callback, list);

                        form.revalidate();

                        database.searchRadioLogicalTestByCost(Integer.parseInt(inputCost.getText()), callback, list);

                        form.revalidate();

                    } else {

                        database.searchPathologicalTestByTitle(inputTitle.getText(), callback, list);

                        form.revalidate();

                        database.searchRadioLogicalTestByTitle(inputTitle.getText(), callback, list);

                        form.revalidate();

                    }

                } else if (testTypePicker.getSelectedString().equals("Radiological Test")) {

                    if (!cost.isSelected() && !title.isSelected()) {

                        //database.getAllPathologicalTest(list, callback);
                        //form.revalidate();
                        database.getAllRadiologicalTest(list, callback);

                        form.revalidate();

                    } else if (cost.isSelected()) {

                        database.searchRadioLogicalTestByCost(Integer.parseInt(inputCost.getText()), callback, list);

                        form.revalidate();

                    } else {

                        //database.searchPathologicalTestByTitle(title.getText().toString(), callback, list);
                        //form.revalidate();
                        database.searchRadioLogicalTestByTitle(inputTitle.getText(), callback, list);

                        form.revalidate();

                    }

                } else if (testTypePicker.getSelectedString().equals("Pathological Test")) {

                    if (!cost.isSelected() && !title.isSelected()) {

                        database.getAllPathologicalTest(list, callback);

                        form.revalidate();

                        //database.getAllRadiologicalTest(list, callback);
                        //form.revalidate();
                    } else if (cost.isSelected()) {

                        database.searchPathologicalTestByCost(Integer.parseInt(inputCost.getText()), callback, list);

                        form.revalidate();

                    } else {

                        database.searchPathologicalTestByTitle(inputTitle.getText(), callback, list);

                        form.revalidate();

                        //database.searchRadioLogicalTestByTitle(title.getText().toString(), callback, list);
                        //form.revalidate();
                    }

                }

            });

        } catch (Exception ex) {

            Dialog.show("Invalid Input", ex.toString(), "OK", null);

        }

        form.addAll(testTypePicker, searchTypeFinder, inputTitle, inputCost, search, list);

        form.setScrollable(true);

        form.show();

    }

    public void destroy() {
    }

    boolean perfect(String input) {

        try {

            double g = Double.parseDouble(input);

            System.out.println(g);

        } catch (NumberFormatException e) {

            return false;

        }

        return true;

    }

}
