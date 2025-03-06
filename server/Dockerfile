# Étape 1 : Image de base pour la construction
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier uniquement le fichier pom.xml (et potentiellement .mvn/) pour la mise en cache des dépendances
COPY pom.xml ./

# Télécharger et mettre en cache les dépendances
RUN mvn dependency:resolve -B

# Copier le reste du projet
COPY src ./src

# Construire l'application
RUN mvn clean package -DskipTests

# Étape 2 : Image d'exécution (légère)
FROM eclipse-temurin:21-jre

# Définir le répertoire de travail
WORKDIR /app

# Copier uniquement le fichier JAR généré
COPY --from=build /app/target/*.jar app.jar

# Exposer le port de l'application
EXPOSE 8080

# Exécuter l'application
CMD ["java", "-jar", "app.jar"]
