# -*- coding: utf-8 -*-
# Agrega "tipo_llamada" y "equipo_atm" como campos seleccionables en la
# pantalla de "Nuevo Ticket" del portal (plantilla HD Ticket Template ->
# "Default"). Marcados hide_from_customer=1 porque son campos de triage
# interno, no algo que un cliente externo deba llenar.
#
# Ejecutar dentro del contenedor erpnext-backend-1:
#   docker cp 03_add_template_fields.py erpnext-backend-1:/home/frappe/frappe-bench/apps/helpdesk/helpdesk/add_template_fields.py
#   docker exec erpnext-backend-1 bench --site frontend execute helpdesk.add_template_fields.run

import frappe


def run():
    doc = frappe.get_doc("HD Ticket Template", "Default")
    existing_fieldnames = {row.fieldname for row in doc.fields}

    to_add = [
        {"fieldname": "tipo_llamada", "required": 0, "hide_from_customer": 1},
        {"fieldname": "equipo_atm", "required": 0, "hide_from_customer": 1},
    ]

    for row in to_add:
        if row["fieldname"] in existing_fieldnames:
            print(f"'{row['fieldname']}' ya esta en la plantilla, se omite.")
            continue
        doc.append("fields", row)
        print(f"Agregado '{row['fieldname']}' a la plantilla Default.")

    doc.save(ignore_permissions=True)
    frappe.db.commit()
    print("LISTO.")
