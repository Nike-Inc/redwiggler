FORMAT: 1A

# My Api Api

## Overview
**MyAPI** is a sample.
            

### Search [GET /my/api/v1{?anchor,count,filter}]

+ Parameters
    + anchor (optional, string) - Anchor points to the last element of the previous page.
    + count: 100 (optional, string) - total number of index objects to return. Default is 25.
    + filter (optional, string) - list of filters.

+ Request

    + Headers

            Accept: application/json
            Authorization: Bearer <jwt>

+ Response 200 (application/json; charset=UTF-8)

    + Body        
    
            {
               "pages": {
                  "next": "/my/api/v1?anchor=ad123dq!!2casd&count=5"
               },
               "objects": [
                    {
                      "id": "myid",
                    }
               ]
            }

    + Schema
    
            {
               "$schema":"http://json-schema.org/draft-04/schema#",
               "type":"object",
               "properties":{
                  "pages":{
                     "type":"object",
                     "properties":{
                        "next":{
                           "type":"string"
                        }
                     },
                     "required":[
                        "next"
                     ]
                  },
                  "objects":{
                     "type":"array",
                     "items":{
                        "type":"object",
                        "properties":{
                           "id":{
                              "type":"string"
                           },
                           "foo":{
                              "type":"string"
                           }
                        },
                        "required":[
                           "id"
                        ]
                     }
                  }
               },
               "required":[
                  "pages",
                  "objects"
               ]
            }
+ Response 304
