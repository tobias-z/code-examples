# Camunda

This example connects to the camunda cloud, through api secrets. It will deploy all `bpmn` files found in the `resources/camunda`
directory.

It contains a worker used by the `laf.bpmn` process, and a scheduled cron job which will continuously start the
HelpLafayetteEscape process.

## Forms

Forms are deployed with the bpmn through providing the json of each of them to the user tasks
