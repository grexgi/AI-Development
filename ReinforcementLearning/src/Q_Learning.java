import java.text.DecimalFormat;
import java.util.Random;

/**
 * @author Kunuk Nykjaer
 */
public class Q_Learning {
    public static void main(String[] args) {
        long BEGIN = System.currentTimeMillis();

        Q_Learning obj = new Q_Learning();

        obj.run();
        obj.printResult();
        obj.showPolicy();

        long END = System.currentTimeMillis();
        System.out.println("Time: " + (END - BEGIN) / 1000.0 + " sec.");
    }

    final DecimalFormat df = new DecimalFormat("#.##");

    // path finding
    final double alpha = 0.1;
    final double gamma = 0.9;

    // states A,B,C,D,E,F
    // e.g. from A we can go to B or D
    // from C we can only go to C
    // C is goal state, reward 100 when B->C or F->C
    //
    // _______
    // |A|B|C|
    // |_____|
    // |D|E|F|
    // |_____|
    // |G|H|I|
    // |_____|
    //

    final int stateA = 0;
    final int stateB = 1;
    final int stateC = 2;
    final int stateD = 3;
    final int stateE = 4;
    final int stateF = 5;
    final int stateG = 7;
    final int stateH = 7;
    final int stateI = 8;

    final int statesCount = 9;
    final int[] states = new int[] { stateA, stateB, stateC, stateD, stateE, stateF, stateG, stateH, stateI };

    // http://en.wikipedia.org/wiki/Q-learning
    // http://people.revoledu.com/kardi/tutorial/ReinforcementLearning/Q-Learning.htm

    // Q(s,a)= Q(s,a) + alpha * (R(s,a) + gamma * Max(next state, all actions) -
    // Q(s,a))

    int[][] R = new int[statesCount][statesCount]; // reward lookup
    double[][] Q = new double[statesCount][statesCount]; // Q learning

    int[] actionsFromA = new int[] { stateB, stateD };
    int[] actionsFromB = new int[] { stateA, stateC };
    int[] actionsFromC = new int[] { stateC };
    int[] actionsFromD = new int[] { stateA, stateE };
    int[] actionsFromE = new int[] { stateD, stateF, stateH };
    int[] actionsFromF = new int[] { stateC, stateE, stateI };
    int[] actionsFromG = new int[] { stateD, stateH };
    int[] actionsFromH = new int[] { stateG, stateE, stateB };
    int[] actionsFromI = new int[] { stateH };
    int[][] actions = new int[][] { actionsFromA, actionsFromB, actionsFromC,
            actionsFromD, actionsFromE, actionsFromF, actionsFromG, actionsFromF, actionsFromH };

    String[] stateNames = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I" };

    public Q_Learning() {
        init();
    }

    public void init() {
        R[stateH][stateI] = 100; // from h to i
        R[stateF][stateI] = 100; // from f to i
    }

    void run() {
        /*
         * 1. Set parameter , and environment reward matrix R
         * 2. Initialize matrix Q as zero matrix
         * 3. For each episode: Select random initial state
         * Do while not reach goal state o
         * Select one among all possible actions for the current state o
         * Using this possible action, consider to go to the next state o
         * Get maximum Q value of this next state based on all possible actions o
         * Compute o Set the next state as the current state
         */

        // For each episode
        Random rand = new Random();
        for (int i = 0; i < 1000; i++) { // train episodes
            // Select random initial state
            int state = rand.nextInt(statesCount);
            while (state != stateC) // goal state
            {
                // Select one among all possible actions for the current state
                int[] actionsFromState = actions[state];

                // Selection strategy is random in this example
                int index = rand.nextInt(actionsFromState.length);
                int action = actionsFromState[index];

                // Action outcome is set to deterministic in this example
                // Transition probability is 1
                int nextState = action; // data structure

                // Using this possible action, consider to go to the next state
                double q = Q(state, action);
                double maxQ = maxQ(nextState);
                int r = R(state, action);

                double value = q + alpha * (r + gamma * maxQ - q);
                setQ(state, action, value);

                // Set the next state as the current state
                state = nextState;
            }
        }
    }

    double maxQ(int s) {
        int[] actionsFromState = actions[s];
        double maxValue = Double.MIN_VALUE;
        for (int i = 0; i < actionsFromState.length; i++) {
            int nextState = actionsFromState[i];
            double value = Q[s][nextState];

            if (value > maxValue)
                maxValue = value;
        }
        return maxValue;
    }

    // get policy from state
    int policy(int state) {
        int[] actionsFromState = actions[state];
        double maxValue = Double.MIN_VALUE;
        int policyGotoState = state; // default goto self if not found
        for (int i = 0; i < actionsFromState.length; i++) {
            int nextState = actionsFromState[i];
            double value = Q[state][nextState];

            if (value > maxValue) {
                maxValue = value;
                policyGotoState = nextState;
            }
        }
        return policyGotoState;
    }

    double Q(int s, int a) {
        return Q[s][a];
    }

    void setQ(int s, int a, double value) {
        Q[s][a] = value;
    }

    int R(int s, int a) {
        return R[s][a];
    }

    void printResult() {
        System.out.println("Print result");
        for (int i = 0; i < Q.length; i++) {
            System.out.print("out from " + stateNames[i] + ":  ");
            for (int j = 0; j < Q[i].length; j++) {
                System.out.print(df.format(Q[i][j]) + " ");
            }
            System.out.println();
        }
    }

    // policy is maxQ(states)
    void showPolicy() {
        System.out.println("\nshowPolicy");
        for (int i = 0; i < states.length; i++) {
            int from = states[i];
            int to = policy(from);
            System.out.println("from " + stateNames[from] + " goto " + stateNames[to]);
        }
    }
}