package pubg

import org.apache.spark.{SparkConf, SparkContext}

object BestKDA {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("firstcircle")

    val sc = new SparkContext(conf)
    val death = sc.textFile("hdfs:///user/hive/warehouse/db_hive.db/deaths_infos")

//    val death = sc.textFile("file:///c:\\Users\\58212\\Desktop\\kill_match_stats_final_0.csv")
    new Test1().demo6(death)
  }
}
