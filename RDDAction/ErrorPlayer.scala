package pubg

import org.apache.spark.{SparkConf, SparkContext}

object ErrorPlayer {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("firstcircle")
    val sc = new SparkContext(conf)
    val death = sc.textFile("hdfs:///user/hive/warehouse/db_hive.db/deaths_infos")

    new Test1().demo3(death)
  }
}
