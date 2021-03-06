package org.emoflon.tggbenchmark.gui.controller.plugin_preferences;

import java.io.IOException;

import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.Validator;
import org.emoflon.tggbenchmark.gui.component.ModelSizesTextArea;
import org.emoflon.tggbenchmark.gui.component.TimeTextField;
import org.emoflon.tggbenchmark.gui.controller.generic_preferences.CategoryController;
import org.emoflon.tggbenchmark.gui.model.PluginPreferences;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;

public class CategoryDefaultsController extends CategoryController<PluginPreferences> {

    // elements from the FXML resource
    @FXML
    private TimeTextField defaultTimeout;
    @FXML
    private ModelSizesTextArea defaultModelSizes;
    @FXML
    private CheckBox defaultModelgenIncludeReport;
    @FXML
    private CheckBox defaultInitialFwdActive;
    @FXML
    private CheckBox defaultInitialBwdActive;
    @FXML
    private CheckBox defaultFwdActive;
    @FXML
    private CheckBox defaultBwdActive;
    @FXML
    private CheckBox defaultFwdOptActive;
    @FXML
    private CheckBox defaultBwdOptActive;
    @FXML
    private CheckBox defaultCcActive;
    @FXML
    private CheckBox defaultCoActive;

    private Tooltip defaultTimeoutTooltip;

    public CategoryDefaultsController() throws IOException {
        super("../../../resources/view/plugin_preferences/CategoryDefaults.fxml");
    }

    @Override
    public void initData(PluginPreferences preferencesData) {
        super.initData(preferencesData);

        // tooltips
        defaultTimeoutTooltip = new Tooltip("The timeout can be specified in the following manner: 30, 30s, 5m, 1h");

        // bindings
        defaultTimeout.bindIntegerProperty(preferencesData.defaultTimeoutProperty());
        defaultTimeout.setTooltip(defaultTimeoutTooltip);
        validation.registerValidator(defaultTimeout, (Control c, String newValue) -> ValidationResult.fromErrorIf(
                defaultTimeout, "The default timeout must be greater than 0",
                newValue.isEmpty() || newValue.equals("0") || newValue.equals("0s") || newValue.equals("0m") || newValue
                        .equals("0h"))); /*
                                          * unfortunately the new formatted value is avaible only after the listener
                                          * event has finished, therefore I cannot access the formatted value
                                          */

        defaultModelSizes.bindListProperty(preferencesData.defaultModelSizesProperty());
        validation.registerValidator(defaultModelSizes,
                Validator.createEmptyValidator("At least one model size need to be specified"));

        defaultModelgenIncludeReport.selectedProperty()
                .bindBidirectional(preferencesData.defaultModelgenIncludeReportProperty());
        defaultInitialFwdActive.selectedProperty().bindBidirectional(preferencesData.defaultInitialFwdActiveProperty());
        defaultInitialBwdActive.selectedProperty().bindBidirectional(preferencesData.defaultInitialBwdActiveProperty());
        defaultFwdActive.selectedProperty().bindBidirectional(preferencesData.defaultFwdActiveProperty());
        defaultBwdActive.selectedProperty().bindBidirectional(preferencesData.defaultBwdActiveProperty());
        defaultFwdOptActive.selectedProperty().bindBidirectional(preferencesData.defaultFwdOptActiveProperty());
        defaultBwdOptActive.selectedProperty().bindBidirectional(preferencesData.defaultBwdOptActiveProperty());
        defaultCcActive.selectedProperty().bindBidirectional(preferencesData.defaultCcActiveProperty());
        defaultCoActive.selectedProperty().bindBidirectional(preferencesData.defaultCcActiveProperty());
    }
}
