import org.apache.spark._
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
class Jdbc_Redzone {
  import java.sql._

  val url = "jdbc:mysql://192.168.2.131:3306/pubg"
  val driver = "com.mysql.jdbc.Driver"
  val username = "root"
  val password = "123456"
  Class.forName(driver)
  var connection = DriverManager.getConnection(url, username, password)


  def inserFirst(x:Int,y:Int,z:Int,g:String): Unit ={
    try {
      val statement = connection.prepareStatement("insert into zk_redzone values(?,?,?,?)")
      statement.setInt(1,x)
      statement.setInt(2,y)
      statement.setInt(3,z)
      statement.setString(4,g)

      statement.executeUpdate()
    } catch {
      case e: Exception => e.printStackTrace
    }
  }
}
object RedZone {
  def main(args: Array[String]): Unit = {
    val jdbc = new Jdbc_Redzone

    val conf = new SparkConf()
      .setAppName("RedZone")
      .setMaster("local")
//统计被轰炸炸死的概率，确定轰炸区的范围
    val sc = new SparkContext(conf)
    val textFile = sc.textFile("C:\\pubg数据\\数据\\deaths_infos\\*")
    val RDD = textFile.map(x=>x.split(",")).filter(x=>x(10) !="0.0"&&x(10)!="victim_position_x"& x(11) !="0.0" &&x(11) !="victim_position_y").filter(x=>x(5) == "MIRAMAR").map(x=>{
      val killed_by = x(0)
      val x_posision = ((x(10).toFloat)/800).toInt
      val y_posision = ((x(11).toFloat)/800).toInt
      (killed_by,(x_posision,y_posision))
    }).filter(x=>x._1 == "RedZone").map(x=>(x._2,1)).reduceByKey(_+_).sortBy(x=>x._2,false).filter(x=>x._2>1)
    RDD.collect.foreach(x=>{
      println(x._1,x._2)
      jdbc.inserFirst(x._1._1,x._1._2,x._2,"MIRAMAR")
    })

  }


}
