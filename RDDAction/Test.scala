import org.apache.spark._
import org.apache.spark.storage.StorageLevel
class Jdbc {
  import java.sql._

  val url = "jdbc:mysql://192.168.2.131:3306/pubg"
  val driver = "com.mysql.jdbc.Driver"
  val username = "root"
  val password = "123456"
  Class.forName(driver)
  var connection = DriverManager.getConnection(url, username, password)


  def inserFirst(x:String,y:Float): Unit ={
    try {
      val statement = connection.prepareStatement("insert into zk_most_arms values(?,?)")
      statement.setString(1,x)
      statement.setFloat(2,y)

      statement.executeUpdate()
    } catch {
      case e: Exception => e.printStackTrace
    }
  }
}

object Test {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("Test")
//      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .setMaster("local")


    val sc = new SparkContext(conf)

    val textFile = sc.textFile("C:\\pubg数据\\数据\\deaths_infos\\*")
    val argmsRDD = textFile.map(x=>(x.split(",")(0),1)) .filter(x=>x._1 != "Down and Out" && x._1 !="Bluezone" && x._1 != "Punch").reduceByKey(_+_)
    val count=argmsRDD.map(_._2).reduce(_+_)
    val fina=argmsRDD.map(x=>(x._1,(x._2.toFloat/count).formatted("%.3f"))).sortBy(x=>x._2,false).take(10)

//    argmsRDD.foreach(println)
//      .persist(StorageLevel.MEMORY_ONLY_SER)
//    argmsRDD.saveAsTextFile("/zhu")
    val jdbc = new Jdbc()
    fina.foreach(x => {
      println(x._1,x._2)

      jdbc.inserFirst(x._1,x._2.toFloat)
    })
    println(count)
  }
}
