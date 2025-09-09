## GitHub App (Android · Kotlin · Jetpack Compose)

A modern Android app for exploring GitHub users and repositories with Jetpack Compose. It follows a clean architecture, supports offline favorites, lets you browse repository files by branch, and includes an optional Gemini-powered code explainer.

### Table of contents
- **Features**
- **Screens & Navigation**
- **Tech stack**
- **Architecture**
- **Getting started**
  - Prerequisites
  - Clone & open
  - Configure API keys (GitHub + Gemini)
  - Build & run
- **Configuration reference**
- **Project structure**
- **Networking**
- **Troubleshooting**
- **Contributing**
- **License**

## Features
- **Home**: Browse and search GitHub users
- **User detail**: View profile, repositories, followers/following, and toggle favorite
- **Favorites**: Room-backed local list of favorited users
- **Repo detail**:
  - **File browser** for repository contents
  - **Branch picker** to reload contents per selected branch
  - **Raw file preview** modal
  - **ASK AI**: optional Gemini-based code explanation for the open file
- **Navigation**: Back from repo detail returns to the user’s detail when available; back from user detail returns to home

## Screens & Navigation
- **Routes**:
  - `home` → Home screen
  - `detail/{username}/{id}` → Selected user detail
  - `repo/{owner}/{repo}?path={path}&branch={branch}&user={user}&uid={uid}` → Repository detail
  - `favorites`, `settings`
- **Back behavior**:
  - From repo detail: Back goes to the originating user detail if `user`/`uid` are provided
  - From user detail: Back goes to Home

## Tech stack
- **Language/UI**: Kotlin, Jetpack Compose, Material 3
- **Async**: Kotlin Coroutines, Flow
- **Networking**: Retrofit, OkHttp, Moshi
- **Images/Animation**: Coil, Lottie
- **Persistence**: Room (favorites)
- **Build**: Gradle (Kotlin DSL)

## Architecture
Layered structure:
- **presentation**: Compose UI, ViewModels, navigation
- **domain**: models, repository interface, use cases
- **data**: Retrofit API service, DTOs, mappers, Room DAO/DB, repository implementation

Dependency flow: `presentation → domain → data`

## Getting started
### Prerequisites
- Android Studio Giraffe/Koala or newer
- Android SDK 34 (compileSdk 34), minSdk 24
- JDK 11+ (project uses JVM target 1.8)

### Clone & open
```bash
git clone https://github.com/akirace/github_app.git
cd github_app
```
Open the project in Android Studio.

### Configure API keys (REQUIRED)
This app uses:
- **GitHub REST API** (a token is strongly recommended to avoid strict rate limits)
- **Google Gemini API** for the optional “ASK AI” feature

Add both to `local.properties` at the project root (this file is ignored by Git).

Example `local.properties`:
```properties
sdk.dir=C:\\Users\\YOU\\AppData\\Local\\Android\\Sdk
GITHUB_TOKEN=ghp_your_personal_access_token
GEMINI_API_KEY=your_gemini_api_key
```
Notes:
- The build reads these and exposes `BuildConfig.GITHUB_TOKEN` and `BuildConfig.GEMINI_API_KEY` to app code.
- If `GITHUB_TOKEN` is omitted, you may hit GitHub rate limits or encounter failures.
- Do not commit `local.properties` or tokens to version control.

### Build & run
From Android Studio: Build > Make Project, then Run.

Command line:
- macOS/Linux: `./gradlew assembleDebug`
- Windows (PowerShell/CMD): `./gradlew.bat assembleDebug`

The debug APK is produced at `app/build/outputs/apk/debug/app-debug.apk`.

## Configuration reference
- `app/build.gradle.kts`:
  - Reads `local.properties` for `GITHUB_TOKEN` and `GEMINI_API_KEY`
  - Exposes them as `BuildConfig` fields
- GitHub API base URL: `https://api.github.com/`
- Gemini endpoint used: `v1beta/models/gemini-1.5-flash:generateContent`

## Project structure
```
app/
  src/main/java/com/train/testcursor/
    data/
      local/              # Room database & DAO
      mapper/             # DTO <-> domain mappers
      remote/             # Retrofit API and DTOs
      repository/         # Repository implementation
    domain/
      model/              # Domain models
      repository/         # Repository interface
      usecase/            # Use cases
    presentation/
      navigation/         # Navigation graph
      screens/            # Compose screens
      detail/ home/ repo/ favorites/ settings/ viewmodels
  build.gradle.kts        # Android module Gradle
build.gradle.kts          # Root Gradle
settings.gradle.kts
```

Key files of interest:
- `NetworkModule.kt` – Retrofit/OkHttp configuration; GitHub token header via `BuildConfig.GITHUB_TOKEN`
- `GithubApiService.kt` – GitHub endpoints, including:
  - `GET repos/{owner}/{repo}/contents/{path}` with `ref` for branch
  - `GET repos/{owner}/{repo}/branches` for branch listing
- `RepoDetailViewModel.kt` – Loads repo detail, branches, and contents per selected branch; exposes `selectBranch`
- `RepoDetailScreen.kt` – Branch picker UI, file browser, raw preview, and Gemini “ASK AI”
- `AppNav.kt` – Route definitions and back navigation behaviors

## Networking
- **Auth**: GitHub Personal Access Token (PAT) via `Authorization: Bearer <token>`
- **JSON**: Moshi
- **Logging**: OkHttp logging interceptor (BASIC)
- **Timeouts**: 30s connect/read/write

Gemini flow (simplified):
1. Build prompt with current file path + content
2. POST to the Gemini generateContent endpoint with the `key` query param
3. Extract the first candidate text

## Troubleshooting
- **401/403 from GitHub**:
  - Ensure `GITHUB_TOKEN` is a valid PAT and not expired
  - Confirm it’s in `local.properties` and Gradle sync succeeded
- **Gemini errors**:
  - 400/403: verify `GEMINI_API_KEY` and API enablement
  - Timeouts: check connectivity/proxies
- **Branch list empty**:
  - Repo may be empty or require permissions (private)
- **Raw preview garbled**:
  - Only UTF-8 plain text is shown; binaries aren’t supported
- **Rate limiting**:
  - Add/refresh `GITHUB_TOKEN`; unauthenticated calls are very limited

## Contributing
1. Fork the repo & create a feature branch
2. Make changes with clear, small commits
3. Open a PR describing the motivation and changes

## License
Apache-2.0.
