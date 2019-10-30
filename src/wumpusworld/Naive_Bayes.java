package wumpusworld;

import java.util.ArrayList;

public class Naive_Bayes {

    private ArrayList<int[]> frontier = new ArrayList<int[]>();
    private ArrayList<double[]> probability_set = new ArrayList<double[]>();
    private World w,w2;
    private int[] wumpus_pos = new int[2];
    private static ArrayList<int[]> pit_pos = new ArrayList<int[]>();
    private  int pit_c = 0; //
    private boolean found_wumpus;
   //static 是作用在类上 而不是对象上，所以在this里面没有pit_pos,因为this是当前对象
    //P(wumpus | stench ) = [P( stench |wumpus )*P(wumpus)]/P(strench) 好像只有P(strench)未知
    //P(pit | breeze ) = [P( breeze |pit )*P(pit)]/P(breeze)
    /*probability of different cases*/ //概率 pit=3/15;   wumpus=1/15 因为默认第一个不可能有pit或者wumpus
    private static double PIT_PROB = 0.2;
    private static double WUMPUS_PROB = 0.0667;

    /*condition constants*/
    private static final int PIT = 1;
    private static final int WUMPUS = 2;
    
     /**
     * w2克隆当前世界情况，用于标注frontier 边界
     * @param world
     */
    public Naive_Bayes(World world) {
        w = world;
        w2=w.cloneWorld();  /*world model for get_frontier function*/
        get_frontier(1,1);
    }
    
    
    public Naive_Bayes(){           //为了每次结束后clear  pit_pos
        
    }
    
    /**
     *
     * @param x   递归起点的坐标
     * @param y
     */
    private void get_frontier(int x, int y) {

        System.out.println("checking the ("+x+","+y+")");
        if (!w2.isValidPosition(x, y)) {
            //System.out.println("因为是不合法的位置所以不执行");
            return;
        }
        if (w2.isUnknown(x, y)) {//未知
            if (!(w2.hasMarked(x, y)))  {//未知切未标记
                frontier.add(new int[]{x, y, 0});
                
                probability_set.add(new double[]{0,0});   /*Synchronously initialize corresponding probability set*/
                w2.setMarked(x, y);
                System.out.println("set ("+x+","+y+") as frontier");
            }
            else   //如果是未知，且被标记
                System.out.println("("+x+","+y+") has been set已经被设置了froniter");   
          
            return;
        }
        //已知
        else if(w2.hasMarked(x,y)) 
            return;
        else
            {
            w2.setMarked(x,y);
            get_frontier(x + 1, y );
            get_frontier(x - 1, y);
            get_frontier(x, y + 1);
            get_frontier(x, y - 1);
        }
    }

    private void get_probability(int condition) {   //计算往哪移的可能性      
        
        double p;
        if (condition == PIT) {
            p = PIT_PROB;
            System.out.println("我要计算P(pit)calculating P(pit)-----------------------------------");
            System.out.println("now the p of pit is: "+p);
        } else if (condition == WUMPUS) {
            p = WUMPUS_PROB; //更新过的
            System.out.println("我要计算P(wumpus) calculating P(Wumpus)===============================");
            System.out.println("now the p of wumpus is: "+p);
        } else {
                System.out.println("OUT OF CONDITION RANGE");
                return;
            }

        System.out.println("===============");
        
        for (int i = 0; i < frontier.size(); i++) {
            if(checkalreadypit(pit_pos,frontier.get(i)[0],frontier.get(i)[1],condition)){
                //probability_set.get(i)[condition-1]=1;                
                System.out.println("我已经算过");
                continue;
            }
            int[] query_true = new int[]{frontier.get(i)[0],frontier.get(i)[1],1}; //(x,y,1)
            int[] query_false = new int[]{frontier.get(i)[0],frontier.get(i)[1],0}; //(x,y,0)
            double total_pro_true=0;
            double total_pro_false=0;
            double f = 0;

            ArrayList<int[]> portion = cloneList(frontier);
          //  System.out.println("删除前frontier.size为"+frontier.size()+":("+frontier.get(i)[0]+","+frontier.get(i)[1]+")");
            portion.remove(i);    //删除的是索引，把当前的删除了
         //   System.out.println("删除后portion.size为"+portion.size());
            ArrayList<ArrayList<int []>> combinations = new ArrayList<ArrayList<int[]>>();
            int[] count = combination(portion,combinations); //combinations
            
            
            
            System.out.println("##### Query ("+frontier.get(i)[0]+","+frontier.get(i)[1]+")################### ");

            /*iteratively calculate P(combination) in list combinations*/
            for(int j = 0; j < combinations.size(); j++)
            {
                int sum = combinations.get(j).size();//sum是combanition里可能组合的长度
               // System.out.println("当前combination是第j:"+j+"个,combinations.size是多少呢是"+combinations.size());
                String msg = "Not consistent";
                //System.out.println("combinations.get(j).size是"+combinations.get(j).size());
                for(int k=0; k<combinations.get(j).size();k++){                  
                ////    System.out.print("第三位判断码是:"+combinations.get(j).get(k)[2]);
                }
                //System.out.println(" ");
                
   



               if((condition==PIT && count[j]<4) || (condition==WUMPUS && count[j]<2))
                {                    
                      //System.out.print("when P(query) is true: ");      
                    if(check_consistent(combinations.get(j),condition,query_true)) //-》》》》》》》》》query_true传
                    {
                       // System.out.println(" count[j]是"+count[j]);
                        double add = Math.pow(p,count[j]) * Math.pow(1-p,sum-count[j]);
                        total_pro_true += add;
                       // System.out.println("P^count*(1-P)^(sum-count)="+p+"^"+count[j]+"*"+(1-p)+"^"+(sum-count[j]));
                      //  System.out.println("当前计算"+frontier.get(i)[0]+","+frontier.get(i)[1]+"的P("+(condition==PIT?"PIT":"WUMPUS")+")为true,(也就是为"+(condition==PIT?"PIT":"WUMPUS")+"的概率)"+add);
                    }else {
                        //System.out.println(msg);
                    }

                   // System.out.print("when P(query) is false: ");                    
                    if(check_consistent(combinations.get(j),condition,query_false))//-》》》》》》》》》
                    {
                        //System.out.println(" count[j]是"+count[j]);
                        double add = Math.pow(p,count[j]) * Math.pow(1-p,sum-count[j]);
                        total_pro_false += add;
                       // System.out.println("P^count*(1-P)^(sum-count)="+p+"^"+count[j]+"*"+(1-p)+"^"+(sum-count[j]));
                        // System.out.println("当前计算("+frontier.get(i)[0]+","+frontier.get(i)[1]+")的P("+(condition==PIT?"PIT":"WUMPUS")+")为flase,(也就是不为"+(condition==PIT?"PIT":"WUMPUS")+"的概率)"+add);
                    }else {
                        //System.out.println(msg);
                    }
                }
                
                
            }//j

            System.out.println("<<<<<<<<<<<<<<<<<<<<<finally probablity>>>>>>>>>>>>>>>>>");
            total_pro_true = p*total_pro_true;//
//            System.out.println("when frontier ("+frontier.get(i)[0]+","+frontier.get(i)[1]+") is true,"+
//                                            "the total probability is "+total_pro_true);
            total_pro_false = (1-p)*total_pro_false;//这里不同
//            System.out.println("when frontier ("+frontier.get(i)[0]+","+frontier.get(i)[1]+") is false,"+
//                    "the total probability is "+total_pro_false);

            try{
                f= total_pro_true/(total_pro_true+total_pro_false);
            }catch (ArithmeticException e){
                System.out.println("ERROR: You shouldn't divide a number by zero!");
            }catch (Exception e){
                System.out.println("WARNING: Some other exception");
            }

            probability_set.get(i)[condition-1]=f; //这里保存了每次的最终概率，根据condition来区别pit和wumpus
            //pit 为1-1=0; wumpus为2-1=1; 
            System.out.println("final probability of (P("+(condition==PIT?"PIT":"WUMPUS")+")("+frontier.get(i)[0]+","+frontier.get(i)[1]+"):"+f);            
            
            if(condition==PIT&&f==1){ //发现pit               
               pit_pos.add(new int[]{frontier.get(i)[0],frontier.get(i)[1]});
                 System.out.println("添加一个新的P(pit)=1的情况");
            }

            if(condition==WUMPUS && f==1){
                found_wumpus = true;  //发现wumpus ，found_wumpus是全局变量，query_wumpus判断，得到wumpus的地址，返回true给doAction，设置wumpus_status的状态
                wumpus_pos[0] = frontier.get(i)[0];
                wumpus_pos[1] = frontier.get(i)[1];
                System.out.println("Oops! I know where is the wumpus now:)");
                return;    //就不继续查询其他的froniter了
            }

        }

        System.out.println("===============");
    }

    //check_consistent(combinations.get( ),condition,query_true)
    // combinations.get(j)是n-1个的 第一次传入的是condition.get(j) 是wumpus,query是当前查询的点(x,y,1)
    private boolean check_consistent(ArrayList<int[]> arrayList, int condition, int[] query) { 
        World cw = w.cloneWorld();  /*create a world of conjecture*/
        int size = cw.getSize();
        boolean is_consist = true;
        ArrayList<int[]> conject = cloneList(arrayList);
        conject.add(query);   //3+1=4 1循环4次(外循环) 3循环8次(内循环)
        if(condition==PIT)
        {
            for (int x = 1; x <= size; x++) {
                for (int y = 1; y <= size; y++)
                {
                    if(!cw.isUnknown(x,y)&&cw.hasPit(x,y)){    
                        cw.markSurrounding(x,y); //如果当前点可知，并且有pit-》》》》》标记周围setmarked标记pit
                    }
                }
            }
        }
        
        //这个不是实际的
        for (int i = 0; i < conject.size(); i++) {
            int cx, cy;
            cx = conject.get(i)[0];
            cy = conject.get(i)[1];
            //就假设这个点为pit,或为wumps
            if (conject.get(i)[2] == 1)
            {
                cw.markSurrounding(cx,cy);  //SetMark() 标记的是他的一周 啊   
            }
        }
        
        
        for (int x = 1; x <= size; x++) {
            for (int y = 1; y <= size; y++) {
                if (!(cw.isUnknown(x, y))) {
                        if(condition==PIT) {
                            //因为上面已经标记过了，所以当有微风且未标记  或者 无微风且标记了  那么不符合事实,就不计算total_pro_true和total_pro_false了
                            //其实这里就是考虑了那种情况，就是因为protion remove掉了当前的那一个，因为后面判断是判断的pit<4,如果pit=3，当前的设置的为query_true也为pit,那就超过了总体为3的设定了，
                            //所以这里根据实际的当前情况否决了那种实际有微风却未标记，或者标记了，却实际无微风的情况，尽管也有可能你是正确的，但我们就是根据实际情况来的吗。
                            
                            if (!(cw.hasBreeze(x, y) == cw.hasMarked(x, y))) {//有微风有标记或者无微风无标记的话就不包含在内
                                is_consist = false;
                            }
                        }
                        else if(condition==WUMPUS) {
                            if (!(cw.hasStench(x, y) == cw.hasMarked(x, y))) {
                                is_consist = false;
                            }
                        }
                }
                if (!is_consist) {
                    return is_consist;   /*once the combination is confirmed as inconsistent, break out of for loop*/
                }
            }
        }
        return is_consist;
    }

    private ArrayList<int[]> cloneList(ArrayList<int[]> arrayList){
        ArrayList<int[]> clone = new ArrayList<int[]>(arrayList.size());
        for (int i=0;i<arrayList.size(); i++)
        {
            clone.add(arrayList.get(i).clone());

        }
        return  clone;
    }

    //                         (x,y,0)
    //int[] count = combination(portion,combinations);
    private int[] combination(ArrayList<int[]> elements, ArrayList<ArrayList<int[]>> result){
          /*elements item format :  int[] { pos_x, pos_y, status }
                          status:  1 = true | 0 = false */
        int n= elements.size();        
        int nbit = 1 << n;    /* bit =2^n, indicates the number of different combinations*/
        //System.out.println("我是传入combination函数的参数portion.size为"+elements.size()+"那么nbit为"+nbit);
        int[] count = new int[nbit];
        for(int i=0; i<nbit; i++)
        {
            int c = 0;   /*count of positions whose statuses are true*/
            ArrayList<int[]> comb = cloneList(elements);
            for(int j=0; j<n; j++)
            {
                int tmp = 1<<j;       //
                if((tmp & i)!= 0){     //得到所有的排列组合
                    comb.get(j)[2]=1; /*set the status in comb[j] as true*/ //这里设置[2]为1 就是对应combinations
                    c++;           
                }
            }
            result.add(comb);//这里result的长度和nbit保持一致，往回传值combinations
            count[i] = c;   //计算状态位置可能性的数量
        }
        
        return  count;
    }

    //   if_shoot = nb.get_goal(goal,wumpus_status)
    //获取暂时性的goal，返回能否射箭if_shoot
    public boolean get_goal(int[] position,int wumpus_status){
        updatePit_Pro();
        /*update PIT_PROB*/
        //未发现的pit除以未发现的方格，就是更新P(pit)
        /*update PIT_PROB*/
//        for (int x = 1; x <= w.getSize(); x++) {
//            for (int y = 1; y <= w.getSize(); y++)
//            {
//                if(!w.isUnknown(x,y)&&w.hasPit(x,y)){ //如果当前可知且是pit 那么pit_count+1;  这个用来在下面更新P(pit)
//                    pit_c += 1;
//                }
//            }
//        }
//        double d_knowns = w.getKnowns();
//        double d_pits = pit_c;  
//        PIT_PROB = (3-d_pits)/(16-d_knowns);
        //未发现的pit除以未发现的方格，就是更新P(pit)
        
        int index;
        boolean shoot=false; //true为射了箭的意思
        boolean is_safe=false; 
        double OFFSET = 0.1; //决策参考参数
        double RISK = 0.25;
        double initial_pitpro=0.2;
        double pit_upper = 0.2; //原始的p(pit)
        get_probability(PIT);//-》》》》》》》》》》》
        if(wumpus_status==MyAgent.NOT_FOUND){ //未发现wumpus的情况下，你肯定要走Pw最小的地方，但是相对地也要走Pp不是很大的地方吧，至少不能走那些肯定Pp=1的情况吧。I dontknow
            double min_wumpus=1; //也就是概率为100%
            double min_pit=1;
            int n = -1;
            while(n<0)
            {
                for(int i=0; i<probability_set.size(); i++){                        
                    double pw = probability_set.get(i)[1]; //probability wumpus
                    double pp = probability_set.get(i)[0]; //probability  pit              
                    if(pw<=min_wumpus && pp<pit_upper){        //pw,pp  找到最小的pw,赋值给min_wumpus,  
                        //理论上我们要找的都是最小的P(pit)和P(wumpus) 因为这样才是最安全的， 但是当出现P=1的时候，那不就是100%了吗,pit还好，P(wumpus)=100%;就可以射箭来杀死他了啊                        
                        //所以P(wumpus)=1的时候要判断。
                         //计算 当前到第i个froniter近还是第n个frontier近  ，如果距离第n个froniter近，则返回true;
                        if (pw == min_wumpus && n>=0&& is_farther(frontier.get(i),frontier.get(n)) ) { //
                            continue;     //当前i点为Pwumps最小的点 ，并且当前点距离第i个比第n个远的话，也不选他作为最终点
                        }
                        min_wumpus=pw;
                        n=i;  
                    }
                     else{}
                }//for                   
                pit_upper += OFFSET;
            }//while
            index = n;
            if(probability_set.get(index)[1]>RISK && w.hasArrow()){ //RISK=0.25初始Pwumpus是0.0667
                System.out.println("Anyway, I will shoot though not sure! ");   //应对情况4的，
                shoot = true; //可以射箭
            }
//               int set=-1;
//             for(int i=0; i<probability_set.size(); i++){                        
//                    double pw = probability_set.get(i)[1]; //probability wumpus
//                    double pp = probability_set.get(i)[0]; //probability  pit
//                         if(pp<=min_pit){                         
//                            min_pit=pp;
//                            set+=1;                             
//                             if(set>=1){
//                                    for(int j=0; j<probability_set.size(); j++){   
//                                        pw = probability_set.get(j)[1];
//                                         if(pw<=min_wumpus){                         
//                                             min_wumpus=pp;
//                                             n=j;
//                                         }
//                                    }
//                             }else{
//                                 n=i;
//                             }
//                         }
//
//                         
//                      }
//                      
//                   
//              index =n;
            
                                                                                                            
        }//if 未发现wumpus的情况结束                        
        else{ //发现wumpus    就知道wumpus的地址position了，那就不用管P(wumpus)
            double min_pit=1;
            int n=-1;
            while(!is_safe)  //发现了wumpus 但是还没有杀死 ，还是很危险
            {
                for(int i=0; i<probability_set.size(); i++){                    
                    double p = probability_set.get(i)[0];  //P(pit)
                    //将最小的P(pit) p 赋值给min_pit
                    if(p<=min_pit) {         //计算 当前到第i个froniter近还是第n个frontier近  ，如果距离第n个froniter近，则返回true;
                        if (p == min_pit && n>=0 && is_farther(frontier.get(i),frontier.get(n))) {
                            continue;
                        }
                        min_pit=p;
                        n=i;
                    }
                }

                if(wumpus_status==MyAgent.DEAD)
                    is_safe=true; //wumpus死了，安全了。现在怎么走都安全，即使调入pit洞 里，都是安全的，因为可以爬上来啊
                else{  //wumpus 没死                    
                    if(wumpus_pos[0]==frontier.get(n)[0] && wumpus_pos[1]==frontier.get(n)[1]){
                    // 如果wumpus的地址与pit最小的地址相同,就是把wunmpus设为goal了
                        System.out.println("there is a wumpus in my goal!!");
                        if(w.hasArrow()){
                            shoot = true;  //可以射箭的意思
                            is_safe = true; 
                            System.out.println("but I have a arrow :)");
                        }
                        else{//没有箭的情况
                            if(probability_set.size()>1){
                                probability_set.remove(probability_set.get(n));
                                min_pit=1;
                                System.out.println("so I quit ~");
                            }
                            else{   //也就是没有箭并且也只有一个位置
                                is_safe = true;    
                                System.out.println("只剩最后一个未知格子，同时存在金块和wumpus，箭已经使用，必输局");
                            }
                        }
                    }
                    else //ww发现了wumpus,wumpuss没死，并且也不是goal.
                        is_safe=true;
                }
            }//while
            index = n;// 如果未发现wumpus 出现第一个有效的地址就可以为goal
                     //如果发现wumpus,将最小P(pit)  设为goal
        }//else  这是发现wumpus的情况结束

        position[0] = frontier.get(index)[0];
        position[1] = frontier.get(index)[1];    //接下来要往这个点去移动

        return shoot;
    }



    public boolean query_wumpus(int[] position){
//        /*update WUMPUS_PROB*/
//        double d_knowns = w.getKnowns();
//        WUMPUS_PROB = 1/(16-d_knowns);   //更新P(wumpus)的值，
        updateWumpus_Pro();
        get_probability(WUMPUS);   //传入的是wumpus

        if(found_wumpus){
            position[0]=wumpus_pos[0];
            position[1]=wumpus_pos[1];
        } //如果发现wumpus,得到wumpus的地址
        return found_wumpus;
    }


    public void set_wumpus_pos(int[] position){
        wumpus_pos[0]=position[0];
        wumpus_pos[1]=position[1];
    }

    
//    is_farther(frontier.get(i),frontier.get(n))
    public boolean is_farther(int[] goalA, int[] goalB){
//计算 当前到第i个froniter近还是第n个frontier近  ，如果距离第n个froniter近，则返回true;
        int x = w.getPlayerX();
        int y = w.getPlayerY();
        int distanceA = Math.abs(x-goalA[0])+Math.abs(y-goalA[1]);
        int distanceB = Math.abs(x-goalB[0])+Math.abs(y-goalB[1]);

        if(distanceA>distanceB) return true;
        else return  false;

    }
    public boolean checkalreadypit(ArrayList<int[]> pit_pos,int x,int y,int condition){  
        for(int i=0;i<pit_pos.size();i++){   
            System.out.println("当前比对("+pit_pos.get(i)[0]+","+pit_pos.get(i)[1]+")与（"+x+","+y+")");
            if(pit_pos.get(i)[0]==x&&pit_pos.get(i)[1]==y&&condition==PIT){
                System.out.println("我已经算过这个点("+x+","+y+")的P(pit)为1了");
                return true;
            }
        }
        return false;
    }
    public void updatePit_Pro(){
        for (int x = 1; x <= w.getSize(); x++) {
            for (int y = 1; y <= w.getSize(); y++)
            {
                if(!w.isUnknown(x,y)&&w.hasPit(x,y)){ //如果当前可知且是pit 那么pit_count+1;  这个用来在下面更新P(pit)
                    pit_c += 1;
                }
            }
        }                
        PIT_PROB = (3-(double)pit_c)/(16-(double)w.getKnowns());
        
    }

    
    public void updateWumpus_Pro(){                        
        WUMPUS_PROB = 1/(16-(double)w.getKnowns());   //更新P(wumpus)的值，
    }

   public  void clearpitpro() {
       
        pit_pos.clear();
        
    }
    
    
}


//

//令froniter (边缘 ） 表 示与已 访问过 的方格 相邻的 、除査询变 量以外 的陷阱 （pit） 变量
