package com.example.gp.a2allakfeendemo.Route_Calculation;

import com.example.gp.a2allakfeendemo.GraphConstr.GraphConstruction;
import com.example.gp.a2allakfeendemo.GraphConstr.GraphLink;
import com.example.gp.a2allakfeendemo.GraphConstr.GraphNode;
import com.example.gp.a2allakfeendemo.GraphConstr.Line;

import java.util.ArrayList;

/**
 * Created by Eman on 01/07/2017.
 */

public class Get_Routes {
    public ArrayList<ArrayList<Node>> All_Paths = new ArrayList<>();
    public ArrayList<Integer> NoOfTransitions = new ArrayList<>();
    public ArrayList<Integer> PathIndex = new ArrayList<>();
    public void Get_Routes (ArrayList<ArrayList<GraphNode>> Paths){
        TraversePaths(Paths);
    }
    public void TraversePaths (ArrayList<ArrayList<GraphNode>> Paths){
        if(Paths == null)
            return;
        for (int i = 0 ; i < Paths.size(); i++){
            GetLinks (Paths.get(i),i);
        }
    }
    public void GetLinks (ArrayList<GraphNode> Path, int ID){
        ArrayList<ArrayList<Line>> PathLines = new ArrayList<>();
        GraphLink TmpLink;
        for (int i = 0 ; i < Path.size()-1; i++){
            GraphConstruction GC = new GraphConstruction();
            TmpLink = GC.GetLink(Path.get(i),Path.get(i+1));
            PathLines.add(TmpLink.Lines);
        }
        Tree tree = ConstructTree(PathLines);
        ArrayList<Node> path = new ArrayList<>();
        for (int i = 0 ; i < tree.nodes.size(); i++){
            DFS(tree.nodes.get(i),path,0);
        }
        ArrayList<ArrayList<Node>> Final_All_Paths = new ArrayList<>();
        ArrayList<Integer> Final_Transitions = new ArrayList<>();
        for(int i=0; i<All_Paths.size(); i++)
        {
            if(NoOfTransitions.get(i) <= 4) {
                Final_Transitions.add(NoOfTransitions.get(i));
                Final_All_Paths.add(All_Paths.get(i));
            }
        }
        All_Paths.clear();
        NoOfTransitions.clear();
        for(int i=0; i<Final_All_Paths.size(); i++)
        {
            if (!LineIsAlreadyThere(Final_All_Paths.get(i)))
            {
                All_Paths.add(Final_All_Paths.get(i));
                NoOfTransitions.add(Final_Transitions.get(i));
                if(i >= PathIndex.size())
                    PathIndex.add(ID);
            }
        }
    }
    public Node ConstructNode(Line ln, ArrayList<ArrayList<Line>> PathLines, int index)
    {
        Node newNode = new Node();
        newNode.line = ln;
        newNode.NextLines = new ArrayList<>();
        if(index < PathLines.size())
        {
            for(int i=0; i<PathLines.get(index).size(); i++)
            {
                int nextIndex = index + 1;
                newNode.NextLines.add(ConstructNode(PathLines.get(index).get(i),PathLines,nextIndex));
            }
        }
        return newNode;
    }

    public Tree ConstructTree(ArrayList<ArrayList<Line>> PathLines){
        Tree tree = new Tree();
        tree.nodes = new ArrayList<>();
        for(int j=0; j<PathLines.get(0).size(); j++)
        {
            tree.nodes.add(ConstructNode(PathLines.get(0).get(j),PathLines,1));
        }
        return tree;
    }

    boolean LineIsAlreadyThere(ArrayList<Node> Arr)
    {
        boolean MetroFound = false;
        boolean TransitionFound = false;
        ArrayList<String> BusLines = new ArrayList<>();
        for(int i=0; i<Arr.size(); i++)
        {
            if (Arr.get(i).line.type == 1)
                MetroFound = true;
            if (MetroFound && (Arr.get(i).line.type == 2 || Arr.get(i).line.type == 0))
                TransitionFound = true;
            if (TransitionFound && Arr.get(i).line.type == 1)
                return true;
            if (Arr.get(i).line.type == 2 && !BusLines.contains(Arr.get(i).line.line))
                BusLines.add(Arr.get(i).line.line);
        }

        boolean BusFound;
        for (int i = 0 ; i < BusLines.size(); i++){
            TransitionFound = false;
            BusFound = false;
            for (int j = 0 ; j < Arr.size(); j++) {
                if (Arr.get(j).line.line.equals(BusLines.get(i)) && Arr.get(j).line.type == 2 && TransitionFound)
                    return true;
                if (Arr.get(j).line.line.equals(BusLines.get(i)) && Arr.get(j).line.type == 2) {
                    BusFound = true;
                }
                else {
                    if (BusFound)
                        TransitionFound = true;
                }
            }
        }
        return false;
    }

    public void DFS(Node n, ArrayList<Node> CurrPath, int NumOfTransitions)
    {
//        if(NumOfTransitions > 4)
//        {
//            CurrPath.clear();
//            return;
//        }
//        if(LineIsAlreadyThere(CurrPath,n,NumOfTransitions))
//        {
//            CurrPath.clear();
//            return;
//        }
        CurrPath.add(n);
        if(n.NextLines != null)
        {
            for(int i=0; i<n.NextLines.size(); i++)
            {
                int nextNumOfTrans = NumOfTransitions;
                if((!n.NextLines.get(i).line.line.equals(n.line.line) && n.line.type == 2 && n.NextLines.get(i).line.type == 2) || (n.NextLines.get(i).line.type != n.line.type))
                {
                    //save number of transitions
                    nextNumOfTrans++;
                }
                DFS(n.NextLines.get(i),CurrPath,nextNumOfTrans);
            }
            if(n.NextLines.size() == 0) {
                ArrayList<Node> new_Path = (ArrayList<Node>) CurrPath.clone();
                All_Paths.add(new_Path);
                NoOfTransitions.add(NumOfTransitions);
            }
            CurrPath.remove(CurrPath.size()-1);
        }
    }
}
