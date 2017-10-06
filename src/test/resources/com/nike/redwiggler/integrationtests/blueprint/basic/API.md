FORMAT: 1A

# My Api Api

## Overview
**MyAPI** is a sample.
            

### GetItem [GET /my/resource/v2/:id]

+ Response 200 (application/json; charset=UTF-8)

    + Schema
    
            {
               "$schema":"http://json-schema.org/draft-04/schema#",
               "type":"object",
               "properties":{
                  "name":{
                     "type":"string",
                  },
                  "id":{
                     "type":"string",
                    "pattern": "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$"
                  },
               }
            }
            
### CreateItem [POST /my/resource/v2]

+ Request

    + Schema
    
            {
               "$schema":"http://json-schema.org/draft-04/schema#",
               "type":"object",
               "properties":{
                  "name":{
                     "type":"string",
                  },
                  "id":{
                     "type":"string",
                  },
               }
            }
            
+ Response 201 (application/json; charset=UTF-8)
