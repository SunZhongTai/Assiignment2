## Wumpus World

The overall steps are divided into two steps. The first step is to find the next grid to arrive, then the second step is how to reach this grid. To know where to go next, first, calculate the probability of pits and wumpus for all grids you might reach next. It's calculated by Naive_bayes. Then select the best grid based on the environment at the time. After getting the goal, move to that grid.

### Naive Bayes

The Naive concept: the assumption of independence, assuming that the features are independent and irrelevant. Given the Known, frontier, and query variables, the observed phenomena are conditionally independent of other variables. This is mainly implemented in the **get_probability()** .

![Krw8CF.gif](https://s2.ax1x.com/2019/10/27/Krw8CF.gif)



**P(A|B)** represents the pit or wumpus probability of the grid to be queried according to the current actual environmental conditions. In our logic, first we get all the frontier combinations by binary calculation, and assume that the query frontier is true or false, and then remove some assumptions that do not conform to the current actual environment. Then calculate the value of P(pit) or P(wumpus) one by one from the frontier. According to the formula, to calculate the probability that the current grid is pit, calculate **P(B|A)** and ![KrwrCD.gif](https://s2.ax1x.com/2019/10/27/KrwrCD.gif)

**P(B|A)** Indicates the sum of the probabilities of all possible combinations in the case where this grid of the current query is true.

![KrwrCD.gif](https://s2.ax1x.com/2019/10/27/KrwrCD.gif) Indicates the sum of the probabilities of all combinations that satisfy the current environment in the case where the current query grid is false.









### Get Goal

After calculating the P(pit) and P(wumpus) of each frontier, we have to choose the best one, that is, where we are going, which is mainly implemented in the **get_goal()**. The principle is to first determine the current state of wumpus, if we have not found wumpus, that is, we have not found the point of P(wumpus)=1, we will find that P(pit) is less than a default value, and P(wumpus) is as small as possible. If we do not find it once, we will increase the default value . until we find this goal, at this time when we find the goal if the P(wumpus) of the goal is bigger than a default value, then we will do action of shoot, here is to consider the  the fourth map. And when we have found wumpus, we will choose the grid with the smallest P(pit) as the goal. If wumpus is not dead at this time, and the location of wumpus is my goal, if I have an arrow, then I will shoot.

 

### Move Goal

After getting the goal, we will execute the method **move_to_goal()** .For example, if we want to turn to right, if the grid adjacent to our grid’s right is known and not pit, we can move right. If the current grid only has unknown grids and pits, then we would only take the direction of the pit. But at first , we need to find if there is  way to move. check all the grids between the goal and current position. If all way is unknown or pits,  we will change the goal. we will execute the method **change_goal()**.

 

### Change Goal

In the method **change_goal()**, we set the current goal unreachable, it mainly helps us to “remove” the current goal, then we find the new goal by Naive Bayes.
