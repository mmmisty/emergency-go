all:
	javac -d . src/com/company/*.java src/org/json/*.java
	jar -cvfe AppServer.jar com.company.Main com/company/* org/json/*
clean:
	rm -rf com/
