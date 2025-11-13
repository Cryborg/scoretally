# ScoreTally

Application Android de gestion de scores pour jeux de société.

## 🎯 Description

ScoreTally est une application permettant de suivre les scores de vos parties de jeux de société, gérer votre ludothèque, et consulter les statistiques de vos joueurs.

## 📱 Fonctionnalités actuelles (V1 - MVP)

- ✅ **Gestion de la ludothèque**
  - Liste des jeux de société
  - Ajout/édition de jeux
  - Informations : nom, joueurs min/max, durée, catégorie, description

- ✅ **Gestion des joueurs**
  - Liste commune de joueurs
  - Ajout/édition de joueurs
  - Avatar et couleur préférée

- 🚧 **Suivi des parties** (En développement)
  - Création de parties
  - Saisie des scores
  - Historique des parties

## 🛠️ Stack Technique

### Architecture
- **Pattern** : MVVM + Clean Architecture
- **Langage** : Kotlin
- **UI** : Jetpack Compose + Material 3
- **Navigation** : Navigation Compose
- **Injection de dépendances** : Hilt

### Persistance
- **Database** : Room (SQLite)
- **Préférences** : DataStore (future)

### Async & Reactive
- **Coroutines** : kotlinx.coroutines
- **Flows** : StateFlow pour l'état UI

### Versions
- Kotlin : 1.9.20
- Java : 21
- Gradle : 8.5
- Android SDK :
  - Min : 24 (Android 7.0)
  - Target : 34 (Android 14)
  - Compile : 34

## 📐 Architecture du projet

```
com.scoretally/
├── ui/                      # Couche présentation
│   ├── navigation/          # Navigation Compose
│   ├── theme/               # Thème Material 3
│   ├── components/          # Composables réutilisables
│   ├── games/               # Écrans jeux
│   ├── players/             # Écrans joueurs
│   ├── match/               # Écrans parties (à venir)
│   ├── history/             # Écrans historique (à venir)
│   └── stats/               # Écrans statistiques (à venir)
├── domain/                  # Couche métier
│   ├── model/               # Modèles (Game, Player, Match, MatchPlayer)
│   ├── repository/          # Interfaces repositories
│   └── usecase/             # Use cases (business logic)
├── data/                    # Couche données
│   ├── local/
│   │   ├── entity/          # Room entities
│   │   ├── dao/             # Room DAOs
│   │   └── database/        # RoomDatabase
│   └── repository/          # Implémentations repositories
└── di/                      # Modules Hilt (DI)
```

## 🚀 Prochaines étapes

Voir le fichier [FUTURE_FEATURES.md](FUTURE_FEATURES.md) pour la liste complète des fonctionnalités futures, notamment :

- **Modes de jeu** : Comptage rapide, Mode complet, Mode personnalisé
- **Outils** : Lanceur de dés, "Qui commence ?"
- **Ludothèque étendue** : Images, notes, ratings
- **Statistiques avancées** : Graphiques, analytics, classements
- **Export de données**

## 🏗️ Build

```bash
./gradlew assembleDebug
```

## 📝 Notes de développement

### Principes suivis
- **DRY** (Don't Repeat Yourself)
- **KISS** (Keep It Simple, Stupid)
- **SOLID**

### Conventions
- Pas de code pour rétrocompatibilité (projet neuf)
- Code bien écrit = peu de commentaires
- Tests unitaires à venir

## 📄 Licence

Projet personnel

---

**Version actuelle** : 1.0.0-alpha
**Dernière mise à jour** : 2025-01-13
