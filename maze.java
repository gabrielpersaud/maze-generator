import java.util.*;

public class maze {

    private static final int right = 0;
    private static final int down = 1;
    private static final int left = 2;
    private static final int up = 3;
    private static Random randomGenerator;  // for random numbers
    
    public static int Size;
    public static int[] Up;
    public static int[] height;
    public static int[] edge;
    public static ArrayList<ArrayList<Integer>> S;
    public static ArrayList<int[]> E;
    public static ArrayList<Integer> traversed = new ArrayList<>();
    public static ArrayList<Integer> shortPath = new ArrayList<>();
    public static boolean endFound = false;
    
    public static class Point {  // a Point is a position in the maze

        public int x, y;
        
        // Constructor
        public Point(int x, int y) {
            this.x = x;
	       this.y = y;
        }

        public void copy(Point p) {
            this.x = p.x;
            this.y = p.y;
        }
    }
    
    public static class Edge { 
	   // an Edge links two neighboring Points: 
	   // For the grid graph, an edge can be represented by a point and a direction.
	   Point point;
	   int direction;    // one of right, down, left, up
	   boolean used;     // for maze creation
	   boolean deleted;  // for maze creation
	
	   // Constructor
	   public Edge(Point p, int d) {
           this.point = p;
	       this.direction = d;
	       this.used = false;
	       this.deleted = false;
       }
    }

    // A board is an SizexSize array whose values are Points                                                                                                           
    public static Point[][] board;
    
    // A graph is simply a set of edges: graph[i][d] is the edge 
    // where i is the index for a Point and d is the direction 
    public static Edge[][] graph;
    public static int N;   // number of points in the graph
    
    public static void displayInitBoard() {
        System.out.println("\nInitial Configuration:");

        for (int i = 0; i < Size; ++i) {
            System.out.print("    -");
            for (int j = 0; j < Size; ++j) System.out.print("----");
            System.out.println();
            if (i == 0) System.out.print("Start");
            else System.out.print("    |");
            for (int j = 0; j < Size; ++j) {
                if (i == Size-1 && j == Size-1) System.out.print("    End");
                else System.out.print("   |");
            }
            System.out.println();
        }
        System.out.print("    -");
        for (int j = 0; j < Size; ++j) System.out.print("----");
        System.out.println();
    }

    public static void union(int i, int j) { // i and j are roots
        int ri = height[i];
        int rj = height[j];
        int index_i = cellSetIndex(i);
        int index_j = cellSetIndex(j);
        if (ri<rj) {
            Up[i] = j;
            if (index_i!=index_j) S.get(index_j).addAll(S.get(index_i)); S.remove(index_i);
        }
        if (rj<ri) {
            Up[j] = i;
            if (index_i!=index_j) S.get(index_i).addAll(S.get(index_j)); S.remove(index_j);
        }
        if (ri==rj) {
            Up[i] = j;
            height[j]++;
            if (index_i!=index_j) {
                S.get(index_j).addAll(S.get(index_i));
                S.remove(index_i);
            }
        }
    }

    public static int cellSetIndex(int cell) {
        int result = -1;
        for (int i=0; i<S.size(); i++) {
            if (S.get(i).contains(cell)) {
                result = i;
                break;
            }
        }
        return result;
    }

    public static int find(int x) {
        int i = x-1;
        int r = i;
        while (Up[r] != -1) {
            r = Up[r];
        }
        if (i != r) {
            int k = Up[i];
            while (k != r) {
                Up[i] = r;
                i = k;
                k = Up[k];
            }
        }
        return r;
    }

    public static void createMaze(ArrayList<ArrayList<Integer>> S, ArrayList<int[]> E) {
        randomGenerator = new Random();
        ArrayList<int[]> used = new ArrayList<>();
        int randomCell;
        int randomDirIndex;
        int randomDir;
        int[] edge = new int[2];
        ArrayList<String> directions = new ArrayList<>();

        while (S.size()>1) {
            randomCell = randomGenerator.nextInt(N-1)+1;
            String[] dir = new String[] {"0","1","2","3"};
            directions.clear();
            directions.addAll(Arrays.asList(dir));

            if (randomCell <= Size) directions.remove("1");
            if ((randomCell-1) % Size == 0) directions.remove("0");
            if (randomCell > N-Size) directions.remove("3");
            if (randomCell % Size == 0) directions.remove("2");
            randomDirIndex = randomGenerator.nextInt(directions.size());
            randomDir = Integer.parseInt(directions.get(randomDirIndex));

            if (randomDir == 0) edge = new int[] {randomCell-1, randomCell};
            if (randomDir == 1) edge = new int[] {randomCell-Size, randomCell};
            if (randomDir == 2) edge = new int[] {randomCell, randomCell+1};
            if (randomDir == 3) edge = new int[] {randomCell, randomCell+Size};

            if (!used.contains(edge)) {
                int u = find(edge[0]);
                int v = find(edge[1]);
                if (u != v) { union(u, v); deleteEdge(edge); used.add(edge.clone()); }
                else used.add(edge.clone());
            }
        }
    }

    public static void deleteEdge(int[] edge) {
        for (int i=0; i<E.size(); i++) {
            if (Arrays.equals(E.get(i), edge)) {
                E.remove(i);
            }
        }
    }
    public static boolean includes(int[] edge) {
        for (int i=0; i<E.size(); i++) {
            if (Arrays.equals(E.get(i), edge)) {
                return true;
            }
        }
        return false;
    }

    public static boolean includesEdgeOf(int x, int y) {
        int[] edge = new int[] {x, y};
        for (int i=0; i<E.size(); i++) {
            if (Arrays.equals(E.get(i), edge)) {
                return true;
            }
        }
        return false;
    }

    public static void dfs(int cell) {
        traversed.add(cell);
        if (cell == N) endFound = true;
        int[] bottomedge = new int[] {cell, cell+Size};
        int[] rightedge = new int[] {cell, cell+1};
        int[] topedge = new int[] {cell-Size, cell};
        int[] leftedge = new int[] {cell-1, cell};
        boolean notLastRow = cell <= N-Size;
        boolean notLastCol = cell % Size !=0;
        boolean notFirstRow = cell > Size;
        boolean notFirstCol = (cell-1) % Size != 0;
        if (endFound) { shortPath.add(cell); return; }
        if ((!includes(bottomedge) && notLastRow) && (!traversed.contains(cell+Size))) dfs(cell+Size);
        if (endFound) { shortPath.add(cell); return; }
        if ((!includes(rightedge) && notLastCol) && (!traversed.contains(cell+1))) dfs(cell+1);
        if (endFound) { shortPath.add(cell); return; }
        if ((!includes(topedge) && notFirstRow) && (!traversed.contains(cell-Size))) dfs(cell-Size);
        if (endFound) { shortPath.add(cell); return; }
        if ((!includes(leftedge) && notFirstCol) && (!traversed.contains(cell-1))) dfs(cell-1);
    }

    
    public static void main(String[] args) {

        // Read in the Size of a maze
        Scanner scan = new Scanner(System.in);
        try {
            System.out.println("What's the size of your maze? ");
            Size = scan.nextInt();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        scan.close();


        // Create one dummy edge for all boundary edges.
        Edge dummy = new Edge(new Point(0, 0), 0);
        dummy.used = true;

        // Create board and graph.
        board = new Point[Size][Size];
        N = Size * Size;  // number of points
        graph = new Edge[N][4];

        for (int i = 0; i < Size; ++i) {
            for (int j = 0; j < Size; ++j) {
                Point p = new Point(i, j);
                int pindex = i * Size + j;   // Point(i, j)'s index is i*Size + j

                board[i][j] = p;

                graph[pindex][right] = (j < Size - 1) ? new Edge(p, right) : dummy;
                graph[pindex][down] = (i < Size - 1) ? new Edge(p, down) : dummy;
                graph[pindex][left] = (j > 0) ? graph[pindex - 1][right] : dummy;
                graph[pindex][up] = (i > 0) ? graph[pindex - Size][down] : dummy;
            }
        }

	    displayInitBoard();

        Up= new int[N];
        height= new int[N];
        S = new ArrayList<ArrayList<Integer>>();
        E = new ArrayList<int[]>();
        for (int i=0; i<N; i++) {
            Up[i] = -1;
            height[i] = 0;
            ArrayList<Integer> singleton = new ArrayList<Integer>();
            singleton.add(i);
            S.add(singleton);
        }

        for (int i=1; i<=N-Size; i++) { //adds edges on first n-1 rows of grid
            edge = new int[] {i, i+Size};
            E.add(edge.clone());
            if (i%Size != 0) {
                edge[1] = i+1;
                E.add(edge.clone());
            }
        }
        for (int i=(N-Size)+1; i<=N; i++) { //adds edges on bottom row of grid
            if (i%Size != 0) {
                edge = new int[] {i, i+1};
                E.add(edge.clone());
            }
        }
         
	    // Hint: To randomly pick an edge in the maze, you may 
	    // randomly pick a point first, then randomly pick
	    // a direction to get the edge associated with the point.
	    randomGenerator = new Random();
	    int i = randomGenerator.nextInt(N);
	    System.out.println("\nA random number between 0 and " + (N-1) + ": " + i);
	    createMaze(S, E);
	    dfs(1);

	    for (i=0;i<E.size();i++) {
	        System.out.print(Arrays.toString(E.get(i)));
        }
        System.out.println("\n");
        System.out.println(Arrays.toString(shortPath.toArray()));

    }
}

