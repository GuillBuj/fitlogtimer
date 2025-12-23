# 🏋️‍♂️ FitLogTimer - Carnet d'entraînement / Timer

![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white&labelColor=ED8B00)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-6DB33F?logo=springboot&logoColor=white&labelColor=6DB33F)
![Spring MVC](https://img.shields.io/badge/Spring%20MVC-6DB33F?logo=spring&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?logo=spring&logoColor=white)
![H2 Database](https://img.shields.io/badge/H2-2.2.224-1A73E8?logo=h2&logoColor=white&labelColor=1A73E8)
![SQL/JPQL](https://img.shields.io/badge/SQL%20%2F%20JPQL-4479A1?logo=postgresql&logoColor=white)

![HTML5](https://img.shields.io/badge/HTML5-E34F26?logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?logo=css3&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?logo=javascript&logoColor=black)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1.2-005F0F?logo=thymeleaf&logoColor=white&labelColor=005F0F)
![HTMX](https://img.shields.io/badge/HTMX-1.9.9-3366FF?logo=htmx&logoColor=white&labelColor=3366FF)
![Chart.js](https://img.shields.io/badge/Chart.js-4.4.1-FF6384?logo=chartdotjs&logoColor=white&labelColor=FF6384)
![FullCalendar.js](https://img.shields.io/badge/FullCalendar.js-6.1.10-3A87F2?logo=fullcalendar&logoColor=white&labelColor=3A87F2)

![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?logo=apachemaven&logoColor=white&labelColor=C71A36)
![Apache POI](https://img.shields.io/badge/Apache%20POI-5.2.3-D22128?logo=apache&logoColor=white&labelColor=D22128)
![Lombok](https://img.shields.io/badge/Lombok-1.18.30-EA4C89?logo=lombok&logoColor=white&labelColor=EA4C89)
![MapStruct](https://img.shields.io/badge/MapStruct-1.5.5-4A90E2?logoColor=white&labelColor=4A90E2)


## 📋 Présentation

**FitLogTimer** est une application web Spring Boot complète pour le suivi d'entraînement personnel avec timer intégré, calculateur intelligent et statistiques avancées. Multi-plateforme avec synchronisation via Google Drive.

## ✨ Fonctionnalités

### 📖 Journal d'entraînement
- Saisie rapide avec autocomplétion
- Historique complet avec recherche/filtrage
- Notes et commentaires par séance et série

### 📊 Analyses & Statistiques
- Records personnels par exercice
- Courbes d'évolution avec Chart.js
- Tableaux récapitulatifs
- Statistiques périodiques
- Calcul de volume total

### ⏱️ Timer intégré
- Affichage permanent en footer
- Personnalisable (travail, repos)
- Notifications par changement de couleur du footer et du favicon

### 🧮 Calculateur 3 entrées
- Poids soulevé
- Nombre de répétitions
- 1RM estimé à partir du poids et du nombre de répétitions
- Tableaux ciblés en fonction 

### 🎨 Personnalisation
- Couleurs par exercice
- Ordre modifiable
- Affichage ou non d'un exercice dans des listes

### 🔄 **Saisie multi-source avec synchronisation**
- **Application Web** : Import via fichier json transmis par Drive
- **Fichier Excel Drive** : Import via Apache POI
- **Application Android** : Saisie basique avec synchro via Drive

  👉 [Voir le dépôt GitHub](https://github.com/GuillBuj/fitlogtimer-mobile)
  *(Application minimaliste permettant de saisir en tout lieu)*
- **Synchronisation bidirectionnelle**

## 🏗️ Architecture

### Backend
- **Java 21** + **Spring Boot 3.4.2**
- **Spring MVC** + **Thymeleaf 3.1.2**
- **Spring Data JPA** + **H2 2.2.224**
- **Apache POI 5.2.3** pour Excel
- **Lombok 1.18.30** + **MapStruct 1.5.5.Final**
- **SQL/JPQL** pour les requêtes personnalisées
- **Google Drive API** pour la synchronisation

### Frontend
- **HTML5** + **CSS3** moderne
- **JavaScript vanilla** pour le timer, le calculateur et le filtrage
- **HTMX 1.9.9** pour une actualisation ciblée
- **Chart.js** pour les graphiques
- **FullCalendar.js** pour les calendriers


## 👤 Auteur

**GuillBuj** - [GitHub Profile](https://github.com/GuillBuj)

---

*Projet débuté dans le cadre de mon Titre Professionnel Concepteur Développeur d'Applications(obtenu). Toujours en cours d'évolution!*
