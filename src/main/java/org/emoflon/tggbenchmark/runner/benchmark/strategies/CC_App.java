package org.emoflon.tggbenchmark.runner.benchmark.strategies;

import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emoflon.ibex.tgg.operational.benchmark.FullBenchmarkLogger;
import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;
import org.emoflon.ibex.tgg.operational.strategies.opt.cc.CC;
import org.emoflon.tggbenchmark.runner.BenchmarkRunParameters;

public class CC_App extends CC {

	private final BenchmarkRunParameters runParameters;

	public CC_App(BenchmarkRunParameters runParameters) throws IOException {
		super(new IbexOptions().projectName(runParameters.getTggProject()).projectPath(runParameters.getTggProject())
				.workspacePath(runParameters.getWorkspacePath().toString())
				.setBenchmarkLogger(new FullBenchmarkLogger()));

		this.runParameters = runParameters;
	}

	@Override
	protected void registerUserMetamodels() throws IOException {
		StrategiesUtils.registerUserMetamodels(rs, this, getClass().getClassLoader(),
				runParameters.getMetamodelsRegistrationClassName(),
				runParameters.getMetamodelsRegistrationMethodName());

		loadAndRegisterCorrMetamodel(options.projectPath() + "/model/" + options.projectPath() + ".ecore");
	}

	@Override
	public void loadModels() throws IOException {
		Path instPath = runParameters.getModelInstancesPath();
		s = loadResource(instPath.resolve("src.xmi").toString());
		t = loadResource(instPath.resolve("trg.xmi").toString());
		c = createResource(instPath.resolve("corr.xmi").toString());
		p = createResource(instPath.resolve("protocol.xmi").toString());

		EcoreUtil.resolveAll(rs);
	}

	@Override
	public void saveModels() {
		// only models created with MODELGEN need to be saved
	}
}
