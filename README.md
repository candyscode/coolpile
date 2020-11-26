# coolpile
Coolpile is a remote compiler that is accessible via a REST API.

To compile a C file, use the `/compile` endpoint and POST your sourcecode embedded in the following JSON format:

```
{
  "sourceCode": "BASE64 ENCODED CODE HERE"
}
```
