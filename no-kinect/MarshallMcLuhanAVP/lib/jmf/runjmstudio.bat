@set JAVA_CMD="%JAVA_HOME%\bin\java"
@if "%JAVA_HOME%"== "" SET JAVA_CMD="java"
%JAVA_CMD% -classpath fobs4jmf.jar;jmf.jar JMStudio