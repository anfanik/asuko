# asuko [![build](https://github.com/anfanik/asuko/actions/workflows/build.yml/badge.svg)](https://github.com/anfanik/asuko/actions/workflows/build.yml)
Asuko - Java autodeploy software.

## Usage
Create a configuration file `asuko.conf`
``` hocon
main: "org.bukkit.craftbukkit.Main"
files: [
	{
		id: "antblock"
		url: "https://repository.anfanik.me/deploy/me/anfanik/antblock/stage/antblock-stage.jar"
		credentials: { # Optional section for Basic Authorization
			username: "username"
			password: "password"
		}
		destination: "./plugins/antblock.jar"
	}
]
```