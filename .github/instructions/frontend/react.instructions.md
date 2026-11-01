---
description: "Rules and best practices for the Onshore React frontend using Vite, React Router, and Redux Toolkit."
applyTo: "onshore-frontend/**"
---

# Onshore React Frontend Guidelines

## Project Stack

- React with JavaScript and JSX, built with Vite.
- React Router for client-side routing.
- Redux Toolkit and React Redux for shared application state.
- Tailwind CSS and the existing shared components in `src/components/ui/`.
- API calls go to the Spring Boot services through the frontend API utility and configured gateway routes.

Prefer the project's existing JavaScript and JSX conventions. Do not introduce TypeScript, React Admin, a new data-fetching framework, or a second state-management library unless the task explicitly calls for it.

## Project Structure

```text
onshore-frontend/
├── package.json
├── vite.config.js
└── src/
    ├── App.jsx                 # Application routes and top-level layout
    ├── main.jsx                # React root and Redux Provider
    ├── Redux/                  # Store, domain slices, and async thunks
    ├── components/             # Shared and domain-specific components
    │   └── ui/                 # Reusable UI primitives
    ├── contexts/               # React context providers
    ├── hooks/                  # Reusable React hooks
    ├── pages/                  # Route-level pages, grouped by persona/domain
    ├── services/               # Domain service helpers and API operations
    ├── utils/                  # Shared utilities, including API configuration
    ├── constants/              # Shared constants
    └── styles/                 # Shared styles
```

Follow the closest existing feature's organization. Keep route-level screens in `src/pages/`, shared UI in `src/components/`, and domain-specific logic near its existing Redux or service module. Do not create `src/resources/` or a React Admin data provider.

## React Components

- Use function components and hooks.
- Keep components focused and compose shared UI from existing primitives before adding new dependencies or duplicating components.
- Use semantic HTML and accessible labels, keyboard behavior, and focus states.
- Keep transient UI state local. Use Redux only for state shared across routes or domain features.
- Avoid unnecessary effects. Derive values during render when possible; use effects for synchronization with external systems.
- Add memoization (`useMemo`, `useCallback`, or `React.memo`) only when it addresses a demonstrated identity or rendering concern; do not make it a blanket requirement.
- Use the existing Tailwind and component styling conventions. Avoid introducing a parallel styling system.
- Use the existing toast and dialog components for user feedback and confirmation flows.

## Routing

- Add and update routes in the existing React Router setup in `src/App.jsx`, following the route grouping and layout patterns already used for traveler, airline, super-admin, and authentication pages.
- Keep navigation inside the application on React Router APIs such as `Link`, `NavLink`, and `useNavigate`.
- Protect authenticated pages and redirect by user role using the established auth state and route conventions.
- Do not duplicate route definitions or introduce another router.

## Redux Toolkit

- The store is configured in `src/Redux/globleState.js`; preserve its existing export and reducer registration patterns.
- Keep domain state in its feature slice under `src/Redux/<domain>/`.
- Use `createSlice` for reducers and `createAsyncThunk` for asynchronous operations, following the neighboring feature's conventions.
- Handle pending, fulfilled, and rejected states explicitly. Expose loading and error state to the UI where needed.
- Prefer selectors and focused state updates. Avoid copying the entire store into component-local state.
- Clear or reset stale errors and domain state at appropriate lifecycle boundaries using explicit slice actions.
- Do not put API requests, browser navigation, or other side effects inside reducers.

## API and Service Calls

- Reuse the configured API client in `src/utils/api` and existing request/header helpers such as `getHeaders`.
- Keep endpoint-specific requests in the relevant domain thunk or service module; do not scatter raw `fetch` or Axios calls through page components.
- Use the Spring Boot service routes already exposed through the API gateway. Verify the gateway and service API contract before changing endpoint paths or payload shapes.
- Pass authentication through the established header helper. Never hard-code tokens, credentials, service secrets, or environment-specific URLs.
- Handle failures explicitly: return or display useful errors, and do not turn failed requests into success-shaped fallback data.
- Put request/response transformations in a domain service or thunk, not in presentational components.

## Forms and Validation

- Follow the existing form implementation in the same feature before choosing or introducing a form library.
- Validate user input before submission and show actionable, field-associated errors.
- Prevent duplicate submissions while a request is in progress.
- Preserve entered values when a recoverable request fails.
- Confirm destructive actions and report their outcome to the user.

## Configuration and Security

- Use Vite's `import.meta.env` only for client-safe configuration exposed with the `VITE_` prefix.
- Client-side environment values are public. Never place secrets or credentials in frontend environment variables.
- Do not store sensitive data in local storage or session storage unless the existing authentication flow requires it; follow that flow without broadening the stored data.
- Avoid logging tokens, personal data, payment details, or other sensitive information.
- Treat values returned by APIs and URL parameters as untrusted input.

## Dependencies and Validation

- Prefer existing dependencies and utilities. Add packages only when necessary and keep changes limited to the frontend package manifest and lockfile.
- Run frontend checks from `onshore-frontend/`:
  - `npm run lint`
  - `npm run build`
- Add or update tests when the feature's existing test setup supports them. Do not claim tests passed unless they were run.
