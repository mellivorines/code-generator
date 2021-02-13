FROM java:8
VOLUME /opt/code-generator
COPY target/code-generator-2.1.0.jar code-generator.jar
RUN bash -c "touch /code-generator.jar"
EXPOSE 8000
ENTRYPOINT ["java","-jar","-Xms256M","-Xmx512M","code-generator.jar"]
