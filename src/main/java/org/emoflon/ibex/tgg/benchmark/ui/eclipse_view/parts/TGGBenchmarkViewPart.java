package org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.parts;

import java.util.function.Function;

import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.model.EclipseTggProject;
import org.emoflon.ibex.tgg.benchmark.ui.components.CheckBoxTableCell;
import org.emoflon.ibex.tgg.benchmark.ui.components.Part;
import org.emoflon.ibex.tgg.benchmark.ui.components.SelectAllCheckBox;
import org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.handlers.EditBenchmarkCaseHandler;
import org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.handlers.RunBenchmarkCaseHandler;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class TGGBenchmarkViewPart extends Part {

    private ObservableList<BenchmarkCasePreferences> benchmarkCasePreferences;
    private BenchmarkCaseTableView table;

    private final Core pluginCore;

    public TGGBenchmarkViewPart() {
        // get the plugin core instance
        pluginCore = Core.getInstance();

        // create the content
        table = new BenchmarkCaseTableView();
        content = new AnchorPane(table);
        AnchorPane.setTopAnchor(table, 0.0);
        AnchorPane.setBottomAnchor(table, 0.0);
        AnchorPane.setLeftAnchor(table, 0.0);
        AnchorPane.setRightAnchor(table, 0.0);

        this.benchmarkCasePreferences = pluginCore.getBenchmarkCases();
        table.initData(benchmarkCasePreferences);
    }

    public class BenchmarkCaseTableView extends TableView<BenchmarkCasePreferences> {

        private TableColumn<BenchmarkCasePreferences, Boolean> executionColumn;
        private TableColumn<BenchmarkCasePreferences, String> nameColumn;
        private TableColumn<BenchmarkCasePreferences, String> projectColumn;
        private TableColumn<BenchmarkCasePreferences, String> patternMatchingEngineColumn;
        private TableColumn<BenchmarkCasePreferences, Boolean> modelgenActiveColumn;
        private TableColumn<BenchmarkCasePreferences, Boolean> initialFwdActiveColumn;
        private TableColumn<BenchmarkCasePreferences, Boolean> initialBwdActiveColumn;
        private TableColumn<BenchmarkCasePreferences, Boolean> fwdActiveColumn;
        private TableColumn<BenchmarkCasePreferences, Boolean> bwdActiveColumn;
        private TableColumn<BenchmarkCasePreferences, Boolean> fwdOptActiveColumn;
        private TableColumn<BenchmarkCasePreferences, Boolean> bwdOptActiveColumn;
        private TableColumn<BenchmarkCasePreferences, Boolean> ccActiveColumn;
        private TableColumn<BenchmarkCasePreferences, Boolean> coActiveColumn;

        public BenchmarkCaseTableView() {
            // the table view itself
            setEditable(true);
            setRowFactory(tv -> {
                final TableRow<BenchmarkCasePreferences> row = new TableRow<>();

                // row menu
                MenuItem runSelectedBenchmarkCases = new MenuItem("Run selected case");
                runSelectedBenchmarkCases.setOnAction(e -> {
                    new RunBenchmarkCaseHandler().execute(getItems());
                });

                final ContextMenu rowMenu = new ContextMenu();
                MenuItem editSelectedBenchmarkCases = new MenuItem("Edit selected case");
                editSelectedBenchmarkCases.setOnAction(e -> {
                    new EditBenchmarkCaseHandler().execute(this);
                });

                MenuItem duplicateSelected = new MenuItem("Duplicate selected case");
                duplicateSelected.setOnAction(e -> {
                    BenchmarkCasePreferences selectedBcp = getSelectionModel().getSelectedItem();
                    if (selectedBcp != null) {
                        BenchmarkCasePreferences newBcp = new BenchmarkCasePreferences(selectedBcp);
                        newBcp.setBenchmarkCaseName(newBcp.getBenchmarkCaseName() + " Copy");
                        newBcp.getEclipseProject().addBenchmarkCase(newBcp);
                    }
                });

                rowMenu.getItems().addAll(runSelectedBenchmarkCases, editSelectedBenchmarkCases, duplicateSelected);
                row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(rowMenu)
                        .otherwise((ContextMenu) null));

                // double click event
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty()) {
                        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                            new EditBenchmarkCaseHandler().execute(this);
                        }
                    }
                });

                return row;
            });
        }

        /**
         * Initializes the table columns and sets the data. This is the easiest way to
         * do, because the {@link SelectAllCheckBox}s need the item list of the table
         * view and the setItems method cannot be overriden. The item list cannot be
         * changed afterwards, but who cares.
         *
         * @param projects
         */
        public void initData(ObservableList<BenchmarkCasePreferences> projects) {
            setItems(projects);

            executionColumn = createCheckboxColumn("Exc", "Mark benchmark case for execution",
                    bcp -> (bcp.markedForExecutionProperty()));
            getColumns().add(executionColumn);

            nameColumn = createTextColumn("Benchmark Case Name", BenchmarkCasePreferences::benchmarkCaseNameProperty);
            getColumns().add(nameColumn);

            projectColumn = createTextColumn("Project", "The asscociated TGG project",
                    bcp -> bcp.getEclipseProject().nameProperty());
            getColumns().add(projectColumn);

            patternMatchingEngineColumn = createTextColumn("Pattern Matching Engine",
                    "The pattern matching engine to use", bcp -> {
                        StringProperty property = new SimpleStringProperty(bcp.getPatternMatchingEngine().toString());
                        bcp.patternMatchingEngineProperty()
                                .addListener(e -> property.set(bcp.getPatternMatchingEngine().toString()));
                        return property;
                    });
            getColumns().add(patternMatchingEngineColumn);

            modelgenActiveColumn = createCheckboxColumn("MG", "Enable the operationalization MODELGEN",
                    bcp -> (bcp.modelgenIncludeReportProperty()));
            getColumns().add(modelgenActiveColumn);

            initialFwdActiveColumn = createCheckboxColumn("IF", "Enable the operationalization INITIAL_FWD",
                    bcp -> (bcp.initialFwdActiveProperty()));
            getColumns().add(initialFwdActiveColumn);

            initialBwdActiveColumn = createCheckboxColumn("IB", "Enable the operationalization INITIAL_BWD",
                    bcp -> (bcp.initialBwdActiveProperty()));
            getColumns().add(initialBwdActiveColumn);

            fwdActiveColumn = createCheckboxColumn("F", "Enable the operationalization FWD",
                    bcp -> (bcp.fwdActiveProperty()));
            getColumns().add(fwdActiveColumn);

            bwdActiveColumn = createCheckboxColumn("B", "Enable the operationalization BWD",
                    bcp -> (bcp.bwdActiveProperty()));
            getColumns().add(bwdActiveColumn);

            fwdOptActiveColumn = createCheckboxColumn("FO", "Enable the operationalization FWD_OPT",
                    bcp -> (bcp.fwdOptActiveProperty()));
            getColumns().add(fwdOptActiveColumn);

            bwdOptActiveColumn = createCheckboxColumn("BO", "Enable the operationalization BWD_OPT",
                    bcp -> (bcp.bwdOptActiveProperty()));
            getColumns().add(bwdOptActiveColumn);

            ccActiveColumn = createCheckboxColumn("CC", "Enable the operationalization CC",
                    bcp -> (bcp.ccActiveProperty()));
            getColumns().add(ccActiveColumn);

            coActiveColumn = createCheckboxColumn("CO", "Enable the operationalization CO",
                    bcp -> (bcp.coActiveProperty()));
            getColumns().add(coActiveColumn);
        }

        private <S, T> TableColumn<S, T> createTextColumn(String title, Function<S, ObservableValue<T>> property) {
            return createTextColumn(title, "", property);
        }

        private <S, T> TableColumn<S, T> createTextColumn(String title, String tooltip,
                Function<S, ObservableValue<T>> property) {
            TableColumn<S, T> col = new TableColumn<>();
            Label columnLabel = new Label(title);
            col.setGraphic(columnLabel);
            if (tooltip != "") {
                Tooltip columnTooltip = new Tooltip(tooltip);
                columnLabel.setTooltip(columnTooltip);
            }
            col.setCellValueFactory(cellData -> property.apply(cellData.getValue()));
            return col;
        }

        private TableColumn<BenchmarkCasePreferences, Boolean> createCheckboxColumn(String title, String tooltip,
                Function<BenchmarkCasePreferences, BooleanProperty> property) {
            TableColumn<BenchmarkCasePreferences, Boolean> column = new TableColumn<>();
            column.setCellValueFactory(cellData -> property.apply(cellData.getValue()));
            column.setCellFactory(
                    new Callback<TableColumn<BenchmarkCasePreferences, Boolean>, TableCell<BenchmarkCasePreferences, Boolean>>() {
                        @Override
                        public TableCell<BenchmarkCasePreferences, Boolean> call(
                                TableColumn<BenchmarkCasePreferences, Boolean> column) {
                            CheckBoxTableCell<BenchmarkCasePreferences> cell = new CheckBoxTableCell<BenchmarkCasePreferences>();

                            cell.getCheckBox().setOnAction(e -> {
                                if (cell.getIndex() >= 0) {
                                    getItems().get(cell.getIndex()).getEclipseProject().delayedSavePreferences();
                                }
                            });
                            return cell;
                        }
                    });
            column.setMinWidth(60.0);

            Label columnLabel = new Label(title);
            columnLabel.setMaxWidth(Double.MAX_VALUE);
            columnLabel.getStyleClass().add("column-header-label");
            SelectAllCheckBox<BenchmarkCasePreferences> columnChkBox = new SelectAllCheckBox<BenchmarkCasePreferences>(
                    getItems(), property);
            columnChkBox.setOnAction(e -> {
                // save all
                for (EclipseTggProject project : Core.getInstance().getWorkspace().getTggProjects()) {
                    project.delayedSavePreferences();
                }
            });

            HBox columnLayout = new HBox(columnChkBox, columnLabel);
            columnLayout.setSpacing(2);
            columnLayout.setAlignment(Pos.CENTER);
            if (tooltip != "") {
                Tooltip columnTooltip = new Tooltip(tooltip);
                columnLabel.setTooltip(columnTooltip);
                columnChkBox.setTooltip(columnTooltip);
            }
            column.setGraphic(columnLayout);

            return column;
        }
    }

    public BenchmarkCaseTableView getTable() {
        return table;
    }
}
