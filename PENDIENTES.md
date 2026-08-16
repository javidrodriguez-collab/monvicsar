# Pendientes de infraestructura (2026-08-16)

Cosas que quedaron sin resolver hoy, para retomar mañana **físicamente en el servidor** (Remote Desktop / consola directa, no por SSH).

## 1. ERPNext Helpdesk caído (500 Internal Server Error) — URGENTE

- `erp.monvicsar.com/helpdesk` da 500. Causado probablemente por un `docker restart erpnext-backend-1` que dejó el link de assets a medias (la carpeta de build de "helpdesk" no aparece en `sites/assets/helpdesk` del contenedor, solo "desk").
- **Fix a correr en el servidor** (bloqueado para mí por protección de seguridad, hay que correrlo manualmente):
  ```powershell
  wsl -d Ubuntu -- bash -c "cd /home/monvicsar/erpnext && docker compose -f pwd.yml restart backend frontend"
  ```
- Si eso no lo arregla, el siguiente paso sería `docker compose -f pwd.yml down` + `up -d` (reinicio completo del stack, mismo patrón que un incidente anterior documentado en `n8n-proyectos/claude.md`).
- Verificar después con: `erp.monvicsar.com/helpdesk` debe cargar el dashboard normal.

## 2. Bug de renombrado en `HD Ticket Status` (sin resolver)

Detalle técnico completo documentado en `n8n-proyectos/claude.md` (sección "App Android Monvicsar Técnico"). Resumen:

- `label_agent` es el campo `autoname` (= la clave primaria del documento) — no se puede cambiar con un PUT normal, hay que usar `frappe.client.rename_doc`.
- `rename_doc` falla con `404 DoesNotExistError: "Módulo Helpdesk no encontrado"`, aunque el `Module Def` "Helpdesk" existe correctamente en la base y todo el filesystem del contenedor está bien instalado.
- Causa raíz: `get_module_app()` en Frappe revisa `frappe.local.module_app`, un mapa cacheado **en memoria del proceso** (`site_cache`) — un `docker restart erpnext-backend-1` no lo arregló, sigue sin explicación completa.
- **Estado actual real de los estados**: `Open`/`Replied`/`En Diagnostico`/`En Reparacion`/`Closed` sin renombrar todavía. Colores sí actualizados (Gray/Orange/Blue/Black-sin-cambiar/Green). `Resolved` fue eliminado.
- **Pendiente**: probar si renombrar funciona directo desde la UI de Frappe (nunca se llegó a probar), o investigar más a fondo el cacheo de `module_app`.

## 3. Login de GLPI (`http://localhost:8090` en el servidor)

- Las credenciales por defecto (`glpi`/`glpi`) no funcionan — el usuario "glpi" no existe en esta instalación.
- Se creó un usuario nuevo por consola: **`monvicsar_admin` / `Monvicsar2026!`** — pero el intento de darle perfil de administrador falló ("Profile not found") probando `Admin`, `Super-Admin`, `Súper-Admin`.
- **Pendiente**: encontrar el nombre real del perfil de admin (consultando la tabla `glpi_profiles` directo, o desde la UI si el login sin perfil especial ya permite entrar) y correr:
  ```powershell
  wsl -d Ubuntu -- docker exec glpi-app bash -c "php /var/www/html/glpi/bin/console user:grant monvicsar_admin --profile='<NOMBRE_REAL>' --allow-superuser -n"
  ```
- Nota: esto era solo una exploración secundaria (alternativa a ERPNext), no es urgente.

## Contexto general del proyecto

Ver `claude.md` en el repo `n8n-proyectos` (https://github.com/javidrodriguez-collab/n8n-proyectos) para todo el detalle de infraestructura, credenciales, y el modelo de datos completo de este proyecto (estados de ticket, Tipo de Llamada/CHGC, etc.).
