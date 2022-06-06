import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import javax.script.ScriptException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sun.java2d.SurfaceDataProxy;


public class XML2CSV {
    static String PMDXMLpath = "./src/main/java/BetterPMD/results-xml/pmd-test.xml";
    static String FBXMLpath = "./src/main/java/BetterPMD/results-xml/findbugs.xml";
    static String PMDCSVpath = "./src/main/java/BetterPMD/results-csv/pmd-test.csv";
    static String FBCSVpath = "./src/main/java/BetterPMD/results-csv/findbugs.csv";

    public static void main(String[] args){
        PMDXML2CSV(PMDXMLpath, PMDCSVpath);
//        FBXML2CSV(FBXMLpath, FBCSVpath);
    }


    public static void Better_PMDXML2CSV(String XMLpath, String CSVpath, int type){
        File writeFile = new File(CSVpath);
        String t = "";
        if(type == 0)
            t = "both";
        else if (type == 1) {
            t = "data";
        } else if (type == 2) {
            t = "external";
        }
        try {
            BufferedWriter writeText = new BufferedWriter(new FileWriter(writeFile));
//            writeText.newLine();
            writeText.write("NO,FileName,Class,Rule,RuleSet,BeginLine,BeginColumn,EndLine,EndColumn,Priority");
            String NO = "";
            String Path = "";
            String FilePath = "";
            String name = "";
            String FileName = "";
            String BeginLine = "";
            String BeginColumn = "";
            String Rule = "";
            String RuleSet = "";
//            String Problem = "";
            String Class = "";
            String EndColumn = "";
            String EndLine = "";
            String Priority = "";
//            String FP = "";
            String Output = "";
            int FPT = 0;
            int FPF = 0;
            int count = 0;
            System.out.println(t);
            DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(XMLpath);
            org.w3c.dom.NodeList fileList = doc.getElementsByTagName("file");

            for (int i = 0; i < fileList.getLength(); i++) {
                System.out.println(" -------------------------" + fileList.item(i).getNodeName() + " NO." + (i+1) + " -------------------------");
                NO = "" + (i+1);
                Node fileNode = fileList.item(i);
                org.w3c.dom.NamedNodeMap namedNodeMap = fileNode.getAttributes();
                for (int j = 0; j < namedNodeMap.getLength(); j++) {
                    System.out.println(namedNodeMap.item(j).getNodeName()+" = "+namedNodeMap.item(j).getNodeValue());

                    if(namedNodeMap.item(j).getNodeName() == "name"){
                        Path = namedNodeMap.item(j).getNodeValue();
                        FilePath = namedNodeMap.item(j).getNodeValue();
                        Path = Path.substring(0,Path.lastIndexOf("\\") + 1);
                        String tmp = namedNodeMap.item(j).getNodeValue();
                        FileName = tmp.substring(tmp.lastIndexOf("\\") + 1,tmp.length());
                        name = FileName.substring(0,FileName.lastIndexOf("."));
                        System.out.println(name);
                    }
                }
//                ArrayList<Integer> ret = new ArrayList<Integer>();
//                ret = DeadCodeDetection.CoreFunc(name,Path,FilePath);
                NodeList nodeList = fileNode.getChildNodes();
                for (int k = 0; k < nodeList.getLength(); k++) {
                    Node node = nodeList.item(k);
                    count++;
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        org.w3c.dom.NamedNodeMap VioInfoMap = node.getAttributes();
                        for (int l = 0; l < VioInfoMap.getLength(); l++) {
                            System.out.println(VioInfoMap.item(l).getNodeName() + " = " + VioInfoMap.item(l).getNodeValue());
                            if(VioInfoMap.item(l).getNodeName() == "beginline") {
                                BeginLine = VioInfoMap.item(l).getNodeValue();
                            } else if (VioInfoMap.item(l).getNodeName() == "begincolumn") {
                                BeginColumn = VioInfoMap.item(l).getNodeValue();
                            } else if (VioInfoMap.item(l).getNodeName() == "rule") {
                                Rule = VioInfoMap.item(l).getNodeValue();
                            } else if (VioInfoMap.item(l).getNodeName() == "ruleset") {
                                RuleSet = VioInfoMap.item(l).getNodeValue();
                            } else if (VioInfoMap.item(l).getNodeName() == "class") {
                                Class = VioInfoMap.item(l).getNodeValue();
                            } else if (VioInfoMap.item(l).getNodeName() == "endcolumn") {
                                EndColumn = VioInfoMap.item(l).getNodeValue();
                            } else if (VioInfoMap.item(l).getNodeName() == "endline") {
                                EndLine = VioInfoMap.item(l).getNodeValue();
                            } else if (VioInfoMap.item(l).getNodeName() == "priority") {
                                Priority = VioInfoMap.item(l).getNodeValue();
                            }
                        }
//                        Problem = node.getFirstChild().getNodeValue();
                        Random r = new Random();
                        double check = r.nextDouble();

                        System.out.println("Problem："+node.getFirstChild().getNodeValue());
                        System.out.println("name: " + name);
                        System.out.println("Path" + Path);
//                        if(!ret.contains(Integer.parseInt(BeginLine))){
                        writeText.newLine();
                        Output = NO + "," + FileName + "," + Class + "," + Rule + "," + RuleSet + "," + BeginLine + "," + BeginColumn + "," + EndLine + "," + EndColumn + "," + Priority;
                        System.out.println(Output);
                        writeText.write(Output);
//                        }
                    }
                }
            }
//            writeText.newLine();
//            writeText.newLine();
//            writeText.write("ALL,FPT,FPF,FPRate");
//            writeText.newLine();
//            double FPR = FPT*1.0/(FPT + FPF);
//            writeText.write((FPT + FPF) + "," + FPT + "," + FPF + "," + FPR);
            writeText.flush();
            writeText.close();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void PMDXML2CSV(String XMLpath, String CSVpath){
        File writeFile = new File(CSVpath);
        try {
            BufferedWriter writeText = new BufferedWriter(new FileWriter(writeFile));
//            writeText.newLine();
            writeText.write("NO,FileName,Class,Rule,RuleSet,BeginLine,BeginColumn,EndLine,EndColumn,Priority");
            String NO = "";
            String Path = "";
            String FilePath = "";
            String name = "";
            String FileName = "";
            String BeginLine = "";
            String BeginColumn = "";
            String Rule = "";
            String RuleSet = "";
//            String Problem = "";
            String Class = "";
            String EndColumn = "";
            String EndLine = "";
            String Priority = "";
            String FP = "";
            String Output = "";
            int FPT = 0;
            int FPF = 0;
            int count = 0;
            DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(XMLpath);
            org.w3c.dom.NodeList fileList = doc.getElementsByTagName("file");

            for (int i = 0; i < fileList.getLength(); i++) {
                System.out.println(" -------------------------" + fileList.item(i).getNodeName() + " NO." + (i+1) + " -------------------------");
                NO = "" + (i+1);
                Node fileNode = fileList.item(i);
                org.w3c.dom.NamedNodeMap namedNodeMap = fileNode.getAttributes();
                for (int j = 0; j < namedNodeMap.getLength(); j++) {
                    System.out.println(namedNodeMap.item(j).getNodeName()+" = "+namedNodeMap.item(j).getNodeValue());

                    if(namedNodeMap.item(j).getNodeName() == "name"){
                        Path = namedNodeMap.item(j).getNodeValue();
                        FilePath = namedNodeMap.item(j).getNodeValue();
                        Path = Path.substring(0,Path.lastIndexOf("\\") + 1);
                        String tmp = namedNodeMap.item(j).getNodeValue();
                        FileName = tmp.substring(tmp.lastIndexOf("\\") + 1,tmp.length());
                        name = FileName.substring(0,FileName.lastIndexOf("."));
                        System.out.println(name);
                    }
                }
//                ArrayList<Integer> ret = new ArrayList<Integer>();
//                ret = DeadCodeDetection.CoreFunc(name,Path,FilePath);
                NodeList nodeList = fileNode.getChildNodes();
                for (int k = 0; k < nodeList.getLength(); k++) {
                    Node node = nodeList.item(k);
                    count++;
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        org.w3c.dom.NamedNodeMap VioInfoMap = node.getAttributes();
                        for (int l = 0; l < VioInfoMap.getLength(); l++) {
                            System.out.println(VioInfoMap.item(l).getNodeName() + " = " + VioInfoMap.item(l).getNodeValue());
                            if(VioInfoMap.item(l).getNodeName() == "beginline") {
                                BeginLine = VioInfoMap.item(l).getNodeValue();
                            } else if (VioInfoMap.item(l).getNodeName() == "begincolumn") {
                                BeginColumn = VioInfoMap.item(l).getNodeValue();
                            } else if (VioInfoMap.item(l).getNodeName() == "rule") {
                                Rule = VioInfoMap.item(l).getNodeValue();
                            } else if (VioInfoMap.item(l).getNodeName() == "ruleset") {
                                RuleSet = VioInfoMap.item(l).getNodeValue();
                            } else if (VioInfoMap.item(l).getNodeName() == "class") {
                                Class = VioInfoMap.item(l).getNodeValue();
                            } else if (VioInfoMap.item(l).getNodeName() == "endcolumn") {
                                EndColumn = VioInfoMap.item(l).getNodeValue();
                            } else if (VioInfoMap.item(l).getNodeName() == "endline") {
                                EndLine = VioInfoMap.item(l).getNodeValue();
                            } else if (VioInfoMap.item(l).getNodeName() == "priority") {
                                Priority = VioInfoMap.item(l).getNodeValue();
                            }
                        }
//                        Problem = node.getFirstChild().getNodeValue();
                        Random r = new Random();
                        double check = r.nextDouble();
                        if(check <= 0.37) {FP = "True";}
                        else {FP = "False";};
                        System.out.println("Problem："+node.getFirstChild().getNodeValue());
                        System.out.println("name: " + name);
                        System.out.println("Path" + Path);

                        writeText.newLine();
                        Output = NO + "," + FileName + "," + Class + "," + Rule + "," + RuleSet + "," + BeginLine + "," + BeginColumn + "," + EndLine + "," + EndColumn + "," + Priority;
                        System.out.println(Output);
                        writeText.write(Output);

                    }
                }
            }
//            writeText.newLine();
//            writeText.newLine();
//            writeText.write("ALL,FPT,FPF,FPRate");
//            writeText.newLine();
//            double FPR = FPT*1.0/(FPT + FPF);
//            writeText.write((FPT + FPF) + "," + FPT + "," + FPF + "," + FPR);
            writeText.flush();
            writeText.close();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void FBXML2CSV(String XMLpath, String CSVpath){
        File writeFile = new File(CSVpath);

        try {
            BufferedWriter writeText = new BufferedWriter(new FileWriter(writeFile));
//            writeText.newLine();
            writeText.write("NO,FileName,Class,Type,Category,BeginLine,EndLine,Priority");
            String NO = "";
            String FileName = "";
            String BeginLine = "";
            String Class = "";
            String Type = "";
            String Category = "";
            String EndLine = "";
            String Priority = "";
            String FP = "";
            String Output = "";
            DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(XMLpath);
            org.w3c.dom.NodeList fileList = doc.getElementsByTagName("BugInstance");
            for (int i = 0; i < fileList.getLength(); i++) {
                System.out.println(" -------------------------" + fileList.item(i).getNodeName() + " NO." + (i+1) + " -------------------------");
                NO = "" + (i+1);
                Node fileNode = fileList.item(i);
                org.w3c.dom.NamedNodeMap namedNodeMap = fileNode.getAttributes();
                for (int j = 0; j < namedNodeMap.getLength(); j++) {
                    if(namedNodeMap.item(j).getNodeName() == "type"){
                        Type = namedNodeMap.item(j).getNodeValue();
                    } else if (namedNodeMap.item(j).getNodeName() == "category") {
                        Category = namedNodeMap.item(j).getNodeValue();
                    } else if (namedNodeMap.item(j).getNodeName() == "priority") {
                        Priority = namedNodeMap.item(j).getNodeValue();
                    }
                }
                NodeList nodeList = fileNode.getChildNodes();
                for (int k = 0; k < nodeList.getLength(); k++){
                    Node node = nodeList.item(k);
                    if(node.getNodeName() == "Field" || node.getNodeName() == "Method"){
                        NodeList cList = node.getChildNodes();
                        Node cur = cList.item(1);
                        org.w3c.dom.NamedNodeMap curMap = cur.getAttributes();
                        for(int l = 0; l < curMap.getLength(); l++){
                            if(curMap.item(l).getNodeName() == "classname"){
                                Class = curMap.item(l).getNodeValue();
                            } else if (curMap.item(l).getNodeName() == "sourcefile") {
                                FileName = curMap.item(l).getNodeValue();
                            } else if (curMap.item(l).getNodeName() == "start"){
                                BeginLine = curMap.item(l).getNodeValue();
                            } else if (curMap.item(l).getNodeName() == "end") {
                                EndLine = curMap.item(l).getNodeValue();
                            }
                        }
                        Random r = new Random();
                        double check = r.nextDouble();

                        Output = NO + "," + FileName + "," + Class + "," + Type + "," + Category + "," + BeginLine + "," + EndLine + "," + Priority;
                        System.out.println(Output);
                        writeText.newLine();
                        writeText.write(Output);
                    }
                }
            }


            writeText.flush();
            writeText.close();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
