package KillingChicken
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



// 杀人数与吃鸡率  计算每个队伍杀人的个数及名次，然后安第一名的的吃鸡个数排序
object KillingChicken {
  def main(args: Array[String]): Unit = {
    val mathMode = "tpp" //游戏模式
    val party_size = "4"  //队伍人数

    killingChicken(mathMode, party_size)

  }
  def killingChicken(mathMode : String, party_size : String) : Unit ={
    val conf = new SparkConf()
      .setAppName("KillingChicken")
      .setMaster("local")
//      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val sc = new SparkContext(conf)

    val aggTextFile = sc.textFile("D:\\绝地求生项目\\agg\\*")
//    ((比赛名次，击杀人数), 杀人总数)
    val eatCheckRDD = aggTextFile.map(x => x.split(","))
      .filter(x =>  x(3) == mathMode && x(4) == party_size )
      .map(x => {
        var team_placement = 1
        if (x(14) != "4"){
          team_placement = 2
        }
        ((x(10), team_placement), 1)
      })
    val reduceByKeyRDD = eatCheckRDD.reduceByKey(_+_).map(x => (x._1._1.toInt, (x._1._2, x._2.toDouble))).reduceByKey((x, y) => {
      val sum = x._2 + y._2
      var result = 0.0
      if (x._1 > 0 && x._1 <30 ){
        if (x._1 == 1){
          result = x._2 / sum
        }
        else {
          result = y._2 / sum
        }

      }
      (1, result)
    })
    reduceByKeyRDD.sortBy(_._1).foreach(println)

//    val countChicken = reduceRDD.count()
//    val chickenRateRDD = reduceRDD.reduceByKey(_+_).map(x => (x._1.toInt / party_size.toInt, x._2.toDouble / countChicken)).reduceByKey(_+_).sortByKey()
    val jdbc = new JDBCUtil
    reduceByKeyRDD.sortBy(_._1).collect.foreach(x => {
      val killNum = x._1
      val checkRate = x._2._2
      val partySize = party_size
      val sql = "insert into zc_killingChicken values("+ killNum + "," + checkRate + ",'" + partySize + "')"
      println(sql)
      jdbc.excueUpdate(sql)
    })
  }
}