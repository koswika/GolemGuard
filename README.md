A lightweight Bukkit/Spigot plugin that restricts iron golem farm spawning to admin approved zones only.
Keeps your server economy balanced by preventing players from building unlimited iron farms anywhere they want.

By default, all player made iron golem spawns are blocked across the entire server. Ops can designate approved farm zones using a simple command. 
Any golem that tries to spawn outside those zones is silently cancelled. Village and raid golems are never affected.

# Usage

/golemzoneSet an approved zone at your current location
/golemzone removeRemove the nearest zone to you
/golemzone listList all active zones and their coordinates

# Permissions
Permission: golemguard.zone op by default
