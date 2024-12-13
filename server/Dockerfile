# Étape 1 : Image de base pour la construction du projet
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier uniquement le fichier pom.xml et télécharger les dépendances (mis en cache si le pom.xml ne change pas)
COPY pom.xml .

# Télécharger les dépendances et les mettre en cache
RUN mvn dependency:go-offline -B

# Copier les sources de l'application (ne sera exécuté que si src/ change)
COPY src ./src

# Construire l'application
RUN mvn clean package -DskipTests

# Étape 2 : Image de base pour exécuter l'application (image plus légère)
FROM eclipse-temurin:21-jre

# Définir le répertoire de travail
WORKDIR /app

# Copier le jar généré depuis l'étape de construction
COPY --from=build /app/target/*.jar app.jar

# Exposer le port de l'application (modifier si nécessaire)
EXPOSE 8080

# Commande d'exécution
CMD ["java", "-jar", "app.jar"]
