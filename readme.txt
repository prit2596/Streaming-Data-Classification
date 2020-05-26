Steps to run the code:

1) In Project folder, run 'sbt assembly' to generate fat jar

2) Run the training of data and creating ml models:
spark-submit --class training <Path to the jar file>BigData_Hw3/target/scala-2.11/BigData_Hw3-assembly-0.1.jar

3) Run the live data on the trained model:
 spark-submit --class streaming BigData_Hw3/target/scala-2.11/BigData_Hw3-assembly-0.1.jar

Also simultaneously run the stream_producer.py file to fetch the data
python3 stream_producer.py 0a571223-a3a8-4fdd-9d2c-6b17c94c4743 2019-04-03 2019-04-04
