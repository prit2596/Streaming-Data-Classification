# Introduction

In this project, we will work with **Big Data Technologies and Machine Learning Classifiers**. The goal of our project is to classify the streaming data coming from Kafka Server. The data is coming from Guardian API which sends news data. Our stream producer file sends data to kafka server which sends data in interval of 20 seconds. The data coming from Kafka Server which we will classify into category of the news. We will use offline data to train our model and use the model to classify streaming data coming from Kafka. 

At the end, we will use Elastic Search and Kibana for visualization of our results. Elastic Search is used for storing results into indexes and these indexes are used by kibana for various visualization. Here we have created **Tag cloud** of the categories of the news data classified. We have shown trending news category in a window time frame by Tag Cloud.

Here, we have used scala and python for our project.

# Files Description

* stream_producer.py : modified to generate offline data for training

* training.scala : Loading of data and then doing the pre-processing tasks - tokenizer, stop words removal, tf-idf. Training the dataset on Decision Tree Classifier and Naive Bayes Classifier.

* streaming.scala : Fetching the news from kafka server in intervals of 20 seconds and using trained model does the classification process and displays the performance results(accuracy,precision,recall)

# Results

Results are displayed on the terminal when running the streaming.scala file. It shows accuracy, precision,recall of the classification. Below are the screenshots for the same.

![alt text](https://github.com/prit2596/Streaming-Data-Classification/blob/master/Screen%20Shot1.png "Screenshot 1")

![alt text](https://github.com/prit2596/Streaming-Data-Classification/blob/master/Screen%20Shot2.png "Logo Title Text 1")

![alt text](https://github.com/prit2596/Streaming-Data-Classification/blob/master/Screen%20Shot3.png "Logo Title Text 1")

![alt text](https://github.com/prit2596/Streaming-Data-Classification/blob/master/Screen%20Shot4.png "Logo Title Text 1")

Also, results are visualized using Kibana and Elastic Search.
Over here, we will display results of trending news category in a window time frame by using a Tag cloud. Screenshots are below.

![alt text](https://github.com/prit2596/Streaming-Data-Classification/blob/master/kibana1.PNG "Logo Title Text 1")

![alt text](https://github.com/prit2596/Streaming-Data-Classification/blob/master/kibana2.PNG "Logo Title Text 1")

![alt text](https://github.com/prit2596/Streaming-Data-Classification/blob/master/kibana3.PNG "Logo Title Text 1")


# How to run the code

1) In Project folder, run *'sbt assembly'* to generate fat jar

2) Run the training of data and creating ml models:\
```spark-submit --class training <Path to the jar file>BigData_Hw3/target/scala-2.11/BigData_Hw3-assembly-0.1.jar```

3) Run the live data on the trained model:\
```spark-submit --class streaming BigData_Hw3/target/scala-2.11/BigData_Hw3-assembly-0.1.jar```

Also simultaneously run the stream_producer.py file to fetch the data\
```python3 stream_producer.py 0a571223-a3a8-4fdd-9d2c-6b17c94c4743 2019-04-03 2019-04-04```
