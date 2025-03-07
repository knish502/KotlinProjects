kotlinc $(pwd)main.kt -include-runtime -d run.jar
java -jar run.jar