package LastPoisonCircle
import java.sql.{Connection, DriverManager}

import org.apache.spark._
class JDBCUtil {

  val url = "jdbc:mysql://192.168.2.131:3306/pubg"
  val driver = "com.mysql.jdbc.Driver"
  val username = "root"
  val password = "123456"
  Class.forName(driver)
  var connection = DriverManager.getConnection(url, username, password)

  def excueUpdate(sql : String): Unit ={
    try {
      val statement = connection.prepareStatement(sql)
      //      statement.setString(1,tableName)
      //      statement.setInt(2,x)
      //      statement.setInt(3,y)
      //      statement.setInt(4,num)
      statement.execute()
    } catch {
      case e: Exception => e.printStackTrace
    }
    //    connection.close
  }

  def init(): Unit ={
    val url = "jdbc:mysql://localhost:3306/pubg"
    val driver = "com.mysql.jdbc.Driver"
    val username = "root"
    val password = "root"

    var connection:Connection = null
    try {
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)
      val statement = connection.createStatement
      val rs = statement.executeQuery("SELECT host, user FROM user")
      while (rs.next) {
        val host = rs.getString("host")
        val user = rs.getString("user")
        println("host = %s, user = %s".format(host,user))
      }
    } catch {
      case e: Exception => e.printStackTrace
    }
    connection.close
  }

}

//最后一波毒圈刷在哪儿
object LastPoisonCircle {
  def main(args: Array[String]): Unit = {
    val whichmap = "ERANGEL"

    lastPoisonCircle(whichmap)
  }

  def lastPoisonCircle(whichmap : String) : Unit = {
    val conf = new SparkConf()
      .setAppName("LastPoisonCircle")
      .setMaster("local")

    val sc = new SparkContext(conf)

    import org.apache.spark.api.java.JavaRDD


    val aggTextFile = sc.textFile("D:\\绝地求生项目\\agg\\agg_match_stats_0.csv")
    val killTextFile = sc.textFile("D:\\绝地求生项目\\kill\\kill_match_stats_final_0.csv")
    //拿出agg表中的每一局第一名的((比赛id，玩家姓名)，比赛名次))
    val aggRDD = aggTextFile.map(x=>x.split(",")).filter(x => x(14) == "1" ).map(x=>{(((x(2), x(11)),1))})
    val killRDD = killTextFile.map(_.split(",")).filter(x => x(5) == whichmap && x(3).length >0 && x(4).length >0).map(x => {(((x(6), x(1))),(x(7),(x(3),x(4))))})
    val aggJoinKillRDD = aggRDD.join(killRDD).reduceByKey((x,y) =>{
      if(x._2._1.toInt>=y._2._1.toInt){
        x
      }else {
        y
      }
    } )
    val coordinateRDD = aggJoinKillRDD.map(x => x._2._2._2).map(x => (((x._1.toDouble / 800).toInt, (x._2.toDouble / 800).toInt), 1)).reduceByKey(_+_)
    val sampleRDD = coordinateRDD.sortBy(_._2, ascending = false).take(1000)
//                    .takeSample(true, 1000)
    val jdbc = new JDBCUtil
    for (i <- sampleRDD){
      var x = i._1._1
      val y = i._1._2
      val num = i._2
      val map = whichmap
      val sql = "insert into zc_lastPoisonCircle values('"+ x + "'," + y + "," + num + ",'" + map + "')"
      println(sql)
      jdbc.excueUpdate(sql)
    }
  }
}
