# ScoreTally - Fonctionnalités Futures

Ce document regroupe les idées de fonctionnalités à implémenter dans les futures versions de ScoreTally.

## 🎮 Modes de jeu (Core Feature - À implémenter en priorité)

### Mode Comptage Rapide (Quick Score)
Interface minimaliste pour des parties rapides sans configuration préalable :
- **Interface simplifiée** :
  - Bouton `+` et `-` pour chaque joueur
  - Bouton "Ajouter joueur" en un clic
  - Pas besoin de créer un jeu en amont
  - Pas de sélection de jeu obligatoire
- **Joueurs à la volée** :
  - Nommer les joueurs directement dans l'interface
  - Option de lier à un joueur existant de la liste (autocomplete)
  - Ou créer un joueur temporaire pour cette session
- **Sauvegarde optionnelle** :
  - À la fin, possibilité de sauvegarder la partie dans l'historique
  - Associer à un jeu si souhaité
- **Use case** : Parties improvisées, comptage rapide sans setup

### Mode Complet
Toutes les fonctionnalités de ScoreTally :
- Sélection du jeu depuis la ludothèque
- Sélection des joueurs depuis la liste
- Historique détaillé
- Statistiques complètes
- Export de données
- Notes et détails de partie

### Mode Personnalisé (V2+)
Permettre à l'utilisateur de configurer l'interface selon ses besoins :
- **Tableau de bord configurable** :
  - Activer/Désactiver des sections (statistiques, historique, ludothèque)
  - Réorganiser les onglets du bottom nav
  - Choisir l'écran d'accueil par défaut
- **Features toggles** :
  - Activer/Désactiver les outils (dés, "qui commence")
  - Masquer les fonctionnalités non utilisées
  - Mode "Joueurs uniquement" ou "Jeux uniquement" si un seul aspect intéresse l'utilisateur
- **Templates de workflow** :
  - "Mode soirée jeux" (ludothèque + parties)
  - "Mode compétition" (statistiques + classements)
  - "Mode minimaliste" (comptage rapide uniquement)

### Sélection du mode
- **Premier lancement** : Assistant pour choisir le mode par défaut
- **Changement à tout moment** : Toggle dans les paramètres
- **Suggestion contextuelle** : L'app peut suggérer le mode adapté selon l'usage

## 👥 Gestion des joueurs (Core Feature - À implémenter en priorité)

### Liste commune de joueurs
- **Base de données centralisée** : Liste de joueurs réutilisable pour toutes les parties
- **Liaison sélective** : Lors de la création d'une partie :
  - Sélectionner le jeu
  - Choisir parmi la liste des joueurs existants ceux qui participent à cette partie
  - Pas obligatoire de tous les inclure, seulement ceux présents
  - Possibilité d'ajouter un nouveau joueur à la volée
- **Avantages** :
  - Pas besoin de recréer les joueurs à chaque partie
  - Statistiques persistantes par joueur
  - Historique complet des parties de chaque joueur
- **Flow UX** :
  1. Créer/Importer des joueurs dans la ludothèque de joueurs
  2. Lors d'une nouvelle partie, cocher ceux qui jouent
  3. Saisir les scores pour ces joueurs uniquement

## 🎲 Outils de jeu

### Lanceur de dés
- Interface pour lancer des dés virtuels
- Paramètres :
  - Nombre de dés (1-10)
  - Nombre de faces (4, 6, 8, 10, 12, 20, 100)
- Affichage animé du résultat
- Historique des derniers lancers
- Option de relancer
- Sons de dés (optionnel, avec toggle)

### Qui commence ?
- Outil pour déterminer aléatoirement qui commence la partie
- Deux modes :
  1. **Mode Joueurs** : Sélection parmi les joueurs de la partie
  2. **Mode Fun** : Phrases rigolotes aléatoires
     - "Celui/Celle qui a les cheveux les plus courts"
     - "Celui/Celle qui a les cheveux les plus longs"
     - "Le/La plus petit(e)"
     - "Le/La plus grand(e)"
     - "Celui/Celle qui a le plus ri aujourd'hui"
     - "Le dernier/La dernière arrivé(e)"
     - "Celui/Celle qui a mangé le plus de pizza cette semaine"
     - "Le/La plus bavard(e)"
     - etc.
- Animation de "roue de la fortune" ou tirage au sort
- Possibilité d'ajouter ses propres phrases personnalisées

## 📚 Ludothèque étendue

### Gestion avancée des jeux
Amélioration de la bibliothèque de jeux existante avec :

- **Image personnalisée** : Upload/sélection d'une image pour chaque jeu
- **Description complète** : Zone de texte libre pour décrire le jeu
- **Système de notation** : Notes sur 5 étoiles
- **Notes personnelles** :
  - Bloc-notes pour chaque jeu
  - Mémos sur les stratégies gagnantes
  - Variantes de règles testées
  - Anecdotes de parties
- **Métadonnées enrichies** :
  - Éditeur
  - Année de sortie
  - Complexité (1-5)
  - Type de jeu (coopératif, compétitif, solo, etc.)
  - Temps de setup
  - Extensions possédées
- **Wishlist** : Marquer des jeux comme "à acheter"
- **Lieu de stockage** : Où le jeu est rangé physiquement
- **État** : Possédé / Emprunté / Prêté (avec à qui)

### Intégration BoardGameGeek (future)
- Import automatique des infos depuis BGG
- Sync des notes et commentaires
- Recherche dans la base BGG

## 🎨 Interface & UX

### Thèmes UI personnalisables
- **Thèmes prédéfinis** :
  - Thème par défaut (violet Material 3)
  - Thème sombre / clair (déjà implémenté avec Dynamic Colors)
  - Thèmes colorés : Bleu, Vert, Rouge, Orange, Rose
  - Thèmes "jeux" : Bois/vintage, Minimaliste, Coloré/ludique
- **Personnalisation avancée** :
  - Sélection de couleur primaire/secondaire
  - Choix du mode d'accentuation
  - Prévisualisation en temps réel
  - Sauvegarde de plusieurs thèmes personnalisés
- **Application contextuelle** :
  - Thème différent par jeu (optionnel)
  - Thème nuit automatique selon l'heure
- **Autres personnalisations** :
  - Polices de caractères alternatives
  - Taille de police (accessibilité)
  - Espacement et densité de l'UI
  - Forme des cartes (arrondies, carrées, etc.)

### Internationalisation (i18n)
- **Langues supportées** (à venir) :
  - 🇫🇷 Français (par défaut)
  - 🇬🇧 Anglais
  - 🇪🇸 Espagnol
  - 🇩🇪 Allemand
  - 🇮🇹 Italien
  - + Autres langues selon la demande
- **Éléments traduits** :
  - Interface complète (boutons, menus, messages)
  - Noms des catégories par défaut
  - Messages d'erreur et notifications
  - Aide et tutoriels
- **Gestion** :
  - Détection automatique de la langue système
  - Sélection manuelle dans les paramètres
  - Fichiers de ressources strings.xml multilingues
  - Format standard Android pour les traductions
- **Formats localisés** :
  - Dates et heures selon la locale
  - Formats de nombres et durées
  - Monnaies (si fonctionnalité prix des jeux ajoutée)

### Autres améliorations UX
- Widgets pour l'écran d'accueil Android
- Animations et transitions fluides
- Mode compact pour petits écrans
- Support tablettes avec layout adaptatif

### Partage social
- Partage des résultats de partie (image générée)
- Export des statistiques en PDF
- Partage de la ludothèque

## 📊 Statistiques avancées

- Graphiques de progression par joueur
- Heatmap des jours de jeu
- Prédictions de victoire basées sur l'historique
- "Némésis" : joueur contre qui on perd le plus
- "Lucky charm" : joueur avec qui on gagne le plus

## 🔔 Notifications & Rappels

- Rappels pour les soirées jeux planifiées
- Suggestions de jeux non joués depuis longtemps
- Notifications de milestones (100ème partie, etc.)

## 🌐 Fonctionnalités communautaires (cloud - si migration future)

- Partage de parties entre joueurs
- Classements entre amis
- Défis et achievements
- Groupes de joueurs récurrents

---

**Note** : Ces fonctionnalités sont des idées pour l'avenir. Priorité aux fonctionnalités core de l'app d'abord !
