//class Jdbc {
//  import java.sql._
//
//  val url = "jdbc:mysql://192.168.2.131:3306/pubg_zk"
//  val driver = "com.mysql.jdbc.Driver"
//  val username = "root"
//  val password = "123456"
//  Class.forName(driver)
//  var connection = DriverManager.getConnection(url, username, password)
//
//
//  def inserFirst(x:String,y:Int): Unit ={
//    try {
//      val statement = connection.prepareStatement("insert into most_arms values(?,?)")
//      statement.setString(1,x)
//      statement.setInt(2,y)
//
//      statement.execute()
//    } catch {
//      case e: Exception => e.printStackTrace
//    }
//  }
//}

//object Jdbc{
//
//  def main(args: Array[String]): Unit = {
//    val jdbc=new Jdbc
//    jdbc.inserFirst("M4A1",600)
//
//
//  }

//}
