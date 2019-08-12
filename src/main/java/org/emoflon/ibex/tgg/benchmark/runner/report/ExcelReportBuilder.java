package org.emoflon.ibex.tgg.benchmark.runner.report;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.TypeVariable;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.emoflon.ibex.tgg.benchmark.runner.BenchmarkResult;
import org.emoflon.ibex.tgg.benchmark.runner.SingleRunResult;

/**
 * ExcelReportBuilder creates a report from a list of benchmark results and
 * saves them to a Excel spreadsheet file.
 *
 * @author Andre Lehmann
 */
public class ExcelReportBuilder extends ReportBuilder {

    private OutputStream reportOutputStream;
    private XSSFWorkbook reportWorkbook;
    private XSSFSheet resultsSheet;
    private XSSFSheet rawResultsSheet;
    private LinkedList<Map<String, Object>> resultsSheetDefinition;
    private LinkedList<Map<String, Object>> rawResultsSheetDefinition;

    /**
     * Constructor for {@link ExcelReportBuilder}.
     * 
     * @param reportFilePath The path for the report file
     * @throws IOException if the report file could not be created or saved
     */
    public ExcelReportBuilder(Path reportFilePath, boolean includeErrors) throws IOException {
        super(reportFilePath, includeErrors);
    }

    @Override
    protected void createReportFile() throws IOException {
        LOG.debug("Creating report file '{}'", reportFilePath);
        try {
            Files.createDirectories(reportFilePath.getParent());
        } catch (FileAlreadyExistsException e) {
            // ignore
        }

        reportOutputStream = Files.newOutputStream(reportFilePath, StandardOpenOption.CREATE_NEW);
        initializeSheetDefinitions();
        initalizeWorkbook();
    }

    /**
     * Initialize the workbook, which is the representation of the excel file.
     */
    private void initalizeWorkbook() {
        reportWorkbook = new XSSFWorkbook();

        CellStyle headerStyle = reportWorkbook.createCellStyle();
        XSSFFont headerFont = reportWorkbook.createFont();
        headerFont.setFontHeightInPoints((short) 16);
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        resultsSheet = reportWorkbook.createSheet("Benchmark Results");
        Row header = resultsSheet.createRow(0);
        for (int i = 0; i < resultsSheetDefinition.size(); i++) {
            Map<String, Object> m = resultsSheetDefinition.get(i);
            Cell headerCell = header.createCell(i);
            headerCell.setCellValue((String) m.get("name"));
            headerCell.setCellStyle(headerStyle);
            resultsSheet.setColumnWidth(i, (int) m.get("columnWidth"));
        }

        rawResultsSheet = reportWorkbook.createSheet("Raw Benchmark Results");
        header = rawResultsSheet.createRow(0);
        for (int i = 0; i < rawResultsSheetDefinition.size(); i++) {
            Map<String, Object> m = rawResultsSheetDefinition.get(i);
            Cell headerCell = header.createCell(i);
            headerCell.setCellValue((String) m.get("name"));
            headerCell.setCellStyle(headerStyle);
            resultsSheet.setColumnWidth(i, (int) m.get("columnWidth"));
        }
    }

    /**
     * Define the column structure of the sheets. The definition will be used when
     * creating the report and adding the results.
     */
    private void initializeSheetDefinitions() {
        this.resultsSheetDefinition = new LinkedList<>();

        Map<String, Object> projectName = new HashMap<>();
        projectName.put("name", "Project Name");
        projectName.put("columnWidth", 8000);
        projectName.put("valueType", CellType.STRING);
        projectName.put("valueSelector", (Function<BenchmarkResult, String>) BenchmarkResult::getProjectName);
        resultsSheetDefinition.add(projectName);
        rawResultsSheetDefinition.add(projectName);

        Map<String, Object> patternMatchingEngin = new HashMap<>();
        patternMatchingEngin.put("name", "Pattern Matching Engine");
        patternMatchingEngin.put("columnWidth", 4000);
        patternMatchingEngin.put("valueType", CellType.STRING);
        patternMatchingEngin.put("valueSelector",
                (Function<BenchmarkResult, String>) br -> br.getPatternMatchingEngine().toString());
        resultsSheetDefinition.add(patternMatchingEngin);
        rawResultsSheetDefinition.add(patternMatchingEngin);

        Map<String, Object> operationalization = new HashMap<>();
        operationalization.put("name", "Operationalization");
        operationalization.put("columnWidth", 4000);
        operationalization.put("valueType", CellType.STRING);
        operationalization.put("valueSelector",
                (Function<BenchmarkResult, String>) br -> br.getOperationalization().toString());
        resultsSheetDefinition.add(operationalization);
        rawResultsSheetDefinition.add(operationalization);

        Map<String, Object> modelSize = new HashMap<>();
        modelSize.put("name", "Model Size");
        modelSize.put("columnWidth", 4000);
        modelSize.put("valueType", CellType.NUMERIC);
        modelSize.put("valueSelector", (Function<BenchmarkResult, Double>) br -> (double) br.getModelSize());
        resultsSheetDefinition.add(modelSize);
        rawResultsSheetDefinition.add(modelSize);

        Map<String, Object> run = new HashMap<>();
        run.put("name", "Run");
        run.put("columnWidth", 4000);
        run.put("valueType", CellType.NUMERIC);
        run.put("valueSelector", (Function<SingleRunResult, Double>) sr -> (double) sr.getRepetition());
        rawResultsSheetDefinition.add(run);

        Map<String, Object> initalizationTime = new HashMap<>();
        initalizationTime.put("name", "Initalization Time");
        initalizationTime.put("columnWidth", 4000);
        initalizationTime.put("valueType", CellType.NUMERIC);
        initalizationTime.put("valueSelector",
                (Function<SingleRunResult, Double>) sr -> (double) sr.getInitializationTime());
        rawResultsSheetDefinition.add(initalizationTime);

        Map<String, Object> executionTime = new HashMap<>();
        executionTime.put("name", "Execution Time");
        executionTime.put("columnWidth", 4000);
        executionTime.put("valueType", CellType.NUMERIC);
        executionTime.put("valueSelector", (Function<SingleRunResult, Double>) sr -> (double) sr.getExecutionTime());
        rawResultsSheetDefinition.add(executionTime);

        Map<String, Object> averageInitalizationTime = new HashMap<>();
        averageInitalizationTime.put("name", "Average Initalization Time");
        averageInitalizationTime.put("columnWidth", 4000);
        averageInitalizationTime.put("valueType", CellType.STRING);
        averageInitalizationTime.put("valueSelector",
                (Function<BenchmarkResult, Double>) BenchmarkResult::getAverageInitalizationTime);
        resultsSheetDefinition.add(averageInitalizationTime);

        Map<String, Object> medianInitalizationTime = new HashMap<>();
        medianInitalizationTime.put("name", "Median Initalization Time");
        medianInitalizationTime.put("columnWidth", 4000);
        medianInitalizationTime.put("valueType", CellType.STRING);
        medianInitalizationTime.put("valueSelector",
                (Function<BenchmarkResult, Double>) BenchmarkResult::getMedianInitializationTime);
        resultsSheetDefinition.add(medianInitalizationTime);

        Map<String, Object> averageExecutionTime = new HashMap<>();
        averageExecutionTime.put("name", "Average Execution Time");
        averageExecutionTime.put("columnWidth", 4000);
        averageExecutionTime.put("valueType", CellType.STRING);
        averageExecutionTime.put("valueSelector",
                (Function<BenchmarkResult, Double>) BenchmarkResult::getAverageExecutionTime);
        resultsSheetDefinition.add(averageExecutionTime);

        Map<String, Object> medianExecutionTime = new HashMap<>();
        medianExecutionTime.put("name", "Median Execution Time");
        medianExecutionTime.put("columnWidth", 4000);
        medianExecutionTime.put("valueType", CellType.STRING);
        medianExecutionTime.put("valueSelector",
                (Function<BenchmarkResult, Double>) BenchmarkResult::getMedianExecutionTime);
        resultsSheetDefinition.add(medianExecutionTime);

        Map<String, Object> createdElements = new HashMap<>();
        createdElements.put("name", "Created Elements");
        createdElements.put("columnWidth", 4000);
        createdElements.put("valueType", CellType.NUMERIC);
        createdElements.put("valueSelector",
                (Function<SingleRunResult, Double>) sr -> (double) sr.getCreatedElements());
        rawResultsSheetDefinition.add(createdElements);

        Map<String, Object> deletedElements = new HashMap<>();
        deletedElements.put("name", "Deleted Elements");
        deletedElements.put("columnWidth", 4000);
        deletedElements.put("valueType", CellType.NUMERIC);
        deletedElements.put("valueSelector",
                (Function<SingleRunResult, Double>) sr -> (double) sr.getDeletedElements());
        rawResultsSheetDefinition.add(deletedElements);

        Map<String, Object> averageCreatedElements = new HashMap<>();
        averageCreatedElements.put("name", "Average Created Elements");
        averageCreatedElements.put("columnWidth", 4000);
        averageCreatedElements.put("valueType", CellType.STRING);
        averageCreatedElements.put("valueSelector",
                (Function<BenchmarkResult, Double>) BenchmarkResult::getAverageCreatedElements);
        resultsSheetDefinition.add(averageCreatedElements);

        Map<String, Object> medianCreatedElements = new HashMap<>();
        medianCreatedElements.put("name", "Median Created Elements");
        medianCreatedElements.put("columnWidth", 4000);
        medianCreatedElements.put("valueType", CellType.STRING);
        medianCreatedElements.put("valueSelector",
                (Function<BenchmarkResult, Double>) BenchmarkResult::getMedianCreatedElements);
        resultsSheetDefinition.add(medianCreatedElements);

        Map<String, Object> averageDeletedElements = new HashMap<>();
        averageDeletedElements.put("name", "Average Deleted Elements");
        averageDeletedElements.put("columnWidth", 4000);
        averageDeletedElements.put("valueType", CellType.STRING);
        averageDeletedElements.put("valueSelector",
                (Function<BenchmarkResult, Double>) BenchmarkResult::getAverageDeletedElements);
        resultsSheetDefinition.add(averageDeletedElements);

        Map<String, Object> medianDeletedElements = new HashMap<>();
        medianDeletedElements.put("name", "Median Deleted Elements");
        medianDeletedElements.put("columnWidth", 4000);
        medianDeletedElements.put("valueType", CellType.STRING);
        medianDeletedElements.put("valueSelector",
                (Function<BenchmarkResult, Double>) BenchmarkResult::getMedianDeletedElements);
        resultsSheetDefinition.add(medianDeletedElements);

        Map<String, Object> error = new HashMap<>();
        error.put("name", "Error");
        error.put("columnWidth", 20000);
        error.put("valueType", CellType.STRING);
        error.put("valueSelector", (Function<BenchmarkResult, String>) BenchmarkResult::getError);
        resultsSheetDefinition.add(error);
        rawResultsSheetDefinition.add(error);
    }

    @Override
    public void addEntry(BenchmarkResult benchmarkResult) throws IOException {
        super.addEntry(benchmarkResult);

        if (benchmarkResult.getError() != null && !includeErrors) {
            return;
        }

        // add benchmark results
        Row row = resultsSheet.createRow(resultsSheet.getLastRowNum() + 1);
        // add each column
        for (int i = 0; i < resultsSheetDefinition.size(); i++) {
            Map<String, Object> m = resultsSheetDefinition.get(i);
            Cell cell = row.createCell(i, (CellType) m.get("valueType"));
            if ((CellType) m.get("valueType") == CellType.STRING) {
                Function<BenchmarkResult, String> valueSelector = (Function<BenchmarkResult, String>) m
                        .get("valueSelector");
                cell.setCellValue(valueSelector.apply(benchmarkResult));
            } else if ((CellType) m.get("valueType") == CellType.NUMERIC) {
                Function<BenchmarkResult, Double> valueSelector = (Function<BenchmarkResult, Double>) m
                        .get("valueSelector");
                cell.setCellValue(valueSelector.apply(benchmarkResult));
            }
        }

        // add raw results (single run results)
        for (SingleRunResult singleRunResult : benchmarkResult.getRunResults()) {
            row = rawResultsSheet.createRow(rawResultsSheet.getLastRowNum() + 1);
            // add each column
            for (int i = 0; i < rawResultsSheetDefinition.size(); i++) {
                Map<String, Object> m = rawResultsSheetDefinition.get(i);
                Cell cell = row.createCell(i, (CellType) m.get("valueType"));
                Function<?, ?> tmpValueSelector = (Function<?, ?>) m.get("valueSelector");
                // I know this is kind of ugly, but how else get the correct type?
                TypeVariable<?>[] valueSelectorTypes = tmpValueSelector.getClass().getTypeParameters();
                if (valueSelectorTypes[0].equals(BenchmarkResult.class)) {
                    if ((CellType) m.get("valueType") == CellType.STRING) {
                        Function<BenchmarkResult, String> valueSelector = (Function<BenchmarkResult, String>) m
                                .get("valueSelector");
                        cell.setCellValue(valueSelector.apply(benchmarkResult));
                    } else if ((CellType) m.get("valueType") == CellType.NUMERIC) {
                        Function<BenchmarkResult, Double> valueSelector = (Function<BenchmarkResult, Double>) m
                                .get("valueSelector");
                        cell.setCellValue(valueSelector.apply(benchmarkResult));
                    }
                } else if (valueSelectorTypes[0].equals(SingleRunResult.class)) {
                    if ((CellType) m.get("valueType") == CellType.STRING) {
                        Function<SingleRunResult, String> valueSelector = (Function<SingleRunResult, String>) m
                                .get("valueSelector");
                        cell.setCellValue(valueSelector.apply(singleRunResult));
                    } else if ((CellType) m.get("valueType") == CellType.NUMERIC) {
                        Function<SingleRunResult, Double> valueSelector = (Function<SingleRunResult, Double>) m
                                .get("valueSelector");
                        cell.setCellValue(valueSelector.apply(singleRunResult));
                    }
                }
            }
        }

        // save report so the results doesn't get lost if the benchmark is
        // stopped before completion
        reportWorkbook.write(reportOutputStream);
    }

    public void close() throws IOException {
        reportWorkbook.close();
        reportOutputStream.close();
    }
}