# portteleport

Binds a local port to a port of a remote server accessed through SSH.

The same operation can be achieved with: `$ ssh -L [bind_address:]port:host:hostport -i identity_file [user@]hostname`

Tunnel is created from CLI options

Use case: Accessing a database port on a remote host as a local port when only SSH access is given to the remote host.

### Building the project
`$ mvn package`

### CLI Parameters
```
$ java -jar ./target/portteleport-0.0.1-SNAPSHOT.jar -?

usage: portteleport
 -?,--help                    Print this menu
 -h,--sshhost <hostname>      Hostname or IP of the SSH server
 -i,--rsaid <filepath>        Path to RSA ID file
 -l,--localport <port>        The port to open on the local machine
 -m,--remoteport <port>       Port name of the remote host
 -p,--sshport <port>          SSH port number
 -r,--remotehost <hostname>   Remote host name or IP
 -u,--user <username>         Username to log in as in remote server
 -v,--version                 Display the version number
```
