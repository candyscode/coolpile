# coolpile
Coolpile is a remote compiler that is accessible via a REST API.

## How to use coolpile

### 1. Check available Services

You can do this by sending a GET request to `/services`.
The result will look similar to this and specifies what coolpile can currently do for you and what the names of the services are:

```
[
    {
        "name": "c-riscv",
        "description": "Compiles C code to RISC-V assembly."
    },
    {
        "name": "c-x86",
        "description": "Compiles C code to x86 assembly."
    }
]
```
In this case, two services `c-riscv` and `c-x86` are available.

### 2. Choose the right service 

A service name always consists of the input language and the target ISA separated by a dash. `c-riscv` for example, compiles C code to RISC-V assembly.

### 3. Query service to compile your code

To use the `c-riscv` service, send a POST request to `/services/riscv`. If for example a Java Compilation Service (called `javac`) would exist as well, it could be queried by accessing `/services/javac` via POST.

To submit your sourcecode, encode it in Base64 (online tools are widely available) and wrap it in the following JSON format:

```
{
  "sourceCode": "BASE64 ENCODED CODE HERE"
}
```

### 4. Receive your results

The result will look somewhat like this:

```
{
  "compilationTime": "241ms",
  "assembly" : "BASE64 ENCODED ASSEMBLY HERE"
}
```

The result needs to be manually Base64-decoded afterwards.
