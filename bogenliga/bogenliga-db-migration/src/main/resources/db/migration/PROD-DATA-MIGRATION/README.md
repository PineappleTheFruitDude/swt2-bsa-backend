# Produktiv-Datenmigration

Migration des produktiven Datenbank-Schemas aus Microsoft Access in das Datenbank-Schema der Bogenliga Application

``mvn clean flyway:clean flyway:migrate -PPROD-DATA-MIGRATION``


Hinweis:

_Die Versionen der Migrationsskripte liegen zwischen 1000 und 1999. Die Schema-Version von Flyway steht danach auf 1999. Version 1000 erstellt ein temporäres Schema und 1999 löscht das Schema.
Schema-Änderungen nach der Migration müssen daher bei Version **1200** starten._
