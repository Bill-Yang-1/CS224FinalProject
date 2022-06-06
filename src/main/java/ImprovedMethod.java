import soot.*;
import soot.jimple.*;
import soot.options.Options;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.ClassicCompleteUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.Kind;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.io.*;
import java.util.*;


public class ImprovedMethod {
    static String CodeAddress = "./src";
    static String TestAddress = "./test";
    static String TestFunctionName = "main";
    static String JimplesPath = "./jimples/";


    static HashMap<String,String> ConditionVars = new HashMap<String,String>();
    static ArrayList Unreachable = new ArrayList();
    static ArrayList Unreachable_IF = new ArrayList();
    static ArrayList Unreachable_ELSE = new ArrayList();
    static ArrayList Unreachable_CASE = new ArrayList();
    static ArrayList Unreachable_SWITCH = new ArrayList();
    static ArrayList Unreachable_IFELSE = new ArrayList();
    static ArrayList<Unit> Visited = new ArrayList<Unit>();

    public static class Block{
        Unit code;
        int index;
        int LineNum;
        List<Unit> Suc;
        List<Unit> Pre;


        ArrayList SucIndexs = new ArrayList();
        ArrayList PreIndexs = new ArrayList();
        ArrayList<Value> In = new ArrayList<Value>();
        ArrayList<Value> Out = new ArrayList<Value>();
        ArrayList<Value> PreIn = new ArrayList<Value>();
    }


    public static void SetUpSoot(String className) {
        Options.v().set_prepend_classpath(true);
        System.out.println(className);
        Options.v().set_process_dir(Collections.singletonList(className));
//        Scene.v().setMainClass(mainClass);
    }

    public static Body FormJimpleBody(String className) throws IOException{
//        soot.Main.main(args);
        SootClass mainClass = Scene.v().loadClassAndSupport("./src/main/java/BetterPMD/test");
        SootMethod mainMethod = mainClass.getMethodByName("main");
        Body JimpleBody = mainMethod.retrieveActiveBody();
        return JimpleBody;
    }

    public static String GetOriginFileAtLineNum(String path, int LineNum) throws IOException{
        File source = new File(path);
        FileReader in = new FileReader(source);
        LineNumberReader reader = new LineNumberReader(in);
        String s = "";
        int line = 1;
        reader.setLineNumber(LineNum - 1);
        long i = reader.getLineNumber();
        while((s = reader.readLine()) != null){
            if(i == line){
                s = reader.readLine();
                break;
            }
            line++;
        }
        reader.close();
        in.close();
        return s;
    }

    public static void GenerateTxtFile(String path, String s, String ClassName) throws IOException{
        String p = path + ClassName + ".txt";
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(p,true)));
        out.write(s+"\r\n");
        out.close();
    }

    public static boolean isConstant(String str){
        return str.matches("^[0.0-9.0]+$");
    }

    public static boolean isOp(String str){
        return str.contains("+") || str.contains("-") || str.contains("*") || str.contains("/") || str.contains("%") || str.contains("<<")|| str.contains(">>");
    }

    public static ArrayList<String> GetOp(String str) {
        str = str.replace(" ","");
        ArrayList<String> ret = new ArrayList<String>();
        if (str.contains("<<")) {
            String[] temp = str.split("<<");
            ret.add(temp[0]);
            ret.add("<<");
            ret.add(temp[1]);
        } else if (str.contains(">>")) {
            String[] temp = str.split(">>");
            ret.add(temp[0]);
            ret.add(">>");
            ret.add(temp[1]);
        } else if (str.contains("+")) {
            String[] temp = str.split("\\+");
            ret.add(temp[0]);
            ret.add("+");
            ret.add(temp[1]);
        } else if (str.contains("-")) {
            String[] temp = str.split("-");
            ret.add(temp[0]);
            ret.add("-");
            ret.add(temp[1]);
        } else if (str.contains("*")) {
            String[] temp = str.split("\\*");
            ret.add(temp[0]);
            ret.add("*");
            ret.add(temp[1]);
        } else if (str.contains("/")) {
            String[] temp = str.split("/");
            ret.add(temp[0]);
            ret.add("/");
            ret.add(temp[1]);
        } else if (str.contains("%")) {
            String[] temp = str.split("%");
            ret.add(temp[0]);
            ret.add("%");
            ret.add(temp[1]);
        } else if (str.contains(">")) {
            String[] temp = str.split(">");
            ret.add(temp[0]);
            ret.add(">");
            ret.add(temp[1]);
        } else if (str.contains("<")) {
            String[] temp = str.split("<");
            ret.add(temp[0]);
            ret.add("<");
            ret.add(temp[1]);
        } else if (str.contains("==")) {
            String[] temp = str.split("==");
            ret.add(temp[0]);
            ret.add("==");
            ret.add(temp[1]);
        } else if (str.contains(">=")) {
            String[] temp = str.split(">=");
            ret.add(temp[0]);
            ret.add(">=");
            ret.add(temp[1]);
        } else if (str.contains("<=")) {
            String[] temp = str.split("<=");
            ret.add(temp[0]);
            ret.add("<=");
            ret.add(temp[1]);
        } else if (str.contains("!=")) {
            String[] temp = str.split("!=");
            ret.add(temp[0]);
            ret.add("!=");
            ret.add(temp[1]);
        }

        return ret;
    }

    public static boolean LogicCompare(String a, String op, String b){
        if(op == ">"){
            return Integer.valueOf(a).intValue() > Integer.valueOf(b).intValue();
        }
        if(op == "<"){
            return Integer.valueOf(a).intValue() < Integer.valueOf(b).intValue();
        }
        if(op == "=="){
            return Integer.valueOf(a).intValue() == Integer.valueOf(b).intValue();
        }
        if(op == ">="){
            return Integer.valueOf(a).intValue() >= Integer.valueOf(b).intValue();
        }
        if(op == "<="){
            return Integer.valueOf(a).intValue() <= Integer.valueOf(b).intValue();
        }
        if(op == "!="){
            return Integer.valueOf(a).intValue() != Integer.valueOf(b).intValue();
        }
        return true;
    }

    public static ArrayList<Integer> CoreFunc(String name, String Path, String filePath) throws IOException, ScriptException {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        String classesDirTest = Path;
        String jceDir = System.getProperty("java.home") + "/lib/jce.jar";
        String jrtDir = System.getProperty("java.home") + "/lib/rt.jar";
        String path = jrtDir + File.pathSeparator + jceDir;
        path += File.pathSeparator + classesDirTest;

        Scene.v().setSootClassPath(path);

        Options.v().set_process_dir(Collections.singletonList(classesDirTest));
        Options.v().set_prepend_classpath(true);
        SootClass sootClass;
        //sootClass = Scene.v().getSootClass(mainClassName);
        sootClass = Scene.v().loadClassAndSupport(name);
        SootMethod sm = sootClass.getMethodByName("main");
        //SootMethod sm = sootClass.getMethod()
        JimpleBody JimpleBody = (JimpleBody) sm.retrieveActiveBody();

        int BlockNum = 0;
        UnitGraph cfg = new BriefUnitGraph(JimpleBody);
        int cfgSize = cfg.size();
        System.out.println("cfgsize: " + cfgSize);

        Block[] blocks = new Block[cfgSize];

        Iterator<Unit> it = cfg.iterator();
        while(it.hasNext()){
            Unit tempUnit = it.next();
            if(Visited.contains(tempUnit)){
                continue;
            }
            Visited.add(tempUnit);
            blocks[BlockNum] = new Block();
            int LineNum = tempUnit.getJavaSourceStartLineNumber();
            blocks[BlockNum].code = tempUnit;
            blocks[BlockNum].index = BlockNum;
            blocks[BlockNum].LineNum = LineNum;
            blocks[BlockNum].Suc = cfg.getSuccsOf(tempUnit);
            blocks[BlockNum].Pre = cfg.getPredsOf(tempUnit);
//            System.out.println("Jimple: " + tempUnit);
//            System.out.println("Line: " + LineNum);
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine se = manager.getEngineByName("js");

            if(tempUnit instanceof AssignStmt){
                Value left = ((AssignStmt) tempUnit).getLeftOp();
                Value right = ((AssignStmt) tempUnit).getRightOp();
                String left_str = left.toString();
                String right_str = right.toString();
                System.out.println(tempUnit);
                System.out.println(ConditionVars);
                // s: x = y op z
                if(isOp(right_str)){
                    String y = GetOp(right_str).get(0);
                    String op = GetOp(right_str).get(1);
                    String z = GetOp(right_str).get(2);
                    System.out.println(y+op+z);
                    if(!isConstant(y)){
                        y = ConditionVars.get(y);
                    }
                    if(!isConstant(z)){
                        z = ConditionVars.get(z);
                    }
                    String s = y + op + z;

                    int result = (int) se.eval(s);
                    if(ConditionVars.containsKey(left_str)){
                        ConditionVars.remove(left_str);
                    }
                    ConditionVars.put(left_str,Integer.toString(result));
                } else if (isConstant(right_str)) { // s: x = c
                    if(ConditionVars.containsKey(left_str)){
                        ConditionVars.remove(left_str);
                    }
                    ConditionVars.put(left_str,right_str);
                }
                else{ //s: x = y
                    String new_right = ConditionVars.get(right_str);
                    if(ConditionVars.containsKey(left_str)){
                        ConditionVars.remove(left_str);
                    }
                    ConditionVars.put(left_str,new_right);
                }
            }

            if(tempUnit instanceof IfStmt){
                boolean EnterElse = true;
                Value IfCondition = ((IfStmt) tempUnit).getCondition();
                String IF = IfCondition.toString();
                String a = GetOp(IF).get(0);
                String op = GetOp(IF).get(1);
                String b = GetOp(IF).get(2);
                if(!isConstant(a)){
                    a = ConditionVars.get(a);
                }
                if(!isConstant(b)){
                    b = ConditionVars.get(b);
                }
                boolean re = LogicCompare(a,op,b);
//                System.out.println(re);
                EnterElse = !re && EnterElse;
                // need to modify
                if(!re){
                    if(!Unreachable_IF.contains(LineNum)){
                        Unreachable_IF.add(LineNum);
                    }
                    if(!Unreachable.contains(LineNum)){
                        Unreachable.add(LineNum);
                    }
                }
                boolean flag = true;
                while (flag){
                    System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII");
                    System.out.println("Layer1 size: " + cfg.getSuccsOf(tempUnit).size());
                    Unit u1 = cfg.getSuccsOf(tempUnit).get(0);
                    System.out.println("Layer1 first: " + u1);

                    System.out.println("Layer2 size: " + cfg.getSuccsOf(u1).size());
                    Unit u2 = cfg.getSuccsOf(u1).get(0);
                    System.out.println("Layer2 first: " + u2);

                    System.out.println("Layer3 size: " + cfg.getSuccsOf(u2).size());
                    Unit u3 = cfg.getSuccsOf(u2).get(0);
                    System.out.println("Layer3 first: " + u3);
                    System.out.println("Layer3 line: " + u3.getJavaSourceStartLineNumber());
                    if(u3 instanceof IfStmt){ //else if part
                        // add u3 into block
                        BlockNum++;
                        blocks[BlockNum] = new Block();
                        int LineNum_ = u3.getJavaSourceStartLineNumber();
                        blocks[BlockNum].code = u3;
                        blocks[BlockNum].index = BlockNum;
                        blocks[BlockNum].LineNum = LineNum_;
                        blocks[BlockNum].Suc = cfg.getSuccsOf(u3);
                        blocks[BlockNum].Pre = cfg.getPredsOf(u3);
                        Visited.add(u3);

//                        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                        Value IfCondition_ = ((IfStmt) u3).getCondition();
                        String IF_ = IfCondition_.toString();
                        String a_ = GetOp(IF_).get(0);
                        String op_ = GetOp(IF_).get(1);
                        String b_ = GetOp(IF_).get(2);
                        if(!isConstant(a_)){
                            a_ = ConditionVars.get(a_);
                        }
                        if(!isConstant(b_)){
                            b_ = ConditionVars.get(b_);
                        }
                        boolean re_ = LogicCompare(a_,op_,b_);
//                        System.out.println(re_);
                        if(!re_){
                            if(!Unreachable_IF.contains(LineNum_)){
                                Unreachable_IF.add(LineNum_);
                            }
                            if(!Unreachable.contains(LineNum_)){
                                Unreachable.add(LineNum_);
                            }
                        }
                        EnterElse = !re_ && EnterElse;
                    }
                    else { // else part
                        flag = false;
                        if(!EnterElse){
                            if(!Unreachable_ELSE.contains(u3.getJavaSourceStartLineNumber())){
                                Unreachable_ELSE.add(u3.getJavaSourceStartLineNumber());
                            }
                            if(!Unreachable.contains(u3.getJavaSourceStartLineNumber()-1)){
                                Unreachable.add(u3.getJavaSourceStartLineNumber()-1);
                            }

                        }
                    }
                    tempUnit = u3;
                }

            }

            if(tempUnit instanceof SwitchStmt){
//                System.out.println(tempUnit);
                Value s = ((SwitchStmt) tempUnit).getKey();
//                System.out.println("s: "+s);
//                System.out.println(ConditionVars);
                String string_s = ConditionVars.get(s.toString());

                if(tempUnit instanceof LookupSwitchStmt){
                    List v = ((LookupSwitchStmt) tempUnit).getLookupValues();
                    int switch_start = tempUnit.getJavaSourceStartLineNumber();
                    boolean defa = true;
//                    System.out.println("Switch start: " + switch_start);
                    for(int val = 0; val < v.size(); val++){
                        Unit u = ((LookupSwitchStmt) tempUnit).getTarget(val);
                        u = cfg.getSuccsOf(u).get(0);
                        int u_line = u.getJavaSourceStartLineNumber();
//                        System.out.println("u: " + u);
//                        System.out.println("u_line: " + u_line);
                        String case_value = String.valueOf(((LookupSwitchStmt) tempUnit).getLookupValue(val));
                        String case_s = string_s + "==" + case_value;
//                        System.out.println(case_s);
                        boolean res = (boolean) se.eval(case_s);
//                        System.out.println(res);
                        if(!res){
                            if(!Unreachable_CASE.contains(u_line)){
                                Unreachable_CASE.add(u_line);
                            }
                        }
                        defa = defa && !res;
                    }
                    // default part
                    Unit de = ((LookupSwitchStmt) tempUnit).getDefaultTarget();
                    de = cfg.getSuccsOf(de).get(0);
                    int de_line = de.getJavaSourceStartLineNumber();
//                    System.out.println("de_line: " + de_line);
                    if(!defa){
                        if(!Unreachable_CASE.contains(de_line)){
                            Unreachable_CASE.add(de_line);
                        }
                    }
                } else if (tempUnit instanceof TableSwitchStmt) {
                    int low_index = ((TableSwitchStmt) tempUnit).getLowIndex();
                    int high_index = ((TableSwitchStmt) tempUnit).getHighIndex();
                    boolean defa_ = true;
                    Unit de_ = ((TableSwitchStmt) tempUnit).getDefaultTarget();
                    de_ = cfg.getSuccsOf(de_).get(0);
                    int deline = de_.getJavaSourceStartLineNumber();
                    for(int val_ = 0 ; val_ < high_index - low_index + 1; val_++){
                        Unit u_ = ((TableSwitchStmt) tempUnit).getTarget(val_);
                        u_ = cfg.getSuccsOf(u_).get(0);
                        int uline = u_.getJavaSourceStartLineNumber();
//                        System.out.println(uline);
                        String check = String.valueOf(low_index + val_);
                        String s_ = string_s + "==" + check;
                        boolean res_ = (boolean) se.eval(s_);
//                        System.out.println(res_);
                        if(!res_){
                            if(!Unreachable_CASE.contains(uline) && deline!= uline){
                                Unreachable_CASE.add(uline);
                            }
                        }
                        defa_ = defa_ && !res_;
                    }
                    // default part
//                    System.out.println("deline: " + deline);
                    if(!defa_){
                        if(!Unreachable_CASE.contains(deline)){
                            Unreachable_CASE.add(deline);
                        }
                    }
                }
            }
            BlockNum++;
        }

//        System.out.println(ConditionVars);



        int i = 0;
        for(i = 0; i < BlockNum; i = i + 1) {
            for(Unit s : blocks[i].Suc){
                int j = 0;
                for(j = 0; j < BlockNum; j = j + 1){
                    if(s == blocks[j].code){
                        blocks[i].SucIndexs.add(j);
                    }
                }
            }
            for(Unit p : blocks[i].Pre){
                int k = 0;
                for(k = 0; k < BlockNum; k = k + 1){
                    if(p == blocks[k].code){
                        blocks[i].PreIndexs.add(k);
                    }
                }
            }
        }


//      live variable iteration
        boolean stop = false;
        while(!stop){
//            System.out.println("#################### iteration start ###################");
            for(int l = BlockNum-1 ; l >= 0; l--){
                // OUT[B] = U suc IN[S]
                for(int m = 0; m < blocks[l].SucIndexs.size(); m++) {
                    int CurBlockIndex = (int)blocks[l].SucIndexs.get(m);
                    for(int n = 0; n < blocks[CurBlockIndex].In.size(); n++){
                        Value CurValue = blocks[CurBlockIndex].In.get(n);
                        if(!blocks[l].Out.contains(CurValue)){
                            blocks[l].Out.add(CurValue);
                        }
                    }
                }
//                System.out.println("Block " + l + " OUT:" + blocks[l].Out);
                // IN[B] = OUT[B] - def
                blocks[l].In = (ArrayList<Value>) blocks[l].Out.clone();
                List<ValueBox> defBoxes = blocks[l].code.getDefBoxes();
                for(ValueBox vb : defBoxes) {
                    Value v = vb.getValue();
                    if(v instanceof Local){
                        Local var = (Local) v;
                        if(blocks[l].In.contains((Value)var)){
                            blocks[l].In.remove((Value)var);
                        }
                    }
                }

                // IN[B] = use U (OUT[B] - def)
                List<ValueBox> useBoxes = blocks[l].code.getUseBoxes();
                for(ValueBox vb : useBoxes) {
                    Value v = vb.getValue();
                    if(v instanceof Local){
                        Local var = (Local) v;
                        if(!blocks[l].In.contains((Value)var)){
                            blocks[l].In.add((Value)var);
                        }
                    }
                }
//                System.out.println("Block " + l + " IN:" + blocks[l].In);
            }

            // determine whether to stop
            stop = true;
            for(int s = BlockNum-1 ; s >= 0; s--){
                if(!(blocks[s].In.containsAll(blocks[s].PreIn) && blocks[s].PreIn.containsAll(blocks[s].In))){
                    stop = false;
                    blocks[s].PreIn = (ArrayList<Value>) blocks[s].In.clone();
                }
            }
//            System.out.println("#################### iteration end ###################");
        }

//
//        for(int check = 0; check < BlockNum; check++){
//            System.out.println("----------------");
//            System.out.println("Block " + check + " Code: " + blocks[check].code.toString());
//            System.out.println("Block " + check + " OUT:" + blocks[check].Out);
//            System.out.println("Block " + check + " LINE:" + blocks[check].LineNum);
//        }

        int start = blocks[0].LineNum;
        int end = start;
        for(int i_ = 0; i_ < BlockNum; i_++){
            int c_ = blocks[i_].LineNum;
            if(start > c_){
                start = c_;
            }
            if(end < c_){
                end = c_;
            }
        }

        while(!Unreachable.isEmpty()){
            int cur_line = (int) Unreachable.get(0);
            Unreachable.remove(0);
            int need_right = 1;
            for(int origin_line = cur_line + 1; origin_line < end; origin_line++){
                String L = GetOriginFileAtLineNum(filePath, origin_line);
                boolean stop_ = false;
                for(int j = 0; j < L.length(); j++){
                    if(L.charAt(j) == "{".charAt(0)){
                        need_right++;
                    } else if (L.charAt(j) == "}".charAt(0)) {
                        need_right--;
                        if(need_right == 0){
                            stop_ = true;
                            break;
                        }
                    }
                }
                if(stop_){
                    break;
                }
                Unreachable_IFELSE.add(origin_line);
            }
        }



        System.out.println("Unreachable_IF: " + Unreachable_IF);
        System.out.println("Unreachable_ELSE: " + Unreachable_ELSE);
        System.out.println("Unreachable_CASE: " + Unreachable_CASE);
        System.out.println("Unreachable_IFELSE: " + Unreachable_IFELSE);
        System.out.println("Unreachable: " + Unreachable);

        // write into txt
        System.out.println("---------------------Final Results-----------------------");

        System.out.println("start: " + start);
        System.out.println("end: " + end);
        int a = Unreachable_IF.size();
        int s = Unreachable_ELSE.size();
        int d = Unreachable_CASE.size();
        int f = Unreachable_IFELSE.size();
        int g = Unreachable.size();


        for(int q = 0; q < a;q++){
            ret.add((Integer) Unreachable_IF.remove(0));
        }
        for(int w = 0; w < s;w++){
            ret.add((Integer) Unreachable_ELSE.remove(0));
        }
        for(int e = 0; e < d;e++){
            ret.add((Integer) Unreachable_CASE.remove(0));
        }
        for(int r = 0; r < f;r++){
            ret.add((Integer) Unreachable_IFELSE.remove(0));
        }
        for(int t = 0; t < g;t++){
            ret.add((Integer) Unreachable.remove(0));
        }

        System.out.println("ret=================" + ret);
        return ret;

    }


    public static void main(String[] args) throws IOException, ScriptException {


    }

}
