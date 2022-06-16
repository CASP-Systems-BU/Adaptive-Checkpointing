# Build Job
```shell
cd expeirment-tools/
```

- Each experiment contains a Query and at least one Source.
- The jar package will be placed in the target/ folder of the current directory. 
- If there is a jar in the folder and you didn't modify the code, you can skip the build step and submit the job directly.

- For Query1 and 5 you need to run commands to package the corresponding Query, as well as the BidSource.
- For Query3 and 8 you need to run commands to package the corresponding Query, as well as the PersonSource and AuctionSource.

## Build KafkaSourceJob
```shell
build.sh b # build BidSource
build.sh a # build AuctionSource
build.sh p # build PersonSource
```
## Build Query
```shell
build.sh 1 # Build Query1
build.sh 3 # Build Query3
build.sh 5 # Build Query5
build.sh 8 # Build Query8
```
# Start Experiment
The script will automatically submit the jars of Query and its Source, just specify the Query to run.
```shell
start-exp.sh 1 # start Query1
start-exp.sh 3 # start Query3
start-exp.sh 5 # start Query5
start-exp.sh 8 # start Query8
```
# stop Experiment
Stop the experiment with a script, rather than manually
```shell
stop-exp.sh 1 # start Query1
stop-exp.sh 3 # start Query3
stop-exp.sh 5 # start Query5
stop-exp.sh 8 # start Query8
```