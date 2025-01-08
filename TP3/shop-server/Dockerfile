
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copie le fichier pom.xml et les fichiers de dépendances
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Télécharge les dépendances pour réduire le temps de build ultérieur
RUN ./mvnw dependency:go-offline -B

# Copie le code source
COPY src ./src

# Compile le projet et crée le JAR
RUN ./mvnw clean package -Dmaven.test.skip=true

# Étape 2 : Exécution
FROM eclipse-temurin:21-jdk-jammy

# Définit le répertoire de travail pour l'exécution
WORKDIR /app

# Copie le JAR généré depuis l'étape de build
COPY --from=build /app/target/shop-app-0.0.1-SNAPSHOT.jar shop-app.jar

# Définit le point d'entrée pour exécuter l'application
ENTRYPOINT ["java", "-jar", "shop-app.jar"]

# Expose le port 8080 pour permettre les connexions
EXPOSE 8080
