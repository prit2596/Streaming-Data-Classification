import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.feature.{StopWordsRemover,HashingTF,IDF,Tokenizer}
import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.classification.NaiveBayesModel
import org.apache.spark.ml.classification.MultilayerPerceptronClassificationModel
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.KafkaUtils
import kafka.serializer.StringDecoder


object streaming {

  def main(args: Array[String]): Unit = {
    val session = SparkSession
      .builder
      .appName("TrainData")
      .getOrCreate()

    val context = session.sparkContext
    val sql = session.sqlContext
    val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel(Level.ERROR)
    import sql.implicits._

    val streamingContext = new StreamingContext(context, Seconds(20))
    val topic: Set[String] = "guardian2".split(",").map(_.trim).toSet
    val broker = Map[String, String]("metadata.broker.list" -> "localhost:9092")
    val labelfile = context.textFile("labels.txt")

    val labeldata = labelfile.map(data => (data.split(",").toVector(0),data.split(",").toVector(1).toInt))
      .toDF("label","index")

    val stream  = KafkaUtils.createDirectStream[String,String,StringDecoder,StringDecoder](streamingContext,broker,topic)
    val news_content = stream.map(_._2)

//testing
    case class Tweet(name: String, message: String)
    case class TweetMessage(message: String)

    import jp.co.bizreach.elasticsearch4s._

//    ESClient.using("http://localhost:9200") { client =>
//      val config = ESConfig("twitter")
//
//      // Insert
//      client.insert(config, Tweet("takezoe", "Hello World!!"))
//      client.insertJson(config, """{name: "takezoe", message: "Hello World!!"}""")
//    }
//
      news_content.foreachRDD{
      df =>
        val news_data = df.map(df => (df.split("~").toVector(0),df.split("~").toVector(1)))
          .toDF("label","topic")
        val newsdf = news_data.join(labeldata,"label")
        val tokenizer = new Tokenizer().setInputCol("topic").setOutputCol("newstoken")
        val tokenized = tokenizer.transform(newsdf)

        val remover = new StopWordsRemover()
          .setInputCol("newstoken")
          .setOutputCol("newsstopwords")

        val sw_removal = remover.transform(tokenized)

        val hashingTF = new HashingTF()
          .setInputCol("newsstopwords")
          .setOutputCol("newsfeatures")
          .setNumFeatures(100)

        val data_final = hashingTF.transform(sw_removal)

        val idf = new IDF()
          .setInputCol("newsfeatures")
          .setOutputCol("newsfinal")

        val idfModel = idf.fit(data_final)

        val pre_processed = idfModel.transform(data_final)

        val pipeline = PipelineModel.load("modelsk/trainmodel1")

        val predictions1 = pipeline.transform(pre_processed)

        val accuracy = new MulticlassClassificationEvaluator().setLabelCol("index").setPredictionCol("prediction").setMetricName("accuracy")
        val precision = new MulticlassClassificationEvaluator().setLabelCol("index").setPredictionCol("prediction").setMetricName("weightedPrecision")
        val recall = new MulticlassClassificationEvaluator().setLabelCol("index").setPredictionCol("prediction").setMetricName("weightedRecall")

       //val model2 = PipelineModel.load("modelsk/trainmodel2")

        val model2 = NaiveBayesModel.load("modelsk/trainmodel2")
        val predictions2 = model2.transform(pre_processed)

        val output : String = "*****Decision Tree*****\n\n" +
                      "Accuracy = " + accuracy.evaluate(predictions1) + "\n\n" +
                      "Precision = " + precision.evaluate(predictions1) + "\n\n" +
                      "recall = " + recall.evaluate(predictions1) +"\n\n\n\n"+
                      "\n\n*****Naive Bayes*****\n\n\n\n"+
                      "Accuracy = " + accuracy.evaluate(predictions2) + "\n\n" +
                      "Precision = " + precision.evaluate(predictions2) + "\n\n" +
                      "recall = " + recall.evaluate(predictions2) + "\n\n"

        println(output)
    }

    streamingContext.start()
    streamingContext.awaitTermination()
    streamingContext.stop()
  }
}
