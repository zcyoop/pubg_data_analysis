import org.apache.spark.{SparkConf, SparkContext}
class Jdbc_First {
  import java.sql._

  val url = "jdbc:mysql://192.168.2.131:3306/pubg"
  val driver = "com.mysql.jdbc.Driver"
  val username = "root"
  val password = "123456"
  Class.forName(driver)
  var connection = DriverManager.getConnection(url, username, password)


  def inserFirst(x:Int,y:Int): Unit ={
    try {
      val statement = connection.prepareStatement("insert into zk_firstpeople values(?,?)")
      statement.setInt(1,x)
      statement.setInt(2,y)

      statement.executeUpdate()
    } catch {
      case e: Exception => e.printStackTrace
    }
  }
}
object First {
  def main(args: Array[String]): Unit = {
    val jdbc = new Jdbc_First
    val conf = new SparkConf()
      .setAppName("RedZone")
//      .setMaster("local")
    //吃鸡的人杀人数量

    val sc = new SparkContext(conf)
    val textFile = sc.textFile("C:\\pubg数据\\数据\\match_infos\\*")
    val RDD = textFile.map(x=>x.split(",")).filter(x=>x(4) == "1" && x(14) =="1").map(x=>(x(10),1)).reduceByKey(_+_).sortBy(x=>x._2,false)
    RDD.collect.foreach(x=>{
      println(x._1,x._2)
      jdbc.inserFirst(x._1.toInt,x._2)
    })
  }

}
