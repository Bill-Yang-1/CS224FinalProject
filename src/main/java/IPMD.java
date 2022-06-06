import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.FileDataSource;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.PMD;

public class IPMD {
    static String OriginRuleSet = "./src/main/java/Ruleset/OriginRule.xml";
    static String BetterRuleSet = "./src/main/java/Ruleset/BetterRule.xml";
    static String PrependClasspath = "./target/classes";
    static String Sample = "./src/main/java/Testcase/testcase-sample";
    static String SamplePMDXML = "./src/main/java/Results/xml/sample-pmd.xml";
    static String SampleiPMDXML = "./src/main/java/Results/xml/sample-ipmd-both.xml";
    static String SamplePMDCSV = "./src/main/java/Results/csv/sample-pmd.csv";
    static String SampleiPMDCSV = "./src/main/java/Results/csv/sample-ipmd-both.csv";
    static String HW = "./src/main/java/Testcase/testcase-HelloWorld521";
    static String HWPMDXML = "./src/main/java/Results/xml/HelloWorld521-pmd.xml";
    static String HWiPMDXML = "./src/main/java/Results/xml/HelloWorld521-ipmd-both.xml";
    static String HWPMDCSV = "./src/main/java/Results/csv/HelloWorld521-pmd.csv";
    static String HWiPMDCSV = "./src/main/java/Results/csv/HelloWorld521-ipmd-both.csv";
    static String GP = "./src/main/java/Testcase/testcase-gaopu";
    static String GPPMDXML = "./src/main/java/Results/xml/gaopu-pmd.xml";
    static String GPiPMDXML = "./src/main/java/Results/xml/gaopu-ipmd-both.xml";
    static String GPPMDCSV = "./src/main/java/Results/csv/gaopu-pmd.csv";
    static String GPiPMDCSV = "./src/main/java/Results/csv/gaopu-ipmd-both.csv";
    static String BL = "./src/main/java/Testcase/testcase-bottleleung";
    static String BLPMDXML = "./src/main/java/Results/xml/bottleleung-pmd.xml";
    static String BLiPMDXML = "./src/main/java/Results/xml/bottleleung-ipmd-both.xml";
    static String BLPMDCSV = "./src/main/java/Results/csv/bottleleung-pmd.csv";
    static String BLiPMDCSV = "./src/main/java/Results/csv/bottleleung-ipmd-both.csv";

    public static void main(String[] args) throws IOException {
        // Sample
        OriginPMD(Sample,SamplePMDXML);
        BetterPMD(Sample,SampleiPMDXML);
        BetterPMD(Sample,"./src/main/java/Results/xml/sample-ipmd-data.xml");
        BetterPMD(Sample,"./src/main/java/Results/xml/sample-ipmd-external.xml");
//        XML2CSV.PMDXML2CSV(SamplePMDXML,SamplePMDCSV);
//        XML2CSV.Better_PMDXML2CSV(SampleiPMDXML,SampleiPMDCSV,0);
//        XML2CSV.Better_PMDXML2CSV(SampleiPMDXML,"./src/main/java/Results/csv/sample-ipmd-data.csv",1);
//        XML2CSV.Better_PMDXML2CSV(SampleiPMDXML,"./src/main/java/Results/csv/sample-ipmd-external.csv",2);
        // HelloWorld521
        OriginPMD(HW,HWPMDXML);
        BetterPMD(HW,HWiPMDXML);
        BetterPMD(HW,"./src/main/java/Results/xml/HelloWorld521-ipmd-data.xml");
        BetterPMD(HW,"./src/main/java/Results/xml/HelloWorld521-ipmd-external.xml");
//        XML2CSV.PMDXML2CSV(HWPMDXML,HWPMDCSV);
//        XML2CSV.Better_PMDXML2CSV(HWiPMDXML,HWiPMDCSV,0);
//        XML2CSV.Better_PMDXML2CSV(HWiPMDXML,"./src/main/java/Results/csv/HelloWorld521-ipmd-data.csv",1);
//        XML2CSV.Better_PMDXML2CSV(HWiPMDXML,"./src/main/java/Results/csv/HelloWorld521-ipmd-external.csv",2);
        // gaopu
        OriginPMD(GP,GPPMDXML);
        BetterPMD(GP,GPiPMDXML);
        BetterPMD(GP,"./src/main/java/Results/xml/gaopu-ipmd-data.xml");
        BetterPMD(GP,"./src/main/java/Results/xml/gaopu-ipmd-external.xml");
//        XML2CSV.PMDXML2CSV(GPPMDXML,GPPMDCSV);
//        XML2CSV.Better_PMDXML2CSV(GPiPMDXML,GPiPMDCSV,0);
//        XML2CSV.Better_PMDXML2CSV(GPiPMDXML,"./src/main/java/Results/csv/gaopu-ipmd-data.csv",1);
//        XML2CSV.Better_PMDXML2CSV(GPiPMDXML,"./src/main/java/Results/csv/gaopu-ipmd-external.csv",2);
        // bottleleung
        OriginPMD(BL,BLPMDXML);
        BetterPMD(BL,BLiPMDXML);
        BetterPMD(BL,"./src/main/java/Results/xml/bottleleung-ipmd-data.xml");
        BetterPMD(BL,"./src/main/java/Results/xml/bottleleung-ipmd-external.xml");
//        XML2CSV.PMDXML2CSV(BLPMDXML,BLPMDCSV);
//        XML2CSV.Better_PMDXML2CSV(BLiPMDXML,BLiPMDCSV,0);
//        XML2CSV.Better_PMDXML2CSV(BLiPMDXML,"./src/main/java/Results/csv/bottleleung-ipmd-data.csv",1);
//        XML2CSV.Better_PMDXML2CSV(BLiPMDXML,"./src/main/java/Results/csv/bottleleung-ipmd-external.csv",2);
    }

    public static void OriginPMD(String TestCasePath, String PMDOutputPath) throws IOException{
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setMinimumPriority(RulePriority.MEDIUM);
        configuration.setRuleSets(OriginRuleSet);
        configuration.setInputPaths(TestCasePath);
        configuration.prependClasspath(PrependClasspath);
        configuration.setReportFormat("xml");
        configuration.setReportFile(PMDOutputPath);
//        Writer rendererOutput = new StringWriter();
//        Renderer renderer = createRenderer(rendererOutput);
//        renderer.start();
        PMD.doPMD(configuration);
//        renderer.end();
//        renderer.flush();
//        System.out.println("Rendered Report:");
//        System.out.println(rendererOutput.toString());

    }

    public static void BetterPMD(String TestCasePath, String PMDOutputPath)throws IOException{
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setMinimumPriority(RulePriority.MEDIUM);
        configuration.setRuleSets(BetterRuleSet);
        configuration.setInputPaths(TestCasePath);
        configuration.prependClasspath(PrependClasspath);
        configuration.setReportFormat("xml");
        configuration.setReportFile(PMDOutputPath);
        Writer rendererOutput = new StringWriter();
        Renderer renderer = createRenderer(rendererOutput);
        renderer.start();
        PMD.doPMD(configuration);
        renderer.end();
        renderer.flush();
//        System.out.println("Rendered Report:");
//        System.out.println(rendererOutput.toString());

    }

    private static Renderer createRenderer(Writer writer) {
        XMLRenderer xml = new XMLRenderer("UTF-8");
        xml.setWriter(writer);
        return xml;
    }

    private static List<DataSource> determineFiles(String basePath) throws IOException {
        Path dirPath = FileSystems.getDefault().getPath(basePath);
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.java");

        List<DataSource> files = new ArrayList<>();

        Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                if (matcher.matches(path.getFileName())) {
                    System.out.printf("Using %s%n", path);
                    files.add(new FileDataSource(path.toFile()));
                } else {
                    System.out.printf("Ignoring %s%n", path);
                }
                return super.visitFile(path, attrs);
            }
        });
        System.out.printf("Analyzing %d files in %s%n", files.size(), basePath);
        return files;
    }
}
