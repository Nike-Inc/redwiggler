# Red wiggler
The composting worm.  Composts your contract specification and tests and confirms that the contract specification is being followed.

[![Build Status](https://travis-ci.com/Nike-Inc/redwiggler.svg?token=PmECSWCH8LFEKNdzr64F&branch=master)](https://travis-ci.com/Nike-Inc/redwiggler)
[![Coverage Status](https://coveralls.io/repos/github/Nike-Inc/redwiggler/badge.svg?branch=master)](https://coveralls.io/github/Nike-Inc/redwiggler?branch=master)

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
