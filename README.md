# pihole Rabbit

This project is a companion for your pihole.
The main objective is to easily enable or disable your groups.

pihole has the notion of groups.
A group can represent the users.
A group can also represent the allowed domains that a user can access.
The idea is to associate a group of domains to a group of users.

We could imagine allowing or denying social networks to a group of users.

## How it could work

The rabbit has two main components:
- the webapp, to configure and manage the groups
- the agent, to update the pihole configuration

Since it could be dangerous to expose your pihole to the external world, the webapp will talk to a backend service.
The backend service could use supabase which is a backend as a service and provides a realtime mechanism.
See https://supabase.com/docs/guides/realtime

The agent should be installed on the pihole machine or in the same network.
When the agent starts, it will send the current groups and clients to supabase.

The webapp should allow to define if a group belongs to an user or a domain group.
Like that, it could be possible to allow or block a domain group to an user group.

The first version could only manage the groups.
Later, we could imagine to associate users to a group.

## Example

User group
- foo
- bar
- qix

Domain group
- youtube
- instagram

It should be possible to say:
- foo can access youtube but can't access instagram
- bar can't access both youtube and instagram

## Scheduling

It might be interesting to add scheduling if users would like to enable or disable groups for a given period.
This period could use a crontab notation.

The webapp could help to build the crontab.
The agent will be responsible to write the crontab.

One crontab item will execute a specific command to the agent.
This command could take the ID of a command as argument.

TODO: try to modify a crontab and see if it is executed correctly

## pihole API

### Get groups

POST admin/scripts/pi-hole/php/groups.php
form-data: {"action":"get_groups", token: "token"}

```
{
    "data": [
        {
            "id": number,
            "enabled": number,
            "name": string,
        },
        ...
    ]
}
```

### Get domains

POST admin/scripts/pi-hole/php/groups.php
form-data: {"action":"get_domains", token: "token"}

Domain type:
- 0: exact whitelist
- 1: exact blacklist
- 2: regex whitelist
- 3: regex blacklist

```
{
    "data": [
        {
            "id": number,
            "type": number,
            "domain": string,
            "enabled": number,
            "comment": string,
            "groups": number[]
        },
        ...
    ]
}
```

### Get clients

POST admin/scripts/pi-hole/php/groups.php
form-data: {"action":"get_clients", token: "token"}

```
{
    "data": [
        {
            "id": number,
            "ip": string",
            "comment": string,
            "name": string | null,
            "groups": number[]
        },
       ...
    ]
}
```

## Database

The project could use a no-sql approach and have tables like the pihole representation.

The webapp will be responsible to update the database.
The agent will listen to the table changes using the realtime capability and update the pihole configuration.

pihole_groups
-------------

json: GroupContent
{
    id: ID
    enabled: number
    name: string
}

id: int
pihole_id: number
content: GroupContent

pihole_clients
--------------
json: ClientContent
{
    id: ID
    ip: string
    comment: string
    name: string | null
    groups: ID[]
}

id: number
pihole_id: number
content: ClientContent

pihole_domains
--------------

json: DomainContent
{
    id: ID
    type: number
    domain: string
    enabled: number
    comment: string
    groups: ID[]
}

groups_crontabs
---------------
id: number
group_id: number
cron_tab: string

cron_tabs
---------
id: ID
name: string
expression: string
