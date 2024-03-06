#!/bin/bash
while true; do
  curl -s -o /dev/null -w "%{http_code}\n" -X POST http://localhost:9000/api/login \
    -H "X-API-Key: QVBJLUtFWS1GT1ItTVNDLVVTRVI=" \
    -H "Content-Type: application/json" \
    -d '{"email": "test@mail.com", "password": "wsf@2435%"}'
done
