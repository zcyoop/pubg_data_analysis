package pubg

import org.apache.spark.{SparkConf, SparkContext}

object BestHelper {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("besthelper")
    val sc = new SparkContext(conf)
    val aggre = sc.textFile("hdfs:///user/hive/warehouse/db_hive.db/match_infos")

    new Test1().demo7(aggre)
  }
}
