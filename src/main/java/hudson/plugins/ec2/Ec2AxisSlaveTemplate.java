package hudson.plugins.ec2;

import hudson.EnvVars;
import hudson.model.TaskListener;
import hudson.slaves.EnvironmentVariablesNodeProperty;

import java.io.IOException;

import com.amazonaws.AmazonClientException;


public class Ec2AxisSlaveTemplate extends SlaveTemplate {

 	private static final String SLAVE_MATRIX_ENV_VAR_NAME = "MATRIX_EXEC_ID";
	private transient String instanceLabel;
	private transient int matrixId;

	public Ec2AxisSlaveTemplate(SlaveTemplate toDecorate) {
		super(
			 toDecorate.ami, 
			 toDecorate.zone, 
			 toDecorate.securityGroups, 
			 toDecorate.remoteFS, 
			 toDecorate.sshPort, 
			 toDecorate.type, 
			 toDecorate.getLabelString(),
			 toDecorate.mode, 
			 toDecorate.description, 
			 toDecorate.initScript, 
			 toDecorate.userData, 
			 toDecorate.numExecutors, 
			 toDecorate.remoteAdmin, 
			 toDecorate.rootCommandPrefix, 
			 toDecorate.jvmopts, 
			 toDecorate.stopOnTerminate, 
			 toDecorate.subnetId, 
			 toDecorate.getTags(), 
			 toDecorate.idleTerminationMinutes, 
			 toDecorate.usePrivateDnsName, 
			 toDecorate.getInstanceCapStr());
		instanceLabel = toDecorate.getLabelString();
	}

	public void setInstanceLabel(String label){
		instanceLabel = label;
	}
	
	public void setMatrixId(int id) {
		this.matrixId = id;
	}
	
	@Override
	public EC2Slave provision(TaskListener listener) throws AmazonClientException, IOException {
		EC2Slave provisionedSlave = super.provision(listener);
		provisionedSlave.setLabelString(instanceLabel);
		EnvVars envVars = getSlaveEnvVars(provisionedSlave);
		envVars.put(SLAVE_MATRIX_ENV_VAR_NAME, ""+matrixId);
		return provisionedSlave;
	}

	private EnvVars getSlaveEnvVars(EC2Slave provisionedSlave)
			throws IOException {
		EnvironmentVariablesNodeProperty v = provisionedSlave.getNodeProperties().get(EnvironmentVariablesNodeProperty.class);
		if (v == null) {
			v = new EnvironmentVariablesNodeProperty();
			provisionedSlave.getNodeProperties().add(v);
		}
		return v.getEnvVars();
	}
}
