<h1 align="center">asuko </h1>  <br>

<p align="center">
  Java application autoupdate software
</p>


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

## Integration

* Set allow restart check. It should block application when it's is not required
``` java
Asuko.setIsCanRestart(() -> Bukkit.getOnlinePlayers().isEmpty());
```

* Try to restart when it makes allowed. It will restart your application if restart is required
``` java
Asuko.performSafeRestartIfRequired();
```

* Done!