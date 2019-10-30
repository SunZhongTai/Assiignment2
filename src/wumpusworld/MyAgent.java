package wumpusworld;

import java.util.ArrayList;

/**
 * Contains starting code for creating your own Wumpus World agent.
 * Currently the agent only make a random decision each turn.
 * 
 * @author Johan Hagelbäck
 */
public class MyAgent implements Agent
{
    private World w;
    private int goal_x = 0;
    private int goal_y = 0;
    private boolean has_goal = false;// 就是暂时想要达到的位置，就是根据当前环境得出的概率的那个位置
    private boolean if_shoot = false;
    private int pits = 0;
    private int wumpus_status = NOT_FOUND;  //没有发现wumpus
    private int[] wumpus_pos = {0,0}; //wumpus的位置
    private int[] arrow_goal = {0,0};  //箭的位置    
    private int[] ban = {0,0};   //
    private  boolean catch_bug = false;


    /*wumpus status*/
    public static final int NOT_FOUND = 0;
    public static final int FOUND = 1;
    public static final int DEAD = 2;




    /**
     * Creates a new instance of your solver agent.
     * 
     * @param world Current world state 
     */
    public MyAgent(World world)
    {
        w = world;   
    }
   
            
    /**
     * Asks your solver agent to execute an action.
     */

    //总方法
    public void doAction()
    {
        
        if(catch_bug){
            System.out.println("????????move function occurs exception??????????");
            return;
        }

        
        

        //Location of the player
        int cX = w.getPlayerX();
        int cY = w.getPlayerY();



        //Basic action:
        //获取金子的优先级还是很高的
        if (w.hasGlitter(cX, cY))
        {
            
            w.doAction(World.A_GRAB);
            return;
        }
        
        //Basic action:
        //
        if (w.isInPit())
        {
            w.doAction(World.A_CLIMB);
            return;
        }
                    
        //获取环境信息
        System.out.println("=========Gather Environmental Information======= ");
        //Test the environment
        if (w.hasBreeze(cX, cY))
        {
            System.out.println("I am in a Breeze");
        }
        if (w.hasStench(cX, cY))
        {
            System.out.println("I am in a Stench");
        }
        if (w.hasPit(cX, cY))
        {
            System.out.println("I am in a Pit");
        }
        if (w.getDirection() == World.DIR_RIGHT)
        {
            System.out.println("I am facing Right");
        }
        if (w.getDirection() == World.DIR_LEFT)
        {
            System.out.println("I am facing Left");
        }
        if (w.getDirection() == World.DIR_UP)
        {
            System.out.println("I am facing Up");
        }
        if (w.getDirection() == World.DIR_DOWN)
        {
            System.out.println("I am facing Down");
        }
        
        //
        System.out.println("============Decide Next Move=============");
        if(cX==goal_x && cY==goal_y){ //到达暂时的想要到达的位置
            has_goal=false;  //到达goal之后就没有了goal,就要获取新的goal
            System.out.println("Hey! I have arrived the goal("+goal_x+","+goal_y+")!");
        }

        if(!has_goal){ //没有goal,获取新的goal
            Naive_Bayes nb = new Naive_Bayes(w);    //这里就调用了get_frontier去得到当前的所有的边界
            if(wumpus_status==NOT_FOUND){  
                if(nb.query_wumpus(wumpus_pos)){  //---》》》调用 query_wumpus->get_probability这里是求P(wumpus),如果找到了，返回wumpus_pos
                    wumpus_status = FOUND;
                }
                else{//                    
                }
            }
            else if (wumpus_status==FOUND){
                nb.set_wumpus_pos(wumpus_pos); //找到wumpus设置wumpus 的位置 ，
            }
            int[] goal = new int[2];
            if_shoot = nb.get_goal(goal,wumpus_status); //-》》》get_probability这里求的是P(pit)，并且返回是否可以射箭
            has_goal = true;  //有了新的goal,还没有到达新goal
            goal_x = goal[0];
            goal_y = goal[1];
            
            if(if_shoot){ //可以射箭, 一，take a risk P>=0.25  二，发现wunmpus并且wumpus是goal
                arrow_goal[0]=goal_x;
                arrow_goal[1]=goal_y;
            }
            System.out.println("I have a new goal:  ("+goal_x+","+goal_y+")"); //每次执行完getgoal都得到一个goal
        }               
        System.out.println(" ============总的行动模式开始！===========");
//        int time = 0;
        ban[0]=0;
        ban[1]=1;
        move_to_goal(cX,cY);//-》》》》》》》》》》》》做出实际行动的move_to_goal->act->再到系统写的doAction 里面就不用管了
        System.out.println(" ============总的行动模式结束！===========");                
    }


    //      move_to_goal(cX,cY)     
    public void move_to_goal(int x,int y)  //这里的x,y是当前的点
    {
        //如果拿到金子，也不用走了啊
        //REVISION
         if (w.hasGlitter(x, y))
        {
            w.doAction(World.A_GRAB);
            return;
        }
            
        boolean is_adjacent = false;
        if(get_distance(x,y,goal_x,goal_y)==1){
            //即相邻,紧挨着
            is_adjacent = true;
        }
        int dir = w.getDirection();
        System.out.println("<<<<ACTION>>>>>");

        /*常规路由*/
        //如果在最终位置左边。大体向右，act right
            if(x<goal_x){
                //因为向右走吗，如果右边这个是已知的并且没有pit并且
                //因为是走向暂时的goal，如果是相邻，就大体向右，实际动作还要看act
                if(!w.isUnknown(x+1,y)&&!w.hasPit(x+1,y)&&!(x+1==ban[0])||is_adjacent){
                    act(dir,World.DIR_RIGHT);     //-》》》》》》》》》》》》right=1;
                    return;
                }
            }
            if(x>goal_x){                                        //x-1!=0;
                if(!w.isUnknown(x-1,y)&&!w.hasPit(x-1,y)&&!(x-1==ban[0])||is_adjacent){
                    act(dir,World.DIR_LEFT);//left=3;
                    return;
                }
            }
            if(y<goal_y){   //因为要总体往上移动
                 //如果相邻，那就直接往上移动就可以了
                 //如果不相邻，
                if(!w.isUnknown(x,y+1)&&!w.hasPit(x,y+1)&&!(y+1==ban[1])||is_adjacent){
                    act(dir,World.DIR_UP);//up=0;
                    return;
                }
            }
            if(y>goal_y){  //因为要总体往下移动
               //如果相邻，那就直接往下移动就可以了
               //如果不相邻，那么当前下面紧挨着的那个点必须 已知 且不是pit 且不等于困境的y(也就是不能走进困境里去，因为走进去的话，有很大几率要进pit来解除困境的状态)
                if(!w.isUnknown(x,y-1)&&!w.hasPit(x,y-1)&&!(y-1==ban[1])||is_adjacent){
                    act(dir,World.DIR_DOWN); //down=2
                    return;
                }
            }            
            
        /*检查当前是否困于死路*/
        int pitdir = is_impasse();// 如果返回的是-1，不是困境，可以继续进行; 返回其他的0,1,2,3  就是困境，走有洞的方向>=0
        if( pitdir >= 0){
            ban[0]=x;       //当前点x
            ban[1]=y;       //当前点y
            act(dir,pitdir); /*如果为死路，只能通过陷阱继续走  dir=w.getDirection(),pitdir为陷阱洞的方向，向陷阱方向走了*/     //-》》》》》》》》》》》》
            System.out.println("因为困境，走进陷阱");
            return;
        }
            /*绕开障碍，迂回*/
            boolean has_new_goal = false;
            if(x != goal_x){       //x没到最终位置,可能在左，也可能在右
                for(int yy=1; yy<=w.getSize(); yy++){
                    boolean has_road = true;
                    if(x>goal_x){    //x在最终位置的右边 ,所以从右向左循环
                        for(int xx=x; xx>=goal_x; xx--){
                            if(w.isUnknown(xx,yy)){  //如果中间有未知的位置，则当前road放弃，要走已经走过的且没有困境的，就是说避开所有危险的
                                has_road = false;
                                break; //跳出当前for循环  
                            }
                        }
                    }
                    else{   //x在最终位置的左边
                        for(int xx=x; xx<=goal_x; xx++){
                            if(w.isUnknown(xx,yy)){
                                has_road = false;
                                break;
                            }
                        }
                    }
                    if(has_road){
                        goal_y = yy; //如果其中得到了一条路径
                        System.out.println("我避开了障碍(x) I'm standing at ("+w.getPlayerX()+","+w.getPlayerY()+")");
                        System.out.println("heading to new goal:("+goal_x+","+goal_y+") !");
                        has_new_goal = true;
                        break;
                    }
                }//for

            }//if x没到最终位置
                //y没到最终位置
            else if(y != goal_y){
                boolean has_road = true;
                for(int xx=1; xx<=w.getSize(); xx++){
                    if(y>goal_y){
                        for(int yy=y; yy>=goal_y; yy--){
                            if(w.isUnknown(xx,yy)){
                                has_road = false;
                                break;
                            }
                        }
                    }

                    else{
                        for(int yy=y; yy<=goal_y; y++){
                            if(w.isUnknown(xx,yy)){
                                has_road = false;
                                break;
                            }
                        }
                    }

                    if(has_road){
                        goal_x = xx;
                        System.out.println("我避开了障碍(y)I'm standing at ("+w.getPlayerX()+","+w.getPlayerY()+")");
                        System.out.println("heading to new goal:("+goal_x+","+goal_y+") !");
                        has_new_goal = true;
                        break;
                    }
                }

            }
            else // x，y都到最终位置了
                System.out.println("喵喵喵喵喵喵？");
            if(!has_new_goal){
                change_goal(); ///如果还是找不到找不到路线，那我只能换goal//重写
                
                System.out.println("如果还是找不到找不到路线，那我只能换goal");
            }
            move_to_goal(w.getPlayerX(),w.getPlayerY());      //因为每次你只走一步吗,所以在你没到goal的时候就要不断执行了啊
    }


    // act(dir,World.DIR_RIGHT)  World.DIR_RIGHT=1;
    private void act(int currentDir,int goalDir) //其实真正的操作都在这里完成的，然后调用系统写的doAction
    {
        if(w.isInPit()){
            w.doAction(World.A_CLIMB);
            System.out.println("I fall into the pit!");
            System.out.println("I climbed out!");
        }
        //同向
        if(currentDir-goalDir==0){  //如果可以射箭并且   当前位置与可以射向箭的位置相邻
            if(if_shoot&&get_distance(w.getPlayerX(),w.getPlayerY(),arrow_goal[0],arrow_goal[1])==1){
                w.doAction(World.A_SHOOT);
                System.out.println("I shoot!"); ///这里是真正的射箭
                if_shoot=false;    //射完箭了，所以不能再射箭了
                if(!w.wumpusAlive()){
                    wumpus_status = DEAD;
                    System.out.println("I killed the fucking wumpus!!!");
                }
            }
            else { //如果不可以射箭或者与射箭的位置大于1个距离
                w.doAction(World.A_MOVE);     //MOVE 的意思就是进入吗?
                System.out.println("I go ahead!");
            }
        }
        
        //目标方向在当前方向的右手边
        else if(currentDir-goalDir==-1 || currentDir-goalDir==3) {
            w.doAction(World.A_TURN_RIGHT);
            System.out.println("I turn right!");
        }
        //目标方向在当前方向的左手边
        else  {
            w.doAction(World.A_TURN_LEFT);
            System.out.println("I turn left!");
        }

    }

    //得到两点间的距离
    private int get_distance(int x1, int y1, int x2, int y2){
        int dis = Math.abs(x1-x2)+Math.abs(y1-y2);
        return  dis;
    }

    //就是从右向左，从上往下逐个看是否能为新的目标
    private  void change_goal(){        
        //如果当前右边这个点已知 并且右边这个点合法，这个时候也没有考虑pit了
//        int[] goal = new int[2];
//        Naive_Bayes nb=new Naive_Bayes();
//          if_shoot=nb.get_goal(goal,wumpus_status);
//          has_goal=true;
//          goal_x=goal[0];
//          goal_y=goal[1];

//
        if(!w.isUnknown(goal_x+1,goal_y) && w.isValidPosition(goal_x+1,goal_y)){
            System.out.print("Change goal ("+goal_x+","+goal_y+") to");
            goal_x += 1;
            System.out.println(" ("+goal_x+","+goal_y+")");
        }

        if(!w.isUnknown(goal_x-1,goal_y) && w.isValidPosition(goal_x-1,goal_y)){
            System.out.print("Change goal ("+goal_x+","+goal_y+") to");
           goal_x -= 1;
            System.out.println(" ("+goal_x+","+goal_y+")");
        }

        if(!w.isUnknown(goal_x,goal_y+1) && w.isValidPosition(goal_x,goal_y+1)){
            System.out.print("Change goal ("+goal_x+","+goal_y+") to");
          goal_y += 1;
            System.out.println(" ("+goal_x+","+goal_y+")");
        }

        if(!w.isUnknown(goal_x,goal_y-1) && w.isValidPosition(goal_x,goal_y-1)){
            System.out.print("Change goal ("+goal_x+","+goal_y+") to");
           goal_y -= 1;
            System.out.println(" ("+goal_x+","+goal_y+")");
        }
    }

    /*检查当前位置是否为死胡同（周边只有墙壁或陷阱或未知区域）,若为死胡同返回陷阱方向，不是困境返回-1*/
    private int is_impasse(){
       int dir = -1;
       int x = w.getPlayerX();
       int y = w.getPlayerY();

       if(w.isVisited(x+1,y) && !w.hasPit(x+1,y)){ //如果访问过并且没有pit，就可以再走这条路，这样就不是不是困境。返回-1
            return -1;
        }else{    //如果没访问过，或者有洞，那么就是困境
            if(w.hasPit(x+1,y))  //如果有洞，就朝着有洞的走吧，这里是向右的
                dir=w.DIR_RIGHT;  //
            else {}              //如果没访问过，为什么不走未知的呢，可能比较危险吧
        }

        if(w.isVisited(x-1,y) && !w.hasPit(x-1,y)){
            return -1;
        }else{
            if(w.hasPit(x-1,y)) dir=w.DIR_LEFT;
        }

        if(w.isVisited(x,y+1) && !w.hasPit(x,y+1)){
            return -1;
        }else{
            if(w.hasPit(x,y+1)) dir=w.DIR_UP;
        }

        if(w.isVisited(x,y-1) && !w.hasPit(x,y-1)){
            return -1;
        }else{
            if(w.hasPit(x,y-1)) dir=w.DIR_DOWN;
        }

        System.out.println("I'm stuck in a impasse !");
        return dir;
    }

    
}

