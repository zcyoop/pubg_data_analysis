# pubg_data_analysis

pubg数据分析

分析手段:Spark


#### 1. 数据说明 

1. 数据大小：20G。包含70多万场比赛,上亿条玩家的数据

2. 数据字段说明：

   ```scala
   date string,   // 时间
   game_size int,  // 队伍数量
   match_id string,  //  id 与 deaths 相关
   match_mode string,  // 游戏模式 第一人称（fpp）或第三人称（tpp）
   party_size int,  // 每只队伍的人数
   player_assists float,  // 玩家救助次数
   player_dbno float,  // 玩家被击倒次数
   player_dist_ride float,  // 玩家乘坐车辆总距离(m)
   player_dist_walk float,  // 玩家步行总距离（m）
   player_dmg float,  // 玩家总共命中点数
   player_kills float,  // 玩家击杀人数
   player_name string,  // 玩家姓名
   player_survive_time string,  // 结束时间
   team_id string,  // 玩家所在团队名称
   team_placement float // 队伍最终总名次
   
   
   killed_by string,  // 玩家被杀死的方式
   killer_name string, // 杀手玩家名称
   killer_placement float, //  杀手玩家最终排名
   killer_position_x float, // 杀手玩家位置x坐标
   killer_position_y float, // 杀手玩家位置y坐标
   map_name string,  // 地图名
   match_id string,  //  id
   time string,  // 游戏时间
   victim_name string,  // 被杀玩家的名字
   victim_placement float, // 被杀玩家最终排名
   victim_position_x float, // 被杀玩家位置 x 坐标
   victim_position_y float // 被杀玩家位置 y 坐标
   
   ```
3. 数据来源
   <https://www.kaggle.com/>
   
   
网站地址：http://120.79.68.233:5101/maps/

