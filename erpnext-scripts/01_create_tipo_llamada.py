# -*- coding: utf-8 -*-
# Crea el DocType "Tipo de Llamada" (catalogo de urgencia/SLA) con sus 5
# entradas, y los 3 campos custom en HD Ticket que lo usan.
#
# Ejecutar dentro del contenedor erpnext-backend-1:
#   docker cp 01_create_tipo_llamada.py erpnext-backend-1:/home/frappe/frappe-bench/apps/helpdesk/helpdesk/create_tipo_llamada.py
#   docker exec erpnext-backend-1 bench --site frontend execute helpdesk.create_tipo_llamada.run

import frappe


def run():
    if frappe.db.exists("DocType", "Tipo de Llamada"):
        print("DocType 'Tipo de Llamada' ya existe, se omite creacion.")
    else:
        doc = frappe.get_doc(
            {
                "doctype": "DocType",
                "name": "Tipo de Llamada",
                "module": "Helpdesk",
                "custom": 1,
                "naming_rule": "Set by user",
                "autoname": "Prompt",
                "fields": [
                    {
                        "fieldname": "integer_value",
                        "fieldtype": "Int",
                        "label": "Orden",
                        "reqd": 1,
                        "description": "Orden de severidad: 1 = mas urgente.",
                    },
                    {
                        "fieldname": "color",
                        "fieldtype": "Select",
                        "label": "Color",
                        "options": "Red\nOrange\nYellow\nBlue\nGreen\nGray",
                    },
                    {"fieldname": "column_break_1", "fieldtype": "Column Break"},
                    {
                        "fieldname": "requiere_comentario",
                        "fieldtype": "Check",
                        "label": "Requiere comentario de justificacion",
                        "default": "0",
                    },
                    {
                        "fieldname": "requiere_evidencia",
                        "fieldtype": "Check",
                        "label": "Requiere foto de evidencia",
                        "default": "0",
                    },
                    {
                        "fieldname": "pausa_sla",
                        "fieldtype": "Check",
                        "label": "Pausa el reloj del SLA",
                        "default": "0",
                    },
                    {
                        "fieldname": "es_facturable",
                        "fieldtype": "Check",
                        "label": "Facturable (aparece en reporte de cierre de mes)",
                        "default": "0",
                    },
                    {"fieldname": "section_break_1", "fieldtype": "Section Break"},
                    {
                        "fieldname": "description",
                        "fieldtype": "Small Text",
                        "label": "Descripcion",
                    },
                    {
                        "fieldname": "disabled",
                        "fieldtype": "Check",
                        "label": "Disabled",
                        "default": "0",
                    },
                ],
                "permissions": [
                    {
                        "role": "System Manager",
                        "read": 1,
                        "write": 1,
                        "create": 1,
                        "delete": 1,
                    },
                    {"role": "Agent", "read": 1},
                ],
            }
        )
        doc.insert()
        print("DocType 'Tipo de Llamada' creado.")

    # Nombres finales (con enye y mayuscula correctas)
    catalog = [
        {
            "name": "Tipo 1 - Urgente/Critico",
            "integer_value": 1,
            "color": "Red",
            "description": "Urgente / Critico",
        },
        {
            "name": "Tipo 2 - Urgente",
            "integer_value": 2,
            "color": "Orange",
            "description": "Urgente",
        },
        {
            "name": "Tipo 3 - Operativo/No urgente",
            "integer_value": 3,
            "color": "Yellow",
            "description": "Operativo / No urgente",
        },
        {
            "name": "Tipo 4 - Pausa SLA (requiere justificacion)",
            "integer_value": 4,
            "color": "Blue",
            "description": "Detiene el reloj del SLA. Requiere comentario de justificacion.",
            "requiere_comentario": 1,
            "pausa_sla": 1,
        },
        {
            "name": "CHGC - Facturable (Daño no cubierto)",
            "integer_value": 5,
            "color": "Gray",
            "description": (
                "Charge: daño no cubierto por el contrato de servicio (ej. vandalismo). "
                "Facturable al cliente en el cierre de mes."
            ),
            "requiere_comentario": 1,
            "requiere_evidencia": 1,
            "es_facturable": 1,
        },
    ]

    for row in catalog:
        name = row["name"]
        if frappe.db.exists("Tipo de Llamada", name):
            print(f"Entrada '{name}' ya existe, se omite.")
            continue
        entry = frappe.get_doc({"doctype": "Tipo de Llamada", **row})
        entry.insert()
        print(f"Entrada '{name}' creada.")

    custom_fields = [
        {
            "dt": "HD Ticket",
            "fieldname": "tipo_llamada",
            "fieldtype": "Link",
            "options": "Tipo de Llamada",
            "label": "Tipo de Llamada",
            "insert_after": "priority",
        },
        {
            "dt": "HD Ticket",
            "fieldname": "tipo_llamada_comentario",
            "fieldtype": "Small Text",
            "label": "Comentario de justificacion (Tipo de Llamada)",
            "insert_after": "tipo_llamada",
            "depends_on": "eval:doc.tipo_llamada",
        },
        {
            "dt": "HD Ticket",
            "fieldname": "chgc_evidencia",
            "fieldtype": "Attach Image",
            "label": "Evidencia CHGC (foto del dano)",
            "insert_after": "tipo_llamada_comentario",
            "depends_on": 'eval:doc.tipo_llamada=="CHGC - Facturable (Daño no cubierto)"',
        },
    ]

    for cf in custom_fields:
        existing = frappe.db.exists(
            "Custom Field", {"dt": cf["dt"], "fieldname": cf["fieldname"]}
        )
        if existing:
            print(f"Custom Field '{cf['fieldname']}' ya existe, se omite.")
            continue
        doc = frappe.get_doc({"doctype": "Custom Field", **cf})
        doc.insert()
        print(f"Custom Field '{cf['fieldname']}' creado en HD Ticket.")

    frappe.clear_cache(doctype="HD Ticket")
    frappe.db.commit()
    print("LISTO.")
