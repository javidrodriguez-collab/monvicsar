# Pendientes de infraestructura (2026-08-16)

## ✅ RESUELTO (2026-08-16, sesión posterior): Helpdesk 500 + bug de rename_doc

**Causa raíz encontrada**: `sites/apps.txt` en el bench (la lista maestra de apps instaladas que usa Frappe para resolver `get_module_app()`) solo tenía `erpnext` y `frappe` — le faltaban `helpdesk` y `telephony`, aunque ambas apps estaban perfectamente instaladas en disco y en la base de datos. Por eso Frappe nunca podía resolver el módulo "Helpdesk" en ningún contexto — esto explicaba **a la vez** el 500 de `/helpdesk` y el bug viejo de `rename_doc` de abajo (misma causa, dos síntomas).

**Fix aplicado**:
1. Se corrigió `sites/apps.txt` dentro del contenedor `erpnext-backend-1` (`erpnext\nfrappe\nhelpdesk\ntelephony`).
2. Restart completo del stack (`backend frontend queue-short queue-long scheduler websocket`).
3. `bench --site frontend clear-cache` — necesario porque Redis (no reiniciado por el restart de contenedores) tenía cacheado el mapa viejo de `app_modules` desde antes del fix.
4. Verificado: `/helpdesk` carga 200 OK, `rename_doc` funciona (se probó renombrando las 5 entradas del nuevo DocType `Tipo de Llamada` sin error).

Si este bug reaparece en el futuro (ej. tras instalar una app nueva), revisar primero `sites/apps.txt` dentro del contenedor backend y confirmar que liste todas las apps instaladas, luego `bench clear-cache`.

## 2. Bug de renombrado en `HD Ticket Status` — YA NO DEBERÍA OCURRIR

Ver el fix de arriba (causa raíz resuelta). **Sigue pendiente**: aplicar el renombrado real de las 5 entradas (`Open`/`Replied`/`En Diagnostico`/`En Reparacion`/`Closed` → `Assign`/`Traveling`/`Working`/`Suspend`/`Closed`), que nunca se llegó a intentar por el bug — ahora que `rename_doc` funciona, debería ser directo. Ver [[project-monvicsar-tecnico-estados]].

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
