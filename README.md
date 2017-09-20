# Red wiggler
The composting worm.  Composts your contract specification and tests and confirms that the contract specification is being followed.

## Current features:
+ Supports swagger 2.0
+ Compares every call to every schema and sees if there's a match to path, code and request schema
+ 5 types of outcome:
  * schema validation fails
  * call not matched by schema
  * schema is not covered
  * schema validation passes
  * call is matched by multiple schemas
+ Outputs a report in html format
