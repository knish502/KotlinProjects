kotlinc $(pwd)Main.kt -include-runtime -d run.jar
java -jar run.jar