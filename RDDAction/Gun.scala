import org.apache.spark.{SparkConf, SparkContext}

object Gun {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("RedZone")
      .setMaster("local")

    //什么武器最有用
    val sc = new SparkContext(conf)
    val textFile = sc.textFile("/user/hive/warehouse/db_hive.db/deaths")
    val RDD = textFile.map(x=>x.split(",")).filter(x=>x(9) =="2").map(x=>(x(0),1))
    RDD.foreach(println)

  }

}
