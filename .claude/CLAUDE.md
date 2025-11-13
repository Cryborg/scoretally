# Instructions Claude pour ce Template

Ce projet est un **template libGDX/Kotlin** destiné à servir de base pour de nouveaux jeux mobiles.

## 🎯 Objectif du Template

Fournir un point de départ rapide et propre pour créer des jeux Android avec :
- Configuration Gradle fonctionnelle
- Architecture SOLID/DRY/KISS
- Natives libGDX correctement configurés
- Support multi-plateforme (Android + Desktop)

## ⚠️ IMPORTANT : Faire Évoluer le Template

Quand tu travailles sur d'autres projets de jeu (comme Idle Mine Tycoon), et que tu développes des **fonctionnalités génériques et réutilisables**, pense à les **ajouter au template** !

### Exemples de fonctionnalités à ajouter :

#### ✅ À Ajouter au Template
- **Système de sauvegarde** (SharedPreferences wrapper)
- **Gestionnaire d'assets** (fonts, images, sons)
- **Components UI réutilisables** (boutons stylisés, labels, barres de progression)
- **Système d'animations/tweening**
- **Gestionnaire audio** (musique, effets sonores)
- **Classes utilitaires** (formatage de nombres, calculs, timers)
- **Système de scènes/transitions** entre écrans
- **Gestionnaire d'input** unifié
- **Système de particules** basique
- **Classes de base** pour les entités de jeu

#### ❌ NE PAS Ajouter au Template
- Logique métier spécifique à un jeu
- Assets spécifiques (images, sons d'un jeu particulier)
- Game design spécifique (idle mechanics, combat system, etc.)
- Contenu narratif

### Workflow pour Ajouter au Template

1. **Développe** la fonctionnalité dans ton jeu actuel
2. **Teste** qu'elle marche bien
3. **Généralise** le code (enlève les dépendances spécifiques)
4. **Copie** dans le template avec une structure propre
5. **Documente** dans le README du template
6. **Crée des exemples** d'utilisation en commentaires

### Structure pour les Nouvelles Features

```
core/src/main/kotlin/com/template/game/
├── domain/
│   ├── models/        # Modèles de base génériques
│   └── usecases/      # Use cases communs
├── data/
│   ├── persistence/   # ← Système de sauvegarde
│   └── repository/    # Patterns repository
├── presentation/
│   ├── screens/       # Écrans de base
│   └── ui/           # ← Components UI réutilisables
└── utils/            # ← Classes utilitaires
```

## 📝 Checklist Avant d'Ajouter une Feature

- [ ] Le code est générique et réutilisable
- [ ] Pas de dépendances vers un jeu spécifique
- [ ] Documentation claire (KDoc + README)
- [ ] Exemples d'utilisation en commentaires
- [ ] Suit les principes SOLID/DRY/KISS
- [ ] Testé et fonctionnel

## 🔄 Maintenir le Template à Jour

Le template doit rester **minimal mais complet** :
- Ne pas le surcharger avec trop de features
- Garder chaque feature **optionnelle et découplée**
- Prioriser les fonctionnalités **les plus réutilisables**

---

**Rappel** : Ce template est vivant ! Enrichis-le au fur et à mesure de tes projets. 🚀
