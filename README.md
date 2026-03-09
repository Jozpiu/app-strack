# Study Tracker Android

Aplicativo Android em Kotlin + Jetpack Compose para registrar e visualizar horas estudadas.

## Funcionalidades

- Registro de horas estudadas por dia
- Indicadores visuais (orbs com progresso circular) de hoje, semana e mês
- Meta semanal (30h) e mensal (100h) com barra de progresso
- Sequência de dias consecutivos estudados (streak)
- Aproveitamento semanal: percentual de dias com estudo no período
- Contagem de sessões no mês e horas restantes para a meta
- Persistência local com Room (SQLite)

## Arquitetura

```
MainActivity
└── HomeScreen (Compose)
    └── HomeViewModel
        └── StudyRepository
            └── StudySessionDao (Room)
                └── AppDatabase
```

- **UI**: Jetpack Compose + Material3
- **Estado**: `ViewModel` com `mutableStateOf`
- **Banco de dados**: Room com entidade `StudySession` (id, dateEpochDay, hours)
- **Reatividade**: `Flow` do Room observado via `collectLatest`

## Requisitos

- Android Studio Hedgehog ou superior
- Android SDK 26+ (minSdk 26)
- Kotlin 1.9+

## Como executar

1. Abra a pasta no Android Studio.
2. Execute o sync do Gradle.
3. Rode no emulador ou dispositivo Android (API 26+).

## Estrutura de arquivos

```
app/src/main/java/com/example/studytracker/
├── MainActivity.kt
├── data/
│   ├── AppDatabase.kt
│   ├── StudyRepository.kt
│   ├── StudySession.kt
│   └── StudySessionDao.kt
└── ui/
    ├── HomeScreen.kt
    ├── HomeViewModel.kt
    └── theme/
```
