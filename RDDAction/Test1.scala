package pubg

import java.sql.{Connection, DriverManager}

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import test.JDBCUtil

import scala.util.Random

class Test1 {
//  def main(args: Array[String]): Unit = {
//    val conf = new SparkConf()
//      .setAppName("gameTime")
//      .setMaster("local")
//    val sc = new SparkContext(conf)
//    val death = sc.textFile("file:///c:\\Users\\58212\\Desktop\\kill_match_stats_final_0.csv")
//    val aggre = sc.textFile("file:///c:\\Users\\58212\\Desktop\\agg_match_stats_0.csv")
//        demo2(death)
//  }

  //  统计游戏时长
  def demo1(rdd: RDD[String]): Unit = {
    val time = rdd.map(_.split(",")).map(x => x(7)).distinct().map(Integer.parseInt(_)).collect().sorted
    time.foreach(println(_))
  }


  //  缩第一个圈之前战斗最激烈的地方
  def demo2(rdd: RDD[String],map:Int): Unit = {
    val jdbc=new JDBCUtil
    val table="first_circle"
    val mapMap = Map(
      1 -> "ERANGEL",
      2 -> "MIRAMAR"
    )
    val point = deathsFilter(rdd,map).map(x => (x(7).toInt, (x(10).toFloat / 800, x(11).toFloat / 800))).filter(x => x._1 < 250).filter(x=>x._2._1!=0&x._2._1!=0).map(x => ((x._2._1.toInt,x._2._2.toInt),1)).reduceByKey(_+_).filter(_._2>=150).collect()
    .foreach(x=>jdbc.inserPoint(x._1._1,x._1._2,x._2,mapMap.get(map).get,table))
  }


  //根据击杀距离找出异常玩家
  def demo3(death: RDD[String]): Unit = {
    val jdbc=new JDBCUtil
    val deathRDD = deathsFilter(death)

    val errorDistance = deathRDD.filter(x => x(3) != "" & x(4) != "").map(x => ((x(6),x(1)),(x(3).toFloat / 100, x(4).toFloat / 100, x(10).toFloat / 100, x(11).toFloat / 100))).filter(x=>x._2._1!=0&x._2._2!=0&x._2._3!=0&x._2._4!=0).map(x => (x._1, math.sqrt(math.pow(x._2._3 - x._2._1, 2) + math.pow(x._2._4 - x._2._2, 2)))).filter(_._2>500).map(x=>(x._1,1)).reduceByKey(_+_).filter(_._2>=30).map(_._1._2)
    errorDistance.collect().foreach(jdbc.insertErrorPlayer)
  }

  //单排吃鸡小能手
  def demo4(rdd: RDD[String]): Unit = {
    val jdbc=new JDBCUtil
//    jdbc.clearTable("zcy_bestplayer")
    val bestChicken = aggreFilter(rdd, 1).filter(_ (14) == "1").map(x => (x(11), 1)).reduceByKey(_ + _).filter(_._1 != "").sortBy(_._2, ascending = false)

//    bestChicken.take(50).foreach(x=>jdbc.insertBestPlayer(x._1,x._2,"zcy_bestplayer"))
    bestChicken.take(50).foreach(println)
  }


  //最后一个毒圈刷新地点分布图
  def demo5(rdd:RDD[String],map:Int=0): Unit = {
    val finaCircle = deathsFilter(rdd,map).filter(x=>x(2)=="1.0"&x(9)=="2.0").map(x=>((((x(3).toFloat+x(10).toFloat)/2/800).toInt/10*10,((x(4).toFloat+x(11).toFloat)/2/800).toInt/10*10),1)).reduceByKey(_+_).sortBy(_._2,false).take(500)
    finaCircle.foreach(println)
  }
  //KDA排行榜

  def demo6(rdd:RDD[String],map:Int=0): Unit = {
    val jdbc=new JDBCUtil
    jdbc.clearTable("zcy_bestkda")
    val death = deathsFilter(rdd)
    val kda = death.map(x=>(x(1),x(8)))
    val killRDD = kda.map(x=>(x._1,1)).reduceByKey(_+_)
    val deathRDD = kda.map(x=>(x._2,1)).reduceByKey(_+_)
    val firstMan = death.filter(_(2)=="1.0").map(x=>(x(1),1)).reduceByKey(_+_)

    val kdainfo = killRDD.join(deathRDD).map(x=> (x._1, x._2._1.toFloat/x._2._2.toFloat)).filter(_._2<50)
    firstMan.join(kdainfo).sortBy(_._2._1,false).filter(_._1!="#unknown").filter(_._2._2<=20).take(500).foreach(x=>jdbc.insertBestKda(x._1,x._2._1,x._2._2))

    //x=>jdbc.insertBestPlayer(x._1,x._2,"zcy_bestkda")

  }

  //中国好队友
  def demo7(rdd:RDD[String],map:Int=0): Unit = {
    val jdbc=new JDBCUtil
    jdbc.clearTable("zcy_besthelper")

    aggreFilter(rdd).filter(x=>x(5)!="player_assists").map(x=>(x(11),x(5).toInt)).reduceByKey(_+_).filter(_._1!="").sortBy(_._2,false).take(500).foreach(x=>jdbc.insertBestPlayer(x._1,x._2,"zcy_besthelper"))
  }

  //优化后的吃鸡小能手
  def demo8(rdd:RDD[String]): Unit ={
    val cac= rdd.map(_.split("\u0001"))
    val bestChicken = cac.filter(x=>x(1) == "1"&x(0)!="").map(x => (x(0), 1)).reduceByKey(_ + _).sortBy(_._2, ascending = false).take(100).foreach(println)
  }

  //death表数据处理
  def deathsFilter(rdd: RDD[String], map: Int = 0): RDD[Array[String]] = {
    var nrdd = rdd.map(_.split(","))
    if (map != 0) {
      val mapMap = Map(
        1 -> "ERANGEL",
        2 -> "MIRAMAR"
      )
      nrdd = nrdd.filter(_(5) == mapMap.get(map).get)
    }
    nrdd
  }
  // aggre 表数据处理
  def aggreFilter(rdd: RDD[String], mode: Int = 0): RDD[Array[String]] = {
    var nrdd = rdd.map(_.split(","))
    if (mode != 0) nrdd = nrdd.filter(_ (4) == mode.toString())
    nrdd
  }

}
