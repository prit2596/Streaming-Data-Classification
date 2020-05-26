import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.feature._
import org.apache.spark.ml.classification.DecisionTreeClassificationModel
import org.apache.spark.ml.classification.DecisionTreeClassifier
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.{RandomForestClassificationModel, RandomForestClassifier}
import org.apache.spark.ml.classification.NaiveBayes
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator


object training{

  def main(args: Array[String]):Unit = {

    val spark = SparkSession
      .builder()
      .appName("TrainData")
      .getOrCreate()

    val sc = spark.sparkContext
    val sql = spark.sqlContext
    val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel(Level.ERROR)
    import sql.implicits._

    val input = sc.textFile("data.txt")
    val labels = sc.textFile("labels.txt")
    val df = input.map(df => (df.split("~").toVector(0),df.split("~").toVector(1))).toDF("label","topic")

    val labeldata = labels.map(data => (data.split(",").toVector(0),data.split(",").toVector(1).toInt))
      .toDF("label","index")
    val data = df.join(labeldata,"label")

    val tokenizer = new Tokenizer().setInputCol("topic").setOutputCol("newstoken")
    val tokenized = tokenizer.transform(data)

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

    val indexer = new VectorIndexer()
      .setHandleInvalid("skip")
      .setInputCol("newsfinal")
      .setOutputCol("indexed")

   /* val indexerModel = indexer.fit(pre_processed)

    val indexedData = indexerModel.transform(pre_processed)*/

    val dtc = new DecisionTreeClassifier().setLabelCol("index").setFeaturesCol("newsfinal")

    val pipeline = new Pipeline().setStages(Array(indexer,dtc))
    val trainmodel1 = pipeline.fit(pre_processed)
    trainmodel1.save("modelsk/trainmodel1")

  /*  val rf = new RandomForestClassifier()
      .setLabelCol("index")
      .setFeaturesCol("indexed")
      .setNumTrees(10)

    val pipelinerf = new Pipeline().setStages(Array(indexerModel,rf))
    val trainingmodel2 = pipelinerf.fit(pre_processed)
    trainingmodel2.save("modelsk/trainmodel2")
*/

    val trainer = new NaiveBayes().setLabelCol("index").setFeaturesCol("newsfinal")
    val trainingmodel2 = trainer.fit(pre_processed)
    trainingmodel2.save("modelsk/trainmodel2")


  }
}