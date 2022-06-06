IPMD
=====
Inspired by other SOTA static analysis tools, we selected PMD as the basic framework and combined methods like `data flow analysis` and `external patch` to make an improved PMD, called `IPMD`.

# Code Structure
```
|--IPMD
|   |--src                                       
|      |--main/java                              /* The place where IPMD is accomplished
|        |--Results                              /* The folder where stores the results
|          |--csv                                /* The folder where stores the csv files
|          |--xml                                /* The folder where stores the xml files
|        |--Ruleset                              /* The folder where stores the ruleset of IPMD
|          |--BetterRule.xml                     /* The better rule we create for IPMD
|          |--OriginRule.xml                     /* The origin rule PMD has
|        |--Testcase                             /* Place to store the testcase
|          |--testcase-bottleleung
|          |--testcase-gaopu
|          |--testcase-HelloWorld521
|          |--testcase-sample   
|        |--IPMD.java                            /* core function to accomplish IPMD
|        |--ImprovedMethod.java                  /* data flow analysis and external patch
|        |--XML2CSV.java                         /* function to change xml files to csv files 
|   |--IPMD.jar                                  /* The jar file of IPMD
|   |--pom.xml                                   /* The configuration settings
```


# Installatoin
* [Maven 3.8.5](https://maven.apache.org/download.cgi) For different device environment configurations, please refer to the link [here](https://blog.csdn.net/m0_69128987/article/details/123948671).
* [PMD 6.32.0](https://pmd.github.io/#downloads) We highly recommend you to download `Ver 6.32.0` in case of different version errors.
* [FindBugs 3.0.1](http://findbugs.sourceforge.net/downloads.html) If installed in editor like `Eclipse`, we recommend you to follow the link [here](https://www.cnblogs.com/kingsonfu/p/12420590.html).
* [Soot](https://www.sable.mcgill.ca/soot/soot_download.html) For detailed install instructions, please refer to the link [here](https://mayuwan.github.io/2018/05/08/soot/).
* [Java 1.8](https://www.oracle.com/java/technologies/downloads/#java8) We highly recommend you to download `Ver 1.7` or `Ver 1.8` in case of different version errors. For detailed install instructions, please refer to the link [here](https://zhuanlan.zhihu.com/p/51238480).
# Usage
* First of all, please download the `IPMD` project to local.
* For the examples in our paper, we have provided you four sample testcases under the `Testcase` folder. You can also add the test files you want under path: `src/main/java/Testcase/`. 
* Finally run the `IPMD.jar` in the root directory with cmd.
```
java -jar IPMD.jar
```
* You can also use IDEA or Eclipse to run our code. You will need to run `IPMD.java` which stores under path: `./src/main/java/IPMD.java`.
```
javac IPMD.java
java IPMD
```
* We have saved different versions of output for original PMD, iPMD with only data flow analysis, iPMD with only external patch and fully improved PMD. To check our results, please refer to the Results below.
# Results
* XML files\
The XML files are generated in path: `./src/main/java/Results/xml/`.\
File ended with `.xml` means running the `original PMD`.\
File ended with `-data.xml` means running the IPMD with `only data flow analysis method`.\
File ended with `-external.xml` means running IPMD with `only external patch method`.\
File ended with `-both.xml` means running IPMD with `both data flow analysis and external patch methods`.

# Conclusion
We have identified several causes of false positive warnings caused by PMD, and proposed iPMD to improve the performance of the method in the face of these problems in a targeted manner. The experimental results show that our modified approach iPMD performs better and achieves a lower false positive rate which is @yaqi on average.
