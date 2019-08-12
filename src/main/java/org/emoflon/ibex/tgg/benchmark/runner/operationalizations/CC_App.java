package org.emoflon.ibex.tgg.benchmark.runner.operationalizations;

import java.io.IOException;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emoflon.ibex.tgg.operational.strategies.opt.cc.CC;
import org.emoflon.ibex.tgg.benchmark.runner.util.PerformanceTestUtil;
import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;

public class CC_App extends CC {
	private String modelPath;

	public CC_App(String projectName, String workspacePath, boolean debug, String modelPath) throws IOException {
		super(createIbexOptions()
				.projectName(projectName)
				.projectPath(projectName)
				.workspacePath(workspacePath)
				.debug(debug));
		this.modelPath = modelPath;
	}

	@Override
	protected void registerUserMetamodels() throws IOException {
		new PerformanceTestUtil().registerUserMetamodels(options.projectPath(), rs, this);
		
		// Register correspondence metamodel last
		loadAndRegisterCorrMetamodel(options.projectPath() + "/model/" + options.projectName() + ".ecore");
	}
	
	@Override
	public void loadModels() throws IOException {
		s = loadResource(options.projectPath() + "/instances/"+modelPath+"/src.xmi");
		t = loadResource(options.projectPath() + "/instances/"+modelPath+"/trg.xmi");
		c = createResource(options.projectPath() + "/instances/"+modelPath+"/corr.xmi");
		p = createResource(options.projectPath() + "/instances/"+modelPath+"/protocol.xmi");
	
		EcoreUtil.resolveAll(rs);
	}
	
	@Override
	public void saveModels() {
		// Models needn't be saved for all Operationalizations except MODELGEN
	}
	
	protected static IbexOptions createIbexOptions() {
		IbexOptions options = new IbexOptions();
		return options;
	}
}
