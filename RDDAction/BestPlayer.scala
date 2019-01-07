package pubg

import org.apache.spark.{SparkConf, SparkContext}

object BestPlayer {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("firstcircle")
    val sc = new SparkContext(conf)
    val aggre = sc.textFile("hdfs:///user/hive/warehouse/db_hive.db/match_infos")

    new Test1().demo4(aggre)
  }
}
