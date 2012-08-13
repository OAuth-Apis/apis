<pre>


                                                     MMMMM    M. MM
                                                     MMM MM  MMM ,M.
                                                    .NMM 8M .MMM  O
                                                     .MN MMD.NMMMM
                                                      MM.NMMM. MMN.   :
                                                 M.  .M .MMMMD. MM   N
                                                  M   M MM:MMM  MM.  M
                                                   N  ,7MM MMMM ?M  M
                                                   M..,MMM   IM  M. M
                                                   OM MM.     .M.M,N
                                                   .MMMM.       :NM.
                                                    .MMM.      M MM
                                                     MMMM     M~MMM
                                                    .MMMMMMMMMMMM.M
                                                     ..  MMMMMM
                                                   .NNMMMMM   MM
                                               ?MMMMMMMMMMM    MM
                                            MMM8MMZDN,Z  8    MMMM
                                         NMMM.MDMMMMMM.  . M ,MMM.
            MMMMMMMMMMM..         ...MMMNM, MMMD,~M8MN   .MM,  . M
         =MMN,...:M MMMMMMMMMMMMM8MMMMMDD?M  MM?                  M
        .MD   M.MMMM MM MMMOMM,:MMZMMM   M,:  M:8  M. M    .M     .
        .DMMM ,.N . MM MMM MM . ..  .. ... . MM+.  MMMM. MMMMM.    N.
        +. MM . MM 7M.NM   MMM  D  ~  OM..   ,MM.~ M ,   .,M,. M  MM.
        N: .        IM.    MMM  ..M.. :M, ...    M   N.MD.     .,..
        8D:M., =M.    ,.          .       ,.M    M. .MMNN
        MMM,   8M .  NMMM.  .,Z   D. M   .~ MM    N    8N
        MNM  .  ,     ..N   .~    . , .  OM  .    M,N,.N
        MM  =M. N,  .M8      .    M$ ..NMM ,..MM  O M.M
        MM OMM ,.  MN,               .. MM ..:..  MMD
       .MM M   M  .M  MMMMMMM?  .   .   MMN N ,..M . M
       ... M MMM  ..NM  .               M .?   8,  NM
       MM  M MMN:  .MM. .               M. .N.. .. M.
     ,D M M    D,   M = .               ,: M     ? M M
     M .  ,M  M        ..              MN $.      MM ~.
     O~.N..          . ,:              MM.N.      .M
     MMMM.  Z           :.              .M         NM M
     MM .. M         D .DM.            . .         ,MM
     MM   7M           M. M           M..           .  M
    :M8  . M            7  M          M M            M N,
     M  MM M             M  N.       .. M             +DM8
    N    7.MM            NM$,D,       M,MM            ,MM:M
          $:=M,            MMNM7       MMMN            ,+MMN
   .MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMZ

</pre>
API Secure
======
<em>(Work in Progress, sponsored by SURFnet)</em>

API Secure (Apis) is a generic OAuth2 provider that can be used to kickstart your API authentication.

Features
--------

- An OAuth2 Provider compliant with [the draft v2-30 specification](http://tools.ietf.org/html/draft-ietf-oauth-v2-30)
  * Pluggable authentication and configurable persistence
  * Support for authorization code and implicit grant
  * GUI for the registration of Resource Servers and Client apps

- An OAuth2 demo Resource Server
  * In-memory JSON-based backend with limited functionality only to demo the OAuth Authorization Server

- An implementation add-on for connecting to a compliant SAML IdP for authentication

## Build / run Authorization Server
    mvn clean install
    cd oaaas-authorization-server-war
    mvn jetty:run


## Resource Servers and Client apps registration
### GUI
The GUI for Resource Servers and Client apps registration can be found at:
[http://localhost:8080/adminClient/](http://localhost:8080/adminClient/)
### REST api
The following URLs are available for the registration interface:

    GET     /admin/resourceServer
    GET     /admin/resourceServer/{resourceServerId}
    PUT     /admin/resourceServer
    POST    /admin/resourceServer/{resourceServerId}
    DELETE  /admin/resourceServer/{resourceServerId}

    GET     /admin/resourceServer/123/client
    GET     /admin/resourceServer/123/client/{clientId}
    PUT     /admin/resourceServer/123/client
    POST    /admin/resourceServer/123/client/{clientId}
    DELETE  /admin/resourceServer/123//client/{clientId}
