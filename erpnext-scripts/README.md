# Scripts de configuración de ERPNext (Helpdesk)

Scripts Python ejecutados contra el sitio `frontend` de ERPNext (`erp.monvicsar.com`) durante la sesión del 2026-08-16. Se guardan aquí para no perder el trabajo y poder re-ejecutarlos o consultarlos desde el servidor físico de la oficina.

## Cómo se ejecutan

Estos scripts corren **dentro del contenedor `erpnext-backend-1`**, colocados temporalmente en `apps/helpdesk/helpdesk/<nombre>.py` (para que `bench execute` los pueda resolver como `helpdesk.<nombre>.run`), y se invocan así:

```powershell
# 1. Copiar el script dentro del contenedor (desde WSL, con el archivo ya accesible ahí)
docker cp <archivo>.py erpnext-backend-1:/home/frappe/frappe-bench/apps/helpdesk/helpdesk/<archivo>.py

# 2. Ejecutarlo
docker exec erpnext-backend-1 bench --site frontend execute helpdesk.<archivo_sin_extension>.run
```

Todos son **idempotentes**: si ya existe lo que intentan crear, lo detectan y lo omiten sin duplicar ni fallar. Se pueden volver a correr sin miedo.

## Orden de aplicación (ya aplicados en producción, 2026-08-16)

1. **Fix de infraestructura raíz** (no es un script Python, fue un fix manual de archivo — ver `PENDIENTES.md` en la raíz del repo, sección resuelta): `sites/apps.txt` dentro del contenedor le faltaban `helpdesk` y `telephony`. Se corrigió a mano + restart del stack + `bench clear-cache`. Esto arregló tanto el 500 de `/helpdesk` como el bug viejo de `rename_doc`.
2. `01_create_tipo_llamada.py` — crea el DocType `Tipo de Llamada` (catálogo de urgencia/SLA) con sus 5 entradas y los 3 campos custom en `HD Ticket` (`tipo_llamada`, `tipo_llamada_comentario`, `chgc_evidencia`).
3. `02_set_ticket_naming.py` — cambia el formato de numeración de `HD Ticket` a `SR` + mes + día + consecutivo diario (ej. `SR08160003`).
4. `03_add_template_fields.py` — agrega `tipo_llamada` y `equipo_atm` como campos seleccionables en la pantalla de "Nuevo Ticket" del portal (plantilla `HD Ticket Template` → "Default").

## Pendiente / próximos pasos (ver también `PENDIENTES.md`)

- **Panel lateral del detalle de ticket** (`/helpdesk/tickets/<id>`, columna derecha con Ticket Type/Priority/Customer/Team/Asignado): el usuario pidió que ahí también se pueda elegir Tipo de Llamada y Equipo ATM. Nota: "Asignado" (para asignar al técnico) y "Comment" (para comentarios) **ya existen** en ese panel, no hace falta agregarlos — solo faltan Tipo de Llamada y Equipo ATM. Se investigó el doctype `HD Field Layout` como mecanismo para configurar este panel, pero la consulta `frappe.get_all("HD Field Layout")` devolvió **vacío** — significa que el layout de esa columna lateral no está controlado por registros en ese doctype (al menos no hay ninguno creado todavía), hay que seguir investigando el código fuente del componente Vue de detalle de ticket (`apps/helpdesk/desk/src/pages/ticket/...`, buscar el archivo que renderiza esa columna derecha, análogo a como se encontró `TicketNew.vue` para la pantalla de creación) para saber si es configurable sin tocar código o si requiere edición de la app + rebuild del frontend.
- Renombrar las 5 entradas de `HD Ticket Status` al modelo de estados real (Assign/Traveling/Working/Suspend/Closed) — ver memoria del proyecto `project_monvicsar_tecnico_estados`.
- Server Script de validación: exigir `tipo_llamada_comentario`/`chgc_evidencia` cuando el `Tipo de Llamada` elegido tenga esas banderas activas, y pausar el cálculo de SLA cuando `pausa_sla=1`.
- Cambiar el texto del botón "Submit" de la pantalla de Nuevo Ticket a "Crear Ticket" (requiere una entrada en el doctype `Translation` de Frappe, con el riesgo de que "Submit" se usa en más lugares — evaluar impacto antes de aplicar).
- Código de técnico interno de Monvicsar (`MV001`, `MV002`...) — aún no implementado, ver memoria `project_monvicsar_tecnico_login`.

## Bugs corregidos hoy que vale la pena recordar

- El campo custom `chgc_evidencia` en `HD Ticket` tenía `depends_on` apuntando al nombre viejo `"CHGC"` de la entrada del catálogo; al renombrarla a `"CHGC - Facturable (Daño no cubierto)"` se rompió la condición (nunca se iba a mostrar). Se corrigió actualizando el `depends_on` del Custom Field al nombre nuevo — **si se vuelve a renombrar esa entrada del catálogo, hay que actualizar también este `depends_on` a mano.**
