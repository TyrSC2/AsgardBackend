*** Asgard backend ***

To build the backend, run the maven package command from the root directory.

```
mvn package
```

Then upload it to the server:

```
scp -r target\Asgard-*.jar simon@82.165.237.237:/home/simon/asgard/
```

Upload latest version of Tyr to the server:
```
scp -r C:\Simon\Code\SC2AI\C#\Tyr\Tyr2\TyrDotnetCore\bin\Debug\net6.0 simon@82.165.237.237:~/asgard/bots/Tyr/V1
```

If there are only changes to the main Tyr project than you can upload just those using: :
```
scp -r C:\Simon\Code\SC2AI\C#\Tyr\Tyr2\TyrDotnetCore\bin\Debug\net6.0\TyrBase.* simon@82.165.237.237:~/asgard/bots/Tyr/V1
```