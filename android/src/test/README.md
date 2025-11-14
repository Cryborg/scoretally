# Tests ScoreTally

## 📋 Vue d'ensemble

Ce projet utilise une stratégie de tests complète avec trois niveaux de tests :
1. **Tests unitaires** (dans `src/test/kotlin`)
2. **Tests d'instrumentation** (dans `src/androidTest/kotlin`)
3. **Tests UI Compose** (dans `src/androidTest/kotlin`)

## 🛠️ Stack de test

### Tests unitaires (JVM)
- **JUnit 4** - Framework de test
- **MockK** - Mocking library pour Kotlin
- **Turbine** - Testing library pour Flow
- **Truth** - Assertions plus lisibles
- **Coroutines Test** - Testing pour coroutines
- **Arch Core Testing** - LiveData testing (si nécessaire)

### Tests d'instrumentation (Android)
- **AndroidX Test** - Framework de test Android
- **Room Testing** - Testing pour bases de données Room
- **Hilt Testing** - Injection de dépendances pour tests
- **Compose UI Test** - Testing pour composables Jetpack Compose

## 🚀 Lancer les tests

### Tous les tests
```bash
./gradlew check
```

### Tests unitaires uniquement
```bash
./gradlew test
# ou
./gradlew testDebugUnitTest
```

### Tests d'instrumentation uniquement
```bash
./gradlew connectedAndroidTest
# ou
./gradlew connectedDebugAndroidTest
```

### Test spécifique
```bash
./gradlew test --tests ValidatePlayerNameUseCaseTest
./gradlew test --tests "ValidatePlayerNameUseCaseTest.validate returns Valid when name is unique"
```

## 📁 Structure des tests

```
src/
├── test/kotlin/                          # Tests unitaires (JVM)
│   ├── domain/
│   │   └── usecase/
│   │       └── ValidatePlayerNameUseCaseTest.kt
│   ├── data/
│   │   └── repository/
│   │       └── PlayerRepositoryImplTest.kt
│   └── ui/
│       ├── players/
│       │   ├── AddPlayerViewModelTest.kt
│       │   └── EditPlayerViewModelTest.kt
│       └── matches/
│           └── MatchDetailViewModelTest.kt
│
└── androidTest/kotlin/                   # Tests d'instrumentation (Android)
    ├── data/
    │   └── local/
    │       └── dao/
    │           └── PlayerDaoTest.kt
    └── ui/
        └── components/
            └── GameFormTest.kt
```

## ✍️ Écrire un test

### Test unitaire pour un ViewModel

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class MyViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: MyViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Setup mocks
        viewModel = MyViewModel(...)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test description`() = runTest {
        // Given

        // When

        // Then
        assertThat(result).isEqualTo(expected)
    }
}
```

### Test pour un Flow avec Turbine

```kotlin
@Test
fun `uiState emits correct values`() = runTest {
    viewModel.uiState.test {
        // When
        viewModel.doSomething()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val firstEmission = awaitItem()
        assertThat(firstEmission.value).isEqualTo(expected)

        val secondEmission = awaitItem()
        assertThat(secondEmission.value).isEqualTo(expected2)
    }
}
```

### Test DAO avec Room in-memory

```kotlin
@RunWith(AndroidJUnit4::class)
class MyDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var dao: MyDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = database.myDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndRetrieve() = runTest {
        // Given
        val entity = MyEntity(...)

        // When
        dao.insert(entity)
        val result = dao.getById(1)

        // Then
        assertThat(result).isEqualTo(entity)
    }
}
```

### Test UI Compose

```kotlin
@RunWith(AndroidJUnit4::class)
class MyComposableTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun myComposable_displaysCorrectly() {
        // Given
        composeTestRule.setContent {
            MyComposable(...)
        }

        // Then
        composeTestRule.onNodeWithText("Expected Text").assertExists()
        composeTestRule.onNodeWithTag("MyButton").assertIsEnabled()
    }
}
```

## 🎯 Bonnes pratiques

### Nommage des tests
- Utiliser le pattern : `methodName_stateUnderTest_expectedBehavior`
- Ou en français : \`méthode avec entrée X retourne Y\`
- Être descriptif et clair

### Structure des tests (Given-When-Then)
```kotlin
@Test
fun `test example`() {
    // Given - Setup
    val input = "test"

    // When - Action
    val result = functionUnderTest(input)

    // Then - Assertion
    assertThat(result).isEqualTo(expected)
}
```

### MockK
```kotlin
// Créer un mock
val repository = mockk<PlayerRepository>()

// Configurer le comportement
coEvery { repository.getPlayer(1) } returns player

// Vérifier les appels
coVerify { repository.getPlayer(1) }
coVerify(exactly = 2) { repository.getPlayer(any()) }

// Mock relaxed (retourne valeurs par défaut)
val mock = mockk<MyClass>(relaxed = true)
```

### Turbine pour Flow
```kotlin
flow.test {
    val item = awaitItem()
    assertThat(item).isEqualTo(expected)

    awaitComplete() // Pour les Flow qui se terminent
    cancelAndIgnoreRemainingEvents() // Pour les Flow infinis
}
```

## 📊 Couverture de code

Pour générer un rapport de couverture (nécessite configuration Jacoco) :

```bash
./gradlew testDebugUnitTestCoverage
# Rapport dans : build/reports/coverage/
```

## 🐛 Debugging des tests

### Lancer un seul test en mode debug
1. Dans Android Studio, clic droit sur le test
2. Sélectionner "Debug 'testName'"
3. Placer des breakpoints comme d'habitude

### Logs dans les tests
```kotlin
@Test
fun myTest() {
    println("Debug: current value = $value")
    // ou
    Log.d("TestTag", "Debug info")
}
```

## ⚠️ Pièges courants

### 1. Oublier `advanceUntilIdle()`
```kotlin
// ❌ Mauvais - le test peut passer à tort
viewModel.doSomething()
assertThat(state.value).isTrue()

// ✅ Bon - attend que toutes les coroutines se terminent
viewModel.doSomething()
testDispatcher.scheduler.advanceUntilIdle()
assertThat(state.value).isTrue()
```

### 2. Ne pas utiliser `runTest` pour les coroutines
```kotlin
// ❌ Mauvais
@Test
fun myTest() {
    viewModelScope.launch { ... }
}

// ✅ Bon
@Test
fun myTest() = runTest {
    // code with coroutines
}
```

### 3. Oublier de reset Main dispatcher
```kotlin
@After
fun tearDown() {
    Dispatchers.resetMain() // ⚠️ IMPORTANT !
}
```

## 📚 Ressources

- [MockK Documentation](https://mockk.io/)
- [Turbine GitHub](https://github.com/cashapp/turbine)
- [Truth Assertions](https://truth.dev/)
- [Compose Testing](https://developer.android.com/jetpack/compose/testing)
- [Room Testing](https://developer.android.com/training/data-storage/room/testing-db)

## 🔄 Workflow recommandé

1. **Avant de coder** : Écrire les tests (TDD optionnel mais recommandé)
2. **Pendant le développement** : Lancer les tests fréquemment
3. **Avant chaque commit** : `./gradlew check`
4. **Avant un refactoring** : S'assurer que tous les tests passent
5. **Après un refactoring** : Re-vérifier tous les tests

---

**Note :** Ces tests sont des exemples de référence. Utilise-les comme base pour créer d'autres tests similaires dans le projet.
