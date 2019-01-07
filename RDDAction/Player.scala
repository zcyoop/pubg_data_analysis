import org.apache.spark._
import org.apache.spark.storage.StorageLevel

class Jdbc_Player {
  import java.sql._

  val url = "jdbc:mysql://localhost:3306/pubg"
  val driver = "com.mysql.jdbc.Driver"
  val username = "root"
  val password = "root"
  Class.forName(driver)
  var connection = DriverManager.getConnection(url, username, password)


  def inserFirst(x:String,y:Float): Unit ={
    try {
      val statement = connection.prepareStatement("insert into test values(?,?)")
      statement.setString(1,x)
      statement.setFloat(2,y)

      statement.executeUpdate()
    } catch {
      case e: Exception => e.printStackTrace
    }
  }
}
object Player {
  def main(args: Array[String]): Unit = {


    val conf = new SparkConf()
      .setAppName("Player")
      .setMaster("local")
//      .setMaster("19")
    val sc = new SparkContext(conf)
//    val textFile = sc.textFile("c:\\pubg数据\\数据\\kill_match_stats_final_0.csv")
    val textFile = sc.textFile("C:\\pubg数据\\数据\\deaths_infos\\*")
//    val RDD = textFile.map(x => {
//      val lines = x.split(",")
//      val armgs = lines(0)
//      val length = Math.sqrt(Math.pow(lines(3).toDouble - lines(10).filter(null).toDouble, 2) + Math.pow(lines(4).filter(null).toDouble - lines(11).filter(null).toDouble, 2))/100
//      (armgs,length)
//    }).filter(x=>x._2>100).map(x=>(x._1,1)).reduceByKey(_+_).persist(StorageLevel.MEMORY_ONLY_SER)
//    近战适合使用什么武器，狙击适合使用什么武器呢？
//    近战是距离小于30m，狙击是距离大于100m
//    val count = textFile.map(x=>x.split(",")).filter(x=>x(3)!=""&x(4)!="").count()
val RDD = textFile.map(x=>x.split(",")).filter(x=>x(3)!="" && x(3) !="killer_position_x" & x(4)!="" &&x(4)!="killer_position_y").filter(x=>x(0)!="Down and Out" &&x(0)!="Punch"&&x(0)!="Bluezone")
  .map(x =>{
      val length = Math.sqrt(Math.pow(x(3).toFloat - x(10).toFloat, 2) + Math.pow(x(4).toFloat - x(11).toFloat, 2))/100
      (x(0),length)
    }).filter(x=>x._2<5).map(x=>(x._1,1)).reduceByKey(_+_)
    val count = RDD.map(x=>x._2).reduce(_+_)
    val per = RDD.map(x=>(x._1,(x._2.toFloat/count).formatted("%.3f"))).sortBy(x=>x._2,false).take(10)
    per.foreach(x=>{
      println(x._1,x._2)
      val jdbc = new Jdbc_Player
//      jdbc.inserFirst(x._1,x._2.toFloat)
    })
  }



}
