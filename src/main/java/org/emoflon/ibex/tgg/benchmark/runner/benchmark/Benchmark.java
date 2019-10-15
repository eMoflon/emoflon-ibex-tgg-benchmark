package org.emoflon.ibex.tgg.benchmark.runner.benchmark;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.runner.BenchmarkRunParameters;
import org.emoflon.ibex.tgg.benchmark.runner.PatternMatchingEngine;
import org.emoflon.ibex.tgg.benchmark.runner.SingleRunResult;
import org.emoflon.ibex.tgg.operational.benchmark.BenchmarkLogger;
import org.emoflon.ibex.tgg.operational.strategies.OperationalStrategy;
import org.emoflon.ibex.tgg.runtime.engine.DemoclesTGGEngine;

/**
 * Exception thrown when the execution of the benchmark failed.
 *
 * @author Andre Lehmann
 */
class BenchmarkFailedException extends Exception {
    
    public BenchmarkFailedException() {
        
    }
    
    public BenchmarkFailedException(String message) {
        super(message);
    }

    private static final long serialVersionUID = 4354487099044087640L;
}

/**
 * Base class for benchmarks.
 *
 * @author Andre Lehmann
 */
public abstract class Benchmark<O extends OperationalStrategy> {

    protected static final Logger LOG = LogManager.getLogger(Core.PLUGIN_NAME);

    protected final BenchmarkRunParameters runParameters;
    protected O op;
    protected SingleRunResult runResult;
    protected boolean patternEngineInitialized;

    public Benchmark(BenchmarkRunParameters runParameters) {
        this.runParameters = runParameters;
        this.runResult = null;
        this.op = null;
        this.patternEngineInitialized = false;
    }

    public SingleRunResult run() {
        LOG.debug("CASE={}, OP={}, SIZE={}, RUN={}: Run benchmark", runParameters.getBenchmarkCaseName(),
                runParameters.getOperationalization(), new Integer(runParameters.getModelSize()),
                runParameters.getRepetition());

        runResult = new SingleRunResult();
        runResult.setRepetition(runParameters.getRepetition());

        try {
            createOperationalizationInstance();
            measureTimes();
        } catch (BenchmarkFailedException e) {
            runResult.setError(e.getMessage());
            LOG.debug("CASE={}, OP={}, SIZE={}, RUN={}: Execution failed. Reason: {}", runParameters.getBenchmarkCaseName(),
                    runParameters.getOperationalization(), new Integer(runParameters.getModelSize()),
                    runParameters.getRepetition(), e.getMessage());
        }

        terminate();

        return runResult;
    }

    protected abstract void createOperationalizationInstance() throws BenchmarkFailedException;

    protected void measureTimes() throws BenchmarkFailedException {
        ExecutorService es = Executors.newSingleThreadExecutor();

        try {
            Future<Long> initializationResult = es.submit(() -> {
                long tic = System.nanoTime();
                if (runParameters.getPatternMatchingEngine() == PatternMatchingEngine.Democles) {
                    op.registerBlackInterpreter(new DemoclesTGGEngine());
                    patternEngineInitialized = true;
                }
                long toc = System.nanoTime();
                return (toc - tic) / 1000000L;
            });
            LOG.debug("CASE={}, OP={}, SIZE={}, RUN={}: Measure initialization time", runParameters.getBenchmarkCaseName(),
                    runParameters.getOperationalization(), new Integer(runParameters.getModelSize()),
                    runParameters.getRepetition());
            runResult.setInitializationTime(initializationResult.get(runParameters.getTimeout(), TimeUnit.SECONDS));

            postInitialization();
            
            Future<Long> executionResult = es.submit(() -> {
                long tic = System.nanoTime();
                op.run();
                long toc = System.nanoTime();
                return (toc - tic) / 1000000L;
            });
            LOG.debug("CASE={}, OP={}, SIZE={}, RUN={}: Measure execution time", runParameters.getBenchmarkCaseName(),
                    runParameters.getOperationalization(), new Integer(runParameters.getModelSize()),
                    runParameters.getRepetition());
            runResult.setExecutionTime(executionResult.get(runParameters.getTimeout(), TimeUnit.SECONDS));

            BenchmarkLogger benchmarkLogger = op.getOptions().getBenchmarkLogger();
            runResult.setCreatedElements(benchmarkLogger.getTotalElementsCreated());
            runResult.setDeletedElements(benchmarkLogger.getTotalElementsDeleted());
            runResult.setFoundMatches(benchmarkLogger.getTotalMatchesFound());
            runResult.setAppliedMatches(benchmarkLogger.getTotalMatchesApplied());

        } catch (TimeoutException e) {
            throw new BenchmarkFailedException("Reached Timeout");
        } catch (Exception e) {
            throw new BenchmarkFailedException(e.getMessage());
        }

        es.shutdownNow();
    }

    protected void terminate() {
        LOG.debug("CASE={}, OP={}, SIZE={}, RUN={}: Terminate operationalization", runParameters.getBenchmarkCaseName(),
                runParameters.getOperationalization(), new Integer(runParameters.getModelSize()),
                runParameters.getRepetition());
        if (op != null && patternEngineInitialized) {
            try {
                op.terminate();
                op = null;
            } catch (IOException e) {
                // actually never thrown...
            }
        }
    }
    
    protected void postInitialization() throws BenchmarkFailedException { }

    /**
     * @return the runResult
     */
    public SingleRunResult getRunResult() {
        return runResult;
    }
}
