# Build Problem
1. when you try to build, run 'mvn clean package -DskipTests -X', maven checkstyle is opened, so the build process fail may relate to style problem, find with -x and fix it! run 'mvn spotless:apply'
2. Never run 'mvn clean package'. It will cause some issue with test. 
3. For "Couldn't find/download xxxplugin" error, just rebuild it
4. If 'xxx module couldn't be found' appears when you are running an example. Try to rebuild the whole module / folder 
5. Build the whole Flink to ensure that you use the imported package of Custom Flink. Build example separately will use flink-1.14.0.

# Test Source Code
1. modify and run test files in every package, for small part of code modification, for example: flink-simplified-checkpoint/flink-runtime/**src/test**/java/org/apache/flink/runtime/jobmaster/JobMasterTest.java
2. Use flink-example/, run main() in the application which uses Flink API 

# Connect To Kafka
1. change listeners in server.properties to ensure port 9092 can be access by Flink application
2. use screen to run kafka and zookeeper

# Connect To Hadoop
1. add 2 env variables (HADOOP_CLASSPATH, HADOOP_CONF_DIR) to hadoop-env.sh. Accroding to https://nightlies.apache.org/flink/flink-docs-release-1.11/ops/deployment/hadoop.html#providing-hadoop-classes,
   Flink will use the environment variable HADOOP_CLASSPATH to augment the classpath that is used when starting Flink components . Most Hadoop distributions and cloud environments will not set this 
   variable by default so if the Hadoop classpath should be picked up by Flink the environment variable must be exported on all machines that are running Flink components explicitly.
   Another reason may be environment isolation cased by running the shell scripts.
2. add flink-shaded-hadoop-3-uber.jar to build-target/lib fro resolving dependencies conflicts. refer to
   https://nightlies.apache.org/flink/flink-docs-release-1.11/ops/deployment/hadoop.html#hadoop-integration.
3. change the IP in core-sites.xml to make the datanode connect to the namenode.
4. use http:<ip of the hadoop vm>:9870 to access the hadoop UI to debug and check status.
5. To add more vms as datanodes: add JAVA_HOME, HADOOP_HOME, HADOOP_CONF_DIR and HADOOP_CLASSPATH in "~/.bashrc" of all vms(master and workers).
Don't forget to use "source ~/.bashrc" to refresh the settings. Make new datanode folder and namenode folder in $HADOOP_HOME/hadoop_data/hdfs. Add these folders
as dfs.datanode.data.dir in hdfs-sites.xml. Change the replication factor to 3 (need 3 copies of the data). Add masters file in $HADOOP_CONF_DIR with masternode private IP. Add all the "workers" 
node private IP in "workers" file. When we need a new node use "scp /usr/local/hadoop flinknode-XX:/usr/local/" to install hadoop in that vm. Remove the datanode as well as namenode folders 
and recreate them. Also change some settings in yarn-site.xml and mapred-site.xml. 
Refer to 1). https://hadooptutorials.info/2020/10/05/part-1-apache-hadoop-installation-on-single-node-cluster-with-google-cloud-virtual-machine/
2).https://hadooptutorials.info/2020/10/09/part-2-add-a-new-data-node-on-existing-hadoop-cluster/
