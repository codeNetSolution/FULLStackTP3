
# Mise à jour Backend et Frontend

Ce document décrit les étapes nécessaires pour mettre à jour le backend et le frontend.

---

## Mises à jour

### Backend
- Mise à jour de Spring Boot vers la version `3.4.1`.
- Mise à jour de Java vers la version `21`.
- Mise à jour des dépendances pour assurer leur compatibilité avec les nouvelles versions de Spring Boot et Java.
- L'application est maintenant stable avec des versions Long Term Support (LTS).

### Frontend
- Remplacement de Webpack par Vite pour améliorer les performances de construction.
- Réinstallation des dépendances à jour et compatibles.
- L'application est désormais stable avec des versions Long Term Support (LTS).

---

## Étapes de mise à jour

### Etape 1: 
- Si l'appplication est lancé, veuillez l'arreter. 
- Accéder au dossier de la racine du projet que vous avez actuellement, et executer cette commande :
```bash
   docker-compose down -v
   ```

### Etape 2 : Cloner le nouveau projet :
```bash
   git clone https://github.com/codeNetSolution/FULLStackTP3.git 

   ```
- Accéder au répertoire racine 
```bash
   cd FULLStackTP3/TP3/
   ```


## Etape 3 : Executer la mise à jour et le lancement de l'application 

```bash
   docker-compose down -v 
   docker-compose up --build
   ```

## Etape 4 : L'application se lancera 
- Forentend : http://localhost:4200
- Serveur : https://localhost:8080




