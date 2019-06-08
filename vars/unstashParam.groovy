import hudson.FilePath
import hudson.model.ParametersAction
import hudson.model.FileParameterValue
import hudson.model.Executor

def call(String name, String fname = null) {
    def paramsAction = currentBuild.rawBuild.getAction(ParametersAction.class);
    if (paramsAction != null) {
        for (param in paramsAction.getParameters()) {
            if (param.getName().equals(name)) {
                if (! param instanceof FileParameterValue) {
                    error "unstashParam: not a file parameter: ${name}"
                }
                if (env['NODE_NAME'] == null) {
                    error "unstashParam: no node in current context"
                }
                if (env['WORKSPACE'] == null) {
                    error "unstashParam: no workspace in current context"
                }
                if (env['NODE_NAME'].equals("master")) {
                  workspace = new FilePath(null, env['WORKSPACE'])
                } else {
                  channel = Jenkins.getInstance().getComputer(env['NODE_NAME']).getChannel()
                  workspace = new FilePath(channel, env['WORKSPACE'])
                }
                filename = fname == null ? param.getOriginalFileName() : fname
                file = workspace.child(name)
				original_file = param.getFile()
				original_file.name = name
                // file.copyFrom(param.getFile())
                error "${original_file}"
				return filename;
            }
        }
    }
    error "unstashParam: No file parameter named '${name}'"
}

