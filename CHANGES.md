## 1.3.6

Issue #53: Be explicit about a move to Java 7.

Issue #46: Now behaving according to spec.

## 1.3.0

Issue #17 is fixed: Changed default properties file for resource servers that use the AuthorizationServerFilter for token verification: it now is 'apis-resource-server.properties'.
To be consistent, the servlet filter's init-param has been renamed as well, from 'apis.application.properties.file' to 'apis-resource-server.properties.file'.
Migration consists of either:

- Rename your current apis.application.properties (only for the resource server!) to 'apis-resource-server.properties'.

or

- Change the init-param in your web.xml from 'apis.application.properties.file' to 'apis-resource-server.properties.file'

Which one to use depends on whether you currently use the init-param or rely on the default file name.


## 1.2.6

Issue #15 Type information fixed
