# Cinema App — Portfolio Case Study

## Upwork-ready fields

### Project title

Cinema App — Modern Android Movie Discovery Experience

### Role

Android Developer — UI/UX, Architecture, Offline Data, and Testing

### Project description

Designed and developed a modular Android movie discovery app using Kotlin, MVVM, Clean Architecture, Hilt, Retrofit, Room, Coroutines, and Flow. I redesigned the home, detail, and favorites experiences; added cache-aware loading, empty, error, and retry states; implemented offline favorites with sorting and undo; and added automated tests with CI verification. The result is a polished portfolio application focused on maintainability, resilient data handling, and a clear Material UI.

### Skills and deliverables

1. Android App Development
2. Kotlin
3. MVVM
4. API Integration
5. Android UI Design

## Website case study

### Overview

Cinema App is a personal Android project for discovering now-playing, popular, and top-rated movies. I evolved the application from a functional movie browser into a more polished portfolio project with clearer visual hierarchy, resilient state handling, modular architecture, and automated verification.

### The challenge

The application needed to present several kinds of movie content without overwhelming the user, preserve useful cached data during network issues, and keep screen-specific behavior from leaking into shared UI components. It also needed secure local configuration so a TMDB key would not be committed to the repository.

### My role

I worked across the Android presentation and data layers, including:

- Home, movie detail, and favorites UX.
- ViewModels and reactive UI-state mapping.
- Room-backed movie and favorite persistence.
- Retrofit API integration and data mapping.
- Hilt dependency injection.
- Unit tests, lint verification, and GitHub Actions.

### The solution

#### 1. Focused discovery experience

The home screen uses a featured now-playing movie plus compact popular and top-rated sections. Loading skeletons, retry messaging, image fallbacks, and cached content keep the experience understandable across network states.

#### 2. Information-rich movie details

The detail screen combines backdrop imagery, rating and release metadata, overview, similar movies, reviews, sharing, and favorite controls in one clear hierarchy.

#### 3. Useful favorite library

Favorites are stored locally and displayed in a dedicated two-column grid. Users can sort saved movies, remove an item directly, and reverse accidental removal with Undo.

#### 4. Maintainable data flow

ViewModels depend on focused domain use cases. A repository coordinates Retrofit and Room through a network-bound flow, while explicit mappers keep API, database, and domain models separate.

### Architecture

```text
UI → ViewModel → Use Case → Repository → REST API
                                  └────→ Room
```

The repository emits reactive data through Kotlin Flow. Cached movie lists can remain visible while fresh data loads or when a refresh fails.

### Verification

- Unit tests cover presentation state, domain use cases, and data mapping.
- The full debug and release unit-test suites pass.
- Android lint and debug APK assembly complete successfully.
- GitHub Actions verifies tests and lint on pushes and pull requests to `main`.

### What I would improve next

- Add pagination for longer movie lists.
- Expand accessibility and UI instrumentation coverage.
- Add screenshot tests for critical visual states.
- Move API access behind a small backend proxy for stronger credential control.
- Prepare a signed internal release for hands-on evaluation.

## Short website card

### Title

Cinema App

### Subtitle

A modular Android movie discovery app with resilient offline data, polished Material UI, and automated verification.

### Technology line

Kotlin · Clean Architecture · MVVM · Hilt · Retrofit · Room · Coroutines · Flow

### Primary links

- Source code: https://github.com/fhmsyhd/Cinema-App
- Case study: use the full website case study above

## Demo storyboard

Recommended duration: 25–35 seconds.

1. **Discover (0–6 seconds):** Show the featured movie and browse popular and top-rated sections.
2. **Explore (6–16 seconds):** Open a movie, reveal its overview, similar movies, and reviews.
3. **Save (16–24 seconds):** Add the movie to favorites and open the favorite library.
4. **Manage (24–30 seconds):** Remove the movie, show Undo, then restore it.
5. **Closing frame (30–35 seconds):** Show “Kotlin · Clean Architecture · Offline-first favorites”.

For Upwork, keep narration in English, avoid contact information inside the video, and export as MP4 under 60 seconds.
