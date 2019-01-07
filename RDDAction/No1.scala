package pubg

import org.apache.spark.{SparkConf, SparkContext}

object No1 {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("no1")

    val sc = new SparkContext(conf)
    val aggre = sc.textFile("hdfs:///user/hive/warehouse/db_hive.db/son")
    new Test1().demo8(aggre)
  }
}
