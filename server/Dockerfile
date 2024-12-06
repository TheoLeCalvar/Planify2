# Étape 1 : Image de base pour construire le projet
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Copier les fichiers du projet dans le conteneur
COPY pom.xml .
COPY src ./src

# Construire le projet avec Maven
RUN mvn clean package -DskipTests

# Étape 2 : Image de base pour exécuter l'application
FROM eclipse-temurin:21-jre

# Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Copier le jar généré depuis l'étape de construction
COPY --from=build /app/target/*.jar app.jar

# Exposer le port de l'application (modifier en fonction de votre configuration Spring)
EXPOSE 8080

# Commande pour exécuter l'application
CMD ["java", "-jar", "app.jar"]
