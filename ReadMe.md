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

![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Screenshot 1")

![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Logo Title Text 1")

![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Logo Title Text 1")

![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Logo Title Text 1")

Also, results are visualized using Kibana and Elastic Search.
Over here, we will display results of trending news category in a window time frame by using a Tag cloud. Screenshots are below.

![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Logo Title Text 1")

![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Logo Title Text 1")

![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Logo Title Text 1")