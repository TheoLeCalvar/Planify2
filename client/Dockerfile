# Utiliser l'image Node 22 pour la construction
FROM node:22 AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers de l'application
COPY . .

# Installer les dépendances
RUN npm install

# Passer les variables d'environnement au moment de la construction
ARG VITE_API_URL

# Construire l'application avec les variables d'environnement
RUN VITE_API_URL=$VITE_API_URL npm run build

# Étape de production
FROM nginx:alpine

# Copier les fichiers statiques de la construction précédente
COPY --from=build /app/dist /usr/share/nginx/html

# Exposer le port 80
EXPOSE 80

# Lancer nginx
CMD ["nginx", "-g", "daemon off;"]
