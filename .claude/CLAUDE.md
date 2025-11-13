# ScoreTally - Documentation pour Claude

## 📱 Description du Projet

**ScoreTally** est une application Android de gestion de scores pour les jeux de société. Elle permet de suivre les parties, gérer une ludothèque personnelle et une liste de joueurs, et consulter des statistiques.

## 🏗️ Architecture

### Stack Technique
- **Langage** : Kotlin
- **UI** : Jetpack Compose + Material Design 3
- **Architecture** : Clean Architecture (Data/Domain/Presentation)
- **Injection de dépendances** : Hilt/Dagger
- **Base de données** : Room (version 2)
- **Navigation** : Navigation Compose
- **Préférences** : DataStore Preferences
- **Coroutines** : Flow, StateFlow, viewModelScope
- **Build** : Gradle 8.13 + Kotlin DSL

### Structure du Projet

```
scoretally/
├── android/src/main/kotlin/com/scoretally/
│   ├── data/
│   │   ├── local/
│   │   │   ├── dao/          # DAOs Room
│   │   │   ├── database/     # Configuration Room
│   │   │   └── entity/       # Entités Room
│   │   └── repository/       # Implémentations des repositories
│   ├── domain/
│   │   ├── model/            # Modèles métier
│   │   ├── repository/       # Interfaces des repositories
│   │   └── usecase/          # Use cases
│   ├── ui/
│   │   ├── components/       # Composables réutilisables
│   │   ├── games/            # Écrans de gestion des jeux
│   │   ├── matches/          # Écrans de gestion des parties
│   │   ├── players/          # Écrans de gestion des joueurs
│   │   ├── settings/         # Écran des paramètres
│   │   ├── navigation/       # Navigation et routes
│   │   └── theme/            # Thème et styles
│   └── di/                   # Modules Hilt
├── android/src/main/res/
│   ├── values/               # Strings anglais (défaut)
│   ├── values-fr/            # Strings français
│   ├── values-es/            # Strings espagnol
│   ├── values-de/            # Strings allemand
│   └── values-it/            # Strings italien
└── build.gradle.kts
```

## 📊 Modèles de Données

### Entités Room

1. **GameEntity** (table: `games`, version DB: 2)
   - id, name, minPlayers, maxPlayers, averageDuration
   - category, imageUri, description, rating, notes
   - **scoreIncrement** (ajouté en v2)

2. **PlayerEntity** (table: `players`)
   - id, name, preferredColor

3. **MatchEntity** (table: `matches`)
   - id, gameId, startTime, endTime, notes, status

4. **MatchPlayerEntity** (table: `match_players`)
   - matchId, playerId, score, rank

### Modèles Métier

- **Game** : Représente un jeu de société
- **Player** : Représente un joueur
- **Match** : Représente une partie jouée
- **MatchPlayer** : Lien entre partie et joueur avec score
- **MatchWithDetails** : Agrégation Match + Game + PlayerScores
- **PlayerScore** : Agrégation MatchPlayer + Player
- **UserPreferences** : Préférences utilisateur (langue, thème)
- **AppLanguage** : Enum (SYSTEM, ENGLISH, FRENCH, SPANISH, GERMAN, ITALIAN)
- **AppTheme** : Enum (SYSTEM, LIGHT, DARK)

## 🧭 Navigation

### Routes disponibles

- **Screen.Matches** : Liste des parties (écran de démarrage)
- **Screen.Games** : Liste des jeux
- **Screen.Players** : Liste des joueurs
- **Screen.Settings** : Paramètres de l'application
- **Screen.AddMatch** : Créer une nouvelle partie
- **Screen.AddGame** : Ajouter un jeu
- **Screen.AddPlayer** : Ajouter un joueur
- **Screen.MatchDetail** : Détails d'une partie avec gestion des scores
- **Screen.GameDetail** : Détails d'un jeu (placeholder)
- **Screen.PlayerDetail** : Détails d'un joueur (placeholder)

### Navigation Bar (Bottom)

4 onglets : Parties | Jeux | Joueurs | Paramètres

## 🎨 Thème et Internationalisation

### Thème
- **Support** : Light / Dark / System (auto-détection)
- **Dynamic Color** : Material You sur Android 12+
- **Gestion** : AppThemeProvider observe les préférences et applique le thème
- **Persistance** : DataStore Preferences

### Internationalisation
- **5 langues supportées** : EN (défaut), FR, ES, DE, IT
- **Auto-détection** : Langue système par défaut
- **Changement dynamique** : Via AppCompatDelegate.setApplicationLocales()
- **Fichiers** : res/values-xx/strings.xml

## ✨ Fonctionnalités Implémentées

### ✅ Gestion des Jeux
- Créer/Lister des jeux
- Champs : nom, min/max joueurs, durée, catégorie, description, scoreIncrement
- scoreIncrement permet de configurer l'incrément par défaut pour les scores

### ✅ Gestion des Joueurs
- Créer/Lister des joueurs
- Champs : nom, couleur préférée
- Affichage avec avatar coloré

### ✅ Gestion des Parties
- Créer une partie en sélectionnant un jeu et des joueurs
- Redirection automatique vers l'écran de partie après création
- Écran de détail avec :
  - Liste des joueurs avec avatars colorés
  - Boutons +/- pour incrémenter/décrémenter les scores
  - Utilisation du scoreIncrement du jeu sélectionné
  - Click sur le score pour édition manuelle via dialog
  - Désactivation du bouton - quand score = 0

### ✅ Paramètres
- Sélection de la langue (5 langues)
- Sélection du thème (System/Light/Dark)
- Persistance avec DataStore
- Application immédiate des changements

### ✅ Base de Données
- Room version 2
- Migration destructive (fallbackToDestructiveMigration)
- 4 tables : games, players, matches, match_players

## 🔧 Dépendances Hilt

### Modules configurés

1. **DatabaseModule** : Fournit Room database et DAOs
2. **RepositoryModule** : Bind les repositories
   - GameRepository
   - PlayerRepository
   - MatchRepository
   - MatchPlayerRepository
   - PreferencesRepository

## 🚀 Build et Déploiement

### Compilation
```bash
cd scoretally && ./gradlew assembleDebug
```

### Installation sur device
```bash
adb install -r android/build/outputs/apk/debug/android-debug.apk
```

### Version actuelle
- versionCode: 1
- versionName: "1.0.0"
- minSdk: 24
- targetSdk: 34
- compileSdk: 34

## 🐛 Erreurs Connues et Solutions

### Erreur : ClassCastException Long cannot be cast to String
**Solution** : Utiliser `savedStateHandle.get<Long>("matchId")` et non `get<String>`

### Erreur : MissingBinding android.content.Context
**Solution** : Utiliser `@ApplicationContext` dans l'injection du Context pour PreferencesRepository

### Erreur : Experimental Material3 API
**Solution** : Ajouter `@OptIn(ExperimentalMaterial3Api::class)` sur les fonctions utilisant ExposedDropdownMenuBox

### Warning : ArrowBack deprecated
**Note** : Utiliser `Icons.AutoMirrored.Filled.ArrowBack` pour les prochaines implémentations

## 📝 Conventions de Code

- **Principes** : DRY, KISS, SOLID
- **Pas de commentaires inutiles** : Le code doit être auto-documenté
- **Pas de code mort** : Pas de code de rétrocompatibilité inutile
- **Strings** : Toujours utiliser stringResource() - jamais de strings hardcodés
- **Navigation** : Utiliser les fonctions createRoute() pour les routes avec paramètres
- **Database** : Toute modification de schéma nécessite incrément de version

## 🔄 Git Repository

- **URL** : https://github.com/Cryborg/scoretally.git
- **Branch** : master
- **Commits** : Messages détaillés avec co-authorship Claude

## 📋 TODO / À Faire

Voir le fichier `FUTURE_FEATURES.md` pour la liste complète des fonctionnalités prévues.

### Priorités court terme
- Écrans GameDetail et PlayerDetail (actuellement placeholder)
- Statistiques de base
- Export/Import de données

### Priorités moyen terme
- Mode comptage rapide
- Outils de jeu (dés, qui commence)
- Thèmes UI personnalisables avancés

## 💡 Notes pour les Prochaines Sessions

- La base de données utilise `.fallbackToDestructiveMigration()` - penser à implémenter des migrations propres pour la production
- Les préférences utilisateur sont appliquées au démarrage via `AppThemeProvider`
- Toutes les modifications de thème/langue sont immédiates (LaunchedEffect)
- Les scores utilisent le `scoreIncrement` configuré dans le jeu
- Compile avec Java 21 et Kotlin 1.9.20
