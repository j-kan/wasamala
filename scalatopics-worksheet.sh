mvn install:install-file -Dfile=../../mallet/lib/trove-2.0.2.jar -DgroupId=trove \
    -DartifactId=trove -Dversion=2.0.2 -Dpackaging=jar
    
[INFO] Scanning for projects...
[INFO] artifact org.apache.maven.plugins:maven-eclipse-plugin: checking for updates from scala-tools.org
[INFO] Searching repository for plugin with prefix: 'install'.
[INFO] org.apache.maven.plugins: checking for updates from scala-tools.org
[INFO] org.codehaus.mojo: checking for updates from scala-tools.org
[INFO] ------------------------------------------------------------------------
[INFO] Building Unnamed - mallet:wasamala:jar:1.0-SNAPSHOT
[INFO]    task-segment: [install:install-file] (aggregator-style)
[INFO] ------------------------------------------------------------------------
[INFO] [install:install-file {execution: default-cli}]
[INFO] Installing /Users/jkan/umass/mallet/lib/trove-2.0.2.jar to /Users/jkan/.m2/repository/trove/trove/2.0.2/trove-2.0.2.jar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESSFUL
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 3 seconds
[INFO] Finished at: Mon Mar 08 10:30:59 EST 2010
[INFO] Final Memory: 10M/80M
[INFO] ------------------------------------------------------------------------


mvn install:install-file -DgroupId=org.processing -DartifactId=processing-core \
  -Dversion=1.1 -Dpackaging=jar -Dfile=/Applications/Processing.app/Contents/Resources/Java/core.jar
  
[INFO] Scanning for projects...
[INFO] artifact org.scala-tools:maven-scala-plugin: checking for updates from scala-tools.org
[INFO] artifact org.apache.maven.plugins:maven-eclipse-plugin: checking for updates from scala-tools.org
[INFO] Searching repository for plugin with prefix: 'install'.
[INFO] org.apache.maven.plugins: checking for updates from scala-tools.org
[INFO] org.codehaus.mojo: checking for updates from scala-tools.org
[INFO] ------------------------------------------------------------------------
[INFO] Building Unnamed - mallet:wasamala:jar:1.0-SNAPSHOT
[INFO]    task-segment: [install:install-file] (aggregator-style)
[INFO] ------------------------------------------------------------------------
[INFO] [install:install-file {execution: default-cli}]
[INFO] Installing /Applications/Processing.app/Contents/Resources/Java/core.jar to /Users/jkan/.m2/repository/org/processing/processing-core/1.1/processing-core-1.1.jar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESSFUL
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 2 seconds
[INFO] Finished at: Mon Mar 15 06:38:27 EDT 2010
[INFO] Final Memory: 10M/80M
[INFO] ------------------------------------------------------------------------




mvn scala:cc -Dmaven.scala.displayCmd=true -o


mvn scala:run -Dlauncher=Viz  -o

git add   pom.xml
git add   src/main/scala/edu/umass/cs/mallet/users/kan/topics/LDAHyperExtended.scala
git add   src/main/scala/edu/umass/cs/mallet/users/kan/topics/runner/RhinopLastRunner.scala
git add   src/main/scala/edu/umass/cs/mallet/users/kan/topics/tui/LDAHyperTUI.scala
git add   src/main/scala/edu/umass/cs/mallet/users/kan/viz/TopicModelViz.scala

ln -s ~/umass/mallet/src mallet-src
ln -s ~/umass/mallet/kan mallet-kan


ctags -f .tmtags\
  --verbose=yes --links=yes\
  --fields=Kn\
  --excmd=pattern\
  -R   \
  --exclude='.svn|.git|.csv|.manager|.settings'\
  --tag-relative=yes\
  --PHP-kinds=+cf\
  --regex-PHP='/abstract class ([^ ]*)/\1/c/'\
  --regex-PHP='/interface ([^ ]*)/\1/c/' \
  --regex-PHP='/(public |static |abstract |protected |private )+function ([^ (]*)/\2/f/'\
  --JavaScript-kinds=+cf\
  --regex-JavaScript='/(\w+) ?: ?function/\1/f/'
    
