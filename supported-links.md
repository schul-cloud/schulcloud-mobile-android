Schemes: `http`, `https`  
Host: `schul-cloud.org`  


| Name          | Path           | Comments                                      |
| ------------- | -------------- | --------------------------------------------- |
| Dashboard     | /dashboard     |                                               |
| News list     | /news          |                                               |
| News          | /news/{id}     |                                               |
| Course list   | /courses       |                                               |
| Course        |                | Not supported<sup>1</sup>                     |
| Topic         |                | Disabled as some content is not supported yet |
| Homework list | /homework      |                                               |
| Homework      | /homework/{id} |                                               |
| File overview | /files         |                                               |
| Folder        |                | Not supported (shared not implemented)        |

*<sup>1</sup> Note: Lazy matching is not supported! Hence `/courses/{courseId}/topics/{id}` is matched by `/courses/{id}`.*
