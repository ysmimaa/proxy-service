FROM openjdk:11.0.9-buster

VOLUME /temp

COPY build/libs/proxy-service*.jar /opt/proxy-service_home/deployments/proxy-service*.jar

CMD ["java" , "-jar" , "/opt/proxy-service_home/deployments/proxy-service*.jar"]

EXPOSE 8080

