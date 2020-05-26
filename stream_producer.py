
from kafka import KafkaProducer
from time import sleep
import json, sys
import requests
import time
import os.path
from elasticsearch import Elasticsearch
import datetime


def getData(url):
    #print('inside get data')
    jsonData = requests.get(url).json()
    data = []
    labels = {}
    index = 0
    if(os.path.isfile('labels.txt')):
              with open('labels.txt','r') as labelfile:
                #print('Inside reading file')
                f = labelfile.readlines()
                for line in f:
                   labels[line.split(',')[0]] = int(line.split(',')[1])
              index = int(f[-1].split(',')[1])+1

    for i in range(len(jsonData["response"]['results'])):
        headline = jsonData["response"]['results'][i]['fields']['headline']
        bodyText = jsonData["response"]['results'][i]['fields']['bodyText']
        headline += bodyText
        label = jsonData["response"]['results'][i]['sectionName']
        if label not in labels:
            labels[label] = index
            with open('labels.txt','a') as labelfile:
              labelfile.write(label + "," + str(index) + "\n")
            index += 1
        # data.append({'label':labels[label],'Descript':headline})
        toAdd = label + '~' + headline
        with open('data.txt', 'a') as outfile:
          print('inside writing file')
          outfile.write(toAdd + '\n')
        data.append(toAdd)
    return (data)


def publish_message(producer_instance, topic_name, value):
    try:
        
        key_bytes = bytes('foo', encoding='utf-8')
        value_bytes = bytes(value, encoding='utf-8')
        producer_instance.send(topic_name, key=key_bytes, value=value_bytes)
        producer_instance.flush()
        print('Message published successfully.')
    except Exception as ex:
        print('Exception in publishing message')
        print(str(ex))


def connect_kafka_producer():
    _producer = None
    try:
        # _producer = KafkaProducer(value_serializer=lambda v:json.dumps(v).encode('utf-8'),bootstrap_servers=['localhost:9092'], api_version=(0, 10),linger_ms=10)
        _producer = KafkaProducer(bootstrap_servers=['localhost:9092'], api_version=(0, 10), linger_ms=10)

    except Exception as ex:
        print('Exception while connecting Kafka')
        print(str(ex))
    finally:
        return _producer


if __name__ == "__main__":

    if len(sys.argv) != 4:
        print('Number of arguments is not correct')
        exit()

    key = sys.argv[1]
    fromDate = sys.argv[2]
    toDate = sys.argv[3]

    url = 'http://content.guardianapis.com/search?from-date=' + fromDate + '&to-date=' + toDate + '&order-by=newest&show-fields=all&page-size=200&%20num_per_section=10000&api-key=' + key
    all_news = getData(url)
    count = 0
    es = Elasticsearch([{'host':'localhost','port':9200}])

    if len(all_news) > 0:
        prod = connect_kafka_producer();
        print(len(all_news))
        for story in all_news:
            publish_message(prod, 'guardian2', story)
            label = story.split("~")[0]
            post_d = datetime.datetime.now()
            e1 = {"categ": label, "post_date":post_d}
            print(e1)
            res = es.index(index = "index", doc_type="type", body=e1)
            print(res)
            print(count)
            count += 1
            time.sleep(1)
        if prod is not None:
            prod.close()
