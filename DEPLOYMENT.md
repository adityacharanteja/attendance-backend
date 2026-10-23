# Vercel and Render Deployment

The frontend repository serves the UI statically on Vercel. Its `/api/*` function proxies requests to the Spring Boot service on Render, keeping the login session and CSRF cookies same-origin in the browser.

## Render

1. Connect the main `attendance` repository to Render and create the service from `render.yaml` at the repository root.
2. Configure `DB_URL` with a reachable MySQL JDBC URL, plus `DB_USERNAME` and `DB_PASSWORD` in Render's environment settings. Render does not provide a MySQL database with this service; use an external MySQL provider.
3. Keep `SESSION_COOKIE_SECURE=true` for the HTTPS service. Render supplies the application port automatically.
4. Wait for `/actuator/health` to report healthy and copy the service's public base URL.

## Vercel

1. Import `adityacharanteja/attendance-frontend` as a Vercel project, using the repository root and the Other framework preset. `vercel.json` configures the static output and routes.
2. Add `BACKEND_URL` as an environment variable, set to the Render service base URL (for example, `https://attendance-backend.onrender.com`, with no path suffix).
3. Redeploy the Vercel project. Test registration, sign-in, and dashboard requests through the Vercel domain.

## Local Development

Copy `.env.example` to `.env` and set the local MySQL connection values there. `.env` is ignored by Git.

A database password was present in an earlier committed version of `application.properties`. Rotate that credential in the database provider; removing it from the current file does not remove it from Git history.
