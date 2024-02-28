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

## Database

The project could use a no-sql approach.

TODO: check if pihole is using id of type number or string

GroupType can be user or domain

pihole_groups
-------------
id: ID
type: GroupeType?

groups
------
id: ID
name: string
domains: ID[]
