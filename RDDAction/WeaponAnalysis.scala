package WeaponAnalysis

import java.sql.{Connection, DriverManager}

import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}


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

//分析武器的使用率 和平均击杀距离
object WeaponAnalysis {
  def main(args: Array[String]): Unit = {
//    val party_size = "2" //队伍人数
//    val whichmap = "MIRAMAR" //那张地图
    weaponAnalysis()
//    val jdbc = new JDBCUtil
//    jdbc.excueUpdate("insert into weaponAnalysis values('UMP9',0.07896397526919606,137)")
  }

  def weaponAnalysis(): Unit = {
    val wayDying = Array(
      "Punch", "Grenade", "Bluezone", "Hit by Car", "death.WeapSawnoff_C", "Pickup Truck",
      "Pan", "Falling", "Buggy", "Motorbike", "Motorbike (SideCar)", "death.ProjMolotov_DamageField_C",
      "Machete", "Sickle", "Van", "death.Buff_FireDOT_C", "Drown", "RedZone",
      "death.ProjMolotov_C", "Aquarail", "death.PG117_A_01_C","Down and Out","death.PlayerMale_A_C"
    )

    val conf = new SparkConf()
      .setAppName("seaponAnalysis")
//      .setMaster("local")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

    val sc = new SparkContext(conf)

    val killTextFile = sc.textFile("/user/hive/warehouse/db_hive.db/deaths_infos")
    val RDD = killTextFile.map(_.split(",")).filter(x => wayDying.contains(x(0)) == false && x(3)!="killer_position_x" && x(4)!="killer_position_y" &&  x(3).length >0 && x(4).length >0)
    val gunInfoRDD = RDD.map(x => (x(0),(1, math.round(math.sqrt(math.pow(x(3).toDouble - x(10).toDouble, 2) + math.pow(x(4).toDouble - x(11).toDouble, 2)) / 100))))
                        .reduceByKey((x,y) => (x._1 + y._1, x._2 + y._2))
                        .persist(StorageLevel.MEMORY_ONLY_SER)
    val count = gunInfoRDD.map(x => x._2._1).sum()
    val weaponAnalysisRDD = gunInfoRDD.map(x => (x._1, x._2._1.toDouble / count, x._2._2 / x._2._1))
                                      .sortBy(_._2, false)
    val jdbc = new JDBCUtil
    weaponAnalysisRDD.collect.foreach(x => {
      val weaponName = x._1
      val usageRate = x._2.formatted("%3f")
      val avgKillDistance = x._3
      val sql = "insert into zc_weaponAnalysis_copy values('"+ weaponName + "'," + usageRate + "," + avgKillDistance + ")"
//      println(sql)
      jdbc.excueUpdate(sql)

    })
  }
}
