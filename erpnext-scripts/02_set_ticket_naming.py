# -*- coding: utf-8 -*-
# Cambia el formato de numeracion de HD Ticket a SR + mes + dia + consecutivo
# diario (ej. SR08160003). Usa un Property Setter (no toca el JSON del
# doctype, que es de la app Helpdesk, no nuestro) para que sobreviva
# actualizaciones de la app.
#
# Ejecutar dentro del contenedor erpnext-backend-1:
#   docker cp 02_set_ticket_naming.py erpnext-backend-1:/home/frappe/frappe-bench/apps/helpdesk/helpdesk/set_ticket_naming.py
#   docker exec erpnext-backend-1 bench --site frontend execute helpdesk.set_ticket_naming.run

import frappe


def run():
    frappe.make_property_setter(
        {
            "doctype": "HD Ticket",
            "doctype_or_field": "DocType",
            "property": "autoname",
            "value": "SR.MM.DD.####",
            "property_type": "Data",
        }
    )
    frappe.clear_cache(doctype="HD Ticket")
    frappe.db.commit()

    val = frappe.get_meta("HD Ticket").autoname
    print("autoname efectivo:", val)
    print("LISTO.")
